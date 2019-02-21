package com.waseel.achi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.UriInfo;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Immutable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSelection;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.query.facet.FacetingRequest;

import com.waseel.achi.Search.Lists.SearchViewTransformer;
import com.waseel.achi.entity.AbstractCode;
import com.waseel.achi.entity.AchiCode;
import com.waseel.achi.entity.Code;
import com.waseel.achi.entity.HospitalCode;

@Stateless
public class Search {
	
	public static final int MAX_FETCH_SIZE_TEXT = 15;

	@PersistenceContext
	EntityManager em;
	
	
	public SearchResult _searchByTextAsResult(int nextPageToken,
			String query, String field, UriInfo uri) {

		SearchResult result = new SearchResult(_searchByText(
				nextPageToken, query, field), nextPageToken,
				MAX_FETCH_SIZE_TEXT,
				SearchViewTransformer.PROVIDER_BLOCK_VIEW, uri);
		return result;
	}
	
	public Collection<Code> _searchByText(int nextPageToken, String q, String field) {
		FullTextSession fullTextSession = createFullTextSession();
		QueryBuilder builder = createBuilder(fullTextSession, Code.class);
		String nGramFieldName = "nGramDesc";
		String edgeNGramFieldName = "edgeNGramDesc";
		String fieldName = "desc_";

//				org.apache.lucene.search.Query query = builder.phrase().withSlop(2).onField("nGramName")
//				   .andField("edgeNGramName").boostedTo(5)
//				   .sentence(q.toLowerCase()).createQuery();
		
		switch(field) {
		case "hospital_code":
			nGramFieldName = "nGramCode";
			edgeNGramFieldName = "edgeNGramCode";
			fieldName = "code_";
			break;
		case "achi_code":
			nGramFieldName = "nGramAchi";
			edgeNGramFieldName = "edgeNGramAchi";
			fieldName = "achi_";
			break;
		case "description":
			break;
		case "achi_description":
			nGramFieldName = "nGramADesc";
			edgeNGramFieldName = "edgeNGramADesc";
			fieldName = "adesc_";
			break;
		default:
			break;
		}
		
		org.apache.lucene.search.Query
//		query = builder.phrase().withSlop(2)//.onField(nGramFieldName)
//				   .onField(edgeNGramFieldName).boostedTo(5)
//				   .sentence(q.toLowerCase()).createQuery();
		
		query = builder.keyword()//.onField(nGramFieldName)
		   .onField(edgeNGramFieldName).andField(fieldName).boostedTo(5f)
		   .matching(q.toLowerCase()).createQuery();
		
		

				 FullTextQuery hibQuery = fullTextSession.createFullTextQuery(
				    query, Code.class);
		
		hibQuery.setFirstResult(nextPageToken).setMaxResults(
				MAX_FETCH_SIZE_TEXT);

		//@SuppressWarnings("unchecked")
		List<Code> results = (List<Code>)hibQuery.list();
		
//		if(field.equals("achi_code")) {
//			Set<Code> rs = new LinkedHashSet<>(results);
//			return rs;
//		}
		return results;
	}
	
	public SearchResult searchByTextFacetAsResult(int nextPageToken,
			String query, String field, UriInfo uri) {

		SearchResult result = new SearchResult(searchByTextFacet(
				nextPageToken, query, field), nextPageToken,
				MAX_FETCH_SIZE_TEXT,
				SearchViewTransformer.PROVIDER_BLOCK_VIEW, uri);
		return result;
	}
	
	public Collection<Code> searchByTextFacet(int nextPageToken, String q, String field) {
//		FullTextSession fullTextSession = createFullTextSession();
//		QueryBuilder builder = createBuilder(fullTextSession, Code.class);
//		String nGramFieldName = "nGramDesc";
//		String edgeNGramFieldName = "edgeNGramDesc";
//		
//		switch(field) {
//		case "hospital_code":
//			nGramFieldName = "nGramCode";
//			edgeNGramFieldName = "edgeNGramCode";
//			break;
//		case "achi_code":
//			nGramFieldName = "nGramAchi";
//			edgeNGramFieldName = "edgeNGramAchi";
//			break;
//		case "description":
//			break;
//		default:
//			break;
//		}
//	
//		
//		org.apache.lucene.search.Query query = builder.keyword()//.onField(nGramFieldName)
//				   .onField(edgeNGramFieldName)
//				   .matching(q.toLowerCase()).createQuery();
//
//				 FullTextQuery hibQuery = fullTextSession.createFullTextQuery(
//				    query, Code.class);//.setProjection(ProjectionConstants.SCORE, ProjectionConstants.THIS);
//		Set<Code> results = new LinkedHashSet<>(hibQuery.list());
//		int fromIndex = nextPageToken;
//		int toIndex = (fromIndex + MAX_FETCH_SIZE_TEXT) > results.size() ? results.size() : (nextPageToken + MAX_FETCH_SIZE_TEXT);
//		if (results.size() <= fromIndex) {
//			return Collections.emptyList(); // way too big / reached maximum
//		}
//		return new LinkedList<>(results).subList(fromIndex, toIndex);
		javax.persistence.Query query = em.createNamedQuery("Code.findOneDistinct")
				.setParameter("icd10", q+"%")
				.setFirstResult(nextPageToken)
				.setMaxResults(MAX_FETCH_SIZE_TEXT);
		return query.getResultList();	
	}
	
//	public Collection<Code> searchByTextFacet(int nextPageToken, String q, String field) {
//		FullTextSession fullTextSession = createFullTextSession();
//		QueryBuilder builder = createBuilder(fullTextSession, Code.class);
//		String nGramFieldName = "nGramDesc";
//		String edgeNGramFieldName = "edgeNGramDesc";
//		
//		switch(field) {
//		case "hospital_code":
//			nGramFieldName = "nGramCode";
//			edgeNGramFieldName = "edgeNGramCode";
//			break;
//		case "achi_code":
//			nGramFieldName = "nGramAchi";
//			edgeNGramFieldName = "edgeNGramAchi";
//			break;
//		case "description":
//			break;
//		default:
//			break;
//		}
//		
//		FacetingRequest icd10FacetingRequest = builder.facet()
//			    .name("icd10FacetRequest")
//			    .onField("achi_")
//			    .discrete()
//			    .orderedBy(FacetSortOrder.COUNT_DESC)
//			    .includeZeroCounts(false)
//			    //.maxFacetCount(3) 
//			    .createFacetingRequest();
//		
//		org.apache.lucene.search.Query query = builder.phrase().withSlop(2).onField(nGramFieldName)
//				   .andField(edgeNGramFieldName).boostedTo(5)
//				   .sentence(q.toLowerCase()).createQuery();
//
//
//				 FullTextQuery hibQuery = fullTextSession.createFullTextQuery(
//				    query, Code.class).setProjection(ProjectionConstants.SCORE, ProjectionConstants.THIS);
//
//				 FacetManager facetManager = hibQuery.getFacetManager();
//				 facetManager.enableFaceting(icd10FacetingRequest);
//		
//				
//
//		@SuppressWarnings("unchecked")
//		List<Code> ls = new LinkedList<>();
//		if(q.length() < 3 || null == facetManager || 
//				facetManager.getFacets("icd10FacetRequest") == null) { // can be because of keyword shorter than 3, minimum gram size is 3
//			return ls;
//		}
//		List<Facet> results = facetManager.getFacets("icd10FacetRequest");
//		int fromIndex = nextPageToken;
//		int toIndex = (fromIndex + MAX_FETCH_SIZE_TEXT) > results.size() ? results.size() : (nextPageToken + MAX_FETCH_SIZE_TEXT);
//		if (results.size() <= fromIndex) {
//			return ls; // way too big / reached maximum
//		}
//		
//		/* can be improved by maxFacetCount */
//		List<Facet> page = results.subList(fromIndex, toIndex);
//		
//		FacetSelection selection = facetManager.getFacetGroup("icd10FacetRequest");
//		
//		for(Facet facet : page) {
//			selection.clearSelectedFacets();
//			selection.selectFacets(facet);
//			ls.addAll(hibQuery.setMaxResults(1).list());
//		}
//		return ls;
//	}
	
	private FullTextSession createFullTextSession() {
		Session session = em.unwrap(Session.class);
		FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(session);
		return fullTextSession;
	}
	
	private QueryBuilder createBuilder(FullTextSession session,
			Class<?> forEntity) {
		QueryBuilder builder = session.getSearchFactory().buildQueryBuilder()
				.forEntity(forEntity).get();
		return builder;
	}
	
	
	/*******************************************************************************/
	
	public <T> SearchResult searchByText(int nextPageToken,
			String q,
			Class<T> resultClass,
			UriInfo uri,
			String ...indexFieldName) {
		FullTextSession fullTextSession = createFullTextSession();
		QueryBuilder builder = createBuilder(fullTextSession, resultClass);
		
		org.apache.lucene.search.Query query = keywordOrPhrase(builder,
				q,
				indexFieldName);
		
		FullTextQuery hibQuery = fullTextSession.createFullTextQuery(
				    query, resultClass);
		
		hibQuery.setFirstResult(nextPageToken)
		.setMaxResults(MAX_FETCH_SIZE_TEXT);

		@SuppressWarnings("unchecked")
		List<T> list = hibQuery.list();
		SearchResult result = new SearchResult(list, nextPageToken,
				MAX_FETCH_SIZE_TEXT,
				SearchViewTransformer.PROVIDER_BLOCK_VIEW, uri);
		return result;
	}
	
	private org.apache.lucene.search.Query keywordOrPhrase(QueryBuilder builder,
			String q,
			String ...indexFieldName) {
		org.apache.lucene.search.Query query = null;
		if(indexFieldName[0].contains("Description")) {
		query = builder.phrase()
					.withSlop(3)
				   .onField(indexFieldName[0])
				   .boostedTo(2f)
				   .sentence(q.toLowerCase())
				   .createQuery();
		} else {
		query = builder.keyword()
					.onField(indexFieldName[0])
					.andField(indexFieldName[1]).boostedTo(2f)
					.matching(q.toLowerCase())
					.createQuery();
		}
		return query;
	}
	
	
 static class Lists {

		public static Object transform(Collection<?> src,
				SearchViewTransformer transformer) {
			Object dest = null;

			switch (transformer) {
			case PROVIDER_BLOCK_VIEW:
				dest = toSearchBlockView(src);
				break;
			}
			return dest;
		}

		@SuppressWarnings("unchecked")
		private static Map<?, ?> toSearchBlockView(Collection<?> src) {
			char block = ' ';
			List<SearchViewShort> views = new LinkedList<>();
			Map<Character, Collection<?>> dest = new HashMap<>();
			for (AbstractCode entity : (Collection<AbstractCode>) src) {
				if (!(entity instanceof HibernateProxy)) {
					views.add(toShort(entity));
				}
			}
			dest.put(block, views);
			return dest;
		}

		private static <S extends AbstractCode> SearchViewShort toShort(S code) {
			SearchViewShort view = new SearchViewShort(code._hospitalCode(),
					code._hospitalDesc(), code._achiCode(),
					code._achiMode(), code._achiDesc());
			return view;
		}
		
		

		public enum SearchViewTransformer {
			PROVIDER_SHORT_VIEW, PROVIDER_MEDIUM_VIEW, PROVIDER_SECTION_VIEW, PROVIDER_BLOCK_VIEW;
		}
	}
	
	static class SearchViewShort {
		private String code;
		private String desc;
		private String icd10;
		private String type;
		private String adesc;

		public SearchViewShort(String code, String desc, String icd10, String type, String adesc) {
			this.code = code;
			this.desc = desc;
			this.icd10 = icd10;
			this.type = type;
			this.adesc = adesc;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getIcd10() {
			return icd10;
		}

		public void setIcd10(String icd10) {
			this.icd10 = icd10;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getAdesc() {
			return adesc;
		}

		public void setAdesc(String adesc) {
			this.adesc = adesc;
		}
	}

}
