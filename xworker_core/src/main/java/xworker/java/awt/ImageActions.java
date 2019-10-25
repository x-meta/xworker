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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class ImageActions {
	private static Logger logger = LoggerFactory.getLogger(ImageActions.class);
	
    public static Object create(ActionContext actionContext) throws OgnlException, MalformedURLException{
        Thing self = actionContext.getObject("self");
        String type = self.getString("type");
        String imageVarName = self.getString("imageVarName");
        String filePath= self.getString("filePath");
        
        if("var".equals(type)){
        	return OgnlUtil.getValue(imageVarName, actionContext);
        }else if("file".equals(type)){
            if(self.getBoolean("isVar")){
                return OgnlUtil.getValue(filePath, actionContext);
            }else{
                return Toolkit.getDefaultToolkit().createImage(UtilFile.getFilePath(filePath));
            }
        }else if("url".equals(type)){
            if(self.getBoolean("isVar")){
                return OgnlUtil.getValue(filePath, actionContext);
            }else{
                String url = (String) self.get("url");
            	if(url != null && !"".equals(url)){				
            		return Toolkit.getDefaultToolkit().createImage(new URL(url));
            	}else{
            	    return null;
                }
            }
        }else if("bytes".equals(type)){
            String bytesStr = self.getString("bytesStr");
            byte[] bytes = (byte[]) OgnlUtil.getValue(bytesStr, actionContext);
        	if(bytes != null){
        		String imageoffset = (String) self.get("imageoffset");
        		String imageLength = (String) self.get("imageLength");
        		if(UtilString.isNull(imageoffset) || UtilString.isNull(imageLength)){
        			return Toolkit.getDefaultToolkit().createImage(bytes);
        		}else{
        			return Toolkit.getDefaultToolkit().createImage(bytes, Integer.parseInt(imageoffset), Integer.parseInt(imageLength));
        		}
        	}else{
        		logger.info("createImage: bytes is null, bytes=" + bytesStr);
        		return null;
        	}
        }else if("crop".equals(type)){
            Image sourceImage = (Image) OgnlUtil.getValue(self.getString("sourceImage"), actionContext);
        	if(sourceImage != null){
        		int x = Integer.parseInt((String) self.get("x"));
        		int y = Integer.parseInt((String) self.get("y"));
        		int width = Integer.parseInt((String) self.get("width"));
        		int height = Integer.parseInt((String) self.get("height"));
        		ImageFilter smallCropFilter =new CropImageFilter(x,y,width,height);
        	    return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(sourceImage.getSource(),smallCropFilter));      
        	}else{
        		logger.info("crateImage: sourceImage is null, sourceImage=" + self.get("sourceImage"));
        		return null;
        	}
        }
        
        return null;
    }

}