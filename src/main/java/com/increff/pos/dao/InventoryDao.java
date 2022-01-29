package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.InventoryPojo;

@Repository
public class InventoryDao extends AbstractDao {
	
	//Select Inventory by Product Id
	public InventoryPojo selectByProductId(int productId){
		TypedQuery<InventoryPojo> query = getQuery("select p from InventoryPojo p where productId=:productId", InventoryPojo.class);
		query.setParameter("productId", productId);
		return getSingle(query);
	}
	
	//Select All Inventory Pojos
	public List<InventoryPojo> selectAll(){
		TypedQuery<InventoryPojo> query = getQuery("select p from InventoryPojo p order by p.name", InventoryPojo.class);
		return query.getResultList();
	}
}
