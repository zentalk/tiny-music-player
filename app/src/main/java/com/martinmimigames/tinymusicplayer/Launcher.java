package com.martinmimigames.tinymusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;


/**
 * activity for controlling the playback by invoking different logics based on incoming intents
 */
@SuppressLint("UnsafeIntentLaunch")
public class Launcher extends Activity {
    static final String TYPE = "type";
    static final byte NULL = 0;
    static final byte PLAY_PAUSE = 1;
    static final byte KILL = 2;
    static final byte PLAY = 3;
    static final byte PAUSE = 4;

    static final byte LOOP = 5;

    private static final int REQUEST_CODE = 3216487;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNeedPermissionGranted()) {
            final var settings = new Intent();
            final var packageName = getPackageName();
            settings.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
            startActivity(settings);
            finish();
            return;
        }

        final var coming = getIntent();
        final var action = coming.getAction();

        if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action)) { // Do the 'play music' things.
            startPlayer(coming);
        } else { // Request a audio to play.
            // intent type to filter application based on your requirement
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("audio/*"), REQUEST_CODE);
        }
    }


    /**
     * redirect call to actual logic
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startPlayer(intent);
    }

    /**
     * restarts service
     */
    private void startPlayer(Intent intent) {
        intent.setClass(this, Service.class);
        stopService(intent);
        startService(intent);
        finish();
    }

    /**
     * call service control on receiving file
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        /* if result unusable, discard */
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            /* redirect to service */
            intent.setAction(Intent.ACTION_VIEW);
            startPlayer(intent);
            return;
        }
        finish();
    }

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    private boolean isNeedPermissionGranted() {
        final int state = getPackageManager().checkPermission(Manifest.permission.POST_NOTIFICATIONS, getPackageName());
        return PackageManager.PERMISSION_GRANTED == state;
    }
}
