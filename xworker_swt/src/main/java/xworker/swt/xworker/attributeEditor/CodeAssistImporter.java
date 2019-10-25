package xworker.swt.xworker.attributeEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.java.assist.JavaCacheItem;
import xworker.java.assist.JavaClassCache;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.xwidgets.SelectContent;
import xworker.swt.xworker.CodeAssistor;

public class CodeAssistImporter {
	public static Object query(ActionContext actionContext) {
		//选择类，最多一次返回500个
		List<SelectContent> contents = new ArrayList<SelectContent>();
		String text = actionContext.getObject("text");
		
		List<JavaCacheItem> cls = JavaClassCache.indexOf(text);
		int count = 0;
		for(JavaCacheItem c : cls){
		    if(c.type == 1){
		        contents.add(new SelectContent(c.path));
		        count++;
		    }
		    
		    if(count >= 500){
		        break;
		    }
		}

		return contents;
	}
	
	public static void selected(ActionContext actionContext) throws ClassNotFoundException {
		String value = actionContext.getObject("value");
		final Control text = actionContext.getObject("text");
		CodeAssistor assistor = actionContext.getObject("assistor");
		
		if(value != null){
		    //插入import
		    String clsName = value;
		    int offset = SwtTextUtils.getCaretOffset(text);
		    value = "import " + value + ";";
		    SwtTextUtils.insert(text, value);
		    if(SwtTextUtils.getCaretOffset(text) == offset){
			    SwtTextUtils.setCaretOffset(text, offset + value.length());
			}

			//设置类辅助
		    Class<?> cls = Class.forName(clsName);
		    int index = clsName.lastIndexOf(".");
		    if(index != -1){
		        clsName = clsName.substring(index + 1, clsName.length());
		        assistor.putCache(text, clsName, cls);
		    }
		}

		Shell shell = actionContext.getObject("shell");
		shell.setVisible(false);
		text.getDisplay().asyncExec(new Runnable(){
			public void run() {
				text.setFocus();
			}
		});
	}
	
	public static void closeButtonSelection(ActionContext actionContext) {
		Shell shell = actionContext.getObject("shell");
		Control text = actionContext.getObject("text");
		
		shell.dispose();
		text.setFocus();
	}
}
