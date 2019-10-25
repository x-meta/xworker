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