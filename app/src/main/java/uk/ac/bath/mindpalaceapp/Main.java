package uk.ac.bath.mindpalaceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.estimote.indoorsdk.EstimoteCloudCredentials;
import com.estimote.indoorsdk.IndoorLocationManagerBuilder;
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.CloudCredentials;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.view.IndoorLocationView;

public class Main extends AppCompatActivity {

    private final String APP_ID = "mind-palace-maker-9zr";
    private final String APP_TOKEN = "058c5ce426b9d034d8e7b2c5f5b64b8e";
    private final String LOCATION_ID = "loc_id";
    private CloudCredentials cloudCredentials;
    private ScanningIndoorLocationManager indoorLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use IndoorCloudManagerFactory to get objects for communicating with Estimote cloud.
        cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(this, cloudCredentials);
        cloudManager.getLocation(LOCATION_ID, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                // ...do something with Location object... (needed to initialise IndoorLocationManager!)

                // Use IndoorLocationView to display location on screen. It exists in activity_main.xml.
                final IndoorLocationView indoorLocationView = findViewById(R.id.indoor_view);
                indoorLocationView.setLocation(location);

                // IndoorLocationManager
                ScanningIndoorLocationManager indoorLocationManager =
                        new IndoorLocationManagerBuilder(getApplicationContext(), location, cloudCredentials)
                                .withDefaultScanner()
                                .build();

                indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(LocationPosition locationPosition) {
                        indoorLocationView.updatePosition(locationPosition);
                    }

                    @Override
                    public void onPositionOutsideLocation() {
                        indoorLocationView.hidePosition();
                    }
                });
            }

            @Override
            public void failure(EstimoteCloudException e) {
                // oops! Failure to get location from cloud.
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        indoorLocationManager.startPositioning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        indoorLocationManager.stopPositioning();
    }
}
