package com.api.naver.blog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
 
public class XmlRpcNaverBlog {
    static final String API_URL = "https://api.blog.naver.com/xmlrpc";
    static final String API_ID = "";
    static final String API_PASSWORD = "";
    
    public static void main(String[] args) {
    	//------------------------ Product Data is Local DB -> BLOG Posting ------------------------    	
    	Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	PreparedStatement preparedStmt = null;
    	
    	DecimalFormat formatter = new DecimalFormat("###,###");
    	
		String productImage = "";
		String productId = "";
		String rank = "";
		String productUrl = "";
		String keyword = "";
		String productName = "";
		String productPrice = "";
		String productIsRocket = "";
		String blogDate = "";
    	
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));
            
        	XmlRpcClient xmlrpcClient = new XmlRpcClient();
			XmlRpcClientConfigImpl xmlrpcClientImpl = new XmlRpcClientConfigImpl();
			xmlrpcClientImpl.setServerURL(new java.net.URL(API_URL));
			xmlrpcClientImpl.setBasicEncoding("UTF-8");
			xmlrpcClientImpl.setEncoding("UTF-8");
            xmlrpcClient.setConfig(xmlrpcClientImpl);
            
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_api", "root", "root");
			con.setAutoCommit(false);
			// here sonoo is database name, root is username and password
			stmt = con.createStatement();
			rs = stmt.executeQuery("select PRDCT_IMAGE, PRDCT_ID, PRDCT_URL, KEYWORD, 'RANK', PRDCT_NM, ISSUE_CODE, PRDCT_PRIC, IS_RCKT, INSRT_DATE, BLOG_DATE "
					+ "from table_coupang_prdct where ISSUE_CODE = 'R'");
			
	         Calendar calendar = Calendar.getInstance();
	         java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
			
			Map<String, String> contents = null;
			int blog_posting_count = 1;
			
			while (rs.next()) {
			    productImage = rs.getString(1);
				productId = rs.getString(2);
				productUrl = rs.getString(3);
				keyword = rs.getString(4);
				rank = rs.getString(5);
				productName = rs.getString(6);
				productPrice = formatter.format(Integer.parseInt(rs.getString(8)));
				productIsRocket = rs.getString(9);
				blogDate = String.valueOf(startDate);
				
				System.out.println("Product Name = " + productName);
				
				 String postDetail = "", postUrl = "", postTitle = "";
	        	 Statement linkStmt = con.createStatement();
	        	 ResultSet linkRs = linkStmt.executeQuery("select POST_DTLS, POST_URL, POST_TITLE from table_coupang_prdct_link where PRDCT_ID = '" + productId + "'");
	        	 while (linkRs.next()) {
	        		 postDetail = linkRs.getString(1) == null ? "" : linkRs.getString(1);
	        		 postUrl = linkRs.getString(2) == null ? "" : linkRs.getString(2);
	        		 postTitle = linkRs.getString(3) == null ? "" : linkRs.getString(3);
	        		 
	        	 }
	        	 
	             try{
	                 if(linkRs != null)linkRs.close();
	             } catch (SQLException e) {throw new Exception(e.getMessage());}
	             try{
	                 if(linkStmt != null)linkStmt.close();
	             } catch (SQLException e) {throw new Exception(e.getMessage());}
				
	            String imgPath = "";
	            imgPath = uploadImg(productImage, xmlrpcClient);
	            System.out.println("imgPath = " + imgPath);
				
				String str = productName;
				String tag = keyword;
				String hashTag = "#" + keyword;
				String[] prdctNmArray = str.replaceAll(",", "").split("\\ ");
				
				for (int i = 0; i < prdctNmArray.length; i++) {
					if(prdctNmArray[i].indexOf(tag) == -1) {
						tag = tag + ", " + prdctNmArray[i];
						hashTag = hashTag + " #" + prdctNmArray[i];
					}
				}
				String convTag = tag.replace("[", "").replace("]", "");
				System.out.println("convTag = " + convTag);
				
				String isRckt = "";
				if("true".equals(productIsRocket)){isRckt = "가능한";}else {isRckt = "불가능한";}
				
				String[] reviewSearchArray = productName.split("\\ ");
				String reviewSearch = ""; 
				switch (reviewSearchArray.length) {
				case 3:
					reviewSearch = reviewSearchArray[0] + " " + reviewSearchArray[1] + " " + reviewSearchArray[2];
					break;
				case 2:
					reviewSearch = reviewSearchArray[0] + " " + reviewSearchArray[1];
					break;
				case 1:
					reviewSearch = reviewSearchArray[0];
					break;
				default:
					reviewSearch = productName;
					break;
				}
				System.out.println("reviewSearch = " + reviewSearch);
				
				//Get review in Naver cafe
				String url = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + reviewSearch;
				Document doc = null;
				StringBuilder reviewSB = new StringBuilder();
				
				try {
					doc = Jsoup.connect(url).get();
					Elements element = doc.select("ul.type01");
					
					String review = "";
					String review_url = "";
					for(Element el1 : element.select("dd.sh_cafe_passage")) {
						review = review + "!@#" + el1.text();
					}
					
					for(Element el : element.select("dd.txt_block")) {
						if(el.html().indexOf("https://cafe.naver.com/") != -1) {
							review_url = review_url + "!@#" + el.getElementsByClass("url");
						}
					}
					
					String[] reviewArray = review.split("\\!@#");
					String[] reviewUrlArray = review_url.split("\\!@#");
//					for (int i = 0; i < reviewArray.length; i++) {
						reviewSB.append(reviewArray[1] + reviewUrlArray[1] + "<br />");
//					}
				} catch (Exception e) {}
				//
				
				//Get review in Naver view
				url = "https://m.search.naver.com/search.naver?sm=mtp_hty.top&where=m&query=" + reviewSearch;
				doc = null;
				StringBuilder viewSB = new StringBuilder();
				
				try {
					doc = Jsoup.connect(url).get();
					Elements element = doc.select("ul.lst_total");
					
					for(Element el : element.select("li")) {
						viewSB.append(el.getElementsByClass("api_txt_lines dsc_txt").text() + el.getElementsByClass("elss web_url"));
						break;
					}
				} catch (IOException e) {}
				//

				contents = new HashMap<String, String>();
	            contents.put("categories", "상품소개"); // 카테고리 텍스트
	            contents.put("title", productName+ "(" + productPrice + "원)\r\n"); // 제목
	            contents.put("description", 
	        			"<div style=\"clear: both; text-align: center;\">\r\n" + 
	        			"<!-- 이미지 -->\r\n" + 
	        			"\r\n" + 
	        			"<br />\r\n" + 
	        			"<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"tr-caption-container\" style=\"margin-left: auto; margin-right: auto; text-align: center;\"><tbody>\r\n" + 
	        			"<tr><td style=\"text-align: center;\"><a href=\""+productImage+"\" imageanchor=\"1\" style=\"margin-left: auto; margin-right: auto;\"><img alt=\""+productName+"\" border=\"0\" data-original-height=\"662\" data-original-width=\"662\" height=\"640\" src=\""+imgPath+"\" title=\""+productName+"\" width=\"640\" /></a></td></tr>\r\n" + 
	        			"<tr><td class=\"tr-caption\" style=\"text-align: center;\">"+productName+"</td></tr>\r\n" + 
	        			"</tbody></table>\r\n" + 
	        			""+postDetail+"<a href=\""+postUrl+"\">"+postTitle+"</a>" + 
	        			reviewSB + 
	        			viewSB.toString() + "<br />" + 
	        			"<br />\r\n" + 
	        			"<!-- 가격 -->\r\n" + 
	        			"판매가: "+productPrice+"원<br />\r\n" + 
	        			"<br />\r\n" + 
	        			"<!-- 상품요약설명 -->\r\n" + 
	        			"해당상품은 로켓배송이 "+ isRckt +" 상품이며<br />\r\n" + 
	        			"자세한 상품설명 및 후기는 <a href=\""+productUrl+"\" target=\"_blank\">클릭</a> 부탁드리<br />\r\n" + 
	        			"겠습니다.<br />\r\n" + 
	        			"<!-- 배너 -->\r\n" + 
	        			"<a href=\""+productUrl+"\" target=\"_blank\"><img alt=\""+productName+"\" height=\"200\" src=\""+productImage+"\" width=\"200\" /></a><br />\r\n" + 
	        			"<br />\r\n" + 
	        			"<!-- 해시태그 -->\r\n" + 
	        			""+hashTag+"<br />\r\n" + 
	        			"<br />\r\n" + 
	        			"<!-- 포스팅 설명 -->\r\n" + 
	        			"포스팅날짜는: "+blogDate+" 이며<br />\r\n" + 
	        			"포스팅 시기에 따라 판매가와 재고의 변동이 있을 수 있습니다.<br />\r\n" + 
	        			"본 블로그는 해당 상품의 주체가 아니며 상품의 주문, 반품 등의 의무와 책임은 \r\n" + 
	        			"각 판매자에게 있으니 유의 부탁드리겠습니다.<br />\r\n" + 
	        			"<br />\r\n" + 
	        			"<br />\r\n" + 
	        			"<br />\r\n" + 
	        			"\"이글은 파트너스 활동을 통해 일정액의 수수료를 제공받을 수 있습니다.</div>\r\n" + 
	        			""); //contents
	            
	            contents.put("tags", convTag); //tag, tag, tag, tag
	 
	            
	            List<Object> params = new ArrayList<Object>();
	            
	            params.add(API_ID);  //Allowed Blank
	            params.add(API_ID);
	            params.add(API_PASSWORD); 
	            params.add(contents);
	            params.add(new Boolean(true)); // Posting is Open = true, Close = false
	            
	            XmlRpcClient client = new XmlRpcClient();
	            client.setConfig(config);
	            
	            String rendValue = String.valueOf(ThreadLocalRandom.current().nextInt(1, 2 + 1)) + "00000";
				try {
					Thread.sleep(Integer.parseInt(rendValue)); // 1000 = 1second waiting
				} catch (InterruptedException e) {throw new Exception(e.getMessage());}
	            
	            String rsString = (String) client.execute("metaWeblog.newPost", params);
	            
	            System.out.println("rsString = https://blog.naver.com/bangtae14/" + rsString);
	            
	            String update_query = "UPDATE `db_api`.`table_coupang_prdct_link` SET LINK_URL = ?,  HASHTAG = ?, UPDT_DATE = ? WHERE PRDCT_ID = ?";
	            preparedStmt = con.prepareStatement(update_query);
		        preparedStmt.setString (1, "https://blog.naver.com/bangtae14/" + rsString);
		        preparedStmt.setString (2, hashTag);
		        preparedStmt.setString (3, blogDate);
		        preparedStmt.setString (4, productId);
		        preparedStmt.execute();
	            
	            
	            update_query = "UPDATE `db_api`.`table_coupang_prdct` SET ISSUE_CODE = ?,  BLOG_DATE = ? WHERE PRDCT_ID = ?";
	            preparedStmt = con.prepareStatement(update_query);
		        preparedStmt.setString (1, "C");
		        preparedStmt.setString (2, blogDate);
		        preparedStmt.setString (3, productId);
		        preparedStmt.execute();
	            
		        con.commit();
		        
		        System.out.println("Blog posting count = " + blog_posting_count++);
			} //rs
            
        }catch(Exception e) {
        	try {
				con.rollback();
				
	            String update_query_error = "UPDATE `db_api`.`table_coupang_prdct` SET ISSUE_CODE = ?,  BLOG_DATE = ? WHERE PRDCT_ID = ?";
	        	System.out.println("rollback!!");
				preparedStmt = con.prepareStatement(update_query_error);
		        preparedStmt.setString (1, "E");
		        preparedStmt.setString (2, blogDate);
		        preparedStmt.setString (3, productId);
		        preparedStmt.execute();
		        
		        con.commit();
			} catch (SQLException e1) {
			}
            e.printStackTrace();
        }finally {
            try{
                if(rs != null)rs.close();
                System.out.println("ResultSet closed !!");
            } catch (SQLException e) {e.printStackTrace();
            }
            try{
                if(stmt != null)stmt.close();
                System.out.println("preparedStmt closed !!");
            } catch (SQLException e) {e.printStackTrace();}
            try{
                if(con != null)con.close();
                System.out.println("Connection closed !!");
            } catch (SQLException e) {e.printStackTrace();
            }
        }
    }
    
	public static String uploadImg(String remoteImg, XmlRpcClient xmlrpcClient) throws Exception {
		Map<String, Object> res = null;
		try {
			String[] imgArray = remoteImg.split("\\/");
			String[] imgNm = imgArray[imgArray.length-1].split("\\.");
			
			URL url = new URL(remoteImg);
			BufferedImage img = ImageIO.read(url);
			File file = new File(imgArray[imgArray.length-1]);
			ImageIO.write(img, imgNm[1], file);
			
			Vector<Object> params = new Vector<Object>();
			params.addElement(new String(API_ID));
			params.addElement(new String(API_ID));
			params.addElement(new String(API_PASSWORD));
			
			Map<String, Object> fileMap = new HashMap<String, Object>();
			fileMap.put("name", file.getName());
			fileMap.put("type", new MimetypesFileTypeMap().getContentType(file));
			fileMap.put("bits", getFileByte(file));
			params.addElement(fileMap);
			res = (HashMap<String, Object>) xmlrpcClient.execute("metaWeblog.newMediaObject", params);
		}catch(Exception e) {
			 throw new Exception(e.getMessage());
		}
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