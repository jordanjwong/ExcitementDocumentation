package me.jordanwong.prg02_excitement_documentation;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Jordan on 7/9/2015.
 */
public class CameraListenerService extends WearableListenerService {
    private static final String CAMERA = "Camera";
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("TAG", "Got a message");
        if (messageEvent.getPath().equals(CAMERA)) {
            TwitterSession twittersession = Twitter.getSessionManager().getActiveSession();
            if (twittersession != null) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
