package xworker.swt.xworker;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;

public class AddChildComposite {
	public static Composite create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//创建控件
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xworker.prototype.AddChildComposite/@addChildSashForm"));
		Composite composite = cc.create();
		
		//创建按钮
		ActionContext ac = cc.getNewActionContext();
		ac.put("controlThing", self);
		Thing buttons = self.getThing("Buttons@0");
		if(buttons != null) {
			actionContext.peek().put("parent", ac.get("childContentButtonComposite"));
			for(Thing button : buttons.getChilds()) {
				button.doAction("create", actionContext);
			}
		}
		
		//动作容器
		ActionContainer actions = ac.getObject("actions");
		Thing thing = self.doAction("getThing", actionContext);
		if(thing != null) {
			actions.doAction("setThing", ac, "thing", thing);
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), actions);
		
		return composite;
	}
	
	public static void setThing(ActionContext actionContext) {
		Thing thing = actionContext.getObject("thing");
		if(thing == null) {
			return;
		}
		
		actionContext.g().put("thing", thing);
		Combo structCombo= actionContext.getObject("structCombo");
		structCombo.removeAll();
		
		//设置描述者
		List<Thing> descriptors = thing.getDescriptors();
		for(Thing desc : descriptors){
		    structCombo.add(desc.getMetadata().getLabel());    
		}

		//默认选中第一个
		structCombo.setData(descriptors);
		actionContext.g().put("doInitChildTree", true);
		if(descriptors.size() > 0) {
			structCombo.select(0);
		}
		SwtListener descriptsComboSelection = actionContext.getObject("descriptsComboSelection");
		descriptsComboSelection.handleEvent(null);
	}
	
	public void addChildSelection(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("thing");
		
		//def selectIndex = childList.getSelectionIndex();
		Tree childTree = actionContext.getObject("childTree");
		Thing currentAddModel = actionContext.getObject("currentAddModel");
		ActionContext currentAddModelContext = actionContext.getObject("currentAddModelContext");
		
		Thing childStructure = actionContext.getObject("newThingDescriptor");
		if(childStructure == null){
		    childStructure = (Thing) childTree.getSelection()[0].getData();
		}
		Map<String, Object> childData = currentAddModel.doAction("getValue", currentAddModelContext);

		//创建和添加子节点
		Thing childObj = new Thing(null, null, childStructure.getMetadata().getPath(), false);
		childObj.cognize(childData);
		childObj.set("descriptors", childStructure.getMetadata().getPath());
		        
		//name属性默认为空
		if(childObj.getStringBlankAsNull("name") == null){
		    Thing desc = World.getInstance().getThing(childStructure.getMetadata().getPath());
		    if(desc != null){
		        childObj.put("name", desc.get("name"));
		    }
		}

		currentThing.addChild(childObj);
		childObj = currentThing.getChilds().get(currentThing.getChilds().size() - 1);

		//添加自动创建的子节点
		List<String> autoAddChilds = childStructure.doAction("ideGetAutoAddChilds", actionContext);
		if(autoAddChilds != null){
		    for(String childDesc : autoAddChilds){
		        Thing childThing = new Thing(null, null, childDesc, false);
		        childThing.initDefaultValue();
		        childObj.addChild(childThing);
		    }
		}
		
		//触发添加子节点的事件
		Thing controlThing = actionContext.getObject("controlThing");
		if(controlThing != null) {
			ActionContext parentContext = actionContext.getObject("parentContext");
			if(UtilData.isTrue(controlThing.doAction("isAutoSave", actionContext))) {
				currentThing.save();
			}
				
			controlThing.doAction("onAdd", parentContext, "thing", currentThing, "childThing", childObj);
		}
	}
}
