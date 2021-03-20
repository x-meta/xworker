package xworker.swt.xworker;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import ognl.OgnlException;
import xworker.java.assist.Javaassist;
import xworker.java.assist.ParameterInfo;
import xworker.lang.VariableDesc;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.util.CodeUtils;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.SelectContent;
import xworker.swt.xworker.codeassist.CodeHelper;
import xworker.swt.xworker.codeassist.VariableProvider;
import xworker.swt.xworker.codeassist.variableproviders.CachedVaribleProvider;

/**
 * 代码辅助。
 * 
 * @author Administrator
 *
 */
public class CodeAssistor implements KeyListener, DisposeListener, VariableProvider{
	//private static Logger logger = LoggerFactory.getLogger(CodeAssistor.class);
	private static final String TAG = CodeAssistor.class.getName();
	private static String key = "xworker.swt.xworker.CodeAssistor";
	private static String keyThing = "xworker.swt.xworker.CodeAssistor_Thing";
	
	
	public static final String CATEGORY_SERVLET = "servlet";
	public static final String CATEGORY_SWT = "swt";
	public static final String CATEGORY_ACTIONCONTEXT = "actionContext";
	

	ActionContext shellActionContext;
	Shell shell;
	Shell parentShell;
	DelayAction delayAction;
	String category;
	ActionContext actionContext;
	ActionContext varActionContext;
	//TextStyle paramTextStyle = new TextStyle();
	Thing thing;
	Map<String, VariableDesc> varCache = new HashMap<String, VariableDesc>();
	String textAssistor;
	
	public static void initCaches(String path, ActionContext actionContext){
		CachedVaribleProvider.instance.initCaches(path, actionContext);
	}
	
	public void initCache(ActionContext actionContext){
		if(actionContext == null){
			return;
		}
		
		CachedVaribleProvider.instance.initCaches(thing, actionContext);
		this.varActionContext = actionContext;
	}

	public static CodeAssistor clone(Control source, Control target){
		CodeAssistor codeAssistor = (CodeAssistor) source.getData(key + "Assistor");
		if(codeAssistor != null){
			Thing thing = (Thing) source.getData(keyThing);
			return attach(thing, target, codeAssistor.varActionContext);
		}else{
			return null;
		}
	}
	
	public static CodeAssistor attach(Thing thing, Control text, ActionContext actionContext){
		Shell shell = text.getShell();
		CodeAssistor codeAssistor = (CodeAssistor) text.getData(key + "Assistor");
		if(codeAssistor == null){
			codeAssistor = new CodeAssistor(shell);
			text.setData(key + "Assistor", codeAssistor);
			text.addKeyListener(codeAssistor);
			text.addDisposeListener(codeAssistor);
		}

		codeAssistor.initCache(actionContext);
		text.setData(key, actionContext);
		text.setData(keyThing, thing);
		codeAssistor.varActionContext = actionContext;
		codeAssistor.thing = thing;
		codeAssistor.initCache(actionContext);
		
		return codeAssistor;
	}
	
	public static CodeAssistor attach(Thing thing, Text text, ActionContext actionContext){
		Shell shell = text.getShell();		
		CodeAssistor codeAssistor = (CodeAssistor) text.getData(key + "Assistor");
		if(codeAssistor == null){
			codeAssistor = new CodeAssistor(shell);
			text.setData(key + "Assistor", codeAssistor);
			text.addKeyListener(codeAssistor);
		}

		codeAssistor.initCache(actionContext);
		text.setData(key, actionContext);
		text.setData(keyThing, thing);
		codeAssistor.varActionContext = actionContext;
		codeAssistor.thing = thing;
		codeAssistor.initCache(actionContext);
		
		return codeAssistor;
	}
	
	private CodeAssistor(Shell parentShell){
		this.parentShell = parentShell;
		parentShell.addDisposeListener(this);
				
		delayAction = new DelayAction(parentShell.getDisplay(), 200);
		//paramTextStyle.borderStyle = SWT.BORDER_SOLID;
		//paramTextStyle.background = parentShell.getDisplay().getSystemColor(SWT.COLOR_GRAY);
	}

	public List<CodeAssitContent> getHelpContents(Control text, ActionContext actionContext){	
		String str = SwtTextUtils.getText(text);
		int offset = SwtTextUtils.getCaretOffset(text);
		return CodeHelper.getHelpContents(textAssistor, str, offset, thing, actionContext);
	}
	
	public void putCache(Control text, String name, Class<?> cls, boolean addDesc){
		if(name == null || "".equals(name) || cls == null) {
			return;
		}
		
		//放到自己的缓存里
		varCache.put(name, VariableDesc.create(name, cls));
		
		//修改模型，加入持久的变量描述，只在事物编辑器里生效
		Thing thing = (Thing) text.getData(keyThing);
		
		if(thing != null && Designer.isAttribute(text) && addDesc) {
			Thing descs = thing.getThing("VariablesDescs@0");
			if(descs == null) {
				descs = new Thing("xworker.lang.MetaThing/@VariablesDesc");
				thing.addChild(descs);
			}
			
			boolean have = false;
			for(Thing desc : descs.getChilds()) {
				if("Variable".equals(desc.getThingName()) && name.equals(desc.getString("varName"))) {
					//修改已经有的
					desc.put("type", "object");
					desc.put("className", cls.getName());
					have = true;
					break;
				}
			}
			
			if(!have) {
				Thing desc = new Thing("xworker.lang.MetaThing/@VariablesDesc/@Object");
				desc.put("name", name);
				desc.put("varName", name);
				desc.put("type", "object");
				desc.put("className", cls.getName());
				descs.addChild(desc);
			}
		}
		
		//CachedVaribleProvider.instance.putCache(thing, name, cls);
		/*
		Map<String, Class<?>> cache = getCache(thing);
		if(cache != null){	
			cache.put(name, cls);
		}*/
	}
	
	
	public List<CodeAssitContent> getClassContents(Control text, ActionContext actionContext){
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		
		Thing thing = (Thing) text.getData(keyThing);
	
		List<String> statements = new ArrayList<String>();
		int index = SwtTextUtils.getCaretOffset(text);
		String code = SwtTextUtils.getText(text);
		parseFunctionCall(code, index - 1, index, statements, new Stack<char[]>());
		
		list = CodeHelper.getObjectContents(code, index, statements, thing, actionContext);
		/*
		if(cache != null){						
			try {
				CtClass ctClass = getClass(text, sentens, cache, this);
				if(ctClass != null){
					for(CtField field : ctClass.getFields()){
						if((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
							list.add(new CodeAssitContent(field.getName(), field.getName() + ": " + field.getType(), "fieldImage"));
						}
					}
					
					for(CtMethod method : ctClass.getMethods()){
						if((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
							List<ParameterInfo> params = Javaassist.getParameterInfo(method);
							String label = method.getName() + "(";
							String methodName = method.getName() + "(";
							for(int i=0; i<params.size(); i++){
								methodName = methodName + params.get(i).getName();
								String type = params.get(i).getType();
								int lastIndex = type.lastIndexOf(".");
								if(lastIndex != -1){
									type = type.substring(lastIndex + 1, type.length());
								}
								label = label + type + " " + params.get(i).getName();
								if(i < params.size() - 1){
									methodName = methodName + ", ";
									label = label + ", ";
								}
							}
							
							CtClass rcls = method.getReturnType();
							String rtype = "void";
							if(rcls != null){
								rtype = rcls.getSimpleName();
							}
							methodName = methodName + ")";
							label = label + "): " + rtype;
							list.add(new CodeAssitContent(methodName, label, "methodImage"));
						}
					}
				}
			} catch (NotFoundException e) {
				logger.warn("get class contents error", e);
			}				
		}
		
		//分析对象
		if(sentens.size() == 1){
			String name = sentens.get(0);
			if(name != null && !"".equals(name) && (this.actionContext != null || this.varActionContext != null)){
				Object obj = null;
				if(this.varActionContext != null) {
					obj = this.varActionContext.get(name);
				}
				if(obj == null && this.actionContext != null) {
					obj = this.actionContext.get(name);
				}
				if(obj instanceof Thing){
					addThingContents(list, (Thing) obj);
				}else if(obj instanceof ActionContainer){
					addActionContainerContents(list, (ActionContainer) obj);
				}
			}
		}
	
		Collections.sort(list);
			*/
		return list;		
	}
	
	/**
	 * 把事物的属性和动作加入到代码辅助内容中。
	 * 
	 * @param list
	 * @param thing
	 */
	public static void addThingContents(List<CodeAssitContent> list, Thing thing){
		//获取属性
		for(Thing attr : thing.getAllAttributesDescriptors()){
			String name = attr.getMetadata().getName();
			String label = attr.getMetadata().getLabel();
			String type = attr.getStringBlankAsNull("type");
			if(type == null){
				type = "string";
			}
			list.add(new CodeAssitContent("put(\"" + name + "\", value);", "put(\"" + name + "\", value):" + type + "(" + label + ")", "fieldImage"));
			list.add(new CodeAssitContent("get(\"" + name + "\", value);", "get(\"" + name + "\", value):" + type + "(" + label + ")", "fieldImage"));
		}
		
		//获取动作列表
		for(Thing ac : thing.getActionsThings()){
			list.add(getActionAssistString(ac));
		}
	}
	
	/**
	 * 返回动作的输入辅助字符串。
	 * 
	 * @param ac
	 * @return
	 */
	public static CodeAssitContent getActionAssistString(Thing ac){
		String name = ac.getMetadata().getName();
		String label = ac.getMetadata().getLabel();
		
		String m = "doAction(\"" + name + "\", actionContext";
		
		//参数列表
		Thing ins = ac.getThing("ins@0");
		if(ins != null){
			for(Thing p : ins.getChilds()){
				String pname = p.getMetadata().getName();
				m = m + ", \"" + pname + "\", " + pname;
			}
		}
		
		m = m + ");";
		
		return new CodeAssitContent(m, m + " - (" + label + ")", "methodImage");
	}
	
	/**
	 * 把一个ActionContainer的动作加入到代码辅助内容中。
	 * 
	 * @param list
	 * @param ac
	 */
	public static void addActionContainerContents(List<CodeAssitContent> list, ActionContainer ac){
		//获取动作列表		
		for(Thing acthing : ac.getActionThings()){
			list.add(getActionAssistString(acthing));
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		try {
			delayAction.cancel();
			
			final Control text = (Control) event.widget;
			final ActionContext actionContext = (ActionContext) text.getData(key);
			
			if((event.character == 'h' || event.character == 'H') && (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT)){
				//弹出所有词的列表
				String object = getCurentWord(text);			
				if(object != null && object.length() > 0){
					int index = SwtTextUtils.getCaretOffset(text);		
					SwtTextUtils.setSelection(text, index - object.length(), index);
				}
				setContents(object, text, getHelpContents(text, actionContext));
	
				event.doit = false;
			}else if((event.character == 't' || event.character == 'T') && (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT)){
				//弹出类型选择，手动设置变量的类型
				openSetTypeShell(text);
				event.doit = false;
			}else if((event.character == 'p' || event.character == 'P') && (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT)){
				//弹出import的选择器
				openImportShell(text);
				event.doit = false;
			}/*else if((event.keyCode == 'i' || event.keyCode == 'I') && event.stateMask == SWT.ALT){
				//弹出import的选择器
				openImportShell(text);
				event.doit = false;
			}*/else if(event.character == '.'){
				delayAction.setRunnable(new Runnable(){
					public void run(){
						try{						
							setContents("", text, getClassContents(text, actionContext));
						}catch(Exception e){
							Executor.error(TAG, "CodeAssistor error", e);
						}
					}
				});
			}
		}catch(Exception e) {
			Executor.error(TAG, "CodeAssistor error", e);
		}
	}
	
	public void openImportShell(Control text){
		//打开设置窗口
		Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.CodeAssistImporter");
		ActionContext ac = new ActionContext();
		ac.put("parent", text.getShell());
		ac.put("text", text);
		ac.put("assistor", this);		
		
		Shell shell = (Shell) shellThing.doAction("create", ac);		
		shell.open();
		//设置焦点
		((ActionContainer) ac.get("calssSelector")).doAction("setFocus", ac);
	}
	
	public void openSetTypeShell(Control text){
		//分析当前输入的词
		List<String> lists = new ArrayList<String>();
		int index = SwtTextUtils.getCaretOffset(text);
		String code = SwtTextUtils.getText(text);
		parseFunctionCall(code, index - 1, index, lists, new Stack<char[]>());
		
		//打开设置窗口
		Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.CodeAssistTypeSetter");
		ActionContext ac = new ActionContext();
		ac.put("parent", text.getParent());
		ac.put("text", text);
		ac.put("assistor", this);		
		
		Shell shell = (Shell) shellThing.doAction("create", ac);
		if(lists.size() > 0){
			((Text) ac.get("varText")).setText(lists.get(0));			
		}		
		shell.open();
		
		//设置焦点
		((ActionContainer) ac.get("calssSelector")).doAction("setFocus", ac);
	}
	
	public String getCurentWord(Control text){
		int index = SwtTextUtils.getCaretOffset(text);
		String str = SwtTextUtils.getText(text);
		String objectStr = "";
		for(int i = index - 1; i >= 0; i--){
			if(i < 0){
				break;
			}
			
			char c = str.charAt(i);
			if(c == '\n' || c == ' ' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == '.'){
				break;
			}
			
			objectStr = c + objectStr;
		}
		
		return objectStr;
	}	
	
	public void setContents(String filterText, Control text, List<CodeAssitContent> contents){
		initShell();	
		
		shellActionContext.getScope(0).put("text", text);
		shellActionContext.getScope(0).put("contents", contents);
		shellActionContext.getScope(0).put("text", text);
		
		//showContents("", contents);
		
		if(contents != null && contents.size() > 0){			
			Point pt = SwtTextUtils.getCaretLocation(text);
			if(pt != null) {
				pt = text.getParent().toDisplay(pt);
				shell.setLocation(pt);
			}else {
				//找不到Text的光标位置，默认显示在中间
				SwtUtils.centerShell(shell);
			}
			
			shell.setVisible(true);
			
			Text t = (Text) shellActionContext.get("filterText");
			if(t != null){
				if(filterText == null){
					filterText = "";
				}
				
				t.setText(filterText);
				t.setSelection(filterText.length());
				t.setFocus();
			}else{
				shell.setFocus();
			}
			//text.setFocus();
		}
	}
	
	public static void showContents(Table table, String filterText, List<CodeAssitContent> contents, ActionContext shellActionContext){	
		if(filterText == null){
			filterText = "";
		}
		
		table.removeAll();
		/*
		for(TableItem item : table.getItems()){
			item.dispose();
		}*/
		
		if(contents != null && contents.size() > 0){
			for(CodeAssitContent c : contents){
				if(c == null || c.value == null){
					continue;
				}
				
				String value = c.value.toLowerCase();
				boolean matched = true;
				String[] filters = filterText.split("[ ]");
				
				for(String filter : filters) {
					filter = filter.trim();
					if("".equals(filter)) {
						continue;
					}else if(value.indexOf(filter.toLowerCase()) == -1) {
						matched = false;
						break;
					}
				}
				if(matched){
					TableItem item = new TableItem(table, SWT.NONE);
					if(c.label != null){
						item.setText(new String[]{c.label});
					}else{
						item.setText(new String[]{c.value});
					}
					
					if(c.image != null && !"".equals(c.image)){
						Image image = (Image) shellActionContext.get(c.image);
						if(image != null){
							item.setImage(image);
						}
						
					}
					item.setData(c);
				}
			}
			
			if(contents.size() > 0){
				table.setSelection(0);				
			}			
		}
	}
	
	public void showContents(String filterText, List<CodeAssitContent> contents){		
		Table table = (Table) shellActionContext.get("table");
		showContents(table, filterText, contents, shellActionContext);
	}
	
	private void initShell(){
		if(shell == null){
			shellActionContext = new ActionContext();
			shellActionContext.put("parent", parentShell);
			shellActionContext.put("assistor", this);
			
			Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.CodeAssist");
			shell = (Shell) shellThing.doAction("create", shellActionContext);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
	}

	public void setDelayAction(Runnable run){
		delayAction.cancel();
		delayAction.setRunnable(run);
	}
	
	@Override
	public void widgetDisposed(DisposeEvent arg0) {		
		if(shell != null && shell.isDisposed() == false){
			shell.dispose();
		}
	}
		
	public String getTextAssistor() {
		return textAssistor;
	}

	/**
	 * 指定文本辅助器的名称，如sql、databind、reactors等。文本辅助器的名字是由TextAssistor对象自定义的，具体参看CodeHelper。
	 * 
	 * @see CodeHelper
	 * @param textAssistor
	 */
	public void setTextAssistor(String textAssistor) {
		this.textAssistor = textAssistor;
	}

	public static CtClass getClass(Control text, List<String> sentens, Map<String, Class<?>> cache, CodeAssistor assistor) throws NotFoundException{
		if(cache == null){
			return null;
		}
		
		List<String> lists = sentens;		
		CtClass cls = null;
		if(lists.size() > 0){
			String name = lists.get(0);
			Class<?> c = cache.get(name);
			if(c == null && assistor.varActionContext != null){
				Object obj = assistor.varActionContext.get(name);
				if(obj != null){
					assistor.initCache(assistor.varActionContext);
					c = obj.getClass();
				}
			}
			if(c == null){
				if(assistor.actionContext != null){
					Object obj = assistor.actionContext.get(name);
					if(obj != null){
						cls = ClassUtils.get(obj.getClass().getName());
						
						if(cls == null){
							return null;
						}
					}
				}else{
					return null;
				}
			}else{
				cls = ClassUtils.get(c.getName());
				
				if(cls == null){
					return null;
				}
			}
			
			//获取语句的后续的类
			cls = CodeUtils.getClass(sentens, 1, cls);
		}
		
		return cls;
	}
	
	public static void parseFunctionCall(String text, int index,  int methodIndex, List<String> lists, Stack<char[]> matchs){				
		if(index < 0){
			if(methodIndex > index){
				lists.add(0, text.substring(0, methodIndex));
			}
			
			return;
		}
		
		char c = text.charAt(index); 
		if(c == ')'){
			matchs.push(new char[]{'('});
			parseFunctionCall(text, index - 1, -1, lists, matchs);
		}else if(c == '('){
			if(matchs.size() > 0){
				matchs.pop();
				parseFunctionCall(text, index - 1, index, lists, matchs);
			}else{
				if(methodIndex > index){
					lists.add(0, text.substring(index + 1, methodIndex));
				}
				return;
			}
		}else if(c == ' ' || c == '\n' || c == ':'){
			if(methodIndex > index){
				lists.add(0, text.substring(index + 1, methodIndex));
			}
			
			if(matchs.size() == 0){
				return;
			}	
			
			parseFunctionCall(text, index - 1, -1, lists, matchs);
		}else if(c == ';'){
			if(methodIndex > index){
				String str = text.substring(index, methodIndex);
				str = str.trim();
				if(!"".equals(str)){
					lists.add(0, str);
				}
			}
			return;
		}else if(c == '.'){
			if(methodIndex > index){
				String str = text.substring(index + 1, methodIndex);
				str = str.trim();
				if(!"".equals(str)){
					lists.add(0, str);
				}
			}
			
			parseFunctionCall(text, index - 1, index, lists, matchs);
		}else{
			parseFunctionCall(text, index - 1, methodIndex, lists, matchs);
		}
	}
	
	public static void create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Control styledText = UtilData.getObjectByType(self, "styledText", Control.class, actionContext);
		if(styledText == null){
			Object parent = actionContext.get("parent");
			styledText = (Control) parent;
			//if(parent != null && parent instanceof StyledText){
			//	styledText = (StyledText) parent;
			//}
		}
		
		if(styledText == null){
			Executor.warn(TAG, "CodeAssistor: styledText is null, path=" + self.getMetadata().getPath());
			return;
		}
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		if(thing == null){
			//logger.warn("CodeAssistor: thing is null, path=" + self.getMetadata().getPath());
			//return;
		}
		
		CodeAssistor assistor = CodeAssistor.attach(thing, styledText, actionContext);
		actionContext.g().put(self.getMetadata().getName(), assistor);
	}
	

	/**
	 * 返回执行类的可选择方法列表。
	 * 
	 * value是方法名，label是显示的包括参数的， object是List&lt;ParameterInfo&gt;。
	 * 
	 * @param className
	 * @return
	 * @throws NotFoundException 
	 */
	public static List<SelectContent> getMethodsForSelect(String className) throws NotFoundException{
		List<SelectContent> list = new ArrayList<SelectContent>();
		
		CtClass ctClass = ClassPool.getDefault().get(className);
		if(ctClass != null){
			for(CtMethod method : ctClass.getMethods()){
				if((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
					List<ParameterInfo> params = Javaassist.getParameterInfo(method);
					String label = method.getName() + "(";
					String methodName = method.getName() + "(";
					for(int i=0; i<params.size(); i++){
						methodName = methodName + params.get(i).getName();
						String type = params.get(i).getType();
						int lastIndex = type.lastIndexOf(".");
						if(lastIndex != -1){
							type = type.substring(lastIndex + 1, type.length());
						}
						label = label + type + " " + params.get(i).getName();
						
						if(i < params.size() - 1){
							methodName = methodName + ", ";
							label = label + ", ";
						}
					}
					
					CtClass rcls = method.getReturnType();
					String rtype = "void";
					if(rcls != null){
						rtype = rcls.getSimpleName();
					}
					methodName = methodName + ")";
					label = label + "): " + rtype;
					SelectContent content = new SelectContent(method.getName(), label, "icons/debug/public_co.gif"); 
					content.object = params;
					list.add(content);
				}
			}
		}
		
		Collections.sort(list);
		return list;
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
	
	public void checkInput(Object text, int start, int end){
		//获取插入的字符串
		String str = SwtTextUtils.getText(text, start, end - 1);
		
		//判断字符串是否包含参数
		int index1 = str.indexOf("(");
		int index2 = str.indexOf(")", index1);
		if(index1 == -1 || index2 == -1){
			return;
		}
		SwtTextUtils.setCaretOffset(text, start + index1 + 1);
		//stext.setCaretOffset(start + index1 + 1);
		/*
		String params = str.substring(index1 + 1, index2);
		if(params.trim().equals("")){
			return;
		}
				
		String[] ps = params.split("[,]");
		int index = start + index1 + 1;
		for(String p : ps){			
			StyleRange range = new StyleRange(this.paramTextStyle);
			range.start = index;
			range.length = p.length();
			stext.setStyleRange(range);			
			
			index = index + p.length();
		}*/
	}

	public static void main(String args[]){
		try{
			String text = " abcd\n.println().";
			List<String> lists = new ArrayList<String>();
			parseFunctionCall(text, text.length() - 1, text.length(), lists, new Stack<char[]>());
			System.out.println(lists);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public List<VariableDesc> getVariables(String code, int offset, List<String> statements, Thing thing,
			ActionContext actionContext) {
		List<VariableDesc> vars = new ArrayList<VariableDesc>();
		for(String key : varCache.keySet()) {
			vars.add(varCache.get(key));
		}
		
		return vars;
	}
}
