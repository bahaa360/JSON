package com.kutub.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class JSONexample extends Activity {
	
	EditText searchInput;
	Button searchButton;
	WebView webView;
	Timer timer = new Timer();

	
	
	final Handler loadContent = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			Log.d("json", (String.valueOf(msg.obj)));
			//create json object
			try {
				JSONObject jObject = new JSONObject(String.valueOf(msg.obj));
				//webView.loadData(String.valueOf(msg.obj), "text/html", "UTF-8");
				JSONObject jArray = jObject.getJSONObject("list");
				JSONArray resources = jArray.getJSONArray("resources");
				JSONObject resourcesObject = resources.getJSONObject(0);
				JSONObject resource = resourcesObject.getJSONObject("resource");
				JSONObject fields = resource.getJSONObject("fields");
				
				
				String name = fields.getString("name");
				String price = fields.getString("price");
				String symbol = fields.getString("symbol");
				String ts = fields.getString("ts");
				String type = fields.getString("type");
				String utctime = fields.getString("utctime");
				String volume = fields.getString("volume");
	
				//JSONObject meta = jArray.getJSONObject("meta");
				//String type = meta.getString("type");
				webView.loadData("name:<br>" + name +
						"<br><br>price:<br>" + price + 
						"<br><br>symbol:<br>" + symbol +
						"<br><br>TS:<br>" + ts +
						"<br><br>type:<br>" + type +
						"<br><br>utctime:<br>" + utctime +
						"<br><br>volume:<br>" + volume, "text/html", "UTF-8");
				Log.d("json", name);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        searchInput = (EditText) findViewById(R.id.searchbar); 
        searchButton = (Button) findViewById(R.id.search);
        webView = (WebView) findViewById(R.id.webView);
        
        
        searchButton.setOnClickListener(new View.OnClickListener() {
       
        	@Override
			public void onClick(View v) {

				timer = new Timer();

				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {

						try {
							if (isNetworkAvailable()) {

								try {
									String stockSymbol = searchInput.getText()
											.toString();
									URL url = new URL(
											"http://finance.yahoo.com/webservice/v1/symbols/"
													+ stockSymbol
													+ "/quote?format=json");

									try {

										BufferedReader in = new BufferedReader(
												new InputStreamReader(url
														.openStream()));
										String data = "";
										String inputLine = "";
										while ((inputLine = in.readLine()) != null)
											data += inputLine;
										in.close();

										JSONObject jsonResponse = new JSONObject(
												data);

										Message msg = new Message();
										msg.obj = jsonResponse;

										loadContent.sendMessage(msg);

									} catch (Exception e) {
										Log.e("Read Error", e.toString());
									}

								} catch (Exception e) {
									Log.e("Read Error", e.toString());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}, 0, 10000);
			}
		});
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnectedOrConnecting());
	}
}
