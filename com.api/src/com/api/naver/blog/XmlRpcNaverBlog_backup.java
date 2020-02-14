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
            contents.put("categories", "상품소개"); 
            contents.put("title", "테스트제목");
            contents.put("description", "테스트내용");
            contents.put("tags", "태그1, 태그2, 태그3, 태그4");
            
            List<Object> params = new ArrayList<Object>();
            params.add(API_ID);  
            params.add(API_ID);
            params.add(API_PASSWORD); 
            params.add(contents);
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
		File file = new File("C:\\Users\\bt1\\Downloads\\2e2b8010-7d00-4c52-89a9-bf9de6ffaa84.JPG");
		Vector<Object> params = new Vector<Object>();
		params.addElement(new String(API_ID));
		params.addElement(new String(API_ID));
		params.addElement(new String(API_PASSWORD));
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