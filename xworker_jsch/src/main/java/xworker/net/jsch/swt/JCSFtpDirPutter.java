package xworker.net.jsch.swt;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import xworker.io.FileLike;
import xworker.lang.executor.Executor;

import java.io.File;

public class JCSFtpDirPutter {
    private static final String TAG = JCSFtpDirPutter.class.getName();

    ChannelSftp channelSftp;
    LsEntryFileLike remoteDir;
    File localDir;
    SftpProgressMonitor monitor;

    public JCSFtpDirPutter(ChannelSftp channelSftp, LsEntryFileLike remoteDir, File localDir, SftpProgressMonitor monitor){
        this.channelSftp = channelSftp;
        this.remoteDir = remoteDir;
        this.localDir = localDir;
        this.monitor = monitor;
    }

    public void start(){
        put(remoteDir, localDir);
    }

    private void put(LsEntryFileLike remoteFile, File localDir){
        String fileName = localDir.getName();

        try {
            if (localDir.isDirectory()) {
                LsEntryFileLike remoteNewDir = null;
                for (FileLike<?> childFile : remoteFile.listFiles()) {
                    if (childFile.getName().equals(fileName)) {
                        remoteNewDir = (LsEntryFileLike) childFile;
                        break;
                    }
                }

                if (remoteNewDir == null) {
                    channelSftp.mkdir(remoteFile.getPath() + "/" + fileName);
                    for (FileLike<?> childFile : remoteFile.listFiles()) {
                        if (childFile.getName().equals(fileName)) {
                            remoteNewDir = (LsEntryFileLike) childFile;
                            break;
                        }
                    }
                }

                for(File  localFile : localDir.listFiles()){
                    put(remoteNewDir, localFile);
                }
            }else{
                try {
                    channelSftp.put(localDir.getAbsolutePath(), remoteFile.getPath() + "/" + fileName, monitor);;
                } catch (Exception e) {
                    Executor.error(TAG, "Put " + localDir.getPath() + " to " + remoteFile.getPath() + " error", e);
                }
            }
        }catch(Exception ee){
            Executor.error(TAG, "Put " + localDir.getPath() + " to " + remoteFile.getPath() + " error", ee);
        }
    }
}
