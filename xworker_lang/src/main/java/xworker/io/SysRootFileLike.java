package xworker.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SysRootFileLike implements FileLike<File>{
    @Override
    public String getName() {
        return "Compute";
    }

    @Override
    public String getPath() {
        return "/";
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
        return 0;
    }

    @Override
    public File getSource() {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public String getIcon() throws IOException {
        return "icons/computer.png";
    }

    @Override
    public String getBigIcon() throws IOException {
        return null;
    }

    @Override
    public List<FileLike<File>> listFiles() {
        List<FileLike<File>> files = new ArrayList<>();
        for(File file : File.listRoots()){
            files.add(new FileFileLike(file));
        }
        return files;
    }

    @Override
    public FileLike<File> getParentFile() {
        return null;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public boolean mkdirs() {
        return false;
    }

    @Override
    public boolean rename(String name) {
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean equals(FileLike<File> other) {
        return false;
    }
}
