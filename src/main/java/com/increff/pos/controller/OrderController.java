package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	@ApiOperation(value = "Add Order Details")
	@RequestMapping(path = "/api/order", method = RequestMethod.POST)
	public OrderData add(@RequestBody List <OrderItemForm> forms, HttpServletResponse response)
			throws ApiException, Exception {
		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();
		OrderPojo orderPojo = orderService.add();
		for (OrderItemForm f : forms) {
			orderItemList.add(ConversionUtil.convertOrderItemForm(f, orderPojo, productService.get(f.getBarcode())));
		}
		int orderId = orderPojo.getId();
		orderService.createOrder(orderItemList, orderId);
		double total = orderService.billTotal(orderId);
		return ConversionUtil.convertOrderPojo(orderPojo, total);
	}

	@ApiOperation(value = "Get Order Item details record by id")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.GET)
	public OrderItemData get(@PathVariable int id) throws ApiException {
		OrderItemPojo orderItemPojo = orderService.get(id);
		ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
		OrderPojo orderPojo = orderService.getOrder(orderItemPojo.getOrderId());
		return ConversionUtil.convert(orderItemPojo, productPojo, orderPojo);
	}

	@ApiOperation(value = "Get list of all Order Items")
	@RequestMapping(path = "/api/order/item", method = RequestMethod.GET)
	public List<OrderItemData> getAll() throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderService.getAll();
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productService.get(orderItemPojo.getProductId());
			OrderPojo orderPojo = orderService.getOrder(orderItemPojo.getOrderId());
			d.add(ConversionUtil.convert(orderItemPojo, productPojo, orderPojo));
		}
		return d;
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
			d.add(ConversionUtil.convert(orderItemPojo, productPojo, orderPojo));
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
		OrderItemPojo ex = orderService.get(id);
		OrderPojo orderPojo = orderService.getOrder(ex.getOrderId());
		OrderItemPojo orderItemPojo = ConversionUtil.convertOrderItemForm(f, orderPojo, productPojo);
		orderService.update(id, orderItemPojo);
	}
}