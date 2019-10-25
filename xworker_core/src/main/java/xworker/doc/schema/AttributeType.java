package xworker.doc.schema;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import freemarker.template.utility.StringUtil;

/**
 * 事物对应的属性定义。
 * 
 * @author bookroom
 *
 */
public class AttributeType {
	String name;
	List<String> values;
	
	public AttributeType(String name, List<String> values){
		this.name = name;
		this.values = values;
	}
	
	public void write(BufferedWriter bw) throws IOException{
		bw.write("    <xsd:simpleType name=\"" + name + "\">\r\n");
		bw.write("        <xsd:restriction base=\"xsd:string\">\r\n");
		for(String value : values){
			bw.write("            <xsd:enumeration value=\"" + StringUtil.XHTMLEnc(value) + "\"/>\r\n");
		}
		bw.write("        </xsd:restriction>\r\n");
		bw.write("    </xsd:simpleType>\r\n");
	}
}
