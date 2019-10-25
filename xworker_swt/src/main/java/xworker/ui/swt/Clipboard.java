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
package xworker.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.xmeta.Thing;
import org.xmeta.codes.XmlCoder;

import xworker.swt.util.SwtUtils;

public class Clipboard  {
	private static final String KEY = "__xworker.ui.swt.Clipboard__";
	private static int maxClips = 20;
	private static List<Thing> clips = new ArrayList<Thing>();
	
	public static void add(Thing dataObject){
		List<Thing> clips = getClips();
		if(clips == null) {
			return;
		}
		
		if(clips.size() > maxClips){
			clips.remove(0);
		}
		
		clips.add(dataObject);
		
		//同时把事物的xml拷贝的前贴板中
		Display display = Display.getCurrent();
		if(display != null && SwtUtils.isRWT() == false){
			//RWT模式下不能使用剪贴板
			try{
				String xml = XmlCoder.encodeToString(dataObject);
				org.eclipse.swt.dnd.Clipboard clipboard = new org.eclipse.swt.dnd.Clipboard(display);
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[]{xml}, new Transfer[]{textTransfer});
				clipboard.dispose();
			}catch(Exception e){
				
			}
		}
	}
	
	public static Thing get(){
		List<Thing> clips = getClips();
		
		if(clips != null && clips.size() != 0){
			return clips.get(clips.size() - 1);
		}else{
			return null;
		}
	}
	
	public static Thing get(int index){
		List<Thing> clips = getClips();
		
		if(clips != null && index < clips.size()){
			return clips.get(index);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thing> getClips(){
		Display display = Display.getCurrent();
		if(SwtUtils.isRWT() == false){
			return clips;
		}else if(display != null) {
			List<Thing> list = (List<Thing>) display.getData(KEY);
			if(list == null) {
				list = new ArrayList<Thing>();
				display.setData(KEY, list);
			}
			
			return list;
		}else{
			return null;
		}
	}
}