<div class="btn-group" role="group" aria-label="">
<#if command == "download">
<#-- 还没有下载 -->
<a class="btn btn-default" href="javascript:download('${projectId}', '${majorVersion}', '${minorVersion}','${arguments[4]}','${arguments[5]}')">下载</a>
<#else>
<#-- 是否有功能链接 -->
<#if 0 < links?size>
            <div class="btn-group" role="group">
              <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu_${projectId}" data-toggle="dropdown" aria-haspopup="true">
                功能菜单
                <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" aria-labelledby="dropdownMenu_${projectId}">
              <#list links as link>
                <li><a href="${link.href}">${link.label}</a></li>
              </#list>
              </ul>
            </div>
</#if>
<a class="btn btn-default" href="javascript:xw_invoke('thing:${project.metadata.path}')">编辑项目</a>
<button type="button" class="btn btn-default"><b>当前版本：</b>${project.majorVersion?string}.${project.minorVersion?string}</button>
<#if command == "update">
<a class="btn btn-default" href="javascript:download('${projectId}', '${project.majorVersion?string}', '${project.minorVersion?string}','${arguments[4]}','${arguments[5]}')">有新版本，点击更新</a></#if>
</#if>
<#if historygo?exists>
<a class="btn btn-default" href="javascript:window.history.go(-1)">返回</a>
</#if>
</div>

