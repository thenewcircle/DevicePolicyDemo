package com.example.android.devicepolicydemo;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * This is the component that is responsible for actual device administration.
 * It becomes the receiver when a policy is applied. It is important that we
 * subclass DeviceAdminReceiver class here and to implement its only required
 * method onEnabled().
 */
public class DemoDeviceAdminReceiver extends DeviceAdminReceiver {
	static final String TAG = "DemoDeviceAdminReceiver";

	//TODO: Override onEnabled(), onDisabled()

    //TODO: Override onPasswordChanged(), onPasswordFailed(), onPasswordSucceeded()

}
