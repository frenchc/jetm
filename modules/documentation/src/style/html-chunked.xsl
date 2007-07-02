<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:import href="html/chunk.xsl"/>

  <xsl:param name="chunk.section.depth">'5'</xsl:param>
  <xsl:param name="use.id.as.filename">'1'</xsl:param>
  <xsl:param name="html.stylesheet">default.css</xsl:param>

  <xsl:param name="use.extensions">1</xsl:param>
  <xsl:param name="graphicsize.extension">1</xsl:param>
  <xsl:param name="tablecolumns.extension">1</xsl:param>
  <xsl:param name="callout.extensions">1</xsl:param>

  <xsl:param name="toc.section.depth">4</xsl:param>

  <xsl:param name="draft.mode">no</xsl:param>


  <xsl:param name="suppress.header.navigation">1</xsl:param>


  <!-- disable body attributes -->
  <xsl:template name="body.attributes">
  </xsl:template>

  <xsl:param name="navig.showtitles">1</xsl:param>
  
  <xsl:param name="chunk.tocs.and.lots.has.title" select="1"></xsl:param>

</xsl:stylesheet>
