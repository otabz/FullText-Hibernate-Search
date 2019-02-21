package com.waseel.achi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.core.UriInfo;

import org.hibernate.bytecode.enhance.spi.CollectionTracker;
import org.hibernate.mapping.Collection;
import org.hibernate.search.annotations.Field;

import com.waseel.achi.Search.Lists;
import com.waseel.achi.Search.SearchViewShort;
import com.waseel.achi.Search.Lists.SearchViewTransformer;
import com.waseel.achi.entity.AchiCode;
import com.waseel.achi.entity.Code;
import com.waseel.achi.entity.HospitalCode;

@Stateless
public class Codes {
	
	public static final int MAX_FETCH_SIZE = 15;
	
	@PersistenceContext
	EntityManager em;

	public CodesResult sub(int nextPageToken,
			String code,
			UriInfo uri) {
		Query q = em.createNamedQuery("Achi.Code", AchiCode.class)
				.setParameter("code", code);
		List<HospitalCode> list = ((AchiCode)q.getSingleResult()).getHospitaCodes();
		CodesResult result = new CodesResult(list, nextPageToken,
				1,//means all
				SearchViewTransformer.PROVIDER_BLOCK_VIEW, uri);
		return result;
		
	}
	
	public <T> SearchViewShort get(String code, Class<T> clazz) {
		T o = em.find(clazz, code);
		SearchViewShort view = null;
		if(null == o) {
			return view;
		}
		if(clazz.equals(HospitalCode.class)){
			HospitalCode c = ((HospitalCode)o);
			view = new SearchViewShort(c.getCode(),
					c.getDescription(),
					c.getAchiCode().getCode(),
					c.getAchiCode().getMode(),
					c.getAchiCode().getDescription());
		} else {
			AchiCode a = ((AchiCode)o);
			view = new SearchViewShort("",
					"",
					a.getCode(),
					a.getMode(),
					a.getDescription());
		}
		return view;
	}
	
	public void map(Code code) {
		HospitalCode entity = new HospitalCode();
		entity.setCode(code.getCode());
		entity.setDescription(code.getDesc());
		entity.addAchiCode(code.getIcd10());
		em.persist(entity);
		em.flush();
	}
	
	public void add(List<Code> codes) {
		Code firstNode = codes.get(0);
		AchiCode achi = new AchiCode();
		List<HospitalCode> hospitalCodes = new LinkedList<HospitalCode>();
		achi.setCode(firstNode.getIcd10());
		achi.setDescription(firstNode.getAdesc());
		achi.setMode("add");
		achi.setHospitaCodes(hospitalCodes);
		for(Code code: codes) {
			HospitalCode hospitalNode = new HospitalCode();
			hospitalNode.setCode(code.getCode());
			hospitalNode.setDescription(code.getDesc());
			achi.addHospitalCode(hospitalNode);
		}
		em.persist(achi);
		em.flush();
	}
	
	public <T> CodesResult getResultList(String queryName, Class<T> resultClass,
			int nextPageToken, UriInfo uri) {
		TypedQuery<T> q  = em.createNamedQuery(queryName, resultClass);
		List<T> list = q.setFirstResult(nextPageToken)
		.setMaxResults(MAX_FETCH_SIZE)
		.getResultList();
		CodesResult result = new CodesResult(list, nextPageToken,
				MAX_FETCH_SIZE,
				SearchViewTransformer.PROVIDER_BLOCK_VIEW, uri);
		return result;
	}

}
