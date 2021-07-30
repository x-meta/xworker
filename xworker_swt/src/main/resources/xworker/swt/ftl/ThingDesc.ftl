<!doctype html>
<html>
<head>
<title>WorkingSet</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script language="javascript">
    window.console = window.console || (function () {
        var c = {}; c.log = c.warn = c.debug = c.info = c.error = c.time = c.dir = c.profile
        = c.clear = c.exception = c.trace = c.assert = function () { };
        return c;
    })();

    function invoke(status){
    	//alert(utilBrowserFunction);
        if (typeof(utilBrowserFunction) == "undefined"){
            window.status = status;
            window.status = "";
        }else{
            utilBrowserFunction(status);
        }
    }

    function xw_invoke(status){
    	if (typeof(utilBrowserFunction) == "undefined"){
            window.status = status;
            window.status = "";
        }else{
            utilBrowserFunction(status);
        }
    }

    function setStatus(status){
        window.status = status;
        window.status = "";
    }
</script>
</head>
<body >
<div id="content" >
${html?if_exists}
</div>
<p style="text-align: right;">
<span style="font-size: xx-small;align=right"><b>
<a onClick="window.clipboardData.setData('text','xworker.ide.worldexplorer.swt.help.ProjectHelps/@WorkingSet');alert('已拷贝到剪贴板。')"  href="javascript:void(0)">copy</a>
<a onClick="invoke('openThing:${path?if_exists}')" href="javascript:void(0)">open</a>
<a onClick="invoke('openThing:${realPath?if_exists}')" href="javascript:void(0)">openr</a>
</b></span></span>
</p>

 </body>
</html>