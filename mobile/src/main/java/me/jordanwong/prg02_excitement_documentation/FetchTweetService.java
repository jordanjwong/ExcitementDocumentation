package me.jordanwong.prg02_excitement_documentation;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Jordan on 7/10/2015.
 */
public class FetchTweetService extends Service {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VhiccgFLe7zZ1PASP7IHjkyHq";
    private static final String TWITTER_SECRET = "3lGj4JX9K6wYFLUHFAIsDInPs8U7s5YYLVl3Lga0Bz9y7bcfkR";
    private String cs160excited;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cs160excited = "#cs160excited";
        fetchTweet();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // :D
    }

    public void fetchTweet() {
        TwitterApiClient client = TwitterCore.getInstance().getApiClient();
        SearchService service = client.getSearchService();

        service.tweets(cs160excited, null, null, null, "mixed", 20, null, null,
                null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        List<Tweet> listoftweets = result.data.tweets;
                        TwitterSession mysession = Twitter.getSessionManager().getActiveSession();

                        for (Tweet tweet : listoftweets) {
                            if(tweet.user.id != mysession.getUserId()) {
                                String imageLink = tweet.entities.media.get(0).mediaUrl;
                                String text = tweet.text;
                                long tweetId = tweet.id;
                                Bitmap image = getBitmapFromURL(imageLink);
                                sendNotification(image, text, tweetId);
                                break;
                            }
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });
    }

    public void sendNotification(Bitmap img, String text, long id) {
        Intent intent = new Intent(this, ViewTweetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id", id);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.WearableExtender wearable = new NotificationCompat.WearableExtender()
                .setHintHideIcon(true)
                .setBackground(img);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.happy)
                .setLargeIcon(img)
                .setContentText(text)
                .setContentTitle("Hey look, another tweet!")
                .setContentIntent(pendingintent)
                .extend(wearable)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        int notificationId = 1;
        notificationManager.notify(notificationId, notification.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        final String source = src;

        ExecutorService executeService = Executors.newSingleThreadExecutor();
        Future<Bitmap> bitmaps = executeService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                try {
                    return BitmapFactory.decodeStream((InputStream) new URL(source).getContent());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        try {
            return bitmaps.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }
}
