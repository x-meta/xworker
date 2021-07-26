package xworker.net.jsch.swt;

import com.jcraft.jsch.ChannelSftp;
import xworker.io.FileLike;
import xworker.lang.executor.Executor;
import xworker.util.UtilFileIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LsEntryFileLike implements FileLike<ChannelSftp.LsEntry> {
    private static final String TAG = LsEntryFileLike.class.getName();

    LsEntryFileLike parent;

    ChannelSftp.LsEntry file;
    ChannelSftp channelSftp;
    //完整路径
    String path;

    public LsEntryFileLike(ChannelSftp channelSftp, ChannelSftp.LsEntry file, String path){
        this.channelSftp = channelSftp;
        this.file = file;
        this.path = path;
    }

    @Override
    public String getName() {
        if(file == null){
            return path;
        }

        return file.getFilename();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public long getLength() {
        return file == null ? 0 : file.getAttrs().getSize();
    }

    @Override
    public ChannelSftp.LsEntry getSource() {
        return file;
    }

    @Override
    public long getLastModified() {
        return file == null ? 0 : 1000L * file.getAttrs().getMTime();
    }

    @Override
    public boolean isDir() {
        return file == null || file.getAttrs().isDir();
    }

    @Override
    public boolean isFile() {
        return file != null && !file.getAttrs().isDir();
    }

    @Override
    public String getIcon() throws IOException {
        if(file == null){
            return "icons/computer.png";
        }

        String fileName = file.getFilename();
        if(file.getAttrs().isDir()){
            return "icons/folder.png";
        }else{
            String ext = null;
            int index = fileName.lastIndexOf(".");
            if(index != -1){
                ext = fileName.substring(index + 1, fileName.length());
            }
            if(ext != null) {
                return UtilFileIcon.getFileIcon(ext, false);
            }
        }

        return null;
    }

    @Override
    public String getBigIcon() throws IOException {
        if(file == null){
            return "icons/computer.png";
        }

        String fileName = file.getFilename();
        if(file.getAttrs().isDir()){
            return "icons/folder.png";
        }else{
            String ext = null;
            int index = fileName.lastIndexOf(".");
            if(index != -1){
                ext = fileName.substring(index + 1, fileName.length());
            }
            if(ext != null) {
                return UtilFileIcon.getFileIcon(ext, true);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FileLike<ChannelSftp.LsEntry>> listFiles() {
        List<FileLike<ChannelSftp.LsEntry>> files = new ArrayList<>();
        if(file == null || file.getAttrs().isDir()) {
            try{
                Vector<ChannelSftp.LsEntry> items = (Vector<ChannelSftp.LsEntry>) channelSftp.ls(path);
                for(ChannelSftp.LsEntry entry : items){
                    Executor.info(TAG, entry.toString());
                    String name = entry.getFilename();
                    if(name.equals(".") || name.equals("..")){
                        continue;
                    }

                    LsEntryFileLike childFile = new LsEntryFileLike(channelSftp, entry, path +
                            (!"/".equals(path) ? "/" : "") + entry.getFilename());
                    childFile.parent = this;
                    files.add(childFile);
                }
            }catch(Exception e){
                Executor.warn(TAG, "Ls remote dir error", e);
            }

        }
        return files;
    }

    @Override
    public FileLike<ChannelSftp.LsEntry> getParentFile() {
        return parent;
    }

    @Override
    public boolean mkdir() {
        try {
            channelSftp.mkdir(path);
            return true;
        }catch(Exception e) {
            Executor.warn(TAG, "Make remote dir error", e);
            return false;
        }
    }

    @Override
    public boolean mkdirs() {
        return mkdir();
    }

    @Override
    public boolean rename(String name) {
        String newPath = name;
        int index = path.lastIndexOf("/");
        if(index != -1){
            newPath = path.substring(0, index + 1) + name;
        }

        try {
            channelSftp.rename(path, newPath);
            return true;
        }catch(Exception e) {
            Executor.warn(TAG, "Rename to " + newPath + " error", e);
            return false;
        }
    }

    @Override
    public String getPermission() {
        return file == null ? null : file.getAttrs().getPermissionsString();
    }

    @Override
    public boolean equals(FileLike<ChannelSftp.LsEntry> other) {
        if(other == null){
            return false;
        }

        return path.equals(other.getPath());
    }
}
