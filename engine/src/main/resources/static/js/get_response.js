var attrArray = new Array();
$( document ).ready(function() {
	var url = window.location;
	
	ajaxAttr();
	
	function ajaxAttr() {
		$.ajax({
			type : "GET",
			url : url + "/api/job/getAttr",
			success : function(result) {
				if (result.status == "Done") {
					
					$.each(result.data, function(i, attr){
						window.attrArray.push(attr);
						var row = attr+"       <input type=\"text\" id="+attr+"_inf"+" class=\"form-control\"/>";
						$('#attrList .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
						$('#attrList2 .list-group').append('<option value="' + attr + '">' + attr + '</option>');
					});
//					alert(window.attrArray);
					console.log("Success: ", result);
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
	
	
	// GET REQUEST
	$("#getBtn").click(function(event) {
		event.preventDefault();
		ajaxGraph();
	});
	
	function ajaxGraph() {
		$.ajax({
			type : "GET",
			url : url + "/api/job/getGraph",
			success : function(result) {
				if (result.status == "Done") {
//					$('#getResultDiv .list-group li').remove();
//					var edgeList = "";
//					$.each(result.edges, function(i, edge){
//						var edge = "Edge " + i +": {start: " + edge.start + " ,end: " + edge.end + "}";
//						$('#getResultDiv .list-group').append('<li><h4 class="list-group-item">'+edge+'</h4></li>');
//						
//					});
					var $ = go.GraphObject.make;


					var diagram = new go.Diagram("myDiagramDiv");
					diagram.initialContentAlignment = go.Spot.Center;

//					var nodeDataArray = [
//						{ key: "Alpha" },
//						{key: "Beta" }
//					];
//					var linkDataArray = [
//						{from: "Alpha", to: "Beta"}
//					];
					diagram.model = new go.GraphLinksModel(result.vertices, result.edges);
					
					console.log("Success: ", result);
				}
				else {
					$("#myDiagramDiv").html("<strong>Error</strong>");
					console.log("Fail: ", result);
				}
			},
			error : function(e) {
				$("#getResultDiv").html("<string>Error</strong>");
				console.log("ERROR: ", e);
			}
		});
	}
	
	// Get Pearson
	$("#PearsonBtn").click(function(event) {
		event.preventDefault();
		ajaxPearson();
	});
	
	function ajaxPearson() {
		$.ajax({
			type: "Get",
			url: url + "/api/job/getPearson",
			success: function(result) {
				if (result.status == "Done") {
					$.each(result.data.first, function(i, pair){
						var row = "(" + i +") " + pair.first + ": " + pair.second;
						$('#pearsonList1 .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
					});
					
					$.each(result.data.second, function(i, pair){
						var row = "(" + i +") " + pair.first + ": " + pair.second;
						$('#pearsonList2 .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
					});
					
					console.log("Success: ", result);
				}
				else {
	//				$("#myDiagramDiv").html("<strong>Error</strong>");
					console.log("Fail: ", result);
				}
			}
		});
	}
	
	// Get Cause
	$("#CauseBtn").click(function () {
		event.preventDefault();
		ajaxCause();
	});
	
	function ajaxCause() {
		$.ajax({
			type: "Get",
			url: url + "/api/job/getCause",
			success: function(result) {
				if (result.status == "Done") {
					$.each(result.data, function(i, pair){
						var row = "(" + i +") " + pair.first + ": " + pair.second;
						$('#causeList .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
					});
					
					console.log("Success: ", result);
				}
				else {
	//				$("#myDiagramDiv").html("<strong>Error</strong>");
					console.log("Fail: ", result);
				}
			}
		});
	}
	
	$("#postInferBnt").click(function(event) {
		event.preventDefault();
		postInfer();
	});
	
	function postInfer() {
		var list = new Array();
//		alert(window.attrArray.length);
		for (var i = 0; i < window.attrArray.length; i++) {
			var tmp = $("#"+window.attrArray[i]+"_inf").val();
//			alert(tmp);
			var str = window.attrArray[i]+":"+tmp;
			var jsonData = {
				infer : str
			};
			list.push(jsonData);
		}
		
		var tmp2 = $("#select_value").val();
		var jsonData2 = {
			infer : tmp2
		};
		list.push(jsonData2);
//		alert(jsonData2.infer);
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/postInfer",
			data : JSON.stringify(list),
			dataType : 'json',
			success : function(result) {
				if (result.status == "Done") {
					$("#relief").html("<strong>" + result.data + "</strong>");
				}
				else {
					alert("错误");
					console.log("Fail: ", result);
				}
				console.log(result);
			},
			error : function(e) {
				alert("ERROR");
				console.log("ERROR: ", e);
			}
		});
	}
	
	
})