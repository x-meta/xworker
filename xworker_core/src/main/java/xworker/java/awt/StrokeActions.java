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
package xworker.java.awt;

import java.awt.BasicStroke;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StrokeActions {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String width = (String) self.get("width");
        if(width == null || "".equals(width)){
        	return null;
        }else{
        	float w = Float.parseFloat(width);
        	int cap = 0;
        	int join = 0;
        	String capStr = (String) self.get("cap");
        	if("CAP_BUTT".equals(capStr)){
        		cap = BasicStroke.CAP_BUTT;
        	}else if("CAP_ROUND".equals(capStr)){
        		cap = BasicStroke.CAP_ROUND;
        	}else if("CAP_SQUARE".equals(capStr)){
        		cap = BasicStroke.CAP_SQUARE;
        	}
        	String joinStr = (String) self.get("join");
        	if("JOIN_BEVEL".equals(joinStr)){
        		join = BasicStroke.JOIN_BEVEL;
        	}else if("JOIN_MITER".equals(joinStr)){
        		join = BasicStroke.JOIN_MITER;
        	}else if("JOIN_ROUND".equals(joinStr)){
        		join = BasicStroke.JOIN_ROUND;
        	}
        	
        	return new BasicStroke(w, cap, join);
        }
    }

}