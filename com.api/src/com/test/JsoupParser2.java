package com.test;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupParser2 {
	public static void main(String[] args) {
		String url = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=+%ED%95%98%ED%8F%AC%EC%8A%A4%EC%B9%A8%EB%8C%80+%ED%8A%B9%EB%B3%84%ED%95%A0%EC%9D%B8+%ED%8F%89%EC%83%81%ED%98%95%EC%B9%A8%EB%8C%80";
		Document doc = null;
		StringBuilder reviewSB = new StringBuilder();
		
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements element = doc.select("ul.type01");
		
		String review = "";
		String review_url = "";
		for(Element el1 : element.select("dd.sh_cafe_passage")) {
//			System.out.println(el1.text());
			review = review + "!@#" + el1.text();
		}
		
		for(Element el : element.select("dd.txt_block")) {
			if(el.html().indexOf("https://cafe.naver.com/") != -1) {
//				System.out.println(el.getElementsByClass("url"));
				review_url = review_url + "!@#" + el.getElementsByClass("url");
			}
		}
		
		String[] reviewArray = review.split("\\!@#");
		String[] reviewUrlArray = review_url.split("\\!@#");
		for (int i = 0; i < reviewArray.length; i++) {
			reviewSB.append(reviewArray[i] + reviewUrlArray[i] + "<br />");
			reviewSB.append("<br />");
		}
		
		System.out.println(reviewSB.toString());
		
//		String url = "https://m.search.naver.com/search.naver?sm=mtp_hty.top&where=m&query=%EB%B0%94%EC%9D%B4%EC%98%A4%ED%94%8C%EB%A0%88+%EC%9A%94%EA%B5%AC%EB%A5%B4%ED%8A%B8+%EC%82%AC%EA%B3%BC%2C+140ml%2C+8%EA%B0%9C%EC%9E%85";
//		Document doc = null;
//		StringBuilder reviewSB = new StringBuilder();
//		
//		try {
//			doc = Jsoup.connect(url).get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		Elements element = doc.select("ul.lst_total");
//		
//		for(Element el : element.select("li")) {
////			System.out.println(el.getElementsByClass("api_txt_lines dsc_txt").text());
////			System.out.println(el.getElementsByClass("elss web_url"));
//			reviewSB.append(el.getElementsByClass("api_txt_lines dsc_txt").text() + el.getElementsByClass("elss web_url"));
//			break;
//		}
//
//		System.out.println(reviewSB.toString());
		
	}
}
