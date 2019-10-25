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

import java.awt.Color;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class ColorActions {
    public static Object create(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
        
        String color = self.getString("color");
        if(color != null && !"".equals(color)){
        	if(color.startsWith("\"")){
        		color = color.substring(1, color.length() - 1);
        	}
        	if(color.startsWith("#")){
        		color = color.substring(2, color.length());				
        	}
        	
        	byte[] bytes = UtilString.hexStringToByteArray(color);
        	return new Color(bytes[0], bytes[1], bytes[2]);
        }else{
        	String preSetColor = self.getString("preSetColor");
        	if(preSetColor != null && !"".equals(preSetColor)){
        		if("black".equals(preSetColor)){
        			return Color.black;
        		}else if("blue".equals(preSetColor)){
        			return Color.blue;
        		}else if("cyan".equals(preSetColor)){
        			return Color.cyan;
        		}else if("darkGray".equals(preSetColor)){
        			return Color.darkGray;
        		}else if("gray".equals(preSetColor)){
        			return Color.gray;
        		}else if("green".equals(preSetColor)){
        			return Color.green;
        		}else if("lightGray".equals(preSetColor)){
        			return Color.lightGray;
        		}else if("magenta".equals(preSetColor)){
        			return Color.magenta;
        		}else if("orange".equals(preSetColor)){
        			return Color.orange;
        		}else if("pink".equals(preSetColor)){
        			return Color.pink;
        		}else if("red".equals(preSetColor)){
        			return Color.red;
        		}else if("white".equals(preSetColor)){
        			return Color.white;
        		}else if("yellow".equals(preSetColor)){
        			return Color.yellow;
        		}
        	}
        }
        
        return null;
    }
}