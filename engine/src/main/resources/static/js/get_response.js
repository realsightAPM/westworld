var attrArray = new Array();
var selected_var="";
var target = "";

$( document ).ready(function() {
	var url = window.location;

	$("#submit_index").click(function(event) {
		event.preventDefault();
		var p = $("#set_index").val();
		setIndexAjax(p);
	});
	
	function setIndexAjax(p) {
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
//					$("#target_list").html("<ul>"+"<li>"+result.data+"</li>"+"</ul>");
					$("#index_list").html(result.data);
					window.target=result.data;
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
	
	$("#build_net").click(function(event) {
		event.preventDefault();
		ajaxBuildNet();
	});
	 
	function ajaxBuildNet() {
		$.ajax({
			type : "GET",
			url : url + "/api/job/buildNet",
			success : function(result) {
				if (result.status == "Done") {
					alert(result.data);
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
	
	getBnGraph();
	
	function getBnGraph() {
		$.ajax({
			type : "GET",
			url : url + "/api/job/getGraph",
			success : function(result) {
				if (result.status == "Done") {
					$.each(result.vertices, function(i, attr){
//						window.attrArray.push(attr);
						$('#pre_set .list-group').append('<option value="' + attr.key + '">' + attr.key + '</option>');
					});
					init(result.vertices, result.edges);
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
	        if (e.modelChange !== "" || (e.change === go.ChangedEvent.Transaction && e.propertyName === "StartingFirstTransaction")) {
	          lay.network = null;
	        }
	      });
	    }
	    var net = this.network;
	    if (net === null) {                                  // the first time, just create the network as
						                                     // normal
	      this.network = net = this.makeNetwork(coll);
	    } else {                                             // but on reuse we need to update the LayoutVertex.bounds
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
			$(go.Diagram, "diagram_div",  // create a Diagram for the DIV HTML
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
			        click: function(e, obj) { window.selected_var=obj.part.data.key;showMessage(obj.part.data.key); },
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
		$("#perform_pie2").html("");
		ajaxPearson(s);
		ajaxHistoryData(s);
		ajaxPerform(s);
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
					$.each(result.data.first, function(i, pair){
						attr.push(pair.first);
						values.push(pair.second);
					});
					$.each(result.data.second, function(i, pair){
						attr.push(pair.first);
						values.push(pair.second);
					});
					drawRelationChart(attr, values, "relation_chart", -1, 1, "相关性");
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
	
	function ajaxOriDis(p) {
		postData = {
			item : p
		};
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/Distribute",
			data : JSON.stringify(postData),
			dataType : 'json',
			
			success : function(result) {
				if (result.status == "Done") {
					drawPieChart(result.data);
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
	
	$(document).on("click","#set_state1", function(){
		ajaxInfer($("#select_state").val());
	});
	
	function ajaxInfer(state) {
		postData = {
			item : window.selected_var+":"+state
		};
		var selected = $('#target_list').html();
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url : "/api/job/Infer",
			data : JSON.stringify(postData),
			dataType : 'json',
			success : function(result) {
				if (result.status == "Done") {
					drawPieChart(result.data, "perform_pie2", window.target+"推理分布");
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
	function drawRelationChart(attr, values, locationId, lo, hi, title_name) {
		var myChart = echarts.init(document.getElementById(locationId));
		
		var option = {
			title: {
				text: title_name
			},
			
	        tooltip: {
	            trigger: 'axis'
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
				data: attr
			},
				
			yAxis: {
				'min':lo,
				'max':hi
           	},
				
			series: [{
				name: title_name,
				type: 'bar',
				data: values
			}]
		};
		
		myChart.setOption(option);
	}
	
	// draw my chart history
	function drawHistoryChart(data_x, data_y) {
		var myChart = echarts.init(document.getElementById('history_chart'));
		
		var option = {
			title: {
				text: '历史数据'
			},
			
	        tooltip: {
	            trigger: 'axis'
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
				name: 'Historical Data',
				type: 'line',
				data: data_y
			}]
		};
		myChart.setOption(option);
	}
	
	function drawPieChart(dis_data, id, title_name) {
		var myChart = echarts.init(document.getElementById(id));
		option = {
			title : {
				text: title_name,
				x:'center'
			},
			tooltip : {
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
//			legend: {
//				orient: 'vertical',
//				left: 'left',
//				data: ['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
//			},
			series : [
				{
				    name: '访问来源',
				    type: 'pie',
				    radius : '55%',
				    center: ['50%', '60%'],
				        data:dis_data,
				        itemStyle: {
				            emphasis: {
					            shadowBlur: 10,
					            shadowOffsetX: 0,
					            shadowColor: 'rgba(0, 0, 0, 0.5)'
				            }
				        }
				}
			]
		};
		myChart.setOption(option);
	}
	
	// Get perform
//	$("#CauseBtn").click(function (event) {
//		event.preventDefault();
//		ajaxPerform();
//	});
	
	function ajaxPerform(p) {
//		alert(p);
		postData = {
			item : p
		};
		var selected = $("#target_list").html();
		var attr = [];
		var values = [];
		$.ajax({
			type : "Post",
			contentType : "application/json",
			url: url + "/api/job/getPerform",
			data : JSON.stringify(postData),
			dataType : 'json',
			success: function(result) {
				if (result.status == "Done") {
					$.each(result.data[0], function(i, pair){
						attr.push(pair.first);
						values.push(pair.second);
					});
					drawRelationChart(attr, values, "perform_left", 0, 2, window.target+"根源问题");
					drawPieChart(result.data[1], "perform_pie1", window.target+"原始分布");
					$("#perform_pie2").html(
							'<button type="submit" id="tspBtn" class="btn btn-primary">预测</button>'
					);
					console.log("Success: ", result);
				}
				else if (result.status == "Done1") {
					$("#perform_left").html(
							'选择'+p+'的状态<br/>'
							+'<select id="select_state" class="list-group">'
							+'<option value="a">low</option>'
							+'<option value="b">medium</option>'
							+'<option value="c">high</option>'
							+'</select>'
							+'<button type="submit" id="set_state1" class="btn btn-primary">Infer</button>'
					);
					drawPieChart(result.data[1], "perform_pie1", window.target+"原始分布");
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
	
	$(document).on("click","#tspBtn", function(){
//		alert("predict");
		ajaxTsp();
	});
	
	function ajaxTsp() {
		$.ajax({
			type : "GET",
			url : "/api/job/TSP",
			success : function(result) {
				if (result.status == "Done") {
					drawPieChart(result.data, "perform_pie2", "预测");
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