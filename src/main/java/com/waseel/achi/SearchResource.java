package com.waseel.achi;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.search.query.facet.Facet;

import com.waseel.achi.entity.AchiCode;
import com.waseel.achi.entity.HospitalCode;

@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class SearchResource {

	private static final Logger LOGGER = Logger.getLogger(SearchResource.class
			.getName());

	@Inject
	Search search;

//	@GET
//	@Path("/textsearch")
//	public Response _searchByKeyword(@QueryParam("q") String query,
//			@QueryParam("nextPageToken") int nextPageToken,
//			@DefaultValue("description") @QueryParam("f") String field, @Context UriInfo uri) {
//		try {
//			Result result = field.equalsIgnoreCase("achi_code") ? search.searchByTextFacetAsResult(nextPageToken, query, field, uri) : 
//				search._searchByTextAsResult(nextPageToken, query, field,
//					uri);
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
	
	/**********************************************************************/
	
	@GET
	@Path("/textsearch")
	public Response searchByText(@QueryParam("q") String query,
			@QueryParam("nextPageToken") int nextPageToken,
			@DefaultValue("description") @QueryParam("f") String field,
			@Context UriInfo uri) {
		try {
			String indexFieldName1 = "edgeNGramDescription";
			String indexFieldName2 = "";
			Class<?> resultClass = HospitalCode.class;
			switch(field) {
			case "hospital_code":
				indexFieldName1 = "edgeNGramCode";
				indexFieldName2 = "code_";
				resultClass = HospitalCode.class;
				break;
			case "achi_code":
				indexFieldName1 = "edgeNGramCode";
				indexFieldName2 = "code_";
				resultClass = AchiCode.class;
				break;
			case "description":
				indexFieldName1 = "standardDescription";
				resultClass = HospitalCode.class;
				break;
			case "achi_description":
				indexFieldName1 = "standardDescription";
				resultClass = AchiCode.class;
				break;
			}
			SearchResult result = search.searchByText(nextPageToken,
					query,
					resultClass,
					uri,
					indexFieldName1,
					indexFieldName2);
			return Response.ok().entity(result)
					.build();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			return Response.serverError()
					.entity(new Result.NoResult()).build();
		}
	}
}
