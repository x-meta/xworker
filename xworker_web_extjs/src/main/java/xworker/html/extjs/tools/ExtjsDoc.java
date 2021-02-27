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
package xworker.html.extjs.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xworker.lang.executor.Executor;

/**
 * Extjs的output下的html文件，从html文件中分析出对象、继承、属性、方法和事件等。
 * 
 * @author Administrator
 *
 */
public class ExtjsDoc {
	private static final String TAG = ExtjsDoc.class.getName();
	
	public String fileName;
	public List<ExtjsMethod> events = new ArrayList<ExtjsMethod>();
	
	public ExtjsDoc(File file) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		byte[] bytes = new byte[fin.available()];
		fin.read(bytes);
		String content = new String(bytes);
		fin.close();
		
		initEvents(content);
	}
	
	public void initEvents(String content){
		int index = content.indexOf("Public Events");
		if(index == -1){
			Executor.info(TAG, "no public events, name=fileName");
			return;
		}
		
		int index2 = index;
		int oldIndex = index;
		while((index = getMarkIndex(content, "<b class=\"event\">", index2)) !=-1 && index > oldIndex){
			ExtjsMethod event = new ExtjsMethod();
			
			//事件名称
			index = getMarkIndex(content, "\">", index);
			index2 = content.indexOf("</a>", index);
			if(index2 == -1){
				continue;
			}
			event.name = content.substring(index, index2);
			
			//参数
			index = getMarkIndex(content, "(&nbsp;", index2);
			index2 = content.indexOf(")", index);
			if(index2 == -1 || index == -1){
				break;
			}
			String params = content.substring(index, index2);
			int i1,i2 = 0;
			for(String param : params.split("[,]")){
				i1 = getMarkIndex(param, "<span title=", 0);
				if(i1 == -1){
					continue;
				}
				i1 = getMarkIndex(param, "\">", i1);
				if(i1 == -1){
					continue;
				}
				i2 = param.indexOf("</span>", i1);
				String p = param.substring(i1, i2);
				i1 = p.indexOf("&nbsp;");
				ExtjsParam pa = new ExtjsParam();
				pa.objectName = p.substring(0, i1);
				pa.name = p.substring(i1 + 6, p.length());
				event.params.add(pa);
			}
			
			//文档，解析短的文档
			index = getMarkIndex(content, "<div class=\"short\">", index2);
			index2 = content.indexOf("</div><div class=\"long\">", index);			
			event.doc = content.substring(index, index2);
			
			//方法的源
			index = getMarkIndex(content, "msource", index2);
			i2 = content.indexOf("</td></tr>", index);
			index = getMarkIndex(content, "ext:cls=\"", index);
			if(index < i2 && index >= 0){
				index2 = content.indexOf("\">", index);
				event.source = content.substring(index, index2);
			}
			events.add(event);
		}
	}
	
	public int getMarkIndex(String content, String mark, int index){
		int index1 = content.indexOf(mark, index);
		if(index1 != -1){
			return index1 + mark.length();
		}else{
			return index1;
		}
	}
}