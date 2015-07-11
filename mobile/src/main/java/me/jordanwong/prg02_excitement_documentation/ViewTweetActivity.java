package me.jordanwong.prg02_excitement_documentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Jordan on 7/10/2015.
 */
public class ViewTweetActivity extends Activity {
    private static final String TWITTER_KEY = "VhiccgFLe7zZ1PASP7IHjkyHq";
    private static final String TWITTER_SECRET = "3lGj4JX9K6wYFLUHFAIsDInPs8U7s5YYLVl3Lga0Bz9y7bcfkR";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        setContentView(R.layout.activity_main);
        fetchTweet();
    }

    public void fetchTweet() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.tweet_layout);
        Intent intent = getIntent();
        long id = intent.getExtras().getLong("id");
        TweetUtils.loadTweet(id, new LoadCallback<Tweet>() {
            @Override
            public void success(Tweet tweet) {
                TweetView view = new TweetView(ViewTweetActivity.this, tweet);
                layout.addView(view);
            }

            @Override
            public void failure(TwitterException exception) {
                // do nothing
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        fetchTweet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tweet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
