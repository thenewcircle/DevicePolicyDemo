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

        instructions = findViewById(R.id.instructions);
        toggleButton = findViewById(R.id.toggle_device_admin);

        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
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
                //TODO: Activate device administration
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
        //TODO: Lock the device immediately
    }

    /**
     * Trigger device reset using the policy manager
     */
    private void wipeAndResetDevice() {
        // We reset the device - this will erase entire /data partition!
        Log.d(TAG, "Resetting and wiping device now");
        //TODO: Reset the device, after asking the user first…
    }

    /**
     * Enforce minimum password using the policy manager
     */
    private void setDevicePassword() {
        //We ask the user to set their device password
        Log.d(TAG, "Enforcing device lock password");
        //TODO: Enforce password quality and set new password
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
