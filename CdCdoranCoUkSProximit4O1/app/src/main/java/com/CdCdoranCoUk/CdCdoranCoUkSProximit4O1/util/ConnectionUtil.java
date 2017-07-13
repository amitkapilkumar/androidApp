package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.model.EnterResponse;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.TEMPERATURE_PLACE_HOLDER;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.TIMESTAMP_PLACE_HOLDER;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_ENTER_ZONE_1;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_ENTER_ZONE_2;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_ENTER_ZONE_3;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_EXIT_ZONE_1;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_EXIT_ZONE_2;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_EXIT_ZONE_3;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_PUSH_PROFILE;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_PUSH_TELEMETRY_ZONE_1;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_PUSH_TELEMETRY_ZONE_2;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.URL_PUSH_TELEMETRY_ZONE_3;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.USER;
import static com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.util.Constants.USER_PLACEHOLDER;


public class ConnectionUtil {

    public void pushProfile(String name) throws IOException {
        String url = URL_PUSH_PROFILE.replace(USER_PLACEHOLDER, URLEncoder.encode(name, "UTF-8"));
        callPutRequest(url);
    }

    public void pushZone1Temperature(String temperature) throws IOException {
        String url = URL_PUSH_TELEMETRY_ZONE_1.replace(TEMPERATURE_PLACE_HOLDER, temperature);
        Long timestamp = System.currentTimeMillis() / 1000L;
        url = url.replace(TIMESTAMP_PLACE_HOLDER, timestamp.toString());
        callPutRequest(url);
    }

    public void pushZone2Temperature(String temperature) throws IOException {
        String url = URL_PUSH_TELEMETRY_ZONE_2.replace(TEMPERATURE_PLACE_HOLDER, temperature);
        Long timestamp = System.currentTimeMillis() / 1000L;
        url = url.replace(TIMESTAMP_PLACE_HOLDER, timestamp.toString());
        callPutRequest(url);
    }

    public void pushZone3Temperature(String temperature) throws IOException {
        String url = URL_PUSH_TELEMETRY_ZONE_3.replace(TEMPERATURE_PLACE_HOLDER, temperature);
        Long timestamp = System.currentTimeMillis() / 1000L;
        url = url.replace(TIMESTAMP_PLACE_HOLDER, timestamp.toString());
        callPutRequest(url);
    }

    public String enterZone1() throws IOException {
        return getPutResponse(URL_ENTER_ZONE_1);
    }

    public String enterZone2()throws IOException {
        return getPutResponse(URL_ENTER_ZONE_2);
    }

    public String enterZone3()throws IOException {
        return getPutResponse(URL_ENTER_ZONE_3);
    }

    public String exitZone1()throws IOException {
        return getDeleteResponse(URL_EXIT_ZONE_1);
    }

    public String exitZone2()throws IOException {
        return getDeleteResponse(URL_EXIT_ZONE_2);
    }

    public String exitZone3()throws IOException {
        return getDeleteResponse(URL_EXIT_ZONE_3);
    }

    @Nullable
    private void callPutRequest(@NonNull String url) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        client.execute(httpPut);
    }

    @Nullable
    private String getPutResponse(@NonNull String url) throws IOException{
        HttpClient client = new DefaultHttpClient();
        /*if(AppUtil.user != null) {
            List<NameValuePair> params = new LinkedList<>();
            params.add(new BasicNameValuePair(USER, AppUtil.user));
            url = url + URLEncodedUtils.format(params, "utf-8");
        }*/
        HttpPut httpPut = new HttpPut(url);
        return getResponse(client, httpPut);
    }

    @Nullable
    private String getDeleteResponse(@NonNull String url) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        return getResponse(client, httpDelete);
    }

    @Nullable
    private String getResponse(HttpClient client, HttpUriRequest http) throws IOException {
        HttpResponse httpResponse = client.execute(http);
        if(httpResponse.getStatusLine().getStatusCode() != 200) {
            return null;
        }
        InputStream is = httpResponse.getEntity().getContent();
        String content = convertStreamToString(is);
        Gson gson = new Gson();
        EnterResponse response = gson.fromJson(content, EnterResponse.class);
        is.close();
        return response.getOperation();
    }

    public String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        return sb.toString();
    }
}
