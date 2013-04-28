package ca.bsolomon.gw2tp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class LegendaryConnection {

	public CookieStore cs = new BasicCookieStore();
	
	
	public HttpClient queryServer(int itemId) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httppost = new HttpGet("http://www.gw2spidy.com/api/v0.9/json/item/"+itemId);
		
	    try {
	        // Add your data
	        HttpResponse response = httpclient.execute(httppost);

	        BufferedReader rd = new BufferedReader
	        		  (new InputStreamReader(response.getEntity().getContent()));
	        		    
	        String longline = "";
    		String line = "";
    		while ((line = rd.readLine()) != null) {
    			longline+=line;
    		}
    		JSONObject json = (JSONObject) JSONSerializer.toJSON( longline );
    		JSONObject result = json.getJSONObject("result");
			double sell_price = result.getDouble("min_sale_unit_price")/100;
			double buy_price = result.getDouble("max_offer_unit_price")/100;
			String name = result.getString("name");
			int buy_count = result.getInt("offer_availability");
			int sell_count = result.getInt("sale_availability");
			
			boolean isHighDemand = false;
			
			if (buy_count > 500)
				isHighDemand = true;
			
			//System.out.println(name+"\t"+sell_price+"\t"+buy_price+"\t"+(sell_price*0.85-buy_price)+"\t"+sell_count/(float)buy_count+"\t"+isHighDemand);
			System.out.print(buy_price+"\t");
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }	
	    
	    return httpclient;
	}
	
	
	public static void main(String[] args) {
		int[] values = {29166,
				24555,
				24358, 24357, 24351, 24340, 24300, 24295, 24289, 24283, 24320,
				24277, 19976, 19721, 19686, 19685, 19684, 19681
				};
		
		LegendaryConnection conn = new LegendaryConnection();
		
		for (int val:values) {
			conn.queryServer(val);
		}
	}
	
}
