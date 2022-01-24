package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class AbstractDao {

	@PersistenceContext
	EntityManager em;
	
	public <T> void insert(T i) {
		em.persist(i);
	}
	
	public <T> void delete(Class<T> c, int id) {
		T p = em.find(c, id);
		em.remove(p);
	}
	
	public <T> T select(Class<T> c, int id) {
		return em.find(c, id);
	}
	
	public <T> void update(T b) {
		em.merge(b);
	}
	
	protected <T> T getSingle(TypedQuery<T> query) {
		return query.getResultList().stream().findFirst().orElse(null);
	}
	
	protected <T> TypedQuery<T> getQuery(String jpql, Class<T> c) {
		return em.createQuery(jpql, c);
	}
	
	protected EntityManager em() {
		return em;
	}
}
