package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderPojo;

@Repository
public class OrderDao extends AbstractDao {

	// Select all Orders
	public List<OrderPojo> selectAll() {
		TypedQuery<OrderPojo> query = getQuery("select p from OrderPojo p", OrderPojo.class);
		return query.getResultList();
	}
}
