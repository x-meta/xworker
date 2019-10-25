package xworker.net.jsch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.task.UserTaskManager;

public class ScpFrom {
	private static Logger logger = LoggerFactory.getLogger(ScpFrom.class);
	
	public static Object run(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		UserTask task = UserTaskManager.createTask(self, true);
		if(UtilAction.getDebugLog(self, actionContext)){
			task.addListener(new UserTaskListener(){
				@Override
				public void started(UserTask task) {
					logger.info(task.getName() + " started");
				}

				@Override
				public void finished(UserTask task) {
					logger.info(task.getName() + " finished");
				}
				
				@Override
				public void progressSetted(UserTask task, int progress) {
					//logger.info(task.getName() + " progress: " + progress);
				}

				@Override
				public void currentLabelUpdated(UserTask task, String label) {
					logger.info(task.getName() + " status changed, current label = " + label);
				}
				
			});
		}
		boolean runBackground = self.getBoolean("runBackground");
		boolean closeSession = Exec.isSessionNeedClose(self);
		Session session = null;
		File _lfile = null;
		Object localFile = self.doAction("getLocalFile", actionContext);
		if (localFile == null) {
			throw new ActionException("Local file is null, path="
					+ self.getMetadata().getPath());
		} else if (localFile instanceof String) {
			_lfile = new File((String) localFile);				
		} else if (localFile instanceof File) {
			_lfile = (File) localFile;
		} else {
			throw new ActionException("Object is not a file, obj="
					+ localFile.toString() + ", path="
					+ self.getMetadata().getPath());
		}
		if(!_lfile.exists()){
			_lfile.getParentFile().mkdirs();
		}

		String rfile = (String) self.doAction("getRemoteFile",
				actionContext);

		task.setDetail("拷贝文件：<br/>远程源文件：" + rfile + "<br/>本地目标文件：" + _lfile.getAbsolutePath());
		task.start();
		task.setCurrentLabel("Connect to remote server...");
		session = (Session) self.doAction("getSession", actionContext);

		if(runBackground){
			new Thread(new ScpTask(self, session, task, _lfile, rfile, closeSession)).start();
			return task;
		}else{
			return scpFrom(self, session, task, _lfile, rfile, closeSession);
		}
	}
	
	static class ScpTask implements Runnable{
		Thing self;
		Session session;
		UserTask task;
		File _lfile;
		String rfile;
		boolean closeSession;
		
		public ScpTask(Thing self, Session session, UserTask task, File _lfile, String rfile, boolean closeSession){
			this.self = self;
			this.session = session;
			this.task = task;
			this._lfile = _lfile;
			this.rfile = rfile;
			this.closeSession = closeSession;
		}
		
		public void run(){
			scpFrom(self, session, task, _lfile, rfile, closeSession);
		}
	}

	public static boolean scpFrom(Thing self, Session session, UserTask task, File _lfile, String rfile, boolean closeSession){
		FileOutputStream fos = null;
		Channel channel = null;
		try{
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}
				if(_lfile.isDirectory()){
					_lfile = new File(_lfile, file);
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(_lfile);
				int foo;
				long sendedSize = 0;
				int percent = 0;
				long speedSize = 0;
				long lastTime = System.currentTimeMillis();
				long totalSize = filesize;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
					
					sendedSize = sendedSize + foo;
					speedSize = speedSize + foo;
									
					int currentPer = (int) (sendedSize * 100 / totalSize);
					if(currentPer != percent || System.currentTimeMillis() - lastTime > 2000){
						percent = currentPer;
						String speed = "0/秒";
						speed = UtilData.getSizeInfo(1000 * speedSize / (System.currentTimeMillis() - lastTime + 1)) + "/秒";
						
						if(System.currentTimeMillis() - lastTime > 2000){							
							speedSize = 0;
							lastTime = System.currentTimeMillis();
						}
						if(filesize > 0){
							task.setCurrentLabel("已完成：" + percent + "%，速度：" + speed + "，剩余：" + UtilData.getSizeInfo(totalSize - sendedSize));
						}else{
							task.setCurrentLabel("已下载：" + UtilData.getSizeInfo(sendedSize) + "%，速度：" + speed);
						}
						task.setProgress(percent);
					}
					
					//如果用户取消，则终止
					if(task.getStatus() == UserTask.CANCEL){
						task.terminated();
						return false;
					}
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					return false;
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			return true;
		} catch (Exception e) {
			throw new ActionException("ScpFrom error, path=" + self.getMetadata().getPath(), e);
		}finally{
			try {
				if (fos != null)
					fos.close();
				
				task.finished();
			} catch (Exception ee) {
			}
			
			if (self.getBoolean("closeSession") && session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}
	

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logger.error(sb.toString());
			}
			if (b == 2) { // fatal error
				logger.error(sb.toString());
			}
		}
		return b;
	}
}
