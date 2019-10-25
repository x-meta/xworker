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
package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.xmeta.World;

import net.sf.colorer.FileType;
import net.sf.colorer.ParserFactory;
import net.sf.colorer.swt.ColorManager;
import net.sf.colorer.swt.TextColorer;

public class Colorer {
	private static String key = "__Colorer__attach__dispose__listener__";
	private static ParserFactory parserFactory = null;
	/**
	 * 返回支持的代码类型。
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getSupportCodeTypes(){
		ParserFactory pf  = getParserFactory();
		List<String> types = new ArrayList<String>();
		Enumeration en = pf.getHRCParser().enumerateFileTypes();
		while(en.hasMoreElements()){
			FileType type = (FileType) en.nextElement();
			
			types.add(type.getName());
		}
		
		return types;
	}
	
	/**
	 * ParserFactory应该是唯一的，好像销毁ParserFactory会造成系统崩溃
	 * @return
	 */
	public static synchronized ParserFactory getParserFactory(){
		if(parserFactory == null) {
			World world = World.getInstance();
			parserFactory = new ParserFactory(world.getPath() + "/config/colorer/catalog.xml" );
		}
		
		return parserFactory;
		/*
		ParserFactory pf = (ParserFactory) world.getData("_colorer_ParserFactory_");
		if(pf == null){
		    pf = new ParserFactory(world.getPath() + "/config/colorer/catalog.xml" );
		    world.setData("_colorer_ParserFactory_", pf);
		}
		
		return pf;*/
	}
	
	public static void attach(StyledText textEditor, String codeName, String codeType){
		World world = World.getInstance();
		
		//设置着色
		ParserFactory pf  = getParserFactory();
		ColorManager colorManager = (ColorManager) world.getData("_colorer_colorerColorManage_");
		if(colorManager == null){
		    colorManager = new ColorManager();
		    world.setData("_colorer_colorerColorManage_", colorManager);
		}		

		final TextColorer textColorer = new TextColorer(pf, colorManager);
		textColorer.attach(textEditor);
		textColorer.setCross(true, true);
		textColorer.setRegionMapper("default", true);
		textEditor.setData("textColorer", textColorer);
		if(codeName != null && codeType != null){
		    textColorer.chooseFileType(codeName + "." + codeType);
		    textEditor.setData("fileType", textColorer.getFileType());		    
		}
		
		//如果已经绑定过其它代码着色，解除绑定
		DisposeListener lis = (DisposeListener) textEditor.getData(key);
		if(lis != null){
			lis.widgetDisposed(null);
			textEditor.removeDisposeListener(lis);
		}
		lis = new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				try{
					textColorer.detach();
				}catch(Exception e){					
				}
			}			
		};
		textEditor.addDisposeListener(lis);
		textEditor.setData(key, lis);
	}
}