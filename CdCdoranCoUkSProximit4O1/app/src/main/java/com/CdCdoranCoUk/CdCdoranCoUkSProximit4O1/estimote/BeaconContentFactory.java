package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.estimote;

public interface BeaconContentFactory {

    void getContent(BeaconID beaconID, Callback callback);

    interface Callback {
        void onContentReady(Object content);
    }
}
