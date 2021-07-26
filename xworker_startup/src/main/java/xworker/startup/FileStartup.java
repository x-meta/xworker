package xworker.startup;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 从一个配置文件中启动模型应用。用于在XWorker的编辑器中使用编辑器的环境启动一个应用等。
 */
public class FileStartup {
    public static void startup(File file, String[] args) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String line = null;
            List<URL> classPaths = new ArrayList<>();
            String thingPath = null;
            String action = "run";
            String worldPath = "./xworker";
            String mainClass = null;

            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty()){
                    continue;
                }

                int index = line.indexOf("=");
                if(index == -1){
                    continue;
                }

                String key = line.substring(0, index).trim().toLowerCase();
                String value = line.substring(index + 1, line.length()).trim();

                if("thing".equals(key)){
                    thingPath = value;
                }else if("classpath".equals(key)){
                    if(value.contains("://")) {
                        //有可能是http等其它协议的URL
                        classPaths.add(new URL(value));
                    }else{
                        classPaths.add(new File(value).toURI().toURL());
                    }
                }else if("action".equals(key)){
                    action = value;
                }else if("world".equals(key)){
                    worldPath = value;
                }else if("mainclass".equals(key)){
                    mainClass = value;
                }
            }

            URL[] urls = new URL[classPaths.size()];
            classPaths.toArray(urls);

            URLClassLoader classLoader = new URLClassLoader(urls);

            //System.out.println("init libs: " + (System.currentTimeMillis() - start));
            Thread.currentThread().setContextClassLoader(classLoader);
            if(thingPath != null && !thingPath.isEmpty()) {
                //运行模型或模型文件
                Class<?> trCls = classLoader.loadClass("org.xmeta.util.ThingRunner");
                Method method = trCls.getDeclaredMethod("run", String[].class);

                //System.out.println("get ThingRunner: " + (System.currentTimeMillis() - start));
                String[] newArgs = null;
                if(args != null){
                    newArgs = new String[args.length + 3];
                }else{
                    newArgs = new String[3];
                }
                newArgs[0] = worldPath;
                newArgs[1] = thingPath;
                newArgs[2] = action;

                if(args != null){
                    for(int i=0; i<args.length; i++){
                        newArgs[i + 3] = args[i];
                    }
                }
                method.invoke(null, new Object[]{newArgs});
            }else if(mainClass != null && !mainClass.isEmpty()){
                //运行类
                Class<?> trCls = classLoader.loadClass(mainClass);
                Method method = trCls.getDeclaredMethod("main", String[].class);
                method.invoke(null, new Object[]{args});
            }
        }finally {
            fin.close();
        }
    }

    public static void main(String args[]){
       //File file = new File(args[0]);
        if(args == null || args.length == 0){
            System.out.println("Can not startup, need config file");
            return;
        }

       try {
           File file = new File(args[0]);

           String[] newArgs = new String[args.length - 1];
           System.arraycopy(args, 1, newArgs, 0, args.length - 1);
           startup(file, newArgs);
       }catch(Exception e){
           e.printStackTrace();
       }
    }
}
