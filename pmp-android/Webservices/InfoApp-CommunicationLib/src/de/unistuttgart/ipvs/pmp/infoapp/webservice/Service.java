/*
 * Copyright 2012 pmp-android development team
 * Project: InfoApp-CommunicationLib
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
package de.unistuttgart.ipvs.pmp.infoapp.webservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InternalDatabaseException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InvalidEventOrderException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InvalidParameterException;

/**
 * This class handles the connection between the application and the webservices.
 * To do so, it stores the webservices' URL and the unique device ID
 * 
 * @author Patrick Strobel
 */
public class Service {
    
    public static String DEFAULT_URL = "https://infoapp.no-ip.org/json";
    private String url;
    private String deviceId;
    private HttpClient httpClient = new DefaultHttpClient();
    
    
    /**
     * Creates a new service-instance used for communicating with the webservices
     * 
     * @param url
     *            URL of the webservices.
     * @param deviceId
     *            16-bit (32 characters) HEX-value used the uniquely identify the Android device
     */
    public Service(String url, String deviceId) {
        this(url, deviceId, true);
    }
    
    
    /**
     * Creates a new service-instance used for communicating with the webservices
     * 
     * @param url
     *            URL of the webservices.
     * @param deviceId
     *            16-bit (32 characters) HEX-value used the uniquely identify the Android device
     * @param acceptAllCerts
     *            If set to true, all SSL certificates are accepted when using a SSL-connection. This has to be set to
     *            <code>true</code> when untrusted certificates (e. g. self-signed) are used.
     */
    public Service(String url, String deviceId, boolean acceptAllCerts) {
        this.url = url;
        this.deviceId = deviceId;
        
        if (acceptAllCerts) {
            acceptUntrustedCerts();
        }
    }
    
    
    /**
     * Get the HTTP client. The client is prepared to accept untrusted SSL-Certificates
     */
    private void acceptUntrustedCerts() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            
            // Load certificates used by the server
            String certType = KeyStore.getDefaultType();
            InputStream certStream = this.getClass()
                    .getResourceAsStream("/DefaultServerCert." + certType.toLowerCase());
            KeyStore keyStore = KeyStore.getInstance(certType);
            keyStore.load(certStream, null);
            certStream.close();
            
            // Create key-store manager and prepare SSL context
            ExtensibleTrustManager manager = new ExtensibleTrustManager();
            manager.addKeyStore(keyStore);
            sslContext.init(null, new TrustManager[] { manager }, new SecureRandom());
            SSLSocketFactory sslSocket = new ExtendedSSLSocketFactory(sslContext);
            sslSocket.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            ClientConnectionManager conMan = this.httpClient.getConnectionManager();
            
            // Register scheme
            Scheme httpsScheme = new Scheme("https", sslSocket, 443);
            SchemeRegistry reg = conMan.getSchemeRegistry();
            reg.register(httpsScheme);
            
            System.out.println("Schemes/Trust managers registered");
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Gets the HEX-string used to identify the device
     * 
     * @return 16-bit (32 characters) HEX-value used the uniquely identify the Android device
     */
    public String getDeviceId() {
        return this.deviceId;
    }
    
    
    /**
     * Requests a webservice using HTTP-GET. The device ID is automatically added to the parameter list
     * 
     * @param service
     *            The scripts name (filename, e. g. upload_connection_events.php)
     * @return Result returned by the service
     * @throws InternalDatabaseException
     *             Thrown, if an internal database error occurred while the webservice was running
     * @throws InvalidParameterException
     *             Thrown, if one parameter set by the constructor or a set-methode was not accepted by the webservice
     * @throws InvalidEventIdException
     *             Thrown, if the given events were not accepted by the webservice because at least one ID is already in
     *             use by a database entry
     * @throws InvalidEventOrderException
     *             Thrown, if the events are not ordered properly. That is, the IDs and timestamps are not in ascending
     *             order. See webservice specification for details
     * @throws IOException
     *             Thrown, if another communication error occured while contacting the webservice
     * @throws JSONException
     *             Thrown, if no JSON-string was returned by the server
     */
    public JSONObject requestGetService(String service) throws InternalDatabaseException, InvalidParameterException,
            InvalidEventOrderException, IOException, JSONException {
        return requestGetService(service, new ArrayList<BasicNameValuePair>(1));
    }
    
    
    /**
     * Requests a webservice using HTTP-GET. The device ID is automatically added to the parameter list
     * 
     * @param service
     *            The scripts name (filename, e. g. upload_connection_events.php)
     * @param params
     *            Additional GET parameters that should be sent to the service
     * @return Result returned by the service
     * @throws InternalDatabaseException
     *             Thrown, if an internal database error occurred while the webservice was running
     * @throws InvalidParameterException
     *             Thrown, if one parameter set by the constructor or a set-methode was not accepted by the webservice
     * @throws InvalidEventIdException
     *             Thrown, if the given events were not accepted by the webservice because at least one ID is already in
     *             use by a database entry
     * @throws InvalidEventOrderException
     *             Thrown, if the events are not ordered properly. That is, the IDs and timestamps are not in ascending
     *             order. See webservice specification for details
     * @throws IOException
     *             Thrown, if another communication error occured while contacting the webservice
     * @throws JSONException
     *             Thrown, if no JSON-string was returned by the server
     */
    public JSONObject requestGetService(String service, List<BasicNameValuePair> params)
            throws InternalDatabaseException, InvalidParameterException, InvalidEventOrderException, IOException,
            JSONException {
        params.add(new BasicNameValuePair("device", this.deviceId));
        String paramString = URLEncodedUtils.format(params, "UTF-8");
        HttpGet httpGet = new HttpGet(this.url + "/" + service + "?" + paramString);
        
        HttpClient client = this.httpClient;
        
        return getContent(client.execute(httpGet));
    }
    
    
    /**
     * Requests a webservice using HTTP-POST. The device ID is automatically added to the parameter list
     * 
     * @param service
     *            The scripts name (filename, e. g. upload_connection_events.php)
     * @param params
     *            Additional POST parameters that should be sent to the service
     * @return Result returned by the service
     * @throws InternalDatabaseException
     *             Thrown, if an internal database error occurred while the webservice was running
     * @throws InvalidParameterException
     *             Thrown, if one parameter set by the constructor or a set-methode was not accepted by the webservice
     * @throws InvalidEventIdException
     *             Thrown, if the given events were not accepted by the webservice because at least one ID is already in
     *             use by a database entry
     * @throws InvalidEventOrderException
     *             Thrown, if the events are not ordered properly. That is, the IDs and timestamps are not in ascending
     *             order. See webservice specification for details
     * @throws IOException
     *             Thrown, if another communication error occured while contacting the webservice
     * @throws JSONException
     *             Thrown, if no JSON-string was returned by the server
     */
    public JSONObject requestPostService(String service, List<BasicNameValuePair> params)
            throws InternalDatabaseException, InvalidParameterException, InvalidEventOrderException, IOException,
            JSONException {
        params.add(new BasicNameValuePair("device", this.deviceId));
        HttpPost httpPost = new HttpPost(this.url + "/" + service);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        
        HttpClient client = this.httpClient;
        return getContent(client.execute(httpPost));
    }
    
    
    private JSONObject getContent(HttpResponse res) throws InternalDatabaseException, InvalidParameterException,
            InvalidEventOrderException, IOException, JSONException {
        HttpEntity entity = res.getEntity();
        
        if (entity == null) {
            return null;
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        reader.close();
        JSONObject json = new JSONObject(sb.toString());
        
        checkResult(json);
        
        return json;
    }
    
    
    /**
     * Inspects a result and throws a exception if an error was returned
     * 
     * @param res
     * @throws JSONException
     * @throws InternalDatabaseException
     * @throws InvalidParameterException
     * @throws InvalidEventIdException
     * @throws InvalidEventOrderException
     */
    private void checkResult(JSONObject res) throws JSONException, InternalDatabaseException,
            InvalidParameterException, InvalidEventOrderException {
        if (!res.getBoolean("successful")) {
            String error = res.getString("error");
            String msg = res.getString("msg");
            
            if (error.equalsIgnoreCase("internal_database_error")) {
                throw new InternalDatabaseException(msg);
            }
            
            if (error.equalsIgnoreCase("invalid_parameter")) {
                throw new InvalidParameterException(msg);
            }
            
            if (error.equalsIgnoreCase("invalid_event_order")) {
                throw new InvalidEventOrderException(msg);
            }
        }
    }
}
