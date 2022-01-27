package com.increff.pos.model;

import com.increff.pos.pojo.BrandPojo;

public class SalesReportData {
	public SalesReportData(BrandPojo brandPojo, int i, double d) {
		this.brand = brandPojo.getBrand();
		this.category = brandPojo.getCategory();
		this.brandCategoryId = brandPojo.getId();
		this.quantity = i;
		this.revenue = d;
	}

	private Double revenue;
	private Integer quantity;
	private String category;
	private String brand;
	private Integer brandCategoryId;

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getBrandCategoryId() {
		return brandCategoryId;
	}

	public void setBrandCategoryId(Integer brandCategoryId) {
		this.brandCategoryId = brandCategoryId;
	}
}
