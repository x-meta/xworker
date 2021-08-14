package xworker.util.autoplay;

import org.xmeta.Thing;
import xworker.thingeditor.ThingEditorUtils;
import xworker.util.Callback;
import xworker.util.XWorkerUtils;
import xworker.workbench.IEditor;

/**
 * 自动创建模型，可以用于演示。
 */
public class ThingAutoCreator {
    Thing thing;
    long sleepTime = 1000;

    public ThingAutoCreator(Thing thing){
        this.thing = thing;
    }

    public void run(){
        try {
            Thing newThing = new Thing(thing.getDescriptor());
            XWorkerUtils.ideOpenThing(newThing);

            IEditor<?, ?, ?> thingEditor = null;
            while (thingEditor == null) {
                thingEditor = XWorkerUtils.getWorkbench().getEditor("thing:" + newThing.getMetadata().getPath());

                Thread.sleep(sleepTime);
            }

            //目前应该是选中的根节点

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void copy(IEditor<?,?,?> thingEditor, Thing oldThing, Thing newThing){
        ThingEditorUtils.selectNode(thingEditor, getNewThing(oldThing, newThing), new TaskCallback(()->{
            ThingEditorUtils.setValues(thingEditor, oldThing.getAttributes(), new TaskCallback(()->{
                ThingEditorUtils.save(thingEditor, new TaskCallback(() ->{
                    newThing.getMetadata().setId(oldThing.getMetadata().getId());
                }, sleepTime));
            }, sleepTime));
        }, sleepTime));
    }

    public Thing getNewThing(Thing oldThing, Thing newThing){
        Thing newRootThing = newThing.getRoot();
        if(oldThing.getParent() == null){
            return newRootThing;
        }

        String path = "/@" + oldThing.getMetadata().getId();
        Thing parent = oldThing.getParent();
        while(parent != null){
            path = "/@" + parent.getMetadata().getId() + path;

            parent = parent.getParent();
        }

        return newRootThing.getThing(path);
    }

    static class TaskCallback implements Callback<Object, Void> {
        Runnable runnable;
        long sleepTime;

        public TaskCallback(Runnable runnable, long sleepTime){
            this.runnable = runnable;
            this.sleepTime = sleepTime;
        }

        @Override
        public Void call(Object o) {
            new Thread(()->{
                try {
                    Thread.sleep(sleepTime);

                    runnable.run();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }).start();

            return null;
        }
    }
}
