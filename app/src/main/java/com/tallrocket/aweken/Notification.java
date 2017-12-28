package com.tallrocket.aweken;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class Notification extends AppCompatActivity {
    Ringtone r;
    private MediaPlayer player=null;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String strUserName = SP.getString("username", "NA");
        Uri uri= Uri.parse(SP.getString("notifications_new_message_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));

//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if(uri==null){
            uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        MediaPlayer player = MediaPlayer.create(this, uri);
        player.setLooping(true);
        player.start();

        Boolean vibration=SP.getBoolean("notifications_new_message_vibrate",true);
        if(vibration){
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] THREE_CYCLES = new long[] { 100, 1000, 1000,  1000, 1000, 1000 };
            v.vibrate(THREE_CYCLES,0);
        }

        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        player = MediaPlayer.create(this, notification);
        player.setLooping(true);
        player.start();*/
        /*v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] THREE_CYCLES = new long[] { 100, 1000, 1000,  1000, 1000, 1000 };
        v.vibrate(THREE_CYCLES,0);*/



        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (r.isPlaying()==false){

            r.play();
        }*/


    }

    public void back(View view) {
//        MyJobService.cancelJob(Notification.this);

        v.cancel();

        player.stop();
        Intent intent=new Intent(getApplicationContext(),HomeA.class);
        intent.putExtra("IsFrom",true);
        startActivity(intent);

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stop(View view) {
        v.cancel();
        player.stop();
//        MyJobService.cancelJob(Notification.this);

//        MyService.cancelJob(Notification.this);
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        this.finish();

    }
}

