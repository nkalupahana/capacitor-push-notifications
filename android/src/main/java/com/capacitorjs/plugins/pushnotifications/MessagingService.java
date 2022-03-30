package com.capacitorjs.plugins.pushnotifications;

import android.app.NotificationManager;
import android.service.notification.StatusBarNotification;

import android.util.Pair;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // For some reason, you can only pull delivered notifications at Android M,
        //  so it's only worth running any of this if we're at that SDK level
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // If this is a data-only cleanUp notification:
            if (remoteMessage.getData().size() > 0) {
                if (remoteMessage.getData().containsKey("cleanUp")) {
                    // Find the latest notification in all delivered notifications
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    ArrayList<Pair<String, Integer>> ids = new ArrayList<>();
                    StatusBarNotification latestNotif = null;
                    for (StatusBarNotification notif : notificationManager.getActiveNotifications()) {
                        ids.add(Pair.create(notif.getTag(), notif.getId()));
                        if (latestNotif == null || latestNotif.getPostTime() < notif.getPostTime()) {
                            latestNotif = notif;
                        }
                    }

                    // Remove the latest from the list of all delivered
                    if (latestNotif != null) ids.remove(Pair.create(latestNotif.getTag(), latestNotif.getId()));

                    // And remove all other notifications
                    for (Pair<String, Integer> pair : ids) {
                        notificationManager.cancel(pair.first, pair.second);
                    }
                }
            }
        }

        PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PushNotificationsPlugin.onNewToken(s);
    }
}
