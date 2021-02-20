package com.sap.httppost;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class DirAPICall {
	
	public static String buildInput(String[] intInfo){
		 // BuildMyString.com generated code. Please enjoy your string responsibly.

		String input = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		"  <SOAP-ENV:Body>" +
		"    <yq1:IntegratedConfiguration750CreateRequest xmlns:yq1=\"http://sap.com/xi/BASIS\">" +
		"      <IntegratedConfiguration>" +
		"        <MasterLanguage>EN</MasterLanguage>" +
		"        <IntegratedConfigurationID>" +
		"          <SenderComponentID>"+intInfo[0]+"</SenderComponentID>" +
		"          <InterfaceName>"+intInfo[1]+"</InterfaceName>" +
		"          <InterfaceNamespace>"+intInfo[2]+"</InterfaceNamespace>" +
		"        </IntegratedConfigurationID>" +
		"        <InboundProcessing>" +
		"          <CommunicationChannel>" +
		"            <ComponentID>"+intInfo[0]+"</ComponentID>" +
		"            <ChannelID>"+intInfo[3]+"</ChannelID>" +
		"          </CommunicationChannel>" +
		"        </InboundProcessing>" +
		"        <Receivers>" +
		"          <ReceiverWildcardIndicator>false</ReceiverWildcardIndicator>" +
		"          <ReceiverRule>" +
		"            <Receiver>" +
		"              <CommunicationComponent>" +
		"                <TypeID>Constant</TypeID>" +
		"                <Value>"+intInfo[0]+"</Value>" +
		"                <Datatype>xsd:string</Datatype>" +
		"              </CommunicationComponent>" +
		"            </Receiver>" +
		"          </ReceiverRule>" +
		"        </Receivers>" +
		"        <ReceiverInterfaces>" +
		"          <Receiver>" +
		"            <ComponentID>"+intInfo[4]+"</ComponentID>" +
		"          </Receiver>" +
		"          <ReceiverInterfaceRule>" +
		"            <Mapping>" +
		"              <Name>"+intInfo[8]+"</Name>" +
		"              <Namespace>"+intInfo[9]+"</Namespace>" +
		"              <SoftwareComponentVersionID>"+intInfo[12]+"</SoftwareComponentVersionID>" +
		"            </Mapping>" +
		"            <Interface>" +
		"              <Name>"+intInfo[5]+"</Name>" +
		"              <Namespace>"+intInfo[6]+"</Namespace>" +
		"            </Interface>" +
		"          </ReceiverInterfaceRule>" +
		"        </ReceiverInterfaces>" +
		"        <OutboundProcessing>" +
		"          <Receiver>" +
		"            <ComponentID>"+intInfo[4]+"</ComponentID>" +
		"          </Receiver>" +
		"          <ReceiverInterface>" +
		"            <Name>"+intInfo[5]+"</Name>" +
		"            <Namespace>"+intInfo[6]+"</Namespace>" +
		"          </ReceiverInterface>" +
		"          <CommunicationChannel>" +
		"            <ComponentID>"+intInfo[4]+"</ComponentID>" +
		"            <ChannelID>"+intInfo[7]+"</ChannelID>" +
		"          </CommunicationChannel>" +
		"        </OutboundProcessing>" +
		"      </IntegratedConfiguration>" +
		"    </yq1:IntegratedConfiguration750CreateRequest>" +
		"  </SOAP-ENV:Body>" +
		"</SOAP-ENV:Envelope>";

	
		return input;
		
	}
	public String CallICOAPI(String host,String port,String user,String password,String[] interfaceInfo) throws ClientProtocolException, IOException
	
	{
		StringEntity stringEntity = new StringEntity(DirAPICall.buildInput(interfaceInfo), "UTF-8");
	    stringEntity.setChunked(true);
	    String userInfo = user+":"+password;
	    String encoding = Base64.getEncoder().encodeToString(userInfo.getBytes("utf-8"));
	   
	    HttpPost httpPost = new HttpPost("http://"+host + ":"+port+"/IntegratedConfiguration750InService/IntegratedConfiguration750InImplBean");
	    httpPost.setEntity(stringEntity);
	    httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
	    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8");
	    httpPost.addHeader("Accept", "text/xml");
	   

	    // Execute and get the response.
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpResponse response = httpClient.execute(httpPost);
	    HttpEntity entity = response.getEntity();

	    String strResponse = null;
	    if (entity != null) {
	        strResponse = EntityUtils.toString(entity);
	    }
	   // String ChangeID = strResponse.substring(strResponse.indexOf("<ChangeListID><ChangeListID>")+28, strResponse.indexOf("</ChangeListID>"));
	    String ChangeID = StringUtils.substringBetween(strResponse,"<ChangeListID><ChangeListID>","</ChangeListID>");
	    		
	    
	   // return ChangeID;
	
	String inpActivate =  this.ActivateCallBuilder(ChangeID);
	
	StringEntity stringEntityAct = new StringEntity(inpActivate,"UTF-8");
    HttpPost httpPostAct = new HttpPost("http://"+host + ":"+port+"/ChangeListInService/ChangeListInImplBean");
    httpPostAct.setEntity(stringEntityAct);
    httpPostAct.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
    httpPostAct.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8");
    httpPostAct.addHeader("Accept", "text/xml");
   

    // Execute and get the response.
    HttpClient httpClientAct = new DefaultHttpClient();
    HttpResponse responseAct = httpClientAct.execute(httpPostAct);
	String respAct = EntityUtils.toString(responseAct.getEntity());
	return respAct;
	}
	
	public  String ActivateCallBuilder( String ChangeID){
		
		String inputActivate = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"  <SOAP-ENV:Body>" +
				"    <yq1:ChangeListActivateRequest xmlns:yq1=\"http://sap.com/xi/BASIS\">"+ChangeID+"</yq1:ChangeListActivateRequest>" +
				"  </SOAP-ENV:Body>" +
				"</SOAP-ENV:Envelope>";
		return inputActivate;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		DirAPICall obj = new DirAPICall();
		String[] intDet = {"ABCD","XYZ"};
		obj.CallICOAPI("host", "port", "User", "password",intDet);
	}

}
