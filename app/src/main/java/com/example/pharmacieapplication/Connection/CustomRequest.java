package com.example.pharmacieapplication.Connection;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.util.Map;

public class CustomRequest extends Request<NetworkResponse> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params,
                         Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        System.out.println("sending !");
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse networkResponse) {
        return Response.success(networkResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

//    @Override
//    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
//        //return Response.success(networkResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
//        return null;
//    }

    @Override
    protected void deliverResponse(NetworkResponse response) {

    }
}
