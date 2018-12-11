package uk.ac.bath.mindpalaceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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

public class IndoorLocation extends AppCompatActivity {

    private final String APP_ID = "mind-palace-maker-9zr";
    private final String APP_TOKEN = "058c5ce426b9d034d8e7b2c5f5b64b8e";
    private final String LOCATION_ID = "cjd47-s-location-nwe";
    private CloudCredentials cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
    private IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(this, cloudCredentials);
    private ScanningIndoorLocationManager indoorLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.indoor_location);

        cloudManager.getLocation(LOCATION_ID, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Location '" + location.getName() + "' loaded from Estimote Cloud.",
                        Toast.LENGTH_SHORT);
                toast.show();

                final IndoorLocationView indoorLocationView = findViewById(R.id.indoor_view);
                indoorLocationView.setLocation(location);

                indoorLocationManager =
                        new IndoorLocationManagerBuilder(getApplicationContext(), location, cloudCredentials)
                                .withDefaultScanner()
                                .build();

                indoorLocationManager.startPositioning();

                indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(LocationPosition locationPosition) {
                        indoorLocationView.updatePosition(locationPosition);
                        System.out.println(locationPosition.getX());
                    }

                    @Override
                    public void onPositionOutsideLocation() {
                        indoorLocationView.hidePosition();
                    }
                });
            }

            @Override
            public void failure(EstimoteCloudException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ERROR: Failed to get location from Estimote Cloud.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
