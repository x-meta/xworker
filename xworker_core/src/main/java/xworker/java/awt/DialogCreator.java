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
import java.awt.Dialog;
import java.lang.reflect.InvocationTargetException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class DialogCreator {
	public static void init(Dialog obj, Thing thing, Container parent, ActionContext actionContext) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		WindowCreator.init(obj, thing, parent, actionContext);
		
		Boolean modal = JavaCreator.createBoolean(thing, "modal");
		if(modal != null){
			obj.setModal(modal);
		}
		
		Dialog.ModalityType modalityType = null;
		String value = thing.getString("modalityType");
		if("APPLICATION_MODAL".equals(value)){
			modalityType = Dialog.ModalityType.APPLICATION_MODAL; 
		}else if("DOCUMENT_MODAL".equals(value)){
			modalityType = Dialog.ModalityType.DOCUMENT_MODAL; 
		}else if("MODELESS".equals(value)){
			modalityType = Dialog.ModalityType.MODELESS; 
		}else if("TOOLKIT_MODAL".equals(value)){
			modalityType = Dialog.ModalityType.TOOLKIT_MODAL; 
		}
		if(modalityType != null){
			obj.setModalityType(modalityType);
		}
		
		Boolean resizable = JavaCreator.createBoolean(thing, "resizable");
		if(resizable != null){
			obj.setResizable(resizable);
		}
		
		String title = JavaCreator.createText(thing, "title", actionContext);
		if(title != null){
			obj.setTitle(title);
		}
		
		Boolean undecorated = JavaCreator.createBoolean(thing, "undecorated");
		if(undecorated != null){
			obj.setUndecorated(undecorated);
		}
		
		Boolean visible = JavaCreator.createBoolean(thing, "visible");
		if(visible != null){
			obj.setVisible(visible);
		}
	}
}