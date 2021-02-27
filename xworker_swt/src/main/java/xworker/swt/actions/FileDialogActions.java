package xworker.swt.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class FileDialogActions {
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		if(shell == null){
			//如果没有使用IDE
			shell = (Shell) XWorkerUtils.getIDEShell();
		}
		
		int style = SWT.OPEN;
		String s = self.getStringBlankAsNull("style");
		if("SAVE".equals(s)){
			style = SWT.SAVE;
		}else if("MULTI".equals(s)){
			style = SWT.MULTI;
		}
		
		 if(SwtUtils.isRWT()) {
        	ActionContext ac = new ActionContext();
        	ac.put("parent", shell);
        	ac.put("thing", self);
        	ac.put("parentContext", actionContext);
        	String filePath = self.doAction("getFilterPath", actionContext);
        	if(filePath == null) {
        		filePath = ".";
        	}
        	ac.put("filePath", filePath);
        	
        	Thing dialogThing = World.getInstance().getThing("xworker.swt.actions.prototypes.RWTFileDialog");
        	Shell dialogShell = dialogThing.doAction("create", ac);
        	dialogShell.setVisible(true);
        	return null;
        }else {
			FileDialog dialog = new FileDialog(shell, style);
			String fileName = (String) self.doAction("getFileName", actionContext);
			String text = (String) self.doAction("getText", actionContext);
			String[] filterExtensions = (String[]) self.doAction("getFilterExtensions", actionContext);
			Integer filterIndex = (Integer) self.doAction("getFilterIndex", actionContext);
			String[] filterNames = (String[]) self.doAction("getFilterNames", actionContext);
			String filterPath = (String) self.doAction("getFilterPath", actionContext);
			Boolean overwrite = (Boolean) self.doAction("getOverwrite", actionContext);
			
			if(fileName != null){
				dialog.setFileName(fileName);
			}
			if(filterExtensions != null){
				dialog.setFilterExtensions(filterExtensions);
			}
			if(filterIndex != null){
				dialog.setFilterIndex(filterIndex);
			}
			if(filterNames != null){
				dialog.setFilterNames(filterNames);
			}
			if(filterPath != null){
				dialog.setFilterPath(filterPath);			
			}
			if(overwrite != null){
				dialog.setOverwrite(overwrite);
			}
			if(text != null){
				dialog.setText(text);
			}
			
			String f = dialog.open();
			if(f != null){
				self.doAction("onSelected", actionContext, "fileName", f);
			}else{
				self.doAction("onUnSelected", actionContext, "fileName", f);
			}
			
			return self.doAction("open", actionContext, UtilMap.toMap("fileName", f));
        }
	}
	
	public static Object open(ActionContext actionContext){
		//Thing self = (Thing) actionContext.get("self");
		//logger.info(self.getMetadata().getPath() + " open:" + actionContext.get("fileName"));
		
		return actionContext.get("fileName");
	}

	public static Object getShell(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Object obj = UtilData.getData(self, "shell", actionContext);
		Shell shell = null;
		if(obj instanceof String){
			shell = (Shell) actionContext.get((String) obj);
		}else if(obj instanceof Shell){
			shell = (Shell) obj;
		}
		
		if(shell == null){
			shell = Display.getCurrent().getActiveShell();
		}
		if(shell == null){
			shell = (Shell) XWorkerUtils.getIDEShell();
		}
		
		return shell;
	}

	public static Object getFileName(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return UtilString.getString(self, "fileName", actionContext);		
	}
	
	public static Object getText(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return UtilString.getString(self, "text", actionContext);		
	}

	public static Object getFilterExtensions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String filterExtensions = self.getStringBlankAsNull("filterExtensions");
		if(filterExtensions != null){
			return filterExtensions.split("[,]");
		}else{
			return null;
		}
	}

	public static Object getFilterIndex(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		if(self.getStringBlankAsNull("filterIndex") != null){
			return self.getInt("filterIndex");
		}else{
			return null;
		}
	}

	public static Object getFilterNames(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String filterExtensions = self.getStringBlankAsNull("filterNames");
		if(filterExtensions != null){
			return filterExtensions.split("[,]");
		}else{
			return null;
		}
	}

	public static Object getFilterPath(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return UtilString.getString(self, "filterPath", actionContext);
	}

	public static Object getOverwrite(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getBoolean("overwrite");
	}
}
