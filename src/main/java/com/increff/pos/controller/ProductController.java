package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ProductController {
	
	@Autowired
	private ProductService product_service;
	
	@Autowired
	private BrandService brand_service;
	
	@ApiOperation(value = "Add Product Details")
	@RequestMapping(path = "/api/product", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm form) throws ApiException{
		BrandPojo brand_pojo = brand_service.getBrandCategory(form.getBrand(), form.getCategory());
		ProductPojo product_pojo = ConversionUtil.convert(brand_pojo, form);
		product_service.add(product_pojo);
	}
	
	@ApiOperation(value = "Get Product Details by ID")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable int id) throws ApiException{
		ProductPojo product_pojo = product_service.get(id);
		BrandPojo brand_pojo = brand_service.get(product_pojo.getBrandId());
		return ConversionUtil.convert(product_pojo, brand_pojo);
	}
	
	@ApiOperation(value = "Get list of Products")
	@RequestMapping(path = "/api/product", method = RequestMethod.GET)
	public List<ProductData> getAll() throws ApiException {
		List<ProductPojo> productpojo_list = product_service.getAll();
		List<ProductData> d = new ArrayList<ProductData>();
		for(ProductPojo p:productpojo_list)
		{
			BrandPojo brand_pojo = brand_service.get(p.getBrandId());
			d.add(ConversionUtil.convert(p, brand_pojo));
		}
		return d;
	}

	@ApiOperation(value = "Update a Product record")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody ProductForm form) throws ApiException {
		BrandPojo brand_pojo = brand_service.getBrandCategory(form.getBrand(), form.getCategory());
		ProductPojo product_pojo = ConversionUtil.convert(brand_pojo,form);
		product_service.update(id, product_pojo, brand_pojo);
	}
}
