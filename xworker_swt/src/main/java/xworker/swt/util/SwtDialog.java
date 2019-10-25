/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.util;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class SwtDialog extends Dialog{
	private static Logger logger = LoggerFactory.getLogger(SwtDialog.class);
	
	Shell myShell;
	ActionContext actionContext;
	
	public SwtDialog(Shell shell, ActionContext actionContext){		
		super((Shell) shell.getParent());
		
		this.actionContext = actionContext;
		this.myShell = shell;
		
		setRWTShell();
	}
	
	public SwtDialog(Shell parent, Shell shell, ActionContext actionContext) {
		super(parent);

		this.actionContext = actionContext;
		this.myShell = shell;
		
		setRWTShell();
	}

	private void setRWTShell() {
		if(SwtUtils.isRWT()) {
			//rwt定义了shell字段，常规的swt没有
			try {
				Field field = Dialog.class.getDeclaredField("shell");
				field.set(this, myShell);
			}catch(Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object open(){
		if(SwtUtils.isRWT()) {
			this.open(null);
			return null;
		}else {
			//Shell parent = getParent();        
			myShell.open();	
	
	        Display display = myShell.getDisplay();
	        try{
		        while (!myShell.isDisposed()) {        	
		                if (!display.readAndDispatch()) display.sleep();
		        }
	        }catch(Exception e){        	
	        }
	        
	        return actionContext.get("result");
		}

	}
	
	/**
	 * 如果要支持RWT，那么应该使用这个方法。
	 * @param callback
	 */
	public Object open(final SwtDialogCallback callback) {
		return open(callback, false);
	}
	
	/**
	 * 如果要支持RWT，那么应该使用这个方法。
	 * @param callback
	 * @param sync 是否同步
	 */
	public Object open(final SwtDialogCallback callback, boolean sync) {
		///Shell parent = getParent();      
		if(SwtUtils.isRWT()) {
			if(callback != null) {
				myShell.addListener(SWT.Dispose, new Listener() {
					@Override
					public void handleEvent(Event event) {
						try {
							Object result = actionContext.get("result");
							callback.disposed(result);
						}catch(Exception e) {
							logger.error("Do callback error",  e);
						}
					}
					
				});
			}
			Thing swt = World.getInstance().getThing("xworker.swt.SWT");
			swt.doAction("rwtOpenDialog", actionContext, "dialog", this, "callback", null);
			return null;
		}else {
			if(callback != null) {
				myShell.addListener(SWT.Dispose, new Listener() {
					@Override
					public void handleEvent(Event event) {
						try {
							Object result = actionContext.get("result");
							callback.disposed(result);
						}catch(Exception e) {
							logger.error("Handle swt dialog callback error", e);
						}
					}				
				});
			}
			myShell.open();
			
			if(sync) {
				Display display = myShell.getDisplay();
		        try{
			        while (!myShell.isDisposed()) {        	
			                if (!display.readAndDispatch()) display.sleep();
			        }
		        }catch(Exception e){        	
		        }
		        
		        return actionContext.get("result");
			}else {
				return null;
			}
		}
	}
	
	public static Object open(Shell shell, ActionContext actionContext){
		SwtDialog dialog = new SwtDialog(shell, actionContext);
		return dialog.open();
	}
	
	/**
	 * 打开对话框的动作。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static Object openDialogAction(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Shell parentShell = (Shell) UtilData.getData(self, "parentShell", actionContext);
		String shellPath = UtilString.getString(self, "shellPath", actionContext);
		String vars = UtilString.getString(self, "vars", actionContext);
		
		ActionContext ac = new ActionContext();
		if(vars != null && !"".equals(vars)){
			for(String var : vars.split("[,]")){
				ac.put(var, actionContext.get(var));
			}
		}
		
		ac.put("parent", parentShell);
		
		Thing shellThing = World.getInstance().getThing(shellPath);
		if(shellThing == null){
			throw new ActionException("Shell thing is null, shellPath=" + shellPath + ", actionPath=" + self.getMetadata().getPath());			
		}
		
		Shell shell = (Shell) shellThing.doAction("create", ac);
		SwtDialog dialog = new SwtDialog(parentShell, shell, ac);
		return dialog.open();		
	}
}