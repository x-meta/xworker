package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileRangeStore extends ThingRangeStore{
    private static final String TAG = FileRangeStore.class.getName();

    File file;

    public FileRangeStore(Thing thing, File file) {
        super(thing);

        this.file = file;
    }

    @Override
    public void save() {
        super.save();

        String xml = thing.toXML();
        try {
            FileOutputStream fout = new FileOutputStream(file);
            try {
                fout.write(xml.getBytes());
            } finally {
                fout.close();
            }
        }catch(Exception e){
            Executor.error(TAG, "Save range to file error, file=" + file, e);
        }
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        File file = self.doAction("getFile", actionContext);
        Thing storeThing = null;
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }

        storeThing = new Thing("xworker.lang.task.impls.ThingRangeStore");
        if(file.exists() && file.length() > 0){
            try{
                FileInputStream fin = new FileInputStream(file);
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);

                String xml = new String(bytes);
                storeThing.parseXML(xml);
            }catch(Exception e){
                throw new ActionException("Parse from file error, path=" + self.getMetadata().getPath(), e);
            }
        }

        return new FileRangeStore(storeThing, file);
    }
}
