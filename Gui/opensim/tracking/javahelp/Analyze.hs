<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">
  <!-- title -->
  <title>Analyze Help</title>
  
  <!-- maps -->
  <maps>
     <homeID>org.opensim.simtrack.analyze.help</homeID>
     <mapref location="Analyze.jhm"/>
  </maps>
  
  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>Analyze_TOC.xml</data>
  </view>
  
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>Analyze_Index.xml</data>
  </view>
  
  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
  </view>
</helpset>