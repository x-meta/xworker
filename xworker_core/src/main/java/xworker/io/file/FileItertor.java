package xworker.io.file;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class FileItertor {
	Thing thing;
	ActionContext actionContext;
	
	Pattern includePattern;
	Pattern excludePattern;
	boolean includeDir = true;
	IOFileFilter filter = null;
	FileStack fileStack = null;
	
	public FileItertor(Thing thing, File dir, IOFileFilter filter,  ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		fileStack = new FileStack(this, new File[]{dir});		
		if(thing != null){
			String include = (String) thing.doAction("getIncludePattern", actionContext);
			if(include != null && !"".equals(include.trim())){
				includePattern = Pattern.compile(include);
			}
			
			String exclude = (String) thing.doAction("getExcludePattern", actionContext);
			if(exclude != null && !"".equals(exclude.trim())){
				excludePattern = Pattern.compile(exclude);
			}
			
			includeDir = UtilData.isTrue(thing.doAction("isIncludeDir", actionContext));
		}
		this.filter = filter;
	}
	
	public Object run() throws IOException{
		int index = 0;
		boolean hasNext = hasNext();
		Object result = null;
		String varName = "file";
		String varIndexName = "file_index";
		String varHasNextName = "file_has_next";
		if(thing != null){
			varName = (String) thing.doAction("getVarName", actionContext);
			varIndexName = varName + "_index";
			varHasNextName = varName + "_has_next";
		}
		actionContext.peek().setVarScopeFlag(); //启动一个本地变量范围
		while(hasNext){
			File file = next();
			hasNext = hasNext();			
			if(thing != null){							
				Thing childActions = thing.getThing("ChildAction@0");
				if(childActions != null){
					actionContext.peek().put(varName, file);
					actionContext.peek().put(varIndexName, index);
					actionContext.peek().put(varHasNextName, hasNext);
					
					for (Thing child : childActions.getChilds()) {
						// System.out.println(child.getMetadata().getPath());
						result = child.getAction().run(actionContext, null,
								true);

						int sint = actionContext.getStatus();
						if (sint != ActionContext.RUNNING) {
							break;
						}
					}

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
				}
			}
			
			index++;
		}
		
		return result;
	}
	
	public boolean hasNext() throws IOException{
		return fileStack.hasNext();
	}
	
	public File next() throws IOException{
		return fileStack.next();
	}
	
	public boolean isIncluded(File file) throws IOException{
		if(file.isDirectory() && includeDir == false) {
			return true;
		}
		
		String path = file.getCanonicalPath();
		
		if(includePattern != null && !includePattern.matcher(path).matches()){
			return false;
		}
		
		if(excludePattern != null && excludePattern.matcher(path).matches()){
			return false;
		}
		
		if(filter != null){
			return filter.accept(file);
		}
		
		return true;
	}
	
	static class FileStack{
		File[] files;
		int index = 0;
		boolean checked = false;
		FileItertor fileIterator;
		FileStack fileStack = null;
		
		public FileStack(FileItertor fileIterator, File[] files){
			this.fileIterator = fileIterator;
			this.files = files;
		}
		
		public File next() throws IOException{
			if(!checked){
				if(!hasNext()){
					return null;
				}
			}
			
			checked = false;
			if(fileStack != null){
				return fileStack.next();
			}else{
				File file = files[index];
				
				if(file.isDirectory()){
					fileStack = new FileStack(fileIterator, file.listFiles());					
				}else{					
					index++;
				}
				
				return file;
			}			
		}		
		
		public boolean hasNext() throws IOException{
			//fileStack是遍历到子目录的时候
			if(fileStack != null){
				if(fileStack.hasNext()){
					checked = true;
					return true;
				}else{
					fileStack = null;
					index++;
				}
			}
			
			if(index >= files.length){
				return false;
			}else{
				for(int i=index; i<files.length; i++){
					File file = files[i];
					if(fileIterator.isIncluded(file)){
						if(file.isDirectory()){
							fileStack = new FileStack(fileIterator, file.listFiles());
							if(fileStack.hasNext()){
								index = i; 
								checked = true;
								return true;
							}else{
								fileStack = null;
							}
						}else{
							index = i;
							checked = true;
							return true;
						}
					}
				}
				
				return false;
			}
		}
	}
	
	public static void main(String args[]){
		try{
			FileItertor fiter = new FileItertor(null, new File("f:\\work\\testexample\\"), null, null);
			fiter.includeDir = false;
			fiter.run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Object fileIterator(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = FileActions.getFile(self, "getDir", actionContext);
		
		IOFileFilter filter = (IOFileFilter) self.doAction("getFilter", actionContext);
		
		FileItertor fiter = new FileItertor(self, file, filter, actionContext);
		return fiter.run();
	}
	
}

