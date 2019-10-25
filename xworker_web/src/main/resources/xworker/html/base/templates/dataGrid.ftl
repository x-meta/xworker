<#import "common.ftl" as lib>
<script language="javascript">
    <#if object.dataSourceType == "xml">
    var ${object.name}Table = new Active.XML.Table;
    ${object.name}Table.setURL("${object.dataSource?if_exists}");
    ${object.name}Table.backURL="${object.dataSource?if_exists}";
    <#if object.dataSource?exists && object.dataSource != "">
    ${object.name}Table.request();
    </#if>
    var columnNames = [<#list object["/column@"] as column>"${column.name?if_exists}"<#if column_has_next>, </#if></#list>];
    ${object.name}Table.setColumns(columnNames);
    </#if>	
    var columns = [<#list object["/column@"] as column>"${column.label?if_exists}"<#if column_has_next>, </#if></#list>];
    var ${object.name} = new Active.Controls.Grid;	
    <#if object.dataSourceType == "xml">
    ${object.name}.setDataModel(${object.name}Table);
    <#elseif object.dataSourceType == "script">	
    ${object.name}.setRowProperty("count", ${object.dataSource?if_exists}.length);
    //${object.name}.setColumnProperty("count", 5);
    ${object.name}.setDataProperty("text", function(i, j){return ${object.dataSource?if_exists}[i][j]});
    </#if>	
    ${object.name}.setColumnProperty("texts", columns);
    var row = new Active.Templates.Row;
    <#list object["/row@"] as row>	
    <#list row["/event@"] as event>
    //row event
    row.setEvent("${event.name?if_exists}", "${event.value?if_exists}");
    </#list>	
    </#list>
    //row.setEvent("onmouseover", "mouseover(this, 'active-row-highlight')");
    //row.setEvent("onmouseout", "mouseout(this, 'active-row-highlight')");
    ${object.name}.setTemplate("row", row);    
    <#if object.multiple?exists && object.multiple == "true">
    //multi selection
    ${object.name}.setSelectionProperty("multiple", true);
    </#if>
    <#list object["/event@"] as event>
    ${object.name}.setEvent("${event.name?if_exists}", "${event.value?if_exists}");
    </#list>
    ${object.script?if_exists}
    document.write(${object.name});
</script>