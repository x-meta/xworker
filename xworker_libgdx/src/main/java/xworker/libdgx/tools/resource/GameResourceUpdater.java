package xworker.libdgx.tools.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

public class GameResourceUpdater {
	private static Logger logger = LoggerFactory.getLogger(GameResourceUpdater.class);
	
	/** 文件类型 */
	private static Map<String, Thing> fileTypes = new HashMap<String, Thing>();
	
	private static boolean running = false;
	
	/**
	 * 更新文件资源。
	 * 
	 * @param file
	 * @throws IOException 
	 */
	public static void updateResource(File file, ActionContext actionContext) throws IOException{
		if(running){
			throw new ActionException("Update game resource is running...");
		}
		running = true;
		try{
			initFileTypes();
			
			Thing taskThing = World.getInstance().getThing("xworker.libgdx.tools.resource.UpdateResourceTask");
			UserTask task = UserTaskManager.createTask(taskThing, true);
			int totalFileCount = getAllFileCount(file);
			task.setTotalJobs(totalFileCount);
			task.start();
			
			//更新日志
			Map<String, Long> log = loadUpdateLog();
					
			task.setLabel("process " + file.getAbsolutePath());
			//先查看父目录是否已经创建
			long parentId = getParentId(file, actionContext);
			
			//更新文件内容
			try{
				updateResource(log, task, parentId, file, actionContext);
			}finally{			
				task.finished();
				saveUpdateLog(log);
			}
		}finally{
			running = false;
		}
	}
	
	public static void viewContent(long resourceId, Composite viewComposite, ActionContext actionContext){
		DataObject resObj = DataObjectUtil.load("xworker.libgdx.tools.dataobjects.Resource", resourceId, actionContext);
		if(resObj != null){
			Thing typeThing = fileTypes.get(resObj.getString("type"));
			if(typeThing != null){
				typeThing.doAction("preview", actionContext, UtilMap.toMap("parentComposite", viewComposite, "resource", resObj));
			}
			
		}
	}
	
	public static Map<String, Long> loadUpdateLog() throws IOException{
		World world = World.getInstance();
		File file = new File(world.getPath() + "/work/xworker_game/resource.log");
		if(file.exists()){
			FileInputStream fin = new FileInputStream(file);
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				String line = null;
				Map<String, Long> log = new HashMap<String, Long>();
				while((line = br.readLine()) != null){
					line = line.trim();
					if(line.equals("")){
						continue;
					}
					
					String s[] = line.split("[|]");
					log.put(s[0], Long.parseLong(s[1]));
				}
				return log;
			}finally{
				fin.close();
			}			
		}else{
			return new HashMap<String, Long>();
		}
	}
	
	public static void saveUpdateLog(Map<String, Long> log) throws IOException{
		World world = World.getInstance();
		File file = new File(world.getPath() + "/work/xworker_game/resource.log");
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			for(String key : log.keySet()){
				String line = key + "|" + log.get(key) + "\n";
				fout.write(line.getBytes());
			}
		}finally{
			fout.close();
		}
	}
	
	public static int getAllFileCount(File file){
		int count = 1;
		
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				count = count + getAllFileCount(child);
			}
		}
		
		return count;
	}
	
	public static void initFileTypes(){
		if(fileTypes.get("fileTypes") != null){
			//暂时改为只初始化一次
			return;
		}
		
		Thing fileTypeThing = World.getInstance().getThing("xworker.libgdx.tools.resource.FileTypes/@FileReisgts");
		World world = World.getInstance();
		
		if(fileTypeThing != null){
			for(Thing child : fileTypeThing.getChilds()){
				String name = child.getMetadata().getName().toLowerCase();
				String path = child.getStringBlankAsNull("fileType");
				Thing fileType = world.getThing(path);
				if(fileType != null){
					fileTypes.put(name, fileType);
				}
			}
			
			fileTypes.put("fileTypes", fileTypeThing);
		}
	}
	
	public static void updateResource(Map<String, Long> log, UserTask task, long parentId, File file, ActionContext actionContext){
		task.setLabel("process " + file.getAbsolutePath());		
		task.setDetail("Total Files : " + task.getTotalJobs() + ", completed: " + task.getCompleteJobs());
		if(task.isStoped()){
			task.finished();
			return;
		}
		
		//更新到目录树
		if(file.isDirectory()){
			task.completeJobs(1);
			long id = createOrUpdateTree(parentId, file, actionContext);		
			//如果是目录，更新下级文件
			for(File child : file.listFiles()){
				updateResource(log, task, id, child, actionContext);
			}
		}else{
			try{
				String path = file.getAbsolutePath();
				if(log.get(path) != null && log.get(path) == file.lastModified()){
					return;
				}
				
				long id = createOrUpdateTree(parentId, file, actionContext);		
				String fileName = file.getName();
				int index = fileName.lastIndexOf(".");
				if(index != -1){
					String type = fileName.substring(index + 1, fileName.length());
					
					Thing fileType = fileTypes.get(type);
					if(fileType == null){
						type = "file";
						fileType= fileTypes.get(type);
					}
					if(fileType != null){
						fileType.doAction("updateResource", actionContext, UtilMap.toMap("treeParentId", parentId, "treeId", id, 
								"file", file, "type", type));
					}
				}
				
				log.put(path, file.lastModified());
			}catch(Exception e){
				logger.error("Game resource update file error: file=" + file, e);
			}finally{
				task.completeJobs(1);
			}
		}
	}
	
	public static Thing getFileType(String type){
		if(type == null){
			return null;
		}
		
		type = type.toLowerCase();
		return fileTypes.get(type);
	}
	
	public static long getParentId(File file, ActionContext actionContext){
		List<File> files = new ArrayList<File>();
		
		while((file = file.getParentFile()) != null){
			files.add(0, file);
		}
		
		long id = 0;
		for(File f : files){
			id  = createOrUpdateTree(id, f, actionContext);
		}
		
		return id;
	}
	
	public static long createOrUpdateTree(long parentId, File file, ActionContext actionContext){
		String name = file.getName();
		if(name == null || "".equals(name)){
			name = file.getPath();
		}
		return createOrUpdateTree(parentId, name, file.isDirectory() ? "dir" : "file", file.lastModified(), actionContext);
	}
	
	public static long createOrUpdateTree(long parentId, String name, String type, long lastModified, ActionContext actionContext){
		List<DataObject> objs = DataObjectUtil.query("xworker.libgdx.tools.dataobjects.ResourceTree", 
				UtilMap.toMap("parentId", parentId, "name", name), actionContext);
		if(objs.size() > 0){
			//存在
			DataObject obj = objs.get(0);
			return obj.getLong("id");
		}else{
			//不存在
			DataObject obj = new DataObject("xworker.libgdx.tools.dataobjects.ResourceTree");
			obj.put("parentId", parentId);
			obj.put("name", name);
			obj.put("type", type);
			obj.put("lastModified", new Date(lastModified));
			obj = obj.create(actionContext);
			return obj.getLong("id");
		}
	}
}
