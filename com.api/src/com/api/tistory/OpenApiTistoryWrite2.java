package com.api.tistory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.api.coupang.HmacGenerator;

/**
 * Product data is Goupang to Tistory
 * Get access token: https://www.tistory.com/oauth/authorize?client_id=becd312217316e7482d8d8ed290d3300&redirect_uri=https://best-reviews.tistory.com&response_type=token
 */
public class OpenApiTistoryWrite2 {
	private final static String ACCESS_TOKEN = "";
	
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "https://api-gateway.coupang.com";
    private final static String URL = "/v2/providers/affiliate_open_api/apis/openapi/products/search?limit=100&keyword=";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final static String ACCESS_KEY = "";
    private final static String SECRET_KEY = "";
	
	public static void main(String[] args) {
		DecimalFormat formatter = new DecimalFormat("###,###");
		
        // create a sql date object so we can use it in our INSERT statement
        Calendar calendar = Calendar.getInstance();
        java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
		
        System.out.println("Local DB Running..");
        Connection conn = null;
        PreparedStatement preparedStmt = null;
        
        try{
	      //------------------------ Get Keyword in Local DB ------------------------
          // create a mysql database connection
          String myDriver = "com.mysql.jdbc.Driver";
          String myUrl = "jdbc:mysql://localhost:3306/db_api";
          Class.forName(myDriver);
          conn = DriverManager.getConnection(myUrl, "root", "root");
          conn.setAutoCommit(false);
          
          String query = "";
      	  Statement stmt = conn.createStatement();
      	  ResultSet rs = stmt.executeQuery("select KEYWORD from table_tistory_prdct where STATUS_MSG is null OR STATUS_MSG = '' LIMIT 10");
      	  
      	  while (rs.next()) {
		
	    	//------------------------ Product Data is Coupang API -> List, Map ------------------------
	    	String keyword = rs.getString(1);
	    	
	    	String convertKeyword = "";
			try {
				convertKeyword = URLEncoder.encode(keyword, "utf-8");
			} catch (UnsupportedEncodingException e2) {
				System.out.println(e2.getMessage());
			}
	    	System.out.println("convertKeyword = " + convertKeyword);
	
	    	// Generate HMAC string
	        String authorization = HmacGenerator.generate(REQUEST_METHOD, URL + convertKeyword, SECRET_KEY, ACCESS_KEY);
	
	        // Send request
	        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
	        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
	                .get(URL + convertKeyword)
	                .addHeader("Authorization", authorization)
	                .build();
	
	        org.apache.http.HttpResponse httpResponse;
	        String responseJson = "";
			try {
				httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);
				responseJson = EntityUtils.toString(httpResponse.getEntity());
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
	        
	        // verify
	        System.out.println("responseJson = " + responseJson);
	        
	        List<Map> list = new ArrayList<Map>();
	        
	        try {
	        	//String(Json) to XML
	        	JSONObject stringToJson = new JSONObject(responseJson);
				String jsonToXml = XML.toString(stringToJson);
				System.out.println("jsonToXml = " + jsonToXml);
				String convJsonToXml = jsonToXml.replaceAll(jsonToXml.substring(jsonToXml.indexOf("<rCode>"), jsonToXml.lastIndexOf("</rMessage>")+11), "");
				System.out.println("convJsonToXml = " + convJsonToXml);
				
				//Add Document to List<Map>
			    Document doc = convertStringToDocument(convJsonToXml);
			    Node root = doc.getFirstChild();
		        NodeList nodes = root.getChildNodes();
		        Node node;
		        Map<String, String> values = null;
		        for (int i = 0; i < nodes.getLength(); i++) {
		        	node = nodes.item(i);
		        	NodeList nodes1 = node.getChildNodes();
		        	Node node1;
		        	values = new HashMap<String, String>();
		        	for (int j = 0; j < nodes1.getLength(); j++) {
		        		node1 = nodes1.item(j);
	//	        		System.out.println(node1.getNodeName());
	//		            System.out.println(node1.getTextContent());
			            values.put(node1.getNodeName(), node1.getTextContent());
		        	}
		            list.add(values);
		        }
		        
		      //------------------------ Product Data is Coupang APL(List, Map) -> TISTORY ------------------------
	        	  System.out.println("Data Running..");
	        	  String convTag = "";
	        	  StringBuilder content = new StringBuilder();
	        	  content.append("<p style=\"text-align: center;\"><b>쇼핑몰 랭킹순으로 상품을 간략하게 보여드리며&nbsp;</b></p>\r\n" + 
		        	  		"<p style=\"text-align: center;\"><b>상품링크를 클릭하시면 자세한 정보나 후기를 보실수</b></p>\r\n" + 
		        	  		"<p style=\"text-align: center;\"><b>있습니다.</b></p>\r\n" + 
		        	  		"<p style=\"text-align: center;\"><b>현명한 선택을 하셔서 좋은 제품 구입하시길 바라겠습니다.</b></p>\r\n" + 
		        	  		"<p style=\"text-align: center;\">&nbsp;</p>");
	        	  
		  		  int i=0;
		  		  int productCnt = list.size();
		  		  String header = "<h3 style=\"text-align: center;\" data-ke-size=\"size23\"><b>" +keyword + " TOP "+ (productCnt-1) +"</b></h3>\r\n";
		          for (; i < productCnt-1; i++) {
		        	  Map<String, String> productInfo = list.get(i);
		        	  System.out.println("Product Name = " + productInfo.get("productName"));
		        	  
	//	        	  System.out.println("Product Price Processing..");
		        	  String productPrice = formatter.format(Integer.parseInt(productInfo.get("productPrice")));
		        	  
	//	        	  System.out.println("IsRocket Processing..");
		        	  String isRckt = "";
						if("true".equals(productInfo.get("isRocket"))){isRckt = "가능";}else {isRckt = "불가능";}
		        	  
	//				  System.out.println("HTML Processing..");
		        	  content.append("<blockquote style=\"text-align: justify;\" data-ke-style=\"style2\"><b><b><span style=\"color: #333333;\"><span style=\"color: #333333;\"><span style=\"color: #006dd7;\">"+productInfo.get("rank")+"</span> "+productInfo.get("productName")+"(로켓배송: "+isRckt+")</span></span></b></b></blockquote>\r\n" + 
		        	  		"<p><img src=\""+productInfo.get("productImage")+"\" alt=\""+productInfo.get("productName")+"\" width=\"400\" height=\"400\" /></p>\r\n" + 
		        	  		"<p style=\"text-align: justify;\"><a href=\""+productInfo.get("productUrl")+"\" target=\"_blank\" rel=\"noopener\">상세정보 및 후기보기</a></p>" + 
		        	  		"<p style=\"text-align: justify;\">"+productPrice+"원</p>");
		        	  
	//	        	  System.out.println("TAG Processing..");
						String str = productInfo.get("productName");
						String[] prdctNmArray = str.replaceAll(",", "").split("\\ ");
						
						String tag = "";
						for (int j = 0; j < prdctNmArray.length; j++) {
							tag = tag + ", " + prdctNmArray[j];
						}
						convTag = convTag + tag.replace("[", "").replace("]", "");
	//					System.out.println("convTag = " + convTag);
		        		  
		          }
		          
		          content.append("<p style=\\\"text-align: center;\\\">&nbsp;</p>"
		          		+ "<p style=\"text-align: justify;\">포스팅날짜는: "+String.valueOf(startDate)+" 이며<br />포스팅 시기에 따라 판매가와 재고의 변동이 있을 수 있습니다.<br />"
		          		+ "본 블로그는 해당 상품의 주체가 아니며 상품의 주문, 반품 등의 의무와 책임은 각 판매자에게 있으니 유의 부탁드리겠습니다.<br /><br /><br /><br />"
		          		+ "<span style=\"color: #666666;\">\"이글은 파트너스 활동을 통해 일정액의 수수료를 제공받을 수 있습니다.</span></p>");
		          
		          System.out.println("Tistory invoke..");
		          	//Posting Tistory
		          	HttpClient httpclient = HttpClients.createDefault();
		          	HttpPost httppost = new HttpPost("https://www.tistory.com/apis/post/write");
				
			  		// Request parameters and other properties.
			  		List<NameValuePair> params = new ArrayList<NameValuePair>(12);
			  		params.add(new BasicNameValuePair("access_token", ACCESS_TOKEN));
			  		params.add(new BasicNameValuePair("output", ""));
			  		params.add(new BasicNameValuePair("blogName", "best-reviews"));
			  		params.add(new BasicNameValuePair("title", keyword + " TOP " + (productCnt-1)));
			  		params.add(new BasicNameValuePair("content", header+content.toString()));
			  		params.add(new BasicNameValuePair("visibility", "3"));
			  		params.add(new BasicNameValuePair("category", "872741"));
			  		params.add(new BasicNameValuePair("published", ""));
			  		params.add(new BasicNameValuePair("slogan", ""));
			  		params.add(new BasicNameValuePair("tag", keyword+convTag));
			  		params.add(new BasicNameValuePair("acceptComment", ""));
			  		params.add(new BasicNameValuePair("password", ""));
		  		
		  		try {
		  			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		  		} catch (UnsupportedEncodingException e) {
		  			System.out.println(e.getMessage());
		  		}
	
		  		// Execute and get the response.
		  		HttpResponse response;
		  		String theString = "";
		  		try {
		  			response = httpclient.execute(httppost);
		  			HttpEntity entity = response.getEntity();
		  			
		            String rendValue = String.valueOf(ThreadLocalRandom.current().nextInt(1, 2 + 1)) + "000000";
					try {
						Thread.sleep(Integer.parseInt(rendValue)); // 1000 = 1second waiting
					} catch (InterruptedException e) {throw new Exception(e.getMessage());}
	
		  			if (entity != null) {
		  				try (InputStream instream = entity.getContent()) {
		  					theString = IOUtils.toString(instream, "UTF-8"); 
		  					System.out.println("theString = " + theString);
		  				}
		  			}
		  		} catch (IOException e) {
		  			System.out.println(e.getMessage());
		  		}
		          
		          System.out.println("Insert success!! count = " + i);
		          
		          // the mysql update statement
		          query = "UPDATE `db_api`.`table_tistory_prdct` SET  STATUS_MSG = ? WHERE KEYWORD = ?";
		          preparedStmt = conn.prepareStatement(query);
		          preparedStmt.setString (1, theString);
		          preparedStmt.setString (2, keyword);
		          
		        }catch (Exception e){
		          System.err.println(e.getMessage());
		          
		          conn.rollback();
		          // the mysql update statement
		          query = "UPDATE `db_api`.`table_tistory_prdct` SET  STATUS_MSG = ? WHERE KEYWORD = ?";
		          preparedStmt = conn.prepareStatement(query);
		          preparedStmt.setString (1, "E");
		          preparedStmt.setString (2, keyword);
		        }
	        
	          preparedStmt.execute();
	          conn.commit();
	        
	      	  }//while
      	  
      	  if(rs != null)rs.close();
      	  if(stmt != null)stmt.close();
            
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
            try{
                if(preparedStmt != null)preparedStmt.close();
                System.out.println("preparedStmt closed !!");
            } catch (SQLException e) {e.printStackTrace();}
            try{
                if(conn != null)conn.close();
                System.out.println("Connection closed !!");
            } catch (SQLException e) {e.printStackTrace();
            }
        }
		
		//<tistory><status>400</status><error_message>access_token 이 유효하지 않습니다. []</error_message></tistory>
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpPost uploadFile = new HttpPost("https://www.tistory.com/apis/post/attach?");
//		
//		List<NameValuePair> imgParams = new ArrayList<NameValuePair>(2);
//		imgParams.add(new BasicNameValuePair("access_token", ACCESS_TOKEN));
//		imgParams.add(new BasicNameValuePair("blogName", "best-reviews"));
//
//		try {
//			uploadFile.setEntity(new UrlEncodedFormEntity(imgParams, "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//		builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);
//
//		// This attaches the file to the POST:
//		File f = new File("C:\\Users\\bt1\\Downloads\\1579185609225.jpg");
//		try {
//			builder.addBinaryBody(
//			    "file",
//			    new FileInputStream(f),
//			    ContentType.APPLICATION_OCTET_STREAM,
//			    f.getName()
//			);
//		} catch (FileNotFoundException e2) {
//			e2.printStackTrace();
//		}
//		
//		HttpEntity multipart = builder.build();
//		uploadFile.setEntity(multipart);
//		CloseableHttpResponse imgResponse;
//		try {
//			imgResponse = httpClient.execute(uploadFile);
//			HttpEntity responseEntity = imgResponse.getEntity();
//			if (responseEntity != null) {
//				try (InputStream instream = responseEntity.getContent()) {
//					String theString = IOUtils.toString(instream, "UTF-8"); 
//					System.out.println("file path = " + theString);
//				}
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
	}
	
	/**
	 * Conversion XML to DOM(Document) 
	 */
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
        	System.out.println("xmlStr = " + xmlStr);
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
