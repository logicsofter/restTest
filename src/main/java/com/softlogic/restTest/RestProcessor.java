package com.softlogic.restTest;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;

public class RestProcessor implements Processor
{

	public void process(Exchange ex) throws Exception
	{
		Message in = ex.getIn();
		in.setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, Integer.class);
		
		Object back = ex.getOut().getBody();
        System.out.println( "Got: " + (Integer) back);

	}

}
