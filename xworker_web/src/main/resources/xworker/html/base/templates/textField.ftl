<#import "common.ftl" as lib>
<input type="text" id="${object.name?if_exists}" <@lib.showProperties names=["name", "value", "size", "alt", "emsg", "class", "style"] dataObject=object/> <#if object.disabled?exists && object.disabled == "true">disabled </#if><#if object.readOnly?exists && object.readOnly == "true"> readOnly="true"</#if>${object.otherAttributes?if_exists}/>