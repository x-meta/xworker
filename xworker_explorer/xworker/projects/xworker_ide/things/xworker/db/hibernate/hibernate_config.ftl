<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD//EN"
    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>

    <#list dataObject.getChilds() as session>
    <#if session.thingName == "session_factory">
    <session-factory<#if session.jndiName?exists && session.jndiName != ""> name="${session.jndiName?if_exists}"</#if>>

        <!-- properties -->
    <@showAttributes dataObject=dataObject path=""/>

        <!-- mapping files -->
    <#list session.childs as child>
        <#if child.thingName == "mapping">
        <mapping resource="${child.resource?if_exists}"/>
        </#if>        
        </#list>
        <#list mappings as mapping>
        <#--<mapping resource="${mapping}"/>-->
        <mapping file="${world.path}/work/hibernate/${mapping}"/>
        </#list>

        <!-- cache settings -->
        <#list session.childs as child>
        <#if child.thingName == "class_cache">
        <class-cache class="${child.class?if_exists}" usage="${child.usage?if_exists}"/>
        </#if>
        <#if child.thingName == "collection_cache">
        <collection-cache collection="${child.collection?if_exists}" usage="${child.usage?if_exists}"/>
        </#if>
        <#if child.thingName == "class-cache">
        ${child.xmlString?if_exists}
        </#if>
        <#if child.thingName == "collection-cache">
        ${child.xmlString?if_exists}
        </#if>
        <#if child.thingName == "event">
        ${child.xmlString?if_exists}
        </#if>
        </#list>
    </session-factory>
    </#if>
    </#list>
</hibernate-configuration>

  
<#macro showAttributes dataObject, path>
<#if dataObject.thingName != "mappings">
<#assign attrs = dataObject.attributes>
<#assign keys = attrs?keys>
<#assign node = dataObject.descriptors[0]>
<#list keys as key>
<#assign value = (attrs[key])?default("")>
<#if !key?starts_with("_xworker_") && value?exists && value?string != "">
<#assign ok = "true">
<#assign have = "false">
<#if node["/attribute@"]?exists> 
<#list node["/attribute@"] as child>
  <#if child.thingName == "attribute" && child.name == key && child.name != "_value" && child.name != "_text" && child.name != "_cdata">
    <#assign have = "true">
    <#if child.default?exists && child.default == value>
      <#assign ok = "false">
    </#if>
    <#break/>
  </#if>
</#list>
</#if>
<#if ok == "true" && have == "true" && key != "things">
        <property name="<#if path != "">${path}.</#if>${key}">${attrs[key]?xml}</property>
</#if>
</#if>
</#list> 
<#list dataObject.childs as child>
<#if child.thingName != "mapping" && child.thingName != "class_cache" && child.thingName != "collection_cache" &&  child.thingName != "class-cache" && child.thingName != "collection-cache" && child.thingName != "event">
<#if path != "">
<#assign p = path + "." + child.thingName>
<#else>
<#assign p = "hibernate">
</#if>
<@showAttributes dataObject=child path="${p?if_exists}"/>
</#if>
</#list>
</#if>
</#macro>