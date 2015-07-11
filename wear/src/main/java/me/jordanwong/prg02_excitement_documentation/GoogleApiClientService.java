package me.jordanwong.prg02_excitement_documentation;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.Set;

/**
 * Created by Jordan on 7/10/2015.
 */
public class GoogleApiClientService extends IntentService {

    private Set<Node> nodes = null;
    private Node node = null;
    private GoogleApiClient myGoogleApiClient;
    private static final String CAMERA = "Camera";

    public GoogleApiClientService() {
        super("GoogleApiClientService");
    }

    public void createGoogleAPI() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("TAG", "onConnected: " + bundle);
                        // Do something
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("TAG", "onConnectionSuspended: " + i);
                        // blank
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("TAG", "onConnectionFailed: " + connectionResult);
                        // blank
                    }
                })
                .addApi(Wearable.API)
                .build();
        myGoogleApiClient.connect();
        CapabilityApi.GetCapabilityResult capResult =
                Wearable.CapabilityApi.getCapability(myGoogleApiClient, CAMERA,
                        CapabilityApi.FILTER_REACHABLE)
                        .await();
        nodes = capResult.getCapability().getNodes();
        if (nodes.size() > 0) {
            node = nodes.iterator().next();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createGoogleAPI();
        sendMessage();
        myGoogleApiClient.disconnect();
    }

    private void sendMessage() {
        if (node != null) {
            Wearable.MessageApi.sendMessage(myGoogleApiClient,
                    node.getId(), CAMERA, null).await();
        }
    }
}
