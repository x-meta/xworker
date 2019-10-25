<#import "common.ftl" as lib>
<span <@lib.showProperties names=["name", "id", "class", "style"] dataObject=object/> ${object.otherAttributes?if_exists}>
${childHtmls?if_exists}
</span>