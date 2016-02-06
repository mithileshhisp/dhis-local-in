/**
 * Created by hisp on 8/12/15.
 */
trackerCapture


    .service('AjaxCalls', function($http) {
        return{

            getTEIbyId : function(id){
                var promise = $http.get(  '../api/trackedEntityInstances/'+id).then(function(response){

                    return response.data;
                });
                return promise;
            },
            getEventbyId : function(id){
                var promise = $http.get(  '../api/events/'+id).then(function(response){

                    return response.data;
                });
                return promise;
            },
            getNoProgramAttributes : function(){
                var promise = $http.get(  '../api/trackedEntityAttributes.json?paging=false&filter=displayInListNoProgram:eq:true&fields=:all').then(function(response){
                    return response.data;
                });
                return promise;
            },
            getTrackedEntities : function(){
                var promise = $http.get(  '../api/trackedEntities.json?paging=false').then(function(response){
                    return response.data;
                });
                return promise;
            },
            getRootOrgUnit : function(){
                var promise = $http.get(  '../api/organisationUnits?filter=level:eq:1').then(function(response){
                    return response.data;
                });
                return promise;
            },
            getAssociationWidgetAttributes : function(){
                var promise = $http.get(  '../api/trackedEntityAttributes?fields=*,attributeValues[*,attribute[id,name,code]]&paging=false').then(function(response){
                    var associationWidgets = [];

                    if (!response.data.trackedEntityAttributes)
                        return associationWidgets;

                    for (var i=0;i<response.data.trackedEntityAttributes.length;i++){
                        if (response.data.trackedEntityAttributes[i].attributeValues)
                            for (var j=0;j<response.data.trackedEntityAttributes[i].attributeValues.length;j++){
                                if (response.data.trackedEntityAttributes[i].attributeValues[j].attribute.code=="ToBeShownInAssociationWidget"){
                                    if (response.data.trackedEntityAttributes[i].attributeValues[j].value){
                                        associationWidgets.push(response.data.trackedEntityAttributes[i]);
                                    }
                                }
                            }
                    }
                    return associationWidgets;
                });
                return promise;
            }
        }

    })
    .service('utilityService', function() {
        return{
            prepareIdToObjectMap : function(object,id){
                var map = [];
                for (var i=0;i<object.length;i++){
                    map[object[i][id]] = object[i];
                }
                return map;
            }
        }

    })