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
package xworker.swt.graphics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.LineAttributes;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class LineAttributesCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{		
    	Thing self = (Thing) actionContext.get("self");
    	
		if("Var".equals(self.getString("type"))){
		    return OgnlUtil.getValue(self.getString("varName"), actionContext);
		}
		
		int width = self.getInt("width");
		int cap = 0;
		String selfCap = self.getString("cap");
		if("CAP_FLAT".equals(selfCap)){
			cap = SWT.CAP_FLAT;
		}else if("CAP_ROUND".equals(selfCap)){
			cap = SWT.CAP_ROUND;
		}else if("CAP_SQUARE".equals(selfCap)){
			cap = SWT.CAP_SQUARE;
		}

		int join = 0;
		String selfJoin = self.getString("join");
		if("JOIN_BEVEL".equals(selfJoin)){
			join = SWT.JOIN_BEVEL;
		}else if("JOIN_MITER".equals(selfJoin)){
			join = SWT.JOIN_MITER;
		}else if("JOIN_ROUND".equals(selfJoin)){
			join = SWT.JOIN_ROUND;
		}
	
		int style = 0;
		String selfStyle = self.getString("style");
		if("LINE_CUSTOM".equals(selfStyle)){
			style = SWT.LINE_CUSTOM;
		}else if("LINE_DASH".equals(selfStyle)){
			style = SWT.LINE_DASH;
		}else if("LINE_DASHDOT".equals(selfStyle)){
			style = SWT.LINE_DASHDOT;
		}else if("LINE_DASHDOTDOT".equals(selfStyle)){
			style = SWT.LINE_DASHDOTDOT;
		}else if("LINE_DOT".equals(selfStyle)){
			style = SWT.LINE_DOT;
		}else if("LINE_SOLID".equals(selfStyle)){
			style = SWT.LINE_SOLID;
		}
		
		float[] dash = null;
		String selfDash = self.getString("dash");
		if(selfDash != null && !"".equals(selfDash)){
		    List<Float> dashs = new ArrayList<Float>();
		    for(String dstr : selfDash.split("[,]")){
		        dashs.add(Float.parseFloat(dstr));
		    }
		    dash = new float[dashs.size()];
		    for(int i=0; i<dashs.size(); i++){
		    	dash[i] = dashs.get(i);
		    }
		}
		
		float dashOffset = self.getFloat("dashOffset");
		float miterLimit = self.getFloat("miterLimit");
		
		LineAttributes line = new LineAttributes(width,cap,join,style,dash,dashOffset,miterLimit);
		if(self.getBoolean("actionContextVar")){
		    actionContext.getScope(0).put(self.getMetadata().getName(), line);
		}
		
		return line;       
	}

}