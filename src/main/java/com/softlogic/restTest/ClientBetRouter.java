package com.softlogic.restTest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.*;

public class ClientBetRouter extends RouteBuilder
{
	@Override
	public void configure() throws Exception
	{
		from("direct:putToRest")
        	.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(true))
        	.setHeader(Exchange.HTTP_PATH, simple("/healthCheck/12"))
        	.setHeader(Exchange.HTTP_METHOD, constant("GET"))
        .to("cxfrs:bean:myRestClient");
		
		from("direct:simplePutToRest").to("cxfrs:bean:myRestClient");
	}
}
