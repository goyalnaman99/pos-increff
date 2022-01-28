package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.SalesReportData;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ReportController {

	@Autowired
	private ReportService service;

	@ApiOperation(value = "Get sales detail of brandcategory")
	@RequestMapping(path = "api/reports/sales", method = RequestMethod.GET)
	public List<SalesReportData> getSalesReport(@RequestParam String brand,
			@RequestParam String startDate,
			@RequestParam String endDate) throws ApiException

	{
		return service.getCategoryWiseSalesReport(brand, startDate, endDate);
	}
}
