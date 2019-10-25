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

public class StyleBackgroundImageCreator {
    public static Object create(ActionContext actionContext){
    	throw new RuntimeException("Not used method");
    	/*
        import groovy.lang.Binding;
        import xworker.util.UtilGroovy;
        
        image = UtilGroovy.getProperty(binding, self.image);
        if(image != null){
            style.backgroundImage = image;
            return image;
        }else{
            imageObj = self.getDataObject("Image@0");
            if(imageObj != null){
                Binding bin = new Binding();
                bin.setVariable("binding", binding);
                bin.setVariable("parent", parent);
                image = imageObj.exec("create", bin);
                style.backgroundImage = image;
                return image;
            }
        }*/
    }

}