$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdateProgramIndicator( context ) {
  location.href = 'showUpdateProgramIndicator.action?id=' + context.id;
}

function removeIndicator( context ) {
  removeItem( context.id, context.name, i18n_confirm_delete , 'removeProgramIndicator.action' );
}

function showProgramIndicatorDetails( context ) {
  jQuery.getJSON('getProgramIndicator.action', { id: context.id }, function( json ) {
    setInnerHTML('nameField', json.programIndicator.name);
    setInnerHTML('codeField', json.programIndicator.code);
    setInnerHTML('descriptionField', json.programIndicator.description);
    setInnerHTML('valueTypeField', json.programIndicator.valueType);
    setInnerHTML('rootDateField', json.programIndicator.rootDate);
    setInnerHTML('expressionField', json.programIndicator.expression);

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Remove Program Indicator
// -----------------------------------------------------------------------------

function removeProgramIndicator( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeProgramIndicator.action');
}

function getTrackedEntityDataElements() {
  clearListById('dataElements');
  clearListById('deSumId');
  var programStageId = getFieldValue('programStageId');

  jQuery.getJSON('getTrackedEntityDataElements.action',
    {
      programId: getFieldValue('programId'),
      programStageId: programStageId
    }
    , function( json ) {
      var dataElements = jQuery('#dataElements');
      for( i in json.dataElements ) {
        if( json.dataElements[i].type == 'int' || json.dataElements[i].type == 'date' ) {
          dataElements.append("<option value='" + json.dataElements[i].id + "' title='" + json.dataElements[i].name + "' suggested='" + json.dataElements[i].optionset + "'>" + json.dataElements[i].name + "</option>");
        }
      }
    });
}

function insertDataElement( element ) {
  var programStageId = getFieldValue('programStageId');
  var dataElementId = element.options[element.selectedIndex].value;

  insertTextCommon('expression', "[DE:" + programStageId + "." + dataElementId + "]");
  getConditionDescription();
}

function insertInfo( element, isProgramStageProperty ) {
  var id = "";
  if( isProgramStageProperty ) {
    id = getFieldValue('programStageId');
  }
  else {
    id = getFieldValue('programId');
  }

  value = element.options[element.selectedIndex].value.replace('*', id);
  insertTextCommon('expression', value);
  getConditionDescription();
}

function insertOperator( value ) {
  insertTextCommon('expression', ' ' + value + ' ');
  getConditionDescription();
}

function getConditionDescription() {
  $.postJSON('getProgramIndicatorDescripttion.action',
    {
      expression: getFieldValue('expression')
    }, function( json ) {
      byId('aggregationDescription').innerHTML = json.message;
    })
}

function programIndicatorOnChange() {
  var valueType = getFieldValue('valueType');
  if( valueType == 'int' ) {
    hideById('rootDateTR');
    disable('rootDate');
  }
  else {
    showById('rootDateTR');
    enable('rootDate');
  }
}
