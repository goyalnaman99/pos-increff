package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.BrandData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ReportController {
	
	@Autowired
	private BrandService brandService;
	
	//Brand Report
	@ApiOperation(value = "Get a list of all Brands")
	@RequestMapping(path = "/api/report", method = RequestMethod.GET)
	public List<BrandData> getAll(){
		List<BrandPojo> plist = brandService.getAll();
		return ConversionUtil.convert(plist);
	}
	
	//Inventory Report
	
}
