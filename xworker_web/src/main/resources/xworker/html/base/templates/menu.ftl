<#import "common.ftl" as lib>
<#if object.display == "menu">
<DIV ID="${object.name}MenuID" width="100%"></DIV>
<script language="javascript">
    var ${object.name}Menu =[<#if object.includeRoot == "true">
    <#if (!object.values?exists || object.values == "") && object.childs?exists>
    <@lib.showMenu menus=menu.childs/>
    <#else>
    <${r"@"}showMenu menus=[${object.values?if_exists}]/>
    </#if>
    <#else>
    <#if (!object.values?exists || object.values == "") && object.childs?exists>
    <@lib.showMenu menus=menu.childs/>
    <#else>
    <${r"@"}showMenu menus=${object.values?if_exists}.childs/>
    </#if>
    </#if>];

    var tmpClickOpen = cmThemeOffice.clickOpen;
    cmThemeOffice.clickOpen = 2;
    cmDraw ('${object.name}MenuID', ${object.name}Menu, 'hbr', cmThemeOffice, 'ThemeOffice');
    cmThemeOffice.clickOpen = tmpClickOpen;
</script>
<#else>
<script language="javascript">
<#if object.checkAble?exists && object.checkAble == "true">
    <#if object.includeRoot?exists && object.includeRoot == "true">
    var ${object.name}tree = new CheckableTree('${r"$"}{${object.values}.name?if_exists}', '${r"$"}{${object.values}.label?default("根节点")}', '${r"$"}{${object.values}.url?if_exists}', true, ${r"$"}{${object.values}.checked?default("false")}, 'checkParentNodes(${object.name}tree, true, false);checkChildNodes(${object.name}tree, true, true)');
    <#else>
    var ${object.name}tree = new CheckableTree('${object.id?if_exists}', '${object.label}', '${object.url?if_exists}');
    </#if>
	${object.name}tree.setBehavior('classic');
	
	<#if (!object.values?exists || object.values == "") && object.childs?exists>
	<@lib.showTree trees=menu.childs node="${object.name}tree"/>
	<#elseif object.values?exists> 
    <${r"@"}showTree trees=${object.values}.childs node="${object.name}tree" checkAble="true"/>    
    </#if>
    //画出树
	document.write(${object.name}tree);
<#else>
    <#if object.includeRoot?exists && object.includeRoot == "true">
    var ${object.name}tree = new WebFXTree('${r"$"}{${object.values}.label?default("根节点")}', '${r"$"}{${object.values}.url?if_exists}');
    ${object.name}tree.target = '${r"$"}{${object.values}.target?default("content")}';
    <#else>
    var ${object.name}tree = new WebFXTree('${object.label}', '${object.url?if_exists}');
    ${object.name}tree.target = '${r"$"}{${object.values}.target?default("content")}';
    </#if>
	${object.name}tree.setBehavior('classic');
	
	<#if (!object.values?exists || object.values == "") && object.childs?exists>
	<@lib.showTree trees=menu.childs node="${object.name}tree"/>
	<#elseif object.values?exists> 
    <${r"@"}showTree trees=${object.values}.childs node="${object.name}tree"/>    
    </#if>
    //画出树
	document.write(${object.name}tree);
</#if>
</script>
</#if>