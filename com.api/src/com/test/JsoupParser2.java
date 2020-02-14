package com.test;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupParser2 {
	public static void main(String[] args) {
//		String url = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=비크래프트 프로폴리스 무설탕 기침 목사탕 키즈용 30정, 1개, 45g";
//		Document doc = null;
//		StringBuilder reviewSB = new StringBuilder();
//		
//		try {
//			doc = Jsoup.connect(url).get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		Elements element = doc.select("ul.type01");
//		
//		String review = "";
//		String review_url = "";
//		for(Element el1 : element.select("dd.sh_cafe_passage")) {
////			System.out.println(el1.text());
//			review = review + "!@#" + el1.text();
//		}
//		
//		for(Element el : element.select("dd.txt_block")) {
//			if(el.html().indexOf("https://cafe.naver.com/") != -1) {
////				System.out.println(el.getElementsByClass("url"));
//				review_url = review_url + "!@#" + el.getElementsByClass("url");
//			}
//		}
//		
//		String[] reviewArray = review.split("\\!@#");
//		String[] reviewUrlArray = review_url.split("\\!@#");
////		for (int i = 0; i < reviewArray.length; i++) {
//			reviewSB.append(reviewArray[1] + reviewUrlArray[1] + "<br />");
////		}
//		
//		System.out.println(reviewSB.toString());
		
//		String url = "https://m.search.naver.com/search.naver?sm=mtp_hty.top&where=m&query=설날+차례음식+만능";
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
////			reviewSB.append(el.getElementsByClass("api_txt_lines dsc_txt").text() + el.getElementsByClass("elss web_url"));
//			reviewSB.append(el.getElementsByClass("api_txt_lines dsc_txt").text() + "<br />");
//		}
//
//		System.out.println(reviewSB.toString());
		
		String url = "https://search.daum.net/search?w=blog&m=board&collName=blog_total&q=잉글레시나 퀴드 휴대용 유모차&spacing=0&DA=BR1";
		Document doc = null;
		StringBuilder reviewSB = new StringBuilder();
		
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements element = doc.select("div.g_comp");
		for(Element el : element.select("div.type_fulltext")) {
			for(Element elLi : el.select("li.fst")) {	
				reviewSB.append(elLi.getElementsByClass("f_eb desc").text());
				break;
			}
		}

		System.out.println(reviewSB.toString());
		
	}
}
