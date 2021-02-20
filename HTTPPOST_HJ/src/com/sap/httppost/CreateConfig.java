package com.sap.httppost;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;

import com.sap.xi.basis.*;

public class CreateConfig {

	 private static String apiURL = "/IntegratedConfigurationInService/IntegratedConfigurationInImplBean?wsdl=binding&mode=ws_policy";
	  private static String user;
	  private static String password;
	  private static String url = "";
	  private static String host,hostport;
	  private static IntegratedConfigurationIn port;
	private static String SourceBS,TargetBS,SourceChannel;

	public void createICO(String[] ConfigInfo, String hostN, String hostportN,String UserName, String UPassword) throws Exception {
		
		user = UserName;
		password = UPassword;
		host = hostN;
		hostport = hostportN;
		SourceBS = ConfigInfo[0];
		SourceChannel = ConfigInfo[1];
		TargetBS	=	ConfigInfo[2];
		setURL(host, hostport);
	    port = getPort();
		IntegratedConfigurationCreateChangeIn createIn = new IntegratedConfigurationCreateChangeIn();
		createIn.setChangeListID(createChangeListId());
		try {
			List<RestrictedIntegratedConfiguration> listCreateICO = createIn.getIntegratedConfiguration();
			listCreateICO.addAll(readICO(queryICO()));
			// Create Configuration Object
			ConfigurationObjectModifyOut createOut = port.create(createIn);
		} catch (Exception e) {

			e.printStackTrace();
			//rejectChangeListId(createIn.getChangeListID().toString());

			System.exit(0);
		}
	}
	private String createChangeListId() {
		// TODO Auto-generated method stub
		return "Created Using API: "+System.currentTimeMillis();
	}
	// Function queryICO()

	public List<MessageHeaderID> queryICO() throws Exception {
	
		setURL(host, hostport);
	    port = getPort();

		IntegratedConfigurationQueryIn queryIn = new IntegratedConfigurationQueryIn();
		MessageHeaderID msgHdr = new MessageHeaderID();

		msgHdr.setSenderComponentID(getSourceBS());
		msgHdr.setInterfaceName("SI_ABC");
		msgHdr.setInterfaceNamespace("urn:int");
		msgHdr.setReceiverComponentID("AD_T");
		queryIn.setIntegratedConfigurationID(msgHdr);
		IntegratedConfigurationQueryOut queryOut = port.query(queryIn);
		List<MessageHeaderID> lMsgHdr = queryOut.getIntegratedConfigurationID();
		return lMsgHdr;
	}

	
	@SuppressWarnings("finally")
	public List<RestrictedIntegratedConfiguration> readICO(List<MessageHeaderID> msgHdrList) {
		List<RestrictedIntegratedConfiguration> createICOlist = new ArrayList<RestrictedIntegratedConfiguration>();
		try {
			
			setURL(host, hostport);
		    port = getPort();
			IntegratedConfigurationReadIn readIn = new IntegratedConfigurationReadIn();

			readIn.getIntegratedConfigurationID().addAll(msgHdrList);
			readIn.setReadContext(ReadContextCode.ACTIVE);
			IntegratedConfigurationReadOut readOut = port.read(readIn);
			List<IntegratedConfiguration> listreadOut = readOut.getIntegratedConfiguration();
			for (int i = 0; i < listreadOut.size(); i++) {
				RestrictedIntegratedConfiguration resICO = new RestrictedIntegratedConfiguration();

				resICO.setMasterLanguage(listreadOut.get(i).getMasterLanguage());
				// Get Description

				resICO.getDescription().addAll(listreadOut.get(i).getDescription());
				// Get Set MessageHeaderId/IntegratedCOnfiguration ID

				resICO.setIntegratedConfigurationID(listreadOut.get(i).getIntegratedConfigurationID());

				resICO.getIntegratedConfigurationID().setSenderComponentID(getTargetBS());
				CommunicationChannelID channel = new CommunicationChannelID();
				channel.setChannelID(getSourceChannel());
				channel.setComponentID(getSourceBS());
				//resICO.getInboundProcessing().setCommunicationChannel(channel);
				
				// Get Set MessageHeaderId/IntegratedCOnfiguration ID
				// Get Set InboundProcessing
				
				resICO.setInboundProcessing(listreadOut.get(i).getInboundProcessing());
				resICO.getInboundProcessing().getCommunicationChannel().setComponentID(getTargetBS());
				// Get Set InboundProcessing
				// Get Receivers

				resICO.setReceivers(listreadOut.get(i).getReceivers());
				// Get Receiver Interfaces

				resICO.getReceiverInterfaces().addAll(listreadOut.get(i).getReceiverInterfaces());
				// Get Outbound Processing

				resICO.getOutboundProcessing().addAll(listreadOut.get(i).getOutboundProcessing());

				createICOlist.add(resICO);
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			return createICOlist;
		}
	}
	private String getSourceBS() {
		// TODO Auto-generated method stub
		return "AD_T";
	}
	private String getSourceChannel() {
		// TODO Auto-generated method stub
		return "CC_SND_HTTP";
	}
	private String getTargetBS() {
		// TODO Auto-generated method stub
		return "AD_T";
	}
	
	 private static void setURL(String host, String hostport) {
		  
		    if ((host == null) || (hostport == null)) {
		      return;
		    }
		    
		    String serverPort = host + ":" + hostport;
		    url = url.concat("http://").concat(serverPort).concat(apiURL);
		    

		  }
	 private static IntegratedConfigurationIn getPort() throws Exception
	  {
	 
	    IntegratedConfigurationIn port = null;
	    try {
	      com.sap.xi.basis.IntegratedConfigurationInService service = null;
	      
	      service = new com.sap.xi.basis.IntegratedConfigurationInService();
	      
	      port = service.getIntegratedConfigurationIn_Port();
	      BindingProvider bp = (BindingProvider)port;
	      bp.getRequestContext().put("javax.xml.ws.security.auth.username", user);
	      bp.getRequestContext().put("javax.xml.ws.security.auth.password", password);
	      if (url.length() != 0) {
	        bp.getRequestContext().put("javax.xml.ws.service.endpoint.address", url);
	        
	      }
	    } catch (Exception ex) {
	      ex.printStackTrace();
	      throw ex;
	    }
	  
	    return port;
	  }
	  
}
