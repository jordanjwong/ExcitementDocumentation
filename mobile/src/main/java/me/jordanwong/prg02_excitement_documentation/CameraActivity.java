package me.jordanwong.prg02_excitement_documentation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jordan on 7/9/2015.
 */
public class CameraActivity extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int TWEET_REQUEST_CODE = 110;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        takePicture();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "PRG02_excitement_documentation");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("PRG02", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            Intent intent = new TweetComposer.Builder(this)
                    .text(getString(R.string.cs160excited))
                    .image(fileUri)
                    .createIntent();

            startActivityForResult(intent, TWEET_REQUEST_CODE);
        } else if (requestCode == TWEET_REQUEST_CODE) {
            Intent intent = new Intent(this, FetchTweetService.class);
            startService(intent);
            finish();
        } else if ((resultCode == RESULT_CANCELED) &&
                (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)) {
            finish();
        } else {
            Intent intent = new Intent(this, FetchTweetService.class);
            startService(intent);
            finish();
        }
    }
}
