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
package xworker.http.fileupload;

import java.io.File;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.xmeta.Thing;

public class DiskFileItemFactoryManager {
	private static FileItemFactory factory = new DiskFileItemFactory();
	
	public static FileItemFactory getDiskFileItemFactory(Thing thing){
		if(thing == null || thing.getBoolean("defaultDiskFileItemFactory")){
			return factory;
		}
		
		int sizeThreshold = thing.getInt("sizeThreshold", 0);
		String repository = thing.getString("repository");
		if(sizeThreshold == 0 || repository == null || "".equals(repository)){
			return factory;
		}
		
		String key = "__apache_disFIleItemFactory__";
		FileItemFactoryEntry selfFactory = (FileItemFactoryEntry) thing.getData(key);
		if(selfFactory == null || selfFactory.lastmodified != thing.getMetadata().getLastModified()){
			if(selfFactory == null){
				selfFactory = new FileItemFactoryEntry();
				thing.setData(key, selfFactory);
			}
			
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory(sizeThreshold, new File(repository));
			selfFactory.factory = fileItemFactory;
		
			selfFactory.lastmodified = thing.getMetadata().getLastModified();
		}
		
		return selfFactory.factory;
	}
	
	private static class FileItemFactoryEntry{
		FileItemFactory factory;
		long lastmodified;
	}
}