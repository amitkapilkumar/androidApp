package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.model.BeaconDescription;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote.BeaconID;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote.EstimoteCloudBeaconDetails;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote.EstimoteCloudBeaconDetailsFactory;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote.ProximityContentManager;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.AppUtil;
import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.ConnectionUtil;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.model.Color;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.internal.DeviceSettingBuilder;
import com.estimote.sdk.connection.settings.Beacon;
import com.estimote.sdk.telemetry.EstimoteTelemetry;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    Button enter;
    EditText user;
    private String scanId;
    private BeaconManager beaconManager;
    private ConnectionUtil connectionUtil;
    private static final String TAG = "MainActivity";

    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();
    private static final Map<String, String> IDENTIFIER_NAME = new HashMap<>();
    private static final Map<String, Double> NAME_TEMPERATURE = new HashMap<>();
    private static Color previousZone = null;

    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE, android.graphics.Color.rgb(98, 84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL, android.graphics.Color.rgb(155, 186, 160));

        IDENTIFIER_NAME.put("[807138244396695d91d4cbe722bcab0e]", "LOUNGE");
        IDENTIFIER_NAME.put("[28c5cf90ee7fb6b2d9c6b18c5744662c]", "BEDROOM");
        IDENTIFIER_NAME.put("[521c61ea31685bdf030ee6c56fc1c835]", "KITCHEN");

        NAME_TEMPERATURE.put("LOUNGE", null);
        NAME_TEMPERATURE.put("BEDROOM", null);
        NAME_TEMPERATURE.put("KITCHEN", null);
    }

    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private ProximityContentManager proximityContentManager;

    private static final int MAX_DURATION = 200;
    private static long startTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if(me.getAction() == MotionEvent.ACTION_UP) {
            startTime = System.currentTimeMillis();
        }
        else if(me.getAction() == MotionEvent.ACTION_DOWN) {
            if(System.currentTimeMillis() - startTime <= MAX_DURATION) {
                Log.d("TAP", "touch detected...");
                if(user == null) {
                    user = (EditText) findViewById(R.id.user);
                }
                if(enter == null) {
                    enter = (Button) findViewById(R.id.enter);
                }
                user.setText("");
                enter.setVisibility(View.VISIBLE);
                user.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enter = (Button) findViewById(R.id.enter);
        user = (EditText) findViewById(R.id.user);
        final BeaconDescription beaconDescription = new BeaconDescription();
        connectionUtil = new ConnectionUtil();
        super.onCreate(savedInstanceState);
        EstimoteSDK.enableRangingAnalytics(true);
        EstimoteSDK.enableMonitoringAnalytics(true);
        setContentView(R.layout.activity_main);
        beaconManager = new BeaconManager(this);
        proximityContentManager = new ProximityContentManager(this, AppUtil.beacons, new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                Integer backgroundColor;
                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    text = "You're in " + beaconDetails.getBeaconName() + "\n";
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());
                    beaconDescription.setName(beaconDetails.getBeaconName());
                    beaconDescription.setColor(beaconDetails.getBeaconColor());
                    processMyZone(previousZone, beaconDetails.getBeaconColor());
                    previousZone = beaconDetails.getBeaconColor();
                } else {
                    processMyZone(previousZone, null);
                    text = "Not in range.";
                    backgroundColor = null;
                    previousZone = null;
                    beaconDescription.setName(null);
                    beaconDescription.setColor(null);
                }
                toggleImage(beaconDescription.getColor());
                ((TextView) findViewById(R.id.textView)).setText(text);
                findViewById(R.id.relativeLayout).setBackgroundColor(backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });

        //Region[] regions = AppUtil.regions;

        beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener() {
            String text;
            @Override
            public void onTelemetriesFound(List<EstimoteTelemetry> telemetries) {
                for (EstimoteTelemetry tlm : telemetries) {
                    Log.d("TELEMETRY", "beaconID: " + tlm.deviceId + ", temperature: " + tlm.temperature + " °C");
                    if(IDENTIFIER_NAME.get(tlm.deviceId.toString()) != null) {
                        NAME_TEMPERATURE.put(IDENTIFIER_NAME.get(tlm.deviceId.toString()).toUpperCase(), tlm.temperature);
                    }
                }
                if(beaconDescription.getName() != null) {
                    text = "You're in " + beaconDescription.getName() + "\n";
                    Double d = NAME_TEMPERATURE.get(beaconDescription.getName().toUpperCase());
                    text = text + "Temperature: " + d + " °C";
                    if(d != null) {
                        processMyTemperature(beaconDescription.getColor(), d.toString());
                    }
                }
                else {
                    text = "Not in range.";
                    findViewById(R.id.relativeLayout).setBackgroundColor(BACKGROUND_COLOR_NEUTRAL);
                }
                ((TextView) findViewById(R.id.textView)).setText(text);
            }
        });

        /*beaconManager.startRanging(regions[0]);
        beaconManager.startRanging(regions[1]);
        beaconManager.startRanging(regions[2]);

        beaconManager.startMonitoring(regions[0]);
        beaconManager.startMonitoring(regions[1]);
        beaconManager.startMonitoring(regions[2]);*/

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void toggleImage(Color color) {
        if(color == null) {
            return;
        }
        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        switch (color) {
            case BLUEBERRY_PIE:
                imgView.setImageResource(R.drawable.bedroom);
                break;
            case ICY_MARSHMALLOW:
                imgView.setImageResource(R.drawable.kitchen);
                break;
            case MINT_COCKTAIL:
                imgView.setImageResource(R.drawable.lounge);
                break;
            default:
        }
    }

    public void login(View view){
        String userName = "";
        if(user == null) {
            user = (EditText) findViewById(R.id.user);
        }
        if(enter == null) {
            enter = (Button) findViewById(R.id.enter);
        }
        if(user != null) {
            userName = user.getText().toString();
            if(userName != null && !userName.trim().equals("")) {
                try {
                    connectionUtil.pushProfile(userName);
                    getSupportActionBar().setTitle("Welcome " + userName + " to Centrica Demo");
                } catch (IOException e) {
                    Log.e(TAG, "ERROR : IOException while pushing profile");
                }
            }
        }
        user.setText("");
        enter.setVisibility(View.GONE);
        user.setVisibility(View.GONE);

        /*AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Home of Future")
                .setMessage("Name saved as " + userName + " !!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }

    @Override protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager.startTelemetryDiscovery();
            }
        });
    }

    @Override protected void onStop() {
        super.onStop();
        beaconManager.stopTelemetryDiscovery(scanId);
    }

    private void processMyTemperature(Color zone, String temperature) {
        switch(zone) {
            case BLUEBERRY_PIE:
                try {
                    connectionUtil.pushZone1Temperature(temperature);
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while pushing temperature for zone 1");
                }
                break;
            case ICY_MARSHMALLOW:
                try {
                    connectionUtil.pushZone2Temperature(temperature);
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while pushing temperature for zone 2");
                }
                break;
            case MINT_COCKTAIL:
                try {
                    connectionUtil.pushZone3Temperature(temperature);
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while pushing temperature for zone 3");
                }
                break;
        }
    }

    private void processMyZone(Color previousZone, Color currentZone) {
        if(previousZone == null && currentZone == null)
            return;

        if(previousZone == null && currentZone != null) {
            processEnteringZone(currentZone);
            return;
        }

        if(previousZone != null && currentZone == null) {
            processExitingZone(previousZone);
            return;
        }
        processEnteringZone(currentZone);
        processExitingZone(previousZone);

    }

    private void processEnteringZone(Color currentZone) {
        switch (currentZone) {
            case BLUEBERRY_PIE:
                Log.d(TAG, "!! Entering zone 1");
                try {
                    connectionUtil.enterZone1();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while entering zone 1");
                }
                break;
            case ICY_MARSHMALLOW:
                Log.d(TAG, "!! Entering zone 2");
                try {
                    connectionUtil.enterZone2();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while entering zone 2");
                }
                break;
            case MINT_COCKTAIL:
                Log.d(TAG, "!! Entering zone 3");
                try {
                    connectionUtil.enterZone3();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while entering zone 3");
                }
                break;
        }
    }

    private void processExitingZone(Color previousZone) {
        switch (previousZone) {
            case BLUEBERRY_PIE:
                Log.d(TAG, "!! Exiting zone 1");
                try {
                    connectionUtil.exitZone1();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while exiting zone 1");
                }
                break;
            case ICY_MARSHMALLOW:
                Log.d(TAG, "!! Exiting zone 2");
                try {
                    connectionUtil.exitZone2();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while exiting zone 2");
                }
                break;
            case MINT_COCKTAIL:
                Log.d(TAG, "!! Exiting zone 3");
                try {
                    connectionUtil.exitZone3();
                }
                catch(IOException e) {
                    Log.e(TAG, "ERROR : IOException while exiting zone 3");
                }
                break;
        }
    }
}