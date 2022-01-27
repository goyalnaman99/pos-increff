package com.increff.pos.dao;

import java.util.Date;
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
	
	// Select orders between dates
	public List<OrderPojo> selectBetweenDate(Date startDate, Date endDate){
		TypedQuery<OrderPojo> query = getQuery("select p from OrderPojo p where date between :start and :end", OrderPojo.class);
		query.setParameter("start", startDate);
		query.setParameter("end", endDate);
		return query.getResultList();
	}
}
