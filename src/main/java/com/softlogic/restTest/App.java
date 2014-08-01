package com.softlogic.restTest;

import org.apache.camel.*;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.context.support.*;
import org.junit.Assert;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	AbstractXmlApplicationContext appContext = new ClassPathXmlApplicationContext("classpath:cxfclient.xml");
    	ProducerTemplate producer = (ProducerTemplate)appContext.getBean("producerTemplate") ;
  
		String retVal = producer.requestBody("direct:putToRest", "44", String.class);
		System.out.println( "Received: " + retVal);

    	Exchange exchange1 = producer.send("direct:simplePutToRest", new Processor() {

    	    public void process(Exchange exchange) throws Exception {
    	        exchange.setPattern(ExchangePattern.InOut);
    	        	
    	        Message inMessage = exchange.getIn();
    	        //setupDestinationURL(inMessage);
    	        // set the operation name 
    	        inMessage.setHeader(CxfConstants.OPERATION_NAME, "healthCheck");
    	        // using the proxy client API
    	        inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.FALSE);
    	        // set a customer header
    	        inMessage.setHeader("key", "value");
    	        // set the parameters , if you just have one parameter 
    	        // camel will put this object into an Object[] itself
    	        inMessage.setBody("12");
    	    }
    	});
    	// get the response message 
    	Integer retVal1 = (Integer) exchange1.getOut().getBody();
        System.out.println( "Proxy send received: " + retVal1);
	
		Exchange exchange2 = producer.send("direct:simplePutToRest", new Processor() {
		    public void process(Exchange exchange) throws Exception {
		        exchange.setPattern(ExchangePattern.InOut);
		        Message inMessage = exchange.getIn();
		        //setupDestinationURL(inMessage);
		        // using the http central client API
		        inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
		        // set the Http method
		        inMessage.setHeader(Exchange.HTTP_METHOD, "GET");
		        // set the relative path
		        inMessage.setHeader(Exchange.HTTP_PATH, "/healthCheck/12");                
		        // Specify the response class , cxfrs will use InputStream as the response object type 
		        inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, Integer.class);
		        inMessage.setHeader(Exchange.ACCEPT_CONTENT_TYPE, "*/*");
		        // set a customer header
		        inMessage.setHeader("key", "value");
		        // since we use the Get method, so we don't need to set the message body
		        inMessage.setBody(null);                
		    }
		});
		// get the response message 
		Integer retVal2 = (Integer) exchange2.getOut().getBody();
        System.out.println( "Http send received: " + retVal2);
		 
        appContext.close();
    }
}
