function getOrderUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}

// BUTTON ACTIONS
var order = [];
function addOrderItem(event) {
	event.preventDefault();
	var $form = $("#order-add-form");
	var json = toJson($form);
	var orderItem = JSON.parse(json);
	
	if(orderItem.quantity > 0 && orderItem.sellingPrice >= 0)
	{
		order.push(orderItem);
		toastr.success("Order Item Added to Cart");
		$("#order-add-form").trigger("reset");
		console.log(order);
		displayOrderItemListAdd(order);
	}
	else if(orderItem.quantity <= 0) {
		toastr.error("Quantity should be positive", "", {
    	"closeButton" : true,
    	"timeOut" : 0,
    	"extentedTimeOut" : 0
    });}
	else if(orderItem.sellingPrice < 0) {
		toastr.error("Selling Price should be 0 or more", "", {
    	"closeButton" : true,
    	"timeOut" : 0,
    	"extentedTimeOut" : 0
    });}
}
function cancelOrder(event) {
	order = [];
	toastr.warning("Cart Cleared");
	$("#order-add-form").trigger("reset");
	$("#orderitemadd-table tbody").remove();
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
			$("#order-add-form").trigger("reset");
			$('#add-order-modal').modal('hide');
			$("#orderitemadd-table tbody").empty();
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});
	return false;
}

function updateOrderItem(event) {
	// Get the ID
	var id = $("#order-edit-form input[name=id]").val();
	var orderId = $("#order-edit-form input[name=orderId]").val();
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
			$('#edit-orderitem-modal').modal('hide');
			toastr.success("Changes Implemented Successfully");
			getOrderItemList(orderId);
			getOrderList();
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});

	return false;
}

function getInvoice(id) {
    var url = getOrderUrl() + "/invoice/" + id;
    console.log(url);
    $.ajax({
        url: url,
        type: 'GET',
        responseType: 'arraybuffer',
        success: function (response) {
            var arrayBuffer = base64ToArrayBuffer(response); // data is the
																// base64
																// encoded
																// string
            function base64ToArrayBuffer(base64) {
                var binaryString = window.atob(base64);
                var binaryLen = binaryString.length;
                var bytes = new Uint8Array(binaryLen);
                for (var i = 0; i < binaryLen; i++) {
                    bytes[i] = binaryString.charCodeAt(i);
                }
                return bytes;
            }

            var blob = new Blob([arrayBuffer], {type: "application/pdf"});
            var link = window.URL.createObjectURL(blob);
            window.open(link, '');
        },
        error: handleAjaxError
    })
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
			handleAjaxError(response)
		}
	});
}

function deleteRow(data) {
	console.log(data);
	order = order.filter((item) => {
		return item.barcode !== data;
	});
	displayOrderItemListAdd(order);
}

// UI DISPLAY METHODS

function displayOrderList(data) {
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn btn-sm btn-outline-primary mx-1" onclick="getOrderItemList('
				+ e.id + ')">View</button>'
			buttonHtml += '<button type="button" class="btn btn-sm btn-outline-primary mx-1" onclick="getInvoice('
                            				+ e.id + ')">Download Invoice</button>'
		var row = '<tr>' + '<td>' + e.id + '</td>' + '<td>' + e.datetime + '</td>'+ '<td>' + e.billAmount + '</td>' + '<td>' + buttonHtml
				+ '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayOrderItem(data) {
	var $tbody = $('#orderitem-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" data-toggle="tooltip" title="Delete" onclick="deleteOrderItem('
				+ e.id + ')"><i class="fas fa-trash-alt"></i></button>'
		buttonHtml += ' <button type="button" class="btn text-bodye" data-toggle="tooltip" title="Edit" onclick="displayEditOrderItem('
				+ e.id + ')"><i class="fas fa-pencil-alt"></i></button>'
		var row = '<tr>' + '<td>' + e.name + '</td>' + '<td>' + e.barcode
				+ '</td>' + '<td>' + e.quantity + '</td>' + '<td>'
				+ e.sellingPrice + '</td>' + '<td>' + buttonHtml + '</td>'
				+ '</tr>';
		$tbody.append(row);
	}
	$('#order-display-modal').modal('toggle');
}

function displayOrderItemListAdd(data) {
	var $tbody = $('#orderitemadd-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = '<button type="button" class="btn text-bodye" data-toggle="tooltip" title="Delete" onclick="deleteRow(\'' + e.barcode + '\')"><i class="fas fa-trash-alt"></i></button>'
		var row = '<tr>' + '<td>' + e.barcode + '</td>' + '<td>' + e.quantity
				+ '</td>' + '<td>' + e.sellingPrice + '</td>'+ '<td>' + buttonHtml + '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayEditOrderItem(id) {
	$('#order-display-modal').modal('hide');
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

function displayOrderItemEdit(data) {
	console.log(data);
	$("#order-edit-form input[name=barcode]").val(data.barcode);
	$("#barcode").html("" + data.barcode);
	$("#mrp").html("" + data.mrp);
	$("#availableQuantity").html("" + data.availableQuantity);
	$("#order-edit-form input[name=quantity]").val(data.quantity);
	$("#order-edit-form input[name=sellingPrice]").val(data.mrp);
	$("#order-edit-form input[name=id]").val(data.id);
	$("#order-edit-form input[name=orderId]").val(data.orderId);
	$('#edit-orderitem-modal').modal('toggle');
}

// INITIALIZATION CODE
function init() {
	$('#order-add-form').submit(addOrderItem)
	$('#order-edit-form').submit(updateOrderItem);
	$('#cancel-order').click(cancelOrder)
	$('#submit-order').click(addOrder)
	$('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(getOrderList);
