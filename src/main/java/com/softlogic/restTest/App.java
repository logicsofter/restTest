package com.softlogic.restTest;

import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.camel.*;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.context.support.*;
import org.junit.Assert;

import com.softlogic.commonRest.*;

/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args)
	{
		AbstractXmlApplicationContext appContext = new ClassPathXmlApplicationContext("classpath:cxfclient.xml");
		ProducerTemplate producer = (ProducerTemplate) appContext.getBean("producerTemplate");

		try
		{
			String retVal = producer.requestBody("direct:putToRest", "44", String.class);
			System.out.println("Received: " + retVal);

			Exchange exchange1 = producer.send("direct:simplePutToRest", new Processor()
			{
				public void process(Exchange exchange) throws Exception
				{
					exchange.setPattern(ExchangePattern.InOut);

					Message inMessage = exchange.getIn();
					// setupDestinationURL(inMessage);
					// set the operation name
					inMessage.setHeader(CxfConstants.OPERATION_NAME, "healthCheck");
					// using the proxy client API
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.FALSE);
					// set the parameters , if you just have one parameter
					// camel will put this object into an Object[] itself
					inMessage.setBody("12");
				}
			});
			// get the response message
			Integer retVal1 = (Integer) exchange1.getOut().getBody();
			System.out.println("Proxy send received: " + retVal1);

			Exchange exchange2 = producer.send("direct:simplePutToRest", new Processor()
			{
				public void process(Exchange exchange) throws Exception
				{
					exchange.setPattern(ExchangePattern.InOut);
					Message inMessage = exchange.getIn();
					// using the http central client API
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
					// set the Http method
					inMessage.setHeader(Exchange.HTTP_METHOD, "GET");
					// set the relative path
					inMessage.setHeader(Exchange.HTTP_PATH, "/healthCheck/12");
					// Specify the response class , cxfrs will use InputStream
					// as
					// the response object type
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, Integer.class);
					inMessage.setHeader(Exchange.ACCEPT_CONTENT_TYPE, "*/*");
					// set a customer header
					inMessage.setHeader("key", "value");
					// since we use the Get method, so we don't need to set the
					// message body
					inMessage.setBody(null);
				}
			});
			// get the response message
			Integer retVal2 = (Integer) exchange2.getOut().getBody();
			System.out.println("Http send received: " + retVal2);

			String res = producer.requestBody("direct:postRest", "Jim", String.class);
			System.out.println("Post received: " + res);
			//
			// Marshalling a simple object
			//
			final Simple simp = new Simple();
			simp.setName("Bruno");
			simp.setMessage("rocks");
			Exchange exchange3 = producer.send("direct:simplePutToRest", new Processor()
			{
				public void process(Exchange exchange) throws Exception
				{
					exchange.setPattern(ExchangePattern.InOut);
					Message inMessage = exchange.getIn();
					// using the http central client API
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
					// set the Http method
					inMessage.setHeader(Exchange.HTTP_METHOD, "POST");
					// set the relative path
					inMessage.setHeader(Exchange.HTTP_PATH, "/simpleProc");
					inMessage.setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);

					inMessage.setBody(simp);
				}
			});
			// get the response message
			Response rep = (Response) exchange3.getOut().getBody();
			String sre = rep.readEntity(String.class);
			// Object resu = exchange3.getOut().getBody();
			System.out.println("Http simple send received: " + sre);

			Preference pf = new Preference(true, true, true);
			final AccountDetails ad = new AccountDetails(30042, "DM&!@()", "MEL", "web", "4", pf);
			Exchange exchange4 = producer.send("direct:simplePutToRest", new Processor()
			{
				public void process(Exchange exchange) throws Exception
				{
					exchange.setPattern(ExchangePattern.InOut);
					Message inMessage = exchange.getIn();
					// setupDestinationURL(inMessage);
					// using the http central client API
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
					// set the Http method
					inMessage.setHeader(Exchange.HTTP_METHOD, "POST");
					// set the relative path
					inMessage.setHeader(Exchange.HTTP_PATH, "/makePayment");
					// Specify the response class , cxfrs will use InputStream
					// as the response object type
					inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, AccountResp.class);
					inMessage.setHeader(Exchange.ACCEPT_CONTENT_TYPE, MediaType.WILDCARD);
					inMessage.setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);

					inMessage.setBody(ad);
				}
			});
			// get the response message
			AccountResp ar = (AccountResp) exchange4.getOut().getBody();
			System.out.println("Post received: " + ar.getType());

			AccountResp resp = producer.requestBody("direct:makePaymentRest", ad, AccountResp.class);
			System.out.println("Post received: " + resp.getAmount() 
					+ " for "+ resp.getAccountLst().get(0).getType());

		} catch (Exception ex)
		{
			System.out.println("Ex: " + ex.toString());
		} finally
		{
			appContext.close();
		}
	}
}
