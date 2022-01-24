package com.increff.pos.util;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;

public class ConversionUtil {

	// BrandForm toBrandPojo
	public static BrandPojo convert(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setBrand(f.getBrand());
		p.setCategory(f.getCategory());
		return p;
	}

	// BrandPojo toBrandData
	public static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrand(p.getBrand());
		d.setCategory(p.getCategory());
		d.setId(p.getId());
		return d;
	}

	// BrandPojo to list of brand data
	public static List<BrandData> convert(List<BrandPojo> list) {
		List<BrandData> list1 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list1.add(convert(p));
		}
		return list1;
	}

	// toProductPojo
	public static ProductPojo convert(BrandPojo p, ProductForm f) {
		ProductPojo product_pojo = new ProductPojo();
		product_pojo.setName(f.getName());
		product_pojo.setMrp(f.getMrp());
		product_pojo.setBrandId(p.getId());
		return product_pojo;
	}

	// ProductPojo toProductData
	public static ProductData convert(ProductPojo p, BrandPojo brandPojo) {
		ProductData d = new ProductData();
		d.setId(p.getId());
		d.setMrp(p.getMrp());
		d.setName(p.getName());
		d.setBarcode(p.getBarcode());
		d.setBrand(brandPojo.getBrand());
		d.setCategory(brandPojo.getCategory());
		return d;
	}

	// Convert Inventory Form to Inventory Pojo
	public static InventoryPojo convert(InventoryForm f, ProductPojo product_pojo) throws ApiException {
		InventoryPojo p = new InventoryPojo();
		p.setProductId(product_pojo.getId());
		p.setQuantity(f.getQuantity());
		return p;
	}

	// Convert Inventory Pojo to Inventory Data
	public static InventoryData convert(InventoryPojo p, ProductPojo productPojo) {
		InventoryData d = new InventoryData();
		d.setId(p.getId());
		d.setBarcode(productPojo.getBarcode());
		d.setName(productPojo.getName());
		d.setQuantity(p.getQuantity());
		return d;
	}

	// Convert OrderItemForm to OrderItemPojo
	public static OrderItemPojo convertOrderItemForm(OrderItemForm orderItemForm, OrderPojo orderPojo,
			ProductPojo productPojo) {
		OrderItemPojo item = new OrderItemPojo();
		item.setOrderId(orderPojo.getId());
		item.setProductId(productPojo.getId());
		item.setQuantity(orderItemForm.getQuantity());
		item.setSellingPrice(orderItemForm.getSellingPrice());
		item.setMrp(productPojo.getMrp());
		return item;
	}

	// Convert Order Pojo to Order Data
	public static OrderData convertOrderPojo(OrderPojo pojo) {
		OrderData d = new OrderData();
		d.setId(pojo.getId());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String datetime = pojo.getDatetime().format(formatter);
		d.setDatetime(datetime);
		return d;
	}

	// Convert to OrderItem data
	public static OrderItemData convert(OrderItemPojo orderItemPojo, ProductPojo productPojo, OrderPojo orderPojo) {
		OrderItemData d = new OrderItemData();
		d.setId(orderItemPojo.getId());
		d.setBarcode(productPojo.getBarcode());
		d.setQuantity(orderItemPojo.getQuantity());
		d.setOrderId(orderPojo.getId());
		d.setName(productPojo.getName());
		d.setSellingPrice(orderItemPojo.getSellingPrice());
		d.setMrp(productPojo.getMrp());
		return d;
	}

	// Convert List of Order Pojos to Data
	public static List<OrderData> convertOrderList(List<OrderPojo> list) {
		List<OrderData> list1 = new ArrayList<OrderData>();
		for (OrderPojo p : list) {
			list1.add(convertOrderPojo(p));
		}
		return list1;
	}
}
