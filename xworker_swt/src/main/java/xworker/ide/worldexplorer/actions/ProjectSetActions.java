package xworker.ide.worldexplorer.actions;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.UtilFile;

import xworker.lang.actions.ActionContainer;
import xworker.swt.design.Designer;
import xworker.util.XWorkerUtils;

public class ProjectSetActions {
	private static Logger logger = LoggerFactory.getLogger(ProjectSetActions.class);
	
	public static void initThingManager(World world, Thing self, Thing child, ActionContext actionContext){
		String dir = child.getStringBlankAsNull("dir");
		if(dir != null){
			File file = new File(dir);
			if(file.exists() && file.isDirectory()){
				boolean isWorkspace = child.getBoolean("isWorkspace");
				if(isWorkspace){
					logger.info("Add workspace " + dir);
					String exclude = child.getStringBlankAsNull("exclude");
					for(File childFile : file.listFiles()){
						if(childFile.isDirectory()){
							if(exclude != null && childFile.getName().matches(exclude)){
								logger.info("Directory is excluded : " + childFile.getAbsolutePath());
								continue;
							}
							try{
								world.initThingManager(childFile, UtilFile.getThingManagerNameByDir(childFile));
							}catch(Exception e){		
								logger.warn("ProjectSet add project error, file=" + childFile.getPath() 
										+ ",path=" + self.getMetadata().getPath(), e);
							}
						}
					}
				}else{
					try{
						if(child.getBoolean("isThingRoot")){
							String name = child.getMetadata().getName();
							if(world.getThingManager(name) == null){
								FileThingManager thingManager = new FileThingManager(name, file, false);
								thingManager.setRootDir(file);
								world.addThingManager(thingManager);
							}else{
								logger.info("ThingManager <" + name + "> already exists");
							}								
						}else{
							world.initThingManager(file, child.getMetadata().getName());
						}
					}catch(Exception e){								
						logger.warn("ProjectSet add project error, file=" + file.getPath() 
								+ ",path=" + self.getMetadata().getPath(), e);
					}
				}
			}else{
				logger.warn("ProjectSet file not exists or is not a file, file=" + dir 
						+ ", path=" + self.getMetadata().getPath());
			}
		}
	}
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		for(Thing child : self.getChilds("ProjectDir")){
			initThingManager(world, self, child, actionContext);
		}
		
		//刷新事物管理器的项目导航
		if(XWorkerUtils.getIdeActionContainer() != null){
			XWorkerUtils.getIdeActionContainer().doAction("refreshProjectTree");
		}
	}
	
	public static void addProject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		boolean have = false;
		File projectFile = (File) actionContext.get("projectFile"); 
		Thing proj = null;
		for(Thing child : self.getChilds("ProjectDir")){
			String dir = child.getStringBlankAsNull("dir");
			if(dir != null){
				File file = new File(dir);
				if(file.equals(projectFile)){
					have = true;
					proj = child;
					break;
				}
			}
		}
		
		if(!have){
			Thing project = new Thing("xworker.ide.worldexplorer.things.ProjectSet/@ProjectDir");
			project.put("name", projectFile.getName());
			project.put("dir", projectFile.getAbsolutePath());
			if(actionContext.get("initAttributes") != null){
				Map<String, Object> initAttributes = actionContext.getObject("initAttributes");
				project.putAll(initAttributes);
			}
			Boolean isWorkspace = (Boolean) actionContext.get("isWorkspace");
			if(isWorkspace != null && isWorkspace == true){
				project.put("isWorkspace", "true");
			}
			self.addChild(project);
			self.save();
			
			//初始化项目
			initThingManager(World.getInstance(), self, project, actionContext);
			
			//刷新ProjecTree，如果存在
			ActionContainer ac = Designer.getExplorerActions();
			if(ac != null){
				Designer.getExplorerDisplay().asyncExec(new Runnable(){
					public void run(){
						Designer.getExplorerActions().doAction("refreshProjectTree");
					}
				});				
			}
		}else if(proj.getBoolean("isThingRoot") && World.getInstance().getThingManager(proj.getMetadata().getName()) == null){
			//项目还未初始化
			//初始化项目
			initThingManager(World.getInstance(), self, proj, actionContext);
			
			//刷新ProjecTree，如果存在
			ActionContainer ac = Designer.getExplorerActions();
			if(ac != null){
				Designer.getExplorerDisplay().asyncExec(new Runnable(){
					public void run(){
						Designer.getExplorerActions().doAction("refreshProjectTree");
					}
				});				
			}
		}else{
			logger.info("Project file alrady exists.");
		}
	}
}
