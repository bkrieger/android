package us.happ.android.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import us.happ.android.R;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class HttpCaller {
	private static final String TAG ="HttpCaller";
	
	public static final String HOST_IP = "158.130.107.180";
	public static final String HOST = "http://" + HOST_IP + ":3000/api";
	
	public static String getRequest(Context context, String path) {
		HttpClient httpclient = new DefaultHttpClient();
		
		String url = HOST + path;

		// TODO
		// Switch to namevaluepair later
		// API key
		
		// Prepare a request object
		HttpGet httpget = new HttpGet(url);

		// Execute the request
		HttpResponse response;
		
		// Set timeout
		final HttpParams httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		try {
			response = httpclient.execute(httpget);
			
//			Log.i(TAG, EntityUtils.toString(response.getEntity()));
			
			// Examine the response status
			String result = new BasicResponseHandler().handleResponse(response);
			
			return result;

		} catch (HttpResponseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
    
    private static String byteArrayToHexString(byte[] b) {
    	String result = "";
    	for (int i=0; i < b.length; i++) {
    		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
    	}
    	return result;
    }

}
