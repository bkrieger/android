package us.happ.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import us.happ.R;

import android.content.Context;
import android.util.Log;

public class HttpCaller {
	private static final String TAG ="HttpCaller";
	
	public static final String HOST_IP = "www.happ.us";
	public static final String API_VERSION = "/v1";
	public static final String HOST = "http://" + HOST_IP + ":3000/api" + API_VERSION;
	
	public static String getRequest(Context context, String path) {
		HttpClient httpclient = new DefaultHttpClient();
		
		String url = HOST + path;

		// TODO
		// Switch to namevaluepair later
		// API key
		String key = context.getResources().getString(R.string.api_key_param);
		String value = context.getResources().getString(R.string.api_key_value);
		url += "&" + key + "=" + value;
		
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

    // TODO
    // handle duplicate code
    // TODO
    // change from path to NameValue
	public static String postRequest(APIService apiService, String path) {
		HttpClient httpclient = new DefaultHttpClient();
		
		String url = HOST + path;
		
		// API key
		url += "&muffin=2";

		// Prepare a request object
		HttpPost httpPost = new HttpPost(url);

		// Execute the request
		HttpResponse response;
		
		// Set timeout
		final HttpParams httpParams = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		
		try {
			response = httpclient.execute(httpPost);
			
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

}
