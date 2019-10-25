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
package xworker.java.awt.event;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.swing.event.ThingListener;

public class ThingDropTargetListener extends ThingListener implements DropTargetListener{
	
	public ThingDropTargetListener(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		doEvent("dragEnter", dtde);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		doEvent("dragOver", dtde);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		doEvent("dropActionChanged", dtde);
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		doEvent("dragExit", dte);
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		doEvent("drop", dtde);
	}

}