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