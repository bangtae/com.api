package com.api.naver.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.activation.MimetypesFileTypeMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
 
 
public class XmlRpcNaverBlog_backup {
    static final String API_URL = "https://api.blog.naver.com/xmlrpc";
    static final String API_ID = "";
    static final String API_PASSWORD = "";
    
    public static void main(String[] args) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));
            
        	XmlRpcClient xmlrpcClient = new XmlRpcClient();
			XmlRpcClientConfigImpl xmlrpcClientImpl = new XmlRpcClientConfigImpl();
			xmlrpcClientImpl.setServerURL(new java.net.URL(API_URL));
			xmlrpcClientImpl.setBasicEncoding("UTF-8");
			xmlrpcClientImpl.setEncoding("UTF-8");
            xmlrpcClient.setConfig(xmlrpcClientImpl);
            String imgPath = uploadImg(xmlrpcClient);
            System.out.println("imgPath = " + imgPath);
            
            Map<String, String> contents = new HashMap<String, String>();
            contents.put("categories", "상품소개"); // 카테고리 텍스트
            contents.put("title", "2222\r\n"); // 제목
            contents.put("description", "<div style=\"clear: both; text-align: center;\">\r\n" + 
            		"<!-- 이미지 -->\r\n" + 
            		"\r\n" + 
            		"<br />\r\n" + 
            		"<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"tr-caption-container\" style=\"margin-left: auto; margin-right: auto; text-align: center;\"><tbody>\r\n" + 
            		"<tr><td style=\"text-align: center;\"><a href=\"https://static.coupangcdn.com/image/retail/images/252500669156049-d1885ae2-75e9-452a-98d7-a6b7d85dbecd.jpg\" imageanchor=\"1\" style=\"margin-left: auto; margin-right: auto;\"><img alt=\"상품명\" border=\"0\" data-original-height=\"662\" data-original-width=\"662\" height=\"640\" src=\"https://static.coupangcdn.com/image/retail/images/252500669156049-d1885ae2-75e9-452a-98d7-a6b7d85dbecd.jpg\" title=\"상품명\" width=\"640\" /></a></td></tr>\r\n" + 
            		"<tr><td class=\"tr-caption\" style=\"text-align: center;\">상품명</td></tr>\r\n" + 
            		"</tbody></table>\r\n" + 
            		"<br />후기<br />\r\n" + 
            		"<!-- 가격 -->\r\n" + 
            		"판매가: 999원<br />\r\n" + 
            		"<!-- 상품요약설명 -->\r\n" + 
            		"해당상품은 로켓배송이 가능한 상품이며<br />\r\n" + 
            		"자세한 상품설명 및 후기는 <a href=\"http:/###\" target=\"_blank\">클릭</a> 부탁드리<br />\r\n" + 
            		"겠습니다.<br />\r\n" + 
            		"<!-- 배너 -->\r\n" + 
            		"<a href=\"https://link.coupang.com/re/AFFSDP?lptag=AF1937507&amp;pageKey=201842251&amp;itemId=589347517&amp;vendorItemId=4543341750&amp;traceid=V0-153-e7147470e4fb215d\" target=\"_blank\"><img alt=\"리훈 오늘쓰임 1년용 가계부 스프링, 블루베리요거트, 1개\" height=\"200\" src=\"https://static.coupangcdn.com/image/retail/images/252500669156049-d1885ae2-75e9-452a-98d7-a6b7d85dbecd.jpg\" width=\"200\" /></a><br />\r\n" + 
            		"쇼핑하기<br />\r\n" + 
            		"<!-- 해시태그 -->\r\n" + 
            		"해시태그<br />\r\n" + 
            		"<!-- 포스팅 설명 -->\r\n" + 
            		"포스팅날짜는: 2019-99-99 이며<br />\r\n" + 
            		"포스팅 시기에 따라 판매가와 재고의 변동이 있을 수 있습니다.<br />\r\n" + 
            		"본 블로그는 해당 상품의 주체가 아니며 상품의 주문, 반품 등의 의무와 책임은<br />\r\n" + 
            		"각 판매자에게 있으니 유의 부탁드리겠습니다.<br />\r\n" + 
            		"<br />\r\n" + 
            		"<br />\r\n" + 
            		"<br />\r\n" + 
            		"\"이글은 파트너스 활동을 통해 일정액의 수수료를 제공받을 수 있습니다.</div>\r\n" + 
            		""); // 내용
            contents.put("tags", "뻬그뻬레고, 다기능, 씨에스타, 유아식탁의자"); // 태크 콤마로 구분한다.
 
            
            List<Object> params = new ArrayList<Object>();
            
            // 블로그ID를 넣으라는데 공백으로 해도 된다.
            params.add(API_ID);  
            
            // API ID
            params.add(API_ID);
            
            // API 암호
            params.add(API_PASSWORD); 
            
            // 블로그 컨텐츠 
            params.add(contents);
            
            // 공개여부 true이면 공개, false면 비공개
            params.add(new Boolean(true)); 
            
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            
            String rsString = (String) client.execute("metaWeblog.newPost", params);
            
            System.out.println(rsString);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
	public static String uploadImg(XmlRpcClient xmlrpcClient) throws Exception {
		File file = new File("C:\\Users\\bt1\\Downloads\\4f242431-645f-41d1-a5fe-35517f828be6.JPG");
		Vector<Object> params = new Vector<Object>();
		// 계정, 인증 파라미터
		params.addElement(new String(API_ID));
		// 네이버 아이디
		params.addElement(new String(API_ID));
		// 네이버 아이디
		params.addElement(new String(API_PASSWORD));
		// 발급받은 API 키
		Map<String, Object> fileMap = new HashMap<String, Object>();
		fileMap.put("name", file.getName());
		fileMap.put("type", new MimetypesFileTypeMap().getContentType(file));
		fileMap.put("bits", getFileByte(file));
		params.addElement(fileMap);
		Map<String, Object> res = (HashMap<String, Object>) xmlrpcClient.execute("metaWeblog.newMediaObject", params);
		return (String) res.get("url");
	}
	
	public static byte[] getFileByte(File file) throws IOException {
		int total = 0; 
		int length = (int) file.length(); 
		byte[] ret = new byte[length]; 
		FileInputStream reader = new FileInputStream( file ); 
		while ( total < length ) { 
			int read = reader.read( ret ); 
			if ( read < 0 ) 
				throw new IOException( "fail read file: " + file ); total += read; } 
		return ret; 
	}
}