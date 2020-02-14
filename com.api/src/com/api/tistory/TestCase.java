package com.api.tistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class TestCase {
	public static void main(String[] args) {
//		URL url;
//		try {
//			url = new URL("");
//			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
//			con.setRequestMethod("GET"); 
//			// optional default is GET 
//			con.setRequestProperty("User-Agent", "Mozilla/5.0"); 
//			// add request header 
//			int responseCode = con.getResponseCode(); 
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
//			String inputLine; StringBuffer response = new StringBuffer(); 
//			while ((inputLine = in.readLine()) != null) { 
//				response.append(inputLine); } in.close(); 
//				// print result 
//				System.out.println("HTTP Code : " + responseCode); 
////				System.out.println("HTTP body : " + response.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
		
		//최대값 최소값을 구할 int배열
		int array[] = {4,3,2,1,10,8,7,6,9,5};
		int max = array[0]; //최대값
		int min = array[0]; //최소값
				
		Arrays.sort(array); // 배열 정렬

		// 최소값(Min) 출력
		 System.out.println("최소값은 : "+array[0]);

		// 최대값(Max) 출력
		System.out.println("최대값은 : " +array[array.length - 1]);
	}
}
