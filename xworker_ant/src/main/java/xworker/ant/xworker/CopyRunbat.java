package xworker.ant.xworker;

import java.io.File;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

public class CopyRunbat {
	public static void toString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		File worldRoot = new File(world.getPath());
		
		//参数
		String destDir = self.getStringBlankAsNull("destDir");
		String batFileName = self.getStringBlankAsNull("batFileName");
		String thingPath = self.getStringBlankAsNull("thingPath");
		if(destDir == null){
			throw new ActionException("Copyrunbat destDir is null, path=" + self.getMetadata().getPath());
		}
		
		if(thingPath == null){
			throw new ActionException("Copyrunbat thing for run is null, path=" + self.getMetadata().getPath());
		}
		if(batFileName == null){
			throw new ActionException("Copyrunbat batfilename is null, path=" + self.getMetadata().getPath());
		}
		String method = self.getStringBlankAsNull("method");
		if(method == null){
			method = "run";
		}
		
		//copy runbat
		Thing copyrunbat = new Thing("xworker.ant.file.copy");
		copyrunbat.set("file", new File(worldRoot, "deploy/xer.bat").getAbsolutePath());
		copyrunbat.set("tofile", destDir + "/" + batFileName);
		
		Thing batfilterset = new Thing("xworker.ant.types.filterset");
		Thing batfilter = new Thing("xworker.ant.types.filterset/@filter");
		batfilter.set("token", "classPath");
		batfilter.set("value", "org.xmeta.util.ThingRunner");
		batfilterset.addChild(batfilter);
		copyrunbat.addChild(batfilterset);
		copyrunbat.doAction("toString", actionContext);
		
		//copy xer.ini
		Thing copyxerini = new Thing("xworker.ant.file.copy");
		copyxerini.set("file", new File(worldRoot, "deploy/xer.ini").getAbsolutePath());
		copyxerini.set("tofile", destDir + "/xer.ini");
		
		Thing inifilterset = new Thing("xworker.ant.types.filterset");
		Thing intfilter = new Thing("xworker.ant.types.filterset/@filter");
		intfilter.set("token", "thingPath");
		intfilter.set("value", thingPath);
		Thing intfilter1 = new Thing("xworker.ant.types.filterset/@filter");
		intfilter1.set("token", "actionName");
		intfilter1.set("value", method);
		inifilterset.addChild(intfilter);
		inifilterset.addChild(intfilter1);
		copyxerini.addChild(inifilterset);
		copyxerini.doAction("toString", actionContext);
	}

}
