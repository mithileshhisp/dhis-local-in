<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:modelType">
    <div class="modelType">
      <h2> <xsl:value-of select="@name" /> </h2>
      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="@created" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>
        <tr>
          <td>Name</td>
          <td> <xsl:value-of select="d:name" /> </td>
        </tr>
        <tr>
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
      </table>
      <xsl:apply-templates select="d:models|d:modelTypeAttributes|d:dataElements|d:dataElementGroups|d:dataSets|d:organisationUnits" mode="short"/>     
    </div>
  </xsl:template>
  
  <xsl:template match="d:modelTypes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Model Types</h3>
      <table class="modelTypes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  </xsl:stylesheet>