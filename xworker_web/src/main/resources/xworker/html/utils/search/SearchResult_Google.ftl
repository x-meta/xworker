<#assign datas = data.listData>
<#assign title = data.title>
<#assign info = data.info>
<#assign url = data.url>
<#assign image = data.image>
<${r"#"}list ${datas} as data>
<${r"#"}if data.${image}?exists && data.${image} != "">
<div class="xworker_result_c-container" id="86" style="font: 13px/1.54 arial; width: 538px; color: rgb(51, 51, 51); text-transform: none; text-indent: 0px; letter-spacing: normal; margin-bottom: 14px; word-spacing: 0px; white-space: normal; border-collapse: collapse; table-layout: fixed; word-break: break-all; word-wrap: break-word; widows: 1; font-size-adjust: none; font-stretch: normal; background-color: rgb(255, 255, 255); -webkit-text-stroke-width: 0px;">
<h3 style="font-weight: normal;font-size: medium;margin-bottom: 1px;"><a href="${r"$"}{(data.${url}?html)?if_exists}" target="${data.urlTarget?default("_blank")}">${r"$"}{(data.${title}?html)?if_exists}</a></h3>
<div>
<div class="general_image_pic c-span6" style="list-style: none; width: 121px; margin-right: 17px; float: left;"><a class="c-img6" href="${r"$"}{(data.${url}?html)?if_exists}" style="background: no-repeat 50% 50% rgb(255, 255, 255); width: 121px; height: 75px; text-align: left; overflow: hidden; text-decoration: none; display: block;" target="${data.urlTarget?default("_blank")}"><img class="c-img c-img6" src="${r"$"}{(data.${image}?html)?if_exists}" style="border:0px currentColor; display:block; height:75px; min-height:1px; width:121px" /></a></div>
<div class="c-abstract" style="font-size: 13px;height: 75px; ">${r"$"}{(data.${info})?if_exists}</div>
</div>
</div>
<${r"#"}else>
<div class="xworker_result_c-container" id="87" style="font: 13px/1.54 arial; width: 538px; color: rgb(51, 51, 51); text-transform: none; text-indent: 0px; letter-spacing: normal; margin-bottom: 14px; word-spacing: 0px; white-space: normal; border-collapse: collapse; table-layout: fixed; word-break: break-all; word-wrap: break-word; widows: 1; font-size-adjust: none; font-stretch: normal; background-color: rgb(255, 255, 255); -webkit-text-stroke-width: 0px;">
<h3 style="font-weight: normal;font-size: medium;margin-bottom: 1px;"><a href="${r"$"}{(data.${url}?html)?if_exists}" target="${data.urlTarget?default("_blank")}">${r"$"}{(data.${title}?html)?if_exists}</a></h3>

<div class="c-abstract" style="font-size: 13px;">${r"$"}{(data.${info})?if_exists}</div>
</div>
</${r"#"}if>
</${r"#"}list>