package com.api.tistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestCase {
	public static void main(String[] args) {
		URL url;
		try {
			url = new URL("");
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
			con.setRequestMethod("GET"); 
			// optional default is GET 
			con.setRequestProperty("User-Agent", "Mozilla/5.0"); 
			// add request header 
			int responseCode = con.getResponseCode(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			String inputLine; StringBuffer response = new StringBuffer(); 
			while ((inputLine = in.readLine()) != null) { 
				response.append(inputLine); } in.close(); 
				// print result 
				System.out.println("HTTP Code : " + responseCode); 
//				System.out.println("HTTP body : " + response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
