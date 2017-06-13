$( document ).ready(function() {
	var url = window.location;
	ajaxLogPearson();
	function ajaxLogPearson() {
//		alert(url);
		$.ajax({
			type : "GET",
			url : url + "/api/job/logList",
			success : function(result) {
				if (result.status == "Done") {
					$.each(result.data, function(i, pair) {
						$("#logList .list-group").append("<li><h4 class='list-group-item'>("+i+")"+pair.first+": "+pair.second + "</h4></li>");
					});
				}
				else {
					console.log("Fail: ", result);
				}
			},
			error : function(e) {
				console.log("ERROR: ", e);
			}
		});
	}
})