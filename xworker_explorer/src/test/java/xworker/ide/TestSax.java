package xworker.ide;

import java.io.ByteArrayInputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestSax implements ContentHandler{
	 private Locator locator;
	
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");
			
			//启动编辑器			
			Thing thing = World.getInstance().getThing("xworker.ide.worldExplorer.swt.SimpleExplorer/@shell1");
			String xml = thing.doAction("toXml", new ActionContext());
			//System.out.println(xml);
			
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(new TestSax());
			
			ByteArrayInputStream bin = new ByteArrayInputStream(xml.getBytes());
			xr.parse(new InputSource(bin));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void characters(char[] chars, int start, int end) throws SAXException {
		//System.out.println("characters: start=" + start + ", end=" + end );
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		System.out.println("endElement: localName=" + localName);
		if(locator != null){
			System.out.println("   line=" + locator.getLineNumber() + ", column=" + locator.getColumnNumber());
		}
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] chars, int start, int end)
			throws SAXException {
		System.out.println("ignorableWhitespace: start=" + start + ", end=" + end );
		
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		System.out.println("startElement, localName=" + localName);
		if(locator != null){
			System.out.println("   line=" + locator.getLineNumber() + ", column=" + locator.getColumnNumber());
		}
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}
}
