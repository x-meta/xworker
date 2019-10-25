package xworker.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class OutputStreamUtils {
	/**
	 * 把事物的子节点当作动作执行，结果写入到输出流中。
	 * 
	 * 忽略actions子节点，忽略null值和不支持的对象。
	 * 
	 * @param thing
	 * @param out
	 * @param actionContext
	 * @throws IOException
	 */
	public static void writeChildDatas(Thing thing, OutputStream out, ActionContext actionContext) throws IOException {
		//获取变量名
		String varName = thing.doAction("getVarName", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, out);
		}
		
		boolean close = UtilData.isTrue(thing.doAction("isClose", actionContext));
		boolean closeInputStream = UtilData.isTrue(thing.doAction("isCloseInputStream", actionContext));
		
		try {
			for(Thing child : thing.getChilds()) {
				if("actions".equals(child.getMetadata().getName())) {
					continue;
				}
				
				Object result = child.getAction().run(actionContext);
				if(result instanceof byte[]) {
					out.write((byte[]) result); 
				}else if(result instanceof Byte) {
					out.write((Byte) result); 
				}else if(result instanceof Integer) {
					out.write((Integer) result);
				}else if(result instanceof ByteBuffer) {
					ByteBuffer buf = (ByteBuffer) result;
					if(buf.hasRemaining()) {
						byte[] bytes = new byte[buf.remaining()];
						buf.get(bytes);
						out.write(bytes);
					}
				}else if(result instanceof InputStream) {
					InputStream in = (InputStream) result;
					IOUtils.copy(in, out);
					if(closeInputStream) {
						in.close();
					}
				}else if(result instanceof File) {
					File file = (File) result;
					if(file.isFile()) {
						FileUtils.copyFile(file, out);
					}
				}else if(result instanceof String) {
					out.write(((String) result).getBytes());
				}
			}
		}finally {
			if(close) {
				out.close();
			}
		}
	}
}
