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

import java.io.IOException;

import org.eclipse.swt.graphics.Resource;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.util.ResourceManager;

public class ImageCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
		if("Var".equals(self.getString("type"))){
		    return OgnlUtil.getValue(self.getString("varName"), actionContext);
		}
		
		Resource image = ResourceManager.createResource(self, actionContext);
	    actionContext.getScope(0).put(self.getString("name"), image);
		
		return image;      
	}

    public static Object getKey(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		return self.getString("imageFile");       
	}

    public static Object createResource(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		String imageFile = self.getString("imageFile");
		if(imageFile == null || imageFile == ""){
		    return null;
		}
		
		return ResourceManager.createIamge(imageFile, actionContext);
		/*
		def imageFilePath = UtilFile.getFilePath(self.imageFile);
		
		if(self.imageFile.indexOf(":") != -1){
		    def fs = self.imageFile.split("[:]");
		    def project = world.getProject(fs[0]);
		    imageFilePath = project.getFilePath() + "/" + fs[1];
		}else{ 
		    imageFilePath = self.imageFile;
		    def file = new File(imageFilePath);
		    if(!file.exists()){
		        def globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
		        imageFilePath = globalConfig.imagePath + "/" + imageFilePath;
		    }
		    file = new File(imageFilePath);
		    if(!file.exists()){
		        imageFilePath = world.getPath() + "/" + self.imageFile;
		    }
		    file = new File(imageFilePath);
		    if(!file.exists()){
		        imageFilePath = self.imageFile;
		    }else{
		        def image = actionContext.get(self.imageFile);
		        if(image instanceof Image){
		            return image;
		        }else{
		            return null;
		        }
		    }
		}
		
		
		//log.info(self.imageFile + ":" + imageFilePath);
		//return null;
		InputStream fin = World.getInstance().getResourceAsStream(imageFile);
		if(fin != null){
		    try{
		        return new Image(null, fin);
		    }finally{
		        fin.close();
		    }
		}
		
		return null;      
		*/
	}

}