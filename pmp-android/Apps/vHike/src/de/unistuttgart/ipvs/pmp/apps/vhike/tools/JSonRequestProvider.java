package de.unistuttgart.ipvs.pmp.apps.vhike.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;

/**
 * Provides all the JSonRequests that are possible between vHike and webservice
 * outside. It connects to the webservice and getting the JsonObject. After the
 * JsonObject was get, return the JsonObject to the caller class.
 * 
 * 
 * @author Alexander Wassiljew
 * 
 */
public class JSonRequestProvider {

	boolean debug = false;

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
	public static JsonObject doRequest(List<ParamObject> listToParse,
			String url, boolean debug) throws ClientProtocolException,
			IOException {

		String getParam = "";
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
		getParam = buf.toString();
		// Cut the last '&' out
		getParam = getParam.substring(0, getParam.length() - 1);

		Log.i("Parameter :" + getParam);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(Constants.WEBSERVICE_URL + getParam);

		List<NameValuePair> namelist = new ArrayList<NameValuePair>();

		// Iterate over objects, wich have to be post to the webservice
		// POST REQUESTS
		for (ParamObject object : listToParse) {
			if ((object.isPost())) {
				namelist.add(new BasicNameValuePair(object.getKey(), object
						.getValue()));
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
			JsonParser parser = new JsonParser();
			BufferedReader tempR = new BufferedReader(r);

			if (debug) {
				Log.w("=====JSonRequestProvider DEBUG MODE===");
				Log.w("======DEBUG MODE============");
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("Json: " + r.readLine());
				Log.w("======DEBUG MODE END=====");
			}

			try {
				jsonObject = parser.parse(r).getAsJsonObject();
			} catch (Exception e) {
			}

		}

		return jsonObject;
	}

}
