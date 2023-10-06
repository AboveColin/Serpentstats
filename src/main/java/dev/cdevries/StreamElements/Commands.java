package dev.cdevries.StreamElements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import dev.cdevries.TwitchApi;

public class Commands {

	public static void main(String[] args) throws IOException {
		ToggleHoelaat();
	}

    public static void ToggleHoelaat() throws IOException {
        String currentReply = getCurrentReply();
        Boolean newState = !getCurrentState();
		URL url = new URL("https://api.streamelements.com/kappa/v2/bot/commands/");
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("PUT");

		httpConn.setRequestProperty("authority", "api.streamelements.com");
		httpConn.setRequestProperty("sec-ch-ua", "\"");
		httpConn.setRequestProperty("accept", "application/json, text/plain, */*");
		httpConn.setRequestProperty("content-type", "application/json;charset=UTF-8");
		httpConn.setRequestProperty("authorization", "Bearer");
		httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
		httpConn.setRequestProperty("user-agent", "a");
		httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
		httpConn.setRequestProperty("origin", "https://streamelements.com");
		httpConn.setRequestProperty("sec-fetch-site", "same-site");
		httpConn.setRequestProperty("sec-fetch-mode", "cors");
		httpConn.setRequestProperty("sec-fetch-dest", "empty");
		httpConn.setRequestProperty("referer", "https://streamelements.com/");
		httpConn.setRequestProperty("accept-language", "");

		httpConn.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
		writer.write("{\"enabled\":" + newState + ",\"reply\":\"" + currentReply + "\"}");
		writer.flush();
		writer.close();
		httpConn.getOutputStream().close();

		InputStream responseStream = httpConn.getResponseCode() / 100 == 2
				? httpConn.getInputStream()
				: httpConn.getErrorStream();
		Scanner s = new Scanner(responseStream).useDelimiter("\\A");
		String response = s.hasNext() ? s.next() : "";
		System.out.println(response);
	}
    

    public static boolean getCurrentState() throws IOException {
        URL url = new URL("https://api.streamelements.com/kappa/v2/bot/commands/");
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("GET");

		InputStream responseStream = httpConn.getResponseCode() / 100 == 2
				? httpConn.getInputStream()
				: httpConn.getErrorStream();
		Scanner s = new Scanner(responseStream).useDelimiter("\\A");
		String response = s.hasNext() ? s.next() : "";
        s.close();
        
        // JSON parse response, get enabled
        String jsonString = response; 
		JSONObject obj = new JSONObject(jsonString);
		boolean state = obj.getBoolean("enabled");
        System.out.println(state);
        return state;
    }

    public static String getCurrentReply() throws IOException {
        URL url = new URL("https://api.streamelements.com/kappa/v2/bot/commands/");
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("GET");

		InputStream responseStream = httpConn.getResponseCode() / 100 == 2
				? httpConn.getInputStream()
				: httpConn.getErrorStream();
		try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
            String response = s.hasNext() ? s.next() : "";
            s.close();
            String jsonString = response; 
            JSONObject obj = new JSONObject(jsonString);
            String msg = obj.getString("reply");
            System.out.println(msg);
            return msg;
        } catch (JSONException e) {
            System.out.println("JSONException: " + e.getMessage());
            return "serpentSleeper Rick is om gaan slapen. serpentSleeper";
        }
    }
}