<@showXml thing=thing ident=""/>

<#macro showXml thing ident>
${ident}<${thing.thingName}<@showAttributes thing=thing/><#if (thing.childs?exists && 0 < thing.childs?size) || thing._value?exists || thing._text?exists || thing._cdata?exists>>${thing._value?if_exists?xml}${thing._text?if_exists?xml}<#if thing._cdata?exists><![CDATA[${thing._cdata}]]></#if><#if thing.childs?exists && 0 < thing.childs?size>
<#list thing.childs as child>
<@showXml thing=child ident="    ${ident}"/>
</#list></#if><#rt>
<#if thing.childs?exists && 0 < thing.childs?size>${ident}</#if></${thing.thingName}><#else>/></#if>
</#macro>        

<#macro showAttributes thing>
<#assign attrs = thing.attributes>
<#assign keys = attrs?keys>
<#assign node = thing.descriptors[0]>
<#list keys as key>
<#assign value = (attrs[key])?default("")>
<#if value?exists && value?string != "">
<#assign ok = "true">
<#assign have = "false">
<#if node["/attribute@"]?exists> 
<#list node["/attribute@"] as child>
  <#if child.thingName == "attribute" && child.name == key && child.name != "_value" && child.name != "_text" && child.name != "_cdata">
    <#assign have = "true">
    <#assign defaultValue = "${(child.default?string)?if_exists}">
    <#if defaultValue != "" && defaultValue == ("" + value?string)>
      <#assign ok = "false">
    </#if>
    <#break/>
  </#if>
</#list>
</#if>
<#if ok == "true" && have == "true">
 ${key}="${(attrs[key]?string)?xml}"<#rt>
</#if>
</#if>
</#list> 
</#macro>
