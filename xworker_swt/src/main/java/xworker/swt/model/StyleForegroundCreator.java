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
package xworker.swt.model;

import org.xmeta.ActionContext;

public class StyleForegroundCreator {
    public static Object create(ActionContext actionContext){
    	throw new RuntimeException("Not used method");
    	/*
        import groovy.lang.Binding;
        import xworker.util.UtilGroovy;
        
        color = UtilGroovy.getProperty(binding, self.color);
        if(color != null){
            style.foreground = color;
            return color;
        }else{
            colorObj = self.getDataObject("Color@0");
            if(colorObj != null){
                Binding bin = new Binding();
                bin.setVariable("binding", binding);
                bin.setVariable("parent", parent);
                color = colorObj.exec("create", bin);
                style.foreground = color;
                return color;
            }
        }*/
    }

}