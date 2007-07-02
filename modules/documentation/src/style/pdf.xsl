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

  <xsl:param name="draft.mode">no</xsl:param>

  <!-- Custom title page -->
  <xsl:template name="book.titlepage.recto">
    <fo:block>
      <fo:table table-layout="fixed" width="175mm">
        <fo:table-column column-width="175mm"/>
        <fo:table-body>

          <fo:table-row>

            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="25pt" padding-top="50mm">
                <xsl:value-of select="d:info/d:title"/>
              </fo:block>
              <fo:block font-family="Helvetica" font-size="16pt" padding-bottom="10mm">
                <xsl:text>Version </xsl:text>
                <xsl:value-of select="d:info/d:releaseinfo"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="12pt" padding-bottom="10mm">
                <xsl:value-of select="date:date()"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="12pt" padding-bottom="5mm">
                <xsl:for-each select="d:info/d:authorgroup/d:author">
                  <xsl:if test="position() > 1">
                    <xsl:text>,</xsl:text>
                  </xsl:if>
                  <xsl:value-of select="d:personname/d:firstname"/>
                  <xsl:text> </xsl:text>
                  <xsl:value-of select="d:personname/d:surname"/>
                </xsl:for-each>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="12pt" padding-top="5mm" padding-bottom="100mm">
                  <xsl:text> </xsl:text>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="12pt" padding="5mm">
                <xsl:text>Copyright &#169; </xsl:text>
                <xsl:value-of select="d:info/d:copyright/d:year" />
                
              </fo:block>
              <fo:block font-family="Helvetica" font-size="10pt" padding="1mm">
                <xsl:value-of select="d:info/d:legalnotice"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>


  <!-- Prevent blank pages -->
  <xsl:template name="book.titlepage.separator">
  </xsl:template>


</xsl:stylesheet>
