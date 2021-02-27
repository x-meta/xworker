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
package xworker.swt.actions;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class ShellActionsOpenShellCreator {
	private static final String TAG = ShellActionsOpenShellCreator.class.getName();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void openShell(ActionContext actionContext ) throws OgnlException{
		 Thing self = (Thing) actionContext.get("self");
		 
		 //父窗口
		 Shell parent = null;
		 Object parentObj = self.doAction("getParent", actionContext);
		 if(parentObj instanceof Shell){
			 parent = (Shell) parentObj;
		 }else if(parentObj instanceof Display){
			 parent = ((Display) parentObj).getActiveShell();
		 }else if(parentObj instanceof Control){
			 parent = ((Control) parentObj).getShell();
		 }
			 
		 if(parent == null){
			 Display display = Display.getCurrent();
             if(display != null){
                 parent = display.getActiveShell();
             }
		 }
		 
		final World world = World.getInstance();
        Thing dataObj  = self;
        Thing viewObject = world.getThing((String) self.doAction("getShellPath", actionContext));
        if(viewObject == null){
            if(parent != null){
                MessageBox box = new MessageBox(parent, SWT.OK | SWT.ICON_WARNING);
                box.setText(UtilString.getString("res:res.w_exp:commonBoxTitle操作信息", actionContext));
                box.setMessage(UtilString.getString("res:res.w_exp:thingNotExists事物不存在：", actionContext) + dataObj.getString("path"));
                box.open();
                return;
            }else{
                Executor.info(TAG, "OpenShell: shell thing not exists, shellPath=" + self.getString("shellPath"));
                return;
            }
        }else{
        	boolean singleInstance = self.getBoolean("singleInstance");
        	boolean anotherThread = dataObj.getBoolean("anotherThread");
            if(singleInstance){
            	WeakReference<Shell> shellref = (WeakReference<Shell>) world.getData("openShell_shell_" + viewObject.getMetadata().getPath()); 
                if(shellref != null && shellref.get() != null && !shellref.get().isDisposed()){
                    final Shell shell = shellref.get();
                    
                    shell.getDisplay().asyncExec(new Runnable(){
                    	public void run(){
                    		shell.setVisible(true);
                            shell.setActive();
                    	}
                    });                
                    return;
                }
            }
                   
            if(!anotherThread){
                ActionContext ac = new ActionContext();
                ac.put("parent", Display.getCurrent().getActiveShell());
                Map<String, Object> params = (Map<String, Object>) self.doAction("getParameters", actionContext);
                if(params != null){
                	ac.putAll(params);
                }
                Shell shell = (Shell) viewObject.doAction("create", ac);
                
                if(singleInstance){
                	WeakReference<Thing> wr = new WeakReference(shell);
                    world.setData("openShell_shell_" + viewObject.getMetadata().getPath(), wr); 
                }
                shell.open();
            } else{
               //final ActionContext currentActionContext = actionContext;
               final Thing viewObj = viewObject;
               final Thing dataO = dataObj;
               final Map<String, Object> params = (Map<String, Object>) self.doAction("getParameters", actionContext);
               new Thread(new Runnable(){
            	   public void run(){
            		   ActionContext ac = new ActionContext();
            		   if(params != null){
            			   ac.putAll(params);
            		   }
                       Display display = new Display ();
                       ac.put("parent", display);                       
                       Shell shell = (Shell) viewObj.doAction("create", ac);
                       if(dataO.getBoolean("singleInstance")){
                    	   WeakReference<Thing> wr = new WeakReference(shell);
                           world.setData("openShell_shell_" + viewObj.getMetadata().getPath(), wr); 
                       }
                       shell.open ();
	               	    while (!shell.isDisposed ()) {
	               	        if (!display.readAndDispatch ()) display.sleep ();
	               	    }
	               	    display.dispose ();
            	   }
               }).start();              
            }      
        }
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        Shell parent = null;
        String sparent = self.getString("parent");
        if(sparent != null && !"".equals(sparent)){
            parent = (Shell) OgnlUtil.getValue(sparent, actionContext);
        }else{
            Display display = Display.getCurrent();
            if(display != null){
                parent = display.getActiveShell();
            }
            
        }
        
        final World world = World.getInstance();
        Thing dataObj  = self;
        Thing viewObject = world.getThing(self.getString("shellPath"));
        if(viewObject == null){
            String shellPath = (String) OgnlUtil.getValue(self.getString("shellPath"), actionContext);
            viewObject = world.getThing(shellPath);
        }
        if(viewObject == null){
            if(parent != null){
                MessageBox box = new MessageBox(parent, SWT.OK | SWT.ICON_WARNING);
                box.setText(UtilString.getString("res:res.w_exp:commonBoxTitle操作信息", actionContext));
                box.setMessage(UtilString.getString("res:res.w_exp:thingNotExists事物不存在：", actionContext) + dataObj.getString("path"));
                box.open();
                return;
            }else{
                Executor.info(TAG, "OpenShell: shell thing not exists, shellPath=" + self.getString("shellPath"));
                return;
            }
        }else{
            if(dataObj.getBoolean("singleInstance")){
            	WeakReference<Shell> shellref = (WeakReference<Shell>) world.getData("openShell_shell_" + viewObject.getMetadata().getPath()); 
                if(shellref != null && shellref.get() != null && !shellref.get().isDisposed()){
                    final Shell shell = shellref.get();
                    
                    shell.getDisplay().asyncExec(new Runnable(){
                    	public void run(){
                    		shell.setVisible(true);
                            shell.setActive();
                    	}
                    });                
                    return;
                }
            }
                   
            if(!dataObj.getBoolean("anotherThread")){
                ActionContext ac = new ActionContext(actionContext);
                ac.put("parent", Display.getCurrent().getActiveShell());
                Shell shell = (Shell) viewObject.doAction("create", ac);
                
                if(dataObj.getBoolean("singleInstance")){
                	WeakReference<Thing> wr = new WeakReference(shell);
                    world.setData("openShell_shell_" + viewObject.getMetadata().getPath(), wr); 
                }
                shell.open();
            } else{
               final ActionContext currentActionContext = actionContext;
               final Thing viewObj = viewObject;
               final Thing dataO = dataObj;
               new Thread(new Runnable(){
            	   public void run(){
            		   ActionContext ac = new ActionContext(currentActionContext);
                       Display display = new Display ();
                       ac.put("parent", display);
                       Shell shell = (Shell) viewObj.doAction("create", ac);
                       if(dataO.getBoolean("singleInstance")){
                    	   WeakReference<Thing> wr = new WeakReference(shell);
                           world.setData("openShell_shell_" + viewObj.getMetadata().getPath(), wr); 
                       }
                       shell.open ();
	               	    while (!shell.isDisposed ()) {
	               	        if (!display.readAndDispatch ()) display.sleep ();
	               	    }
	               	    display.dispose ();
            	   }
               }).start();              
            }      
        }
    }
	
	public static void runOpenCompositeShell(ActionContext actionContext){
		//创建窗口
		Thing self = (Thing) actionContext.get("self");
		ActionContext ac = new ActionContext();
		
		Shell parentShell = self.doAction("getParentShell", actionContext);
		ac.put("parent", parentShell);
		
		Map<String, Object> variables = self.doAction("getVariables", actionContext);
		if(variables != null) {
			ac.putAll(variables);
		}
		
		String title = self.doAction("getTitle", actionContext);
		
		World world = World.getInstance();
		Thing shellThing = world.getThing("xworker.swt.actions.CompositeShell");
		Shell shell = (Shell) shellThing.doAction("create", ac);

		//创建composite
		ac.put("parent", shell);
		Thing compositeThing = world.getThing(self.getString("compositePath"));
		if(compositeThing != null){
		    compositeThing.doAction("create", ac);
		}

		//设置标题
		if(title != null){
		    shell.setText(title);
		}

		//打开窗口
		shell.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	public static void runOpenCompositeShell2(ActionContext actionContext){
		//创建窗口
		Thing self = (Thing) actionContext.get("self");
		Shell parent = null;
		 Object parentObj = self.doAction("getParent", actionContext);
		 if(parentObj instanceof Shell){
			 parent = (Shell) parentObj;
		 }else if(parentObj instanceof Display){
			 parent = ((Display) parentObj).getActiveShell();
		 }else if(parentObj instanceof Control){
			 parent = ((Control) parentObj).getShell();
		 }
			 
		 if(parent == null){
			 Display display = Display.getCurrent();
            if(display != null){
                parent = display.getActiveShell();
            }
		 }
		 
		 //变量上下文
		ActionContext ac = new ActionContext();		
		Map<String, Object> params = (Map<String, Object>) self.doAction("getParameters", actionContext);
        if(params != null){
        	ac.putAll(params);
        }
		ac.put("parent", parent);
		World world = World.getInstance();
		Thing shellThing = world.getThing("xworker.swt.actions.CompositeShell");
		Shell shell = (Shell) shellThing.doAction("create", ac);
		shell.setLayout(new FillLayout());
		
		//创建composite
		ac.put("parent", shell);
		Thing compositeThing = world.getThing((String) self.doAction("getCompositePath", actionContext));
		if(compositeThing != null){
		    compositeThing.doAction("create", ac);
		}

		//设置标题
		String title = (String) self.doAction("getTitle", actionContext);
		if(title != null){
		    shell.setText(title);
		}
		
		int width = (Integer) self.doAction("getWidth", actionContext);
		int height = (Integer) self.doAction("getHeight", actionContext);
		if(width != -1 && height != -1){
			shell.setSize(width, height);
		}else{
			shell.pack();
		}

		//打开窗口
		shell.setVisible(true);
	}
}