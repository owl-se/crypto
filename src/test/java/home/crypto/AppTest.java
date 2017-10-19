package home.crypto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Simple test for Cryptopay
 */
public class AppTest {
	
	private CloseableHttpClient httpclient;
	
	@Test(dataProvider = "test1")
	public void enterTest(String...param) throws ClientProtocolException, IOException{
		HttpGet httpGet = new HttpGet(param[0]);
		httpGet.setHeader("x-api-key", param[1]);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		
		try{
			if(response.getStatusLine().getStatusCode() == 200){
				JSONArray jsonArray = new JSONArray(EntityUtils.toString(response.getEntity()));
				assertNotEquals(0, jsonArray.length());
			} else{assert false;}
			
		} finally {
		    response.close();
		}
	}
	
	@Test(dataProvider = "test1")
	public void feeTest(String...param) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(param[0]);
		httpPost.setHeader("x-api-key", param[1]);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("amount", param[4]));
		nvps.add(new BasicNameValuePair("amount_currency", "EUR"));
		nvps.add(new BasicNameValuePair("from_account", param[2]));
		nvps.add(new BasicNameValuePair("to_account", param[3]));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		try{
			if(response.getStatusLine().getStatusCode() == 201){
				JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
			    System.out.println(obj);
			    System.out.println(obj.get("fee"));
			    assertEquals(Double.valueOf(param[4])/100*1,obj.get("fee"));
			} else{assert false;}
		} finally{
			response.close();
		}
	}
	
	@BeforeMethod
	public void setUp(){
		httpclient = HttpClients.createDefault();
	}
	
	@DataProvider(name = "test1")
	public static Object[][] testSubjects() {
	      return new Object[][] {{"https://sandbox.cryptopay.me/api/v2/exchange_transfers", "acea9d6d570540bb7d0e0f077182ffdc","95b22bb7-1bee-4bc5-9555-52689137eb49","d96d23be-30c9-4243-a9ab-e432e9a6f71d","100"}};
	   }
}
