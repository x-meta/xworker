<html>
<body>
模板列表<p/>
<#list thing.allDescriptors as descriptor>
${descriptor.metadata.label}</br>
<#list descriptor["templates@0/textTemplate@"] as txtTemplate>
<a href="do?sc=core:tools:web.ThingTemplate:/@process&thing=${thing.metadata.path?html}&tempalte=${(txtTemplate.path?html)?if_exists}&contentType=${txtTemplate.contentType?if_exists}">${txtTemplate.label}</a><br/>
</#list>
</br>
</#list>
</body>
</html>