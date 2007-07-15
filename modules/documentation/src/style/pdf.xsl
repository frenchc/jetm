<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://docbook.org/ns/docbook"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="d">

  <xsl:import href="fo/docbook.xsl"/>

  <xsl:param name="paper.type" select="'A4'"/>
  <xsl:param name="use.extensions">1</xsl:param>

  <xsl:param name="chapter.autolabel">1</xsl:param>
  <xsl:param name="preface.autolabel">0</xsl:param>

  <xsl:param name="section.autolabel" select="1"/>
  <!--<xsl:param name="fop.extensions">1</xsl:param>-->

  <xsl:param name="draft.mode">no</xsl:param>

  <xsl:param name="headers.on.blank.pages">0</xsl:param>
  <xsl:param name="footers.on.blank.pages">0</xsl:param>

  <xsl:param name="region.before.extent">10mm</xsl:param>
  <xsl:param name="body.margin.top">10mm</xsl:param>

  <xsl:param name="body.margin.bottom">15mm</xsl:param>
  <xsl:param name="region.after.extent">10mm</xsl:param>
  <!--<xsl:param name="page.margin.bottom">10mm</xsl:param>-->
  <!--<xsl:param name="page.margin.top">5mm</xsl:param>-->

  <xsl:param name="page.margin.outer">18mm</xsl:param>
  <xsl:param name="page.margin.inner">18mm</xsl:param>

  <!-- No intendation of Titles -->
  <xsl:param name="title.margin.left">0pc</xsl:param>

  <!-- Prevent blank pages -->
  <xsl:template name="book.titlepage.before.verso">
  </xsl:template>
  <xsl:template name="book.titlepage.separator">
  </xsl:template>


  <!-- Sections 1, 2 and 3 titles have a small bump factor and padding -->
  <!--<xsl:attribute-set name="section.title.level1.properties">-->
    <!--<xsl:attribute name="space-before.optimum">0.8em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.maximum">0.8em</xsl:attribute>-->
    <!--<xsl:attribute name="font-size">-->
      <!--<xsl:value-of select="$body.font.master * 1.5"/>-->
      <!--<xsl:text>pt</xsl:text>-->
    <!--</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.optimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.minimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.maximum">0.1em</xsl:attribute>-->
  <!--</xsl:attribute-set>-->
  <!--<xsl:attribute-set name="section.title.level2.properties">-->
    <!--<xsl:attribute name="space-before.optimum">0.6em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.minimum">0.6em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.maximum">0.6em</xsl:attribute>-->
    <!--<xsl:attribute name="font-size">-->
      <!--<xsl:value-of select="$body.font.master * 1.25"/>-->
      <!--<xsl:text>pt</xsl:text>-->
    <!--</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.optimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.minimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.maximum">0.1em</xsl:attribute>-->
  <!--</xsl:attribute-set>-->
  <!--<xsl:attribute-set name="section.title.level3.properties">-->
    <!--<xsl:attribute name="space-before.optimum">0.4em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.minimum">0.4em</xsl:attribute>-->
    <!--<xsl:attribute name="space-before.maximum">0.4em</xsl:attribute>-->
    <!--<xsl:attribute name="font-size">-->
      <!--<xsl:value-of select="$body.font.master * 1.0"/>-->
      <!--<xsl:text>pt</xsl:text>-->
    <!--</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.optimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.minimum">0.1em</xsl:attribute>-->
    <!--<xsl:attribute name="space-after.maximum">0.1em</xsl:attribute>-->
  <!--</xsl:attribute-set>-->

  <!--<xsl:attribute-set name="chapter.titlepage.recto.style">-->
    <!--<xsl:attribute name="text-align">left</xsl:attribute>-->
    <!--<xsl:attribute name="font-weight">bold</xsl:attribute>-->
    <!--<xsl:attribute name="font-size">-->
      <!--<xsl:value-of select="$body.font.master * 1.8"/>-->
      <!--<xsl:text>pt</xsl:text>-->
    <!--</xsl:attribute>-->
  <!--</xsl:attribute-set>-->

  <!-- Default Font size -->
  <xsl:param name="body.font.master">11</xsl:param>
  <xsl:param name="body.font.small">8</xsl:param>

  <!-- Line height in body text -->
  <!--<xsl:param name="line-height">1.4</xsl:param>-->

  <!-- Monospaced fonts are smaller than regular text -->
  <xsl:attribute-set name="monospace.properties">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$monospace.font.family"/>
    </xsl:attribute>
    <xsl:attribute name="font-size">0.8em</xsl:attribute>
  </xsl:attribute-set>

  <!-- The table width should be adapted to the paper size -->
  <xsl:param name="default.table.width">17.4cm</xsl:param>

  <!-- Some padding inside tables -->
  <xsl:attribute-set name="table.cell.padding">
    <xsl:attribute name="padding-left">4pt</xsl:attribute>
    <xsl:attribute name="padding-right">4pt</xsl:attribute>
    <xsl:attribute name="padding-top">4pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">4pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- Only hairlines as frame and cell borders in tables -->
  <xsl:param name="table.frame.border.thickness">0.1pt</xsl:param>
  <xsl:param name="table.cell.border.thickness">0.1pt</xsl:param>

  <xsl:param name="body.start.indent">0pt</xsl:param>


  <!-- Custom title page -->
  <xsl:template name="book.titlepage.recto">
    <fo:block>
      <fo:table table-layout="fixed" width="175mm">
        <fo:table-column column-width="175mm"/>
        <fo:table-body>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="22pt" font-weight="bold" padding-top="70mm">
                <xsl:value-of select="d:info/d:title"/>
              </fo:block>
              <fo:block font-family="Helvetica" font-size="14pt" font-weight="bold" padding-bottom="10mm">
                <xsl:text>Version</xsl:text>
                <xsl:value-of select="d:info/d:releaseinfo"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" padding-bottom="145mm">
                <xsl:text> </xsl:text>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>


          <fo:table-row>
            <fo:table-cell text-align="center">
              <fo:block font-family="Helvetica" font-size="10pt" margin="0mm" padding-bottom="0mm">
                <xsl:text>Copyright &#169; </xsl:text>
                <xsl:for-each select="d:info/d:copyright/d:year">
                  <xsl:if test="position() > 1">
                    <xsl:text>, </xsl:text>
                  </xsl:if>
                  <xsl:value-of select="current()" />
                </xsl:for-each>
                <xsl:text> </xsl:text>
                <xsl:value-of select="d:info/d:copyright/d:holder"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>

        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
  