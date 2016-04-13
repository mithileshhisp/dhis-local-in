'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($rootScope,
                $scope,
                $location,
                Paginator,
                TranslationService, 
                SelectedEntity,
                storage,
                AttributesFactory,
                TrackedEntityInstanceService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    $scope.ouMode = 'SELECTED';
    
    //Filtering
    $scope.reverse = false;
    $scope.filterText = {}; 
    $scope.currentFilter;
    
    //Paging
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;   
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchField = {title: 'search', isOpen: false};     
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {
        
        if( angular.isObject($scope.selectedOrgUnit)){                  
            
            $scope.trackedEntityList = [];
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();
            $scope.attributes = storage.get('ATTRIBUTES');            
        }
    });
    
    //get events for the selected program (and org unit)
    $scope.loadTrackedEntities = function(){
        
        $scope.showSearchDiv = false;
        
        $scope.trackedEntityList = null;
        
        $scope.gridColumns = AttributesFactory.getForListing();

        //generate grid column for the selected program
        angular.forEach($scope.gridColumns, function(gridColumn){
            gridColumn.showFilter =  false;
            gridColumn.hide = false;                   
            if(gridColumn.type === 'date'){
                 $scope.filterText[gridColumn.id]= {start: '', end: ''};
            }
        });
            
        //Load entities for the selected orgunit
        TrackedEntityInstanceService.getByOrgUnit($scope.selectedOrgUnit.id).then(function(data){
            $scope.trackedEntityList = data;                
        });
    };
    
    $scope.clearEntities = function(){
        $scope.trackedEntityList = null;
    };
    
    $scope.showRegistration = function(){
        
    };  
    
    $scope.showSearch = function(){        
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };
    
    $scope.hideSearch = function(){        
        $scope.showSearchDiv = false;
        $rootScope.showAdvancedSearchDiv = false;
    };
    
    $scope.closeSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };   
    
    $scope.sortGrid = function(gridHeader){
        
        if ($scope.sortHeader === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }
        
        $scope.sortHeader = gridHeader.id;
        $scope.reverse = false;    
    };    
    
    $scope.filterInGrid = function(gridColumn){
        
        $scope.currentFilter = gridColumn;
        for(var i=0; i<$scope.gridColumns.length; i++){
            
            //toggle the selected grid column's filter
            if($scope.gridColumns[i].id === gridColumn.id){
                $scope.gridColumns[i].showFilter = !$scope.gridColumns[i].showFilter;
            }            
            else{
                $scope.gridColumns[i].showFilter = false;
            }
        }
    };    
    
    $scope.showDashboard = function(currentEntity){       
        SelectedEntity.setSelectedEntity(currentEntity);
        storage.set('SELECTED_OU', $scope.selectedOrgUnit);        
        $location.path('/dashboard').search({selectedEntityId: currentEntity.id});                                    
    };   
    
    $scope.search = function(){       
        console.log('the search is:  ', $scope.attributes);
        console.log('the mode is:  ', $scope.ouMode);
    };
       
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
})

//Controller for the search section
.controller('SearchController',
        function($rootScope,
                $scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    //search attibutes
    $scope.attributes = storage.get('ATTRIBUTES');     
    $scope.availableAttributes = [];
    $scope.selectedAttributes = [];
    $rootScope.showAdvancedSearchDiv = false;
    $scope.ouMode = 'SELECTED';
    
    angular.forEach($scope.attributes, function(attribute){
        $scope.availableAttributes.push(attribute);
    });
    
    $scope.moveToSelected = function(attribute, fromView){  
        
        if(attribute){
            
            if(fromView){
                attribute = attribute[0];
            }

            if(angular.isObject(attribute)) {            
                if($scope.selectedAttributes.indexOf(attribute) === -1){       

                    var filter = {operands: [], operand: '', values: [], value: ''};

                    if(attribute.valueType === 'number' || attribute.valueType === 'date'){
                        filter.operands = ['=', '>','>=', '<', '<=', '!=' ];
                        filter.operand = filter.operands[0];
                    }

                    else if(attribute.valueType === 'bool'){
                        filter.operands = ['One of'];
                        filter.operand = filter.operands[0];
                        filter.values = ['No', 'Yes'];
                        filter.value = filter.values[0];
                    }

                    else if(attribute.valueType === 'combo'){
                        filter.operands = ['One of'];
                        filter.operand = filter.operands[0];
                        filter.values = attribute.optionSet.options;
                        filter.value = filter.values[0];
                    }
                    else{
                        filter.operands = ['like', 'not_like' ];
                        filter.operand = filter.operands[0];
                    }

                    attribute.filters = [filter];
                    $scope.selectedAttributes.push(attribute);
                    var index = $scope.availableAttributes.indexOf(attribute);
                    $scope.availableAttributes.splice(index, 1);
                }
            }            
        }         
    };
    
    $scope.moveAllToSelected = function(){     
        for(var i=0; i<$scope.availableAttributes.length;)
        angular.forEach($scope.availableAttributes, function(attribute){
            //$scope.selectedAttributes.push(attribute);            
            $scope.moveToSelected(attribute, false);
        });        
        
        $scope.availableAttributes = [];
    };
    
    $scope.addFilter = function(attribute){
        
        var filter = {operands: [], operand: '', values: [], value: ''};
                    
        if(attribute.valueType === 'number' || attribute.valueType === 'date'){
            filter.operands = ['=', '>','>=', '<', '<=', '!=' ];
            filter.operand = filter.operands[0];
        }

        else if(attribute.valueType === 'bool'){
            filter.operands = ['One of'];
            filter.operand = filter.operands[0];
            filter.values = ['No', 'Yes'];
            filter.value = filter.values[0];
        }

        else if(attribute.valueType === 'combo'){
            filter.operands = ['One of'];
            filter.operand = filter.operands[0];
            filter.values = attribute.optionSet.options;
            filter.value = filter.values[0];
        }
        else{
            filter.operands = ['like', 'not_like' ];
            filter.operand = filter.operands[0];
        }

        attribute.filters.push(filter);

        //console.log('I am going to add filter for dataElement:  ', dataElement);  
    };
    
    $scope.removeFilter = function(filter, attribute){
        
        var index = attribute.filters.indexOf(filter);        
        attribute.filters.splice(index, 1);

        if(attribute.filters.length === 0 || angular.isUndefined(attribute.filters.length)){
            index = $scope.selectedAttributes.indexOf(attribute);
            $scope.selectedAttributes.splice(index, 1);
            $scope.availableAttributes.push(attribute);
        }
        
    };
    
    $scope.showAdvancedSearch = function(){        
        $rootScope.showAdvancedSearchDiv = !$rootScope.showAdvancedSearchDiv;
    };
    
    $scope.hideAdvancedSearch = function(){        
        $rootScope.showAdvancedSearchDiv = false;
    };
    
    $scope.closeAdvancedSearch = function(){
        $rootScope.showAdvancedSearchDiv = !$rootScope.showAdvancedSearchDiv;
    }; 
    
    $scope.search = function(){
        console.log($scope.attributes);
        console.log($scope.ouMode);
    };
    
    $scope.test = function(){
        console.log('the mode is:  ', $scope.ouMode);
    };                
    
})

//Controller for dashboard
.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                storage,
                TrackedEntityInstanceService,      
                SelectedEntity,
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard items   
    $rootScope.dashboardWidgets = {bigger: [], smaller: []};       
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "views/enrollment.html", show: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "views/dataentry.html", show: true};
    $rootScope.selectedWidget = {title: 'current_selections', view: "views/selected.html", show: false};
    $rootScope.profileWidget = {title: 'profile', view: "views/profile.html", show: true};
    $rootScope.notesWidget = {title: 'notes', view: "views/notes.html", show: true};    
   
    $rootScope.dashboardWidgets.bigger.push($rootScope.enrollmentWidget);
    $rootScope.dashboardWidgets.bigger.push($rootScope.dataentryWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.selectedWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.profileWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.notesWidget);
    
    //selections
    $scope.selectedEntityId = ($location.search()).selectedEntityId;       
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');     
        
    if( $scope.selectedEntityId ){
        
        //Fetch the selected entity
        TrackedEntityInstanceService.get($scope.selectedEntityId).then(function(data){
            $scope.selectedEntity = data;    
            SelectedEntity.setSelectedEntity($scope.selectedEntity);
            //broadcast selected entity for dashboard controllers
            $rootScope.$broadcast('selectedEntity', {});    

        });       
    }   
    
    $scope.back = function(){
        $location.path('/');
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
    
    $scope.removeWidget = function(widget){        
        widget.show = false;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "views/widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };   

})

//Controller for the profile section
.controller('ProfileController',
        function($scope,                
                storage,
                SelectedEntity,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected entity
    $scope.$on('selectedEntity', function(event, args) {        
        
        $scope.selectedEntity = SelectedEntity.getSelectedEntity();        

        $scope.trackedEntity = storage.get($scope.selectedEntity.trackedEntity);       
    });
})

//Controller for the enrollment section
.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                storage,
                SelectedEntity,
                EnrollmentService,
                TranslationService) {

    TranslationService.translate();
    
    //selected org unit
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');  
    
    //programs for enrollment
    $scope.enrollments = [];
    $scope.programs = []; 
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {     
        
        $scope.selectedEntity = SelectedEntity.getSelectedEntity();      
        
        angular.forEach(storage.get('TRACKER_PROGRAMS'), function(program){
            program = storage.get(program.id);
            if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
               program.trackedEntity.id === $scope.selectedEntity.trackedEntity){
                $scope.programs.push(program);
            }
        });        
    }); 
    
    $scope.loadEvents = function() {
        
        if($scope.selectedProgram){
            
            $scope.selectedEnrollment = '';           
            
            EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
                $scope.enrollments = data.enrollmentList;                
                
                angular.forEach($scope.enrollments, function(enrollment){
                    if(enrollment.program === $scope.selectedProgram.id ){
                        $scope.selectedEnrollment = enrollment;
                    }
                }); 
            
                $scope.programStages = [];

                angular.forEach($scope.selectedProgram.programStages, function(stage){
                   $scope.programStages.push(storage.get(stage.id)); 
                });

                if($scope.selectedEnrollment){
                    $scope.selectedEnrollment.dateOfIncident = $filter('date')($scope.selectedEnrollment.dateOfIncident, 'yyyy-MM-dd');
                }
                
                $rootScope.$broadcast('dataentry', {selectedEntity: $scope.selectedEntity,
                                                    selectedProgramId: $scope.selectedProgram.id,
                                                    selectedEnrollment: $scope.selectedEnrollment});
            });            
        }        
    };
})

//Controller for the data entry section
.controller('DataEntryController',
        function($scope, 
                $filter,
                orderByFilter,
                storage,
                DHIS2EventFactory,
                OrgUnitService,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
    //selected org unit
    $scope.selectedOrgUnit = storage.get('SELECTED_OU'); 
    
    //listen for the selected items
    $scope.$on('dataentry', function(event, args) {  

        $scope.currentEvent = null;
        
        $scope.dhis2Events = [];       
    
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedEnrollment = args.selectedEnrollment;
        
        if($scope.selectedOrgUnit && $scope.selectedProgramId && $scope.selectedEntity ){
            
            DHIS2EventFactory.getByEntity($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgramId).then(function(data){
                $scope.dhis2Events = data;
                
                if(angular.isUndefined($scope.dhis2Events)){
                    
                    $scope.dhis2Events = [];
                    
                    console.log('need to create new ones:  ', $scope.selectedEnrollment);
                    
                    if($scope.selectedEnrollment.status == 'ACTIVE'){
                        //create events for the selected enrollment
                        var program = storage.get($scope.selectedProgramId);
                        var programStages = [];
                        
                        angular.forEach(program.programStages, function(ps){  
                            
                            programStages.push(storage.get(ps.id));
                            var dhis2Event = {programStage: ps.id, 
                                              orgUnit: $scope.selectedOrgUnitId, 
                                              eventDate: moment(),
                                              name: ps.name,
                                              status: 'ACTIVE'};
                            
                            var date = moment();                        
                            
                            if( moment().add('days', ps.minDaysFromStart).isBefore(date)){
                                dhis2Event.statusColor = 'stage-overdue';
                            }
                            else{
                                dhis2Event.statusColor = 'stage-on-time';
                            }                      
                            
                            $scope.dhis2Events.push(dhis2Event);
                        });
                        
                        console.log('the stages are:  ', $scope.dhis2Events);
                    }
                }
                
                angular.forEach($scope.dhis2Events, function(dhis2Event){
                        
                    dhis2Event.name = storage.get(dhis2Event.programStage).name;
                    dhis2Event.eventDate = moment(dhis2Event.eventDate, 'YYYY-MM-DD')._d;
                    dhis2Event.eventDate = Date.parse(dhis2Event.eventDate);
                    dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');
                    
                    if(dhis2Event.status == 'COMPLETED'){
                        dhis2Event.statusColor = 'stage-completed';
                    }
                    else{
                        var date = moment(dhis2Event.eventDate, 'yyyy-MM-dd');
                        if(moment().isAfter(date)){
                            dhis2Event.statusColor = 'stage-overdue';
                        }
                        else{
                            dhis2Event.statusColor = 'stage-on-time';
                        }
                    } 
                    
                    if(dhis2Event.orgUnit){
                        OrgUnitService.open().then(function(){
                            OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                                if(ou){
                                    dhis2Event.orgUnitName = ou.n;
                                }                                                       
                            });                            
                        }); 
                    }                                                             
                });

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();              
            });          
        }
    });
    
    $scope.createNewEvent = function(){        
        console.log('need to create new event');        
    };
    
    $scope.showDataEntry = function(event){
        
        if(event){
            
            $scope.currentEvent = event;   
            $scope.currentStage = storage.get($scope.currentEvent.programStage); 
            
            angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){                
                $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            });
            
            angular.forEach($scope.currentEvent.dataValues, function(dataValue){
                var val = dataValue.value;
                var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                if( de && de.type == 'int' && val){
                    val = parseInt(val);
                }
        
                $scope.currentEvent[dataValue.dataElement] = val;
            });                   
        }     
    };    
})

//Controller for the dashboard widgets
.controller('DashboardWidgetsController', 
    function($scope, 
            $modalInstance,
            TranslationService){
    
    TranslationService.translate();
    
    $scope.close = function () {
        $modalInstance.close($scope.eventGridColumns);
    };       
})

//Controller for the notes section
.controller('NotesController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
})

//Controller for the selected section
.controller('SelectedInfoController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {
        
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;        
        
        if($scope.selectedProgramId){
            $scope.selectedProgram = storage.get($scope.selectedProgramId);        
        }
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selections = [];
        
        $scope.selections.push({title: 'registering_unit', value: $scope.selectedOrgUnit ? $scope.selectedOrgUnit.name : 'not_selected'});
        $scope.selections.push({title: 'program', value: $scope.selectedProgram ? $scope.selectedProgram.name : 'not_selected'});               
        
    });     
})

//Controller for the header section
.controller('HeaderController',
        function($scope,                
                DHIS2URL,
                TranslationService) {

    TranslationService.translate();
    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };
    
});