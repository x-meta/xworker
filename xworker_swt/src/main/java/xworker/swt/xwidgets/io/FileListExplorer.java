package xworker.swt.xwidgets.io;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.io.FileFileLike;
import xworker.io.FileLike;
import xworker.io.SysRootFileLike;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileListExplorer {
    private static final String TAG = FileListExplorer.class.getName();

    List<FileListExplorerListener> listeners = new ArrayList<>();
    @ActionField
    public org.xmeta.Thing dataStore;
    @ActionField
    public org.eclipse.swt.widgets.Table table;
    @ActionField
    Thing self;
    @ActionField
    ActionContext actionContext;

    DataObjectList dataObjects;

    ExecutorService executorService;
    FileLike<?> dir;
    FileLike<?> rootDir = null;
    boolean sysRoots = false;

    public void init(){
        dataObjects = (DataObjectList) dataStore.get("records");
        table.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                TableItem item = (TableItem) selectionEvent.item;
                FileLike<?> fileLike = getFileLike(item);
                self.doAction("onSelection", actionContext, "file", fileLike);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                TableItem item = (TableItem) selectionEvent.item;
                FileLike<?> fileLike = getFileLike(item);

                self.doAction("onDefaultSelection", actionContext, "file", fileLike);

                if(fileLike.isDir()){
                    setDir(fileLike);
                }
            }
        });
        table.addDragDetectListener(new DragDetectListener() {
            @Override
            public void dragDetected(DragDetectEvent dragDetectEvent) {
                System.out.println("dragDetectEvent: " + dragDetectEvent);
            }
        });
    }

    private FileLike<?> getFileLike(TableItem item){
        DataObject data = (DataObject) item.getData();
        return (FileLike<?>) data.getData("source");
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Control getControl(){
        return table;
    }

    public void addListener(FileListExplorerListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeListener(FileListExplorerListener listener){
        listeners.remove(listener);
    }

    public void setDir(FileLike<?> dir){
        try {
            if(executorService != null){
                Executor.push(executorService);
            }
            this.dir = dir;

            dataObjects.clear();

            if (!dir.equals(rootDir) && (dir.getParentFile() != null || (sysRoots && !(dir instanceof SysRootFileLike)))) {
                FileLike<?> parent = dir.getParentFile();
                DataObject parentData = parent == null ? addFile(new SysRootFileLike()) : addFile(dir.getParentFile());
                parentData.set("name", "..");
            }

            List<? extends FileLike<?>> childs = dir.listFiles();
            Collections.sort(childs, new Comparator<FileLike<?>>() {
                @Override
                public int compare(FileLike o1, FileLike o2) {
                    if (o1.isDir() && !o2.isDir()) {
                        return -1;
                    } else if (!o1.isDir() && o2.isDir()) {
                        return 1;
                    }

                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (FileLike<?> file : childs) {
                addFile(file);
            }

            self.doAction("onSetDir", actionContext, "dir", dir);
            for(FileListExplorerListener listener : listeners){
                listener.onSetDir(this, dir);
            }
        }catch(Exception e){
            Executor.warn(TAG, "Set dir error", e);
        }finally {
            if(executorService != null){
                Executor.pop();
            }
        }
    }

    public void refresh(){
        if(dir != null){
            this.setDir(dir);
        }
    }

    public void clear(){
        dataObjects.clear();
    }

    public List<FileLike<?>> getSelection(){
        List<FileLike<?>> list = new ArrayList<>();
        TableItem[] items = table.getSelection();
        if(items != null ){
            for(TableItem item : items){
                DataObject data = (DataObject) item.getData();
                list.add((FileLike<?>) data.getData("source"));
            }
        }

        return list;
    }

    public void remove(FileLike<?> file){
        for(TableItem item : table.getItems()){
            FileLike<?> fileLike = getFileLike(item);
            if(file.getPath().equals(fileLike.getPath())){
                item.dispose();
            }
        }
    }

    public void update(FileLike<?> file){
        DataObject data = null;
        for(TableItem item : table.getItems()){
            FileLike<?> fileLike = getFileLike(item);
            if(file.getPath().equals(fileLike.getPath())){
                data = (DataObject) item.getData();
                break;
            }
        }

        update(file, data);
    }

    public void update(FileLike<?> file, DataObject data){
        if(file == null || data == null){
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        data.setData("source", file);
        String name = file.getName();
        if(name == null || name.isEmpty()){
            name = file.getPath();
        }
        data.put("name", name);
        if(file.isFile()){
            int index = name.lastIndexOf(".");
            if(index != -1){
                data.put("ext", name.substring(index + 1, name.length()));
            }
        }
        if (file.isDir()) {
            data.put("size", "<DIR>");
        }else{
            data.put("size", decimalFormat.format(file.getLength()));
        }
        if(file.getLastModified() > 0){
            data.put("date", sf.format(new Date(file.getLastModified())));
        }
        data.put("permission", file.getPermission());
        data.put("group", file.getGroup());
        data.put("owner", file.getOwner());
        try {
            data.put("icon", file.getIcon());
        }catch (Exception e){
        }
    }
    public DataObject addFile(FileLike<?> file){
        DataObject data = new DataObject("xworker.swt.xwidgets.io.prototypes.FileListExplorerShell/@table/@dataStore/@dataObjects/@AbstractDataObject");
        update(file, data);
        dataObjects.add(data);

        return data;
    }

    public FileLike<?> getDir() {
        return dir;
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //创建控件
        ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
        cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.io.prototypes.FileListExplorerShell/@table"));
        FileListExplorer fileListExplorer = new FileListExplorer();
        Object object = cc.create(fileListExplorer);
        fileListExplorer.self = self;
        fileListExplorer.actionContext = actionContext;
        fileListExplorer.init();

        File rootDir = self.doAction("getRootDir", actionContext);
        if(rootDir != null){
            FileLike<File> fileLike = new FileFileLike(rootDir);
            fileListExplorer.setDir(fileLike);
            fileListExplorer.rootDir = fileLike;
        }else {
            FileLike<?> rootFileLikeDir = self.doAction("getRootFileLikeDir", actionContext);
            if(rootFileLikeDir != null){
                fileListExplorer.setDir(rootFileLikeDir);
                fileListExplorer.rootDir = rootFileLikeDir;
            }else if(UtilData.isTrue(self.doAction("isSysRoots", actionContext))){
                fileListExplorer.sysRoots = true;

                for(File file : File.listRoots()){
                    fileListExplorer.addFile(new FileFileLike(file));
                }
            }
        }

        actionContext.g().put(self.getMetadata().getName(), fileListExplorer);

        return object;
    }

}
