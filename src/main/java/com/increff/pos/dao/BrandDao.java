package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;

@Repository
public class BrandDao extends AbstractDao {

	// Select all Brands
	public List<BrandPojo> selectAll() {
		TypedQuery<BrandPojo> query = getQuery("select p from BrandPojo p", BrandPojo.class);
		return query.getResultList();
	}
	
	// Select BrandPojo based on brand and category values
	public BrandPojo selectBrandCategory(String brand, String category) {
		TypedQuery<BrandPojo> query = getQuery("select p from BrandPojo p where p.brand=:brand and p.category=:category", BrandPojo.class);
		query.setParameter("brand", brand);
		query.setParameter("category", category);
		return getSingle(query);
	}
	
	// Select BrandPojo based on brand name
	public List<BrandPojo> selectBrand(String brand){
		TypedQuery<BrandPojo> query = getQuery("select p from BrandPojo p where p.brand=:brand", BrandPojo.class);
		query.setParameter("brand", brand);
		return query.getResultList();
	}
}
