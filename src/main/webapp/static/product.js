var no = 0;
function getProductUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	console.log(baseUrl);
	return baseUrl + "/api/product";
}

function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/brand";
}

// BUTTON ACTIONS
function addProduct(event) {

	// Set the values to update
	var $form = $("#product-add-form");
	var json = toJson($form);
	var url = getProductUrl();
	console.log(json);
	$.ajax({
		url : url,
		type : 'POST',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Product Added Successfully");
			$('#add-product-modal').modal('toggle');
			getProductList();
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});

	return false;
}

function updateProduct(event) {
	$('#edit-product-modal').modal('toggle');
	// Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	// Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
		url : url,
		type : 'PUT',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Product Updated Successfully");
			getProductList();
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});

	return false;
}

function getProductList() {
	var url = getProductUrl();
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayProductList(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

// FILE UPLOAD METHODS

var fileData = [];
var errorData = [];
var processCount = 0;

function processData() {
	var file = $('#productFile')[0].files[0];
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
	console.log(row);
	processCount++;

	var json = JSON.stringify(row);
	var url = getProductUrl();

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

function displayProductList(data) {
	console.log('Printing Product data');
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" onclick="displayEditProduct('
				+ e.id + ')"><i class="fas fa-pencil-alt"></i></button>'
		console.log('brand');
		var row = '<tr>' + '<td>' + ++no + '</td>'+ '<td>' + e.name + '</td>' + '<td>' + e.brand
				+ '</td>' + '<td>' + e.category + '</td>' + '<td>' + e.barcode
				+ '</td>' + '<td>' + parseFloat(e.mrp).toFixed(2) + '</td>'
				+ '<td>' + buttonHtml + '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayEditProduct(id) {
	var url = getProductUrl() + "/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayProduct(data);
		},
		error : handleAjaxError
	});
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
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
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData() {
	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function displayAddData() {
	$("#product-add-form").trigger('reset');
	$('#add-product-modal').modal('toggle');
}

function displayProduct(data) {
	$("#product-edit-form select[name=brandId]").val(data.brandId);
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function addDataToBrandCategoryDropdown(data, formId) {
	var $brand = $(`${formId} select[name=brandId]`);
	$brand.empty();
	$brand.append('<option value="" disabled selected>Select Brand Category</option>');
	for ( var i in data) {
		var e = data[i];
		var option = '<option value="' + e.id + '">' + e.brand + "-"
				+ e.category + "</option>";
		$brand.append(option);
	}
}

function populateBrandCategoryDropDown() {
	var url = getBrandUrl();
	$.ajax({
		url : url,
		type : "GET",
		success : function(data) {
			addDataToBrandCategoryDropdown(data, "#product-add-form");
			addDataToBrandCategoryDropdown(data, "#product-edit-form");
		},
		error : handleAjaxError,
	});
}

// INITIALIZATION CODE
function init() {
	$('#submit-add-product').click(addProduct);
	$('#add-product').click(displayAddData);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#download-errors').click(downloadErrors);
	$('#process-data').click(processData);
	$('#productFile').on('change', updateFileName);
}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(populateBrandCategoryDropDown);