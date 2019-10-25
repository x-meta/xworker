package xworker.ui.function.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.util.UtilData;

import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;

public class FileDialogActions {
	public static void saveFileDialog(final ActionContext actionContext){
		final FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("OpenFileDialog must be running in ui mode");
		}
		
		final Shell parent = (Shell) actionContext.get("parent");
		int style = SWT.SAVE;				
		
		final int st = style;
				
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				FileDialog fileDialog = new FileDialog(parent, st);
				String value = fileDialog.open();
				if(value != null){
					FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
				}else{
					FunctionRequestUtil.callbakMyselfCancel(request, null, actionContext);
				}
			}
		});
	}
	
	public static void openFileDialogNoParams(final ActionContext actionContext){
		final FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("OpenFileDialog must be running in ui mode");
		}
		
		final Shell parent = (Shell) actionContext.get("parent");
		int style = SWT.OPEN;				
		
		final int st = style;
				
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				FileDialog fileDialog = new FileDialog(parent, st);
				String value = fileDialog.open();
				if(value != null){
					FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
				}else{
					FunctionRequestUtil.callbakMyselfCancel(request, null, actionContext);
				}
			}
		});
	}
	
	public static void openFileDialogMulti(final ActionContext actionContext){
		final FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("OpenFileDialogMulti must be running in ui mode");
		}
		
		final Shell parent = (Shell) actionContext.get("parent");
		int style = SWT.OPEN | SWT.MULTI;				
		
		final int st = style;
				
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				FileDialog fileDialog = new FileDialog(parent, st);
				String value = fileDialog.open();
				if(value != null){
					FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
				}else{
					FunctionRequestUtil.callbakMyselfCancel(request, null, actionContext);
				}
			}
		});
	}
	
	public static void openFileDialog(final ActionContext actionContext){
		final FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("FileDialog must be running in ui mode");
		}
		final Shell parent = (Shell) actionContext.get("parent");
		Object styleObj = actionContext.get("style");
		int style = SWT.OPEN;
		if(styleObj != null){
			if(styleObj instanceof String){
				if("SAVE".equals(styleObj)){
					style =SWT.SAVE;
				}else{
					style =SWT.OPEN;
				}
			}else{
				style = (Integer) styleObj;
			}
		}
		
		Object multi = (Object) actionContext.get("multi");
		if(multi != null && UtilData.getBoolean(multi, false)){
			style = style | SWT.MULTI;
		}
		
		final int st = style;
		final String fileName = (String) actionContext.get("fileName");
		final String filterExtensions[] = (String[]) actionContext.get("filterExtensions");
		final String filterNames[] = (String[]) actionContext.get("filterNames");
		final Integer filterIndex = (Integer) actionContext.get("filterIndex");
		final String filterPath = (String) actionContext.get("filterPath");		
				
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				FileDialog fileDialog = new FileDialog(parent, st);
				if(fileName != null){
					fileDialog.setFileName(fileName);
				}
				if(filterExtensions != null){
					fileDialog.setFilterExtensions(filterExtensions);
				}
				if(filterNames != null){
					fileDialog.setFilterNames(filterNames);
				}
				if(filterIndex != null){
					fileDialog.setFilterIndex(filterIndex);
				}
				if(filterPath != null){
					fileDialog.setFilterPath(filterPath);
				}
				
				String value = fileDialog.open();
				if(value != null){
					FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
				}else{
					FunctionRequestUtil.callbakMyselfCancel(request, null, actionContext);
				}
			}
		});
		
	}
}
