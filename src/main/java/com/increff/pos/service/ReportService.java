package com.increff.pos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.model.SalesReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;

@Service
public class ReportService {

	@Autowired
	private BrandService brandService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	// Sales Report
	@Transactional
	public List<SalesReportData> getCategoryWiseSalesReport(String brand, Date startDate, Date endDate)
			throws ApiException {
		List<SalesReportData> salesReportData = new ArrayList<SalesReportData>();
		
		//Getting BrandPojos by brand name
		List<BrandPojo> brandCategoryList = brandService.getbyBrand(brand);
		
		//Getting all orders between two dates
		List<OrderPojo> orderList = orderService.getAllBetween(startDate, endDate);
		
		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();

		for (OrderPojo order : orderList) {
			List<OrderItemPojo> orderItemListTemp = orderService.getOrderItems(order.getId());
			orderItemList.addAll(orderItemListTemp);
		}

		List<ProductPojo> productList = new ArrayList<ProductPojo>();
		for (OrderItemPojo orderItem : orderItemList) {
			ProductPojo product = productService.get(orderItem.getProductId());
			productList.add(product);
		}

		for (BrandPojo brandCategory : brandCategoryList) {
			SalesReportData salesReportItemDataItem = new SalesReportData(brandCategory, 0, 0.00);
			salesReportData.add(salesReportItemDataItem);
		}

		for (OrderItemPojo orderItem : orderItemList) {
			int productId = orderItem.getProductId();
			ProductPojo productPojo = productList.stream().filter(p -> p.getId() == productId).findFirst().get();
			int brandCategoryId = productPojo.getBrandId();
			for (SalesReportData salesReportItemDataItem : salesReportData) {
				if (salesReportItemDataItem.getBrandCategoryId() == brandCategoryId) {
					salesReportItemDataItem
							.setQuantity(salesReportItemDataItem.getQuantity() + orderItem.getQuantity());
					salesReportItemDataItem.setRevenue(salesReportItemDataItem.getRevenue()
							+ orderItem.getQuantity() * orderItem.getSellingPrice());
				}
			}
		}

		return salesReportData;
	}
}
