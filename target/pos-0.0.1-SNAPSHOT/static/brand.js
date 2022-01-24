function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

// BUTTON ACTIONS
function addBrand(event) {
	// Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();
	console.log(json);
	$.ajax({
		url : url,
		type : 'POST',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			toastr.success("Brand Added Successfully");
			getBrandList();
		},
		error : function(response) {
			handleAjaxError
			toastr.error("Error adding brand/category pair");
		}
	});

	return false;
}

function updateBrand(event) {
	$('#edit-brand-modal').modal('toggle');
	// Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	// Set the values to update
	var $form = $("#brand-edit-form");
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

function getBrandList() {
	console.log("getting brand list");
	var url = getBrandUrl();
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayBrandList(data);
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
	var file = $('#brandFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results) {
	fileData = results.data;
	if(fileData.length > 5000)
	{
		toastr.error("File Contains more than 5000 rows!);
		return;
	}
	uploadRows();
}

function uploadRows() {
	// Update progress
	updateUploadDialog();
	// If everything processed then return
	if (processCount == fileData.length) {
		toastr.success("File processed Successfully");
		return;
	}

	// Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getBrandUrl();

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
			getBrandList();
			toastr.success("File Uploaded Successfully");
		},
		error : function(response) {
			row.error = response.responseText
			errorData.push(row);
			uploadRows();
			toastr.success("File could not be uploaded");
		}
	});

}

function downloadErrors() {
	writeFileData(errorData);
}

// UI DISPLAY METHODS

function displayBrandList(data) {
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" onclick="displayEditBrand(' + e.id
				+ ')"><i class="fas fa-edit"></i></button>'
		var row = '<tr>' + '<td>' + e.brand + '</td>'
				+ '<td>' + e.category + '</td>' + '<td>' + buttonHtml + '</td>'
				+ '</tr>';
		$tbody.append(row);
	}
}

function displayEditBrand(id) {
	var url = getBrandUrl() + "/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayBrand(data);
		},
		error : handleAjaxError
	});
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData() {
	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data) {
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

// INITIALIZATION CODE
function init() {
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);
