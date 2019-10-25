package xworker.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

/**
 * 文件行的遍历。
 * 
 * @author zyx
 *
 */
public class FileLineItertor {
	public static Object run(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");	
		File file = FileActions.getFile(self, "getFile", actionContext);
		if(file == null || file.isDirectory()){
			throw new ActionException("Can not iterate null file or directory, file=" + file + ", action=" + self.getMetadata().getPath());
		}
		
		String charset = (String) self.doAction("getCharset", actionContext);
		
		FileInputStream fin = new FileInputStream(file);
		Object result = null;
		try{
			BufferedReader br = null;
			if(charset != null && !"".equals(charset)){
				br = new BufferedReader(new InputStreamReader(fin, charset));
			}else{
				br = new BufferedReader(new InputStreamReader(fin));
			}
			
			String line = br.readLine();
			int index = 0;
			actionContext.peek().setVarScopeFlag(); //启动一个本地变量范围
			String fileVarName = (String) self.doAction("getFileVarName", actionContext);
			String lineVarName = (String) self.doAction("getLineVarName", actionContext);
			String lineIndexName = lineVarName + "_index";
			String lineHasNextName = lineVarName + "_has_next";
			while(line != null){
				String nextLine = br.readLine();
				Thing childActions = self.getThing("ChildAction@0");
				if(childActions != null){
					actionContext.peek().put(fileVarName, file);
					actionContext.peek().put(lineVarName, line);
					actionContext.peek().put(lineIndexName, index);
					actionContext.peek().put(lineHasNextName, nextLine != null);
					
					for (Thing child : childActions.getChilds()) {
						// System.out.println(child.getMetadata().getPath());
						result = child.getAction().run(actionContext, null,	true);

						int sint = actionContext.getStatus();
						if (sint != ActionContext.RUNNING) {
							break;
						}
					}

					line = nextLine;
					int status = actionContext.getStatus();
					if (status == ActionContext.BREAK) {
						actionContext.setStatus(ActionContext.RUNNING);
						break;
					} else if (status == ActionContext.CONTINUE) {
						actionContext.setStatus(ActionContext.RUNNING);
						continue;
					} else if (status == ActionContext.RETURN) {
						break;
					}
				}else{
					line = nextLine;
				}
			}
			
			br.close();
		}finally{
			fin.close();
		}
		
		return result;
	}
}
