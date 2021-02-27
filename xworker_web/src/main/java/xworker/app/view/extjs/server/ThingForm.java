package xworker.app.view.extjs.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.app.model.tree.impl.ThingTreeModelActions;
import xworker.dataObject.utils.JsonFormator;
import xworker.lang.executor.Executor;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;
import xworker.util.JacksonFormator;

public class ThingForm {
	private static final String TAG = ThingForm.class.getName();

	@SuppressWarnings("deprecation")
	public static void read(ActionContext actionContext) throws IOException {
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get("request");
		String thingPath = request.getParameter("thingPath");
		Thing thing = world.getThing(thingPath);

		if (!SecurityManager
				.doCheck(
						"WEB",
						PermissionConstants.XWOREKR_THING_MANAGER,
						"read", thingPath, actionContext)) {
			return;
		}

		// 基于安全性的考虑，暂时移除这部分的代码
		String record = "{}";
		Map<String, Object> result = UtilMap.toMap(new Object[] { "success",
				"true", "msg", "" });
		actionContext.peek().put("result", result);
		if (thing == null) {
			result.put("success", "false");
			result.put("msg", "事物不存在=" + thingPath);
		} else {
			try {
				record = JacksonFormator.formatObject(thing.getAttributes());
				// Thing jsonFactory =
				// world.getThing("xworker.text.JsonDataFormat");
				// record = (String) jsonFactory.doAction("format",
				// actionContext, UtilMap.toMap(new
				// Object[]{"data",thing.getAttributes()}));
				if (result.get("msg") == null) {
					result.put("msg", "数据读取成功");
				}
			} catch (Exception e) {
				Executor.error(TAG, "数据读取失败", e);
				result.put("success", "false");
				result.put("msg", JsonFormator.formatString(ExceptionUtil
						.getRootMessage(e)));
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + result.get("success") + ",\n"
				+ "\"msg\":\"" + result.get("msg") + "\",\n" + "\"data\":"
				+ record + "\n" + "}";
		response.getWriter().println(code);
	}

	public static void delete(ActionContext actionContext) throws IOException {
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get("request");
		String thingPath = request.getParameter("thingPath");
		Thing thing = world.getThing(thingPath);

		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,
				"delete", thingPath, actionContext)) {
			return;
		}

		// 基于安全性的考虑，暂时移除这部分的代码
		String record = "{}";
		Map<String, Object> result = UtilMap.toMap(new Object[] { "success",
				"true", "msg", "" });
		actionContext.peek().put("result", result);
		if (thing == null) {
			result.put("success", "false");
			result.put("msg", "事物不存在=" + thingPath);
		} else {
			try {
				Thing parent = thing.getParent();
				thing.remove();
				if (parent != null) {
					parent.save();
				}
				record = "{thingPath: '" + thingPath + "'}";
				result.put("msg", "事物已经删除！");
			} catch (Exception e) {
				Executor.error(TAG, "数据读取失败", e);
				result.put("success", "false");
				result.put("msg", JsonFormator.formatString(ExceptionUtil
						.getRootMessage(e)));
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + result.get("success") + ",\n"
				+ "\"msg\":\"" + result.get("msg") + "\",\n" + "\"data\":"
				+ record + "\n" + "}";
		response.getWriter().println(code);
	}

	public static void update(ActionContext actionContext) throws IOException {
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get("request");
		String thingPath = request.getParameter("thingPath");
		Thing thing = world.getThing(thingPath);

		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,
				"update", thingPath, actionContext)) {
			return;
		}

		// 基于安全性的考虑，暂时移除这部分的代码
		String record = "{}";
		Map<String, Object> result = UtilMap.toMap(new Object[] { "success",
				"true", "msg", "" });
		actionContext.peek().put("result", result);
		if (thing == null) {
			result.put("success", "false");
			result.put("msg", "事物不存在=" + thingPath);
		} else {
			try {
				List<Thing> attributes = thing.getDescriptor().getAllChilds(
						"attribute");
				for (Thing attr : attributes) {
					String name = attr.getMetadata().getName();
					thing.put(name, request.getParameter(name));
					if ("closeAction".equals(name)) {
						Executor.info(TAG, name + "=" + request.getParameter(name));
					}
				}
				thing.save();
				record = "{}";
				result.put("msg", "保存成功！");
			} catch (Exception e) {
				Executor.error(TAG, "数据读取失败", e);
				result.put("success", "false");
				result.put("msg", JsonFormator.formatString(ExceptionUtil
						.getRootMessage(e)));
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + result.get("success") + ",\n"
				+ "\"msg\":\"" + result.get("msg") + "\",\n" + "\"data\":"
				+ record + "\n" + "}";
		response.getWriter().println(code);
	}
	
	public static void create(ActionContext actionContext) throws IOException {
		World world = World.getInstance();

		//参数
		HttpServletRequest request = (HttpServletRequest) actionContext	.get("request");
		String name = request.getParameter("name");
		String descriptor = request.getParameter("descriptor");
		String thingManager = request.getParameter("thingManager");
		String path = request.getParameter("path");
		
		String thingPath = null;
		if(path == null || "".equals(path)){
			thingPath = name;
		}else{
			thingPath = path + "." + name;
		}
						
		Thing thing = world.getThing(thingPath);
		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,
				"create", thingPath, actionContext)) {
			return;
		}
		
		boolean ok = false;
		String msg = null;
		if(thing != null){
			msg = "事物" + thingPath + "已经存在！";
		}else if(name == null || "".equals(name)){
			msg = "事物名不能为空！";
		}else{
			thing = new Thing(descriptor);
			thing.put("name", name);
			thing.saveAs(thingManager, thingPath);
			ok = true;
			msg = "事物创建成功！";
		}

		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + ok + ",\n"
				+ "\"msg\":\"" + msg + "\",\n" + "\"data\":"
				+ null + "\n" + "}";
		response.getWriter().println(code);
	}

	public static void updateBaseInfo(ActionContext actionContext)
			throws IOException {
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get("request");
		String thingPath = request.getParameter("thingPath");
		Thing thing = world.getThing(thingPath);

		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,
				"update", thingPath, actionContext)) {
			return;
		}

		// 基于安全性的考虑，暂时移除这部分的代码
		String record = "{}";
		Map<String, Object> result = UtilMap.toMap(new Object[] { "success",
				"true", "msg", "" });
		actionContext.peek().put("result", result);
		if (thing == null) {
			result.put("success", "false");
			result.put("msg", "事物不存在=" + thingPath);
		} else {
			try {
				thing.put("name", request.getParameter("name"));
				thing.put("label", request.getParameter("label"));
				thing.put("description", request.getParameter("description"));
				thing.save();
				record = "{}";
				result.put("msg", "保存成功！");
			} catch (Exception e) {
				Executor.error(TAG, "数据读取失败", e);
				result.put("success", "false");
				result.put("msg", JsonFormator.formatString(ExceptionUtil
						.getRootMessage(e)));
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + result.get("success") + ",\n"
				+ "\"msg\":\"" + result.get("msg") + "\",\n" + "\"data\":"
				+ record + "\n" + "}";
		response.getWriter().println(code);
	}

	public static void addChild(ActionContext actionContext) throws IOException {
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		String thingPath = request.getParameter("thingPath");
		Thing thing = world.getThing(thingPath);

		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,	"addChild", thingPath, actionContext)) {
			return;
		}

		// 基于安全性的考虑，暂时移除这部分的代码
		String record = "{}";
		Map<String, Object> result = UtilMap.toMap(new Object[] { "success", "true", "msg", "" });
		actionContext.peek().put("result", result);
		if (thing == null) {
			result.put("success", "false");
			result.put("msg", "事物不存在=" + thingPath);
		} else {
			try {
				String descriptorPath = request.getParameter("descriptorPath");
				String name = request.getParameter("name");
				Thing child = new Thing(descriptorPath);
				child.put("name", name);
				thing.addChild(child);				
				thing.save();
				String icon = ThingTreeModelActions.getThingIcon(child);
				record = "{path:'" + child.getMetadata().getPath() + "', name:'" + child.getMetadata().getName() + "', icon:'" + icon + "'}";
				result.put("msg", "保存成功！");
			} catch (Exception e) {
				Executor.error(TAG, "数据读取失败", e);
				result.put("success", "false");
				result.put("msg", JsonFormator.formatString(ExceptionUtil
						.getRootMessage(e)));
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + result.get("success") + ",\n"
				+ "\"msg\":\"" + result.get("msg") + "\",\n" + "\"data\":"
				+ record + "\n" + "}";
		response.getWriter().println(code);
	}

	public static void createCategory(ActionContext actionContext) throws IOException{
		World world = World.getInstance();

		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		String indexPath = request.getParameter("indexPath");
		String name = request.getParameter("name");
		
		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,	"addChild", indexPath, actionContext)) {
			return;
		}
		
		Index parentIndex = Index.getIndexById(indexPath);
		Object obj = parentIndex.getIndexObject();
		ThingManager thingManager = null;
		String categoryName = null;
		
		if(obj instanceof ThingManager){
			thingManager = (ThingManager) obj;
			categoryName = name;
		}else if(obj instanceof Category){
			Category parentCategory = (Category) obj;
			thingManager = parentCategory.getThingManager();
			categoryName = parentCategory.getName() + "." + name; 
		}		
		
		String msg = null;
		boolean ok = false;
		String data = null;
		if(thingManager.getCategory(categoryName) != null){
			msg = "目录" + categoryName + "已经存在！";
		}else{
			if(thingManager.createCategory(categoryName)){
				Category category = thingManager.getCategory(categoryName);
				parentIndex.refresh();
				Index index = null;
				for(Index childIndex : parentIndex.getChilds()){
					if(childIndex.getIndexObject() == category){
						index = childIndex;
						break;
					}
				}
				
				if(index != null){
					data = "{type:'category', dataId:'" + category.getName() + 
							"', name:'" + name + "', id:'" + Index.getIndexId(index) +
							"',icon:'/icons/xworker/package.gif'}"; 
					ok = true;
				}else{
					msg = "创建目录发生错误，新目录的索引不存在！";
				}
			}else{
				msg = "创建目录" + categoryName + "失败！";
			}
		}
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + ok + ",\n"
				+ "\"msg\":\"" + msg + "\",\n" + "\"data\":"
				+ data + "\n" + "}";
		response.getWriter().println(code);
	}
	
	public static void deleteCategory(ActionContext actionContext) throws IOException{
		// 数据对象定义
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		String indexPath = request.getParameter("indexPath");
		
		String msg = null;
		boolean ok = false;
		String data = null;
		
		if (!SecurityManager.doCheck("WEB", PermissionConstants.XWOREKR_THING_MANAGER,	"addChild", indexPath, actionContext)) {
			return;
		}
		
		try{
			Index parentIndex = Index.getIndexById(indexPath);
			if(parentIndex == null){
				msg = "目录不存在，不能删除!";
			}else{
				Category category = (Category) parentIndex.getIndexObject();
				
				if(category.getThingManager().removeCategory(category.getName())){
					ok = true;
					data = "{id:'" + indexPath + "'}";
					
					parentIndex.getParent().refresh();
				}else{
					msg = "删除目录" + category.getName() + "失败";
				}
			}
		}catch(Exception e){
			msg = e.getMessage();
		}
		
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get("response");
		if (ServletFileUpload.isMultipartContent(request)) {
			// extjs提交文件是使用iframe，contentType必须是text/html
			response.setContentType("text/html; charset=utf-8");
		} else {
			response.setContentType("text/plain; charset=utf-8");
		}
		String code = "{\n" + "\"success\":" + ok + ",\n"
				+ "\"msg\":\"" + msg + "\",\n" + "\"data\":"
				+ data + "\n" + "}";
		response.getWriter().println(code);
	}
	
	public static void moveUp(ActionContext actionContext) throws IOException {

	}

	public static void moveDown(ActionContext actionContext) throws IOException {

	}
}
