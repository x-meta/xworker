package xworker.lang.actions.thing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.doc.schema.ThingDocument;

public class ThingActions {
	public static void generateThingSchema(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		Thing thing = self.doAction("getThing", actionContext);
		Object output = self.doAction("getOutput", actionContext);
		
		ThingDocument tdoc = new ThingDocument(thing);
		if(output instanceof String) {
			FileOutputStream fout = new FileOutputStream((String) output);
			try {
				tdoc.write(fout);
			}finally {
				fout.close();
			}
		}else if(output instanceof File) {
			FileOutputStream fout = new FileOutputStream((File) output);
			try {
				tdoc.write(fout);
			}finally {
				fout.close();
			}
		}else if(output instanceof OutputStream) {
			tdoc.write((OutputStream) output);
		}else {
			throw new IOException("Not support output " + output + ", only support String\\File\\OutputStream, action=" + self.getMetadata().getPath());
		}
	}
	
	public static String getShortenPath(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing thing = self.doAction("getThing", actionContext);
		if(thing == null) {
			thing = self;
		}
		
		StringBuilder sb = new StringBuilder(thing.getRoot().getMetadata().getPath());
		sb.append(".p");
		String hashCode = String.valueOf(thing.getMetadata().getPath().hashCode());
		sb.append(hashCode.replace('-', '_'));
		sb.append(".");
		sb.append(thing.getMetadata().getName());
		
		return sb.toString();
	}

}
