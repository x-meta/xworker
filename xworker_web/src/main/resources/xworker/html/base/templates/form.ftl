<#import "common.ftl" as lib>
<form name="${object.name}" id="${object.id?if_exists}" action="${object.getString("action")?if_exists}" <@lib.showProperties names=["enctype", "class", "style"] dataObject=object/> method="${object.method?default("POST")}"<#if object.target?exists && object.target != ""> target="${object.target?if_exists}"</#if> <@lib.showEvent object=object/> ${object.otherAttributes?if_exists}>
${childHtmls?if_exists}
</form>