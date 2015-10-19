/**
 * Created by gaurav on 20-11-2014.
 */

$(document).ready(function () {


    var dhisBaseURL = new String();

    $.ajaxSetup({
        async: false
    });

    jQuery.getJSON("manifest.webapp", function (json) {
        console.log(json.toString());
        dhisBaseURL = json.activities.dhis.href + "/api";
        console.log(json.activities.dhis.href + "/api");
    });

    $.ajaxSetup({
        async: true
    });

    //dhisBaseURL='http://192.168.0.27:8962/cairo_ir/api/'; //For Custom URL

    console.log('DHISBaseURL: ' + dhisBaseURL);

    var reportList = new Array;


    $.ajax({
        url: dhisBaseURL + "/reports.jsonp",
        dataType: "jsonp",
        data: {
            format: "json"
        },
        success: function (response) {
            /** @namespace response.reports */
            for (var item in response.reports) {

                var report = new Object();

                report.name = response.reports[item].name;
                report.URL = response.reports[item].href;

                reportList[report.name] = report.URL;

                $('#reportSelect')
                    .append('<option id=\'' + response.reports[item].name + '\' value=\'' + response.reports[item].name + '\'>' + response.reports[item].name + '</option>');
            }
            $('#reportSelect').selectpicker({
                width: 'auto'
            });

        }
    });

    $.ajax({
        url: dhisBaseURL+"/organisationUnits.jsonp?paging=false",
        dataType: "jsonp",
        data: {
            format: "json"
        },
        success: function (response) {

            console.log(response);
            /** @namespace response.organisationUnits */
            for (var item in response.organisationUnits) {
                $('#orgUnitOUSelect')
                    .append('<option id=\'' + response.organisationUnits[item].id + '\' value=\'' + response.organisationUnits[item].id + '\'>' + response.organisationUnits[item].name + '</option>');
            }
            $('#orgUnitOUSelect').selectpicker({
                width: 'auto'
            });

            $('#ougFormOU').show();
        }

    });

    //noinspection JSUnresolvedFunction
    $('#datetimepickerStart').datetimepicker({
        pickTime: false,
        defaultDate: new Date()
    });

    //noinspection JSUnresolvedFunction
    $('#datetimepickerEnd').datetimepicker({
        pickTime: false,
        defaultDate: new Date()
    });


    $("#datetimepickerStart").on("dp.change", function (e) {
        $('#datetimepickerEnd').data("DateTimePicker").setMinDate(e.date);
    });
    $("#datetimepickerEnd").on("dp.change", function (e) {
        $('#datetimepickerStart').data("DateTimePicker").setMaxDate(e.date);
    });


    $("#btnHome").click(function (e) {
        var hrefURL = dhisBaseURL.substring(0, dhisBaseURL.length - 3);
        window.location.href = hrefURL + 'dhis-web-dashboard-integration/index.action';
    });


    $('#genButton').click(function () {

        var startDateCode = moment($('#startDate').val());
        var endDateCode = moment($('#endDate').val());

        //noinspection JSUnusedAssignment
        var periodList = new String();
        var startYearCode = startDateCode;
        var endYearCode = endDateCode;

        periodList = startDateCode.format('YYYY');
        while (startYearCode < endYearCode) {
            startYearCode = moment(startYearCode).add(1, 'y');
            periodList = periodList.concat(';' + startYearCode.format('YYYY'));
        }


        console.log('periodList: ' + periodList);
        console.log('dhisBaseURL : ' + dhisBaseURL);

        var orgUnitCode = $('#orgUnitOUSelect').val();


        sessionStorage.setItem('dhisBaseURL', dhisBaseURL);
        sessionStorage.setItem('periodList', periodList);
        sessionStorage.setItem('startDate', $('#startDate').val());
        sessionStorage.setItem('endDate', $('#endDate').val());
        sessionStorage.setItem('orgUnitCode', orgUnitCode);
        sessionStorage.setItem('orgUnitName', $('#orgUnitOUSelect option:selected').text());

        var reportName = $('#reportSelect option:selected').text() + '.html';

        reportName = encodeURI(reportName);

        console.log(reportName);

        window.location.href = reportName;

    });


});
