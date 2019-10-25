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
package xworker.java.swing.event;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingInternalFrameListener extends ThingListener implements InternalFrameListener{
	public ThingInternalFrameListener(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		doEvent("internalFrameOpened", e);
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		doEvent("internalFrameClosing", e);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		doEvent("internalFrameClosed", e);
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		doEvent("internalFrameIconified", e);
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		doEvent("internalFrameDeiconified", e);
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		doEvent("internalFrameActivated", e);
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		doEvent("internalFrameDeactivated", e);
	}

}