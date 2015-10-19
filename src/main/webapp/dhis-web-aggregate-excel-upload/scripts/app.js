/* global angular */

'use strict';

/* App Module */

var excelUpload = angular.module('excelUpload',
                    ['ui.bootstrap',
                    'ngRoute',
                    'ngCookies',
                    'ngMessages',
                    'ngSanitize',
                    'excelUploadDirectives',
                    'excelUploadControllers',
                    'excelUploadServices',
                    'excelUploadFilters',
                    'd2Filters',
                    'd2Directives',
                    'd2Services',
                    'd2Controllers',
                    'ui.select',
                    'angularGrid',
                    'pascalprecht.translate',
                    'd2HeaderBar'])

.value('DHIS2URL', '..')

.config(function ($routeProvider, $translateProvider) {

    $routeProvider.when('/', {
        templateUrl: 'components/excel-upload/excel-upload.html',
        controller: 'MainController'
    }).when('/excel-mapping',{
        templateUrl:'components/excel-mapping/excel-mapping.html',
        controller: 'ExcelMappingController'
    }).otherwise({
        redirectTo: '/'
    });

    $translateProvider.preferredLanguage('en');
    $translateProvider.useSanitizeValueStrategy('escaped');
    $translateProvider.useLoader('i18nLoader');
});
