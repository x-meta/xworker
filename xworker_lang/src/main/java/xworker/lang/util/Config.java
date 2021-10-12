package xworker.lang.util;

import org.xmeta.Thing;
import xworker.util.XWorkerUtils;

public class Config {
    public static void setConfig(String configName, String path, String value){
        Thing config = XWorkerUtils.getThingIfNotExistsCreate("_local.xworker.config." + configName,
                "_local", "xworker.lang.util.Config");
        synchronized (config){
            String[] paths = path.split("[.]");
            for (String s : paths) {
                Thing c = null;
                for (Thing child : config.getChilds()) {
                    if (child.getMetadata().getName().equals(s)) {
                        c = child;
                        break;
                    }
                }

                if(c == null){
                    c = new Thing("xworker.lang.util.Config");
                    c.put("name", s);
                    config.addChild(c);
                }

                config = c;
            }

            config.set("value", value);
            config.save();
        }
    }

    public static String getConfig(String configName, String path){
        Thing config = XWorkerUtils.getThingIfNotExistsCreate("_local.xworker.config." + configName,
                "_local", "xworker.lang.util.Config");
        String[] paths = path.split("[.]");
        for (String s : paths) {
            if (config == null) {
                return null;
            }

            Thing c = null;
            for (Thing child : config.getChilds()) {
                if (child.getMetadata().getName().equals(s)) {
                    c = child;
                    break;
                }
            }

            config = c;
        }

        if(config != null){
            return config.getStringBlankAsNull("value");
        }else{
            return null;
        }
    }
}
