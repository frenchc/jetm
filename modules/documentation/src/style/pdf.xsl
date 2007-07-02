<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://docbook.org/ns/docbook"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:date="http://exslt.org/dates-and-times"
                exclude-result-prefixes="d date">

  <xsl:import href="fo/docbook.xsl"/>

  <xsl:param name="paper.type" select="'A4'"/>
  <xsl:param name="use.extensions">1</xsl:param>

  <!--<xsl:param name="fop.extensions">1</xsl:param>-->
  
  <xsl:param name="draft.mode">no</xsl:param>

  <!-- Custom title page -->
  <xsl:template name="book.titlepage.recto">
    <fo:block>
      <fo:table table-layout="fixed" width="175mm">
        <fo:table-column column-width="175mm"/>
        <fo:table-body>

          <fo:table-row>

            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="25pt" padding-top="70mm">
                <xsl:value-of select="d:info/d:title"/>
              </fo:block>
              <fo:block font-family="Helvetica" font-size="16pt" padding-bottom="10mm">
                <xsl:text>Version </xsl:text>
                <xsl:value-of select="d:info/d:releaseinfo"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <!--<fo:table-row>-->
            <!--<fo:table-cell text-align="center">-->
              <!--<fo:block font-family="Helvetica" font-size="12pt" padding-bottom="10mm">-->
                <!--<xsl:value-of select="date:date()"/>-->
              <!--</fo:block>-->
            <!--</fo:table-cell>-->
          <!--</fo:table-row>-->

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" padding-top="5mm" padding-bottom="140mm">
                  <xsl:text> </xsl:text>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="12pt" margin="0mm" padding-bottom="0mm">
                <xsl:text>Copyright &#169; </xsl:text>
                <xsl:value-of select="d:info/d:copyright/d:year" />
                <xsl:text> </xsl:text>
                <xsl:value-of select="d:info/d:copyright/d:holder" />
              </fo:block>
              <fo:block font-family="Helvetica" font-size="10pt" margin="0mm" padding="1mm">
                <xsl:value-of select="d:info/d:legalnotice"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>


  <xsl:template name="footer.content">
      <xsl:param name="pageclass" select="''" />
      <xsl:param name="sequence" select="''" />
      <xsl:param name="position" select="''" />
      <xsl:param name="gentext-key" select="''" />
      <xsl:variable name="Version">
        <xsl:if test="//d:releaseinfo">
          <xsl:text>JETM (</xsl:text><xsl:value-of select="//d:releaseinfo" /><xsl:text>)</xsl:text>
        </xsl:if>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$sequence='blank'">
          <xsl:if test="$position = 'center'">
            <xsl:value-of select="$Version" />
          </xsl:if>
        </xsl:when>
        <!-- for double sided printing, print page numbers on alternating sides (of the page) -->
        <xsl:when test="$double.sided != 0">
          <xsl:choose>
            <xsl:when test="$sequence = 'even' and $position='left'">
              <fo:page-number />
            </xsl:when>
            <xsl:when test="$sequence = 'odd' and $position='right'">
              <fo:page-number />
            </xsl:when>
            <xsl:when test="$position='center'">
              <xsl:value-of select="$Version" />
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <!-- for single sided printing, print all page numbers on the right (of the page) -->
        <xsl:when test="$double.sided = 0">
          <xsl:choose>
            <xsl:when test="$position='center'">
              <xsl:value-of select="$Version" />
            </xsl:when>
            <xsl:when test="$position='right'">
              <fo:page-number />
            </xsl:when>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:template>


  <xsl:param name="headers.on.blank.pages">0</xsl:param>
  <xsl:param name="footers.on.blank.pages">0</xsl:param>

  <!-- Space between paper border and content (chaotic stuff, don't touch) -->
  <xsl:param name="page.margin.top">5mm</xsl:param>
  <xsl:param name="region.before.extent">10mm</xsl:param>
  <xsl:param name="body.margin.top">10mm</xsl:param>

  <xsl:param name="body.margin.bottom">15mm</xsl:param>
  <xsl:param name="region.after.extent">10mm</xsl:param>
  <xsl:param name="page.margin.bottom">0mm</xsl:param>

  <xsl:param name="page.margin.outer">18mm</xsl:param>
  <xsl:param name="page.margin.inner">18mm</xsl:param>

  <!-- No intendation of Titles -->
  <xsl:param name="title.margin.left">0pc</xsl:param>

  <!-- Prevent blank pages -->
    <xsl:template name="book.titlepage.before.verso">
    </xsl:template>
    <xsl:template name="book.titlepage.verso">
    </xsl:template>
    <xsl:template name="book.titlepage.separator">
    </xsl:template>


</xsl:stylesheet>
