package com.miris;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.miris.ui.activity.BaseActivity;
import com.miris.net.SessionPreferences;
import com.miris.ui.activity.SignInActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fantastic on 2015-10-20.
 */
public class MirisPushReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, SignInActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void onPushReceive(Context context, Intent intent) {
        SessionPreferences session = new SessionPreferences(context);
        JSONObject pushData;
        String alert = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(MirisPushReceiver.KEY_PUSH_DATA));
            alert = pushData.getString("alert");
        } catch (JSONException e) {}

        if (!alert.equals("false") && session.getPushAlert() == true) {
            Intent cIntent = new Intent(MirisPushReceiver.ACTION_PUSH_OPEN);
            cIntent.putExtras(intent.getExtras());
            cIntent.setPackage(context.getPackageName());

            PendingIntent pContentIntent =
                    PendingIntent.getBroadcast(context, 0 , cIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder
                    .setSmallIcon(R.drawable.noti_icon)
                    .setContentTitle("미르이즈")
                    .setContentText(alert)
                    .setContentIntent(pContentIntent)
                    .setAutoCancel(true);


            NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            myNotificationManager.notify(1, builder.build());
        }
        BaseActivity.badge_count = BaseActivity.badge_count +1;
        Intent badgeintent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeintent.putExtra("badge_count_package_name", "com.miris");
        badgeintent.putExtra("badge_count_class_name", "com.miris.ui.activity.SignInActivity");
        badgeintent.putExtra("badge_count", BaseActivity.badge_count);
        context.sendBroadcast(badgeintent);
    }

}
