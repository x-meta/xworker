package xworker.swt.guide;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

public class Guide {
	Stack<GuideEntry> guideStack = new Stack<>();
	///** 向导展示过的历史节点 */
	//List<Thing> historyNodes = new ArrayList<Thing>();
	/** 父变量上下文 */
	ActionContext parentContext;
	/** 当前节点变量上下文 */
	ActionContext nodeActionContext = null;
	/** 内容面板 */ 
	Composite contentComposite;
	/** 按钮面板 */
	Composite buttonComposite;
	/** 是否显示标题 */
	boolean showHeader = false;
	/** 标题的标签 */
	Label headerLabel = null;	
	Map<String, Object> datas = new HashMap<>();
	
	public Guide(Composite contentComposite, Composite buttonComposite, Thing thing, ActionContext parentContext){
		guideStack.push(new GuideEntry(thing));
		this.parentContext = parentContext;
		this.contentComposite = contentComposite;		
		this.buttonComposite = buttonComposite;
		this.showHeader = thing.doAction("isShowHeader", parentContext);
		headerLabel = parentContext.getObject("headerLabel");
		if(!showHeader && headerLabel != null) {
			headerLabel.dispose();
		}
	}
	
	/**
	 * 跳转到指定节点。
	 * 
	 * @param node
	 */
	public void go(Thing node){
		guideStack.peek().setCurrentNode(node);
		showCurrentNode();
	}
	
	/**
	 * 设置一个新的向导模型，替换当前层级的向导。
	 * 
	 * @param thing
	 */
	public void setGuide(Thing thing){
		guideStack.pop();
		guideStack.push(new GuideEntry(thing));
		next();
	}
	
	/**
	 * 调用一个新的向导模型。
	 * 
	 * @param thing
	 */
	public void callGuide(Thing thing) {
		guideStack.push(new GuideEntry(thing));
		next();
	}
	
	/**
	 * 转到下一个节点。
	 */
	public void next(){		
		Thing currentNode = getCurrentNode();
		if(currentNode != null && nodeActionContext != null) {
			if(UtilData.isTrue(currentNode.doAction("nodeFinished", nodeActionContext))){
				return;
			}
		}
		
		Thing nextNode = null;
		while(guideStack.size() > 0) {
			nextNode = guideStack.peek().getNextNode(nodeActionContext);
			if(nextNode == null && guideStack.size() > 1) {
				guideStack.pop();
			} else {
				break;
			}
		}
		if(nextNode != null) {
			showCurrentNode();
		}
	}
	
	/**
	 * 转到上一个节点。
	 */
	public void pre() {
		Thing nextNode = guideStack.peek().getPreNode();
		if(nextNode == null && guideStack.size() > 1) {
			guideStack.pop();
			nextNode = guideStack.peek().getCurrentNode();
		}
		
		if(nextNode != null) {
			showCurrentNode();
		}
	}
	
	public Thing getCurrentNode() {
		return guideStack.peek().getCurrentNode();
	}
		
	protected void showCurrentNode(){
		Thing thing = guideStack.peek().getThing();
		Thing currentNode = guideStack.peek().getCurrentNode();
		
		//在XWorker的主页下，重置概要栏中的文档
		Browser topicBrowser = UtilData.getParentContextValue(parentContext, "topicBrowser");
		if(topicBrowser != null) {
			topicBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
		}
		
		for(Control control : contentComposite.getChildren()){
			control.dispose();
		}
		for(Control control : buttonComposite.getChildren()){
			control.dispose();
		}
				
		if(currentNode != null){
			nodeActionContext = new ActionContext();
			nodeActionContext.put("guide", this);
			nodeActionContext.put("parentContext", parentContext);
			nodeActionContext.put("parent", contentComposite);
			try {
				currentNode.doAction("create", nodeActionContext);
			}catch(Exception e) {
				MessageBox box = new MessageBox(buttonComposite.getShell(), SWT.ICON_ERROR | SWT.OK);
				box.setText("Guide");
				box.setMessage(e.getMessage());
				e.printStackTrace();
			}
			
			nodeActionContext.put("parent", buttonComposite);
			currentNode.doAction("createButtons", nodeActionContext);
			
			if(showHeader && headerLabel != null) {
				headerLabel.setText(currentNode.getMetadata().getLabel());
			}
			
			contentComposite.layout();
			buttonComposite.layout();
			buttonComposite.getParent().layout();
			
			currentNode.doAction("nodeInit", nodeActionContext);
		}
	}
	
	public void setData(String key, Object value) {
		datas.put(key, value);
	}
	
	public void putDatas(Map<String, Object> values) {
		datas.putAll(values);;
	}
	
	public Object getData(String key) {
		return datas.get(key);
	}
	
	public Map<String, Object> getDatas(){
		return datas;
	}

}
