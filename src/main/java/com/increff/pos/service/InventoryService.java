package com.increff.pos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;

@Service
public class InventoryService {
	
	@Autowired
	private InventoryDao inventory_dao;

	//Add Inventory
	@Transactional
	public void add(InventoryPojo p, ProductPojo productPojo) throws ApiException {
		validate(p);
		InventoryPojo ex = getByProductId(productPojo.getId());
		ex.setQuantity(p.getQuantity() + ex.getQuantity());
		inventory_dao.update(ex);
	}
	
	//Delete Inventory
	@Transactional
	public void delete(int id) {
		inventory_dao.delete(InventoryPojo.class, id);
	}

	//Get Inventory by ID
	@Transactional
	public InventoryPojo get(int id) throws ApiException {
		InventoryPojo p = checkIfExists(id);
		return p;
	}
	
	//Get Inventory by Product ID
	@Transactional
	public InventoryPojo getByProductId(int productId) {
		InventoryPojo p = inventory_dao.selectByProductId(productId);
		return p;
	}

	//Get all inventory pojos
	@Transactional
	public List<InventoryPojo> getAll() {
		return inventory_dao.selectAll();
	}

	//Update Inventory
	@Transactional(rollbackFor = ApiException.class)
	public void update(int id, InventoryPojo p) throws ApiException {
		validate(p);
		InventoryPojo ex = checkIfExists(id);
		ex.setQuantity(p.getQuantity());
		inventory_dao.update(ex);
	}

	//Check if inventory pojo exists
	@Transactional(rollbackFor = ApiException.class)
	public InventoryPojo checkIfExists(int id) throws ApiException {
		InventoryPojo p = inventory_dao.select(InventoryPojo.class, id);
		if (p == null) {
			throw new ApiException("Inventory with given ID does not exist, id: " + id);
		}
		return p;
	}

	//Validate
	protected void validate(InventoryPojo p) throws ApiException {
		if (p.getQuantity() < 0) {
			throw new ApiException("You cannot have negative inventory");
		}
	}
}
