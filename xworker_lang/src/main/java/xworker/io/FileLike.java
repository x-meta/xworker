package xworker.io;

import java.io.IOException;
import java.util.List;

public interface FileLike<T> {
    public String getName();

    public String getPath();

    public String getOwner();

    public String getGroup();

    public long getLength();

    T getSource();

    public long getLastModified();

    public boolean isDir();

    public boolean isFile();

    public String getIcon() throws IOException;

    public String getBigIcon() throws IOException;

    public List<FileLike<T>> listFiles();

    public FileLike<T> getParentFile();

    public boolean mkdir();

    public boolean mkdirs();

    public boolean rename(String name);

    public String getPermission();

    public boolean equals(FileLike<T> other);
}
