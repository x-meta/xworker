package xworker.java.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.OgnlException;

public class ClipboardActions {
	public static void setContent(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();//获取系统剪切板  
	    String str = (String) self.doAction("getContent", actionContext);//String.valueOf(UtilData.getObject(self, "content", actionContext));
	    if(str != null){
		    StringSelection selection = new StringSelection(str);//构建String数据类型  
		    clipboard.setContents(selection, selection);//添加文本到系统剪切板
	    }
	}
}
