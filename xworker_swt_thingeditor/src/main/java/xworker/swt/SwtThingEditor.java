package xworker.swt;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.startup.Startup;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SwtThingEditor {
    /** 是否下载的资源，如果下载了需要重新启动　*/
    private static boolean downloaded = false;

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
            //检查系统里是否设置了动态库
            String path = System.getProperty("java.library.path");
            if(path == null || !path.contains("library")){
                String worldPath = World.getInstance().getPath();

                System.out.println("Please set JVM options -Djava.library.path=" + worldPath + "/library/ when start current program");
            }
        }else{
            System.out.println("Colorer config file not exists, download it form XWorker");
            try{
                URL url = new URL("https://www.xworker.org/files/xworker_swt_editor.zip");
                ZipInputStream zin = new ZipInputStream(url.openStream());
                ZipEntry entry = null;
                while((entry = zin.getNextEntry()) != null){
                    File outFile = new File(world.getPath() + "/" + entry.getName());
                    if(!outFile.exists()) {
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
                String worldPath = World.getInstance().getPath();

                System.out.println("Please set JVM options -Djava.library.path=" + worldPath + "/library/ when start current program");
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

    public static void unzipResource() throws IOException {
        String worldPath = World.getInstance().getPath();
        boolean ok = true;
        //检查文件是否存在
        File xmlFile = new File(worldPath + "/config/colorer/catalog.xml");
        if (!xmlFile.exists()) {
            try(InputStream resourceIn = World.getInstance().getResourceAsStream("xworker_swt.zip");
                ZipInputStream zin = new ZipInputStream(resourceIn)){
                XWorkerUtils.unzipToXWorker(zin, true);
            }
            ok = false;
        }

        //检查类库是否已经
        if(ok) {
            try {
                Class.forName("org.eclipse.swt.SWT");
            } catch (Exception e) {
                ok = false;
            }
        }

        if(!ok){
            String OS = Startup.getOS();
            String PROCESSOR_ARCHITECTURE = Startup.getPROCESSOR_ARCHITECTURE();
            System.out.println("If this program starts from a java project:");
            System.out.println("    Add JVM options -Djava.library.path=" + worldPath + "/os/library/"
                    + OS + "_" + PROCESSOR_ARCHITECTURE +"/ to Run/Debug Configuration");
            System.out.println("    Add jars in "  + worldPath + "/lib/; "  + worldPath + "/os/lib/lib_"
                    + OS + "_" + PROCESSOR_ARCHITECTURE +"/ to java project");

            System.out.println("Please restart the system.");
            System.exit(0);
        }
    }

    public static void check() throws IOException{
        //检查xworker的目录是否存在，不存在则创建一个
        File xworkerRoot = new File(World.getInstance().getPath());
        if(!xworkerRoot.exists()){
            xworkerRoot.mkdirs();
        }

        unzipResource();
        //checkSWT();

        //checkColorer();

        checkDir(new File(xworkerRoot, "databases"));
    }

    private static void checkDir(File dir){
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    /**
     * 运行模型管理器。
     */
    public static void run() throws IOException {
        run(true, null);
    }

    /**
     * 运行模型管理器。
     *
     * @param editorConfig 模型管理器额外的配置，使用xworker.swt.SwtThingEditorConfig模型编写
     */
    public static void run(Thing editorConfig) throws IOException {
        run(true, editorConfig);
    }

    /**
     * 运行模型管理器。
     *
     * @param check 是否检查已经下载了swt.jar
     * @param editorConfig 模型管理器额外的配置，使用xworker.swt.SwtThingEditorConfig模型编写
     */
    public static void run(boolean check, Thing editorConfig) throws IOException {
        if(check) {
            //检查环境是否可以运行
            check();
            if (downloaded) {
                //执行了下载，那么需要重启
                System.out.println("Please restart ThingEditor");
                System.exit(0);
                return;
            }
        }

        World world = World.getInstance();

        Thing simpleEditor = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
        ActionContext actionContext = new ActionContext();
        actionContext.put("simpleThingEditorConfig", editorConfig);
        simpleEditor.doAction("run", actionContext);
    }

    //xworker.thingeditor.SwtThingEditor/@run1
    public static void actionRun(ActionContext actionContext) throws IOException {
        Action startWebServer = actionContext.getObject("startWebServer");

        if(startWebServer != null){
            try{
                startWebServer.run(actionContext);
            }catch (Exception ignore){
            }
        }

        run();
    }

    public static void main(String[] args) {
        try {
            World world = World.getInstance();
            world.init("./xworker/");

            run();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
