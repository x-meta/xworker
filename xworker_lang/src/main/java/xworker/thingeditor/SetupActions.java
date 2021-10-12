package xworker.thingeditor;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.zip.ZipInputStream;

public class SetupActions {
    public static Object init(ActionContext actionContext){
        //找到startup的包
        File libDir = new File("./lib/mvn/");
        for(File file : Objects.requireNonNull(libDir.listFiles())){
            if(file.getName().startsWith("xworker_startup")){
                return file.getName();
            }
        }

        return "no startup";
    }

    public static void setup(ActionContext actionContext) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> Setup = Class.forName("xworker.startup.Setup");
        Class<?> Startup = Class.forName("xworker.startup.Startup");

        //环境
        Method initOS = Startup.getMethod("initOS");
        initOS.invoke(null);

        Method getOS = Startup.getMethod("getOS");
        String os = (String) getOS.invoke(null);

        World world = World.getInstance();
        Thing swt = world.getThing("xworker.thingeditor.SwtThingEditor");
        Thing web = world.getThing("xworker.webserver.WebThingEditor");
        Thing javafx = world.getThing("xworker.javafx.thingeditor.JavaFXThingEditor");

        //解压资源，如果存在
        System.out.println("Unzip resources and execute init");
        XWorkerUtils.setup();

        System.out.println("Os is " + os);
        //生成各种启动脚本文件
        if(os.contains("win32")){
            Action dml_cmd = actionContext.getObject("dml_cmd");
            Action dml_cmd_swt = actionContext.getObject("dml_cmd_swt");
            Action dml_cmd_web = actionContext.getObject("dml_cmd_web");
            Action dml_cmd_javafx = actionContext.getObject("dml_cmd_javafx");

            //windows
            dml_cmd.run(actionContext);
            if(swt != null){
                dml_cmd_swt.run(actionContext);
            }
            if(web != null){
                dml_cmd_web.run(actionContext);
            }
            if(javafx != null){
                dml_cmd_javafx.run(actionContext);
            }
        }else{
            Action dml_sh = actionContext.getObject("dml_sh");
            Action dml_sh_swt = actionContext.getObject("dml_sh_swt");
            Action dml_sh_web = actionContext.getObject("dml_sh_web");
            Action dml_sh_javafx = actionContext.getObject("dml_sh_javafx");

            //其它操作系统
            dml_sh.run(actionContext);
            if(swt != null){
                dml_sh_swt.run(actionContext);
            }
            if(web != null){
                dml_sh_web.run(actionContext);
            }
            if(javafx != null){
                dml_sh_javafx.run(actionContext);
            }
        }

        //生成setup文件
        System.out.println("Execute Setup.setup()");
        try {
            Method setup = Setup.getMethod("setup");
            setup.invoke(null);
        }catch(Exception e){
            e.printStackTrace();
        }

        System.exit(0);
    }

}
