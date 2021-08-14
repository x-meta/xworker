package xworker.ide.xmleditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xmeta.Thing;
import org.xmeta.codes.XmlCoder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ThingXmlDocument implements ContentHandler{
	private Locator locator;
	Thing thing;	
	List<XmlThingLocation> locs = new ArrayList<XmlThingLocation>();
	Stack<XmlThingLocation> locStacks = new Stack<XmlThingLocation>();
	String xml = null;
		
	public ThingXmlDocument(String thingPath, String xml) throws ParserConfigurationException, SAXException, IOException{
        parse(xml, thingPath);
	}
	
	public void parse(String xml, String thingPath) throws ParserConfigurationException, SAXException, IOException{
		//空或已经分析过，则不用分析
		if(xml == null || "".equals(xml) || xml.equals(this.xml)){
			return;
		}
		
		locs.clear();
		locStacks.clear();
		
		thing = new Thing();
        XmlCoder.parse(thing, xml);
        
        XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		
		ByteArrayInputStream bin = new ByteArrayInputStream(xml.getBytes());
		xr.parse(new InputSource(bin));
		
		this.xml = xml;
		
		//设置真实的路径，用于寻找真实的节点
		thing.getMetadata().setPath(thingPath);
		thing.initChildPath();
		
		/*
		System.out.println("first");
		for(int i=0; i<locs.size(); i++){
			XmlThingLocation loc = locs.get(i);
			if(loc != null){
				System.out.println("    row=" + loc.row + ",endRow=" + loc.endRow + ",startRow=" + loc.startRow + ", thing=" + loc.getThing());
			}else{
				System.out.println("    null");
			}
		}*/
		
		for(int i=0; i<locs.size(); i++){
			XmlThingLocation loc = locs.get(i);
			if(loc == null){
				continue;
			}
			
			XmlThingLocation ploc = getPreLoc(loc, i);
			if(ploc != null){
				loc.startCol = ploc.getEndCol() + 1;
				loc.startRow = ploc.getEndRow();
			}else{
				ploc = getParentLoc(loc, i);
				if(ploc != null){
					loc.startCol = ploc.getCol() + 1;
					loc.startRow = ploc.getRow();
				}
			}
		}
		
		/*
		System.out.println("second");
		for(int i=0; i<locs.size(); i++){
			XmlThingLocation loc = locs.get(i);
			if(loc != null){
				System.out.println("    row=" + loc.row + ",endRow=" + loc.endRow + ",startRow=" + loc.startRow + ", thing=" + loc.getThing());
			}else{
				System.out.println("    null");
			}
		}*/
	}
	
	public XmlThingLocation getParentLoc(XmlThingLocation loc, int index){
		for(int i=index - 1; i>=0; i--){
			XmlThingLocation ploc = locs.get(i);
			if(ploc == null){
				continue;
			}
			if(loc.getStackLayer() - ploc.getStackLayer() == 1){
				return ploc;
			}
		}
		
		return null;
	}
	
	public XmlThingLocation getPreLoc(XmlThingLocation loc, int index){
		for(int i=index - 1; i>=0; i--){
			XmlThingLocation ploc = locs.get(i);
			if(ploc == null){
				continue;
			}
			if(loc.getStackLayer() == ploc.getStackLayer()){
				return ploc;
			}else if(loc.getStackLayer() > ploc.getStackLayer() ){
				return null;
			}
		}
		
		return null;
	}
	
	public XmlThingLocation getThingLocation(Thing thing){
		if(thing == null){
			return null;
		}
		
		for(int i=0; i<locs.size(); i++){			
			XmlThingLocation loc = locs.get(i);
			if(loc == null){
				continue;
			}
			
			Thing t = loc.getThing();
			if(t != null && t.getMetadata().getPath().equals(thing.getMetadata().getPath())){
				return loc;
			}
		}
		
		return null;
	}
	
	public XmlThingLocation getThingLocation(int line, int column){
		Stack<XmlThingLocation> stacks = new Stack<XmlThingLocation>();
		//System.out.println(line);
		for(int i=0; i<locs.size(); i++){			
			XmlThingLocation loc = locs.get(i);
			if(loc == null){
				continue;
			}
			
			if(loc != null && (line >= loc.startRow && line <= loc.endRow)){
				//System.out.println("    row=" + loc.row + ",endRow=" + loc.endRow + ",startRow=" + loc.startRow + ", thing=" + loc.getThing().getMetadata().getPath());
				stacks.push(loc);
			}else{	
				//System.out.println("    next: row=" + loc.row + ",startRow=" + loc.startRow + ", thing=" + loc.getThing().getMetadata().getPath());
				//break;
			}
		}
		
		//正向查找
		List<XmlThingLocation> matchLocs = new ArrayList<XmlThingLocation>();
		for(int i=stacks.size() -1; i>=0; i--){
			XmlThingLocation sloc = stacks.get(i);
			if(sloc.row == line){
				matchLocs.add(sloc);
			}else if(sloc.endRow >= line){
				matchLocs.add(sloc);
			}
		}
		if(matchLocs.size() > 0){
			//先过滤一些不可能的
			for(int i=0; i<matchLocs.size() - 1; i++){
				XmlThingLocation loc1 = matchLocs.get(i);
				//在起始行的前面
				if(loc1.startRow == line && loc1.startCol > column){
					matchLocs.remove(i);
					i--;
					continue;
				}
				//在截至行的后面
				if(loc1.endRow == line && loc1.endCol < column){
					matchLocs.remove(i);
					i--;
					continue;
				}
				
				//父子关系时，过滤父节点
				XmlThingLocation loc2 = matchLocs.get(i + 1);
				if(loc1.endRow < loc2.endRow){
					//loc1是loc2的父节点
					matchLocs.remove(i + 1);
					i--;
				}
			}
			
			//优先判断row和endRow
			for(XmlThingLocation loc : matchLocs){
				if(loc.row == line || loc.endRow == line){
					if(loc.col >= column || loc.endCol >= column){
						return loc;
					}
				}
			}
			
			for(XmlThingLocation loc : matchLocs){
				if(loc.startRow == line && loc.startCol > column){
					continue;
				}
				if(loc.col >= column || loc.endCol >= column){
					return loc;
				}
			}
			
			return matchLocs.get(0);
		}
			
		return locs.size() > 0 ? locs.get(0) : null;
	}
	
	@Override
	public void characters(char[] chars, int start, int end) throws SAXException {
		//System.out.println("characters: row=" + locator.getLineNumber() + ", col=" + locator.getColumnNumber() + ", chars=" + new String(chars, start, end));
	}

	@Override
	public void endDocument() throws SAXException {
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//System.out.println("End element: " + localName + ",line=" + locator.getLineNumber() + ",column=" + locator.getColumnNumber());
		XmlThingLocation loc = locStacks.pop();
		if(loc != null && locator != null){
			loc.endCol = locator.getColumnNumber();
			loc.endRow = locator.getLineNumber();
		}else{
			for(int i=locStacks.size() -1; i>=0; i--){
				XmlThingLocation ploc = locStacks.get(i);
				if(ploc != null && ploc.endRow == 0 && ploc.endCol == 0){
					ploc.row = locator.getLineNumber();
					ploc.col = locator.getColumnNumber();
					break;
				}
			}
		}
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] chars, int start, int end)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		//System.out.println(arg0 + ":" + arg1);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		//System.out.println("Start element: " + localName + ",line=" + locator.getLineNumber() + ",column=" + locator.getColumnNumber());
		XmlThingLocation loc = null;
		if(locStacks.size() == 0){
			loc = new XmlThingLocation(thing, locator.getLineNumber(), locator.getColumnNumber(), atts);
			loc.setStackLayer(locStacks.size());
		}else{
			XmlThingLocation parentLoc = locStacks.peek();
			if(parentLoc != null){
				if(parentLoc.getThing() != null){
					Thing child = parentLoc.getThing().getChildAt(parentLoc.childs.size());
					if(child != null) {
						String thingName = child.getThingName();
						if(thingName.startsWith("#")) {
							thingName = thingName.replace('#', '_');
						}
						if(localName.equals(thingName)){
							loc = new XmlThingLocation(child, locator.getLineNumber(), locator.getColumnNumber(), atts);
							//起始的行和列是预估的
							loc.setStartCol(parentLoc.col + 1);
							loc.setStartRow(parentLoc.row);
							loc.setStackLayer(locStacks.size());
							parentLoc.add(loc);
						}
					}
				}
			}		

		}
		
		locStacks.push(loc);	
		locs.add(loc);
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}
}
