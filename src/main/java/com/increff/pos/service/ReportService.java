package com.increff.pos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

@Service
public class ReportService {

	@Autowired
	private BrandDao brandDao;

	// Brand Report
	// Getting all brands
	public List<BrandPojo> getAll() {
		return brandDao.selectAll();
	}
}
