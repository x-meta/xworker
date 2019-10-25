package xworker.swt.xworker;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.util.SwtUtils;
import xworker.util.IIde;

/**
 * 单个SWT应用的IDE实现，使普通的SWT应用在非XWorker事物管理器下也可以正常使用XWorker的部分功能。
 * 
 * @author zyx
 *
 */
public class SwtAppIde implements IIde, Listener{
	private static Logger logger = LoggerFactory.getLogger(SwtAppIde.class);
	
	Thing thing;
	Shell shell;
	ActionContext actionContext;	
	xworker.lang.actions.ActionContainer actionContainer;
	//编辑器相关
	Shell editorShell;
	ActionContext editorContext;
	ActionContainer editorActionContainer;
	
	public SwtAppIde(Thing thing, Shell shell,  ActionContext actionContext){
		this.thing = thing;
		this.shell = shell;
		this.actionContext = actionContext;
		actionContainer = new ActionContainer(thing, actionContext);
		
		//增加键盘事件的过滤器，如实现快捷点打开帮助小精灵
		shell.getDisplay().addFilter(SWT.KeyDown, this);
	}
	
	@Override
	public void ideOpenFile(File file) {
		xworker.lang.actions.ActionContainer ac = getActionContainer();
		ac.doAction("openTextFile", ac.getActionContext(),  "file", file, "ide", this);
	}

	@Override
	public void ideOpenThing(Thing thing) {
		xworker.lang.actions.ActionContainer ac = getActionContainer();
		ac.doAction("openThing", ac.getActionContext(),  "thing", thing, "ide", this);
	}

	@Override
	public void ideOpenThingAndSelectCodeLine(final Thing thing, final String codeAttrName, final int line) {
		shell.getDisplay().asyncExec(new Runnable(){
			public void run(){
				xworker.lang.actions.ActionContainer  actions =  getActionContainer();
				ActionContext ac = actions.getActionContext();
				Shell shell = (Shell) ac.get("shell");
				if(shell != null){
					shell.forceActive();
				}
				ActionContext bin = (ActionContext)actions.doAction("openThing", UtilMap.toMap(new Object[]{"thing", thing}));
				
				if(bin != null){
					ActionContext modelBin = (ActionContext) bin.get("currentModelContext");
					StyledText input = (StyledText) modelBin.get(codeAttrName + "Input");
	                if(input != null){
	                	try{
		                	int start = input.getOffsetAtLine(line);
		                	int end = start + input.getLine(line).length();
	                        input.setCaretOffset(start);
	                        input.setSelection(start, end);
	                        input.showSelection();
	                	}catch(Exception e){
	                		
	                	}
	                }
				}
			}
		});
	}

	@Override
	public void ideDoAction(String name, Map<String, Object> parameters) {
		xworker.lang.actions.ActionContainer ac = getActionContainer();
		ac.doAction(name, ac.getActionContext(), parameters);		
	}

	@Override
	public Object getIDEShell() {
		return shell;
	}

	@Override
	public void ideShowMessageBox(final String title, final String message, final int style) {
		shell.getDisplay().asyncExec(new Runnable(){
			public void run(){
				MessageBox box = new MessageBox(shell, style);
				box.setText(title);
				box.setMessage(message);
				SwtUtils.openDialog(box, null, actionContext);
				//box.open();
			}
		});
		
	}

	@Override
	public boolean isThingExplorer() {
		return false;
	}

	@Override
	public xworker.lang.actions.ActionContainer getActionContainer() {
		if(thing.getBoolean("isIde")){
			return actionContainer;
		}else{
			if(editorShell == null || editorShell.isDisposed()){
				editorContext = new ActionContext();
				editorContext.put("parent", shell);
				
				if(World.getInstance().getThing("xworker.ide.IDE") != null) {
					//在XWorker的环境下
					Thing simpleEditor = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
					editorShell = (Shell) simpleEditor.doAction("create", editorContext);
					editorActionContainer = new ActionContainer(simpleEditor, editorContext);				
					editorShell.setVisible(false);
				}
			}
						
			return editorActionContainer;
		}
	}

	@Override
	public ActionContext getActionContext() {
		return actionContext;
	}

	@Override
	public void handleEvent(Event event) {
		if(event.type == SWT.KeyDown){
			if((event.keyCode == 'h' || event.keyCode == 'H') && (event.stateMask & SWT.CTRL) == SWT.CTRL && (event.stateMask & SWT.ALT) == SWT.ALT){
				Action action = World.getInstance().getAction("xworker.ide.worldExplorer.swt.SimpleExplorer/@shell1/@mainComposite/@mainCoolBar1/@mainCollItem/@mainToolBar/@commandAssistorItem/@listeners/@openCommander/@openAssistor");
				action.run(actionContext);
			}
		}
	}

	@Override
	public boolean isDisposed() {
		return shell == null || shell.isDisposed();
	}
}
