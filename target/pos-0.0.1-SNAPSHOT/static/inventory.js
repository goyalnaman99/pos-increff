function getInventoryUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

// BUTTON ACTIONS
function addInventory(event) {
	// Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();
	console.log(json);
	$.ajax({
		url : url,
		type : 'POST',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Inventory Added Successfully");
			getInventoryList();
		},
		error : function(response){
			handleAjaxError
			toastr.error("Error Adding Inventory");
		}
	});

	return false;
} //datavalueobject

function updateInventory(event) {
	$('#edit-inventory-modal').modal('toggle');
	// Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;

	// Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
		url : url,
		type : 'PUT',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Inventory Updated Successfully");
			getInventoryList();
		},
		error : function(response){
			handleAjaxError
			toastr.error("Error Updating Inventory");
		}
	});

	return false;
}

function getInventoryList() {
	console.log("getting inventory list");
	var url = getInventoryUrl();
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayInventoryList(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

function deleteInventory(id) {
	var url = getInventoryUrl() + "/" + id;

	$.ajax({
		url : url,
		type : 'DELETE',
		success : function(data) {
			getInventoryList();
		},
		error : handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function processData() {
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results) {
	fileData = results.data;
	uploadRows();
}

function uploadRows() {
	// Update progress
	updateUploadDialog();
	// If everything processed then return
	if (processCount == fileData.length) {
		return;
	}

	// Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getInventoryUrl();

	// Make ajax call
	$.ajax({
		url : url,
		type : 'POST',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			uploadRows();
			toastr.success("File Uploaded Successfully");
		},
		error : function(response) {
			row.error = response.responseText
			errorData.push(row);
			uploadRows();
			toastr.error("Error Uploading File");
		}
	});

}

function downloadErrors() {
	writeFileData(errorData);
}

// UI DISPLAY METHODS

function displayInventoryList(data) {
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" onclick="displayEditInventory(' + e.id
				+ ')"><i class="fas fa-edit"></button>'
		var row = '<tr>' + '<td>' + e.barcode + '</td>'
				+ '<td>' + e.name + '</td>' + '<td>' + e.quantity + '</td>' + '<td>' + buttonHtml + '</td>'
				+ '</tr>';
		$tbody.append(row);
	}
}

function displayEditInventory(id) {
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayInventory(data);
		},
		error : handleAjaxError
	});
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	// Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	// Update counts
	updateUploadDialog();
}

function updateUploadDialog() {
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName() {
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData() {
	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data) {
	$("#inventory-edit-form input[name=id]").val(data.id);
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=name]").val(data.name);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}

// INITIALIZATION CODE
function init() {
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);
