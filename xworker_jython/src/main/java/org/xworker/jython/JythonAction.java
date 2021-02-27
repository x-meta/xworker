package org.xworker.jython;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import xworker.lang.executor.Executor;

public class JythonAction {
	private static final String TAG = JythonAction.class.getName();
	static {
		Properties props = new Properties();
		props.put("python.console.encoding", "UTF-8"); // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
		props.put("python.security.respectJavaAccessibility", "false"); //don't respect java accessibility, so that we can access protected members on subclasses
		//props.put("python.import.site","false");
		Properties preprops = System.getProperties();
		PythonInterpreter.initialize(preprops, props, new String[0]);
	}
	
	private final ThingEntry thingEntry;
	private final PythonInterpreter interp;
	private PyCode code;
	
	public JythonAction(Thing thing, ActionContext actionContext) throws UnsupportedEncodingException, IOException {
		thingEntry = new ThingEntry(thing);
		interp = PythonInterpreter.threadLocalStateInterpreter(null);
		interp.setIn(System.in);
		interp.setErr(System.err);
		interp.setOut(System.out);
		
		init(actionContext);
	}
	
	public void init(ActionContext actionContext) throws UnsupportedEncodingException, IOException {
		Thing thing = thingEntry.getThing();
		Action action = thing.getAction();
		
		File pyFile = thing.doAction("getPyFile", actionContext);
		if(pyFile == null) {
			pyFile = new File(action.getFileName() + ".py");
			if(!pyFile.exists()){
				pyFile.getParentFile().mkdirs();
			}
			
			FileOutputStream fout = new FileOutputStream(pyFile);
			try{					
				fout.write(("# path:" + action.getThing().getMetadata().getPath() + "\n").getBytes());
				fout.write(action.getCode().getBytes("utf-8"));
				
			}finally{
				fout.close();
			}
		}
		
		if(pyFile != null && pyFile.exists()) {
			code = interp.compile(new FileReader(pyFile), pyFile.getName());
		} else {
			Executor.warn(TAG, "Py file not exists or not set code, path=" + thing.getMetadata().getPath());
		}
	}
	
	public Object eval(ActionContext actionContext) throws UnsupportedEncodingException, IOException {
		try {
			if(thingEntry.isChanged()) {
				init(actionContext);
			}
			 
            interp.setLocals(new JythonActionScope(actionContext));
            
            if(code != null) {
            	return interp.eval(code).__tojava__(Object.class);
            }else {
            	return null;
            }
            
        } catch (PyException pye) {
            throw new ActionException(pye);
        }		
	}
	
	public static Object run(ActionContext actionContext) throws UnsupportedEncodingException, IOException {
		//脚本的上下文		
		Bindings bindings = actionContext.getScope(actionContext.getScopesSize() - 2);		
		
		//World world = bindings.world;
		Action action = null;
		if(bindings.getCaller() instanceof Thing){
			Thing actionThing = (Thing) bindings.getCaller();
			action = actionThing.getAction();
			action.checkChanged();
		}else{
			action = (Action) bindings.getCaller();			
		}
		
		Thing thing = action.getThing();
		JythonAction ja = thing.getData(TAG);
		if(ja == null) {
			ja = new JythonAction(thing, actionContext);
			thing.setData(TAG, ja);
		}
		
		return ja.eval(actionContext);
	}
}
