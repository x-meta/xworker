package xworker.lang.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.MapData;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.xwidgets.SelectContent;
import xworker.util.XWorkerUtils;

public class CommandExecutor extends MapData{
	//private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
	//private static final String TAG = CommandExecutor.class.getName();
	
	Stack<CommandDomain>  domainStack = new Stack<>();
	
	/** 动作上下文，包含窗口等的动作上下文 */
	ActionContext actionContext;
	
	/** 当前命令  */
	Command currentCmd;
	
	/** 根命令 */
	Command rootCmd;
	
	public CommandExecutor(Thing domainThing, ActionContext actionContext, ActionContext domainContext){
		this.actionContext = actionContext;
		
		
		CommandDomain domain = new CommandDomain(this, domainThing, domainContext);
		pushDomain(domain);
	}
	
	public void pushDomain(Thing domainThing, ActionContext actionContext, Map<String, Object> params) {
		if(params != null) {
			actionContext.peek().putAll(params);
		}
		CommandDomain domain = new CommandDomain(this, domainThing, actionContext);
		domain.setParams(params);
		
		pushDomain(domain);
	}
	
	private void pushDomain(CommandDomain domain) {
		if(domainStack.size() > 0) {
			domain.setParentDomain(domainStack.peek());
		}
		domainStack.push(domain);
		
		reset();
	}
		
	public void popDomain() {
		//最后一层Domain不能Pop
		if(domainStack.size() > 1) {		
			domainStack.pop();
			
			reset();
		}
	}
	
	public Thing getDomainThing() {
		return domainStack.peek().getThing();
	}
	
	public ActionContext getDomainActionContext() {
		return domainStack.peek().getActionContext();
	}
	
	/**
	 * 获取可选择的内容。
	 */
	@SuppressWarnings("unchecked")
	public List<SelectContent> getContents(String text){
		text =  text.toLowerCase();
		if(rootCmd == null || currentCmd.getCommandThing() == null){
			CommandDomain domain = this.getDomain();
			List<Thing> list = domain.getCommands();
			List<SelectContent> cmds = new ArrayList<>();
			
			String[] texts = text.split("[ ]");
			for(int i=0; i<texts.length; i++) {
				texts[i] = texts[i].trim();
			}
						
			//根据text过滤
			for(Thing  child : list) {
				String label = child.getMetadata().getLabel();
				String name = child.getMetadata().getName();
				name = name + " (" + label + ")";
				name = name.toLowerCase();
				
				boolean ok = true;
				for(String txt : texts) {
					if(name.contains(txt)) {
						continue;
					}
					
					ok = false;
					break;
				}
				if(ok) {
					SelectContent content = new SelectContent(name, child);
					cmds.add(content);
				}
			}
			
			//测试大量数据的情况
			/*int count = 1000000;
			for(int i=0; i<count; i++) {
				SelectContent content = new SelectContent("test " + System.currentTimeMillis(), "test");
				cmds.add(content);
			}*/
			Collections.sort(cmds);
			return cmds;			
		}else{
			Map<String, Object> params = UtilMap.toMap("command", currentCmd, "text", text);
			params.putAll(domainStack.peek().getParams());
			List<SelectContent> contents =  (List<SelectContent>) currentCmd.run("getContents", 
					getDomainActionContext(), params);
					/*
					.getCommandThing()
					.doAction("getContents", domainContext,	UtilMap.toMap("command", currentCmd, "text", text));*/			
			if(contents == null || contents.size() == 0){
				//checkStatus();
				return Collections.emptyList();
			}else{
				//过滤条件
				if(!"".equals(text)) {
					text = text.toLowerCase();
					List<SelectContent> list = new ArrayList<>();
					for(SelectContent sc : contents) {
						if((sc.label != null && sc.label.toLowerCase().contains(text)) ||
								(sc.value != null && sc.value.toLowerCase().contains(text))) {
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
	
	public void setDomainPathLabel(String path) {
		if(path == null) {
			return;
		}
		
		Label domainPathLabel = actionContext.getObject("domainPathLabel");
		domainPathLabel.setText(path);
	}
	
	public void setTip(String tip){
		if(tip == null) {
			return;
		}
		
		Label tipLabel = actionContext.getObject("tipLabel");
		tipLabel.setText(tip);
		tipLabel.getParent().layout();
		
		//功能目前有些鸡肋，暂时取消了
		
		/*
		Text tipText = actionContext.getObject("tipText");
		if(tipText != null){
			tipText.setText(tip == null ? "" : tip);
			tipText.getParent().update();
		}*/
	}
	
	/**
	 * 设置执行器中的浏览器中的网页地址。
	 */
	public void setUrl(String url) {
		Browser browser = actionContext.getObject("browser");
		if(url != null) {
			browser.setUrl(url);
		}		
	}
	
	/**
	 * 设置执行器中的浏览器中网页内容。
	 */
	public void setHtml(String html) {
		Browser browser = actionContext.getObject("browser");
		if(html != null) {
			browser.setText(html);
		}
	}
	
	public void select(SelectContent content, String text){		
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
				currentCmd.setContent(content.object == null ? content.value : content.object);
			}else{
				currentCmd.setContent(text);
			}
			if(currentCmd != null && !currentCmd.isExecuted()) {
				refresh(currentCmd);
			}
			
			checkStatus();
		}
	}
	
	public void setCurrentCommandThing(Thing commandThing) {
		if(currentCmd == null) {
			return;
		}
		
		currentCmd.setCommandThing(commandThing);
		refresh(currentCmd);
		
		if(commandThing.getBoolean("hasContents")){
			doSearch("");
		}else{
			checkStatus();
		}
	}
	
	/**
	 * 刷新树。
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
		TreeItem item;
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
		String label;
		if(command.getParamThing() != null) {
			label = command.getParamThing().getMetadata().getName() + ": ";
			
			if(command.isExecuted()) {
				label = label + command.getResult();
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
		if(!command.isExecuted()){
			item.setBackground(item.getParent().getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}else{
			item.setBackground(item.getParent().getBackground());
			//TreeItem tempItem = new TreeItem(item.getParent(), SWT.None);
			//item.setBackground(tempItem.getBackground());
			//tempItem.dispose();
		}
		
		for(Command child : command.getParams()){
			createCommandTreeItem(item, child);
		}
	}
	
	/**
	 * 检查命令的状态，如果有未准备好的命令这准备，否则将执行整个命令，并且清除命令重新来过。
	 */
	public void checkStatus(){
		//清空提示
		setTip("");
		
		//切换到查询界面
		StackLayout mainStackLayout = actionContext.getObject("mainStackLayout");
		Composite commandComposite = actionContext.getObject("commandComposite");
		Composite mainComposite = actionContext.getObject("mainComposite");
		
		mainStackLayout.topControl = commandComposite;
		mainComposite.layout();
				
		Tree tree = (Tree) actionContext.get("tree");
		if(tree.getItemCount() == 0){
			doSearch("");
		}else{
			for(TreeItem item : tree.getItems()){
				if(!checkStatus(tree, item)){
					return;
				}
			}
			
			//检查通过，执行
			try{
				if(!rootCmd.isExecuted()) {
					rootCmd.run(getDomainActionContext());
				}
				
				//重新来过
				if(rootCmd != null && rootCmd.isExecuted()) {
					reset();
				}
			}catch(Exception e){
				String html = "<pre>" + ExceptionUtil.toString(e) + "</pre>";
				setHtml(html);
				//Executor.error(TAG, "execute command error", e);
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
		initTreeItem(item, command);
		if(command.isReady()){		
			for(TreeItem child : item.getItems()){
				if(!checkStatus(tree, child)){
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
		
	public CommandDomain getDomain() {
		return domainStack.peek();
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
		Shell shell = shellThing.doAction("create", ac);
		shell.setText(self.getMetadata().getLabel());
		
		CommandExecutor executor = new CommandExecutor(self, ac, actionContext);
		ac.put("executor", executor);
		executor.doSearch("");
		shell.open();
		
		return executor;
	}
	
	
	public void reset(){
		//切换到查询界面
		StackLayout mainStackLayout = actionContext.getObject("mainStackLayout");
		Composite commandComposite = actionContext.getObject("commandComposite");
		Composite mainComposite = actionContext.getObject("mainComposite");
		
		mainStackLayout.topControl = commandComposite;
		mainComposite.layout();
		
		Tree tree = (Tree) actionContext.get("tree");
		if(tree == null || tree.isDisposed()) {
			return;
		}
		
		tree.removeAll();
		currentCmd = null;
		rootCmd = null;
		doSearch("");
		
		
		StringBuilder domainPath = null;
		for(CommandDomain domain : domainStack) {
			if(domainPath == null) {
				domainPath = new StringBuilder(domain.getLabel());
			}else {
				domainPath.append(" / ").append(domain.getLabel());
			}
		}
		
		Button editDomainButton = actionContext.getObject("editDomainButton");
		if(domainPath == null) {
			domainPath = new StringBuilder(UtilString.getString("lang:d=未设置命令域&en=Command domain not setted", actionContext));
			if(editDomainButton != null && !editDomainButton.isDisposed()) {
				editDomainButton.setEnabled(false);
			}
		}else {
			if(editDomainButton != null && !editDomainButton.isDisposed()) {
				editDomainButton.setEnabled(true);
			}
		}
		
		setDomainPathLabel(domainPath.toString());
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public ActionContext getDomainContext() {
		return domainStack.peek().getActionContext();
	}

	public Command getCurrentCmd() {
		return currentCmd;
	}

	public Command getRootCmd() {
		return rootCmd;
	}
}
