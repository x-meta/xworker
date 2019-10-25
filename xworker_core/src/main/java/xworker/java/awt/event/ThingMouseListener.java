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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.swing.event.ThingListener;

public class ThingMouseListener extends ThingListener implements MouseListener{
	public ThingMouseListener(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		doEvent("mouseClicked", e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		doEvent("mousePressed", e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		doEvent("mouseReleased", e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		doEvent("mouseEntered", e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		doEvent("mouseExited", e);
	}

}