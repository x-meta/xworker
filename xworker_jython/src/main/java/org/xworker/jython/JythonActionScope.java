package org.xworker.jython;

import java.util.HashMap;
import java.util.Map;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyIterator;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyType;
import org.python.core.Untraversable;
import org.python.core.Visitproc;
import org.python.expose.ExposedGet;
import org.python.expose.ExposedMethod;
import org.python.expose.ExposedType;
import org.xmeta.ActionContext;

@SuppressWarnings("serial")
@Untraversable
@ExposedType(name = "scope", isBaseType = false)
public final class JythonActionScope extends PyObject {
	public static final PyType TYPE = PyType.fromClass(JythonActionScope.class);

    private final ActionContext context;

    JythonActionScope(ActionContext context) {
        this.context = context;
    }

    @ExposedGet(name = "context")
    public PyObject pyGetContext() {
        return Py.java2py(context);
    }

    @ExposedMethod
    public PyObject scope_keys() {
        PyList members = new PyList();
        for(String key : context.keySet()) {
        	members.append(new PyString(key));
        }
        members.sort();
        return members;
    }

    // satisfy mapping and lookup
    @ExposedMethod(names = "__getitem__")
    @Override
    public PyObject __getitem__(PyObject key) {
        return __finditem__(key);
    }

    // satisfy iterable
    @ExposedMethod
    @Override
    public PyObject __iter__() {
        return new ScopeIterator(this);
    }

    @ExposedMethod(defaults = "Py.None")
    final PyObject scope_get(PyObject keyObj, PyObject defaultObj) {
        String key = keyObj.asString();
        return Py.java2py(context.get(key));
    }

    @ExposedMethod
    final boolean scope_has_key(PyObject key) {
    	return context.containsKey(key.asString());
    }

    @Override
    public boolean __contains__(PyObject obj) {
        return scope___contains__(obj);
    }

    @ExposedMethod
    final boolean scope___contains__(PyObject obj) {
        return scope_has_key(obj);
    }

    @ExposedMethod(defaults = "Py.None")
    final PyObject scope_setdefault(PyObject keyObj, PyObject failObj) {
        PyObject result;
        String key = keyObj.asString();
        if(!context.containsKey(key)) {
        	context.peek().put(key, failObj.__tojava__(Object.class));
        	result = failObj;
        }else {
        	result = Py.java2py(context.get(key));
        }
        return result;
    }

    @Override
    public String toString() {
        return getDictionary().toString();
    }

    @Override
    public PyObject __finditem__(PyObject key) {
        return __finditem__(key.asString());
    }

    @Override
    public PyObject __finditem__(String key) {
    	Object obj = context.get(key);
    	if(obj == null) {
    		return null;
    	}else {
    		return Py.java2py(context.get(key));
    	}
    }

    @ExposedMethod
    @Override
    public void __setitem__(PyObject key, PyObject value) {
        __setitem__(key.asString(), value);
    }

    @Override
    public void __setitem__(String key, PyObject value) {
    	if(context.containsKey(key)) {
    		context.put(key,  value.__tojava__(Object.class));
    	}else {
    		context.peek().put(key, value.__tojava__(Object.class));
    	}
    }

    @ExposedMethod
    @Override
    public void __delitem__(PyObject key) {
        __delitem__(key.asString());
    }

    @Override
    public void __delitem__(String key) {
    	context.remove(key);
    }

    private Map<PyObject, PyObject> getMap() {
        ScopeIterator iterator = new ScopeIterator(this);
        Map<PyObject, PyObject> map = new HashMap<PyObject, PyObject>(iterator.size());
        PyObject key = iterator.__iternext__();
        while (key != null) {
            map.put(key, __finditem__(key));
            key = iterator.__iternext__();
        }
        return map;
    }

    private PyDictionary getDictionary() {
        return new PyDictionary(getMap());
    }

    
    @Override
	public boolean isMappingType() {
		return true;
	}


	public class ScopeIterator extends PyIterator {
		private int _index;
        private int _size;
        private PyObject _keys;

        ScopeIterator(JythonActionScope scope) {
            _keys = scope.scope_keys();
            _size = _keys.__len__();
            _index = -1;
        }

        public int size() {
            return _size;
        }

        @Override
        public PyObject __iternext__() {
            PyObject result = null;
            _index++;
            if (_index < size()) {
                result = _keys.__getitem__(_index);
            }
            return result;
        }


        /* Traverseproc implementation */
        @Override
        public int traverse(Visitproc visit, Object arg) {
            int retVal = super.traverse(visit, arg);
            if (retVal != 0) {
                return retVal;
            }
            return _keys != null ? visit.visit(_keys, arg) : 0;
        }

        @Override
        public boolean refersDirectlyTo(PyObject ob) {
            return ob != null && (ob == _keys || super.refersDirectlyTo(ob));
        }
    }

}
