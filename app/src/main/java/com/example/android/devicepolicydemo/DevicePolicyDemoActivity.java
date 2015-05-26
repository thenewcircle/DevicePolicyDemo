package com.example.android.devicepolicydemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DevicePolicyDemoActivity extends Activity {
    static final String TAG = DevicePolicyDemoActivity.class.getSimpleName();
    static final int ACTIVATION_REQUEST = 47; // identifies our request id

    DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;

    TextView instructions;
    Button toggleButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructions = (TextView) findViewById(R.id.instructions);
        toggleButton = (Button) findViewById(R.id.toggle_device_admin);

        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Verify if we are the device owner already
        if (!devicePolicyManager.isAdminActive(demoDeviceAdmin)) {
            instructions.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.VISIBLE);
        } else {
            instructions.setVisibility(View.GONE);
            toggleButton.setVisibility(View.GONE);
        }
    }

    /**
     * Called when a button is clicked on.
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_device_admin:
                // Activate device administration
                Intent intent = new Intent(
                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        demoDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Your boss told you to do this");
                startActivityForResult(intent, ACTIVATION_REQUEST);
                break;
            case R.id.button_lock_device:
                lockDevice();
                break;
            case R.id.button_reset_device:
                wipeAndResetDevice();
                break;
            case R.id.button_device_password:
                setDevicePassword();
                break;
        }
    }

    /**
     * Trigger device lock using the policy manager
     */
    private void lockDevice() {
        // We lock the screen
        Log.d(TAG, "Locking device now");
        try {
            devicePolicyManager.lockNow();
            Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            //We are not the device owner application
            Toast.makeText(this, R.string.device_admin_required,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trigger device reset using the policy manager
     */
    private void wipeAndResetDevice() {
        // We reset the device - this will erase entire /data partition!
        Log.d(TAG,
                "Resetting and wiping device now");
        try {
            devicePolicyManager.wipeData(ACTIVATION_REQUEST);
            Toast.makeText(this, "Wiping device...", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            //We are not the device owner application
            Toast.makeText(this, R.string.device_admin_required,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Enforce minimum password using the policy manager
     */
    private void setDevicePassword() {
        //We ask the user to set their device password
        Log.d(TAG, "Enforcing device lock password");
        try {
            //Enforce minimum password restrictions
            devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
                    DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
            if (devicePolicyManager.isActivePasswordSufficient()) {
                Toast.makeText(this, R.string.device_password_ok,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //Launch the activity to set a new password
            Intent passwordIntent =
                    new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            startActivity(passwordIntent);
        } catch (SecurityException e) {
            //We are not the device owner application
            Toast.makeText(this, R.string.device_admin_required,
                    Toast.LENGTH_SHORT).show();
        }
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
                } else {
                    Log.i(TAG, "Administration enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
