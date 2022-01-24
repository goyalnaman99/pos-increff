package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class BrandController {

	@Autowired
	private BrandService service;

	@ApiOperation(value = "Add Brand Details")
	@RequestMapping(path = "/api/brand", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm form) throws ApiException {
		BrandPojo p = ConversionUtil.convert(form);
		service.add(p);
	}

	@ApiOperation(value = "Add Brand Details as List")
	@RequestMapping(path = "/api/brand/list", method = RequestMethod.POST)
	public void add(@RequestBody List<BrandForm> form) throws ApiException {
		List<BrandPojo> p = new ArrayList<>();
		for (BrandForm f : form) {
			p.add(ConversionUtil.convert(f));
		}
		service.addList(p);
	}

	@ApiOperation(value = "Get Brand Details by ID")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		BrandPojo p = service.get(id);
		return ConversionUtil.convert(p);
	}

	@ApiOperation(value = "Get a list of all Brands")
	@RequestMapping(path = "/api/brand", method = RequestMethod.GET)
	public List<BrandData> getAll() {
		List<BrandPojo> plist = service.getAll();
		return ConversionUtil.convert(plist);
	}

	@ApiOperation(value = "Update Brand Details")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		BrandPojo p = ConversionUtil.convert(f);
		service.update(id, p);
	}
}
