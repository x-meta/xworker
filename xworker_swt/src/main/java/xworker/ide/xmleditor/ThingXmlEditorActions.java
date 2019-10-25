package xworker.ide.xmleditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xml.sax.SAXException;

import xworker.lang.actions.ActionContainer;
import xworker.swt.custom.StyledTextProxy;
import xworker.swt.custom.textutils.StyledTextContentInserter;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.xwidgets.SelectContent;

/**
 * 事物编辑器中的XML编辑器的相关动作。
 * 
 * @author zyx
 *
 */
public class ThingXmlEditorActions {
	public static ThingXmlDocument getThingXmlDocument(Control xmlText, ActionContext actionContext) throws ParserConfigurationException, SAXException, IOException{
		String docKey = "xworker.ide.xmleditor.ThingXmlDocument";
		ThingXmlDocument doc = (ThingXmlDocument) xmlText.getData(docKey);
		Thing currentThing = actionContext.getObject("currentThing");
		
		if(doc == null){
			if(StyledTextProxy.isStyledText(xmlText)) {
				doc = new ThingXmlDocument(currentThing.getMetadata().getPath(), StyledTextProxy.getText(xmlText));
			}else if(xmlText instanceof Text) {
				doc = new ThingXmlDocument(currentThing.getMetadata().getPath(), ((Text) xmlText).getText());
			}
			xmlText.setData(docKey, doc);
		}
		
		return doc;
	}
	

	/**
	 * 在xmlText中显示thing对应的xml代码。
	 * 
	 * @param xmlText
	 * @param thing
	 * @param actionContext
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void showSelection(Control xmlText, Thing thing, ActionContext actionContext) throws ParserConfigurationException, SAXException, IOException{
		String docKey = "xworker.ide.xmleditor.ThingXmlDocument";
		ThingXmlDocument doc = (ThingXmlDocument) xmlText.getData(docKey);		
		
		String content = SwtTextUtils.getText(xmlText);
		if(doc == null){
			doc = new ThingXmlDocument(thing.getRoot().getMetadata().getPath(), content);
			xmlText.setData(docKey, doc);
		}
			
		doc.parse(content, thing.getRoot().getMetadata().getPath());
		XmlThingLocation loc = doc.getThingLocation(thing);
		if(loc != null){
			//System.out.println("startCol=" + loc.startCol);
			int start = SwtTextUtils.getOffsetAtLine(xmlText, loc.startRow > 0 ? (loc.startRow - 1) : 0) + loc.startCol;
			int lineCount = SwtTextUtils.getLineCount(xmlText);
			int end = 0;
			if(loc.endRow >= lineCount){
				end = content.length();
			}else{
				end = SwtTextUtils.getOffsetAtLine(xmlText, loc.endRow) -1;
			}
				
			if(end == start){
				try{
					end = SwtTextUtils.getOffsetAtLine(xmlText, loc.endRow + 1) - 1;
				}catch(Exception e){					
				}
			}
			if(end > content.length()){
				end = content.length() - 1;
			}
			if(start > end){
				start = end;
			}
			if(start < 0){
				return;
			}
			//System.out.println("start=" + start + ",end=" + end);
			
			SwtTextUtils.setSelection(xmlText, start, end);
			SwtTextUtils.showSelection(xmlText);
		}
	}
	
	public static XmlThingLocation getXmlThingLocation(Object xmlText, ThingXmlDocument doc){
        int offset = SwtTextUtils.getSelection(xmlText).x;        
        int line = SwtTextUtils.getLineAtOffset(xmlText, offset);
        int column = offset - SwtTextUtils.getOffsetAtLine(xmlText, line);
        	        
        XmlThingLocation loc = doc.getThingLocation(line + 1, column);
        return loc;
	}
	
	/**
	 * 添加属性的 事件。 
	 * @param actionContext
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void addAttribute(final ActionContext actionContext) throws ParserConfigurationException, SAXException, IOException{
		
		final ActionContainer actions = actionContext.getObject("actions");
		final Control xmlText = actionContext.getObject("xmlText");
		xmlText.setFocus();
		Thing currentThing = actionContext.getObject("currentThing");		
		
		ThingXmlDocument doc = getThingXmlDocument(xmlText, actionContext);
		
		//添加属性
		doc.parse(SwtTextUtils.getText(xmlText), currentThing.getMetadata().getPath());
        XmlThingLocation loc = getXmlThingLocation(xmlText, doc);
        if(loc != null){
        	List<SelectContent> contents = new ArrayList<SelectContent>();
        	for(Thing attr : loc.getThing().getAllAttributesDescriptors()){
        		//if(loc.getAtts().getValue(attr.getMetadata().getName()) == null){
        			//属性没有在xml中出现，加入
        			String name = attr.getMetadata().getName();
        			String value = name;//name + "=\"\" ";
        			String label = attr.getMetadata().getLabel();
        			if(label.equals(name) == false){
        				label = name + " (" + label + ")"; 
        			}
        			SelectContent content = new SelectContent(value, label);
        			contents.add(content);
        		//}
        	}
        	Collections.sort(contents);
        	if(contents.size() > 0){
        		//ThingXmlEditorActions.showSelection(xmlText, loc.getThing(), actionContext);
        		
        		final String name = StyledTextContentInserter.open("插入属性", xmlText.getShell(), contents, actionContext);        		
        		if(name != null){
        			final Thing thing = loc.getThing();
        			Thing descriptor = thing.getAttributeDescriptor(name);

        			if(descriptor != null){
        				descriptor = descriptor.detach();
        				descriptor.put("group", null);
        				
        				ActionContext ac = new ActionContext();
        			    ac.put("parent", xmlText.getShell());
        			    ac.put("thing", thing.getRoot().getMetadata().getPath());
        			    ac.put("currentThing", thing);
        			    ac.put("ec", actionContext);
        			    
        			    Thing desthing = new Thing("xworker.lang.MetaDescriptor3");
        			    desthing.addChild(descriptor.detach());
        			    
        			    Thing newThing = new Thing(desthing.getMetadata().getPath());
        			    newThing.put(name, thing.get(name));
        			    
        			    Thing shellThing = World.getInstance().getThing("xworker.ide.worldExplorer.swt.command.EditAttributeShell");
        			    Shell shell = (Shell) shellThing.doAction("create", ac);
        			    shell.setText("编辑属性-" + descriptor.getMetadata().getName()  + "(" + descriptor.getMetadata().getLabel() + ")");
        			    Thing thingForm = ac.getObject("thingForm");
        			    thingForm.doAction("setThing", ac, "thing",  newThing);
        			    shell.open();
        			    thingForm.doAction("setFocus", ac);
        			    
        			    SwtDialog dialog = new SwtDialog(shell, ac);
        			    dialog.open(new SwtDialogCallback() {
							@Override
							public void disposed(Object result) {
								Map<String, Object> resultMap = (Map<String, Object>) result;
		        			    if(result != null){
		        			        thing.put(name, resultMap.get(name));
		        			        //重新刷新XML
		        			        actions.doAction("xmlEditorSetXmlThing", actionContext, "thing", thing.getRoot());
		        			        //actions.doAction("xmlEditorShowThing", actionContext, "thing", thing);        			       
		        			    }
							}
        			    	
        			    });
        			   
        			}
        		}
        	}
        }
        
        
	}
}
