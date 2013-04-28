package ca.bsolomon.gw2tp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

public class SpidyConnection {

	public CookieStore cs = new BasicCookieStore();
	
	
	public HttpClient queryServer(int itemId, String delim, BufferedWriter bw) {
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
			
			double break_even = buy_price/0.85;
			
			System.out.println(name+""+delim+""+sell_price+""+delim+""+buy_price+""+delim+""+(sell_price*0.85-buy_price)+""+delim+""+break_even+""+delim+""+sell_count/(float)buy_count+""+delim+""+isHighDemand);
			bw.write(name+""+delim+""+sell_price+""+delim+""+buy_price+""+delim+""+(sell_price*0.85-buy_price)+""+delim+""+break_even+""+delim+""+sell_count/(float)buy_count+""+delim+""+isHighDemand+System.getProperty("line.separator"));
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }	
	    
	    return httpclient;
	}
	
	
	public static void main(String[] args) {
		String delim = "\t";
		
		if (args.length > 0)
			delim = args[0];
			
		System.out.println("Item Name"+delim+"Sell Price"+delim+"Buy Price"+delim+"Flip Profit"+delim+"Break Even"+delim+"Supply/Demand"+delim+"High Demand");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("ids.txt")));
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output"+System.currentTimeMillis()+".txt")));
			
			bw.write("Item Name"+delim+"Sell Price"+delim+"Buy Price"+delim+"Flip Profit"+delim+"Break Even"+delim+"Supply/Demand"+delim+"High Demand"+System.getProperty("line.separator"));
			
			SpidyConnection conn = new SpidyConnection();
			
			String line = null;
			
			while ((line=br.readLine())!=null) {
				int val = Integer.parseInt(line);
				conn.queryServer(val, delim, bw);
			}
			
			br.close();
			
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
}
