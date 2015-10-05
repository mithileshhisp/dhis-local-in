Ext.onReady(function(){function b(){var o=0;var l=0;var j=0;var i=0;var p=0;var m=0;var k=0;this.addData=function(n,s){if(p==0){m=n;k=s}else{var r=n-m;var q=s-k;j+=r*r*p/(p+1);i+=r*q*p/(p+1);m+=r/(p+1);k+=q/(p+1)}o+=n;l+=s;p++};this.predict=function(n){var q=this.getSlope();return this.getIntercept(q)+q*n};this.getSlope=function(){if(p<2){return Number.NaN}return i/j};this.getIntercept=function(n){return(l-n*o)/p}}Ext.Ajax.method="GET";DV={};DV.instances=[];DV.i18n={};DV.isDebug=false;DV.isSessionStorage=("sessionStorage" in window&&window.sessionStorage!==null);DV.getCore=function(o){var k={},m={},l={},i={},j={},n;(function(){k.finals={ajax:{path_module:"/dhis-web-visualizer/",path_api:"/api/",path_commons:"/dhis-web-commons-ajax-json/",data_get:"chartValues.json",indicator_get:"indicatorGroups/",indicator_getall:"indicators.json?paging=false&links=false",indicatorgroup_get:"indicatorGroups.json?paging=false&links=false",dataelement_get:"dataElementGroups/",dataelement_getall:"dataElements.json?domainType=aggregate&paging=false&links=false",dataelementgroup_get:"dataElementGroups.json?paging=false&links=false",dataset_get:"dataSets.json?paging=false&links=false"},dimension:{data:{value:"data",name:DV.i18n.data,dimensionName:"dx",objectName:"dx"},indicator:{value:"indicator",name:DV.i18n.indicator,dimensionName:"dx",objectName:"in"},dataElement:{value:"dataelement",name:DV.i18n.data_element,dimensionName:"dx",objectName:"de"},operand:{value:"operand",name:"Operand",dimensionName:"dx",objectName:"dc"},dataSet:{value:"dataset",name:DV.i18n.dataset,dimensionName:"dx",objectName:"ds"},category:{name:DV.i18n.categories,dimensionName:"co",objectName:"co"},period:{value:"period",name:DV.i18n.period,dimensionName:"pe",objectName:"pe"},fixedPeriod:{value:"periods"},relativePeriod:{value:"relativePeriods"},organisationUnit:{value:"organisationUnits",name:DV.i18n.organisation_units,dimensionName:"ou",objectName:"ou"},dimension:{value:"dimension"},value:{value:"value"}},chart:{series:"series",category:"category",filter:"filter",column:"column",stackedcolumn:"stackedcolumn",bar:"bar",stackedbar:"stackedbar",line:"line",area:"area",pie:"pie",radar:"radar"},data:{domain:"domain_",targetLine:"targetline_",baseLine:"baseline_",trendLine:"trendline_"},image:{png:"png",pdf:"pdf"},cmd:{init:"init_",none:"none_",urlparam:"id"},root:{id:"root"}};n=k.finals.dimension;n.objectNameMap={};n.objectNameMap[n.data.objectName]=n.data;n.objectNameMap[n.indicator.objectName]=n.indicator;n.objectNameMap[n.dataElement.objectName]=n.dataElement;n.objectNameMap[n.operand.objectName]=n.operand;n.objectNameMap[n.dataSet.objectName]=n.dataSet;n.objectNameMap[n.category.objectName]=n.category;n.objectNameMap[n.period.objectName]=n.period;n.objectNameMap[n.organisationUnit.objectName]=n.organisationUnit;n.objectNameMap[n.dimension.objectName]=n.dimension;k.period={periodTypes:[{id:"Daily",name:DV.i18n.daily},{id:"Weekly",name:DV.i18n.weekly},{id:"Monthly",name:DV.i18n.monthly},{id:"BiMonthly",name:DV.i18n.bimonthly},{id:"Quarterly",name:DV.i18n.quarterly},{id:"SixMonthly",name:DV.i18n.sixmonthly},{id:"Yearly",name:DV.i18n.yearly},{id:"FinancialOct",name:DV.i18n.financial_oct},{id:"FinancialJuly",name:DV.i18n.financial_july},{id:"FinancialApril",name:DV.i18n.financial_april}]};k.layout={west_width:424,west_fieldset_width:416,west_width_padding:4,west_fill:2,west_fill_accordion_indicator:59,west_fill_accordion_dataelement:59,west_fill_accordion_dataset:33,west_fill_accordion_period:296,west_fill_accordion_organisationunit:62,west_maxheight_accordion_indicator:350,west_maxheight_accordion_dataelement:350,west_maxheight_accordion_dataset:350,west_maxheight_accordion_period:513,west_maxheight_accordion_organisationunit:500,west_maxheight_accordion_group:350,west_scrollbarheight_accordion_indicator:300,west_scrollbarheight_accordion_dataelement:300,west_scrollbarheight_accordion_dataset:300,west_scrollbarheight_accordion_period:450,west_scrollbarheight_accordion_organisationunit:450,west_scrollbarheight_accordion_group:300,east_tbar_height:31,east_gridcolumn_height:30,form_label_width:55,window_favorite_ypos:100,window_confirm_width:250,window_share_width:500,grid_favorite_width:420,grid_row_height:27,treepanel_minheight:135,treepanel_maxheight:400,treepanel_fill_default:310,treepanel_toolbar_menu_width_group:140,treepanel_toolbar_menu_width_level:120,multiselect_minheight:100,multiselect_maxheight:250,multiselect_fill_default:345,multiselect_fill_reportingrates:315};k.chart={style:{inset:30,fontFamily:"Arial,Sans-serif,Lucida Grande,Ubuntu"},theme:{dv1:["#94ae0a","#0b3b68","#a61120","#ff8809","#7c7474","#a61187","#ffd13e","#24ad9a","#a66111","#414141","#4500c4","#1d5700"]}};k.status={icon:{error:"error_s.png",warning:"warning.png",ok:"ok.png"}}}());(function(){m.layout={};m.layout.Record=function(p){var p=Ext.clone(p);return function(){if(!Ext.isObject(p)){console.log("Record: config is not an object: "+p);return}if(!Ext.isString(p.id)){alert("Record: id is not text: "+p);return}p.id=p.id.replace(".","-");return p}()};m.layout.Dimension=function(p){var p=Ext.clone(p);return function(){if(!Ext.isObject(p)){console.log("Dimension: config is not an object: "+p);return}if(!Ext.isString(p.dimension)){console.log("Dimension: name is not a string: "+p);return}if(p.dimension!==k.finals.dimension.category.objectName){var q=[];if(!Ext.isArray(p.items)){console.log("Dimension: items is not an array: "+p);return}for(var r=0;r<p.items.length;r++){q.push(m.layout.Record(p.items[r]))}p.items=Ext.Array.clean(q);if(!p.items.length){console.log("Dimension: has no valid items: "+p);return}}return p}()};m.layout.Layout=function(q){var q=Ext.clone(q),r={},p,s;p=function(u){var u=Ext.clone(u);if(!(u&&Ext.isArray(u)&&u.length)){return}for(var t=0;t<u.length;t++){u[t]=m.layout.Dimension(u[t])}u=Ext.Array.clean(u);return u.length?u:null};analytical2layout=function(u){var w=Ext.clone(u),x=n.category.objectName;u=Ext.clone(u);w.columns=[];w.rows=[];w.filters=w.filters||[];if(Ext.isArray(u.columns)&&u.columns.length){u.columns.reverse();for(var t=0,v;t<u.columns.length;t++){v=u.columns[t];if(v.dimension===x){continue}if(!w.columns.length){w.columns.push(v)}else{if(v.dimension===n.indicator.objectName){w.filters.push(w.columns.pop());w.columns=[v]}else{w.filters.push(v)}}}}if(Ext.isArray(u.rows)&&u.rows.length){u.rows.reverse();for(var t=0,v;t<u.rows.length;t++){v=u.rows[t];if(v.dimension===x){continue}if(!w.rows.length){w.rows.push(v)}else{if(v.dimension===n.indicator.objectName){w.filters.push(w.rows.pop());w.rows=[v]}else{w.filters.push(v)}}}}return w};s=function(){var v=k.finals.dimension,u,w={};if(!r){return}u=Ext.Array.clean([].concat(r.columns||[],r.rows||[],r.filters||[]));for(var t=0;t<u.length;t++){w[u[t].dimension]=u[t]}if(r.filters&&r.filters.length){for(var t=0;t<r.filters.length;t++){if(r.filters[t].dimension===v.indicator.objectName){j.message.alert(DV.i18n.indicators_cannot_be_specified_as_filter||"Indicators cannot be specified as filter");return}if(r.filters[t].dimension===v.category.objectName){j.message.alert(DV.i18n.categories_cannot_be_specified_as_filter||"Categories cannot be specified as filter");return}if(r.filters[t].dimension===v.dataSet.objectName){j.message.alert(DV.i18n.data_sets_cannot_be_specified_as_filter||"Data sets cannot be specified as filter");return}}}if(w[v.operand.objectName]&&w[v.indicator.objectName]){j.message.alert("Indicators and detailed data elements cannot be specified together");return}if(w[v.operand.objectName]&&w[v.dataElement.objectName]){j.message.alert("Detailed data elements and totals cannot be specified together");return}if(w[v.operand.objectName]&&w[v.dataSet.objectName]){j.message.alert("Data sets and detailed data elements cannot be specified together");return}if(w[v.operand.objectName]&&w[v.category.objectName]){j.message.alert("Categories and detailed data elements cannot be specified together");return}return true};return function(){var t=[],v=k.finals.dimension;if(!(q&&Ext.isObject(q))){alert("Layout: config is not an object ("+o.el+")");return}q.columns=p(q.columns);q.rows=p(q.rows);q.filters=p(q.filters);if(!q.columns){alert("No series items selected");return}if(!q.rows){alert("No category items selected");return}for(var u=0,w=Ext.Array.clean([].concat(q.columns||[],q.rows||[],q.filters||[]));u<w.length;u++){if(m.layout.Dimension(w[u])){t.push(w[u].dimension)}}if(!Ext.Array.contains(t,v.period.objectName)){alert("At least one period must be specified as series, category or filter");return}if(q.id){r.id=q.id}if(q.name){r.name=q.name}q=analytical2layout(q);r.type=Ext.isString(q.type)?q.type.toLowerCase():k.finals.chart.column;r.columns=q.columns;r.rows=q.rows;r.filters=q.filters;r.showTrendLine=Ext.isBoolean(q.regression)?q.regression:(Ext.isBoolean(q.showTrendLine)?q.showTrendLine:false);r.showValues=Ext.isBoolean(q.showData)?q.showData:(Ext.isBoolean(q.showValues)?q.showValues:true);r.hideLegend=Ext.isBoolean(q.hideLegend)?q.hideLegend:false;r.hideTitle=Ext.isBoolean(q.hideTitle)?q.hideTitle:false;r.targetLineValue=Ext.isNumber(q.targetLineValue)?q.targetLineValue:null;r.targetLineTitle=Ext.isString(q.targetLineLabel)&&!Ext.isEmpty(q.targetLineLabel)?q.targetLineLabel:(Ext.isString(q.targetLineTitle)&&!Ext.isEmpty(q.targetLineTitle)?q.targetLineTitle:null);r.baseLineValue=Ext.isNumber(q.baseLineValue)?q.baseLineValue:null;r.baseLineTitle=Ext.isString(q.baseLineLabel)&&!Ext.isEmpty(q.baseLineLabel)?q.baseLineLabel:(Ext.isString(q.baseLineTitle)&&!Ext.isEmpty(q.baseLineTitle)?q.baseLineTitle:null);r.title=Ext.isString(q.title)&&!Ext.isEmpty(q.title)?q.title:null;r.domainAxisTitle=Ext.isString(q.domainAxisLabel)&&!Ext.isEmpty(q.domainAxisLabel)?q.domainAxisLabel:(Ext.isString(q.domainAxisTitle)&&!Ext.isEmpty(q.domainAxisTitle)?q.domainAxisTitle:null);r.rangeAxisTitle=Ext.isString(q.rangeAxisLabel)&&!Ext.isEmpty(q.rangeAxisLabel)?q.rangeAxisLabel:(Ext.isString(q.rangeAxisTitle)&&!Ext.isEmpty(q.rangeAxisTitle)?q.rangeAxisTitle:null);r.parentGraphMap=Ext.isObject(q.parentGraphMap)?q.parentGraphMap:null;if(!s()){return}return r}()};m.response={};m.response.Header=function(p){var p=Ext.clone(p);return function(){if(!Ext.isObject(p)){console.log("Header: config is not an object: "+p);return}if(!Ext.isString(p.name)){console.log("Header: name is not a string: "+p);return}if(!Ext.isBoolean(p.meta)){console.log("Header: meta is not boolean: "+p);return}return p}()};m.response.Response=function(p){var p=Ext.clone(p);return function(){if(!(p&&Ext.isObject(p))){console.log("Response: config is not an object");return}if(!(p.headers&&Ext.isArray(p.headers))){console.log("Response: headers is not an array");return}for(var q=0,r;q<p.headers.length;q++){p.headers[q]=m.response.Header(p.headers[q])}p.headers=Ext.Array.clean(p.headers);if(!p.headers.length){console.log("Response: no valid headers");return}if(!(Ext.isArray(p.rows)&&p.rows.length>0)){alert("No values found");return}if(p.headers.length!==p.rows[0].length){console.log("Response: headers.length !== rows[0].length")}return p}()}}());(function(){l.prototype={};l.prototype.array={};l.prototype.array.getLength=function(q,p){if(!Ext.isArray(q)){if(!p){console.log("support.prototype.array.getLength: not an array")}return null}return q.length};l.prototype.array.sort=function(r,q,p){if(!l.prototype.array.getLength(r)){return}p=p||"name";r.sort(function(t,s){if(Ext.isObject(t)&&Ext.isObject(s)&&p){t=t[p];s=s[p]}if(Ext.isString(t)&&Ext.isString(s)){t=t.toLowerCase();s=s.toLowerCase();if(q==="DESC"){return t<s?1:(t>s?-1:0)}else{return t<s?-1:(t>s?1:0)}}else{if(Ext.isNumber(t)&&Ext.isNumber(s)){return q==="DESC"?s-t:t-s}}return 0});return r};l.prototype.object={};l.prototype.object.getLength=function(p,s){if(!Ext.isObject(p)){if(!s){console.log("support.prototype.object.getLength: not an object")}return null}var r=0;for(var q in p){if(p.hasOwnProperty(q)){r++}}return r};l.prototype.object.hasObject=function(q,t,s){if(!l.prototype.object.getLength(q)){return null}for(var r in q){var p=q[r];if(q.hasOwnProperty(r)&&p[t]===s){return true}}return null};l.prototype.str={};l.prototype.str.replaceAll=function(r,q,p){return r.replace(new RegExp(q,"g"),p)}}());(function(){i.layout={};i.layout.cleanDimensionArray=function(q){if(!l.prototype.array.getLength(q)){return null}var r=[];for(var p=0;p<q.length;p++){r.push(m.layout.Dimension(q[p]))}r=Ext.Array.clean(r);return r.length?r:null};i.layout.sortDimensionArray=function(s,r){if(!l.prototype.array.getLength(s,true)){return null}s=i.layout.cleanDimensionArray(s);if(!s){console.log("service.layout.sortDimensionArray: no valid dimensions");return null}r=r||"dimensionName";Ext.Array.sort(s,function(u,t){if(u[r]<t[r]){return -1}if(u[r]>t[r]){return 1}return 0});for(var q=0,p;q<s.length;q++){l.prototype.array.sort(s[q].items,"ASC","id");if(l.prototype.array.getLength(s[q].ids)){l.prototype.array.sort(s[q].ids)}}return s};i.layout.getObjectNameDimensionMapFromDimensionArray=function(s){var r={};if(!l.prototype.array.getLength(s)){return null}for(var p=0,q;p<s.length;p++){q=m.layout.Dimension(s[p]);if(q){r[q.dimension]=q}}return l.prototype.object.getLength(r)?r:null};i.layout.getObjectNameDimensionItemsMapFromDimensionArray=function(s){var r={};if(!l.prototype.array.getLength(s)){return null}for(var p=0,q;p<s.length;p++){q=m.layout.Dimension(s[p]);if(q){r[q.dimension]=q.items}}return l.prototype.object.getLength(r)?r:null};i.layout.getExtendedLayout=function(u){var u=Ext.clone(u),w={columns:[],rows:[],filters:[],columnObjectNames:[],columnDimensionNames:[],columnItems:[],columnIds:[],rowObjectNames:[],rowDimensionNames:[],rowItems:[],rowIds:[],axisDimensions:[],axisObjectNames:[],axisDimensionNames:[],sortedAxisDimensionNames:[],filterDimensions:[],filterObjectNames:[],filterDimensionNames:[],filterItems:[],filterIds:[],sortedFilterDimensions:[],dimensions:[],objectNames:[],dimensionNames:[],objectNameDimensionsMap:{},objectNameItemsMap:{},objectNameIdsMap:{},dimensionNameDimensionsMap:{},dimensionNameItemsMap:{},dimensionNameIdsMap:{},dimensionNameSortedIdsMap:{}};Ext.applyIf(w,u);if(u.columns){for(var s=0,t,v,p;s<u.columns.length;s++){t=u.columns[s];v=t.items;p={};p.dimension=t.dimension;p.objectName=t.dimension;p.dimensionName=n.objectNameMap[t.dimension].dimensionName;if(v){p.items=v;p.ids=[];for(var r=0;r<v.length;r++){p.ids.push(v[r].id)}}w.columns.push(p);w.columnObjectNames.push(p.objectName);w.columnDimensionNames.push(p.dimensionName);w.columnItems=w.columnItems.concat(p.items);w.columnIds=w.columnIds.concat(p.ids);w.axisDimensions.push(p);w.axisObjectNames.push(p.objectName);w.axisDimensionNames.push(n.objectNameMap[p.objectName].dimensionName);w.objectNameDimensionsMap[p.objectName]=p;w.objectNameItemsMap[p.objectName]=p.items;w.objectNameIdsMap[p.objectName]=p.ids}}if(u.rows){for(var s=0,t,v,p;s<u.rows.length;s++){t=u.rows[s];v=t.items;p={};p.dimension=t.dimension;p.objectName=t.dimension;p.dimensionName=n.objectNameMap[t.dimension].dimensionName;if(v){p.items=v;p.ids=[];for(var r=0;r<v.length;r++){p.ids.push(v[r].id)}}w.rows.push(p);w.rowObjectNames.push(p.objectName);w.rowDimensionNames.push(p.dimensionName);w.rowItems=w.rowItems.concat(p.items);w.rowIds=w.rowIds.concat(p.ids);w.axisDimensions.push(p);w.axisObjectNames.push(p.objectName);w.axisDimensionNames.push(n.objectNameMap[p.objectName].dimensionName);w.objectNameDimensionsMap[p.objectName]=p;w.objectNameItemsMap[p.objectName]=p.items;w.objectNameIdsMap[p.objectName]=p.ids}}if(u.filters){for(var s=0,t,v,p;s<u.filters.length;s++){t=u.filters[s];v=t.items;p={};p.dimension=t.dimension;p.objectName=t.dimension;p.dimensionName=n.objectNameMap[t.dimension].dimensionName;if(v){p.items=v;p.ids=[];for(var r=0;r<v.length;r++){p.ids.push(v[r].id)}}w.filters.push(p);w.filterDimensions.push(p);w.filterObjectNames.push(p.objectName);w.filterDimensionNames.push(n.objectNameMap[p.objectName].dimensionName);w.filterItems=w.filterItems.concat(p.items);w.filterIds=w.filterIds.concat(p.ids);w.objectNameDimensionsMap[p.objectName]=p;w.objectNameItemsMap[p.objectName]=p.items;w.objectNameIdsMap[p.objectName]=p.ids}}w.axisDimensionNames=Ext.Array.unique(w.axisDimensionNames);w.filterDimensionNames=Ext.Array.unique(w.filterDimensionNames);w.columnDimensionNames=Ext.Array.unique(w.columnDimensionNames);w.rowDimensionNames=Ext.Array.unique(w.rowDimensionNames);w.filterDimensionNames=Ext.Array.unique(w.filterDimensionNames);w.sortedAxisDimensionNames=Ext.clone(w.axisDimensionNames).sort();w.sortedFilterDimensions=i.layout.sortDimensionArray(Ext.clone(w.filterDimensions));w.dimensions=[].concat(w.axisDimensions,w.filterDimensions);w.objectNames=[].concat(w.axisObjectNames,w.filterObjectNames);w.dimensionNames=[].concat(w.axisDimensionNames,w.filterDimensionNames);for(var s=0,q;s<w.dimensionNames.length;s++){q=w.dimensionNames[s];w.dimensionNameDimensionsMap[q]=[];w.dimensionNameItemsMap[q]=[];w.dimensionNameIdsMap[q]=[]}for(var s=0,p;s<w.dimensions.length;s++){p=w.dimensions[s];w.dimensionNameDimensionsMap[p.dimensionName].push(p);w.dimensionNameItemsMap[p.dimensionName]=w.dimensionNameItemsMap[p.dimensionName].concat(p.items);w.dimensionNameIdsMap[p.dimensionName]=w.dimensionNameIdsMap[p.dimensionName].concat(p.ids)}for(var x in w.dimensionNameIdsMap){if(w.dimensionNameIdsMap.hasOwnProperty(x)){w.dimensionNameSortedIdsMap[x]=Ext.clone(w.dimensionNameIdsMap[x]).sort()}}return w};i.layout.getSyncronizedXLayout=function(z,p){var t=Ext.Array.clean([].concat(z.columns||[],z.rows||[],z.filters||[])),L=z.objectNameDimensionsMap[n.organisationUnit.objectName],w=L&&Ext.Array.contains(L.ids,"USER_ORGUNIT"),N=L&&Ext.Array.contains(L.ids,"USER_ORGUNIT_CHILDREN"),K=L&&Ext.Array.contains(L.ids,"USER_ORGUNIT_GRANDCHILDREN"),q=function(){if(L&&Ext.isArray(L.ids)){for(var P=0;P<L.ids.length;P++){if(L.ids[P].substr(0,5)==="LEVEL"){return true}}}return false}(),y=function(){if(L&&Ext.isArray(L.ids)){for(var P=0;P<L.ids.length;P++){if(L.ids[P].substr(0,8)==="OU_GROUP"){return true}}}return false}(),s=n.organisationUnit.objectName,O;for(var H=0,F,x,A;H<t.length;H++){F=t[H];F.items=[];x=p.metaData[F.objectName];if(F.dimensionName===s){if(w||N||K){var J,v,C;if(w){J=[{id:o.user.ou,name:p.metaData.names[o.user.ou]}]}if(N){v=[];for(var E=0;E<o.user.ouc.length;E++){v.push({id:o.user.ouc[E],name:p.metaData.names[o.user.ouc[E]]})}l.prototype.array.sort(v)}if(K){var r=[].concat(o.user.ou,o.user.ouc),u=p.metaData[s];C=[];for(var E=0,B;E<u.length;E++){B=u[E];if(!Ext.Array.contains(r,B)){C.push({id:B,name:p.metaData.names[B]})}}l.prototype.array.sort(C)}F.items=[].concat(J||[],v||[],C||[])}else{if(q||y){for(var E=0,u=p.metaData[s],B;E<u.length;E++){B=u[E];F.items.push({id:B,name:p.metaData.names[B]})}l.prototype.array.sort(F.items)}else{F.items=Ext.clone(z.dimensionNameItemsMap[F.dimensionName])}}}else{if(Ext.isArray(x)&&x.length){var D=Ext.clone(p.metaData[F.dimensionName]);for(var E=0;E<D.length;E++){F.items.push({id:D[E],name:p.metaData.names[D[E]]})}}else{F.items=Ext.clone(z.objectNameItemsMap[F.objectName])}}}O=m.layout.Layout(z);if(O){t=Ext.Array.clean([].concat(O.columns||[],O.rows||[],O.filters||[]));for(var H=0,G=p.metaData.names,I;H<t.length;H++){I=t[H].items;if(Ext.isArray(I)&&I.length){for(var E=0,M;E<I.length;E++){M=I[E];if(Ext.isObject(M)&&Ext.isString(G[M.id])&&!Ext.isString(M.name)){M.name=G[M.id]||""}}}}return i.layout.getExtendedLayout(O)}return null};i.layout.layout2plugin=function(t){var t=Ext.clone(t),s=Ext.Array.clean([].concat(t.columns||[],t.rows||[],t.filters||[]));if(Ext.isString(t.id)){return{id:t.id}}for(var q=0,u,r;q<s.length;q++){u=s[q];delete u.id;delete u.ids;delete u.type;delete u.dimensionName;delete u.objectName;for(var p=0,r;p<u.items.length;p++){r=u.items[p];delete r.name;delete r.code;delete r.created;delete r.lastUpdated}}if(!t.showTrendLine){delete t.showTrendLine}if(!t.targetLineValue){delete t.targetLineValue}if(!t.targetLineTitle){delete t.targetLineTitle}if(!t.baseLineValue){delete t.baseLineValue}if(!t.baseLineTitle){delete t.baseLineTitle}if(t.showValues){delete t.showValues}if(!t.hideLegend){delete t.hideLegend}if(!t.hideTitle){delete t.hideTitle}if(!t.title){delete t.title}if(!t.domainAxisTitle){delete t.domainAxisTitle}if(!t.rangeAxisTitle){delete t.rangeAxisTitle}if(!t.sorting){delete t.sorting}delete t.parentGraphMap;delete t.reportingPeriod;delete t.organisationUnit;delete t.parentOrganisationUnit;delete t.regression;delete t.cumulative;delete t.sortOrder;delete t.topLimit;return t};i.response={};i.response.getExtendedResponse=function(q,p){var r=[];p.nameHeaderMap={};p.idValueMap={};(function(){for(var s=0,t;s<p.headers.length;s++){t=p.headers[s];t.index=s;if(t.meta){t.ids=Ext.clone(q.dimensionNameIdsMap[t.name])||[];t.size=t.ids.length;r=r.concat(t.ids)}}for(var s=0,t;s<p.headers.length;s++){t=p.headers[s];p.nameHeaderMap[t.name]=t}}());(function(){for(var s=0,u,t;s<r.length;s++){u=r[s];if(u.indexOf("-")!==-1){t=u.split("-");p.metaData.names[u]=p.metaData.names[t[0]]+" "+p.metaData.names[t[1]]}}}());(function(){var x=p.nameHeaderMap[k.finals.dimension.value.value].index,t=p.nameHeaderMap[k.finals.dimension.category.dimensionName],B=n.data.dimensionName,y=n.category.dimensionName,v=q.axisDimensionNames,z=[];for(var w=0;w<v.length;w++){z.push(p.nameHeaderMap[v[w]].index);if(t&&!Ext.Array.contains(v,y)&&v[w]===B){z.push(t.index)}}for(var w=0,A,s;w<p.rows.length;w++){A=p.rows[w];s="";for(var u=0;u<z.length;u++){s+=A[z[u]]}p.idValueMap[s]=A[x]}}());return p}}());(function(){j.mask={};j.mask.show=function(p,q){if(!Ext.isObject(p)){console.log("support.gui.mask.show: component not an object");return null}q=q||"Loading..";if(p.mask){p.mask.destroy();p.mask=null}p.mask=new Ext.create("Ext.LoadMask",p,{shadow:false,message:q,style:"box-shadow:0",bodyStyle:"box-shadow:0"});p.mask.show()};j.mask.hide=function(p){if(!Ext.isObject(p)){console.log("support.gui.mask.hide: component not an object");return null}if(p.mask){p.mask.destroy();p.mask=null}};j.message={};j.message.alert=function(p){console.log(p)};j.analytics={};j.analytics.getParamString=function(B,s){var w=s?B.sortedAxisDimensionNames:B.axisDimensionNames,C=s?B.sortedFilterDimensions:B.filterDimensions,p=s?B.dimensionNameSortedIdsMap:B.dimensionNameIdsMap,t="?",r=false,q=B.dimensionNameItemsMap,D=n.indicator.dimensionName;for(var x=0,u,A;x<w.length;x++){u=w[x];t+="dimension="+u;A=Ext.clone(p[u]);if(u===D){for(var v=0,z;v<A.length;v++){z=A[v].indexOf("-");if(z>0){r=true;A[v]=A[v].substr(0,z)}}A=Ext.Array.unique(A)}if(u!==n.category.dimensionName){t+=":"+A.join(";")}if(x<(w.length-1)){t+="&"}}if(r){t+="&dimension="+k.finals.dimension.category.dimensionName}if(Ext.isArray(C)&&C.length){for(var x=0,y;x<C.length;x++){y=C[x];t+="&filter="+y.dimensionName+":"+y.ids.join(";")}}return t};j.analytics.validateUrl=function(q){var r;if(Ext.isIE){r="Too many items selected (url has "+q.length+" characters). Internet Explorer accepts maximum 2048 characters."}else{var p=q.length>8000?"8000":(q.length>4000?"4000":"2000");r="Too many items selected (url has "+q.length+" characters). Please reduce to less than "+p+" characters."}r+="\n\nHint: A good way to reduce the number of items is to use relative periods and level/group organisation unit selection modes.";alert(r)};j.chart={};j.chart.createChart=function(K){var u=K.app.xResponse,A=K.app.xLayout,w,s,D,t,H,J,q,x,G,C,I,v,B,r,F,E,z,y,p={};t=function(){var X=k.finals.dimension.period.dimensionName,N=A.columns[0].dimensionName,O=A.rows[0].dimensionName,V=[],W=A.columnIds,R=A.rowIds,ab=[],Z=[],Q=[],Y;for(var U=0,T,M;U<R.length;U++){T={};M=R[U];T[k.finals.data.domain]=u.metaData.names[M];for(var S=0,L;S<W.length;S++){L=l.prototype.str.replaceAll(W[S],"-","")+l.prototype.str.replaceAll(R[U],"-","");T[W[S]]=parseFloat(u.idValueMap[L])}V.push(T)}if(A.showTrendLine){for(var U=0,P,aa;U<W.length;U++){P=new b();aa=k.finals.data.trendLine+W[U];for(var S=0;S<V.length;S++){P.addData(S,V[S][W[U]])}for(var S=0;S<V.length;S++){V[S][aa]=parseFloat(P.predict(S).toFixed(1))}ab.push(aa);u.metaData.names[aa]=DV.i18n.trend+" ("+u.metaData.names[W[U]]+")"}}if(Ext.isNumber(A.targetLineValue)||Ext.isNumber(parseFloat(A.targetLineValue))){for(var U=0;U<V.length;U++){V[U][k.finals.data.targetLine]=parseFloat(A.targetLineValue)}Z.push(k.finals.data.targetLine)}if(Ext.isNumber(A.baseLineValue)||Ext.isNumber(parseFloat(A.baseLineValue))){for(var U=0;U<V.length;U++){V[U][k.finals.data.baseLine]=parseFloat(A.baseLineValue)}Q.push(k.finals.data.baseLine)}Y=Ext.create("Ext.data.Store",{fields:function(){var ac=Ext.clone(W);ac.push(k.finals.data.domain);ac=ac.concat(ab,Z,Q);return ac}(),data:V});Y.rangeFields=W;Y.domainFields=[k.finals.data.domain];Y.trendLineFields=ab;Y.targetLineFields=Z;Y.baseLineFields=Q;Y.numericFields=[].concat(Y.rangeFields,Y.trendLineFields,Y.targetLineFields,Y.baseLineFields);Y.getMaximum=function(){var ad=[];for(var ac=0;ac<Y.numericFields.length;ac++){ad.push(Y.max(Y.numericFields[ac]))}return Ext.Array.max(ad)};Y.getMinimum=function(){var ad=[];for(var ac=0;ac<Y.numericFields.length;ac++){ad.push(Y.max(Y.numericFields[ac]))}return Ext.Array.min(ad)};Y.getMaximumSum=function(){var ad=[],ac=0;Y.each(function(ae){ac=0;for(var af=0;af<Y.rangeFields.length;af++){ac+=ae.data[Y.rangeFields[af]]}ad.push(ac)});return Ext.Array.max(ad)};if(DV.isDebug){console.log("data",V);console.log("rangeFields",Y.rangeFields);console.log("domainFields",Y.domainFields);console.log("trendLineFields",Y.trendLineFields);console.log("targetLineFields",Y.targetLineFields);console.log("baseLineFields",Y.baseLineFields)}return Y};H=function(N){var M=k.finals.chart,P=N.getMinimum(),Q,O;if((A.type===M.stackedcolumn||A.type===M.stackedbar)&&(A.showTrendLine||A.targetLineValue||A.baseLineValue)){var L=[N.getMaximum(),N.getMaximumSum()];Q=Math.ceil(Ext.Array.max(L)*1.1);Q=Math.floor(Q/10)*10}O={type:"Numeric",position:"left",fields:N.numericFields,minimum:P<0?P:0,label:{renderer:Ext.util.Format.numberRenderer("0,0")},grid:{odd:{opacity:1,stroke:"#aaa","stroke-width":0.1},even:{opacity:1,stroke:"#aaa","stroke-width":0.1}}};if(Q){O.maximum=Q}if(A.rangeAxisTitle){O.title=A.rangeAxisTitle}return O};J=function(L){var M={type:"Category",position:"bottom",fields:L.domainFields,label:{rotate:{degrees:330}}};if(A.domainAxisTitle){M.title=A.domainAxisTitle}return M};q=function(M){var L=[];for(var N=0,P,O;N<M.rangeFields.length;N++){P=M.rangeFields[N];L.push(u.metaData.names[P])}return L};x=function(M){var L={type:"column",axis:"left",xField:M.domainFields,yField:M.rangeFields,style:{opacity:0.8,lineWidth:3},markerConfig:{type:"circle",radius:4},tips:v(),title:q(M)};if(A.showValues){L.label={display:"outside","text-anchor":"middle",field:M.rangeFields,font:k.chart.style.fontFamily}}return L};G=function(M){var L=[];for(var N=0;N<M.trendLineFields.length;N++){L.push({type:"line",axis:"left",xField:M.domainFields,yField:M.trendLineFields[N],style:{opacity:0.8,lineWidth:3,"stroke-dasharray":8},markerConfig:{type:"circle",radius:0},title:u.metaData.names[M.trendLineFields[N]]})}return L};C=function(L){return{type:"line",axis:"left",xField:L.domainFields,yField:L.targetLineFields,style:{opacity:1,lineWidth:2,"stroke-width":1,stroke:"#041423"},showMarkers:false,title:(Ext.isString(A.targetLineTitle)?A.targetLineTitle:DV.i18n.target)+" ("+A.targetLineValue+")"}};I=function(L){return{type:"line",axis:"left",xField:L.domainFields,yField:L.baseLineFields,style:{opacity:1,lineWidth:2,"stroke-width":1,stroke:"#041423"},showMarkers:false,title:(Ext.isString(A.baseLineTitle)?A.baseLineTitle:DV.i18n.base)+" ("+A.baseLineValue+")"}};v=function(){return{trackMouse:true,cls:"dv-chart-tips",renderer:function(L,M){this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">'+M.value[1]+'</div><div style="font-size:10px">'+L.data[k.finals.data.domain]+"</div></div>")}}};B=function(M){var L=k.chart.theme.dv1.slice(0,M.rangeFields.length);if(A.targetLineValue||A.baseLineValue){L.push("#051a2e")}if(A.targetLineValue){L.push("#051a2e")}if(A.baseLineValue){L.push("#051a2e")}Ext.chart.theme.dv1=Ext.extend(Ext.chart.theme.Base,{constructor:function(N){Ext.chart.theme.Base.prototype.constructor.call(this,Ext.apply({seriesThemes:L,colors:L},N))}})};r=function(V){var R=30,Q=7,W,X=0,S="",N,U=false,P="top",T=0;if(A.type===k.finals.chart.pie){W=V.getCount();V.each(function(Y){S+=Y.data[V.domainFields[0]]})}else{W=V.rangeFields.length;for(var O=0,M,L;O<V.rangeFields.length;O++){if(V.rangeFields[O].indexOf("-")!==-1){L=V.rangeFields[O].split("-");M=u.metaData.names[L[0]]+" "+u.metaData.names[L[1]]}else{M=u.metaData.names[V.rangeFields[O]]}S+=M}}X=S.length;N=(W*R)+(X*Q);if(N>K.app.centerRegion.getWidth()-50){U=true;P="right"}if(P==="right"){T=5}return Ext.create("Ext.chart.Legend",{position:P,isVertical:U,labelFont:"13px "+k.chart.style.fontFamily,boxStroke:"#ffffff",boxStrokeWidth:0,padding:T})};F=function(M){var O=A.filterIds,L=[],Q="",P;if(A.type===k.finals.chart.pie){O=O.concat(A.columnIds)}if(Ext.isArray(O)&&O.length){for(var N=0;N<O.length;N++){Q+=u.metaData.names[O[N]];Q+=N<O.length-1?", ":""}}if(A.title){Q=A.title}P=(K.app.centerRegion.getWidth()/Q.length)<11.6?13:18;return Ext.create("Ext.draw.Sprite",{type:"text",text:Q,font:"bold "+P+"px "+k.chart.style.fontFamily,fill:"#111",height:20,y:20})};E=function(){return function(){this.animate=false;this.setWidth(K.app.centerRegion.getWidth()-15);this.setHeight(K.app.centerRegion.getHeight()-40);this.animate=true}};z=function(){return function(){if(this.items){var P=this.items[0],M=this.legend,O,L;if(this.legend.position==="top"){O=M.x+(M.width/2);L=O-(P.el.getWidth()/2)}else{var N=M?M.width:0;L=(this.width/2)-(P.el.getWidth()/2)}P.setAttributes({x:L},true)}}};y=function(L,Q,N,P){var O,M={store:L,axes:Q,series:N,animate:true,shadow:false,insetPadding:35,width:K.app.centerRegion.getWidth()-15,height:K.app.centerRegion.getHeight()-40,theme:P||"dv1"};if(!A.hideLegend){M.legend=r(L);if(M.legend.position==="right"){M.insetPadding=40}}if(!A.hideTitle){M.items=[F(L)]}else{M.insetPadding=10}O=Ext.create("Ext.chart.Chart",M);O.setChartSize=E();O.setTitlePosition=z();O.onViewportResize=function(){O.setChartSize();O.redraw();O.setTitlePosition()};O.on("afterrender",function(){O.setTitlePosition()});return O};p.column=function(){var L=t(),P=H(L),O=J(L),N=[P,O],M=[x(L)];if(A.showTrendLine){M=G(L).concat(M)}if(A.targetLineValue){M.push(C(L))}if(A.baseLineValue){M.push(I(L))}B(L);return y(L,N,M)};p.stackedcolumn=function(){var M=this.column();for(var L=0,N;L<M.series.items.length;L++){N=M.series.items[L];if(N.type===k.finals.chart.column){N.stacked=true}}return M};p.bar=function(){var T=t(),P=H(T),Q=J(T),S,O=x(T),L,M,U,R;P.position="bottom";Q.position="left";S=[P,Q];O.type="bar";O.axis="bottom";if(A.showValues){O.label={display:"outside","text-anchor":"middle",field:T.rangeFields}}O=[O];if(A.showTrendLine){L=G(T);for(var N=0;N<L.length;N++){L[N].axis="bottom";L[N].xField=T.trendLineFields[N];L[N].yField=T.domainFields}O=L.concat(O)}if(A.targetLineValue){M=C(T);M.axis="bottom";M.xField=T.targetLineFields;M.yField=T.domainFields;O.push(M)}if(A.baseLineValue){U=I(T);U.axis="bottom";U.xField=T.baseLineFields;U.yField=T.domainFields;O.push(U)}B(T);return y(T,S,O)};p.stackedbar=function(){var M=this.bar();for(var L=0,N;L<M.series.items.length;L++){N=M.series.items[L];if(N.type===k.finals.chart.bar){N.stacked=true}}return M};p.line=function(){var S=t(),O=H(S),P=J(S),Q=[O,P],M=[],L=k.chart.theme.dv1.slice(0,S.rangeFields.length),R=q(S);for(var N=0,T;N<S.rangeFields.length;N++){T={type:"line",axis:"left",xField:S.domainFields,yField:S.rangeFields[N],style:{opacity:0.8,lineWidth:3},markerConfig:{type:"circle",radius:4},tips:v(),title:R[N]};M.push(T)}if(A.showTrendLine){M=G(S).concat(M);L=L.concat(L)}if(A.targetLineValue){M.push(C(S));L.push("#051a2e")}if(A.baseLineValue){M.push(I(S));L.push("#051a2e")}Ext.chart.theme.dv1=Ext.extend(Ext.chart.theme.Base,{constructor:function(U){Ext.chart.theme.Base.prototype.constructor.call(this,Ext.apply({seriesThemes:L,colors:L},U))}});return y(S,Q,M)};p.area=function(){var L=t(),P=H(L),O=J(L),N=[P,O],M=x(L);M.type="area";M.style.opacity=0.7;M.style.lineWidth=0;delete M.label;delete M.tips;M=[M];if(A.showTrendLine){M=G(L).concat(M)}if(A.targetLineValue){M.push(C(L))}if(A.baseLineValue){M.push(I(L))}B(L);return y(L,N,M)};p.pie=function(){var M=t(),O,L,P,N={field:k.finals.data.domain};if(A.showValues){N.display="middle";N.contrast=true;N.font="14px "+k.chart.style.fontFamily;N.renderer=function(R){var Q=M.getAt(M.findExact(k.finals.data.domain,R));return Q.data[M.rangeFields[0]]}}O=[{type:"pie",field:M.rangeFields[0],donut:7,showInLegend:true,highlight:{segment:{margin:5}},label:N,style:{opacity:0.8,stroke:"#555"},tips:{trackMouse:true,cls:"dv-chart-tips",renderer:function(Q){this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">'+Q.data[M.rangeFields[0]]+'</div><div style="font-size:10px">'+Q.data[k.finals.data.domain]+"</div></div>")}}}];L=k.chart.theme.dv1.slice(0,u.nameHeaderMap[A.rowDimensionNames[0]].ids.length);Ext.chart.theme.dv1=Ext.extend(Ext.chart.theme.Base,{constructor:function(Q){Ext.chart.theme.Base.prototype.constructor.call(this,Ext.apply({seriesThemes:L,colors:L},Q))}});P=y(M,null,O);P.insetPadding=40;P.shadow=true;return P};p.radar=function(){var L=t(),R=[],O=[],N=q(L),P;R.push({type:"Radial",position:"radial",label:{display:true}});for(var M=0,Q;M<L.rangeFields.length;M++){Q={showInLegend:true,type:"radar",xField:L.domainFields,yField:L.rangeFields[M],style:{opacity:0.5},tips:v(),title:N[M]};if(A.showValues){Q.label={display:"over",field:L.rangeFields[M]}}O.push(Q)}P=y(L,R,O,"Category2");P.insetPadding=40;P.height=K.app.centerRegion.getHeight()-80;P.setChartSize=function(){this.animate=false;this.setWidth(K.app.centerRegion.getWidth());this.setHeight(K.app.centerRegion.getHeight()-80);this.animate=true};return P};return p[A.type]()}}());(function(){if(Ext.isArray(o.dimensions)){l.prototype.array.sort(o.dimensions);for(var p=0,q;p<o.dimensions.length;p++){q=o.dimensions[p];q.dimensionName=q.id;q.objectName=k.finals.dimension.dimension.objectName;k.finals.dimension.objectNameMap[q.id]=q}}l.prototype.array.sort(o.user.ouc)}());return{conf:k,api:m,support:l,service:i,web:j,init:o}};var d=".dv-chart-tips { border-radius: 2px; padding: 0px 3px 1px; border: 2px solid #000; background-color: #000; } \n";d+=".dv-chart-tips .x-tip-body { background-color: #000; font-size: 13px; font-weight: normal; color: #fff; -webkit-text-stroke: 0; } \n";d+=".dv-chart-tips .x-tip-body div { font-family: arial,sans-serif,ubuntu,consolas !important; } \n";d+=".x-mask-msg { padding: 0; \n border: 0 none; background-image: none; background-color: transparent; } \n";d+=".x-mask-msg div { background-position: 11px center; } \n";d+=".x-mask-msg .x-mask-loading { border: 0 none; \n background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n";Ext.util.CSS.createStyleSheet(d);DV.i18n={target:"Target",base:"Base",trend:"Trend"};DV.plugin={};var g={user:{}},f=[],h=false,c=false,a,e;a=function(j){var o=false,n=[],m=0,l;l=function(){if(++m===n.length){c=true;for(var p=0;p<f.length;p++){e(f[p])}f=[]}};n.push({url:j+"/api/system/context.jsonp",success:function(i){g.contextPath=i.contextPath;l()}});n.push({url:j+"/api/organisationUnits.jsonp?userOnly=true&viewClass=detailed&links=false",success:function(p){var i=p.organisationUnits[0];g.user.ou=i.id;g.user.ouc=Ext.Array.pluck(i.children,"id");l()}});n.push({url:j+"/api/mapLegendSets.jsonp?viewClass=detailed&links=false&paging=false",success:function(i){g.legendSets=i.mapLegendSets;l()}});n.push({url:j+"/api/dimensions.jsonp?links=false&paging=false",success:function(i){g.dimensions=i.dimensions;l()}});for(var k=0;k<n.length;k++){Ext.data.JsonP.request(n[k])}};e=function(l){var n,k,j,i,m={core:{},app:{}};n=function(o){if(!Ext.isObject(o)){console.log("Chart configuration is not an object");return}if(!Ext.isString(o.el)){console.log("No element id provided");return}o.id=o.id||o.uid;return true};k=function(s){var t=s.core.init,r=s.core.api,q=s.core.support,o=s.core.service,p=s.core.web;t.el=l.el;p.chart=p.chart||{};p.chart.loadChart=function(u){if(!Ext.isString(u)){alert("Invalid chart id");return}Ext.data.JsonP.request({url:t.contextPath+"/api/charts/"+u+".jsonp?viewClass=dimensional&links=false",failure:function(v){window.open(t.contextPath+"/api/charts/"+u+".json?viewClass=dimensional&links=false","_blank")},success:function(w){var v=r.layout.Layout(w);if(v){p.chart.getData(v,true)}}})};p.chart.getData=function(v,w){var u,x;if(!v){return}u=o.layout.getExtendedLayout(v);x=p.analytics.getParamString(u,true);p.mask.show(s.app.centerRegion);Ext.data.JsonP.request({url:t.contextPath+"/api/analytics.jsonp"+x,timeout:60000,headers:{"Content-Type":"application/json",Accepts:"application/json"},disableCaching:false,failure:function(y){p.mask.hide(s.app.centerRegion);window.open(t.contextPath+"/api/analytics.json"+x,"_blank")},success:function(z){var y=r.response.Response(z);if(!y){p.mask.hide(s.app.centerRegion);return}u=o.layout.getSyncronizedXLayout(u,y);if(!u){p.mask.hide(s.app.centerRegion);return}s.app.paramString=x;p.chart.getChart(v,u,y,w)}})};p.chart.getChart=function(z,w,v,A){var B,y,u,x;if(!w){w=o.layout.getExtendedLayout(z)}B=o.response.getExtendedResponse(w,v);s.app.layout=z;s.app.xLayout=w;s.app.response=v;s.app.xResponse=B;s.app.chart=s.core.web.chart.createChart(s);s.app.centerRegion.removeAll();s.app.centerRegion.add(s.app.chart);p.mask.hide(s.app.centerRegion)}};j=function(){var r=Ext.get(m.core.init.el),u,o,v=parseInt(r.getStyle("border-left-width"))+parseInt(r.getStyle("border-right-width")),s=parseInt(r.getStyle("border-top-width"))+parseInt(r.getStyle("border-bottom-width")),t=parseInt(r.getStyle("padding-left"))+parseInt(r.getStyle("padding-right")),p=parseInt(r.getStyle("padding-top"))+parseInt(r.getStyle("padding-bottom")),q=r.getWidth()-v-t,w=r.getHeight()-s-p;o=Ext.create("Ext.panel.Panel",{renderTo:r,bodyStyle:"border: 0 none",width:l.width||q,height:l.height||w,layout:"fit"});return{centerRegion:o}};i=function(){if(!n(l)){return}m.core=DV.getCore(Ext.clone(g));k(m);m.app.viewport=j();m.app.centerRegion=m.app.viewport.centerRegion;if(l.id){m.core.web.chart.loadChart(l.id)}else{layout=m.core.api.layout.Layout(l);if(!layout){return}m.core.web.chart.getData(layout)}}()};DV.plugin.getChart=function(i){if(Ext.isString(i.url)&&i.url.split("").pop()==="/"){i.url=i.url.substr(0,i.url.length-1)}if(c){e(i)}else{f.push(i);if(!h){h=true;a(i.url)}}};DHIS=Ext.isObject(window.DHIS)?DHIS:{};DHIS.getChart=DV.plugin.getChart});
