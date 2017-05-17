var attrArray = new Array();
$( document ).ready(function() {
	var url = window.location;
	
	$("#set_target").click(function(event) {
		event.preventDefault();
		var p = $("#set_value").val();
		setajax(p);
	});
	
	function setajax(p) {
		postData = {
			item : p
		};
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/setTarget",
			data : JSON.stringify(postData),
			dataType : 'json',
			success : function(result) {
				if (result.status == "Done") {
					$("#target_list").html("<ul>"+"<li>"+result.data+"</li>"+"</ul>");
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
	
	// initailize bayes network
//	$("#getBtn0").click(function(event) {
//		event.preventDefault();
//		getBnGraph();
//	});
	
	getBnGraph();
	
	function getBnGraph() {
		$.ajax({
			type : "GET",
			url : url + "/api/job/getGraph",
			success : function(result) {
				if (result.status == "Done") {
					$.each(result.vertices, function(i, attr){
//						window.attrArray.push(attr);
						$('#attrList2 .list-group').append('<option value="' + attr.key + '">' + attr.key + '</option>');
					});
					init(result.vertices, result.edges);
				}
				else {
					$("#myDiagramDiv0").html("<strong>Error</strong>");
					console.log("Fail: ", result);
				}
			},
			error : function(e) {
				$("#getResultDiv0").html("<string>Error</strong>");
				console.log("ERROR: ", e);
			}
		});
	}
	
	  // This variation on ForceDirectedLayout does not move any selected
		// Nodes
	  // but does move all other nodes (vertexes).
	  function ContinuousForceDirectedLayout() {
	    go.ForceDirectedLayout.call(this);
	    this._isObserving = false;
	  }
	  go.Diagram.inherit(ContinuousForceDirectedLayout, go.ForceDirectedLayout);

	  /** @override */
	  ContinuousForceDirectedLayout.prototype.isFixed = function(v) {
	    return v.node.isSelected;
	  }

	  // optimization: reuse the ForceDirectedNetwork rather than re-create it
		// each time
	  /** @override */
	  ContinuousForceDirectedLayout.prototype.doLayout = function(coll) {
	    if (!this._isObserving) {
	      this._isObserving = true;
	      // cacheing the network means we need to recreate it if nodes or
			// links have been added or removed or relinked,
	      // so we need to track structural model changes to discard the saved
			// network.
	      var lay = this;
	      this.diagram.addModelChangedListener(function (e) {
	        // modelChanges include a few cases that we don't actually care
			// about, such as
	        // "nodeCategory" or "linkToPortId", but we'll go ahead and recreate
			// the network anyway.
	        // Also clear the network when replacing the model.
	        if (e.modelChange !== "" ||
	            (e.change === go.ChangedEvent.Transaction && e.propertyName === "StartingFirstTransaction")) {
	          lay.network = null;
	        }
	      });
	    }
	    var net = this.network;
	    if (net === null) {  // the first time, just create the network as
								// normal
	      this.network = net = this.makeNetwork(coll);
	    } else {  // but on reuse we need to update the LayoutVertex.bounds
					// for selected nodes
	      this.diagram.nodes.each(function (n) {
	        var v = net.findVertex(n);
	        if (v !== null) v.bounds = n.actualBounds;
	      });
	    }
	    // now perform the normal layout
	    go.ForceDirectedLayout.prototype.doLayout.call(this, coll);
	    // doLayout normally discards the LayoutNetwork by setting
		// Layout.network to null;
	    // here we remember it for next time
	    this.network = net;
	  }
	  // end ContinuousForceDirectedLayout

	function init(nodeDataArray,linkDataArray) {
		var $ = go.GraphObject.make;  // for conciseness in defining templates
		
		myDiagram =
			$(go.Diagram, "myDiagramDiv0",  // create a Diagram for the DIV HTML
											// element
			{
				 initialAutoScale: go.Diagram.Uniform,  // an initial automatic
														// zoom-to-fit
		          contentAlignment: go.Spot.Center,  // align document to the
														// center of the
														// viewport
		          layout:
		            $(ContinuousForceDirectedLayout,  // automatically spread
														// nodes apart while
														// dragging
		              { defaultSpringLength: 30, defaultElectricalCharge: 100 }),
		          // do an extra layout at the end of a move
		          "SelectionMoved": function(e) { e.diagram.layout.invalidateLayout(); }
			 });
		
		myDiagram.toolManager.draggingTool.doMouseMove = function() {
		    go.DraggingTool.prototype.doMouseMove.call(this);
		    if (this.isActive) { this.diagram.layout.invalidateLayout(); }
		}
		
			    // These nodes have text surrounded by a rounded rectangle
			    // whose fill color is bound to the node data.
			    // The user can drag a node by dragging its TextBlock label.
			    // Dragging from the Shape will start drawing a new link.
	    myDiagram.nodeTemplate =
		    $(go.Node, "Auto",  // the whole node panel define the node's outer shape, which will surround the TextBlock
			  $(go.Shape, "Circle",
			    { fill: "CornflowerBlue", stroke: "black", spot1: new go.Spot(0, 0, 5, 5), spot2: new go.Spot(1, 1, -5, -5) }),
			  $(go.TextBlock,
			    { font: "bold 10pt helvetica, bold arial, sans-serif", textAlign: "center", maxSize: new go.Size(100, NaN) },
			    new go.Binding("text", "key")),
			    {
			        click: function(e, obj) { showMessage(obj.part.data.key); },
			        selectionChanged: function(part) {
				    	var shape = part.elt(0);
				    	shape.fill = part.isSelected ? "red" : "CornflowerBlue";
			        }
			    }
		    );
		
			    // The link shape and arrowhead have their stroke brush data
				// bound to the "color" property
			myDiagram.linkTemplate =
			    $(go.Link,  // the whole link panel
			      $(go.Shape,  // the link shape
			    	{ stroke: "black" }),
			      $(go.Shape,  // the arrowhead
			    	{ toArrow: "standard", stroke: null })
			     );
			    myDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);
	}
	
	function reload() {
	    //myDiagram.layout.network = null;
	    var text = myDiagram.model.toJson();
	    myDiagram.model = go.Model.fromJson(text);
	    //myDiagram.layout =
	    //  go.GraphObject.make(ContinuousForceDirectedLayout,  // automatically spread nodes apart while dragging
	    //    { defaultSpringLength: 30, defaultElectricalCharge: 100 });
	}
	
	function showMessage(s) {
		ajaxPearson(s);
		ajaxHistoryData(s);
	}

	// Get Pearson
	
	function ajaxPearson(p) {
		postData = {
			item : p
		};
		var attr = [];
		var values = [];
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/getPearson",
			data : JSON.stringify(postData),
			dataType : 'json',
			
			success: function(result) {
				if (result.status == "Done") {
					$('#pearsonList1 .list-group li').remove();
					$.each(result.data.first, function(i, pair){
						attr.push(pair.first);
						values.push(pair.second);
						var row = "(" + i +") " + pair.first + ": " + pair.second;
						$('#pearsonList1 .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
					});
					$('#pearsonList2 .list-group li').remove();
					$.each(result.data.second, function(i, pair){
						attr.push(pair.first);
						values.push(pair.second);
						var row = "(" + i +") " + pair.first + ": " + pair.second;
						$('#pearsonList2 .list-group').append('<li><h4 class="list-group-item">'+row+'</h4></li>');
					});
//					alert(attr);
//					alert(values);
					drawMyChart(attr, values);
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
	
	function ajaxHistoryData(p) {
		postData = {
			item : p
		};
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/getHistoryData",
			data : JSON.stringify(postData),
			dataType : 'json',
			
			success: function(result) {
				if (result.status == "Done") {
//					alert(result.data.first);
					drawHistoryChart(result.data.first, result.data.second);
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
	
	// draw my chart pearson
	function drawMyChart(attr, values) {
		var myChart = echarts.init(document.getElementById('main'));
		
		var option = {
			title: {
				text: 'Pearson Correlation'
			},
				
			toolbox: {
				show: true,
				feature: {
					dataView: {
						show: true
					},
					restore: {
						show: true
					},
					dataZoom: {
						show: true
					},
//					saveAsImage: {
//						show: true
//					},
//					magicType: {
//						type: ['line', 'bar']
//					}
				},
				
			},
				
			xAxis: {
				data: attr//["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
			},
				
			yAxis: {
				'min':-1,
				'max':1
           	},
				
			series: [{
				name: 'Pearson Correlation',
				type: 'bar',
				data: values//[5, 20, 36, 10, 10, 20]
			}]
		};
		
		myChart.setOption(option);
	}
	
	// draw my chart history
	function drawHistoryChart(data_x, data_y) {
		var myChart = echarts.init(document.getElementById('history'));
		
		var option = {
			title: {
				text: 'History Data'
			},
				
			toolbox: {
				show: true,
				feature: {
					dataView: {
						show: true
					},
					restore: {
						show: true
					},
					dataZoom: {
						show: true
					},
				},
				
			},
			
			xAxis: {
				data: data_x
			},
				
			yAxis: {},
				
			series: [{
				name: 'History Data',
				type: 'line',
				data: data_y
			}]
		};
		myChart.setOption(option);
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
					console.log("Fail: ", result);
				}
			},
			error : function(e) {
				console.log("ERROR: ", e);
			}
		});
	}
	
	$("#postInferBnt").click(function(event) {
		event.preventDefault();
		postInfer();
	});
	
	function postInfer() {
		var list = new Array();
		// alert(window.attrArray.length);
		for (var i = 0; i < window.attrArray.length; i++) {
			var tmp = $("#"+window.attrArray[i]+"_inf").val();
			// alert(tmp);
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
		// alert(jsonData2.infer);
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/postInfer",
			data : JSON.stringify(list),
			dataType : 'json',
			success : function(result) {
				if (result.status == "Done") {
					$("#relief").html("<strong>html报警概率：" + result.data + "</strong>");
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
	
	$("#predictBnt").click(function(event) {
		event.preventDefault();
		predictAjax();
	});
	
	function predictAjax() {
		$.ajax({
			type : "Get",
			url : url+"/api/job/TSP",
			success : function(result) {
				if (result.status == "Done") {
					$("#predictDiv").html("<strong>html报警预测：" + result.data + "</strong>")
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
})