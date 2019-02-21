package com.waseel.achi;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("/admin")
public class Admin {
	
	@PersistenceContext
	EntityManager em;
	
	@POST
	@Path("/create/indexer")
	public synchronized Response createIndexer() {
		FullTextEntityManager fullTextEntityManager = Search
				.getFullTextEntityManager(em);
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			return Response.serverError().build();
		}
		StringBuffer result = new StringBuffer("result-> success");
		return Response.ok(result).build();
	}

}
