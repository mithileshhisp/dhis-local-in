'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get( '../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($http) {
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get( '../api/programs/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        },       
        
        getMine: function(type){ 
            if( !programsPromise ){
                programsPromise = $http.get( '../api/me/programs?includeDescendants=true&type='+type).then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        },
        
        getEventProgramsByOrgUnit: function(orgUnit, type){
                       
            var promise = $http.get(  '../api/programs.json?orgUnit=' + orgUnit + '&type=' + type ).then(function(response){
                programs = response.data;
                return programs;
            });            
            return promise;
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, storage) {  
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get(  '../api/programStages/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                   programStage = response.data.id;

                   //store locally - might need them for event data values
                   angular.forEach(response.data.programStageDataElements, function(prStDe){      
                       storage.set(prStDe.dataElement.id, prStDe);                       
                   });
                   
                   return response.data;
                });
            }
            return promise;
        }
    };    
})

/*Orgunit service for local db */
.service('OrgUnitService', function($window, $q){
    
    var indexedDB = $window.indexedDB;
    var db = null;
    
    var open = function(){
        var deferred = $q.defer();
        
        var request = indexedDB.open("dhis2ou");
        
        request.onsuccess = function(e) {
          db = e.target.result;
          deferred.resolve();
        };

        request.onerror = function(){
          deferred.reject();
        };

        return deferred.promise;
    };
    
    var get = function(uid){
        
        var deferred = $q.defer();
        
        if( db === null){
            deferred.reject("DB not opened");
        }
        else{
            var tx = db.transaction(["ou"]);
            var store = tx.objectStore("ou");
            var query = store.get(uid);
                
            query.onsuccess = function(e){
                deferred.resolve(e.target.result);
            };
        }
        return deferred.promise;
    };
    
    return {
        open: open,
        get: get
    };    
})

/* Service to deal with enrollment */
.service('EnrollmentService', function($http) {
    
    return {        
        get: function( entity ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return response.data;
            });
            return promise;
        },
        enroll: function( enrollment ){
            var promise = $http.post(  '../api/enrollments', enrollment ).then(function(response){
                return response.data;
            });
            return promise;
        }        
    };   
})

/* Service for getting tracked entity instances */
.factory('TrackedEntityInstanceService', function($http, AttributesFactory) {
    
    var promise;
    return {
        
        get: function(entityUid) {
            promise = $http.get(  '../api/trackedEntityInstances/' +  entityUid ).then(function(response){                                
                return response.data;
            });            
            return promise;
        },
        
        getByOrgUnitAndProgram: function(orgUnitUid, programUid) {
            
            //var attributes = AttributesFactory.convertListingForToQuery();
            var url = '../api/trackedEntityInstances.json?ou=' + orgUnitUid + '&program=' + programUid;
            
            promise = $http.get( url ).then(function(response){
               
                return entityFormatter(response.data);
            });            
            return promise;
        },
        getByOrgUnit: function(orgUnitUid) {
            
            //var attributes = AttributesFactory.convertListingForToQuery();
            var url =  '../api/trackedEntityInstances.json?ou=' + orgUnitUid;
            
            promise = $http.get( url ).then(function(response){
                                
                return entityFormatter(response.data);
            });            
            return promise;
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function(storage) { 
    
    return {
        getAll: function(){  

            var attributes = storage.get('ATTRIBUTES');
            
            if(attributes){
                return attributes;
            }                
            return; 
        },        
        getForListing: function(){
            
            var attributes = [];
            
            angular.forEach(this.getAll(), function(attribute) {
                if (attribute.displayInListNoProgram) {
                    attributes.push(attribute);
                }
            });           

            return attributes;
        },
        convertListingForToQuery: function(){
            var param = '';
            angular.forEach(this.getForListing(), function(attribute) {
                param +=  '&' + 'attribute=' + attribute.id;
            });
            
            return param;
        }
    };
})


/* factory for handling events */
.factory('DHIS2EventFactory', function($http) {   
    
    return {
        
        getByEntity: function(entity, orgUnit, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        
        getByStage: function(orgUnit, programStage){
            var promise = $http.get( '../api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&paging=false')
                    .then(function(response){
                        
                return response.data.events;             
        
            }, function(){
                
                return dhis2.ec.storageManager.getEvents(orgUnit, programStage);
                
            });            
            
            return promise;
        },
        
        get: function(eventUid){
            
            var promise = $http.get( '../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;
                
            }, function(){
                return dhis2.ec.storageManager.getEvent(eventUid);
            });            
            return promise;
        },
        
        create: function(dhis2Event){
            
            var e = angular.copy(dhis2Event);            
            dhis2.ec.storageManager.saveEvent(e);            
        
            var promise = $http.post( '../api/events.json', dhis2Event).then(function(response){
                dhis2.ec.storageManager.clearEvent(e);
                return response.data;
            }, function(){
                return {importSummaries: [{status: 'SUCCESS', reference: e.event}]};
            });
            return promise;            
        },
        
        delete: function(dhis2Event){
            dhis2.ec.storageManager.clearEvent(dhis2Event);
            var promise = $http.delete( '../api/events/' + dhis2Event.event).then(function(response){
                return response.data;
            }, function(){                
            });
            return promise;           
        },
    
        update: function(dhis2Event){   
            dhis2.ec.storageManager.saveEvent(dhis2Event);
            var promise = $http.put( '../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                dhis2.ec.storageManager.clearEvent(dhis2Event);
                return response.data;
            });
            return promise;
        },
        
        updateForSingleValue: function(singleValue, fullValue){                
            
            dhis2.ec.storageManager.saveEvent(fullValue);            
            
            var promise = $http.put( '../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                dhis2.ec.storageManager.clearEvent(fullValue);
                return response.data;
            }, function(){                
            });
            return promise;
        }
    };    
})

/* Modal service for user interaction */
.service('ModalService', ['$modal', function($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: 'views/modal.html'
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function(customModalDefaults, customModalOptions) {
            if (!customModalDefaults)
                customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function(customModalDefaults, customModalOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = function($scope, $modalInstance) {
                    $scope.modalOptions = tempModalOptions;
                    $scope.modalOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };
                    $scope.modalOptions.close = function(result) {
                        $modalInstance.dismiss('cancel');
                    };
                };
            }

            return $modal.open(tempModalDefaults).result;
        };

    }])

/* Dialog service for user interaction */
.service('DialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,            
            templateUrl: 'views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

/* current item selected from grid */
.service('SelectedEntity', function(){
    this.selectedEntity = '';
    
    this.setSelectedEntity = function(selectedEntity){  
        this.selectedEntity = selectedEntity;        
    };
    
    this.getSelectedEntity = function(){
        return this.selectedEntity;
    };
})

/* Translation service - gets logged in user profile for the server, 
 * and apply user's locale to translation
 */
.service('TranslationService', function($translate, storage){
    
    this.translate = function(){
        var profile = storage.get('USER_PROFILE');        
        if( profile ){        
            $translate.uses(profile.settings.keyUiLocale);
        }
    };
})

/* Pagination service */
.service('Paginator', function () {
    this.page = 0;
    this.rowsPerPage = 50;
    this.itemCount = 0;
    this.limitPerPage = 5;

    this.setPage = function (page) {
        if (page > this.pageCount()) {
            return;
        }

        this.page = page;
    };
    
    this.getPage = function(){
        return this.page;
    };
    
    this.getRowsPerPage = function(){
        return this.rowsPerPage;
    };

    this.nextPage = function () {
        if (this.isLastPage()) {
            return;
        }

        this.page++;
    };

    this.perviousPage = function () {
        if (this.isFirstPage()) {
            return;
        }

        this.page--;
    };

    this.firstPage = function () {
        this.page = 0;
    };

    this.lastPage = function () {
        this.page = this.pageCount() - 1;
    };

    this.isFirstPage = function () {
        return this.page == 0;
    };

    this.isLastPage = function () {
        return this.page == this.pageCount() - 1;
    };

    this.pageCount = function () {
        var count = Math.ceil(parseInt(this.itemCount, 10) / parseInt(this.rowsPerPage, 10)); 
        if (count === 1) { this.page = 0; } return count;
    };

    this.lowerLimit = function() { 
        var pageCountLimitPerPageDiff = this.pageCount() - this.limitPerPage;

        if (pageCountLimitPerPageDiff < 0) { 
            return 0; 
        }

        if (this.page > pageCountLimitPerPageDiff + 1) { 
            return pageCountLimitPerPageDiff; 
        } 

        var low = this.page - (Math.ceil(this.limitPerPage/2) - 1); 

        return Math.max(low, 0);
    };
})

/*this is just a hack - there should be better way */
.service('ValidDate', function(){    
    var dateValidation;    
    return {
        get: function(dt) {
            dateValidation = dt;
        },
        set: function() {    
            return dateValidation;
        }
    };
            
});

/*
* Helper functions
*/
//This is is to have consistent display of entities and attributes
//as every entity might not have value for every attribute.                
function entityFormatter(grid){
    
    if(!grid || !grid.rows){
        return;
    }   
   
    //grid.headers[0-4] = Instance, Created, Last updated, Org unit, Tracked entity
    //grid.headers[5..] = Attribute, Attribute,.... 
    var attributes = [];
    for(var i=5; i<grid.headers.length; i++){
        attributes.push({id: grid.headers[i].name, name: grid.headers[i].column});
    }
    
    var entityList = [];
    
    angular.forEach(grid.rows, function(row){
        var entity = {};
        
        entity.id = row[0];
        entity.orgUnit = row[3];
        entity.type = row[4];        
        
        for(var i=5; i<row.length; i++){
            entity[grid.headers[i].name] = row[i];            
        }
        
        entityList.push(entity);        
        
    });
    
    return {headers: attributes, rows: entityList};
}