package xworker.lang.actionflow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.util.XWorkerUtils;

public class ActionFlowRunner {
	ActionContext actionFlowContext;	
	
	List<ActionInstance> actions = new ArrayList<ActionInstance>();
	Shell shell;
	
	public ActionFlowRunner(ActionContext actionContext){
		//ActionFlow使用自己的变量上下文
		actionFlowContext = new ActionContext();
		actionFlowContext.put("parent", XWorkerUtils.getIDEShell());
		actionFlowContext.put("parentContext", actionContext);
		actionFlowContext.put("parentActionContext", actionContext);
		actionFlowContext.put("actionFlow", this);
	}
	
	public static ActionFlowRunner getActionFlowRunner(ActionContext actionContext){
		ActionFlowRunner runner = (ActionFlowRunner) actionContext.get("actionFlow");
		if(runner == null){
			throw new ActionException("Not a ActionFlow Context, actionFlow is null");			
		}
		
		return runner;
	}
	
	public void showRejectMessage(String message){		
	}
	
	public void showInsOnTable(ActionInstance ins){
		initUI();
		
		Table table = (Table) actionFlowContext.get("flowTable");
		
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(ins.getActionThing().getMetadata().getLabel());
		item.setData(ins);
	}
	
	/**
	 * 从已执行过的节点重新执行。
	 * 
	 * @param index
	 */
	public void runAtion(int index){
		if(index >= actions.size() || index < 0){
			return;
		}
		
		Table table = (Table) actionFlowContext.get("flowTable");
		while(actions.size() - 1 > index){
			table.remove(actions.size() - 1);
			actions.remove(actions.size() - 1);
		}
		
		ActionInstance ins = actions.get(index);
		ins.getActionThing().doAction("run", actionFlowContext);
	}
	
	public void addAction(Thing actionThing){
		Object it = null;
		if(actions.size() > 0){
			it = actions.get(actions.size() - 1).getResult();
		}
		
		String rejectMessage = (String) actionThing.doAction("accept", actionFlowContext, UtilMap.toMap("it", it)); 
		if(rejectMessage == null){
			ActionInstance ins = new ActionInstance(actionThing);
			actions.add(ins);
			showInsOnTable(ins);
			
			actionThing.doAction("run", actionFlowContext);
		}else{
			showRejectMessage(rejectMessage);
		}
	}
	
	public void actionFinished(Thing actionThing, Object result){
		ActionInstance ins = actions.get(actions.size() - 1);
		ins.setResult(result);
		
		selectNextActions(actionThing);
	}
	
	/**
	 * 选择下一个动作。
	 */
	@SuppressWarnings({ "unchecked" })
	public void selectNextActions(Thing actionThing){
		List<Thing> things = (List<Thing>) actionThing.doAction("getNextActions", actionFlowContext);
		
		initUI();
		
		Table selectTable = (Table) actionFlowContext.get("selectTable");
		selectTable.removeAll();
		for(Thing thing : things){
			TableItem item = new TableItem(selectTable, SWT.None);
			item.setText(thing.getMetadata().getLabel());
			item.setData(thing);
		}		
		
		//显示请求面板
		Composite contentComposite = (Composite) actionFlowContext.get("contentComposite");
		StackLayout contentCompositeStackLayout = (StackLayout) actionFlowContext.get("contentCompositeStackLayout");
		contentCompositeStackLayout.topControl = (Composite) actionFlowContext.get("actionSelectorComposite");
		contentComposite.layout();
		shell.setVisible(true);
	}
	
	public void put(String key , Object value){
		actionFlowContext.getScope(0).put(key, value);
	}
	
	public Object get(String key){
		return actionFlowContext.get(key);
	}
	
	public void initUI(){
		if(shell == null || shell.isDisposed()){
			Thing shellThing = World.getInstance().getThing("xworker.ui.actionflow.ActionFlowRunnerShell");
			shellThing.doAction("create", actionFlowContext);
			shell = (Shell) actionFlowContext.get("shell");
		}
	}
	
	public Composite getRequestUI(){
		initUI();
			
		//清空请求的面板
		Composite composite = (Composite) actionFlowContext.get("requestComposite");
		if(composite != null){
			for(Control c : composite.getChildren()){
				c.dispose();
			}
		}
		
		//显示请求面板
		Composite contentComposite = (Composite) actionFlowContext.get("contentComposite");
		StackLayout contentCompositeStackLayout = (StackLayout) actionFlowContext.get("contentCompositeStackLayout");
		contentCompositeStackLayout.topControl = composite;
		contentComposite.layout();
		
		shell.setVisible(true);
		return composite;
	}
}
