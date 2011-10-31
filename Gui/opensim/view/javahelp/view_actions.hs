<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">
  <!-- title -->
  <title>Preferences Help</title>
  
  <!-- maps -->
  <maps>
     <homeID>org.opensim.view.actions.help</homeID>
     <mapref location="view_actions.jhm"/>
  </maps>
  
  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>view_actions_TOC.xml</data>
  </view>
  
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>view_actions_Index.xml</data>
  </view>
  
  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
  </view>
</helpset>