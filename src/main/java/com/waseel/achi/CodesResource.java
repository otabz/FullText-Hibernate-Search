package com.waseel.achi;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hibernate.exception.ConstraintViolationException;

import com.waseel.achi.Search.SearchViewShort;
import com.waseel.achi.entity.AchiCode;
import com.waseel.achi.entity.Code;
import com.waseel.achi.entity.HospitalCode;

@Stateless
@Path("/codes")
@Produces("application/json")
public class CodesResource {
	
	private static final Logger LOGGER = Logger.getLogger(CodesResource.class
			.getName());
	
	@Inject
	Codes codes;

//	@GET
//	public Response list(@QueryParam("nextPageToken") int nextPageToken,
//			@DefaultValue("hospital_code") @QueryParam("f") String field,
//			@Context UriInfo uri) {
//		try {
//			Result result = (field.equalsIgnoreCase("achi_code")) ? codes.allGroupByAsResult(nextPageToken, uri) : 
//				codes.allAsResult(field, nextPageToken, uri);
//			return Response.ok().entity(result)
//					//.header("Access-Control-Allow-Origin", "*")
//					.build();
//			// return result;
//		} catch (Exception e) {
//			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
//			return Response.serverError()
//					//.header("Access-Control-Allow-Origin", "*")
//					.entity(new Result.NoResult()).build();
//		}
//	}
//	
	@GET
	@Path("/code")
	public Response code(@QueryParam("code") String code) {
		try {
			SearchViewShort entity = codes.get(code, HospitalCode.class);			
			return Response.ok().entity(entity).build();
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			return Response.serverError().entity(e.getLocalizedMessage()).build();
		}
	}
	
	@GET
	@Path("/find/achi")
	public Response achi(@QueryParam("code") String code) {
		try {
			SearchViewShort entity = codes.get(code, AchiCode.class);
			return Response.ok().entity(entity).build();
		}  catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			return Response.serverError().entity(e.getLocalizedMessage()).build();
		}
	}
	
	@GET
	@Path("/achi/{code}")
	public Response list(@PathParam("code") String code,
			@QueryParam("nextPageToken") int nextPageToken,
			@Context UriInfo uri) {
		try {
			Result result = codes.sub(nextPageToken, code, uri);
			return Response.ok().entity(result)
					.build();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			return Response.serverError()
					.entity(new Result.NoResult()).build();
		}
	}
	
	@POST
	@Path("/map")
	@Consumes("application/json")
	public Response map(
			Code data,
			@Context UriInfo uri) {
		try {
			codes.map(data);
			return Response.ok().entity(data)
					.build();
		} 
		catch(Exception e) {
			return Response.serverError().entity(e.getLocalizedMessage())
					.build();
		}
	}
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	public Response add(List<Code> data,
			@Context UriInfo uri) {
		try {
			codes.add(data);
			return Response.ok().entity(data)
					.build();
		} 
		catch(Exception e) {
			return Response.serverError().entity(e.getLocalizedMessage())
					.build();
		}
	}
	
	/****************************************************************************/
	@GET
	public Response codes(@QueryParam("nextPageToken") int nextPageToken,
			@DefaultValue("hospital_code") @QueryParam("f") String field,
			@Context UriInfo uri) {
		try {
			CodesResult result = null;
			switch(field) {
				case "hospital_code":
				result = codes.getResultList("Hospital.Codes",
						HospitalCode.class,
						nextPageToken,
						uri);
				break;
				case "description":
				result = codes.getResultList("Hospital.Descriptions",
							HospitalCode.class,
							nextPageToken,
							uri);
				break;
				case "achi_code":
				result = codes.getResultList("Achi.Codes",
							AchiCode.class,
							nextPageToken,
							uri);
				break;
				case "achi_description":
				result = codes.getResultList("Achi.Descriptions",
							AchiCode.class,
							nextPageToken,
							uri);
				break;
			} 
			return Response.ok().entity(result)
					.build();
		} catch(Exception e) {
			return Response.serverError().entity(e.getLocalizedMessage())
					.build(); 
		}
	}
}
