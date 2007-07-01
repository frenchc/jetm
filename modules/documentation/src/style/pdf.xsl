<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:import href="fo/docbook.xsl"/>

  <xsl:param name="paper.type" select="'A4'"/>
  <xsl:param name="use.extensions">1</xsl:param>

  <!-- Custom title page -->
  <xsl:template name="book.titlepage.recto">
    <fo:block>
      <fo:table table-layout="fixed" width="200mm">
        <fo:table-column column-width="200mm"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block>
                <fo:external-graphic src="file:images/jetm_logo.png"/>
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
