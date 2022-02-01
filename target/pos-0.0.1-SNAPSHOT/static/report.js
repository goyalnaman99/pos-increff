function getSalesReportUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/reports/sales";
}

function getBrandCategoryUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/brand";
}

function filterSalesReport(e) {
	e.preventDefault();
	getSalesReport();
}

function getSalesReport() {
	var $form = $("#filter-form");
	var json = toJson($form);
	var url = getSalesReportUrl();
	$.get(url, JSON.parse(json), function(returnedData) {
		displaySalesReportList(returnedData);
	}).fail(function(response) {
		handleAjaxError(response);
	});
}

// UI DISPLAY METHODS
function displaySalesReportList(data) {
	var $tbody = $("#sales-report-table").find("tbody");
	$tbody.empty();
	var no = 0;
	for ( var i in data) {
		var e = data[i];
		var row = "<tr>" + "<td>" + ++no + "</td>" + "<td>" + e.brand + "</td>" + "<td>" + e.category + "</td>" + "<td>" + e.quantity
				+ "</td>" + "<td>" + e.revenue + "</td>" + "</tr>";
		$tbody.append(row);
	}
}

function addDataToBrandCategoryDropdown(data, formId) {
	var $brand = $(`${formId} select[name=brand]`);
	$brand.empty();
	for ( var i in data) {
		var e = data[i]
		var option = '<option value="' + e.brand + '">' + e.brand + "</option>";
		$brand.append(option);
	}
}

function populateBrandCategoryDropDown() {
	var url = getBrandCategoryUrl();
	$.ajax({
		url : url,
		type : "GET",
		success : function(data) {
			addDataToBrandCategoryDropdown(data, "#filter-form");
			getSalesReport();
		},
		error : handleAjaxError,
	});
}

// INITIALIZATION CODE
function init() {
	$("#refresh-data").click(getSalesReport);
	$('#startingdatepicker').datepicker({
		uiLibrary : 'bootstrap4',
		showOnFocus : true,
		showRightIcon : true,
		maxDate : function() {
			return $("#endingdatepicker").val();
		}
	});
	$('#endingdatepicker').datepicker(
			{
				uiLibrary : 'bootstrap4',
				showOnFocus : true,
				showRightIcon : true,
				minDate : function() {
					return $("#startingdatepicker").val();
				},
				maxDate : function() {
					var date = new Date();
					date.setDate(date.getDate());
					return new Date(date.getFullYear(), date.getMonth(), date
							.getDate());
					console.log(date);
				}
			});
	// Set ending date as today's date
	$("#endingdatepicker").datepicker().value(new Date().toLocaleDateString());
	// Set starting date as 30 days before today's date
	$("#startingdatepicker").datepicker().value(
			new Date(new Date().setDate(new Date().getDate() - 30))
					.toLocaleDateString());
	$("#filter-form").submit(filterSalesReport);
	$("#export").click(function() {
		$("#sales-report-table").tableToCSV();
	});
}

$(document).ready(init);
$(document).ready(populateBrandCategoryDropDown);