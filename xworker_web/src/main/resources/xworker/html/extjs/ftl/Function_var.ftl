<#if thing.ref?exists && thing.ref != "">
${ident?if_exists}var ${thing.name} = ${thing.ref};
<#else>
${ident?if_exists}var ${thing.name} = new function(${thing.params?if_exists}){
<#if thing.code?exists>
<#list thing.code?split("\n") as line>
${ident?if_exists}    ${line}
</#list>
</#if>
};
</#if>