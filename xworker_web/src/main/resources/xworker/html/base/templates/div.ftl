<#import "common.ftl" as lib>
<div <@lib.showProperties names=["name", "id", "class", "style"] dataObject=object/> ${object.otherAttributes?if_exists}>
${childHtmls?if_exists}
</div>