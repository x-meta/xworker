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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.swing.event.ThingListener;

public class ThingKeyListener extends ThingListener implements KeyListener{
	public ThingKeyListener(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		doEvent("keyTyped", e);	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		doEvent("keyPressed", e);	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		doEvent("keyReleased", e);	
	}

}