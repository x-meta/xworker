<#import "common.ftl" as lib>
<input type="text" id="${object.name?if_exists}" <@lib.showProperties names=["name", "value", "size", "alt", "emsg", "class", "style"] dataObject=object/> <#if object.disabled?exists && object.disabled == "true">disabled </#if>${object.attris?if_exists} onKeyUp="XWorker.dateInput(this)"/><IMG id="calendar${object.name?if_exists}" src="${r"$"}{request.contextPath}/js/calendar/jscalendar.gif" border="0" align="absmiddle" ${object.otherAttributes?if_exists}>
<script type="text/javascript">
 Calendar.setup({
        inputField     :    "${object.name?if_exists}",     // id of the input attribute
        ifFormat       :    "${object.formatf?default("%Y-%m-%d")}",     // format of the input attribute
        button         :    "calendar${object.name?if_exists}",  // trigger for the calendar (button ID)
        align          :    "Bl",           // alignment (defaults to "Bl")
        singleClick    :    true
    });    
</script>