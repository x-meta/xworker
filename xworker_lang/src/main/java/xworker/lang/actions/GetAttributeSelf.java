package xworker.lang.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class GetAttributeSelf {
	public static Object run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String type = self.getStringBlankAsNull("type");
		Thing parentSelf = null;
		List<Thing> things = actionContext.getThings();
        if(things.size() > 1){
        	parentSelf = things.get(things.size() - 2);
        }
		
        //获取事物定义的属性的值
		String attributeName = self.getStringBlankAsNull("attributeName");
		String value = parentSelf.getStringBlankAsNull(attributeName);
		if(value != null && value.startsWith("ognl:")){			
			return OgnlUtil.getValue(value.substring(5, value.length()), actionContext);
		}else if(value != null && value.startsWith("var:")){
			return actionContext.get(value.substring(4, value.length()));
		}else{
			if("bigDecimal".equals(type)){
				return parentSelf.getBigDecimal(attributeName);
			}else if("bigInteger".equals(type)){
				return parentSelf.getBigInteger(attributeName);
			}else if("boolean".equals(type)){
				return parentSelf.getBoolean(attributeName);
			}else if("byte".equals(type)){
				return parentSelf.getByte(attributeName);
			}else if("bytes".equals(type)){
				return parentSelf.getBytes(attributeName);
			}else if("char".equals(type)){
				return parentSelf.getChar(attributeName);
			}else if("date".equals(type)){
				return parentSelf.getData(attributeName);
			}else if("double".equals(type)){
				return parentSelf.getDouble(attributeName);
			}else if("float".equals(type)){
				return parentSelf.getFloat(attributeName);
			}else if("int".equals(type)){
				return parentSelf.getInt(attributeName);
			}else if("long".equals(type)){
				return parentSelf.getLong(attributeName);
			}else if("short".equals(type)){
				return parentSelf.getShort(attributeName);
			}else if("object".equals(type)){
				return parentSelf.getObject(attributeName, actionContext);
			}else{
				String str = UtilString.getString(value, actionContext);
				if("".equals(str) && self.getBoolean("stringBlankAdNull")){
					return null;
				}else{
					return str;
				}
			}
		}
	}
}
