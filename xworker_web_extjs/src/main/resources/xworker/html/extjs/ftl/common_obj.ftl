<#-- 生成为{}的样式 -->
<#if thing.varref?exists && thing.varref != "">
${ident?if_exists}${thing.varref}
<#else>
${ident?if_exists}{
<#list thing.allAttributesDescriptors as attributeDescriptor>
<#if thing[attributeDescriptor.name]?exists && ((attributeDescriptor.default?exists && attributeDescriptor.default != "" && attributeDescriptor.default != thing[attributeDescriptor.name]) || (!attributeDescriptor.default?exists || attributeDescriptor.default?exists == ""))>
${ident?if_exists}    ${attributeDescriptor.name}: ${thing[attributeDescriptor.name]}<#if attributeDescriptor_has_next>,</#if>
</#if>
</#list>
${ident?if_exists}}
</#if>