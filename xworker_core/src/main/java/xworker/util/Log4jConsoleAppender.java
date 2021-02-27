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
package xworker.util;

import java.io.OutputStream;
import java.io.Writer;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;

import xworker.io.SystemIoRedirector;

public class Log4jConsoleAppender extends WriterAppender {
	static {
		if(SystemIoRedirector.psout != null) {
			Log4jConsoleAppender.setStaticOutputStream(SystemIoRedirector.psout);
		}
	}
	public static Log4jConsoleAppender instance;
	private static OutputStream out = System.out;
	
	public Log4jConsoleAppender() {
		super();
		
		instance = this;
	}

	public Log4jConsoleAppender(Layout layout, OutputStream os) {
		super(layout, os);
		
		instance = this;
		
	}

	public Log4jConsoleAppender(Layout layout, Writer writer) {
		super(layout, writer);
		
		instance = this;
	}
	
	@Override
	public void activateOptions() {
		if(out != null){
			this.setWriter(createWriter(out));
		}
		
		super.activateOptions();
	}

	public static void setStaticOutputStream(OutputStream out){
		Log4jConsoleAppender.out = out;
		if(instance != null){
			instance.closeWriter();
			instance.setWriter(instance.createWriter(out));
		}
	}

}