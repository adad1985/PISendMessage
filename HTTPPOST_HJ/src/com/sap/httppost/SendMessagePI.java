package com.sap.httppost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public class SendMessagePI {
	
	public void sendMessageToPI(String host,String port, String user, String password, String[] interfaceInfo){
		StringBuffer response = new StringBuffer();
		
		try {
		String finalURL = "http://"+host+":"+port+"/HttpAdapter/HttpMessageServlet?interfaceNamespace="+
				interfaceInfo[2]+"&interface="+interfaceInfo[1]+"&senderService="+interfaceInfo[0]+"&qos=EO";
		
		String fileN = interfaceInfo[13];
		File textFile = new File(fileN);

		
		
		
		HttpURLConnection conn = setConnect(finalURL,user,password);
		
		OutputStream os = conn.getOutputStream();
		Files.copy(textFile.toPath(), os);
		os.flush();

		
		
		String messageID = conn.getHeaderField("msgguid");

		String messageKeyInp = getMessageKeyInp(messageID);
	//	System.out.println(messageKeyInp);
		
		String messageKeyURL = "http://"+host+":"+port+"/AdapterMessageMonitoring/basic?style=document";
		HttpURLConnection connKey = setConnect(messageKeyURL,user,password,messageKeyInp);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connKey.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		String keyPayload = content.toString();
		
		// Fix to handle failed messages
		if(!keyPayload.contains("errorCode"))
		{
			
	
		String messageKey = StringUtils.substringBetween(keyPayload,"messageKey>","</");
		String messageKeyAPIInp = getMessageKeyAPIInp(messageKey);

		
		HttpURLConnection connKeyID = setConnect(messageKeyURL,user,password,messageKeyAPIInp);
		
		//Here write logic to download file from connKeyID
		
		BufferedReader inM = new BufferedReader(new InputStreamReader(connKeyID.getInputStream()));
		String inputLineM;
		StringBuffer contentM = new StringBuffer();
		while ((inputLineM = inM.readLine()) != null) {
			contentM.append(inputLineM);
		}
		inM.close();
		String payloadRaw =contentM.toString();
		String payloadEnc = StringUtils.substringBetween(payloadRaw,"Response>","</");
		String payloadDecRaw = new String(Base64.getDecoder().decode(payloadEnc));
		String payloadFinal = StringUtils.substringBetween(payloadDecRaw,"Content-Description: MainDocument","--SAP");
		
		String folder = textFile.getParent();
		PrintStream out = new PrintStream(new FileOutputStream(folder+"/Response_"+System.currentTimeMillis()+textFile.getName())) ;
			    out.print(payloadFinal.trim());
		
		    out.close();
		}
		
		}
		
		 
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMessageKeyAPIInp(String messageKey) {
		String MessageKeyAPIInp = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"  <SOAP-ENV:Body>" +
				"    <pns:getMessageBytesJavaLangStringBoolean xmlns:pns=\"urn:AdapterMessageMonitoringVi\">" +
				"      <pns:messageKey>"+messageKey+"</pns:messageKey>" +
				"      <pns:archive>false</pns:archive>" +
				"    </pns:getMessageBytesJavaLangStringBoolean>" +
				"  </SOAP-ENV:Body>" +
				"</SOAP-ENV:Envelope>";
		return MessageKeyAPIInp;

	}

	private HttpURLConnection setConnect(String finalURL,String user, String password, String inpQuery) throws IOException {
		URL url = new URL(finalURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
try{
		String userAuth = user+":"+password;
		String encoding = Base64.getEncoder().encodeToString((userAuth).getBytes("UTF-8"));

		con.setRequestProperty("Authorization", "Basic " + encoding);
		con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		
		OutputStream os = con.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(os, "UTF-8"));
		writer.write(inpQuery);
		writer.flush();
		writer.close();
		os.close();
		con.connect();
		
}
catch (Exception e){
	e.printStackTrace();
}return con;
	}
	private HttpURLConnection setConnect(String finalURL,String user, String password) throws IOException {
		URL url = new URL(finalURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		try{
		String userAuth = user+":"+password;
		String encoding = Base64.getEncoder().encodeToString((userAuth).getBytes("UTF-8"));

		con.setRequestProperty("Authorization", "Basic " + encoding);
		con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		con.setRequestMethod("POST");
		con.setDoOutput(true);	
		con.connect();}
		catch (Exception e){
			e.printStackTrace();
		}
		return con;
	}
	private String getMessageKeyInp(String messageID) {
	String inpAPIMessageKey	= "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"  <SOAP-ENV:Body>" +
				"    <pns:getMessagesByIDs xmlns:pns=\"urn:AdapterMessageMonitoringVi\">" +
				"      <yq1:messageIds xmlns:yq1=\"urn:AdapterMessageMonitoringVi\" xmlns:pns=\"urn:java/lang\">" +
				"        <pns:String>"+messageID+"</pns:String>" +
				"      </yq1:messageIds>" +
				"      <yq2:referenceIds xmlns:yq2=\"urn:AdapterMessageMonitoringVi\" xmlns:pns=\"urn:java/lang\"/>" +
				"      <yq3:correlationIds xmlns:yq3=\"urn:AdapterMessageMonitoringVi\" xmlns:pns=\"urn:java/lang\"/>" +
				"      <pns:archive>false</pns:archive>" +
				"    </pns:getMessagesByIDs>" +
				"  </SOAP-ENV:Body>" +
				"</SOAP-ENV:Envelope>";

		return inpAPIMessageKey;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SendMessagePI obj = new SendMessagePI();
		String[] interfaceInfo = {"AD_T","SI_Sender","urn:sender","","","","","","","","","","","Desktop\\Folder\\File01.xml"};
		obj.sendMessageToPI("host", "port", "user", "pwd", interfaceInfo);

	}

}
