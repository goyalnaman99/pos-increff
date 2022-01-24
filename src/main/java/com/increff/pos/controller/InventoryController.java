package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class InventoryController {

	@Autowired
	private InventoryService inventory_service;

	@Autowired
	private ProductService product_service;

	@ApiOperation(value = "Add Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
	public void add(@RequestBody InventoryForm form) throws ApiException {
		ProductPojo productPojo = product_service.get(form.getBarcode());
		InventoryPojo inventoryPojo = ConversionUtil.convert(form,productPojo);
		inventory_service.add(inventoryPojo, productPojo);
	}

	@ApiOperation(value = "Delete Inventory record")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) {
		inventory_service.delete(id);
	}

	@ApiOperation(value = "Get Inventory record by id")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable int id) throws ApiException {
		InventoryPojo inventory_pojo = inventory_service.get(id);
		ProductPojo product_pojo = product_service.get(inventory_pojo.getProductId());
		return ConversionUtil.convert(inventory_pojo, product_pojo);
	}

	@ApiOperation(value = "Get list of Products with Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
	public List<InventoryData> getAll() throws ApiException {
		List<InventoryPojo> inventory_pojo_list = inventory_service.getAll();
		List<InventoryData> d = new ArrayList<InventoryData>();
		for(InventoryPojo p : inventory_pojo_list)
		{
			ProductPojo productPojo = product_service.get(p.getProductId());
			d.add(ConversionUtil.convert(p, productPojo));
		}
		return d;
	}

	@ApiOperation(value = "Update an Inventory record")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
		ProductPojo product = product_service.get(f.getBarcode());
		InventoryPojo inventory_pojo = ConversionUtil.convert(f,product);
		inventory_service.update(id, inventory_pojo);
	}
}