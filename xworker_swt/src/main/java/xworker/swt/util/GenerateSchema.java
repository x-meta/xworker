package xworker.swt.util;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.doc.schema.ThingDocument;

/**
 * 生成SWT的Schema的工具。
 * 
 * @author zyx
 *
 */
public class GenerateSchema {
	public static String[][] extendExcludes = new String[][]{
		new String[]{"SubModels", "TreeModel" }
	};
	
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init(".");
			
			Thing shell = world.getThing("xworker.swt.widgets.Shell");
			ThingDocument doc = new ThingDocument(shell);
			doc.write("target/swt.xsd");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
