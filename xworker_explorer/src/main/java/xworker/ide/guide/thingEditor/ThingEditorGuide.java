package xworker.ide.guide.thingEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;

public class ThingEditorGuide {
	Shell shell;
	ActionContext actionContext;
	ActionContext guideContext;
	Thing thing;
	ThingEditorGuide subGuide;
	ThingEditorGuide parent;
	int index = 0;
	
	public ThingEditorGuide(Thing thing, Shell shell, ActionContext actionContext){
		this.shell = shell;
		this.actionContext = actionContext;
		this.thing = thing;
		
		guideContext = (ActionContext) actionContext.get("editorContext");
	}
	
	public ThingEditorGuide(ThingEditorGuide parent, Thing thing){
		this.shell = parent.shell;
		this.actionContext = parent.actionContext;
		this.thing = thing;		
		this.parent = parent;
		this.parent.subGuide = this;
	}
	
	/**
	 * 执行下一步向导。
	 */
	public boolean next(){
		if(subGuide != null){
			if(subGuide.next()){
				return true;
			}
		}
		
		if(index >= thing.getChilds().size()){
			if(parent != null){
				return false;
			}else{
				//向导已经结束
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				box.setText("向导");
				box.setMessage("向导已执行完毕，即将关闭！");
				box.open();
				
				shell.dispose();
				return false;
			}
		}else{			
			Thing guideThing = thing.getChilds().get(index);
			
			//创建按钮
			List<Thing> buttons = new ArrayList<Thing>();
			Thing bthings = guideThing.getThing("Buttons@0");
			if(bthings != null){
				for(Thing child : bthings.getChilds()){
					buttons.add(child);
				}
			}
			//添加nextButton
			if(getRoot().hasNext()){
				buttons.add(World.getInstance().getThing("xworker.ide.guide.thingeditor.NextButton"));
			}
			
			//叙述人的动作集，参看：xworker.ide.guide.narrator.DefaultNarrator
			ActionContainer actions = (ActionContainer) actionContext.get("actions");
			//显示当前事物的描述做为文档
			actions.doAction("setThingDesc", actionContext, UtilMap.toMap("narrator", guideThing.getString("narrator"), 
					"thing", guideThing));			
			boolean closeButton = guideThing.getBoolean("closeable");
			if(!getRoot().hasNext()){
				closeButton = true;
			}
			
			//创建按钮
			actions.doAction("setButtons", actionContext, UtilMap.toMap("buttonThings", buttons, "closeButton", closeButton));
			
			//执行任务
			guideThing.doAction("run", actionContext, UtilMap.toMap("guide", this));
						
			index++;
			
			//判断是否自动执行下一步
			if(guideThing.getBoolean("autoNext")){
				if(this.next()){
					return true;
				}else{
					return false;
				}
			}
			
			return true;
		}
	}
		
	public boolean hasNext(){
		if(subGuide != null){
			if(subGuide.hasNext()){
				return true;
			}
		}
		
		if(index >= thing.getChilds().size() - 1){
			return false;
		}else{
			return true;
		}
	}
	
	public ThingEditorGuide getRoot(){
		ThingEditorGuide p = this;
		while(p.parent != null){
			p = p.parent;
		}
		
		return p;
	}
}
