package xworker.net.jsch.swt;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import xworker.io.FileLike;
import xworker.lang.executor.Executor;

import java.io.File;
import java.util.List;

/**
 * 从远程服务器上获取目录。
 */
public class JCSftpDirGetter {
    private static final String TAG = JCSftpDirGetter.class.getName();

    ChannelSftp channelSftp;
    LsEntryFileLike remoteDir;
    File localDir;
    SftpProgressMonitor monitor;

    public JCSftpDirGetter(ChannelSftp channelSftp, LsEntryFileLike remoteDir, File localDir, SftpProgressMonitor monitor){
        this.channelSftp = channelSftp;
        this.remoteDir = remoteDir;
        this.localDir = localDir;
        this.monitor = monitor;
    }

    public void start(){
        get(remoteDir, localDir);
    }

    private void get(LsEntryFileLike remoteFile, File localDir){
        File localFile = new File(localDir, remoteFile.getName());

        if(remoteFile.isDir()){
            localFile.mkdirs();

            for(FileLike<?> childFile : remoteFile.listFiles()){
                get((LsEntryFileLike) childFile, localFile);
            }
        }else{
            try {
                channelSftp.get(remoteFile.getPath(), localFile.getAbsolutePath(), monitor);
            }catch(Exception e){
                Executor.error(TAG, "Get " + remoteFile.getPath() + " to " + localFile.getPath() + " error", e);
            }
        }
    }
}
