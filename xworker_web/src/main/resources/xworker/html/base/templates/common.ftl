<#--
  显示一个数据对象的属性，按照指定的名称。
-->
<#macro showProperties names dataObject showStyle=true>
<#assign have = false>
<#list names as name>
  <#if dataObject.get(name)?exists && dataObject.get(name) != "">
    <#assign have = true>
<#if have> </#if><#if name=="class"><@showClass dataObject=dataObject/><#else>${name}="${dataObject.attributes[name]}"</#if><#rt>
  </#if>
</#list>
</#macro>

<#--
  显示对象的一个属性。
-->
<#macro showProperty dataName showName dataObject>
<#if dataObject[dataName]?exists && dataObject[dataName] != "">
 ${showName}="${dataObject[dataName]}"<#rt>
</#if><#rt>
</#macro>

<#--
   Freemarker显示属性class的问题，显示的java Class了????????
-->
<#macro showClass dataObject default="">
<#if dataObject.get("class")?exists && dataObject.get("class") != "">class="${dataObject.get("class")}"<#elseif default != "">class="${default}"</#if><#rt>
</#macro>

<#-- 
  基本控件。
-->
<#macro showEvent object>
<#if object["/event@"]?exists>
<#list object["/event@"] as event>
 ${event.name?if_exists}="${event.value?if_exists}"<#rt>
</#list>
</#if>
</#macro>

<#macro showTable object>
<table border="1" class="sort-table" name="${object.name?default("table_1")}" <#if object.width?exists>width="${object.width}"</#if> cellspacing='0'>
<#-- Draw Table Header -->
  <thead>
    <tr color="gray">  
<#list object["/column@"] as column>
      <td width="${column.width?if_exists}" nowrap>${column.label?if_exists}</td>
</#list>
    </tr>
  </thead>
  <tbody>
  <#if object.value?exists>
  <${r"#"}list ${object.value} as row>
   <${r"#"}if row_index % 2 == 0>
    <tr class="sorttable_odd">
    <${r"#"}else>
    <tr class="sorttable_even">
    </${r"#"}if>
    <#list object["/column@"] as column>
    <td class="srottable_td" nowrap><div class="tabletext"><#if column.childs?exists && 0 < column.childs?size><#list column.childs as child><@showObject object=child/></#list><#elseif column.value?exists && column.value != "">${column.value?if_exists}</#if>&nbsp;</div></td>
    </#list>
    </tr>
  </${r"#"}list>
  </#if>
  </tbody>
</table>
</#macro>

<#macro showGroup group>
<#assign cols = group.cols?number/>
<#if group.layout = "formLayout">
<#if group.isFieldSet?exists && group.isFieldSet = "true">
<fieldSet><lenged>${group.title?if_exists}</lenged>
</#if>
<table <@lib.showProperties names=["height", "width", "cellpadding", "cellspacing", "border"] dataObject=group/>>
<#if group.haveTitle?exists && group.haveTitle = "true">
  <tr>
	<th align="left" class="groupLable">${group.title?if_exists}</th>
  </tr>
</#if>
<#if group._rows?exists>
<#list group._rows as row>
  <tr>
  <#list row as col>
  <#if group.haveLabel?exists && group.haveLabel = "true" && (!col.showTitle?exists || col.showTitle == "true")>
    <td class="dataLabel" width="15%" nowrap>${col.label}</td>
    <td class="dataField" width="35%" <@lib.showProperties names=["height", "valign", "align", "rowspan"] dataObject=col showStyle=false/> colspan="${col.colspan?number * 2 - 1}" nowrap><@showObject object=col/></td>
  <#else>
    <td class="dataField" <@lib.showProperties names=["width", "height", "valign", "align", "rowspan"] dataObject=col showStyle=false/> colspan="${col.colspan?number * 2}" nowrap><@showObject object=col/></td>
  </#if>
  </#list>
  </tr>
</#list>
</#if>
</table>
<#if group.isFieldSet?exists && group.isFieldSet = "true">
</fieldSet>
</#if>
<#elseif group.layout = "flowLayout">
<#list group.childs as child>
<@showObject object=child/>
</#list>
</#if>
</#macro>

<#macro showMenuTree>
<!--
<${r"#"}macro showMenu menus>
    <${r"#"}list menus as m>
    <${r"#"}if m.isSplit == "true">
    _cmSplit,
    <${r"#"}else>
    [${r"$"}{m.action}, ${r"$"}{m.label}, ${r"$"}{m.url}, ${r"$"}{m.target}, ${r"$"}{m.description}<${r"#"}if m.childs?exists && 0 < m.childs?size>,<${r"@"}showMenu menus=m.childs/></${r"#"}if>]<${r"#"}if m_has_next>,</${r"#"}if>
    </${r"#"}if>
    </${r"#"}list>
</${r"#"}macro>
<${r"#"}macro showTree trees node checkAble="false">
<${r"#"}if checkAble == "false">
    <${r"#"}list trees as menu>
    var ${r"$"}{node}_${r"$"}{menu_index}  = new WebFXTreeItem('${r"$"}{menu.label?if_exists}'<${r"#"}if menu.url?exists && menu.url != "">, '${r"$"}{menu.url?if_exists}'</${r"#"}if>);
    <${r"#"}if menu.target?exists && menu.target != "">
    ${r"$"}{node}_${r"$"}{menu_index}.target = '${r"$"}{menu.target}';
    </${r"#"}if>
    <${r"#"}if menu.id?exists>
    ${r"$"}{node}_${r"$"}{menu_index}.nodeId = '${r"$"}{menu.id}';
    </${r"#"}if>
    ${r"$"}{node}.add(${r"$"}{node}_${r"$"}{menu_index});
    <${r"#"}if menu.childs?exists>
    <${r"@"}showTree trees=menu.childs node="${r"$"}{node}_${r"$"}{menu_index}"/>
    </${r"#"}if>
    </${r"#"}list>
<${r"#"}else>
    <${r"#"}list trees as menu>
    var ${r"$"}{node}_${r"$"}{menu_index}  = new CheckableTreeItem('${r"$"}{menu.name?if_exists}', '${r"$"}{menu.label?if_exists}', '${r"$"}{menu.url?if_exists}', true, ${r"$"}{menu.checked?default("false")}, 'checkParentNodes(${r"$"}{node}_${r"$"}{menu_index}, true, false);checkChildNodes(${r"$"}{node}_${r"$"}{menu_index}, true, true)');
    <${r"#"}if menu.target?exists && menu.target != "">
    ${r"$"}{node}_${r"$"}{menu_index}.target = '${r"$"}{menu.target}';
    </${r"#"}if>
    ${r"$"}{node}.add(${r"$"}{node}_${r"$"}{menu_index});
    <${r"#"}if menu.childs?exists>
    <${r"@"}showTree trees=menu.childs node="${r"$"}{node}_${r"$"}{menu_index}" checkAble=checkAble/>
    </${r"#"}if>
    </${r"#"}list>
</${r"#"}if>    
</${r"#"}macro>
-->
</#macro>

<#macro showValue name value>
<#if value?exists && value != "">${name}="${value}"</#if><#t>
</#macro>

<#macro showMenu menus>
    <#list menus as m>
    <#if m.isSplit == "true">
    _cmSplit,
    <#else>
    [${m.action?if_exists}, ${m.label?if_exists}, ${m.url?if_exists}, ${m.target?if_exists}, ${m.description?if_exists}<#if m.childs?exists && 0 < m.childs?size>,<@showMenu menus=m.childs/></#if>]<#if m_has_next>,</#if>
    </#if>
    </#list>
</#macro>

<#macro showTree trees node checkAble="false">
<#if checkAble == "false">
    <#list trees as menu>
    var ${node}_${menu_index}  = new WebFXTreeItem('${menu.label?if_exists}', '${menu.url?if_exists}');
    <#if menu.target?exists && menu.target != "">
    ${node}_${menu_index}.target = '${menu.target}';
    </#if>
    ${node}.add(${node}_${menu_index});
    <#if menu.childs?exists>
    <@showTree trees=menu.childs node="${node}_${menu_index}"/>
    </#if>
    </#list>
<#else>
    <#list trees as menu>
    var ${node}_${menu_index}  = new CheckableTreeItem('${menu.name?if_exists}', '${menu.label?if_exists}', '${menu.url?if_exists}', true, false, 'checkParentNodes(${node}_${menu_index}, true, false);checkChildNodes(${node}_${menu_index}, true, true)');
    <#if menu.target?exists && menu.target != "">
    ${node}_${menu_index}.target = '${menu.target}';
    </#if>
    ${node}.add(${node}_${menu_index});
    <#if menu.childs?exists>
    <@showTree trees=menu.childs node="${node}_${menu_index}" checkAble=checkAble/>
    </#if>
    </#list>
</#if>    
</#macro>
