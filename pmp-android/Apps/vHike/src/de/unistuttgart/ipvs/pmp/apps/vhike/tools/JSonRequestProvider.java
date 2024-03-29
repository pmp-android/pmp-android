/*
 * Copyright 2012 pmp-android development team
 * Project: vHikeApp
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.apps.vhike.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;

/**
 * Provides all the JSonRequests that are possible between vHike and web service outside. It connects to the
 * web service and getting the JsonObject. After the JsonObject was get, return the JsonObject to the caller
 * class.
 * 
 * @author Alexander Wassiljew, Dang Huynh
 */
public class JSonRequestProvider {
    
    private static final String TAG = "JSonRequestProvider";
    
    
    // private static final boolean debug = false;
    
    /**
     * private constructor cause of singleton
     */
    public JSonRequestProvider() {
    }
    
    
    /**
     * Sending a request to the WEBSERVICE_URL defined in {@link Constants}
     * 
     * @param listToParse
     *            contains all the parameters, which have to be parsed
     * @param url
     * @param debug
     *            mode true or false
     * @return JsonObject
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static JsonObject doRequest(List<ParamObject> listToParse, String url)
            throws ClientProtocolException,
            IOException {
        
        // GET REQUESTS
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append("?");
        
        for (ParamObject object : listToParse) {
            
            if (!(object.isPost())) {
                buf.append(object.getKey() + "=" + object.getValue());
                buf.append("&");
                // getParam = getParam + object.getKey() + "=" +
                // object.getValue();
                // getParam = getParam + "&";
            }
        }
        String getParam = buf.toString();
        // Cut the last '&' out
        getParam = getParam.substring(0, getParam.length() - 1);
        
        Log.d(TAG, "Param: " + getParam);
        
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost httppost = new HttpPost(Constants.WEBSERVICE_URL + getParam);
        
        List<NameValuePair> namelist = new ArrayList<NameValuePair>(listToParse.size());
        
        // Iterate over objects, which have to be post to the web service
        // POST REQUESTS
        for (ParamObject object : listToParse) {
            if ((object.isPost())) {
                namelist.add(new BasicNameValuePair(object.getKey(), object.getValue()));
                // TODO: Remove this line
                Log.d(TAG, "POST: " + object.getKey() + " - " + object.getValue());
            }
        }
        
        httppost.setEntity(new UrlEncodedFormEntity(namelist, "UTF-8"));
        
        // Execute HTTP Post Request
        HttpResponse response;
        JsonObject jsonObject = null;
        response = httpclient.execute(httppost);
        // for JSON:
        if (response != null) {
            InputStream is = response.getEntity().getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            try {
                StringBuffer sb = new StringBuffer();
                String s = null;
                while ((s = r.readLine()) != null) {
                    sb.append(s).append(System.getProperty("line.separator"));
                }
                s = sb.toString();
                
                jsonObject = (new JsonParser()).parse(s).getAsJsonObject();
                Log.d(TAG, "======DEBUG=====");
                Log.d(TAG, s);
                Log.d(TAG, "======DEBUG=====");
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "No Response");
        }
        return jsonObject;
    }
}
