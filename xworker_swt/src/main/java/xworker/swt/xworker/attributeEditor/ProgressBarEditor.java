package xworker.swt.xworker.attributeEditor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.swt.xworker.AttributeEditor;

public class ProgressBarEditor {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//创建标签
		int style = SWT.NONE;
		
		//Thing attribute = (Thing) actionContext.get("attribute");
		Map<String, String> params = null;
		String inputattrs = self.getString("inputattrs");
		if(inputattrs != null && !"".equals(inputattrs)){
		    params = UtilString.getParams(inputattrs, ",");
		    
		    String pstyle = params.get("style");
		    if("HORIZONTAL".equals(pstyle)){
		    	style |= SWT.HORIZONTAL;
		    }else if("VERTICAL".equals(pstyle)){
		    	style |= SWT.VERTICAL;
		    }
		
		    if("true".equals(params.get("SMOOTH")))
		        style |= SWT.SMOOTH;
		    
		    if("true".equals(params.get("INDETERMINATE")))
		        style |= SWT.INDETERMINATE;
		    
		    if("true".equals(params.get("BORDER")))
		        style |= SWT.BORDER;
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		ProgressBar progressBar = new ProgressBar(parent, style);
		
		if(params != null) {
			if(params.get("maximum") != null) {
				progressBar.setMaximum(UtilData.getInt(params.get("maximum"), 100));
			}
			
			if(params.get("minimum") != null) {
				progressBar.setMinimum(UtilData.getInt(params.get("minimum"), 0));
			}
		}

		//设置布局数据
		progressBar.setLayoutData((GridData) actionContext.get("layoutData"));
		
		//创建并返回ActionContainer
		ActionContext ac = new ActionContext();
		ac.put("control", progressBar);		
		Thing actionThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.ProgressBarEditor/@actions1");		
		ActionContainer actionContainer =  actionThing.doAction("create", ac);
		
		progressBar.setData(AttributeEditor.ACTIONCONTAINER, actionContainer);
		
		return progressBar;
    } 
    
    public static void setValue(ActionContext actionContext){
    	ProgressBar progressBar = (ProgressBar) actionContext.get("control");
    	Object value = actionContext.get("value");
    	progressBar.setData(value);
    	if(value != null){
    		progressBar.setSelection(UtilData.getInt(value, 0));
    	}    	    
    }
    
    public static Object getValue(ActionContext actionContext){
    	ProgressBar progressBar = (ProgressBar) actionContext.get("control");
    	return progressBar.getSelection();
    }
}
