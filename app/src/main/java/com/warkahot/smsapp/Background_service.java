package com.warkahot.smsapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by warkahot on 26-Feb-17.
 */
public class Background_service extends Service {

    Vibrator vibrator ;
    Ringtone ringtone;
    IntentFilter filter;

    //Code for binding to service START
    private final IBinder iBinder = new LocalService() ;


    public class LocalService extends Binder
    {
        Background_service get_service()
        {
            return  Background_service.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
    //Code for binding to service END

    public void onCreate() {
        super.onCreate();

        Log.d("Order", "Service Created");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        setup_for_local_broadcast_reciever_for_messages();

    }


    public void setup_for_local_broadcast_reciever_for_messages()
    {
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Brd Rcv", "Broadcast Reviever was fired...");

            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
            {
                final Bundle bundle = intent.getExtras();

                try {

                    if (bundle != null) {

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        for (int i = 0; i < pdusObj.length; i++) {

                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();

                            String noti_msg = senderNum+" :  "+message;
                            create_notification(noti_msg);

                        } // end for loop
                    } // bundle is null

                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);
                }
            }


        }
    };

    public void create_notification(String message)
    {
        Intent intent = new Intent(this,Activity_for_intent_problem.class);
        intent.putExtra("refresh", "yes");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sms_icon)
                .setColor(getResources().getColor(R.color.blue))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sms_app_icon))
                .setContentTitle("New Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                ;

        vibrator.vibrate(400);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
