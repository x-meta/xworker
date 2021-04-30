<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
	
<@showXml dataObject=thing ident=""/>

<#macro showXml dataObject ident>
${ident}<${dataObject.thingName}<@showAttributes dataObject=dataObject/><#if (dataObject.childs?exists && 0 < dataObject.childs?size) || dataObject._value?exists || dataObject._text?exists || dataObject._cdata?exists>>${dataObject._value?if_exists?xml}${dataObject._text?if_exists?xml}<#if dataObject._cdata?exists><![CDATA[${dataObject._cdata}]]></#if><#if dataObject.childs?exists && 0 < dataObject.childs?size>
<#list dataObject.childs as child>
<@showXml dataObject=child ident="    ${ident}"/>
</#list></#if><#rt>
<#if dataObject.childs?exists && 0 < dataObject.childs?size>${ident}</#if></${dataObject.thingName}><#else>/></#if>
</#macro>        

<#macro showAttributes dataObject>
<#assign attrs = dataObject.attributes>
<#assign keys = attrs?keys>
<#assign node = dataObject.descriptors[0]>
<#list keys as key>
<#if key != "x_enable">
<#assign value = (attrs[key])?default("")>
<#if value?exists && value?string != "">
<#assign ok = "true">
<#assign have = "false">
<#if node["/attribute@"]?exists> 
<#list node["/attribute@"] as child>
  <#if child.thingName == "attribute" && child.name == key && child.name != "_value" && child.name != "_text" && child.name != "_cdata">
    <#assign have = "true">
    <#if child.default?exists && child.default == value>
      <#assign ok = "false">
    </#if>
<#--    
    <#if (dataObject.thingName == "class") && child.name == "name">
      <#assign ok = "false">
    </#if>
    <#if (dataObject.thingName == "one-to-many" || dataObject.thingName == "many-to-one") && child.name == "class">
      <#assign ok = "false">
    </#if>
-->    
    <#break/>
  </#if>
</#list>
</#if>
<#if ok == "true" && have == "true">
 ${key}="${attrs[key]?xml}"<#rt>
</#if>
</#if>
</#if>
</#list> 
</#macro>