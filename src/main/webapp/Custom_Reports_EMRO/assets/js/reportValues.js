/**
 * Created by Janaka on 2/3/2015.
 */

var reportNotNull=false;

var getMappedIndicatorValue = function (analyticsURL, indicatorID, cellID) {

    var value = new Number(0);

    analyticsURL = analyticsURL + "&dimension=dx:" + indicatorID;

    console.log(analyticsURL);

    $.ajax({
        url: analyticsURL,
        dataType: "json",
        async:false,
        data: {
            format: "json"
        },
        success: function (response) {
            console.log(response);
            for (var items in response.rows) {
                reportNotNull=true;
                console.log(parseInt(response.rows[items][2]));
                value = value + parseInt(response.rows[items][2]);
            }
            console.log('VALUE: ' + value);

            $(cellID).append(value);
        }

    });
}

var getMappedDEValue = function (analyticsURL, dataelementID, categoryFilters, cellID) {
    var value = new Number(0);

    analyticsURL = analyticsURL + "&dimension=dx:" + dataelementID;

    if (categoryFilters != null) {
        analyticsURL = analyticsURL + categoryFilters;
    }

    analyticsURL = analyticsURL;

    console.log(analyticsURL);

    $.ajax({
        url: analyticsURL,
        dataType: "json",
        async:false,
        data: {
            format: "json"
        },
        success: function (response) {
            console.log(response);
            for (var items in response.rows) {
                reportNotNull=true;
                console.log(parseInt(response.rows[items][1]));
                value = value + parseInt(response.rows[items][1]);
            }

            console.log('VALUE: ' + value);

            $(cellID).append(value);
        }

    });
}



var getMappedDEValueReturn = function( analyticsURL, filters)
{
    var value = new Number(0);

    if(filters != null)
    {
        analyticsURL = analyticsURL + filters;
    }

    analyticsURL = analyticsURL;

    console.log(analyticsURL);

    $.ajax({
        url: analyticsURL,
        dataType: "json",
        async:false,
        data: {
            format: "json"
        },
        success: function (response) {
            console.log(response);
            for(var items in response.rows){
                reportNotNull=true;
                value +=parseInt(response.rows[items][2]);
            }
            console.log(analyticsURL);
            console.log('VALUE: '+value);
        }

    });
    return value;
}

var getMappedFormulaValue = function (analyticsURL, formula, cellID) {

    var re = /\+|\-|\*|\//;
    var DXFilterArray = formula.split(re);
    console.log("O: " +  DXFilterArray);
    for(var item in  DXFilterArray)
    {
        var formulaItem =  DXFilterArray[item];
        if(isNaN(formulaItem))
        {
            formulaItem = formulaItem.replace('[','');
            formulaItem = formulaItem.replace(']','');
            formulaItem = formulaItem.replace('(','');
            formulaItem = formulaItem.replace(')','');
            var categoryFilters = formulaItem;

            formula = formula.replace(formulaItem,getMappedDEValueReturn(analyticsURL,categoryFilters));
            console.log("FF: "+formula);
        }
        formula = formula.replace('[','(');
        formula = formula.replace(']',')');
    }
    var finalVal=eval(formula);
    $(cellID).append(finalVal);
}

var getMappedFormulaValueReturn = function (analyticsURL, formula) {

    var re = /\+|\-|\*|\//;
    var DXFilterArray = formula.split(re);
    console.log("O: " +  DXFilterArray);
    for(var item in  DXFilterArray)
    {
        var formulaItem =  DXFilterArray[item];
        if(isNaN(formulaItem))
        {
            formulaItem = formulaItem.replace('[','');
            formulaItem = formulaItem.replace(']','');
            formulaItem = formulaItem.replace('(','');
            formulaItem = formulaItem.replace(')','');
            var categoryFilters = formulaItem;

            formula = formula.replace(formulaItem,getMappedDEValueReturn(analyticsURL,categoryFilters));
            console.log("FF: "+formula);
        }
        formula = formula.replace('[','(');
        formula = formula.replace(']',')');
    }
    var finalVal=eval(formula);
    return finalVal;
}

var getMappedFormulaValuePercentage = function (analyticsURL, formula, cellID) {

    var re = /\+|\-|\*|\//;
    var DXFilterArray = formula.split(re);
    console.log("O: " +  DXFilterArray);
    for(var item in  DXFilterArray)
    {
        var formulaItem =  DXFilterArray[item];
        if(isNaN(formulaItem))
        {
            formulaItem = formulaItem.replace('[','');
            formulaItem = formulaItem.replace(']','');
            formulaItem = formulaItem.replace('(','');
            formulaItem = formulaItem.replace(')','');
            var categoryFilters = formulaItem;

            console.log("FF: "+formula);

            formula = formula.replace(formulaItem,getMappedDEValueReturn(analyticsURL,categoryFilters));
            console.log("FF: "+formula);
        }
        formula = formula.replace('[','(');
        formula = formula.replace(']',')');
    }
    var finalVal=((eval(formula))*100).toFixed(2);
    $(cellID).append(finalVal!='NaN'?finalVal:0);
}


var fillFormulaValuesArray = function (analyticsURL, formula) {

    var re = /\+|\-|\*|\//;
    var DXFilterArray = formula.split(re);
    console.log("O: " +  DXFilterArray);
    for(var item in  DXFilterArray)
    {
        var formulaItem =  DXFilterArray[item];
        if(isNaN(formulaItem))
        {
            formulaItem = formulaItem.replace('[','');
            formulaItem = formulaItem.replace(']','');
            formulaItem = formulaItem.replace('(','');
            formulaItem = formulaItem.replace(')','');
            var categoryFilters = formulaItem;

            formula = formula.replace(formulaItem,getMappedDEValueReturn(analyticsURL,categoryFilters));
            console.log("FF: "+formula);
        }
        formula = formula.replace('[','(');
        formula = formula.replace(']',')');
    }
    var finalVal=eval(formula);
    return finalVal;
}

var countNumberOfDataPoint=function(analyticsURL, filters){
    var counter=0;
    if(filters != null)
    {
        analyticsURL = analyticsURL + filters;
    }

    analyticsURL = analyticsURL;

    console.log(analyticsURL);

    $.ajax({
        url: analyticsURL,
        dataType: "json",
        async:false,
        data: {
            format: "json"
        },
        success: function (response) {
            console.log(response);
            for(var items in response.rows){
                reportNotNull=true;
                counter++;
            }
            console.log(analyticsURL);
            console.log('counter: '+counter);
        }

    });
    return counter;
}

var getMovingAverageValue=function(analyticsURL, filters){
    var movingAvg=parseFloat(getMappedDEValueReturn(analyticsURL, filters)/countNumberOfDataPoint(analyticsURL, filters)).toFixed(2);
    return (movingAvg!='NaN'?movingAvg:0);
}

function loadXMLDoc(filename)
{
    if (window.XMLHttpRequest)
    {
        xhttp=new XMLHttpRequest();
    }
    else // code for IE5 and IE6
    {
        xhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.open("GET",filename,false);
    xhttp.send();
    return xhttp.responseXML;
}