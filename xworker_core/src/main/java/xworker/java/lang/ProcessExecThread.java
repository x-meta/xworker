package xworker.java.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public class ProcessExecThread extends Thread{
	Thing thing;
	ActionContext actionContext;
	Process process;
	boolean sync;
	long timeout;
	int maxContentLength;
	OutputStream outputStream;
	OutputStream errorOutputSteam;
	boolean exited = false;
	StringBuffer buffer = new StringBuffer();
	Bindings reservedVars = null;
	List<ExecutorService> executorServiceList;
	boolean log = false;
	
	public ProcessExecThread(Process process, Thing thing, ActionContext actionContext){
		super(thing.getMetadata().getLabel());
		
		this.process = process;
		this.thing = thing;
		this.actionContext = actionContext;
		
		this.sync = (Boolean) thing.doAction("isSync", actionContext);
		this.timeout = (Long) thing.doAction("getTimeout", actionContext);
		if(this.timeout == 0) {
			this.timeout = 10000;
		}
		String reservedVarsStr = thing.doAction("getReservedVars", actionContext);
		this.reservedVars = UtilAction.getReservedVars(reservedVarsStr, actionContext);
		this.maxContentLength = (Integer) thing.doAction("getMaxContentLength", actionContext);
		Object out = thing.doAction("getOutputStream", actionContext);
		if(out instanceof OutputStream){
			outputStream = (OutputStream) out;
		}
		Object outErr = thing.doAction("getErrorOutputStream", actionContext);
		if(outErr instanceof OutputStream){
			errorOutputSteam = (OutputStream) outErr;
		}

		executorServiceList = Executor.getExecutorServices();
	}
	
	public void write(byte[] bytes) throws IOException{
		process.getOutputStream().write(bytes);
	}
	
	public void write(byte[] b, int off, int len) throws IOException{
		process.getOutputStream().write(b, off, len);
	}
	
	public String getResult(){
		return buffer.toString();
	}
	
	
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Process getProcess() {
		return process;
	}

	public boolean isSync() {
		return sync;
	}

	public boolean isExited() {
		return exited;
	}
	
	public void setExecutorService(ExecutorService executorService) {
		this.executorServiceList.add(executorService);
	}
	
	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public void run(){
		int exitValue = -1;
		try {
			Executor.push(executorServiceList);
			//启动Process的两个线程
			new InputThread(this, process.getInputStream(), true).start();
			new InputThread(this, process.getErrorStream(), false).start();
			
			long start = System.currentTimeMillis();
			while(true){
				try{
					exitValue = process.exitValue();
					break;
				}catch(Exception e){				
				}
				
				if(timeout > 0 && (System.currentTimeMillis() - start ) > timeout){
					//超时中断，终止进程
					process.destroy();
					break;
				}
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			exited = true;
		}finally {
			try {
				actionContext.push(reservedVars);
				thing.doAction("onExited", actionContext, "out", buffer.toString(), "exitValue", exitValue, 
						"pt", this, "process", process);
			}catch(Exception e){				
			}finally {
				actionContext.pop();
			}
			
			Executor.pop();			
		}
	}
	
	static class InputThread extends Thread{
		InputStream in;
		ProcessExecThread pt;
		boolean isOut;
		
		public InputThread(ProcessExecThread pt, InputStream in, boolean isOut){
			super(pt.thing.getMetadata().getLabel() + "-inputstream");
			
			this.pt = pt;
			this.in = in;
			this.isOut = isOut;
		}
		
		public void run(){
			try{
				Executor.push(pt.executorServiceList);
				
				int length = -1;
				byte[] bytes = new byte[2048];
				while((length = in.read(bytes)) != -1){
					//重定向到输出流中
					OutputStream out = getOutputStream();
					if(out != null) {
						out.write(bytes, 0, length);
					}
					
					//写入到返回值
					String txt = new String(bytes, 0, length);
					pt.buffer.append(txt);
					while(pt.buffer.length() > pt.maxContentLength) {
						pt.buffer.delete(0, 100);
					}
					
					//触发事件
					try {
						if(pt.log) {
							Executor.info("", txt);
						}
						pt.actionContext.push(pt.reservedVars);
						if(isOut) {
							pt.thing.doAction("onOut", pt.actionContext, "bytes", bytes, "length", length, "pt", pt);
						}else {
							pt.thing.doAction("onErrOut", pt.actionContext, "bytes", bytes, "length", length, "pt", pt);
						}
					}finally {
						pt.actionContext.pop();
					}
					
					
				}
				
			}catch(Exception e){
				//e.printStackTrace();				
			}
		}
		
		public OutputStream getOutputStream(){
			if(isOut){
				if(pt.outputStream != null){
					return pt.outputStream;
				}else{
					return null;
				}
			}else{
				if(pt.errorOutputSteam != null){
					return pt.errorOutputSteam;
				}else{
					return null;
				}
			}
		}
	}
}
