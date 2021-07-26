package xworker.net.jsch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import ognl.OgnlException;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

public class ScpTo {
	private static Logger logger = LoggerFactory.getLogger(ScpTo.class);
	
	public static Object getLocalFile(ActionContext actionContext) throws OgnlException {
		Thing self = (Thing) actionContext.get("self");
		return UtilData.getData(self, "localFile", actionContext);
	}

	public static Object getRemoteFile(ActionContext actionContext) throws OgnlException {
		Thing self = (Thing) actionContext.get("self");
		return UtilData.getData(self, "remoteFile", actionContext);
	}

	public static Object run(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");

		Session session = null;
		
		UserTask task = UserTaskManager.createTask(self, true);
		boolean runBackground = self.getBoolean("runBackground");
		boolean closeSession = Exec.isSessionNeedClose(self);
		
		File _lfile = null;
		Object localFile = self.doAction("getLocalFile", actionContext);
		if (localFile == null) {
			throw new ActionException("Local file is null, path="
					+ self.getMetadata().getPath());
		} else if (localFile instanceof String) {
			_lfile = new File((String) localFile);
			if (!_lfile.exists()) {
				throw new ActionException("Local file not exists, path="
						+ localFile + ", scpToPath="
						+ self.getMetadata().getPath());
			}
		} else if (localFile instanceof File) {
			_lfile = (File) localFile;
		} else {
			throw new ActionException("Object is not a file, obj="
					+ localFile.toString() + ", path="
					+ self.getMetadata().getPath());
		}
		if (!_lfile.isFile()) {
			throw new ActionException("Is not a file, path="
					+ self.getMetadata().getPath());
		}

		String rfile = (String) self.doAction("getRemoteFile",
				actionContext);

		task.setDetail("拷贝文件：<br/>" + "本地源文件：" + _lfile.getAbsolutePath() + "<br/>远程目标文件：" + rfile);
		task.start();
		task.setCurrentLabel("Connect to remote server...");
		session = (Session) self.doAction("getSession", actionContext);
		
		if(runBackground){
			new Thread(new ScpTask(self, session, task, _lfile, rfile, closeSession)).start();
			return task;
		}else{
			return scpTo(self, session, task, _lfile, rfile, closeSession);
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
			scpTo(self, session, task, _lfile, rfile, closeSession);
		}
	}

	public static boolean scpTo(Thing self, Session session, UserTask task, File _lfile, String rfile, boolean closeSession){
		FileInputStream fis = null;
		Channel channel = null;
		try {
			
			boolean ptimestamp = true;
			
			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				return false;
			}

			if (ptimestamp) {
				command = "T " + (_lfile.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					return false;
				}
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			String lfile = _lfile.getPath();
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else if(lfile.lastIndexOf('\\') > 0){
				command += lfile.substring(lfile.lastIndexOf('\\') + 1);
			}else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				return false;
			}
			
			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			long sendedSize = 0;
			int percent = 0;
			long speedSize = 0;
			long lastTime = System.currentTimeMillis();
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
				sendedSize = sendedSize + len;
				speedSize = speedSize + len;
								
				int currentPer = (int) (sendedSize * 100 / filesize);
				if(currentPer != percent || System.currentTimeMillis() - lastTime > 2000){
					percent = currentPer;
					String speed = "0/秒";
					speed = UtilData.getSizeInfo(1000 * speedSize / (System.currentTimeMillis() - lastTime + 1)) + "/秒";
					if(System.currentTimeMillis() - lastTime > 2000){						
						speedSize = 0;
						lastTime = System.currentTimeMillis();
					}
					task.setCurrentLabel("已完成：" + percent + "%，速度：" + speed + "，剩余：" + UtilData.getSizeInfo(filesize - sendedSize));
					task.setProgress(percent);
				}
				
				//如果用户取消，则终止
				if(task.getStatus() == UserTask.CANCEL){
					task.terminated();
					return false;
				}
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				return false;
			}
			out.close();
			
			return true;
		} catch (Exception e) {
			throw new ActionException("Scpto fail, path=" + self.getMetadata().getPath(), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				
				task.finished();
			} catch (Exception ee) {
			}
			
			if (channel != null) {
				channel.disconnect();
			}
			
			if (closeSession && session != null) {
				session.disconnect();
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
