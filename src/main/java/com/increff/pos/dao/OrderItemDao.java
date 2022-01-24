package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.OrderItemPojo;

@Repository
public class OrderItemDao extends AbstractDao {

	// Select all OrderItems
	public List<OrderItemPojo> selectAll() {
		TypedQuery<OrderItemPojo> query = getQuery("select p from OrderItemPojo p",
				OrderItemPojo.class);
		return query.getResultList();
	}

	// Select all OrderItems of particular order
	public List<OrderItemPojo> selectOrder(int orderId) {
		TypedQuery<OrderItemPojo> query = getQuery("select p from OrderItemPojo p where orderId=:orderId",
				OrderItemPojo.class);
		query.setParameter("orderId", orderId);
		return query.getResultList();
	}
}
