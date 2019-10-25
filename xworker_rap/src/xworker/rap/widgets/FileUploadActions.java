package xworker.rap.widgets;

import java.io.File;

import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;

public class FileUploadActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite parent = actionContext.getObject("parent");
		
		int style = SWT.NONE;
		if(self.getBoolean("MULTI")) {
			style = style | SWT.MULTI;
		}
		
		FileUpload fileUpload = new FileUpload( parent, style);
		String text = self.getString("text", "FileUpload", actionContext);
		if(text != null) {
			fileUpload.setText(text);
		}
		
		String extensions = self.getString("extensions", null, actionContext);
		if(extensions != null && !"".equals(extensions.trim())) {
			fileUpload.setFilterExtensions(extensions.split("[,]"));
		}
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", fileUpload);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
        	fileUpload.setImage((Image) image);
        }

		if (self.getBoolean("useDefaultHandler")) {			
			fileUpload.addSelectionListener(new FileUploadSelection(fileUpload, self, actionContext));
		}
		
		actionContext.peek().put("parent", fileUpload);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		Designer.attach(fileUpload, self.getMetadata().getPath(), actionContext);
		actionContext.g().put(self.getMetadata().getName(), fileUpload);
		return fileUpload;
	}
	
	public static void uploadProgress(ActionContext actionContext) {
		FileUploadEvent event = actionContext.getObject("event");
		StringBuilder sb = new StringBuilder("FileUpload:unploadProgress\n");
		sb.append("    FileDetails: " + event.getFileDetails() + "\n");
		sb.append("    ContentLength: " + event.getContentLength() + ", BytesRead: " + event.getBytesRead());
		System.out.println(sb);
	}
	
	public static void uploadFailed(ActionContext actionContext) {
		FileUploadEvent event = actionContext.getObject("event");
		StringBuilder sb = new StringBuilder("FileUpload:uploadFailed\n");
		sb.append("    FileDetails: " + event.getFileDetails() + "\n");
		sb.append("    ContentLength: " + event.getContentLength() + ", BytesRead: " + event.getBytesRead());
		sb.append("    Exception: " + event.getException());
		System.out.println(sb);
	}
	
	public static void uploadFinished(ActionContext actionContext) {
		FileUploadEvent event = actionContext.getObject("event");		
		StringBuilder sb = new StringBuilder("FileUpload:uploadFinished\n");
		sb.append("    FileDetails: " + event.getFileDetails() + "\n");
		sb.append("    ContentLength: " + event.getContentLength() + ", BytesRead: " + event.getBytesRead());
		System.out.println(sb);
	}
	
	static class FileUploadSelection extends SelectionAdapter{
		private static final long serialVersionUID = 2416683192392424552L;
		FileUpload fileUpload;
		Thing self;
		ActionContext actionContext;
		
		public FileUploadSelection(FileUpload fileUpload, Thing self, ActionContext actionContext) {
			this.fileUpload = fileUpload;
			this.self = self;
			this.actionContext = actionContext;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileUploadSelListener fu = new FileUploadSelListener(fileUpload, self, actionContext);
			fileUpload.submit(fu.uploadHandler.getUploadUrl());
			System.out.println("submit to server, " + fu.uploadHandler.getUploadUrl());
		}
	}
	
	public static Object getFileUploadReceiver(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		File dir = self.doAction("getFileDir", actionContext);
		if(dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		return new XDiskFileUploadReceiver(dir);
	}
	
	static class FileUploadSelListener extends SelectionAdapter{
		private static final long serialVersionUID = 1L;
		
		FileUpload fileUpload;
		Thing self;
		ActionContext actionContext;
		
		FileUploadReceiver receiver;
		FileUploadHandler uploadHandler = null;
		
		public FileUploadSelListener(FileUpload fileUpload, Thing self, ActionContext actionContext) {
			this.fileUpload = fileUpload;
			this.self = self;
			this.actionContext = actionContext;
			receiver = self.doAction("getFileUploadReceiver", actionContext);			
			uploadHandler = new FileUploadHandler(receiver);
			
			uploadHandler.addUploadListener(new FileUploadListener() {
				public void uploadProgress(FileUploadEvent event) {
					try {
						FileUploadSelListener.this.self.doAction("uploadProgress", FileUploadSelListener.this.actionContext, 
								"fileUpload", FileUploadSelListener.this.fileUpload, "receiver", receiver, 
								"uploadHandler", uploadHandler, "event", event);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
	
				public void uploadFailed(FileUploadEvent event) {
					try {
						FileUploadSelListener.this.self.doAction("uploadFailed", FileUploadSelListener.this.actionContext, 
								"fileUpload", FileUploadSelListener.this.fileUpload, "receiver", receiver, 
								"uploadHandler", uploadHandler, "event", event);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
	
				public void uploadFinished(FileUploadEvent event) {
					try {
						FileUploadSelListener.this.self.doAction("uploadFinished", FileUploadSelListener.this.actionContext, 
								"fileUpload", FileUploadSelListener.this.fileUpload, "receiver", receiver, 
								"uploadHandler", uploadHandler, "event", event);
					}catch(Exception e) {
						e.printStackTrace();
					}
	
				}
			});
						
			fileUpload.addListener(SWT.Dispose, new Listener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void handleEvent(Event event) {
					uploadHandler.dispose();
				}
				
			});
			
			if(self.getStringBlankAsNull("maxFileSize") != null) {
				uploadHandler.setMaxFileSize(self.getLong("maxFileSize"));
			}
			if(self.getStringBlankAsNull("timeLimit") != null) {
				uploadHandler.setUploadTimeLimit(self.getLong("timeLimit"));
			}			
		}

	}
	
	public static void openFileUploadDialog(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		Shell parent = self.doAction("getShell", actionContext);
		boolean MULTI = self.doAction("isMULTI", actionContext);
		String filterExtensions = self.doAction("getFilterExtensions", actionContext);
		long maxFileSize = self.doAction("getMaxFileSize", actionContext);
		long timeLimit = self.doAction("getTimeLimit", actionContext);
		String text = self.doAction("getText", actionContext);
		
		final FileDialog fileDialog = new FileDialog( parent, MULTI ? SWT.MULTI : SWT.NONE);
		if(text != null) {
			fileDialog.setText(text);
		}
		if(filterExtensions != null && !"".equals(filterExtensions.trim())) {
			fileDialog.setFilterExtensions(filterExtensions.split("[,]"));
		}
		if(maxFileSize > 0) {
			fileDialog.setUploadSizeLimit(maxFileSize);
		}
		if(timeLimit > 0) {
			fileDialog.setUploadTimeLimit(timeLimit);
		}

		// JEE compatibility mode:
		fileDialog.open(new DialogCallback() {
			private static final long serialVersionUID = -2624410795321029773L;

			public void dialogClosed(int returnCode) {
				try {
					self.doAction("dialogClosed", actionContext, "fileDialog", fileDialog);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
