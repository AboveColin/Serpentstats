package dev.cdevries.StreamElements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.*;


public class ObtainSubs {

	public static void main(String[] args) throws IOException {
	System.out.println(getSubscriberAmount());
	}

    public static int getSubscriberAmount() throws IOException {
        
        URL url = new URL("https://api.streamelements.com/kappa/v2/sessions/");
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("GET");

		httpConn.setRequestProperty("authorization", "Bearer ");
		

		InputStream responseStream = httpConn.getResponseCode() / 100 == 2
				? httpConn.getInputStream()
				: httpConn.getErrorStream();
		try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
			String response = s.hasNext() ? s.next() : "";
			s.close();
			String jsonString = response; 
			JSONObject obj = new JSONObject(jsonString);
			int subCount = obj.getJSONObject("data").getJSONObject("subscriber-total").getInt("count");
			return subCount;
		} catch (JSONException e) {
			System.out.println("[getSubscriberAmount] JSONException: " + e.getMessage());
			return 0;
		}  
    }
}
