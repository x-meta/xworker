package xworker.swt.design;

import org.eclipse.swt.dnd.DropTargetEffect;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Thing;
import org.xmeta.World;

public class DesignDropTargetEffect extends DropTargetEffect {
    Control insertedControl = null;
    int dropType;

    public DesignDropTargetEffect(Control control) {
        super(control);
    }

    private Thing getThing(DropTargetEvent event){
        return DesignDND.thing;
        /*
        if(event.data instanceof String){
            String data = (String) event.data;
            if(data.startsWith(DesignListener.DRAG_PREFIX)){
                String path = data.substring(DesignListener.DRAG_PREFIX.length());
                return World.getInstance().getThing(path);
            }
        }

        return null;*/
    }

    private int getType(DropTargetEvent event){
        Control control = getControl();
        int width = control.getSize().x;
        int height = control.getSize().y;
        int x = event.x;
        int y = event.y;

        if(DesignTools.isContainer(control)){
            //移动到容器类控件上
            if(control.getParent() == null){
                //没有父是Shell，只能插入
                return DesignTools.INSIDE;
            }else if(isIn(x, y, width / 4, height / 4, width / 2, height / 2)){
                //移动到中间位置，插入
                return DesignTools.INSIDE;
            }else if(isIn(x, y, 0, 0, width * 3 / 4, height * 3 / 4) || isIn(x, y, 0, 0, width / 4, height)){
                //移动到左边和上面，插入到前面
                return DesignTools.ABOVE;
            }else{
                //移动到右边下面，插入到后面
                return DesignTools.BELOW;
            }
        }else if(isIn(x, y, 0, 0, width * 3 / 4, height / 2) || isIn(x, y, 0, 0, width / 4, height)){
            //普通控件的左面和上面，插入到前面
            return DesignTools.ABOVE;
        }else{
            //普通控件的右面和后面，插入到后面
            return DesignTools.BELOW;
        }

    }

    private boolean isIn(int x, int y, int x1, int y1, int width, int height){
        return x >= x1 && x <= x1 + width && y >= y1 && y <= y1 + height;
    }

    @Override
    public void dragEnter(DropTargetEvent event) {
        if(getControl() != DesignDND.source) {
            boolean disableMessage = DesignTools.isDisableMessage();
            try {
                DesignTools.setDisableMessage(true);
                Thing thing = getThing(event);
                if (thing != null) {
                    //是设计器的拖拽
                    if (insertedControl == null) {
                        dropType = getType(event);
                        insertedControl = DesignTools.insert(getControl(), thing, dropType);
                    }
                }
            }finally {
                DesignTools.setDisableMessage(disableMessage);
            }
        }
    }

    @Override
    public void dragLeave(DropTargetEvent event) {
        //拖拽已经离开了，说明没有放到控件里
        if(insertedControl != null && !insertedControl.isDisposed()){
            DesignTools.remove(insertedControl, false);
            this.insertedControl = null;
            this.dropType = 0;
        }
    }

    @Override
    public void dragOver(DropTargetEvent event) {
        if(getControl() == DesignDND.source || insertedControl == null) {
            return;
        }
        Thing thing = getThing(event);
        if(thing != null){
            //是设计器的拖拽
            int newDropType = getType(event);
            if(newDropType != dropType){
                DesignTools.remove(insertedControl, false);
                dropType = newDropType;
                insertedControl = DesignTools.insert(getControl(), thing, dropType);
            }

        }
    }

    @Override
    public void drop(DropTargetEvent event) {
        this.insertedControl = null;
    }
}
