package xworker.lang.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.MapData;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.xwidgets.SelectContent;
import xworker.util.XWorkerUtils;

public class CommandExecutor extends MapData{
	private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
	
	/** 命令域。 */
	Thing domain;
	
	/** 动作上下文，包含窗口等的动作上下文 */
	ActionContext actionContext;
	ActionContext domainContext;
	
	/** 当前命令  */
	Command currentCmd;
	
	/** 根命令 */
	Command rootCmd;
	
	public CommandExecutor(Thing domain, ActionContext actionContext, ActionContext domainContext){
		this.domain = domain;
		this.actionContext = actionContext;
		this.domainContext = domainContext;
		
		domain.doAction("init", domainContext, "executor", this);
	}
	
	/**
	 * 获取可选择的内容。
	 * 
	 * @param text
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SelectContent> getContents(String text){
		text =  text.toLowerCase();
		if(rootCmd == null || currentCmd.getCommandThing() == null){
			List<Thing> list = domain.doAction("getCommands", actionContext);
			List<SelectContent> cmds = new ArrayList<SelectContent>();
			
			//根据text过滤
			for(Thing  child : list) {
				String label = child.getMetadata().getLabel();
				String name = child.getMetadata().getName();
				name = name + "(" + label + ")";
				
				if(name.toLowerCase().indexOf(text) != -1) {
					SelectContent content = new SelectContent(name, child);
					cmds.add(content);
				}
			}
			
			Collections.sort(cmds);
			return cmds;			
		}else{
			List<SelectContent> contents =  (List<SelectContent>) currentCmd.run("getContents", domainContext, UtilMap.toMap("command", currentCmd, "text", text));
					/*
					.getCommandThing()
					.doAction("getContents", domainContext,	UtilMap.toMap("command", currentCmd, "text", text));*/			
			if(contents == null || contents.size() == 0){
				//checkStatus();
				return Collections.emptyList();
			}else{
				//过滤条件
				if(text != null || !"".equals(text)) {
					text = text.toLowerCase();
					List<SelectContent> list = new ArrayList<SelectContent>();
					for(SelectContent sc : contents) {
						if((sc.label != null && sc.label.toLowerCase().indexOf(text) != -1) || 
								(sc.value != null && sc.value.toLowerCase().indexOf(text) != -1)) {
							list.add(sc);
						}
					}
					
					return list;
				}else {
					return contents;
				}
			}
		}
	}
	
	public void setTip(String tip){
		Text tipText = actionContext.getObject("tipText");
		if(tipText != null){
			tipText.setText(tip == null ? "" : tip);
			tipText.getParent().update();
		}
	}
	
	public void select(SelectContent content, String text){		
		setTip(null);
		
		if(rootCmd == null){
			if(content == null){
				setTip("请选择一个命令!");
				return;
			}
			
			Thing cmdThing = (Thing) content.object;
			rootCmd = new Command(this, cmdThing, null);
			currentCmd = rootCmd;
			if(currentCmd.getCommandThing() != null){
				showHtml(currentCmd.getCommandThing());
			}
			refresh(currentCmd);
			if(cmdThing.getBoolean("hasContents")){
				doSearch("");
			}else{
				checkStatus();
			}
		}else if(currentCmd.getCommandThing() == null || currentCmd.getCommandThing().getBoolean("commandContent")){
			if(content != null){
				Thing cmdThing = (Thing) content.object;
				currentCmd.setCommandThing(cmdThing);
				refresh(currentCmd);
				
				if(cmdThing.getBoolean("hasContents")){
					doSearch("");
				}else{
					checkStatus();
				}
			}
		}else{
			if(content != null){
				currentCmd.setResult(content.object == null ? content.value : content.object);
			}else{
				currentCmd.setResult(text);
			}
			refresh(currentCmd);
			
			checkStatus();
		}
		
		//if(rootCmd != null) {
		//	setTip(rootCmd.toString());
		//}
	}
	
	/**
	 * 刷新树。
	 * 
	 * @param command
	 */
	public void refresh(Command command){
		Tree tree = (Tree) actionContext.get("tree");
		
		if(tree.getItemCount() == 0){
			//树还没有初始化
			createCommandTreeItem(tree, rootCmd);
		}else{
			//查找command对应的树节点
			TreeItem item = findTreeItem(tree.getItems()[0], command);
			updateTreeItem(item, command);
		}
	}
	
	public void updateTreeItem(TreeItem item, Command command){
		//先移除子节点
		item.removeAll();
		
		//初始化本节点，以及子节点
		initTreeItem(item, command);
	}
	
	public TreeItem findTreeItem(TreeItem item, Command command){
		if(item.getData() == command){
			return item;
		}else{
			for(TreeItem child : item.getItems()){
				TreeItem r = findTreeItem(child, command);
				if(r != null){
					return r;
				}
			}
		}
		
		return null;
	}
	
	public void createCommandTreeItem(Object parentItem, Command command){
		TreeItem item = null;
		if(parentItem instanceof Tree){
			item = new TreeItem((Tree) parentItem, SWT.None);
		}else{
			item = new TreeItem((TreeItem) parentItem, SWT.None);
			((TreeItem) parentItem).setExpanded(true);
		}
		
		item.setData(command);
				
		initTreeItem(item, command);
	}
	
	private void initTreeItem(TreeItem item, Command command){
		String label = null;
		if(command.getParamThing() != null) {
			label = command.getParamThing().getMetadata().getName() + ": ";
			
			if(command.isExecuted()) {
				label = label + String.valueOf(command.getResult());
			}else {
				if(command.getCommandThing() != null) {
					label = label + command.getCommandThing().getMetadata().getLabel() + "()";
				}else {
					label = label + "Undefined";
				}
			}
		}else {
			label = command.getCommandThing().getMetadata().getName() + "(" + command.getCommandThing().getMetadata().getLabel() + ")";
		}
				
		item.setText(label);
		if(!command.isReady()){
			item.setBackground(item.getParent().getDisplay().getSystemColor(SWT.COLOR_RED));
		}else{
			TreeItem tempItem = new TreeItem(item.getParent(), SWT.None);
			item.setBackground(tempItem.getBackground());
			tempItem.dispose();
		}
		
		for(Command child : command.getParams()){
			createCommandTreeItem(item, child);
		}
	}
	
	/**
	 * 检查命令的状态，如果有未准备好的命令这准备，否则将执行整个命令，并且清除命令重新来过。
	 */
	public void checkStatus(){
		Tree tree = (Tree) actionContext.get("tree");
		if(tree.getItemCount() == 0){
			doSearch("");
		}else{
			for(TreeItem item : tree.getItems()){
				if(checkStatus(tree, item) == false){
					return;
				}
			}
			
			//检查通过，执行
			try{
				if(!rootCmd.isExecuted()) {
					rootCmd.run(domainContext);
				}
				
				//重新来过
				if(rootCmd.isExecuted()) {
					reset();
				}
			}catch(Exception e){
				logger.error("execute command error", e);
			}
		}
	}
	
	public boolean checkStatus(Tree tree, TreeItem item){
		if(item.getItems().length > 0){
			for(TreeItem childItem : item.getItems()){
				if(!checkStatus(tree, childItem)){
					return false;
				}
			}
		}
		
		Command command = (Command) item.getData();
		if(command.isReady()){
			
			for(TreeItem child : item.getItems()){
				if(checkStatus(tree, child) == false){
					return false;
				}
			}
			
			return true;
		}else{
			tree.setSelection(item);
			currentCmd = command;
			if(currentCmd.getCommandThing() != null && currentCmd.getCommandThing().getBoolean("hasContents")){				
				doSearch("");
			}
			if(currentCmd.getCommandThing() != null){
				showHtml(currentCmd.getCommandThing());
			}
			return false;
		}
	}
	
	public void showHtml(Thing thing){
		//Browser browser = (Browser) actionContext.get("browser");
		//browser.setUrl(XWorkerUtils.getThingDescUrl(thing));
	}
	
	public void doSearch(String text){
		ActionContainer ac = (ActionContainer) actionContext.get("commandSelector");
		ac.doAction("setText", UtilMap.toMap("text", text));
	}
	
	public Shell getShell(){
		Tree tree = (Tree) actionContext.get("tree");
		if(tree == null || tree.isDisposed()){
			return null;
		}else{
			return tree.getShell();
		}
	}
		
	public Thing getDomain() {
		return domain;
	}

	public static CommandExecutor domainRun(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Shell parentShell = UtilData.getObjectByType(self, "parentShell", Shell.class, actionContext);
		if(parentShell == null){
			parentShell = (Shell) XWorkerUtils.getIDEShell();
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", parentShell);		
		Thing shellThing = World.getInstance().getThing("xworker.lang.command.CommandExecutor");
		Shell shell = (Shell) shellThing.doAction("create", ac);
		shell.setText(self.getMetadata().getLabel());
		
		CommandExecutor executor = new CommandExecutor(self, ac, actionContext);
		ac.put("executor", executor);
		executor.doSearch("");
		shell.open();
		
		return executor;
	}
	
	
	public void reset(){
		Tree tree = (Tree) actionContext.get("tree");
		if(tree == null || tree.isDisposed()) {
			return;
		}
		
		tree.removeAll();
		currentCmd = null;
		rootCmd = null;
		doSearch("");
		
		String label = domain.getMetadata().getLabel();
		Command parentCommand = actionContext.getObject("parentCommand");
		while(parentCommand != null) {
			CommandExecutor parentExecutor = parentCommand.getExecutor();
			label = parentExecutor.domain.getMetadata().getLabel() + " -> " + label;
			parentCommand = parentExecutor.getActionContext().getObject("parentCommand");
		}
		
		//setTip(label);
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public ActionContext getDomainContext() {
		return domainContext;
	}

	public Command getCurrentCmd() {
		return currentCmd;
	}

	public Command getRootCmd() {
		return rootCmd;
	}

	public void setDomain(Thing domain, ActionContext domainContext) {
		this.domain = domain;
		this.domainContext = domainContext;
		
		domain.doAction("init", actionContext, "executor", this);
	}
}
