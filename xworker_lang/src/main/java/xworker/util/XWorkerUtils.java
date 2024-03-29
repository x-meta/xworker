package xworker.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilThing;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.lang.util.Config;
import xworker.workbench.IWorkbench;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 集中零散的一些常用的静态方法以便容易找到和使用。
 * 
 * @author Administrator
 *
 */
public class XWorkerUtils {
	//private static Logger logger = LoggerFactory.getLogger(XWorkerUtils.class);
	private static final String TAG = XWorkerUtils.class.getName();
	
	private static final String KEY_IDE = "__xworker_xworkerUtils_IDE__";
	private static final String IDE_PERMISSION = "XWorker_Thing_IDE";
	private static IIde ide = null;	

	private static Boolean isRWT = null;

	/**
	 * 返回是否运行在Eclpise的RWT环境下。
	 */
	public static boolean isRWT() {
		if(isRWT == null) {
			try {
				Thing swt = World.getInstance().getThing("xworker.swt.SWT");
				isRWT = swt.doAction("isRWTWebClient", new ActionContext());
				if(isRWT == null) {
					isRWT = false;
				}
			}catch(Throwable t) {
				//log.error("Check isRWT error", t);
				isRWT = false;
			}
		}
		
		return isRWT;
	}
	
	public static void setRWTAttribute(String key, Object value, ActionContext actionContext) {
		Thing swt = World.getInstance().getThing("xworker.swt.SWT");
		if(swt != null) {
			swt.doAction("rwtSetData", actionContext, "key", key, "value", value);
		}
	}
	
	public static Object getRWTAttribute(String key, ActionContext actionContext) {
		Thing swt = World.getInstance().getThing("xworker.swt.SWT");
		if(swt != null) {
			return swt.doAction("rwtGetData", actionContext, "key", key);
		}else {
			return null;
		}
	}
	
	public static void setIde(IIde ide){
		setIde(ide, false);
	}

	public static void setIde(IIde ide, boolean force){
		//只能存在一个IDE
		if(isRWT()) {
			IIde oldIde = (IIde) getRWTAttribute(KEY_IDE, null);
			if(oldIde == null || force) {
				setRWTAttribute(KEY_IDE, ide, null);
			}
		}else {
			if(XWorkerUtils.ide == null || XWorkerUtils.ide.isDisposed() || force){
				XWorkerUtils.ide = ide;
			}
		}
	}
	
	public static IIde getIde(){
		if(isRWT()) {
			return (IIde) getRWTAttribute(KEY_IDE, null);
		}else{
			return ide;
		}
	}
	
	/**
	 * 对一个事物列表进行排序。
	 */
	public static void sortThings(final List<Thing> things, final String attributeName){
		things.sort((o1, o2) -> {
			String str1 = o1.getStringBlankAsNull(attributeName);
			String str2 = o2.getStringBlankAsNull(attributeName);
			if (str1 == null && str2 == null) {
				return 0;
			} else if (str1 != null) {
				return str1.compareTo(str2);
			} else {
				return -1;
			}
		});
	}

	public static boolean hasWebServer(){
		return GlobalConfig.getWebUrl() != null;
	}

	/**
	 * 返回系统是否包含了XWorker的环境，主要是判断编辑器等是否存在。
	 */
	public static boolean hasXWorker() {
		World world = World.getInstance();
		return world.getThing("xworker.thingeditor.SwtThingEditor") != null
				|| world.getThing("xworker.javafx.thingeditor.JavaFXThingEditor") != null
				|| world.getThing("xworker.webserver.WebThingEditor") != null;
	}
	
	/**
	 * 在XWorker的事物管理器中打开并编辑一个事物。
	 */
	public static void ideOpenThing(Thing thing){
		if(thing == null) {
			return;
		}
		
		//判断权限		
		IIde ide = getIde();
		if(ide != null){
			if(checkIDEPermission("openThing", thing.getMetadata().getPath())) {
				ide.ideOpenThing(thing);
			}
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}
	
	/**
	 * IDE的权限校验。使用DEFAULT环境。
	 */
	private static boolean checkIDEPermission(String action, String path) {
		IIde ide = getIde();
		if(ide != null) {
			return xworker.security.SecurityManager.doCheck("DEFAULT",
					IDE_PERMISSION, action, path, ide.getActionContext());
		}else {
			return xworker.security.SecurityManager.doCheck("DEFAULT", 
					IDE_PERMISSION, action, path, new ActionContext());
		}
	}

	/**
	 * 返回IDE的工作台，如果存在。
	 */
	public static IWorkbench<?, ?, ?> getWorkbench(){
		IIde ide = getIde();
		if(ide != null){
			return ide.getActionContainer().doAction("getWorkbench", ide.getActionContext());
		}else{
			Executor.warn(TAG, "Can not return workbench, Ide is not setted!");
			return null;
		}
	}
	
	/**
	 * 事物管理器打开一个文件。
	 */
	public static void ideOpenFile(File file){
		IIde ide = getIde();
		
		if(ide != null){
			if(checkIDEPermission("openFile", file.getAbsolutePath())) {
				ide.ideOpenFile(file);
			}
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}
	
	/**
	 * 打开事物并选择事物的代码的某一行
	 */
	public static void ideOpenThingAndSelectCodeLine(final Thing thing, final String codeAttrName, final int line){
		IIde ide = getIde();
		
		if(ide != null){
			if(checkIDEPermission("openThingAndSelectCodeLine", thing.getMetadata().getPath())) {
				ide.ideOpenThingAndSelectCodeLine(thing, codeAttrName, line);
			}
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}
	
	/**
	 * 在IDE的编辑器区域打开一个Composite，其中Composite作为编辑器。
	 * 
	 * 编辑器参数：
	 *     path:    编辑器的标识，使用Composite事物的路径。
	 *     title:   编辑器的标题，使用Composite事物的标签。
	 *     
	 * 初始化编辑器：    
	 *     如果编辑器创建后，在编辑器自身的上下文中有名为ations的类型为ActionContainer的变量，
	 *     那么会执行actions的doInit方法。
	 */
	public static void ideOpenComposite(Thing compositeThing){
		if(checkIDEPermission("openComposite", compositeThing.getMetadata().getPath())) {
			Map<String, Object> params = new HashMap<>();
			params.put("compositeThing", compositeThing);			
			params.put("path", "composite/" + compositeThing.getMetadata().getPath());
			params.put("title", compositeThing.getMetadata().getLabel());
			ideDoAction("openTab", params);
		}
	}
	
	/**
	 * 在IDE的编辑器区域打开一个Composite，其中Composite作为编辑器。
	 * 
	 * 编辑器参数：
	 *     path:    编辑器的标识，如果parameters中有path属性则使用，否则使用Composite事物的路径。
	 *     title:   编辑器的标题，如果parameters中有title属性则使用，否则使用Composite事物的标签。
	 *     
	 * 初始化编辑器：    
	 *     如果编辑器创建后，在编辑器自身的上下文中有名为ations的类型为ActionContainer的变量，
	 * 那么会执行actions的doInit方法，会把parameters参数作为params变量传入。
	 */
	public static void ideOpenComposite(Thing compositeThing, Map<String, Object> parameters){
		if(checkIDEPermission("openComposite", compositeThing.getMetadata().getPath())) {
			Map<String, Object> params = new HashMap<>();
			params.put("compositeThing", compositeThing);	
			Object path = parameters.get("path");
			if(path != null) {
				params.put("path", path);
			}else {
				params.put("path", "composite/" + compositeThing.getMetadata().getPath());
			}
			Object title = parameters.get("title");
			if(title == null) {
				params.put("title", title);
			}else {
				params.put("title", compositeThing.getMetadata().getLabel());
			}
			params.put("params", parameters);
			params.putAll(parameters);

			ideDoAction("openTab", params);
		}
	}
	
	/**
	 * 在IDE的编辑器区域打开一个Composite，其中Composite作为编辑器。
	 * 
	 * 编辑器参数：
	 *     path:    编辑器的标识，如果parameters中有path属性则使用，否则使用Composite事物的路径。
	 *     title:   使用参数title，如果title参数为null，从parameters中取title，其次使用Composite事物的标签。
	 *     
	 * 初始化编辑器：    
	 *     如果编辑器创建后，在编辑器自身的上下文中有名为ations的类型为ActionContainer的变量，
	 *     那么会执行actions的doInit方法，会把parameters参数作为params变量传入。
	 */
	public static void ideOpenComposite(Thing compositeThing, String title, Map<String, Object> parameters){
		if(checkIDEPermission("openComposite", compositeThing.getMetadata().getPath())) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("compositeThing", compositeThing);			
			Object path = parameters.get("path");
			if(path != null) {
				params.put("path", path);
			}else {
				params.put("path", "composite/" + compositeThing.getMetadata().getPath());
			}
			params.put("title", title);
			if(title == null) {
				Object title1 = parameters.get("title");
				if(title1 == null) {
					params.put("title", title1);
				}else {
					params.put("title", compositeThing.getMetadata().getLabel());
				}
			}
			params.put("params", parameters);
			ideDoAction("openTab", params);
		}
	}
	
	/**
	 * 在XWorker事物管理器中内置的浏览器上打开一个URL。
	 */
	public static void ideOpenUrl(String url){
		if(checkIDEPermission("openUrl", url)) {
			Map<String, Object> params = new HashMap<>();
			params.put("url", url);
			params.put("name", url);
			ideDoAction("openUrl", params);
		}
	}
	
	public static void ideOpenThingTab(Thing thing){
		if(checkIDEPermission("openThingTab", thing.getMetadata().getPath())) {
			Map<String, Object> params = new HashMap<>();
			params.put("thing", thing);
			ideDoAction("thingTab", params);	
		}
	}
	
	/**
	 * 在XWorker事物管理器中内置的浏览器上打开一个WebControl。
	 */
	public static void ideOpenWebControl(Thing control){
		ideOpenUrl(getWebControlUrl(control));
	}
	
	/**
	 * 返回在XWorker事物管理器内置的WEB服务器上一个WebControl的地址。
	 */
	public static String getWebControlUrl(Thing control){
		return GlobalConfig.getWebUrl() + "do?sc=" + control.getMetadata().getPath();
	}
	
	/**
	 * 从父变量上下文中(parentActionContext或parentContext)中获取变量，是递归获取的，如果父变量上下返回null
	 * ，那么取父的父...。
	 */
	public static <T> T getParentVar(ActionContext actionContext, String name){
		Map<ActionContext, ActionContext> context = new HashMap<>();
		return getParentVar(actionContext, name, context);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getParentVar(ActionContext actionContext, String name, Map<ActionContext, ActionContext> context) {
		//context用于避免递归，如当前的actionContext的parentContext还是它自己
		if(context.get(actionContext) != null) {
			return null;
		}else {
			context.put(actionContext, actionContext);
		}
		
		
		ActionContext p = actionContext.getObject("parentActionContext");
		if(p == null){
			p = actionContext.getObject("parentContext");
		}
		
		if(p == null){
			return null;
		}else{
			T obj = (T) p.get(name);
			if(obj != null){
				return obj; 
			}else{
				return getParentVar(p, name, context);
			}
		}
	}
	
	/**
	 * 获取XWorker的网站根目录。
	 */
	public static String getWebUrl(){
		return GlobalConfig.getWebUrl();
	}
	
	/**
	 * 执行XWorker事物管理器的指定的动作。
	 */
	public static void ideDoAction(String name, Map<String, Object> parameters){
		IIde ide = getIde();
		
		if(ide != null){
			if(checkIDEPermission("doAction", name)) {
				ide.ideDoAction(name, parameters);
			}
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}
	
	/**
	 * 返回IDE的动作容器。
	 */
	public static ActionContainer getIdeActionContainer(){
		IIde ide = getIde();
		
		if(ide != null){
			return ide.getActionContainer();
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
		
		return null;
	}
	
	/**
	 * 返回编辑器的主界面的Shell对象。
	 */
	public static Object getIDEShell(){
		IIde ide = getIde();
		if(ide != null){
			return ide.getIDEShell();
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
		
		return null;
	}
	
	/**
	 * 通过XWorker的IDE显示一条提示信息。该方式是异步执行的。
	 */
	public static void ideShowMessageBox(final String title, final String message, final int style){
		IIde ide = getIde();
		if(ide != null){
			ide.ideShowMessageBox(title, message, style, null);
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}

	/**
	 * 通过XWorker的IDE显示一条提示信息。该方式是异步执行的。
	 */
	public static void ideShowMessageBox(final String title, final String message, final int style, final Callback<Integer, Void> callback){
		IIde ide = getIde();
		if(ide != null){
			ide.ideShowMessageBox(title, message, style, callback);
		}else{
			Executor.warn(TAG, "Ide is not setted");
		}
	}
	
	/**
	 * 打开一个Editor，如果Editor已经打开那么是设置参数。
	 */
	public static Object openEditor(String id, Thing editor, Map<String, Object> params) {
		IIde ide = getIde();
		if(ide != null){
			return ide.getActionContainer().doAction("openEditor", ide.getActionContext(), 
					"id", id, "editor", editor, "params", params);
		}else{
			Executor.warn(TAG, "Ide is not setted");
			return null;
		}
	}
	
	/**
	 * 在工作台IDE上打开一个视图。
	 * 
	 * @param id 视图标识
	 * @param type 可选值left,right,top,bottom之一
	 * @param closeable 是否可关闭
	 * @param composite 容器模型
	 * @param params 参数
	 */
	public static Object openView(String id, String type, boolean closeable, Thing composite, Map<String, Object> params) {
		IIde ide = getIde();
		if(ide != null){
			return ide.getActionContainer().doAction("openView", ide.getActionContext(), 
					"id", id, "type", type, "params", params, "composite", composite);
		}else{
			Executor.warn(TAG, "Ide is not setted");
			return null;
		}
	}
	
	/**
     * 获取显示事物Description的URL地址。
     * 
     * @param thing 事物
     * @return 事物文档的URL
     */
    public static String getThingDescUrl(Thing thing){
    	return GlobalConfig.getThingDescUrl(thing);
    }
    
    /**
     * 获取显示事物Description的URL地址。
     * 
     * @param thingPath 事物的路径
     * @return 事物文档的URL
     */
    public static String getThingDescUrl(String thingPath) {
    	return GlobalConfig.getThingDescUrl(thingPath);
    }

	/**
	 * 返回模型的描述文档。
	 */
	public static String getThingDesc(Thing thing){
		Thing realThing = thing;
		while(true){
			String description = realThing.getStringBlankAsNull("description");
			if((description == null || "".equals(description.trim())) && realThing.getBoolean("inheritDescription")){
				//是否是继承描述
				List<Thing> extendsThing = realThing.getExtends();
				if(extendsThing.size() > 0){
					realThing = extendsThing.get(0);
				}else{
					break;
				}
			}else{
				break;
			}
		}

		String description = realThing.getStringBlankAsNull("description");
		if(description != null) {
			return  description;
		}else {
			return "";
		}
	}
    
    /**
     * 事物Http协议的客户端向服务器端发送请求。
     * 
     * @param url URL地址
     * @param action 动作
     * @param thing 事物
     * @param actionContext 变量上下文
     * @return 事物
     * @throws IOException IO异常
     */
    public static Thing httpThingSendRequest(String url, String action, Thing thing, ActionContext actionContext) throws IOException{
    	//return ThingHttpActions.clientSendRequest(url, action, thing, actionContext);
    	return null;
    }
    
    /**
     * 发送一条系统信息。
     */
    public static void sendMessage(String senderId, String label, String desc, String type, String descPath, String thingPath, String compositePath, ActionContext actionContext){
    	/*
    	List<DataObject> datas = DataObjectUtil.query("", UtilMap.toMap("senderId", senderId), actionContext);
    	if(datas != null && datas.size() > 0){
    		DataObject data = datas.get(0);
    		data.put("cnt", data.getInt("cnt") + 1);
    		data.put("label", label);
	    	data.put("description", desc);
	    	data.put("descPath", descPath);
	    	data.put("thingPath", thingPath);
	    	data.put("compositePath", compositePath);
	    	data.put("createDate", new Date());
	    	data.put("type", type);
    		data.put("updateDate", new Date());
    		data.update(actionContext);
    	}else{    	
	    	DataObject data = new DataObject("xworker.app.message.dataobjects.IDEMessage");
	    	data.put("senderId", senderId);
	    	data.put("cnt", 1);
	    	data.put("label", label);
	    	data.put("description", desc);
	    	data.put("descPath", descPath);
	    	data.put("thingPath", thingPath);
	    	data.put("compositePath", compositePath);
	    	data.put("createDate", new Date());
	    	data.put("type", type);
	    	data.put("updateDate", new Date());
	    	
	    	data = data.create(actionContext);
    	}
    	
    	// 通知有新消息
    	MessageCenter.hasNewMessage = true;
    	*/
    }
    
    /**
     * 发送一条编辑事物的系统信息。
     * 
     * @param senderId 发送者
     * @param label 标签
     * @param desc 描述
     * @param thingPath 事物路径
     * @param actionContext 变量上下文
     */
    public static void sendThingMessage(String senderId, String label, String desc, String thingPath, ActionContext actionContext){
    	sendMessage(senderId, label, desc, "thing", null, thingPath, null, actionContext);
    }
    
    /**
     * 发送一条打开Composite的系统信息。
     */
    public static void sendCompositeMessage(String senderId, String label, String desc, String compositePath, ActionContext actionContext){
    	sendMessage(senderId, label, desc, "composite", null, null, compositePath, actionContext);
    }
    
    /**
	 * 通过事物属性获取事物定义的事物，或者通过指定的子事物的第一个子节点获取定义的事物。
	 * 
	 * @param thing 事物
	 * @param attributeName 属性名
	 * @param childThingPath 子事物路径
	 * @return 事物
	 */
	public static Thing getThingFromAttributeOrChild(Thing thing, String attributeName, String childThingPath){
		return UtilThing.getThingFromAttributeOrChild(thing, attributeName, childThingPath);
	}
	
	/**
	 * 通过事物属性获取事物定义的事物，或者通过指定的子事物的第一个子节点获取定义的事物。
	 * 
	 * @param thing 事物
	 * @param attributeName 属性名
	 * @param childThingPath 子事物路径
	 * @return 事物
	 */
	public static Thing getThingFromAttributeOrChilds(Thing thing, String attributeName, String childThingPath){
		return UtilThing.getThingFromAttributeOrChilds(thing, attributeName, childThingPath);
	}
	
	/**
	 * 获取事物，如果不存在就创建一个。
	 * 
	 * @param path 路径
	 * @param thingManager 事物管理器
	 * @param descriptorForCreate 描述者
	 * @return 事物
	 */
	public static Thing getThingIfNotExistsCreate(String path, String thingManager, String descriptorForCreate){
		return UtilThing.getThingIfNotExistsCreate(path, thingManager, descriptorForCreate);
	}
	
	/**
	 * 获取事物，如果不存在就用已有的事物创建一个。
	 * 
	 * @param path 路径
	 * @param thingManager 事物管理器
	 * @param forReplace 是否覆盖
	 * @return 事物
	 */
	public static Thing getThingIfNotExistsCopy(String path, String thingManager, Thing forReplace){
		return UtilThing.getThingIfNotExistsCreate(path, thingManager, forReplace);
	}
	
	/**
	 * 获取事物，如果不存在就用已有的事物创建一个。
	 * 
	 * @param path 路径
	 * @param thingManager 事物管理器
	 * @param forReplacePath 是否覆盖
	 * @return 事物
	 */
	public static Thing getThingIfNotExistsCopy(String path, String thingManager, String forReplacePath){
		Thing forReplace = World.getInstance().getThing(forReplacePath);
		return UtilThing.getThingIfNotExistsCreate(path, thingManager, forReplace);
	}
	
	/**
	 * 返回目标文件目录是否是XWorker的HOME目录。
	 */
	public static boolean isXWorkerHome(File file) throws IOException{
		if(file.isFile()){
			return false;
		}
		
		File xfile = new File(World.getInstance().getPath());
		return xfile.getCanonicalFile().equals(file.getCanonicalFile());
	}
	
    public static boolean isThingExplorer(){
    	IIde ide = XWorkerUtils.getIde();
		return ide != null && ide.isThingExplorer();
	}
    
    public static void addOuterProject(String projectName, String projectDir){
    	World world = World.getInstance();
        Thing  projectSet = world.getThing("_local.xworker.worldExplorer.ProjectSet");
        if(projectSet != null){
        	Thing project = new Thing("xworker.ide.worldexplorer.things.ProjectSet/@ProjectDir");
			project.put("name", projectName);
			project.put("dir", projectDir);

			projectSet.addChild(project);
			projectSet.save();                 
        }
    }
    
    /**
     * 获取一组事物的分组。
     */
    public static List<ThingGroup> getThingGroups(List<Thing> things){
    	//获取分组并排序
		List<ThingGroup> groups = new ArrayList<>();
		for(Thing thing : things){	
			String group = thing.getMetadata().getGroup();//thing.getStringBlankAsNull("group");
			if(group == null){
				group = "";
			}
			
			//一个分组字符串可以是多个分组，使用,号隔开
			for(String g : group.split("[,]")){
				addThingToGroups(thing, g, groups);
			}
		}		
		
		Collections.sort(groups);
		for(ThingGroup group : groups){
			group.sort();
		}
		return groups;
    }
    
    public static void addThingToGroups(Thing thing, String groupStr, List<ThingGroup> groups){
    	if(groupStr == null || "".equals(groupStr)){
    		groups.add(new ThingGroup(thing, groupStr));
    	}else{
    		String[] gs = groupStr.split("[.]");
    		ThingGroup parent = null;
			for (String s : gs) {
				String g = s;
				int index = g.indexOf("|");
				if (index != -1) {
					g = g.substring(index + 1);
				}
				List<ThingGroup> gps;
				if (parent == null) {
					gps = groups;
				} else {
					gps = parent.childs;
				}

				boolean ok = false;
				for (ThingGroup tg : gps) {
					if (tg.group.equals(g)) {
						parent = tg;
						tg.addWeight(s);
						ok = true;
						break;
					}
				}

				if (!ok) {
					parent = new ThingGroup(null, s);
					gps.add(parent);
				}
			}
    		
    		if(parent != null){
    			parent.childs.add(new ThingGroup(thing, ""));
    		}
    	}
    }   
    
    public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext){
    	if(keywords == null) {
    		keywords = Collections.emptyList();    		
    	}
    	return ThingUtils.searchRegistThings(registorThing, registType, keywords, actionContext);
    }
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  ActionContext actionContext){
		if(keywords == null) {
    		keywords = Collections.emptyList();    		
    	}
		return ThingUtils.searchRegistThings(registorThing, registType, keywords, parent, actionContext);
	}
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  boolean noDescriptor, ActionContext actionContext){

		if(keywords == null) {
    		keywords = Collections.emptyList();    		
    	}
		return ThingUtils.searchRegistThings(registorThing, registType, keywords, parent, noDescriptor, actionContext);
	}
	
	/**
	 * 根据默认首选项返回对应的首选项实例模型。
	 */
	public static Thing getPreference(String configDesc) {
		return getPreference(World.getInstance().getThing(configDesc));
	}
	
	/**
	 * 根据默认首选项返回对应的首选项实例模型。
	 */
	public static Thing getPreference(Thing configDesc) {		
		String path = configDesc.getRoot().getMetadata().getPath();
		if(configDesc.getRoot() != configDesc) {
			path = path + "." + configDesc.getMetadata().getName();
		}
		path = "_local.xworker.preferences." + path.replace('.', '_');
		Thing config = World.getInstance().getThing(path);
		if(config == null) {
			config = new Thing(configDesc.getMetadata().getPath());
			config.getAttributes().putAll(configDesc.getAttributes());
			config.set("descriptors", configDesc.getMetadata().getPath());
			config.set("th_createIndex", false);
			config.set("th_registThing", null);			
			config.saveAs("_local", path);
		}

		return config;
	}

	public static String getThingIcon(Thing thing){
		if(thing == null){
			return null;
		}

		String icon = thing.getStringBlankAsNull("icon");

		if(icon == null){
			for(Thing descriptor : thing.getAllDescriptors()){
				//println descriptor.getMetadata().getPath();
				icon = descriptor.getString("icon");
				//println icon;
				if(icon != null && !"".equals(icon)){
					break;
				}
			}
		}

		if(icon == null){
			icon = "icons/xworker/dataObjectChild.gif";
		}
		return icon;
	}

	public static <T> T requireNonNull(T obj, Thing thing){
		if (obj == null)
			throw new NullPointerException(thing != null ? "thing: " + thing.getMetadata().getPath() : "");
		return obj;
	}

	public static <T> T requireNonNull(T obj, Thing thing, String message){
		if (obj == null)
			throw new NullPointerException(message + (thing != null ? ", thing: " + thing.getMetadata().getPath() : ""));
		return obj;
	}

	/**
	 * 解压一个ZipInputStream中的文件到XWorker的目录下。
	 */
	public static void unzipToXWorker(ZipInputStream zipInputStream, boolean verbose) throws IOException {
		ZipEntry entry = null;
		String webroot = World.getInstance().getPath() + "/";
		while ((entry = zipInputStream.getNextEntry()) != null) {
			if(entry.isDirectory()){
				continue;
			}

			File outFile = new File(webroot + entry.getName());
			if (!outFile.getParentFile().exists()) {
				if(!outFile.getParentFile().mkdirs()){
					throw new IOException("Can not create directory " + outFile.getParentFile());
				}
			}

			try (FileOutputStream fout = new FileOutputStream(outFile)) {
				byte[] bytes = new byte[4096];
				int length = -1;
				while ((length = zipInputStream.read(bytes)) != -1) {
					fout.write(bytes, 0, length);
				}
			}
			if(verbose){
				Executor.info(TAG, "Unzip file to " + outFile.getPath());
			}

			zipInputStream.closeEntry();
		}
	}

	/**
	 * 遍历系统类库下的jar文件。
	 * 查找jar根目录下的/xworker_resources.zip，如果存在则解压到xworker的目录下。
	 * 查找jar根目录下的/xworker_init.conf，如果存在则遍历每一行，一行是一个模型的路径，如果模型存在，则执行它的run方法。
	 */
	public static void setup(){
		//启动扫描模型注册，放到内存缓存中
		ThingRegistUtils.startRegistThingCache();

		//解压jar中的资源
		World world = World.getInstance();
		try {
			for(String path : world.getClassLoader().getClassPath().split("[" + File.pathSeparator + "]")){
				if(path.toLowerCase().endsWith(".jar")){
					try {
						URL url = null;
						if(!hasProtocol(path)) {
							url = new URL("jar:file:" + path + "!/xworker_resources.zip");
						} else {
							url = new URL("jar:" + path + "!/xworker_resources.zip");
						}

						try {
							InputStream in = url.openStream();
							if(in != null) {
								Executor.info(TAG, "Unzip resources " + url);
								ZipInputStream zin = new ZipInputStream(in);
								unzipToXWorker(zin, false);
								zin.close();
								in.close();
							}
						}catch(Exception ignored) {
							//Executor.info(TAG, "Extract zip " + url + " exception", e);
						}
					}catch(Exception ignored) {
					}
				}
			}
		}catch(Exception e) {
			Executor.warn(TAG, "Extract xworker resources error", e);
		}

		//执行模型的run方法
		try {
			try {
				while (!ThingUtils.isFirstCacheFinised()) {
					Thread.sleep(100);
				}
			}catch(Exception ignore){
			}

			for(String path : world.getClassLoader().getClassPath().split("[" + File.pathSeparator + "]")){
				if(path.toLowerCase().endsWith(".jar")){
					try {
						URL url = null;
						if(!hasProtocol(path)) {
							url = new URL("jar:file:" + path + "!/xworker_init.conf");
						} else {
							url = new URL("jar:" + path + "!/xworker_init.conf");
						}

						try {
							InputStream in = url.openStream();
							if(in != null) {
								Executor.info(TAG, "Execute init conf file " + url);
								BufferedReader br = new BufferedReader(new InputStreamReader(in));
								String line = null;
								while((line = br.readLine()) != null){
									line = line.trim();
									Thing thing = world.getThing(line);
									if(thing != null){
										Executor.info(TAG, "Execute " + line + " run action");
										thing.doAction("run", new ActionContext());
									}
								}
								br.close();
								in.close();
							}
						}catch(Exception ignored) {
							//Executor.info(TAG, "Extract zip " + url + " exception", e);
						}
					}catch(Exception ignored) {
					}
				}
			}
		}catch(Exception e) {
			Executor.warn(TAG, "Execute xworker init error", e);
		}

		String OS = world.getOS();
		String worldPath = world.getPath();
		String PROCESSOR_ARCHITECTURE = world.getJVMBit();
		Executor.info(TAG, "If this program starts from a java project:");
		String library = worldPath + "/os/library/"	+ OS;
		Executor.info(TAG, "    Add JVM options -Djava.library.path=" + library + "_" + PROCESSOR_ARCHITECTURE +"/ to Run/Debug Configuration");
		Executor.info(TAG, "    Add jars in "  + worldPath + "/lib/; "  + worldPath + "/os/lib/lib_"
				+ OS + "_" + PROCESSOR_ARCHITECTURE +"/ to java project");
	}

	private static boolean hasProtocol(String path) {
		try {
			URL purl = new URL(path);
			return purl.getProtocol() != null;
		}catch(Exception e) {
			return false;
		}
	}

	public static String getConfigValue(String configName, String path){
		return Config.getConfig(configName, path);
	}

	public static void setConfigValue(String configName, String path, String value){
		Config.setConfig(configName, path, value);
	}
}
