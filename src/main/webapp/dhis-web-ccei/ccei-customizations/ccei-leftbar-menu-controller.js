//Controller for column show/hide
trackerCapture.controller('LeftBarMenuController',
    function ($rootScope,$scope, $location, ProgramFactory, CCEIOrgUnitFactory, $timeout,SessionStorageService) {

        $rootScope.showModalFlag = false;

        $scope.showHome = function () {
            $rootScope.showModalFlag = false;
            SessionStorageService.set('SHOW_MODEL_FLAG',false);

            $location.path('/').search();
            $timeout(function () {

                $scope.$parent.previousOu = SessionStorageService.get('PREV_OU',$scope.$parent.selectedOrgUnit);

                if ($scope.$parent.previousOu)
                    $scope.$parent.selectedOrgUnit = $scope.$parent.previousOu;
                else $scope.$parent.selectedOrgUnit = $scope.$parent.selectedOrgUnit;

                selection.enable();

                selection.setSelected($scope.$parent.selectedOrgUnit.id);
                subtree.reloadTree();

            });


        };

        $scope.showModal = function () {
            $rootScope.showModalFlag = true;
            SessionStorageService.set('SHOW_MODEL_FLAG',true);

            CCEIOrgUnitFactory.getRootOu().then(function (rootOu) {
                $location.path('/').search();
                $timeout(function(){
                    SessionStorageService.set('PREV_OU',$scope.$parent.selectedOrgUnit);
                    $scope.$parent.selectedOrgUnit = rootOu;
                    selection.setSelected(rootOu.id);
                    selection.disable();
                    subtree.reloadTree();
                })
            });

        };

        $scope.showReportTypes = function () {
            $location.path('/report-types').search();
        };

        $scope.showConfiguration = function () {
            $location.path('/ccei-configuration');

        };

    });
