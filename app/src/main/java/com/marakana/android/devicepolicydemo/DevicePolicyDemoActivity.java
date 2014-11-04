package com.marakana.android.devicepolicydemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DevicePolicyDemoActivity extends Activity implements
		OnCheckedChangeListener {
	static final String TAG = "DevicePolicyDemoActivity";
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
    private static final int MIN_PW_LEN = 6;
    private static final int MIN_PW_NUMERIC = 1;

    DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;
	ToggleButton toggleButton;
    Button mLock;
    Button mReset;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		toggleButton = (ToggleButton) super
				.findViewById(R.id.toggle_device_admin);
		toggleButton.setOnCheckedChangeListener(this);

		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);

		mLock = (Button)findViewById(R.id.button_lock_device);
		mReset = (Button)findViewById(R.id.button_reset_device);

		if (devicePolicyManager.isAdminActive(demoDeviceAdmin)) {
		    toggleButton.setChecked(true);
		    toggleButton.setEnabled(false);
            mLock.setEnabled(true);
            mReset.setEnabled(true);
        }
	}

    @Override
    protected void onResume() {
        super.onResume();
        if (devicePolicyManager.isAdminActive(demoDeviceAdmin)) {
            toggleButton.setChecked(true);
            toggleButton.setEnabled(false);
            verifyGoodPassword(false);
        }
    }

    /**
	 * Called when a button is clicked on. We have Lock Device and Reset Device
	 * buttons that could invoke this method.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_lock_device:
			// We lock the screen
			Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
			Log.d(TAG, "Locking device now");
			devicePolicyManager.lockNow();
			break;
		case R.id.button_reset_device:
			// We reset the device - this will erase entire /data partition!
			Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
			Log.d(TAG,
					"RESETing device now - all user data will be ERASED to factory settings");
			devicePolicyManager.wipeData(ACTIVATION_REQUEST);
			break;
		}
	}

	/**
	 * Called when the state of toggle button changes. In this case, we send an
	 * intent to activate the device policy administration.
	 */
	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		if (isChecked) {
			// Activate device administration
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					demoDeviceAdmin);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"Your boss told you to do this");
			startActivityForResult(intent, ACTIVATION_REQUEST);
		}
		Log.d(TAG, "onCheckedChanged to: " + isChecked);
	}

	/**
	 * Called when startActivityForResult() call is completed. The result of
	 * activation could be success of failure, mostly depending on user okaying
	 * this app's request to administer the device.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTIVATION_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Administration enabled!");
				toggleButton.setChecked(true);
				verifyGoodPassword(true);
			} else {
				Log.i(TAG, "Administration enable FAILED!");
				toggleButton.setChecked(false);
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    private void verifyGoodPassword(boolean forcePwChange) {

        devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, MIN_PW_LEN);
        devicePolicyManager.setPasswordMinimumNumeric(demoDeviceAdmin, MIN_PW_NUMERIC);
        devicePolicyManager.setPasswordQuality(demoDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
        if (devicePolicyManager.isActivePasswordSufficient()) {
            Log.d(TAG, "Password is good");
            mLock.setEnabled(true);
            mReset.setEnabled(true);
        } else {
            Log.d(TAG, "Password is not good, force user to set it");
            if (forcePwChange) {
                Intent pwChange = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                startActivity(pwChange);
            } else {
                Toast.makeText(this, R.string.invalid_pw, Toast.LENGTH_LONG).show();
                toggleButton.setChecked(false);
                mReset.setEnabled(false);
                mLock.setEnabled(false);
            }
        }
    }
}
