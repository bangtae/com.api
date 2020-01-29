package com.api.coupang;

import java.util.Calendar;

public class TestCase {
	public static void main(String[] args) {
		String xmlStr = "<data><productData><productImage>https://static.coupangcdn.com/image/vendor_inventory/a096/7ad5341914b82cc0cc1efdd1b3918fcd6b2c96f9d712b2454a393b364487.png</productImage><productId>216089170</productId><rank>1</rank><productUrl>https://link.coupang.com/re/AFFSDP?lptag=AF1937507&amp;pageKey=216089170&amp;itemId=663062252&amp;vendorItemId=4715718561&amp;traceid=V0-153-92131e3937a9f080</productUrl><keyword>오늘의특가</keyword><productName>[오늘의특가] 금바우 누룽지 200g, 20개입</productName><productPrice>29900</productPrice><isRocket>false</isRocket></productData><productData><productImage>https://static.coupangcdn.com/image/retail/images/252500669156049-d1885ae2-75e9-452a-98d7-a6b7d85dbecd.jpg</productImage><productId>201842251</productId><rank>2</rank><productUrl>https://link.coupang.com/re/AFFSDP?lptag=AF1937507&amp;pageKey=201842251&amp;itemId=589347517&amp;vendorItemId=4543341750&amp;traceid=V0-153-e7147470e4fb215d</productUrl><keyword>오늘의특가</keyword><productName>리훈 오늘쓰임 1년용 가계부 스프링, 블루베리요거트, 1개</productName><productPrice>11140</productPrice><isRocket>true</isRocket></productData><productData><productImage>https://static.coupangcdn.com/image/vendor_inventory/7bf3/9bc6bf2ccf840b449a9ed0dabad24a8df5777cf91ad84df3222376513396.jpg</productImage><productId>323619835</productId><rank>3</rank><productUrl>https://link.coupang.com/re/AFFSDP?lptag=AF1937507&amp;pageKey=323619835&amp;itemId=1036168174&amp;vendorItemId=5489582847&amp;traceid=V0-153-cdb08c8747632cbc</productUrl><keyword>오늘의특가</keyword><productName>오늘도입다 오버핏 브이넥 크롭 꽈배기 니트</productName><productPrice>25500</productPrice><isRocket>false</isRocket></productData><landingUrl>https://link.coupang.com/re/AFFSRP?lptag=AF1937507&amp;pageKey=%EC%98%A4%EB%8A%98%EC%9D%98%ED%8A%B9%EA%B0%80&amp;traceid=V0-163-9afd96be8c6da361</landingUrl></data>";
		System.out.println(xmlStr.indexOf("<landingUrl>"));
		System.out.println(xmlStr.lastIndexOf("</landingUrl>"));
		String convStr = xmlStr.replaceAll(xmlStr.substring(1622, 1778), "");
//		String convStr = xmlStr.replaceAll(xmlStr.substring(xmlStr.indexOf("<landingUrl>"), xmlStr.lastIndexOf("</landingUrl>")+13), "");
		System.out.println(convStr);
		
//        Calendar calendar = Calendar.getInstance();
//        java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
//        System.out.println(String.valueOf(startDate));
	}
}
