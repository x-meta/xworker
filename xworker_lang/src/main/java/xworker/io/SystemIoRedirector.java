/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SystemIoRedirector extends OutputStream {
	private static CircularByteBuffer cbb = new CircularByteBuffer();
	private PrintStream sysout; 
	
	private static List<SystemIoRedirectorListener> listeners = new ArrayList<SystemIoRedirectorListener>();
	public static PrintStream psout ;
	static{
		try{		
			final SystemIoRedirector sOut = new SystemIoRedirector(System.out);
			final SystemIoRedirector sErr = new SystemIoRedirector(System.err);
			psout = new PrintStream(sOut); 
			System.setOut(psout);
			System.setErr(new PrintStream(sErr));
			try {
				Class.forName("xworker.util.Log4jConsoleAppender");
			}catch(Exception e) {				
			}
			
			Thread th = new Thread(new Runnable(){
				public void run(){
					try{
						byte[] bytes = new byte[4096];
						int length = -1;
						InputStream in = cbb.getInputStream();
						while((length = in.read(bytes)) != -1){
							String str = new String(bytes, 0, length);		
							for(SystemIoRedirectorListener l : listeners){
								try{
									l.read(str);
								}catch(Exception e){
									e.printStackTrace();
								}								
							}				
						}
						/*
						BufferedReader br = new BufferedReader(new InputStreamReader(cbb.getInputStream()));
						String line = null;
						while((line = br.readLine()) != null){
							for(SystemIoRedirectorListener l : listeners){
								try{
									l.readLine(line);
								}catch(Exception e){
									e.printStackTrace();
								}								
							}
						}*/
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}, "System IO Redirect");
			th.setDaemon(true);
			th.start();					
		}catch(Exception e){		
			e.printStackTrace();
		}		
	}
	
	public static void addListener(SystemIoRedirectorListener listener){
		listeners.add(listener);
	}
	
	public static void removeListener(SystemIoRedirectorListener listener){
		listeners.remove(listener);
	}
	
	public static void init(){
		
	}
	
	public SystemIoRedirector(PrintStream sysout) {
		this.sysout = sysout;
	}

	public void close() {
		cbb.clear();
	}

	public void flush() {
		sysout.flush();
		try {
			cbb.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(final byte[] b) throws IOException {
		sysout.write(b);
		cbb.getOutputStream().write(b);
	}

	public void write(final byte[] b, final int off, final int len)
			throws IOException {
		sysout.write(b, off, len);
		cbb.getOutputStream().write(b, off, len);		
	}

	public void write(final int b) throws IOException {
		sysout.write(b);
		cbb.getOutputStream().write(b);		
	}
}