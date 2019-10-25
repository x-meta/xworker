function openwin(nUrl,nheight,nwidth) 
{ 
	window.open (nUrl, "newwindow", "height="+nheight+", width="+nwidth+",top=100, left=100,toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, status=no") 
}

function OpenWinCountFile(nUrl,nheight,nwidth) 
{ 
	window.open (nUrl, "window", "height="+nheight+", width="+nwidth+",top=100, left=100,toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, status=no") 
}


function winfullopen(nUrl)
{
	var targeturl=nUrl
	newwin=window.open("","","scrollbars")
	if (document.all)
	{
		newwin.moveTo(0,0)
		newwin.resizeTo(screen.width,screen.height)
	}
	newwin.location=targeturl
}

function  RefreshWindow()
{
	try{
	opener.location.reload(); 
	window.close();
	}
	catch(e){window.close();}
}