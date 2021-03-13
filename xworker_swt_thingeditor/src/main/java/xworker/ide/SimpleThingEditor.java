package xworker.ide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.UtilResource;

import xworker.lang.executor.Executor;
import xworker.swt.app.View;
import xworker.swt.design.Designer;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

@ActionClass(creator="createInstance")
public class SimpleThingEditor {
	private static final String TAG = SimpleThingEditor.class.getName();
	/** 是否下载的资源，如果下载了需要重新启动　*/
	private static boolean downloaded = false;

	public void save() {
		workbench.save();
	}
	
	public void saveAll() {
		workbench.saveAll();
	}
	
	public void changeLanguage() {
		Action saveDialog = actionContext.getObject("saveDialog");
		
		//当有编辑器未保存时，不修改语言
		if(workbench.getEditorContainer() != null && workbench.getEditorContainer().isDirty()){
		    saveDialog.run(actionContext);
		    return;
		}

		boolean zh = true;

		Session session = SessionManager.getSession(null);
		Locale locale = session.getLocale();
		if(locale != null && !locale.getLanguage().equals(new Locale("en").getLanguage())){
		    zh = true;
		}else{
		    zh = false;
		}

		if(zh == true){
		    Locale l = new Locale("en", "US");
		    session.setLocale(l);
		    session.setI18nResource(UtilResource.getInstance(l));
		}else{
		    Locale l = new Locale("zh", "CN");
		    session.setLocale(l);
		    session.setI18nResource(UtilResource.getInstance(l));
		}

		//重新启动Workbench
		Thing thing =  workbench.getThing();

		//为避免系统退出，先建一个shell，保证不会触发最后一个Shell退出时系统退出
		Shell shell = new Shell(SWT.NONE);
		Display oldDisplay = shell.getDisplay();
		
		//重新创建一个Workbench
		ActionContext ac = new ActionContext();
		ac.put("parent", shell.getDisplay());
		thing.doAction("run", ac);
				
		//关闭之前的
		workbench.exit();
		shell.dispose();
		
		try{
		    if(oldDisplay.getShells().length == 0){
		        oldDisplay.dispose();
		    }
		}catch(Exception e){
		}
	}
	
	public Object restartSelection() {
		Action restart = actionContext.getObject("restart");
		
		if(workbench.getEditorContainer().isDirty()){
		    Action openSaveDialog = world.getAction("xworker.swt.app.prototypes.WorkbenchShell/@actions/@exit/@ActionDefined/@openSaveDialog");
		    openSaveDialog.run(actionContext);
		}else{
			//重新启动
		    restart.run(new ActionContext());
		    
		    shell.dispose();
		}

		return false;
	}
	
	public void resourceManagerSelection() {
		Action showMenu = actionContext.getObject("showMenu");
		Action openEditor = actionContext.getObject("openEditor");
		
		if(event.detail == SWT.ARROW){
		    showMenu.run(actionContext);
		}else{
		    openEditor.run(actionContext);
		}
	}
	
	public void onCreate() {
		if(XWorkerUtils.hasXWorker()){
		    //如果XWorker存在，默认打开主页
		    //openXWorkerHomeEditor(actionContext);
		}
	}
	
	public void init() {
		//启用设计器
		//Designer designer = Designer.getDesigner();
		Designer.setEnabled(true);

		//println "SimpleThingEditor inited.";
		try{
		    /*
		    if(shell == XWorkerUtils.getIDEShell()){
		        def thingManager = world.getThingManagers().get(0).getName();
		        ThingLibs.registLib(thingManager, "thingslib", "工作目录事物库");
		    }*/
		    
		    //启动注册缓存
		    ThingUtils.startRegistThingCache();

		    //外部的FileThingManager自动监听文件改动
		    Thing thing = world.getThing("_local.xworker.config.InnerThingManagers.dml");
		    if(thing != null){
		        for(ThingManager thingManager : world.getThingManagers()){
		            if(!(thingManager instanceof FileThingManager)){
		                continue;
		            }

		            String thName = thingManager.getName();             
		            for(Thing thingManagerThing : thing.getChilds("ThingManager")){
		                if(thName == thingManagerThing.getMetadata().getName()){
		                    ((FileThingManager) thingManager).setMonitor(true);
		                }             
		            }
		        }
		    }
		    
		    Thing actionsThing = world.getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor/@ActionContainer");
		    XWorkerUtils.getIde().getActionContainer().append(actionsThing);
		    
		    Action preventCtrlS = actionContext.getObject("preventCtrlS");
		    try {
		    	preventCtrlS.run(actionContext);
		    }catch(Exception e) {		    	
		    }		    
		}catch(Exception e){
		    Executor.info(TAG, "Init simple editor error", e);
		}
	}

    public static SimpleThingEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = SimpleThingEditor.class.getName();
        SimpleThingEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new SimpleThingEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    public void refreshProjectTree(ActionContext actionContext) {    	
    	View view = workbench.getView("SimpleThingEditor_XWokerExplorer");
    	view.doAction("refreshProjectTree");
    }

    public static void checkSWT(){
		World world = World.getInstance();
		//检查是否有swt的包
		try{
			Class.forName("org.eclipse.swt.SWT");
		}catch(Exception e){
			System.out.println("SWT not exists, downloading swt from XWorker, if failed please download swt jar from https://www.eclipse.org/swt/");

			String worldPath = world.getPath();
			String url = "https://www.xworker.org/files/os/lib/lib_" + world.getOS() + "_" + world.getJVMBit() + "/swt.jar";

			downloaded = true;
			if(download(url, worldPath + "/lib/swt.jar")) {
				System.out.println("Please add " + worldPath + "/lib/swt.jar as current project's library");
			}
		}
	}

	public static void checkColorer(){
		World world = World.getInstance();
		//检查config是否存在
		File catalogFile = new File(world.getPath() + "/config/colorer/catalog.xml");
		if(catalogFile.exists()){
		}else{
			System.out.println("Colorer config file not exists, download it form XWorker");
			try{
				URL url = new URL("https://www.xworker.org/files/xworker_swt_editor.zip");
				ZipInputStream zin = new ZipInputStream(url.openStream());
				ZipEntry entry = null;
				while((entry = zin.getNextEntry()) != null){
					File outFile = new File(world.getPath() + "/" + entry.getName());
					if(outFile.exists() == false) {
						if (entry.isDirectory()) {
							outFile.mkdirs();
						} else {
							outFile.getParentFile().mkdirs();

							byte[] bytes = new byte[4096];
							int length = -1;
							FileOutputStream fout = new FileOutputStream(world.getPath() + "/" + entry.getName());
							while((length = zin.read(bytes)) != -1) {
								fout.write(bytes, 0, length);
							}
							fout.close();

							System.out.println("Downloaded " + entry.getName());
						}
					}

					zin.closeEntry();
				}
				downloaded = true;
			}catch(IOException e){
				System.out.println("Download colorer config error");
				e.printStackTrace();
			}
		}

		//检测和下载colorer的动态库
		String name = world.getOS() + "_" + world.getJVMBit();
		Map<String, String> paths = new HashMap<String, String>();
		paths.put("linux_x86", "libnet_sf_colorer.so");
		paths.put("linux_x86_64", "libnet_sf_colorer.so");
		paths.put("macosx_ppc", "libnet_sf_colorer.jnilib");
		paths.put("macosx_x86", "libnet_sf_colorer.jnilib");
		paths.put("macosx_x86_64", "libnet_sf_colorer.jnilib");
		paths.put("win32_x86", "net_sf_colorer.dll");
		paths.put("win32_x86_64", "net_sf_colorer.dll");
		String dllName = paths.get(name);
		if(dllName != null){
			download("https://www.xworker.org/files/os/library/" + name + "/" + dllName, world.getPath() + "/library/" + dllName);
		}
	}

	public static boolean download(String url, String fileName){
		File targetFile = new File(fileName);
		if(targetFile.exists()){
			return true;
		}else {
			targetFile.getParentFile().mkdirs();
		}
		try {
			System.out.println("Downloading '" + url + "' to " + fileName);
			URL url_ = new URL(url);
			InputStream in = url_.openStream();
			FileOutputStream fout = new FileOutputStream(targetFile);
			byte[] bytes = new byte[4096];
			int length = -1;
			while((length = in.read(bytes)) != -1){
				fout.write(bytes, 0 , length);
			}
			fout.close();
			in.close();
			System.out.println("Download successes");
			downloaded = true;
			return true;
		} catch (IOException e) {
			System.out.println("Download failed");
			e.printStackTrace();
			return false;
		}
	}

    public static void check(){
		//检查xworker的目录是否存在，不存在则创建一个
		File xworkerRoot = new File(World.getInstance().getPath());
		if(xworkerRoot.exists() == false){
			xworkerRoot.mkdirs();
		}

		checkSWT();

		checkColorer();
	}
    
    World world = World.getInstance();
    
    @ActionField
    public ActionContext actionContext;
    
    @ActionField
    public Event event;
    
    @ActionField
    public Shell shell;
    
    @ActionField
    public xworker.swt.app.Workbench workbench;
    
	public static void run() throws MalformedURLException {
		//检查环境是否可以运行
		check();
		if(downloaded){
			String worldPath = World.getInstance().getPath();

			System.out.println("Please set JVM options -Djava.library.path=" + worldPath + "/library/ when start current program");
			System.out.println("Please restart ThingEditor");
			return;
		}

		World world = World.getInstance();
		//World.getInstance().getClassLoader().addClassPath(new File(world.getPath() + "/config/").toURI().toURL());
		Thing simpleEditor = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
		simpleEditor.doAction("run", new ActionContext());
	}
	
	public static void main(String[] args) {
		try {
			World world = World.getInstance();
			world.init(".");
						
			run();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
