package xworker.ui.guide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class GuideExecutor  {
	ActionContext actionContext;	
	
	List<Thing> guides = new ArrayList<Thing>();
	Shell shell;
	boolean shellSelfCreated = false;
	
	public GuideExecutor(ActionContext parentActionContext){
		//ActionFlow使用自己的变量上下文
		actionContext = new ActionContext();
		actionContext.put("parent", XWorkerUtils.getIDEShell());
		actionContext.put("parentContext", parentActionContext);
		actionContext.put("parentActionContext", parentActionContext);
		actionContext.put("guide", this);
		
		Thing shellThing = World.getInstance().getThing("xworker.ui.guide.GuideExecutor");
		shellThing.doAction("create", actionContext);
		shell = (Shell) actionContext.get("shell");		
		shell.setVisible(true);
		shellSelfCreated = true;
	}
	
	public GuideExecutor(ActionContext parentActionContext, Composite parent){
		//ActionFlow使用自己的变量上下文
		actionContext = new ActionContext();
		actionContext.put("parent", parent);
		actionContext.put("parentContext", parentActionContext);
		actionContext.put("parentActionContext", parentActionContext);
		actionContext.put("guide", this);
		
		Thing shellThing = World.getInstance().getThing("xworker.ui.guide.GuideExecutor/@mainSashForm");
		shellThing.doAction("create", actionContext);
		shell = parent.getShell();
	}
	
	public static GuideExecutor getGuideExecutor(ActionContext actionContext){
		GuideExecutor guide = (GuideExecutor) actionContext.get("guide");
		if(guide == null){
			throw new ActionException("Not a Guide Context, guide is null");			
		}
		
		return guide;
	}
	
	public void showRejectMessage(String message){	
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		box.setText("Guide");
		box.setMessage(message);
		box.open();
	}
	
	public void showInsOnTable(Thing thing){
		Table table = (Table) actionContext.get("guideTable");
		
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(thing.getMetadata().getLabel());
		item.setData(thing);
		
		table.setSelection(item);
		
		Browser guideBrowser = (Browser) actionContext.get("guideBrowser");
		guideBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
	}
	
	public void go(int index){
		runGuide(index);
	}
	
	/**
	 * 从已执行过的节点重新执行。
	 * 
	 * @param index
	 */
	public void runGuide(int index){
		if(index < 0){
			index = guides.size() + index - 1;
		}
		if(index >= guides.size() || index < 0){
			return;
		}
		
		Table table = (Table) actionContext.get("guideTable");
		while(guides.size() - 1 > index){
			table.remove(guides.size() - 1);
			guides.remove(guides.size() - 1);
		}
		
		Thing thing = guides.get(index);
		thing.doAction("doGuide", actionContext);
	}
	
	public void addGuide(Thing thing){
		if("GuideReference".equals(thing.getThingName())){
			thing = World.getInstance().getThing(thing.getString("guidePath"));
			if(thing == null){
				return;
			}
		}
		
		if(guides.size() == 0 && shellSelfCreated){
			shell.setText(thing.getMetadata().getLabel());
		}
		
		guides.add(thing);
		showInsOnTable(thing);
		
		thing.doAction("doGuide", actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public void guideFinished(Thing thing){
		List<Thing> things = (List<Thing>) thing.doAction("getNextGuides", actionContext);
		
		Table selectTable = (Table) actionContext.get("selectTable");
		selectTable.removeAll();
		
		if(things.size() == 1){
			this.addGuide(things.get(0));
			return;
		}else if(things.size() == 0){
			Composite endComposite = (Composite) actionContext.get("endComposite");
			
			Composite contentComposite = (Composite) actionContext.get("contentComposite");
			StackLayout contentCompositeStackLayout = (StackLayout) actionContext.get("contentCompositeStackLayout");
			contentCompositeStackLayout.topControl = endComposite;
			contentComposite.layout();
			return;
		}
		
		for(Thing nextGuideThing : things){
			TableItem item = new TableItem(selectTable, SWT.None);
			item.setText(nextGuideThing.getMetadata().getLabel());
			item.setData(nextGuideThing);
		}		
		selectTable.select(0);
		
		//显示请求面板
		Composite contentComposite = (Composite) actionContext.get("contentComposite");
		StackLayout contentCompositeStackLayout = (StackLayout) actionContext.get("contentCompositeStackLayout");
		contentCompositeStackLayout.topControl = (Composite) actionContext.get("actionSelectorComposite");
		contentComposite.layout();
	}
	
	public void put(String key , Object value){
		actionContext.getScope(0).put(key, value);
	}
	
	public Object get(String key){
		return actionContext.get(key);
	}
	
	public Composite getRequestUI(){
		//清空请求的面板
		Composite composite = (Composite) actionContext.get("requestComposite");
		if(composite != null){
			for(Control c : composite.getChildren()){
				c.dispose();
			}
		}
		
		//显示请求面板
		Composite contentComposite = (Composite) actionContext.get("contentComposite");
		StackLayout contentCompositeStackLayout = (StackLayout) actionContext.get("contentCompositeStackLayout");
		contentCompositeStackLayout.topControl = composite;
		contentComposite.layout();
		
		return composite;
	}	

	public ActionContext getActionContext() {
		return actionContext;
	}

}
