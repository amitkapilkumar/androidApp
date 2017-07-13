package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.model;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.annotations.SerializedName;

/**
 * Created by cognizant on 10/05/2017.
 */

public class ExitResponse {
    @SerializedName("exit-operation")
    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
