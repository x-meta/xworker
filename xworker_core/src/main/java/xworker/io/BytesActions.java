package xworker.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BytesActions {
	public static byte[] bytesBuilder(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		OutputStreamUtils.writeChildDatas(self, bout, actionContext);
		return bout.toByteArray();
	}
}
