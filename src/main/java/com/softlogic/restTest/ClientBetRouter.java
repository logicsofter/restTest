package com.softlogic.restTest;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.*;

import com.softlogic.commonRest.*;

public class ClientBetRouter extends RouteBuilder
{
	@Override
	public void configure() throws Exception
	{
		onException(Exception.class).
		handled(true).					
		process(new Processor() {
	          public void process(Exchange exchange) throws Exception {
	                Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
	               
	                StackTraceElement[] trace = cause.getStackTrace();
	                cause.printStackTrace(System.out);
	                System.out.println("Router exception " + cause.getMessage());
	                // we now have the caused exception
	          }
	        }).
		end();
	    
		from("direct:simplePutToRest").to("cxfrs:bean:myRestClient");

		from("direct:simplerPutToRest").marshal().json(JsonLibrary.Gson, Simple.class).to("cxfrs:bean:myRestClient");

		from("direct:putToRest")
        	.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(true))
        	.setHeader(Exchange.HTTP_PATH, simple("/healthCheck/12"))
        	.setHeader(Exchange.HTTP_METHOD, constant("GET"))
        .to("cxfrs:bean:myRestClient");
		
		
		from("direct:postRest")
    		.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(true))
    		.setHeader(Exchange.HTTP_PATH, simple("/sayHello"))
    		.setHeader(Exchange.HTTP_METHOD, constant("POST"))
    	.to("cxfrs:bean:myRestClient");

		from("direct:convert").wireTap("direct:witeError").marshal().json(JsonLibrary.Gson, Simple.class)
		.wireTap("direct:afterError")
		.to("stream:out").to("direct:postRest");//.to("direct:simpleProcRest");
		
		from("direct:simpleProcRest")
			.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(true))
			.setHeader(Exchange.HTTP_PATH, simple("/simpleProc"))
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			//.setHeader("Content-Type").constant(MediaType.APPLICATION_JSON)
			//.setHeader("Accept").constant(MediaType.APPLICATION_JSON)
			//.marshal().json(JsonLibrary.Gson, Simple.class)
		.to("cxfrs:bean:myRestClient");
		
		from("direct:makePaymentRest")
			.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(true))
			.setHeader(Exchange.HTTP_PATH, simple("/makePayment"))
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader("Content-Type").constant("application/json")
			.setHeader("Accept").constant("application/json")
			.wireTap("direct:witeError")
			.marshal().json(JsonLibrary.Gson, AccountDetails.class)
			.wireTap("direct:afterError")
			.log("Request: ${body}")
		.to("cxfrs:bean:myRestClient");

		from("direct:witeError").beanRef("clientRoutes", "beforeJason");
		from("direct:afterError").beanRef("clientRoutes", "afterJason");
//		.marshal().json(JsonLibrary.Gson)
	}
	public void beforeJason()
	{
        System.out.println("before Json");		
	}
	public void afterJason()
	{
        System.out.println("after Json");		
	}
}
