/*

 ## Histogram

 ### Parameters
 * auto_int :: Auto calculate data point interval?
 * resolution ::  If auto_int is enables, shoot for this many data points, rounding to
 sane intervals
 * interval :: Datapoint interval in elasticsearch date math format (eg 1d, 1w, 1y, 5y)
 * fill :: Only applies to line charts. Level of area shading from 0-10
 * linewidth ::  Only applies to line charts. How thick the line should be in pixels
 While the editor only exposes 0-10, this can be any numeric value.
 Set to 0 and you'll get something like a scatter plot
 * timezone :: This isn't totally functional yet. Currently only supports browser and utc.
 browser will adjust the x-axis labels to match the timezone of the user's
 browser
 * spyable ::  Dislay the 'eye' icon that show the last elasticsearch query
 * zoomlinks :: Show the zoom links?
 * bars :: Show bars in the chart
 * stack :: Stack multiple queries. This generally a crappy way to represent things.
 You probably should just use a line chart without stacking
 * points :: Should circles at the data points on the chart
 * lines :: Line chart? Sweet.
 * legend :: Show the legend?
 * x-axis :: Show x-axis labels and grid lines
 * y-axis :: Show y-axis labels and grid lines
 * interactive :: Allow drag to select time range

 */
define([
        'angular',
        'app',
        'jquery',
        'underscore',
        'kbn',
        'moment',
        './timeSeries'
    ],
    function (angular, app, $, _, kbn, moment, timeSeries) {
        'use strict';
        var bmw_total;
        var bmw_total_disk;
        var bmw_total_memory;
        var module = angular.module('kibana.panels.topology', []);
        app.useModule(module);

        module.controller('topology', function($scope, $q, querySrv, dashboard, filterSrv) {
            $scope.panelMeta = {
                modals : [
                    {
                        description: "Inspect",
                        icon: "icon-info-sign",
                        partial: "app/partials/inspector.html",
                        show: $scope.panel.spyable
                    }
                ],
                editorTabs : [
                    {
                        title:'Queries',
                        src:'app/partials/querySelect.html'
                    }
                ],
                status  : "Stable",
                description : "A bucketed time series chart of the current query, including all applied time and non-time filters, when used in <i>count</i> mode. Uses Solr’s facet.range query parameters. In <i>values</i> mode, it plots the value of a specific field over time, and allows the user to group field values by a second field."
            };

            // Set and populate defaults
            var _d = {
                mode        : 'value',
                queries     : {
                    mode        : 'all',
                    ids         : [],
                    query       : '*:*',
                    custom      : ''
                },
                max_rows    : 100000,  // maximum number of rows returned from Solr (also use this for group.limit to simplify UI setting)
                reverse     :0,
                segment	  :4,
                threshold_first:1000,
                threshold_second:2000,
                threshold_third:3000,
                group_field : null,
                auto_int    : true,
                total_first :'%',
                fontsize:20,
                field_color:'#209bf8',
                resolution  : 100,
                value_sort  :'rs_timestamp',
                interval    : '5m',
                intervals   : ['auto','1s','1m','5m','10m','30m','1h','3h','12h','1d','1w','1M','1y'],
                fill        : 0,
                linewidth   : 3,
                chart       :'stacking',
                chartColors :['#209bf8', '#f4d352','#ccf452','#8cf452','#3cee2b','#f467d8','#2fd7ee'],
                timezone    : 'browser', // browser, utc or a standard timezone
                spyable     : true,
                zoomlinks   : true,
                bars        : true,
                stack       : true,
                label       : true,
                points      : false,
                lines       : false,
                lines_smooth: false, // Enable 'smooth line' mode by removing zero values from the plot.
                legend      : true,
                'x-axis'    : true,
                'y-axis'    : true,
                percentage  : false,
                interactive : true,
                options     : true,
                show_queries:true,
                tooltip     : {
                    value_type: 'cumulative',
                    query_as_alias: false
                }
            };

            _.defaults($scope.panel,_d);

            $scope.init = function() {
                // Hide view options by default
                $scope.options = false;
                $scope.$on('refresh',function(){
                    $scope.get_data();
                });

                $scope.get_data();

            };

            $scope.set_interval = function(interval) {
                if(interval !== 'auto') {
                    $scope.panel.auto_int = false;
                    $scope.panel.interval = interval;
                } else {
                    $scope.panel.auto_int = true;
                }
            };

            $scope.interval_label = function(interval) {
                return $scope.panel.auto_int && interval === $scope.panel.interval ? interval+" (auto)" : interval;
            };

            /**
             * The time range effecting the panel
             * @return {[type]} [description]
             */
            $scope.get_time_range = function () {
                var range = $scope.range = filterSrv.timeRange('min');
                return range;
            };

            $scope.get_interval = function () {
                var interval = $scope.panel.interval,
                    range;
                if ($scope.panel.auto_int) {
                    range = $scope.get_time_range();
                    if (range) {
                        interval = kbn.secondsToHms(
                            kbn.calculate_interval(range.from, range.to, $scope.panel.resolution, 0) / 1000
                        );
                    }
                }
                $scope.panel.interval = interval || '10m';
                return $scope.panel.interval;
            };

            /**
             * Fetch the data for a chunk of a queries results. Multiple segments occur when several indicies
             * need to be consulted (like timestamped logstash indicies)
             *
             * The results of this function are stored on the scope's data property. This property will be an
             * array of objects with the properties info, time_series, and hits. These objects are used in the
             * render_panel function to create the historgram.
             *
             * !!! Solr does not need to fetch the data in chunk because it uses a facet search and retrieve
             * !!! all events from a single query.
             *
             * @param {number} segment   The segment count, (0 based)
             * @param {number} query_id  The id of the query, generated on the first run and passed back when
             *                            this call is made recursively for more segments
             */
            $scope.get_data = function(segment, query_id) {
                if (_.isUndefined(segment)) {
                    segment = 0;
                }
                delete $scope.panel.error;

                // Make sure we have everything for the request to complete
                if(dashboard.indices.length === 0) {
                    return;
                }
                var _range = $scope.get_time_range();
                var _interval = $scope.get_interval(_range);

                if ($scope.panel.auto_int) {
                    $scope.panel.interval = kbn.secondsToHms(
                        kbn.calculate_interval(_range.from,_range.to,$scope.panel.resolution,0)/1000);
                }

                $scope.panelMeta.loading = true;

                // Solr
                $scope.sjs.client.server(dashboard.current.solr.server + dashboard.current.solr.core_name);

                var request = $scope.sjs.Request().indices(dashboard.indices[segment]);
                $scope.panel.queries.ids = querySrv.idsByMode($scope.panel.queries);


                $scope.panel.queries.query = "";
                // Build the query
                _.each($scope.panel.queries.ids, function(id) {
                    var query = $scope.sjs.FilteredQuery(
                        querySrv.getEjsObj(id),
                        filterSrv.getBoolFilter(filterSrv.ids)
                    );

                    var facet = $scope.sjs.DateHistogramFacet(id);

                });

                if(_.isNull($scope.panel.value_field)) {
                    $scope.panel.error = "In " + $scope.panel.mode + " mode a field must be specified";
                    return;
                }

                // Populate the inspector panel
                $scope.populate_modal(request);

                // Build Solr query
                var metric_fq = '&fq=type_s:Metric';
                var model_fq = '&fq=type_s:Metric';
                if (filterSrv.getSolrFq()) {
                    metric_fq = '&' + filterSrv.getSolrFq();
                    model_fq = '&' + filterSrv.getSolrFq();
                }

                var time_field = filterSrv.getTimeField();
                var start_time = filterSrv.getStartTime();
                var end_time = filterSrv.getEndTime();

                // facet.range.end does NOT accept * as a value, need to convert it to NOW
                if (end_time === '*') {
                    end_time = 'NOW';
                }


                var wt_json = '&wt=json';
                var sort_value = '&sort='+'rs_timestamp_l'+'%20asc';
                var sort_as = '';
                var rows_limit = '&rows=0'; // for histogram, we do not need the actual response doc, so set rows=0
                var values_mode_query = '';
                var ass_mode_query = '';
                var facet = '';
                // For mode = value

                if (!$scope.panel.metric_field) {
                    $scope.panel.error = "In " + $scope.panel.mode + " mode a field must be specified";
                    return;
                }

                values_mode_query = '&fl=' + time_field + ' viz_timestamp_s ' + $scope.panel.metric_field;
                ass_mode_query = '&fl=' + time_field + ' ad_score_f viz_timestamp_s detect_metric_value_f';
                rows_limit = '&rows=' + $scope.panel.max_rows;


                var mypromises = [];

                var arr_id = [0, 1];
                _.each(arr_id, function(id) {
                    if (id === 0) {
                        var temp_q = 'q=' + $scope.panel.metric_field + '%3A%5B' + '*' + '%20TO%20' + '*' + '%5D' +
                            wt_json + rows_limit + metric_fq + facet + values_mode_query + sort_value;
                    }
                    else if( id === 1){
                        var temp_q = 'q=' + 'ad_score_f' + '%3A%5B' + '*' + '%20TO%20' + '*' + '%5D' +
                            wt_json + rows_limit + model_fq + facet + ass_mode_query + sort_as;
                    }
                    $scope.panel.queries.query += temp_q + "\n";
                    if ($scope.panel.queries.custom !== null) {
                        request = request.setQuery(temp_q + $scope.panel.queries.custom);
                    } else {
                        request = request.setQuery(temp_q);
                    }
                    mypromises.push(request.doSearch());
                });

                $scope.data = [];
                if (dashboard.current.services.query.ids.length >= 1) {
                    $q.all(mypromises).then(function(results) {
                        $scope.panelMeta.loading = false;
                        // Convert facet ids to numbers
                        // var facetIds = _.map(_.keys(results.facets),function(k){return parseInt(k, 10);});
                        //var facetIds = [0]; // Need to fix this

                        // Make sure we're still on the same query/queries
                        // TODO: We probably DON'T NEED THIS unless we have to support multiple queries in query module.
                        // if ($scope.query_id === query_id && _.difference(facetIds, $scope.panel.queries.ids).length === 0) {
                        var i = 0,
                            time_series,
                            hits;

                        _.each(arr_id, function(id,index) {
                            // Check for error and abort if found
                            if (!(_.isUndefined(results[index].error))) {
                                $scope.panel.error = $scope.parse_error(results[index].error.msg);
                                return;
                            }
                            // we need to initialize the data variable on the first run,
                            // and when we are working on the first segment of the data.
                            if (_.isUndefined($scope.data[i]) || segment === 0) {
                                time_series = new timeSeries.ZeroFilled({
                                    interval: _interval,
                                    start_date: _range && _range.from,
                                    end_date: _range && _range.to,
                                    fill_style: 'minimal'
                                });
                                hits = 0;
                            } else {
                                time_series = $scope.data[i].time_series;
                                hits = 0;
                                $scope.hits = 0;
                            }
                            var entry_time, entries, entry_value;
                            $scope.data[i] = results[index].response.docs;
                            i++;
                        });

                        // Tell the histogram directive to render.
                        $scope.$emit('render');
                    });
                }

            };

            // function $scope.zoom
            // factor :: Zoom factor, so 0.5 = cuts timespan in half, 2 doubles timespan
            $scope.zoom = function(factor) {
                var _range = filterSrv.timeRange('min');
                var _timespan = (_range.to.valueOf() - _range.from.valueOf());
                var _center = _range.to.valueOf() - _timespan/2;

                var _to = (_center + (_timespan*factor)/2);
                var _from = (_center - (_timespan*factor)/2);

                // If we're not already looking into the future, don't.
                if(_to > Date.now() && _range.to < Date.now()) {
                    var _offset = _to - Date.now();
                    _from = _from - _offset;
                    _to = Date.now();
                }

                var time_field = filterSrv.getTimeField();
                if(factor > 1) {
                    filterSrv.removeByType('time');
                }

                filterSrv.set({
                    type:'time',
                    from:moment.utc(_from).toDate(),
                    to:moment.utc(_to).toDate(),
                    field:time_field
                });

                dashboard.refresh();
            };

            // I really don't like this function, too much dom manip. Break out into directive?
            $scope.populate_modal = function(request) {
                $scope.inspector = angular.toJson(JSON.parse(request.toString()),true);
            };

            $scope.set_refresh = function (state) {
                $scope.refresh = state;
            };

            $scope.close_edit = function() {
                if($scope.refresh) {
                    $scope.get_data();
                }
                $scope.refresh =  false;
                $scope.$emit('render');
            };

            $scope.render = function() {
                $scope.$emit('render');
            };

        });

        module.directive('topologyChart', function(querySrv,dashboard,filterSrv) {
            return {
                restrict: 'A',
                link: function(scope, elem) {
                    console.log("topologyChart");
                    var myDiagram = null;
                    // Receive render events
                    scope.$on('render',function(){
                        render_panel();
                    });

                    // Re-render if the window is resized
                    angular.element(window).bind('resize', function(){
                        render_panel();
                    });

                    // Function for rendering panel
                    function render_panel() {
                        var plot, chartData;
                        var colors = [];

                        // IE doesn't work without this
                        elem.css({height:scope.panel.height||scope.row.height});

                        // Make a clone we can operate on.

                        chartData = _.clone(scope.data);

                        chartData = scope.panel.missing ? chartData :
                            _.without(chartData,_.findWhere(chartData,{meta:'missing'}));
                        chartData = scope.panel.other ? chartData :
                            _.without(chartData,_.findWhere(chartData,{meta:'other'}));

                        if (filterSrv.idsByTypeAndField('terms',scope.panel.field).length > 0) {
                            colors.push(scope.panel.lastColor);
                        } else {
                            colors = scope.panel.chartColors;
                        }

                        var idd = scope.$id;

						
						// start from this place
						
                        //try {
                        go.licenseKey = "73fe40e1ba1c28c702d95d76423d6cbc5cf07f21de8219a00b5011a7ee5c3f167699ed7057d78dd2c2ff4daf4f7d908a8d976b2b9e4c5133e735d2d546e68efeb43323b5440a44dda21136c5ccaa2ca1ae2870e0d2b676a2dc678eedebab";
                        var $ = go.GraphObject.make;  // for conciseness in defining templates
                        //if(!myDiagram) {
                        myDiagram =
                            $(go.Diagram, idd,
                                {
                                    initialContentAlignment: go.Spot.Center,
                                    validCycle: go.Diagram.CycleNotDirected,  // don't allow loops
                                    // For this sample, automatically show the state of the diagram's model on the page
                                    "ModelChanged": function (e) {
                                        //if (e.isTransactionFinished) showModel();
                                    },
                                    "undoManager.isEnabled": true
                                }
                            );

                        var optionTemplate =
                            $(go.Panel, "Table",
                                $(go.TextBlock,
                                    {
                                        row: 0,
                                        column:0,
                                        font: "10pt Verdana, sans-serif",
                                        margin: 5 },
                                    new go.Binding("text", "text")
                                ),
                                $(go.Panel, "Horizontal",
                                    {
                                        row: 0,
                                        column: 1 },
                                    $("Button",
                                        {
                                            click: incrementCount
                                        },
                                        $(go.Shape, "PlusLine", { margin: 3, desiredSize: new go.Size(7, 7) })
                                    )
                                ),
                                $(go.Panel, "Auto",
                                    {
                                        row: 0,
                                        column: 2 },
                                    $(go.Shape, { fill: "#F2F2F2" }),
                                    $(go.TextBlock,
                                        {
                                            font: "10pt Verdana, sans-serif",
                                            textAlign: "center", margin: 2,
                                            wrap: go.TextBlock.None, width: 30,
                                            editable: true, isMultiline: false,
                                        },
                                        new go.Binding("text", "count1").makeTwoWay(function(count) { return parseInt(count, 10); })
                                    )
                                ),
                                $(go.Panel, "Auto",
                                    {
                                        row: 0,
                                        column: 3 },
                                    $(go.Shape, { fill: "#F2F2F2" }),
                                    $(go.TextBlock,
                                        {
                                            font: "10pt Verdana, sans-serif",
                                            textAlign: "center", margin: 2,
                                            wrap: go.TextBlock.None, width: 30,
                                            editable: true, isMultiline: false,
                                        },
                                        new go.Binding("text", "count2").makeTwoWay(function(count) { return parseInt(count, 10); })
                                    )
                                ),
                                $(go.Panel, "Auto",
                                    {
                                        row: 0,
                                        column: 4 },
                                    $(go.Shape, { fill: "#F2F2F2" }),
                                    $(go.TextBlock,
                                        {
                                            font: "10pt Verdana, sans-serif",
                                            textAlign: "center", margin: 2,
                                            wrap: go.TextBlock.None, width: 30,
                                            editable: true, isMultiline: false,
                                        },
                                        new go.Binding("text", "count3").makeTwoWay(function(count) { return parseInt(count, 10); })
                                    )
                                ),
                                $(go.Panel, "Horizontal",
                                    {
                                        row: 0,
                                        column: 5 },
                                    $("Button",
                                        {
                                            click: decrementCount
                                        },
                                        $(go.Shape, "MinusLine", { margin: 3, desiredSize: new go.Size(7, 7) })
                                    )
                                )
                            );

                        myDiagram.nodeTemplate =
                            $(go.Node, "Auto",
                                new go.Binding("location", "loc", go.Point.parse),
                                $(go.Shape, "RoundedRectangle", { fill: "lightblue" }),
                                $(go.TextBlock,
                                    {
                                        font: "10pt Verdana, sans-serif",
                                        margin: 5 },
                                    new go.Binding("text", "key")
                                ),
                                $(go.Panel, "Table",
                                    {
                                        margin: 5,
                                        itemTemplate: optionTemplate
                                    },
                                    new go.Binding("itemArray", "slices")
                                )
                            );

                        myDiagram.linkTemplate =
                            $(go.Link,
                                new go.Binding("routing", "routing"),
                                $(go.Shape),
                                $(go.Shape, { toArrow: "Standard" })
                            );
                        // but use the default Link template, by not setting Diagram.linkTemplate

                        // create the model data that will be represented by Nodes and Links

                        var nodeDataArray = [
                            {
                                key: "Alpha",
                                loc: "0 0",
                                slices: [
                                    { text: "CPU", count1: 0.1, count2:0.4, count3:0.5,  color: "#B378C1" },
                                ]
                            },
                            {
                                key: "Beta",
                                loc: "200 100",
                                slices: [
                                    { text: "Memory", count1: 0.1, count2:0.4, count3:0.5,  color: "#B378C1" },
                                ]
                            },
                            {
                                key: "Gamma",
                                loc: "0 200",
                                slices: [
                                    { text: "Threads", count1: 0.1, count2:0.4, count3:0.5,  color: "#B378C1" },
                                ]
                            }
                        ];
                        var linkDataArray = [
                            { from: "Alpha", to: "Beta", routing: go.Link.Normal },
                            { from: "Alpha", to: "Gamma", routing: go.Link.Orthogonal }
                        ];
                        myDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);

                        // Validation function for editing text
                        function isValidCount(textblock, oldstr, newstr) {
                            if (newstr === "") return false;
                            var num = +newstr; // quick way to convert a string to a number
                            return !isNaN(num) && Number.isInteger(num) && num >= 0;
                        }

                        // Given some slice data, find the corresponding node data
                        function findNodeDataForSlice(slice) {
                            var arr = myDiagram.model.nodeDataArray;
                            for (var i = 0; i < arr.length; i++) {
                                var data = arr[i];
                                if (data.slices.indexOf(slice) >= 0) {
                                    return data;
                                }
                            }
                        }

                        function makeGeo(data) {
                            var nodedata = findNodeDataForSlice(data);
                            var sliceindex = nodedata.slices.indexOf(data);
                            var angles = getAngles(nodedata, sliceindex);

                            // Constructing the Geomtery this way is much more efficient than calling go.GraphObject.make:
                            return new go.Geometry()
                                .add(new go.PathFigure(pieRadius, pieRadius)  // start point
                                    .add(new go.PathSegment(go.PathSegment.Arc,
                                        angles.start, angles.sweep,  // angles
                                        pieRadius, pieRadius,  // center
                                        pieRadius, pieRadius)  // radius
                                        .close()));
                        }

                        // Ensure slices get the proper positioning after we update any counts
                        function positionSlice(data, obj) {
                            var nodedata = findNodeDataForSlice(data);
                            var sliceindex = nodedata.slices.indexOf(data);
                            var angles = getAngles(nodedata, sliceindex);

                            var selected = obj.findObject("SLICE").stroke !== "transparent";
                            if (selected && angles.sweep !== 360) {
                                var offsetPoint = new go.Point(pieRadius / 10, 0); // offset by 1/10 the radius
                                offsetPoint = offsetPoint.rotate(angles.start + angles.sweep / 2); // rotate to the correct angle
                                offsetPoint = offsetPoint.offset(pieRadius / 10, pieRadius / 10); // translate center toward middle of pie panel
                                return offsetPoint;
                            }
                            return new go.Point(pieRadius / 10, pieRadius / 10);
                        }

                        // This is a bit inefficient, but should be OK for normal-sized graphs with reasonable numbers of slices per node
                        function findAllSelectedItems() {
                            var slices = [];
                            for (var nit = myDiagram.nodes; nit.next(); ) {
                                var node = nit.value;
                                var pie = node.findObject("PIE");
                                if (pie) {
                                    for (var sit = pie.elements; sit.next(); ) {
                                        var slicepanel = sit.value;
                                        if (slicepanel.findObject("SLICE").stroke !== "transparent") slices.push(slicepanel);
                                    }
                                }
                            }
                            return slices;
                        }

                        // Return total count of a given node
                        function getTotalCount(nodedata) {
                            var totCount = 0;
                            for (var i = 0; i < nodedata.slices.length; i++) {
                                totCount += nodedata.slices[i].count;
                            }
                            return totCount;
                        }

                        // Determine start and sweep angles given some node data and the index of the slice
                        function getAngles(nodedata, index) {
                            var totCount = getTotalCount(nodedata);
                            var startAngle = -90;
                            for (var i = 0; i < index; i++) {
                                startAngle += 360 * nodedata.slices[i].count / totCount;
                            }
                            return { "start": startAngle, "sweep": 360 * nodedata.slices[index].count / totCount };
                        }

                        // When user hits + button, increment count on that option
                        function incrementCount(e, obj) {
                            myDiagram.model.startTransaction("increment count");
                            var slicedata = obj.panel.panel.data;
                            myDiagram.model.setDataProperty(slicedata, "count", slicedata.count + 1);
                            myDiagram.model.commitTransaction("increment count");
                        }

                        // When user hits - button, decrement count on that option
                        function decrementCount(e, obj) {
                            myDiagram.model.startTransaction("decrement count");
                            var slicedata = obj.panel.panel.data;
                            if (slicedata.count > 0)
                                myDiagram.model.setDataProperty(slicedata, "count", slicedata.count - 1);
                            myDiagram.model.commitTransaction("decrement count");
                        }
                    }
                }
            };
        });

    });

