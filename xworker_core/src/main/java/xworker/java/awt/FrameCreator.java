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
package xworker.java.awt;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class FrameCreator {
	public static void init(Frame obj, Thing thing, Container parent, ActionContext actionContext) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		WindowCreator.init(obj, thing, parent, actionContext);
		
		Integer extendedState = null;
		String v = thing.getString("extendedState");
		if("NORMAL".equals(v)){
			extendedState = Frame.NORMAL;
		}else if("ICONIFIED".equals(v)){
			extendedState = Frame.ICONIFIED;
		}else if("MAXIMIZED_HORIZ".equals(v)){
			extendedState = Frame.MAXIMIZED_HORIZ;
		}else if("MAXIMIZED_VERT".equals(v)){
			extendedState = Frame.MAXIMIZED_VERT;
		}else if("MAXIMIZED_BOTH".equals(v)){
			extendedState = Frame.MAXIMIZED_BOTH;
		}
		if(extendedState != null){
			obj.setExtendedState(extendedState);
		}
		
		Rectangle maximizedBounds = AwtCreator.createRectangle(thing, "maximizedBounds", actionContext);
		if(maximizedBounds != null){
			obj.setMaximizedBounds(maximizedBounds);
		}
		
		Boolean resizable = JavaCreator.createBoolean(thing, "resizable");
		if(resizable != null){
			obj.setResizable(resizable);
		}
		
		Integer state = null;
		v = thing.getString("extendedState");
		if("NORMAL".equals(v)){
			state = Frame.NORMAL;
		}else if("ICONIFIED".equals(v)){
			state = Frame.ICONIFIED;
		}
		if(state != null){
			obj.setState(state);
		}
		
		String title = JavaCreator.createText(thing, "title", actionContext);
		if(title != null){
			obj.setTitle(title);
		}
		
		Boolean undecorated = JavaCreator.createBoolean(thing, "undecorated");
		if(undecorated != null){
			obj.setUndecorated(undecorated);
		}
	}
}