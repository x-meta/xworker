<#-- 数据对象转Java的模板 -->
package ${thing.package?if_exists};

<#assign haveList = false/>
<#assign haveDate = false/>
<#list thing.get("attribute@") as attr>
<#if attr.type?exists && (attr.type =="date" || attr.type == "datetime" || attr.type == "time")>
<#assign haveDate = true/>
<#break>
</#if>  
</#list>
<#-- 引入其他数据对象 -->
<#list thing.get("thing@") as child>
<#assign rthing = world.getThing(child.getString("dataObjectPath"))/>
import ${rthing.package?if_exists}.${rthing.name?if_exists};
<#if child.many?exists && child.many == "true">
<#assign haveList = true/>
</#if>
</#list>

<#if haveList>
import java.util.List;
import java.util.ArrayList;
</#if>
<#if haveDate>
import java.util.Date;
</#if>

/**
 * ${thing.description?if_exists}
 */
public class ${thing.name?if_exists}{
<#-- 属性 -->
<#list thing.get("attribute@") as attr>
    /** ${attr.label?if_exists}: ${attr.description?if_exists} */
    public <@getAttributeType attr=attr/> ${attr.name?if_exists};
    
</#list>
<#list thing.get("thing@") as child>
<#assign rthing = world.getThing(child.getString("dataObjectPath"))/>
    /** ${child.label?if_exists}: ${child.description?if_exists} **/
<#if child.many?exists && child.many == "true">
    public List<${rthing.name?if_exists}> ${child.name?if_exists} = new ArrayList<${rthing.name?if_exists}>();
<#else>
    public ${rthing.name?if_exists} ${child.name?if_exists} = null;
</#if>
</#list>

<#-- public method -->
<#list thing.get("attribute@") as attr>
    /** ${attr.label?if_exists}: ${attr.description?if_exists} */
    public <@getAttributeType attr=attr/> get${attr.name?cap_first}(){
        return ${attr.name?if_exists};
    }
    
</#list>
<#list thing.get("thing@") as child>
<#assign rthing = world.getThing(child.getString("dataObjectPath"))/>
    /** ${child.label?if_exists}: ${child.description?if_exists} **/
<#if child.many?exists && child.many == "true">
    public List<${rthing.name?if_exists}> get${child.name?cap_first}(){
        return ${child.name?if_exists};
    };
<#else>
    public ${rthing.name?if_exists} get${child.name?cap_first}(){
        return ${child.name?if_exists};
    };
</#if>
</#list>
} 
<#macro getAttributeType attr>
<#switch attr.type>
<#case "string">String<#break>
<#case "byte">byte<#break>
<#case "short">short<#break>
<#case "int">int<#break>
<#case "long">long<#break>
<#case "float">float<#break>
<#case "double">double<#break>
<#case "boolean">boolean<#break>
<#case "date">
<#case "datetime">
<#case "time">Date<#break>
<#case "byte[]">byte[]<#break>
<#default>String<
</#switch>
</#macro>