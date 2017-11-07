package com.example.neilcastellino.controls;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int ADMIN_INTENT = 1;
    private static DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private Button fab, restart;
    Switch switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, Admin.class);

        switchCompat = (Switch) findViewById(R.id.switch1);
        restart = (Button) findViewById(R.id.btnRestart);

        // Check if app is given Device Administrator permissions
        if (mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName)) {
            switchCompat.setChecked(true);
        }

        // Navigate to settings and enable Device Administrator for this app
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ((switchCompat).isChecked()) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Administrator description");
                    startActivityForResult(intent, ADMIN_INTENT);
                } else {
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                }

            }
        });

        // On click of lock button
        fab = (Button) findViewById(R.id.btnLock);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    // Vibrate for 0.5 second
                    vibrator.vibrate(500);
                    callLockScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "Turn ON Lockscreen monitor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // On click of restart button
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
                // Vibrate for 0.5 second
                vibrator.vibrate(500);
                callRestart();
            }
        });

        addNotification();
    }

    // Function to lock screen
    public static void callLockScreen() {
        mDevicePolicyManager.lockNow();
    }

    // Function to restart phone
    public static void callRestart() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Notification method
    private void addNotification() {

//      On clicking the main notification area
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//      On clicking the Lockscreen button in the notification area
        Intent lockScreenIntent = new Intent();
        lockScreenIntent.setAction(AppConstant.LOCK_SCREEN);
        PendingIntent lockScreenPendingIntent = PendingIntent.getBroadcast(this, 0, lockScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//      On clicking the Restart button in the notification area
        Intent restartIntent = new Intent();
        restartIntent.setAction(AppConstant.RESTART);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, restartIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//      On clicking the Screenshot button in the notification area
        Intent screenshotIntent = new Intent();
        screenshotIntent.setAction(AppConstant.SCREENSHOT);
        PendingIntent screenshotPendingIntent = PendingIntent.getBroadcast(this, 0, screenshotIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.abc)
                        .setContentTitle("Controls")
                        .setContentText("by Neil Castellino")
                        .addAction(R.drawable.abc, "  Lock Screen ", lockScreenPendingIntent)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .addAction(R.drawable.abc, "  Restart  ", restartPendingIntent)
                        .addAction(R.drawable.abc, " Screenshot", screenshotPendingIntent)
                        .setOngoing(true);

        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void shareScreen() {
        try {
            File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "ControlsSS");

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            String path = new File(android.os.Environment.getExternalStorageDirectory(), "ControlsSS") + "/" + now + ".jpg";

            ScreenShot.savePic(ScreenShot.takeScreenShot(this), path);

            Toast.makeText(getApplicationContext(), "Screenshot Saved", Toast.LENGTH_SHORT).show();

        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Device Administrator Process Cancelled", Toast.LENGTH_SHORT).show();
                switchCompat.setChecked(false);
            }
        }
    }
}
