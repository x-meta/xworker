package xworker.lang.updator;

import ognl.Ognl;
import ognl.OgnlException;

public class OgnlAttributeSetter implements AttributeSetter{

	@Override
	public void setAttribute(Object obj, String name, Object value) throws OgnlException {
		Ognl.setValue(name, obj, value);
	}

}
