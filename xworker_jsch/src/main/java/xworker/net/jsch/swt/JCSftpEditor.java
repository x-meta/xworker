package xworker.net.jsch.swt;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import org.xmeta.annotation.ActionParams;
import xworker.dataObject.DataObject;
import xworker.io.FileLike;
import xworker.lang.executor.Executor;
import xworker.swt.app.View;
import xworker.swt.xwidgets.io.FileListExplorer;
import xworker.swt.xwidgets.io.FileListExplorerListener;
import xworker.workbench.EditorParams;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCSftpEditor implements FileListExplorerListener {
    private static final String TAG = JCSftpEditor.class.getName();

    private boolean closeSession = false;
    /** 传入的会话参数，可以是DataObject、Thing或Session */
    private Object inputSession;
    private Session session;
    private Thing sessionThing;
    private ChannelSftp channelSftp;
    private String title;

    @ActionField
    public ActionContext actionContext;
    @ActionField
    public xworker.lang.executor.ExecutorService executorService;
    @ActionField
    public org.eclipse.swt.custom.StyledText logText;
    @ActionField
    public org.xmeta.Thing progressDataStore;
    @ActionField
    public xworker.swt.xwidgets.io.FileListExplorer localFileExplorer;
    @ActionField
    public org.eclipse.swt.widgets.Text localPathText;
    @ActionField
    public xworker.swt.xwidgets.io.FileListExplorer remoteFileExplorer;
    @ActionField
    public org.eclipse.swt.widgets.Text remotePathText;

    LsEntryFileLike rootDir;
    @ActionField
    public xworker.swt.app.Workbench workbench;

    public static EditorParams<Object> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Object session = null;
        String path = null;
        if(content instanceof Thing){
            Thing thing = (Thing) content;
            if(thing.isThing("xworker.net.jsch.Session")){
                session = thing;
                path = "jcsftp:" + path;
            }
        }else if(content instanceof  DataObject){
            DataObject dataObject = (DataObject) content;
            if(dataObject.getMetadata().getDescriptor().isThing("xworker.app.server.dataobjects.Server")){
                session = dataObject;
                path = "jcsftp:" + dataObject.getString("id");
            }
        }else if(content instanceof Session){
            session = content;
            path = "jcsftp:" + session;
        }
        if(session != null){
            return new EditorParams<Object>(self, path, session) {
                @Override
                public Map<String, Object> getParams() {
                    Map<String, Object> params = new HashMap<>();
                    params.put("session", this.getContent());

                    return params;
                }
            };
        }

        return null;
    }

    @ActionParams(names = "params")
    public void setContent(Map<String, Object> params){
        localFileExplorer.addListener(this);
        remoteFileExplorer.addListener(this);

        if(workbench != null){
            View progressView = (View) workbench.getView("progressView");
            if(progressView != null){
                progressDataStore = progressView.getActionContext().getObject("progressDataStore");
            }
        }

        Object pinputSession = params.get("session");

        if(pinputSession != this.inputSession) {
            //先停止之前的
            close();
        }

        this.inputSession = pinputSession;

        try {
            if(workbench != null && workbench.getLogService() != null){
                remoteFileExplorer.setExecutorService(workbench.getLogService());
                Executor.push(workbench.getLogService());
            }
            if (inputSession instanceof Thing) {
                title = ((Thing) inputSession).getMetadata().getLabel();
                start((Thing) inputSession);
            }else if(inputSession instanceof DataObject){
                DataObject dataObject = (DataObject) inputSession;
                title = dataObject.getString("name");
                if(title == null){
                    title = "Server: " + dataObject.getString("adminIP");
                }

                Thing sessionThing = (Thing) dataObject.doAction("getSessionThing", actionContext);
                if(sessionThing != null){
                    start(sessionThing);
                }else{
                    Executor.warn(TAG, "Can not get session from dataObject " + inputSession);
                }
            }else if(inputSession instanceof Session){
                title = ((Session) inputSession).getHost();
                start((Session) inputSession, false);
            }else{
                Executor.info(TAG, "Can not start Jsftp, unknown session type, session=" + inputSession);
            }
        }catch(Exception e){
            Executor.warn(TAG, "Start session error", e);
        }finally {
            if(workbench != null && workbench.getLogService() != null){
                Executor.pop();
            }
        }
    }

    public void start(Thing thing) throws JSchException {
        this.sessionThing = thing;

        Session session = sessionThing.doAction("create", actionContext);
        if (session != null) {
            start(session, true);
        }
    }

    public void restart() throws JSchException{
        close();

        Session session = sessionThing.doAction("create", actionContext);
        if (session != null) {
            start(session, true);
        }
    }

    public void start(Session session ,boolean closeSession) throws JSchException {
        this.session = session;
        this.closeSession = closeSession;

        channelSftp =  (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        actionContext.g().put("channelSftp", channelSftp);

        rootDir = new LsEntryFileLike(this, null, "/");
        remoteFileExplorer.setDir(rootDir);
    }

    public ChannelSftp getChannelSftp() throws JSchException{
        if(channelSftp == null || channelSftp.isClosed()){
            //重新连接
            if(!session.isConnected()){
                restart();
            }
        }

        return channelSftp;
    }

    @ActionParams(names = "params")
    public boolean isSameContent(Map<String, Object> params){
        Object session = params.get("session");
        return this.session == session;
    }

    public void close(){
        try {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }catch (Exception ignored){

        }

        try{
            if(session != null && closeSession){
                session.disconnect();
            }
        }catch(Exception ignored){

        }
    }

    public void doSave(){

    }

    public boolean isDirty(){
        return false;
    }

    public String getSimpleTitle(){
        return title;
    }

    public String getTitle(){
        return title;
    }

    public Image getIcon(){
        return null;
    }

    public Composite getOutline(){
        return null;
    }

    public void doDispose(){
        close();
    }

    public void onActive(){

    }

    public void onUnActive(){

    }

    @SuppressWarnings("unchecked")
    public void upload(){
        List<FileLike<?>> localFile = localFileExplorer.getSelection();
        FileLike<ChannelSftp.LsEntry> remoteDir = (FileLike<ChannelSftp.LsEntry>) remoteFileExplorer.getDir();


    }

    public void downLoad(){

    }

    //xworker.net.jsch.swt.JCSftpEditor/@EditorComposite/@topSashForm/@localComposite/@localFileExplorer/@DragSource/@localDragStart
    @ActionParams(names = "event")
    public void localDragStart(Event event){
        if(localFileExplorer.getSelection().size() == 0){
            event.doit = false;
        }
    }

    @ActionParams(names = "event")
    public void localDragSetData(Event event){
        System.out.println("localDragSetData:" + event);
        FileLike<?> localFile = localFileExplorer.getSelection().get(0);
        File file = (File) localFile.getSource();
        event.data = new String[]{file.getAbsolutePath()};
    }

    @ActionParams(names = "event")
    public void remoteDropAccept(Event event){
    }

    @ActionParams(names = "event")
    public void remoteDrop(Event event){
        if(event.data instanceof String[]){
            String[] datas = (String[]) event.data;
            if(datas.length > 0){
                final File file = new File(datas[0]);
                if(file.exists()) {
                    final LsEntryFileLike remoteDir = (LsEntryFileLike) remoteFileExplorer.getDir();
                    new Thread(() ->{
                        try {
                            if(workbench.getLogService() != null){
                                Executor.push(workbench.getLogService());
                            }

                            JCSFtpDirPutter getter = new JCSFtpDirPutter(channelSftp, remoteDir, file, new ProgressMonitor(progressDataStore));
                            getter.start();

                            remoteFileExplorer.refresh();
                        }catch(Exception e){
                            Executor.error(TAG, "Get file error, remote path:" + remoteDir.getPath(), e);
                        }finally {
                            if(workbench.getLogService() != null){
                                Executor.pop();
                            }
                        }
                    }).start();
                }
            }
        }
    }

    @ActionParams(names = "event")
    public void remoteDragStart(Event event){
        if(remoteFileExplorer.getSelection().size() == 0){
            event.doit = false;
        }
    }

    @ActionParams(names = "event")
    public void remoteDragSetData(Event event){
        FileLike<?> localFile = remoteFileExplorer.getSelection().get(0);
        event.data = new String[]{"lsEntry:" + localFile.getName() + ":" + localFile.getPath()};
    }

    @ActionParams(names = "event")
    public void localDropAccept(Event event){
    }

    @ActionParams(names = "event")
    public void localDrop(Event event){
        if(event.data instanceof String[]){
            String[] datas = (String[]) event.data;
            if(datas.length > 0 && datas[0].startsWith("lsEntry:")){
                LsEntryFileLike remoteDir = (LsEntryFileLike) remoteFileExplorer.getSelection().get(0);
                File localDir = (File) localFileExplorer.getDir().getSource();
                new Thread(() ->{

                    try {
                        if(workbench.getLogService() != null){
                            Executor.push(workbench.getLogService());
                        }

                        JCSftpDirGetter getter = new JCSftpDirGetter(channelSftp, remoteDir, localDir, new ProgressMonitor(progressDataStore));
                        getter.start();

                        localFileExplorer.refresh();
                    }catch(Exception e){
                        Executor.error(TAG, "Get file error, remote path:" + remoteDir.getPath(), e);
                    }finally {
                        if(workbench.getLogService() != null){
                            Executor.pop();
                        }
                    }
                }).start();

            }

        }
    }

    @ActionParams(names="text")
    public void localRename(String text){
        try {
            FileLike<?> localFile = localFileExplorer.getSelection().get(0);
            File file = (File) localFile.getSource();
            file.renameTo(new File(file.getParentFile(), text));

            localFileExplorer.refresh();
        }catch(Exception e){
            Executor.error(TAG, "Rename local file error", e);
        }
    }

    public void localDelete(){
        try {
            FileLike<?> localFile = localFileExplorer.getSelection().get(0);
            File file = (File) localFile.getSource();
            if(file.isDirectory()){
                FileUtils.deleteDirectory(file);
            }else{
                FileUtils.deleteQuietly(file);
            }

            localFileExplorer.remove(localFile);
        }catch(Exception e){
            Executor.error(TAG, "Delete local file error", e);
        }

    }

    @ActionParams(names="text")
    public void remoteRename(String text){
        try {
            FileLike<?> remoteFile = remoteFileExplorer.getSelection().get(0);
            String oldPath = remoteFile.getPath();
            String newPath = text;
            int index = oldPath.lastIndexOf("/");
            if(index != -1){
                newPath = oldPath.substring(0, index + 1) + text;
            }
            channelSftp.rename(oldPath, newPath);

            remoteFileExplorer.refresh();
        }catch(Exception e){
            Executor.error(TAG, "Rename remote file error", e);
        }
    }

    public void remoteDelete(){
        try {
            FileLike<?> remoteFile = remoteFileExplorer.getSelection().get(0);
            if (remoteFile.isDir()) {
                channelSftp.rmdir(remoteFile.getPath());
            } else {
                channelSftp.rm(remoteFile.getPath());
            }

            remoteFileExplorer.remove(remoteFile);
        }catch(Exception e){
            Executor.error(TAG, "Delete remote file error", e);
        }
    }

    @Override
    public void onSetDir(FileListExplorer fileListExplorer, FileLike<?> fileLike) {
        localPathText.getDisplay().asyncExec(() ->{

            if(fileListExplorer == this.localFileExplorer){
                localPathText.setText(fileLike.getPath());
            }else{
                remotePathText.setText(fileLike.getPath());
            }
        });
    }

    static class ProgressMonitor implements SftpProgressMonitor{
        xworker.app.view.swt.data.DataStore dataStore;
        DataObject data = null;

        String src;
        String dest;
        long max;
        int lastProgress = 0;
        public ProgressMonitor(Thing dataStore){
            if(dataStore != null) {
                this.dataStore = xworker.app.view.swt.data.DataStore.getDataStore(dataStore);
            }
        }

        @Override
        public void init(int op, String src, String dest, long max) {
            this.src = src;
            this.dest = dest;
            if(dataStore != null) {
                data = new DataObject("xworker.net.jsch.swt.JCTermWorkbench/@Views/@progressView/@Composite/@progressTable/@progressDataStore/@dataObjects/@AbstractDataObject");
                this.max = max;
                if (op == SftpProgressMonitor.GET) {
                    data.put("type", "download");
                } else {
                    data.put("type", "upload");
                }
                data.put("srcFileName", src);
                data.put("targetFile", dest);
                data.put("progress", 0);
                lastProgress = 0;

                //添加到表格中
                dataStore.getDatas().add(data);
            }else{
                Executor.info(TAG, "Copy " + src + " to " + dest);
            }
        }

        @Override
        public boolean count(long count) {
            int progress = (int) (100d * count / max);
            if(progress != lastProgress) {
                if(data != null) {
                    data.put("progress", progress);
                }
                lastProgress = progress;
            }
            if(data != null) {
                return true;
            }else{
                Executor.info(TAG, "copy : " + src + " " + progress + "%");
                return true;
            }
        }

        @Override
        public void end() {
            if(data != null) {
                data.put("progress", 100);
            }else{
                Executor.info(TAG, "Copy " + src + " end");
            }
        }
    }
}
