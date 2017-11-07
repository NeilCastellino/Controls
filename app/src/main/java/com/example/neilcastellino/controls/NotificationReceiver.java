package com.example.neilcastellino.controls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

/**
 * Created by NEIL CASTELLINO on 03-11-17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    MainActivity m = new MainActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (AppConstant.RESTART.equals(action)) {
            MainActivity.callRestart();
        } else if (AppConstant.LOCK_SCREEN.equals(action)) {
            MainActivity.callLockScreen();
        } else if (AppConstant.SCREENSHOT.equals(action)) {
            Toast.makeText(context, "Taking Screenshot...", Toast.LENGTH_SHORT).show();
            m.shareScreen();
        }
    }
}
