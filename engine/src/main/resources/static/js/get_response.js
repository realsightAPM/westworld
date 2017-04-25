$( document ).ready(function() {
	var url = window.location;
	
	// GET REQUEST
	$("#getBtn").click(function(event) {
		event.preventDefault();
		ajaxGet();
	});
	
	function ajaxGet() {
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
					$("#getResultDiv").html("<strong>Error</strong>");
					console.log("Fail: ", result);
				}
			},
			error : function(e) {
				$("#getResultDiv").html("<string>Error</strong>");
				console.log("ERROR: ", e);
			}
		});
	}
})