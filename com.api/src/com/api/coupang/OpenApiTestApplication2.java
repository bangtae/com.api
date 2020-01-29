package com.api.coupang;


import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.sql.*;
import java.util.Calendar;

/**
 * Insert Product Data is Coupang API to Local MySQL DB
 *
 */
public final class OpenApiTestApplication2 {
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "https://api-gateway.coupang.com";
    private final static String URL = "/v2/providers/affiliate_open_api/apis/openapi/products/search?limit=100&keyword=";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final static String ACCESS_KEY = "";
    private final static String SECRET_KEY = "";

    public static void main(String[] args) throws IOException {
    	//------------------------ Product Data is Coupang API -> List, Map ------------------------
    	String keyword = "할인";
    	String convertKeyword = URLEncoder.encode(keyword, "utf-8");
    	System.out.println("convertKeyword = " + convertKeyword);

    	// Generate HMAC string
        String authorization = HmacGenerator.generate(REQUEST_METHOD, URL + convertKeyword, SECRET_KEY, ACCESS_KEY);

        // Send request
        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(URL + convertKeyword)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);
        String responseJson = EntityUtils.toString(httpResponse.getEntity());
        
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
	        
	      //------------------------ Product Data is Coupang APL(List, Map) -> Local MySQL DB ------------------------
	        Connection conn = null;
	        PreparedStatement preparedStmt = null;
	        try{
	          // create a mysql database connection
	          String myDriver = "com.mysql.jdbc.Driver";
	          String myUrl = "jdbc:mysql://localhost:3306/db_api";
	          Class.forName(myDriver);
	          conn = DriverManager.getConnection(myUrl, "root", "root");
	          conn.setAutoCommit(false);
	        
	          // create a sql date object so we can use it in our INSERT statement
	          Calendar calendar = Calendar.getInstance();
	          java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

	          // the mysql insert statement
	          String query = "INSERT INTO `db_api`.`table_coupang_prdct` (`PRDCT_IMAGE`, `PRDCT_ID`, `RANK`, `PRDCT_URL`, `KEYWORD`, `PRDCT_NM`, `PRDCT_PRIC`, `IS_RCKT`, `INSRT_DATE`) VALUES "
	          		+ " (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	          // create the mysql insert preparedstatement
	          preparedStmt = conn.prepareStatement(query);
	          
	          //list.size()-1 is except for <landingUrl>
	          int i=0;
	          for (; i < list.size()-1; i++) {
	        	  Map<String, String> productInfo = list.get(i);
	        	  String productId = productInfo.get("productId");
	        	  Statement stmt = conn.createStatement();
	        	  ResultSet rs = stmt.executeQuery("select COUNT(1) AS isExist from table_coupang_prdct where PRDCT_ID = '" + productId + "'");
	        	  while (rs.next()) {
	        		  String isExist = rs.getString(1);
	        		  if("0".equals(isExist)) {
				          preparedStmt.setString (1, productInfo.get("productImage"));
				          preparedStmt.setString (2, productId);
				          preparedStmt.setString (3, productInfo.get("rank"));
				          preparedStmt.setString (4, productInfo.get("productUrl"));
				          preparedStmt.setString (5, productInfo.get("keyword"));
				          preparedStmt.setString (6, productInfo.get("productName"));
				          preparedStmt.setString (7, productInfo.get("productPrice"));
				          preparedStmt.setString (8, productInfo.get("isRocket"));
				          preparedStmt.setString (9, String.valueOf(startDate));
				          
				          // execute the preparedstatement
				          preparedStmt.execute();
				          
			            query = "INSERT INTO `db_api`.`table_coupang_prdct_link` (`PRDCT_ID`) VALUES (?)";
			            PreparedStatement pStmt = conn.prepareStatement(query);
			            pStmt.setString (1, productId);
			            pStmt.execute();
			            
			            try{
			                if(pStmt != null)pStmt.close();
			            } catch (SQLException e) {e.printStackTrace();}
			            
	        		  }//if
	        	  }//while
	        	  if(rs != null)rs.close();
	        	  if(stmt != null)stmt.close();
	          }
	          
	          System.out.println("Insert success!! count = " + i);
	          
	          conn.commit();
	        }catch (Exception e){
	          System.err.println("Got an exception!");
	          System.err.println(e.getMessage());
	          
	          conn.rollback();
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
            
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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