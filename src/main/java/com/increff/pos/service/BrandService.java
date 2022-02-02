package com.increff.pos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

@Service
@Transactional
public class BrandService {

	@Autowired
	private BrandDao dao;

	// Adding Brand
	public void add(BrandPojo p) throws ApiException {
		normalize(p);
		validate(p);
		dao.insert(p);
	}

	// Adding brand list
	public void addList(List<BrandPojo> p) throws ApiException {
		for (BrandPojo brandPojo : p) {
			add(brandPojo);
		}
	}

	// Getting particular brand
	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	// Getting all brands
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	// Checking if brand exists
	public BrandPojo getCheck(int id) throws ApiException {
		BrandPojo p = dao.select(BrandPojo.class, id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Updating exist brand
	public void update(int id, BrandPojo p) throws ApiException {
		validate(p);
		normalize(p);
		BrandPojo ex = getCheck(id);
		ex.setBrand(p.getBrand());
		ex.setCategory(p.getCategory());
		dao.update(ex);
	}

	// Getting BrandPojo with particular brand and category
	public BrandPojo getBrandCategory(String brand, String category) throws ApiException {
		BrandPojo pojo = dao.selectBrandCategory(brand, category);
		if (pojo == null) {
			throw new ApiException("This brand name and category does not exist " + brand + " " + category);
		}
		return pojo;
	}

	// Getting List of BrandPojos of a particular brand
	public List<BrandPojo> getbyBrand(String brand) {
		return dao.selectBrand(brand);
	}

	// Validation
	public void validate(BrandPojo p) throws ApiException {
		if (p.getBrand().isEmpty() || p.getCategory().isEmpty()) {
			throw new ApiException("Fill both brand and category fields");
		}
		if(dao.selectBrandCategory(p.getBrand(), p.getCategory()) != null)
		{
			throw new ApiException("Brand-Category combination exists");
		}
	}

	// Normalizing
	public void normalize(BrandPojo p) {
		p.setBrand(p.getBrand().toLowerCase().trim());
		p.setCategory(p.getCategory().toLowerCase().trim());
	}
}
