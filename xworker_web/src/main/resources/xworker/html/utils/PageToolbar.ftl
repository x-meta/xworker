<#assign pageInfo = data.pageInfo>
<#assign page = data.page>
<div>
<${r"#"}if pageInfo.hasPrePage()>
<a class="xworekr_text_box_1" href="${data.url}&${data.page}=${r"$"}{${pageInfo}.page - 1}">${data.prePage}</a>
</${r"#"}if>
<${r"#"}list pageInfo.prePages as prePage>
<a class="xworekr_text_box_1" href="${data.url}&${data.page}=${r"$"}{prePage}">${r"$"}{prePage}</a>
</${r"#"}list>
<span class="xworekr_text_box_1_noborder"><b>${r"$"}{${pageInfo}.page}</b></span>
<${r"#"}list pageInfo.nextPages as nextPgae>
<a class="xworekr_text_box_1" href="${data.url}&${data.page}=${r"$"}{nextPgae}">${r"$"}{nextPgae}</a>
</${r"#"}list>
<${r"#"}if pageInfo.hasNextPage()>
<a class="xworekr_text_box_1" href="${data.url}&${data.page}=${r"$"}{${pageInfo}.page + 1}">${data.nextPage}</a>
</${r"#"}if>
<#if data.getStringBlankAsNull("total")?exists>
<span class="xworekr_text_box_1_noborder">${data.total}</span></#if>
</div>