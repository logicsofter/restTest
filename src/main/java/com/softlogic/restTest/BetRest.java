package com.softlogic.restTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;


/**
 * 
 * Exposes a CXF betting interface
 * 
 */
public interface BetRest 
{
	@GET
	@Path(value = "/healthCheck/{callerID}")
	@Produces(MediaType.APPLICATION_JSON)
	Integer healthCheck(@PathParam("callerID")String callerID);
}
