package xworker.io;

import xworker.util.UtilFileIcon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileFileLike implements FileLike<File>{
    File file;

    public FileFileLike(File file){
        this.file = file;
    }


    @Override
    public String getName() {
        String name = file.getName();
        if(name == null || name.isEmpty()){
            return file.getPath();
        }else{
            return name;
        }
    }

    @Override
    public String getPath() {
        return file.getPath();
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
        return file.length();
    }

    @Override
    public File getSource() {
        return file;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public boolean isDir() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public String getIcon() throws IOException {
        return UtilFileIcon.getFileIcon(file, false);
    }

    @Override
    public String getBigIcon() throws IOException {
        return UtilFileIcon.getFileIcon(file, true);
    }

    @Override
    public List<FileLike<File>> listFiles() {
        List<FileLike<File>> files = new ArrayList<>();
        if(file.isDirectory()){
            for(File child : Objects.requireNonNull(file.listFiles())){
                files.add(new FileFileLike(child));
            }
        }
        return files;
    }

    @Override
    public FileLike<File> getParentFile() {
        File parentFile = file.getParentFile();
        if(parentFile != null){
            return new FileFileLike(parentFile);
        }else {
            return null;
        }
    }

    @Override
    public boolean mkdir() {
        return file.mkdir();
    }

    @Override
    public boolean mkdirs() {
        return file.mkdirs();
    }

    @Override
    public boolean rename(String name) {
        return file.renameTo(new File(file.getParentFile(), name));
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean equals(FileLike<File> other) {
        if(other == null){
            return false;
        }
        return file.equals(other.getSource());
    }
}
