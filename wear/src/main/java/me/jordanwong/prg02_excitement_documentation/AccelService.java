package me.jordanwong.prg02_excitement_documentation;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by Jordan on 7/9/2015.
 */
public class AccelService extends Service implements SensorEventListener {

    private static final String DEBUG_TAG = "AccelService";
    private SensorManager mySensorManager;
    private Sensor mySensor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do nothing
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // :D
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float gravity =  (float) 9.8;
        float xAccel, yAccel, zAccel;
        xAccel = Math.abs(event.values[0]);
        yAccel = Math.abs(event.values[1]);
        zAccel = Math.abs(event.values[2]);
        if ((xAccel > gravity) || (yAccel > gravity)
                || (zAccel > gravity)) {
            createNotification();
        }
    }

    public void createNotification() {
        Intent intent  = new Intent(this, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.happy)
                .setContentText("Swipe Left!")
                .setContentTitle("GETTING HYPHY?")
                .setContentIntent(pendingintent);
        Intent intent1 = new Intent(getApplicationContext(), GoogleApiClientService.class);
        PendingIntent pendingintent1 = PendingIntent.getService(getApplicationContext(), 0,
                intent1, 0);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.camera,
                        getString(R.string.picture_time), pendingintent1)
                .extend(new NotificationCompat.Action.WearableExtender()
                        .setAvailableOffline(false)).build();

        notification.addAction(action);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        int notificationId = 1;
        notificationManager.notify(notificationId, notification.build());
    }

    @Override
    public void onDestroy() {
        mySensorManager.unregisterListener(this);
        super.onDestroy();
    }
}