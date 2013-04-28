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

public class Connection {

	public CookieStore cs = new BasicCookieStore();
	
	
	public HttpClient queryServer(int itemId) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httppost = new HttpGet("http://www.gw2tp.net/api/v1/items/"+itemId);
		
	    try {
	        // Add your data
	        HttpResponse response = httpclient.execute(httppost);

	        BufferedReader rd = new BufferedReader
	        		  (new InputStreamReader(response.getEntity().getContent()));
	        		    
	        
    		String line = "";
    		while ((line = rd.readLine()) != null) {
    			JSONObject json = (JSONObject) JSONSerializer.toJSON( line );
    			double sell_price = json.getDouble("sell_price")/100;
    			double buy_price = json.getDouble("buy_price")/100;
    			String name = json.getString("name");
    			int buy_count = json.getInt("buy_count");
    			int sell_count = json.getInt("sell_count");
    			
    			boolean isHighDemand = false;
    			
    			if (buy_count > 500)
    				isHighDemand = true;
    			
    			System.out.println(name+"\t"+sell_price+"\t"+buy_price+"\t"+(sell_price*0.85-buy_price)+"\t"+sell_count/(float)buy_count+"\t"+isHighDemand);
    		}
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }	
	    
	    return httpclient;
	}
	
	
	public static void main(String[] args) {
		System.out.println("Item Name\tSell Price\tBuy Price\tFlip Profit\tSupply/Demand\tHigh Demand");
        
		int[] values = {6030,6035,6045,6039,8354,7064,
				8355,8763,8755,8489,
				8996,9402,9027,
				16495,16219,16569,
				15694,15966,16155
				//,19619,19633
				};
		
		Connection conn = new Connection();
		
		for (int val:values) {
			conn.queryServer(val);
		}
	}
	
}
