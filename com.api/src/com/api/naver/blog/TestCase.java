package com.api.naver.blog;

import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class TestCase {
	public static void main(String[] args) {
//		String urlImg = "https://static.coupangcdn.com/image/retail/images/252500669156049-d1885ae2-75e9-452a-98d7-a6b7d85dbecd.jpg";
//		String[] strArray = urlImg.split("\\/");
//		System.out.println(strArray[strArray.length-1]);
//		
//		String[] imgNm = strArray[strArray.length-1].split("\\.");
//		System.out.println(imgNm[0] + " / " + imgNm[1]);
		
//		String str = "[오늘의특가] 금바우 누룽지 200g, 20개입";
//		String tag = "오늘의특가";
//		String[] prdctNmArray = str.replaceAll(",", "").split("\\ ");
//		for (int i = 0; i < prdctNmArray.length; i++) {
//			if(prdctNmArray[i].indexOf("오늘의특가") == -1) {
//				tag = tag + ", " + prdctNmArray[i];
//			}
//		}
//		System.out.println( tag.replace("[", "").replace("]", "") );
		
		String rendValue = String.valueOf(ThreadLocalRandom.current().nextInt(3, 6 + 1)) + "000";
		System.out.println(rendValue);
		
	}
}
