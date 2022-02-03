package com.increff.pos.controller;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.BillData;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.PDFUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private InventoryService inventoryService;

	@ApiOperation(value = "Add Order Details")
	@RequestMapping(path = "/api/order", method = RequestMethod.POST)
	public OrderData add(@RequestBody List <OrderItemForm> forms)
			throws ApiException, Exception {
		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();
		//Converting list of OrderItemForms to OrderItemPojos
		for (OrderItemForm f : forms) {
			orderItemList.add(ConversionUtil.convertOrderItemForm(f, productService.get(f.getBarcode())));
		}
		int orderId = orderService.createOrder(orderItemList);
		double total = orderService.billTotal(orderId);
		return ConversionUtil.convertOrderPojo(orderService.getOrder(orderId), total);
	}

	@ApiOperation(value = "Get Order Item details record by id")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.GET)
	public OrderItemData get(@PathVariable int id) throws ApiException {
		OrderItemPojo orderItemPojo = orderService.get(id);
		ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
		OrderPojo orderPojo = orderService.getOrder(orderItemPojo.getOrderId());
		InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());
		return ConversionUtil.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo);
	}

	@ApiOperation(value = "Get list of all Order Items")
	@RequestMapping(path = "/api/order/item", method = RequestMethod.GET)
	public List<OrderItemData> getAll() throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderService.getAll();
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
			OrderPojo orderPojo = orderService.getOrder(orderItemPojo.getOrderId());
			InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());
			d.add(ConversionUtil.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

	@ApiOperation(value = "Get Invoice")
	@RequestMapping(path = "/api/order/invoice/{orderId}", method = RequestMethod.GET)
	public String getInvoice(@PathVariable int orderId) throws Exception {
		BillData billData = new BillData();
		OrderPojo orderPojo = orderService.getOrder(orderId);
		List<OrderItemPojo> orderItemPojo_list = orderService.getOrderItems(orderId);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
			InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());
			d.add(ConversionUtil.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		billData.setOrderItemData(d);
		billData.setBillAmount(orderService.billTotal(orderId));
		billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(orderPojo.getDate()));
		billData.setOrderId(orderId);
		//orderService.updateInvoice(orderId);
		PDFUtils.generatePDFFromJavaObject(billData);
		File file = new File("invoice.pdf");
		byte[] contents = Files.readAllBytes(file.toPath());
		return Base64.getEncoder().encodeToString(contents);
	}

	@ApiOperation(value = "Get list of Orders")
	@RequestMapping(path = "/api/order", method = RequestMethod.GET)
	public List<OrderData> getAllOrders() {
		List<OrderPojo> orders_list = orderService.getAllOrders();
		List<OrderData> list1 = new ArrayList<OrderData>();
		for (OrderPojo p : orders_list) {
			double total = orderService.billTotal(p.getId());
			list1.add(ConversionUtil.convertOrderPojo(p, total));
		}
		return list1;
	}

	@ApiOperation(value = "Get list of Order Items of a particular order")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
	public List<OrderItemData> getOrderItemsbyOrderId(@PathVariable int id) throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderService.getOrderItems(id);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
			OrderPojo orderPojo = orderService.getOrder(orderItemPojo.getOrderId());
			InventoryPojo inventoryPojo = inventoryService.getByProductId(productPojo.getId());
			d.add(ConversionUtil.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

	@ApiOperation(value = "Delete Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) throws ApiException {
		orderService.delete(id);
	}
	
	@ApiOperation(value = "Delete Order by id")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
	public void deleteOrder(@PathVariable int id) throws ApiException {
		orderService.deleteOrder(id);
	}
	
	@ApiOperation(value = "Update Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {
		ProductPojo productPojo = productService.get(f.getBarcode());
		OrderItemPojo orderItemPojo = ConversionUtil.convertOrderItemForm(f, productPojo);
		orderService.update(id, orderItemPojo);
	}
}