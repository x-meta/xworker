package xworker.swt.custom;

import java.io.InputStream;

import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ExceptionUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.swt.functions.AutoScroll;
import xworker.swt.util.SwtUtils;

public class TextInputStreamRunnable implements Runnable, AutoScroll{
	private static final String TAG = TextInputStreamRunnable.class.getName();
	
	Text text;
	InputStream inputStream;
	boolean autoScroolToBottom;
	String charset;
	
	public TextInputStreamRunnable(Text text, InputStream inputStream, boolean autoScroolToBottom, String charset){
		this.text = text;
		this.inputStream = inputStream;
		this.autoScroolToBottom = autoScroolToBottom;
		this.charset = charset;

		SwtUtils.regist(text, this, AutoScroll.class);
	}
	
	public void setAutoScroolToBottom(boolean autoScroolToBottom){
		this.autoScroolToBottom = autoScroolToBottom;
	}
	
	public void run(){
		try{
			byte[] bytes = new byte[1024];
			int length = -1;
			while((length = inputStream.read(bytes)) != -1){
				final String str = new String(bytes, 0, length, charset);
				if(text == null || text.isDisposed()){
					break;
				}
				
				text.getDisplay().asyncExec(new Runnable(){
					public void run(){
						text.append(str);
						
						if(autoScroolToBottom){
							 text.setTopIndex(text.getLineCount() - 1);
							 //text.showSelection();
						}
					}
				});			
			}					
		}catch(final Exception e){
			Executor.info(TAG, "get data form inputStream error", e);
			
			if(text != null && text.isDisposed() == false) {
				//是InputStream结束了
				text.getDisplay().asyncExec(new Runnable(){
					public void run(){
						text.append(ExceptionUtil.toString(e));
						
						if(autoScroolToBottom){
							 text.setTopIndex(text.getLineCount() - 1);
							 //text.showSelection();
						}
					}
				});
			}
		}
	}

	@Override
	public boolean isAutoScroll() {
		return autoScroolToBottom;
	}

	@Override
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroolToBottom = autoScroll;
		
		if(autoScroolToBottom){
			 text.setTopIndex(text.getLineCount() - 1);
			 //text.setCaretOffset(text.getText().length());
		}
	}

	/**
	 * 绑定InputStream到StyledText。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException
	 */
	public static Object bindInputStream(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("data");
		Text text = (Text) self.doAction("getStyledText", actionContext);
		InputStream inputStream = (InputStream) self.doAction("getInputStream", actionContext);
		boolean autoScroolToBottom = self.getBoolean("autoScroolToBottom");
		
		if(text != null && inputStream != null){
			String charset = self.getStringBlankAsNull("charset");
			if(charset == null){
				charset = "utf-8";
			}
			TextInputStreamRunnable run = new TextInputStreamRunnable(text, inputStream, autoScroolToBottom, charset);
			if(self.getBoolean("createThread")){
				new Thread(run).start();
			}else{
				run.run();
			}
			
			return run;
		}else{
			return null;
		}
	}
}
