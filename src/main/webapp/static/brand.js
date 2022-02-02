function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

// BUTTON ACTIONS
function addBrand(event) {
	// Set the values to update
	event.preventDefault();
	var $form = $("#brand-add-form");
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
			$('#add-brand-modal').modal('toggle');
			getBrandList();
		},
		error : function(response) {
			handleAjaxError(response)
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
			handleAjaxError(response)
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
	uploadRows();
}

function uploadRows() {
	// Update progress
	updateUploadDialog();
	// If everything processed then return
	if (processCount == fileData.length && processCount > 0) {
		if (errorData.length == 0) {
			toastr.success("File uploaded successfully");
		} else {
			toastr.options.closeButton = true;
	        toastr.options.timeOut = 0;
	        toastr.options.extendedTimeOut = 0;
			toastr.error("Uploaded with " + errorData.length + " errors");
		}
		getBrandList();
		$("#error-data").show();
		if (errorData.length == 0) {
			$("#download-errors").hide();
		}
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
		},
		error : function(response) {
			row.error = JSON.parse(response.responseText).message
			errorData.push(row);
			uploadRows();
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
	var no = 0;
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn text-bodye" data-toggle="tooltip" title="Edit" onclick="displayEditBrand('
				+ e.id + ')"><i class="fas fa-pencil-alt"></i></button>'
		var row = '<tr>' + '<td>' + ++no + '</td>' + '<td>' + e.brand + '</td>'
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

function displayAddData() {
	$("#brand-add-form").trigger('reset');
	$('#add-brand-modal').modal('toggle');
}

function displayBrand(data) {
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

// INITIALIZATION CODE
function init() {
	$('#add-brand').click(displayAddData);
	$('#brand-add-form').submit(addBrand);
	$('#brand-edit-form').submit(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$("#error-data").hide();
	$('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);
