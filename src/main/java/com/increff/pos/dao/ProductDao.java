package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.ProductPojo;

@Repository
public class ProductDao extends AbstractDao {

	// Select product using barcode
	public ProductPojo select(String barcode) {
		TypedQuery<ProductPojo> query = getQuery("select p from ProductPojo p where barcode=:barcode", ProductPojo.class);
		query.setParameter("barcode", barcode);
		List<ProductPojo> list = query.getResultList();
		if (list.size() > 0) {
			ProductPojo p = list.get(0);
			return p;
		} else
			return null;
	}
	
	//Select all products
	public List<ProductPojo> selectAll() {
		TypedQuery<ProductPojo> query = getQuery("select p from ProductPojo p", ProductPojo.class);
		return query.getResultList();
	}
}
