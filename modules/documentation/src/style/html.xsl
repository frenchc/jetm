<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:import href="html/docbook.xsl"/>

  <xsl:param name="html.stylesheet">default.css</xsl:param>

  <xsl:param name="use.extensions">1</xsl:param>
  <xsl:param name="graphicsize.extension">1</xsl:param>
  <xsl:param name="tablecolumns.extension">1</xsl:param>
  <xsl:param name="callout.extensions">1</xsl:param>

  <xsl:param name="toc.section.depth">3</xsl:param>

  <xsl:param name="chapter.autolabel">1</xsl:param>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>

  <xsl:param name="draft.mode">yes</xsl:param>

</xsl:stylesheet>
