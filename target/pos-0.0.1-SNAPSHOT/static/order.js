function getOrderUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}

// BUTTON ACTIONS
var order = [];
function addOrderItem(event) {
	var $form = $("#order-add-form");
	var json = toJson($form);
	order.push(JSON.parse(json));
	toastr.success("Order Item Added to Cart");
	console.log(order);
	displayOrderItemListAdd(order);

}
function cancelOrder(event) {
	order = [];
	toastr.warning("Cart Cleared");
	$("#orderitemadd-table tr").remove(); 
}
function addOrder(event) {
	var url = getOrderUrl();
	console.log("submit order");
	console.log(JSON.stringify(order));
	$.ajax({
		url : url,
		type : 'POST',
		data : JSON.stringify(order),
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			order = [];
			getOrderList();
			toastr.success("Order Created Successfully");
			
		},
		error : function(response) {
			handleAjaxError
			toastr.error("Error Creating Order");
		}
	});
	$("#orderitemadd-table tr").remove(); 
	return false;
}

function updateOrderItem(event) {
	$('#edit-orderitem-modal').modal('toggle');
	// Get the ID
	var id = $("#order-edit-form input[name=id]").val();
	var url = getOrderUrl() + "/item/" + id;

	// Set the values to update
	var $form = $("#order-edit-form");
	var json = toJson($form);

	$.ajax({
		url : url,
		type : 'PUT',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Changes Implemented Successfully");
			getBrandList();
		},
		error : function(response) {
			handleAjaxError
			toastr.error("Error making changes");
		}
	});

	return false;
}


function getOrderList() {
	console.log("getting order list");
	var url = getOrderUrl();
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderList(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

function getOrderItemList(id) {
	console.log("getting order item list");
	var url = getOrderUrl() + "/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderItem(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

function deleteOrderItem(id) {
	var url = getOrderUrl() + "/item/" + id;

	$.ajax({
		url : url,
		type : 'DELETE',
		success : function(data) {
			getOrderItemList(id);
			getOrderList(id);
			toastr.success("Order Item Deleted Successfully");
		},
		error : function(response) {
			handleAjaxError
			toastr.error("Error Deleting Order Item");
		}
	});
}

function deleteOrder(id) {
	var url = getOrderUrl() + "/" + id;

	$.ajax({
		url : url,
		type : 'DELETE',
		success : function(data) {
			toastr.success("Order Deleted Successfully");
			getOrderList(id);
		},
		error : function(reponse) {
			handleAjaxError
			toastr.error("Error Deleting Order");
		}
	});
}

// UI DISPLAY METHODS

function displayOrderList(data) {
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" onclick="getOrderItemList('
				+ e.id + ')"><i class="fas fa-info-circle"></i></button>'
		buttonHtml += ' <button type="button" class="btn text-bodye" onclick="deleteOrder('
				+ e.id + ')"><i class="fas fa-trash-alt"></i></button>'
		var row = '<tr>' + '<td>' + e.datetime + '</td>' + '<td>' + buttonHtml
				+ '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayOrderItem(data) {
	var $tbody = $('#orderitem-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" onclick="deleteOrderItem('
				+ e.id + ')"><i class="fas fa-trash-alt"></i></button>'
				buttonHtml += ' <button type="button" class="btn text-bodye" onclick="displayEditOrderItem(' + e.id
				+ ')"><i class="fas fa-edit"></i></button>'
		var row = '<tr>' + '<td>' + e.barcode + '</td>' + '<td>' + e.name
				+ '</td>' + '<td>' + e.quantity + '</td>' + '<td>'
				+ e.sellingPrice + '</td>' + '<td>' + buttonHtml + '</td>'
				+ '</tr>';
		$tbody.append(row);
	}
	$('#order-display-modal').modal('toggle');
}

function displayEditOrderItem(id) {
	var url = getOrderUrl() + "/item/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderItemEdit(data);
		},
		error : handleAjaxError
	});
}

function displayOrderItemListAdd(data) {
	var $tbody = $('#orderitemadd-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var row = '<tr>' + '<td>' + e.barcode + '</td>' + '<td>' + e.quantity
				+ '</td>' + '<td>' + e.sellingPrice + '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayOrderItemEdit(data) {
	$("#order-edit-form input[name=barcode]").val(data.barcode);
	$("#brand-edit-form input[name=quantity]").val(data.quantity);
	$("#brand-edit-form input[name=sellingPrice]").val(data.sellingPrice);
	$('#edit-orderitem-modal').modal('toggle');
}
// INITIALIZATION CODE
function init() {
	$('#add-order').click(addOrderItem)
	$('#cancel-order').click(cancelOrder)
	$('#update-orderitem').click(updateOrderItem);
	$('#submit-order').click(addOrder)
	$('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(getOrderList);
