<#marco listAttributes thing ident>
<#list thing.allAttributesDescriptors as attributeDescriptor>
<#if thing[attributeDescriptor.name]?exists && ((attributeDescriptor.default?exists && attributeDescriptor.default != "" && attributeDescriptor.default != thing[attributeDescriptor.name]) || (!attributeDescriptor.default?exists || attributeDescriptor.default?exists == ""))>
${ident?if_exists}    ${attributeDescriptor.name}: ${thing[attributeDescriptor.name]}<#if attributeDescriptor_has_next>,</#if>
</#if>
</#list>
</#macro>