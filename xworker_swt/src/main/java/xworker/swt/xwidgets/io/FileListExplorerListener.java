package xworker.swt.xwidgets.io;

import xworker.io.FileLike;

public interface FileListExplorerListener {
    void onSetDir(FileListExplorer fileListExplorer, FileLike<?> fileLike);
}
