package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util;

import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote.BeaconID;
import com.estimote.sdk.Region;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by cognizant on 10/05/2017.
 */

public class AppUtil {
    private static final String uuid = "B075080D-19C2-730D-B9D9-D923941D8970";
    public static final Region[] regions = new Region[] {
        // B075080D-19C2-730D-B9D9-D923941D8970
        // B9407F30-F5F8-466E-AFF9-25556B57FE6D default
        new Region("521c61ea31685bdf030ee6c56fc1c835", UUID.fromString(uuid), 4686, 54554), // ice
        new Region("28c5cf90ee7fb6b2d9c6b18c5744662c", UUID.fromString(uuid), 29864, 24287), // blueberry
        new Region("807138244396695d91d4cbe722bcab0e", UUID.fromString(uuid), 56363, 15885)  // mint
    };

    public static final List<BeaconID> beacons = Arrays.asList(
        new BeaconID(uuid, 29864, 24287),
        new BeaconID(uuid, 4686, 54554), // ice
        new BeaconID(uuid, 56363, 15885)
    );

    //public static String user = null;
    // Zone1 -> blueberry
    // Zone2 -> ice
    // Zone3 -> mint
}