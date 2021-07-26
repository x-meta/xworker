package xworker.javafx.dataobject;

import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.util.UtilData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataObjectAttributeStringConverter extends StringConverter<Object> {
    Thing attribute;

    public DataObjectAttributeStringConverter(Thing attribute){
        this.attribute = attribute;
    }

    @Override
    public String toString(Object object) {
        if(object == null){
            return "";
        }

        String viewPattern = attribute.getStringBlankAsNull("viewPattern");
        if(viewPattern != null){
            if(object instanceof Number){
                NumberFormat nf = new DecimalFormat(viewPattern);
                return nf.format(object);
            }else if(object instanceof Date){
                SimpleDateFormat sf = new SimpleDateFormat(viewPattern);
                return sf.format(object);
            }
        }
        return String.valueOf(object);
    }

    @Override
    public Object fromString(String string) {
        if(string == null || string.isEmpty()){
            return null;
        }

        String type = attribute.getStringBlankAsNull("type");

        if("string".equals(type)){
            return string;
        }else if("byte".equals(type)){
            return UtilData.getByte(string, (byte) 0);
        }else if("short".equals(type)){
            return UtilData.getShort(string, (short) 0);
        }else if("int".equals(type)){
            return UtilData.getInt(string, 0);
        }else if("long".equals(type)){
            return UtilData.getLong(string, 0);
        }else if("float".equals(type)){
            return UtilData.getFloat(string, 0);
        }else if("double".equals(type)){
            return UtilData.getDouble(string, 0);
        }else if("boolean".equals(type)){
            return UtilData.getBoolean(string, false);
        }else{
            return string;
        }
    }
}
