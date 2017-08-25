package chhatrachhorm.androidapp.onenterpise.lapidchat;



import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by chhormchhatra on 8/25/17.
 * This is to create foreground notification
 * (retrieve info from background notification)
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notiTitle = remoteMessage.getNotification().getTitle();
        String notiBody = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();
        String fromUserID = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notiTitle)
                        .setContentText(notiBody);


        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("user_id", fromUserID);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        // setting notification click behaviour
        mBuilder.setContentIntent(resultPendingIntent);




//        provide unique notification id for multiple notification
        int mNotificationId = (int)System.currentTimeMillis();
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
/**
 * https://developer.android.com/training/notify-user/build-notification.html
 * https://firebase.google.com/docs/cloud-messaging/android/client
 * */