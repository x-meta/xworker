package xworker.ide.xmleditor;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;
import org.xml.sax.Attributes;

public class XmlThingLocation {
	int startRow;
	int startCol;
	int row;
	int col;
	int endRow;
	int endCol;
	int stackLayer;
	
	Thing thing;	
	List<XmlThingLocation> childs = new ArrayList<XmlThingLocation>();
	Attributes atts;
	
	public XmlThingLocation(Thing thing, int row, int col, Attributes atts){
		this.thing = thing;
		this.row = row;
		this.col = col;
		this.atts = atts;
		
		//System.out.println("NewLoc: row=" + row + ", col=" + col + ",thing=" + thing + ",attrs=" + atts.getLength());
	}
	
	public void add(XmlThingLocation loc){
		childs.add(loc);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Thing getThing() {
		return thing;
	}

	public Attributes getAtts() {
		return atts;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getEndCol() {
		return endCol;
	}

	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public int getStackLayer() {
		return stackLayer;
	}

	public void setStackLayer(int stackLayer) {
		this.stackLayer = stackLayer;
	}
}
