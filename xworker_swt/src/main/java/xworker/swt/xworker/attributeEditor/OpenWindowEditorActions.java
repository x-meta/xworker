/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.swt.xworker.attributeEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;

import xworker.swt.events.SwtListener;

public class OpenWindowEditorActions {
    public static void keyDownContrlQ(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Event event = actionContext.getObject("event");
        SwtListener editButtonSelection = actionContext.getObject("editButtonSelection");
        
        if((event.keyCode == 'q' || event.keyCode == 'Q') && event.stateMask == SWT.CTRL){
            editButtonSelection.handleEvent(null);
            event.doit = false;
        }else if(event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP) {
        	editButtonSelection.handleEvent(null);
            event.doit = false;
        }
    }

}