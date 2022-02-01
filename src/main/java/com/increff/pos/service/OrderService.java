package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private ProductService productService;

	// Creating order
	@Transactional(rollbackFor = ApiException.class)
	public int createOrder(List<OrderItemPojo> orderItemList) throws ApiException {
		if(orderItemList.size() == 0){
			throw new ApiException("Order List cannot be empty");
		}
		OrderPojo orderPojo = new OrderPojo();
		orderPojo.setDate(new Date());
		orderDao.insert(orderPojo);
		for (OrderItemPojo p : orderItemList) {
			ProductPojo productPojo = productService.get(p.getProductId());
			if (productPojo == null) {
				throw new ApiException("Product with barcode: " + productPojo.getBarcode() + " does not exist.");
			}
			validate(p);
			p.setOrderId(orderPojo.getId());
			orderItemDao.insert(p);
			updateInventory(p);
		}
		return orderPojo.getId();
	}

	// Fetching an Order item by id
	@Transactional
	public OrderItemPojo get(int id) throws ApiException {
		OrderItemPojo p = checkIfExists(id);
		return p;
	}

	// Fetching an Order by id
	@Transactional
	public OrderPojo getOrder(int id) throws ApiException {
		OrderPojo p = checkIfExistsOrder(id);
		return p;
	}

	// Fetch all order items of a particular order
	@Transactional
	public List<OrderItemPojo> getOrderItems(int order_id) throws ApiException {
		List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
		return lis;
	}

	// Calculate Total
	public double billTotal(int orderId) {
		double total = 0;
		List<OrderItemPojo> lis = orderItemDao.selectOrder(orderId);
		for (OrderItemPojo orderItemPojo : lis) {
			total += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
		}
		return total;
	}

	// Fetching all order items
	@Transactional
	public List<OrderItemPojo> getAll() {
		return orderItemDao.selectAll();
	}

	// Fetching all orders
	@Transactional
	public List<OrderPojo> getAllOrders() {
		return orderDao.selectAll();
	}

	// Deletion of order item
	@Transactional
	public void delete(int id) throws ApiException {
		int order_id = orderItemDao.select(OrderItemPojo.class, id).getOrderId();
		increaseInventory(orderItemDao.select(OrderItemPojo.class, id));
		orderItemDao.delete(OrderItemPojo.class, id);
		List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
		if (lis.isEmpty()) {
			orderDao.delete(OrderPojo.class, order_id);
		}
	}

	// Deletion of order
	@Transactional
	public void deleteOrder(int orderId) throws ApiException {
		List<OrderItemPojo> orderItemList = getOrderItems(orderId);
		for (OrderItemPojo orderItemPojo : orderItemList) {
			increaseInventory(orderItemPojo);
			orderItemDao.delete(OrderItemPojo.class, orderItemPojo.getId());
		}
		orderDao.delete(OrderPojo.class, orderId);
	}

	// Updating order item
	@Transactional(rollbackFor = ApiException.class)
	public void update(int id, OrderItemPojo p) throws ApiException {
		validate(p);
		OrderItemPojo ex = checkIfExists(id);
		increaseInventory(ex);
		ex.setQuantity(p.getQuantity());
		ex.setProductId(p.getProductId());
		ex.setSellingPrice(p.getSellingPrice());
		orderItemDao.update(ex);
		updateInventory(ex);
	}

	// Increasing inventory while deleting order
	private void increaseInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryService.getByProductId(orderItemPojo.getProductId());
		int updatedQuantity = inventoryPojo.getQuantity() + orderItemPojo.getQuantity();
		inventoryPojo.setQuantity(updatedQuantity);
		inventoryService.update(inventoryPojo.getId(), inventoryPojo);
	}

	// To update inventory while adding order
	private void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryService.getByProductId(orderItemPojo.getProductId());
		int updatedQuantity = inventoryPojo.getQuantity() - orderItemPojo.getQuantity();
		inventoryPojo.setQuantity(updatedQuantity);
		inventoryService.update(inventoryPojo.getId(), inventoryPojo);
	}

	// Checking if a particular order item exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderItemPojo checkIfExists(int id) throws ApiException {
		OrderItemPojo p = orderItemDao.select(OrderItemPojo.class, id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Checking if a particular order exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderPojo checkIfExistsOrder(int id) throws ApiException {
		OrderPojo p = orderDao.select(OrderPojo.class, id);
		if (p == null) {
			throw new ApiException("Order with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Validation of order item
	private void validate(OrderItemPojo p) throws ApiException {
		if (p.getQuantity() <= 0) {
			throw new ApiException("Quantity must be positive");
		} else if (inventoryService.getByProductId(p.getProductId()).getQuantity() < p.getQuantity()) {
			throw new ApiException("Available quantity for this product is: " + inventoryService.getByProductId(p.getProductId()).getQuantity());
		}
		
		if(p.getSellingPrice() > p.getMrp())
		{
			throw new ApiException("SP can't be greater than MRP: " + p.getMrp());
		}
	}

	public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
		return orderDao.selectBetweenDate(startingDate, endingDate);
	}
}