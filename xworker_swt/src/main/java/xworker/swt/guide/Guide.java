package xworker.swt.guide;

import java.util.ArrayList;
import java.util.List;

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
	/** 向导事物 */
	Thing thing;
	/** 当前节点 */
	Thing currentNode;
	/** 向导展示过的历史节点 */
	List<Thing> historyNodes = new ArrayList<Thing>();
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
	
	public Guide(Composite contentComposite, Composite buttonComposite, Thing thing, ActionContext parentContext){
		this.thing = thing;
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
		currentNode = node;
		historyNodes.add(currentNode);
		showCurrentNode();
	}
	
	public void setGuide(Thing thing){
		this.thing = thing;
		currentNode = null;
		next();
	}
	
	/**
	 * 转到下一个节点。
	 */
	public void next(){
		currentNode = getNextNode(currentNode);		
		historyNodes.add(currentNode);
		showCurrentNode();
	}
	
	/**
	 * 转到上一个节点。
	 */
	public void pre() {
		if(historyNodes.size() > 1) {
			historyNodes.remove(historyNodes.size() - 1);
			currentNode = historyNodes.get(historyNodes.size() - 1);
			showCurrentNode();
		}
	}
		
	protected void showCurrentNode(){
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
		}
	}
	
	public Thing getNextNode(Thing node){
		if(node == null){
			return thing.getChildAt(0);
		}else{
			Thing parentNode = node.getParent();
			if(parentNode == null){
				return null;
			}else{
				Thing nextNode = parentNode.getChildBy(currentNode, 1);
				if(nextNode != null){
					return nextNode;
				}else{
					return getNextNode(parentNode);
				}
			}
		}
	}
}
