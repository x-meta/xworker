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
package xworker.swt.util.subtitle;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

public class shellActions {
    public static void init(ActionContext actionContext){
        Shell shell = actionContext.getObject("shell");
        String texts = actionContext.getObject("texts");
        String font = actionContext.getObject("font");
        String color = actionContext.getObject("color");
        
        Subtitle subtitle = new Subtitle( texts, font, color, 10, false);
        actionContext.getScope(0).put("subtitle", subtitle);
    }

}