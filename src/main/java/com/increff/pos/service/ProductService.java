package com.increff.pos.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.BarcodeUtil;

@Service
public class ProductService {

	@Autowired
	private ProductDao product_dao;

	@Autowired
	private InventoryDao inventory_dao;

	// Add Product Details
	@Transactional
	public void add(ProductPojo p) throws ApiException {
		validate(p);
		normalize(p);
		p.setBarcode(BarcodeUtil.randomString(8));
		product_dao.insert(p);
		InventoryPojo inventoryPojo = new InventoryPojo();
		inventoryPojo.setQuantity(0);
		inventoryPojo.setProductId(p.getId());
		inventory_dao.insert(inventoryPojo);
	}

	// Get Product details by ID
	@Transactional
	public ProductPojo get(int id) throws ApiException {
		ProductPojo p = checkIfExists(id);
		return p;
	}

	// Get Product details by barcode
	@Transactional
	public ProductPojo get(String barcode) throws ApiException {
		ProductPojo p = checkIfExists(barcode);
		return p;
	}

	// Get all product details
	@Transactional
	public List<ProductPojo> getAll() {
		return product_dao.selectAll();
	}

	// Update product details
	@Transactional(rollbackFor = ApiException.class)
	public void update(int id, ProductPojo p, BrandPojo brand_pojo) throws ApiException {
		validate(p);
		normalize(p);
		ProductPojo ex = checkIfExists(id);
		ex.setBarcode(BarcodeUtil.randomString(8));
		ex.setBrandId(brand_pojo.getId());
		ex.setMrp(p.getMrp());
		ex.setName(p.getName());
		product_dao.update(ex);
	}

	// Check if product exists with given id
	@Transactional(rollbackFor = ApiException.class)
	public ProductPojo checkIfExists(int id) throws ApiException {
		ProductPojo p = product_dao.select(ProductPojo.class, id);
		if (p == null) {
			throw new ApiException("Product with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Check if product exists with given barcode
	@Transactional(rollbackFor = ApiException.class)
	public ProductPojo checkIfExists(String barcode) throws ApiException {
		ProductPojo p = product_dao.select(barcode);
		if (p == null) {
			throw new ApiException("Product with given barcode does not exist, barcode: " + barcode);
		}
		return p;
	}

	

	// Normalize
	protected void normalize(ProductPojo p) {
		p.setName(p.getName().toLowerCase());
	}

	// Validate
	protected void validate(ProductPojo p) throws ApiException {
		if (p.getName().isEmpty()) {
			throw new ApiException("The name of product must not be empty");
		}
		if (p.getMrp() <= 0) {
			throw new ApiException("Mrp value should be positive");
		}
	}
}
