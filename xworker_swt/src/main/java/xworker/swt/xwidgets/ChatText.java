package xworker.swt.xwidgets;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

/**
 * 聊天文本窗口。
 * 
 * @author Administrator
 *
 */
public class ChatText {
	public static final int EDITABLE = 1;
	Composite composite;
	JEditorPane editorPane;
	Document document;
	
	public ChatText(Composite parent, int style){
		composite = new Composite(parent, SWT.EMBEDDED);
		composite.addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent arg0) {
			}

			@Override
			public void controlResized(ControlEvent arg0) {
				if(editorPane != null) {
					editorPane.updateUI();
				}
			}
			
		});
		Frame frame = SWT_AWT.new_Frame(composite);

		Panel panel = new Panel();
		frame.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane = new JRootPane();
		panel.add(rootPane);

		editorPane = new JEditorPane();
		JScrollPane scroolPane = new JScrollPane(editorPane);
		editorPane.setContentType("text/html");
		rootPane.getContentPane().add(scroolPane, BorderLayout.CENTER);
		document = editorPane.getDocument();

		if((style & EDITABLE) == EDITABLE){
			editorPane.setEditable(true);
		}else{
			editorPane.setEditable(false);
		}
	}

	public Composite getComposite() {
		return composite;
	}

	public void appendHtml(String html) throws BadLocationException, IOException{
		//document.insertString(document.getLength(), text, null);
		HTMLDocument htmlDoc = (HTMLDocument) document;
		Element[] roots = htmlDoc.getRootElements(); // #0 is the HTML element, #1 the bidi-root
		Element body = null;
		for( int i = 0; i < roots[0].getElementCount(); i++ ) {
		    Element element = roots[0].getElement( i );
		    if( element.getAttributes().getAttribute( StyleConstants.NameAttribute ) == HTML.Tag.BODY ) {
		        body = element;
		        break;
		    }
		}
		if(body == null){
			body = roots[0];
		}
		htmlDoc.insertBeforeEnd(body, html);
		editorPane.setCaretPosition(htmlDoc.getLength());
		//htmlDoc.get
	}
	
	public void SetMaxPosition(){
		HTMLDocument htmlDoc = (HTMLDocument) document;
		editorPane.setCaretPosition(htmlDoc.getLength());
	}

	
	public static Composite create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int style = 0;
		if(self.getBoolean("EDITABLE")){
			style = style | ChatText.EDITABLE;
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		ChatText chatText = new ChatText(parent, style);
		
		//创建子节点
		Bindings bindings = actionContext.push();
		bindings.put("parent", chatText.getComposite());
		try{
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), chatText);
		return chatText.getComposite();
	}
}
