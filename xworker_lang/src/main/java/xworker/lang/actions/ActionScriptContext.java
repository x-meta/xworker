/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.lang.actions;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.xmeta.ActionContext;

public class ActionScriptContext implements ScriptContext{
    
    protected Writer writer;
    
    protected Writer errorWriter;
    
    protected Reader reader;
    
    protected Bindings engineScope;
    
    protected Bindings globalScope;
    
    
    public ActionScriptContext(ActionContext actionContext) {       
        engineScope = new ActionContextBindings(actionContext);        
        globalScope = new ActionBindings(actionContext.getScope(0));
        reader = new InputStreamReader(System.in);
        writer = new PrintWriter(System.out , true);
        errorWriter = new PrintWriter(System.err, true);        
    }
    
    public void setBindings(Bindings bindings, int scope) {
        
        switch (scope) {
            
            case ENGINE_SCOPE:
                if (bindings == null) {
                    throw new NullPointerException("Engine scope cannot be null.");
                }
                engineScope = bindings;
                break;
            case GLOBAL_SCOPE:
                globalScope = bindings;
                break;
            default:
                throw new IllegalArgumentException("Invalid scope value.");
        }
    }
    
    public Object getAttribute(String name) {
        if (engineScope.containsKey(name)) {
            return getAttribute(name, ENGINE_SCOPE);
        } else if (globalScope != null && globalScope.containsKey(name)) {
            return getAttribute(name, GLOBAL_SCOPE);
        }
        
        return null;
    }
    
    public Object getAttribute(String name, int scope) {
        
        switch (scope) {
            
            case ENGINE_SCOPE:
                return engineScope.get(name);
                
            case GLOBAL_SCOPE:
                if (globalScope != null) {
                    return globalScope.get(name);
                }
                return null;
                
            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    public Object removeAttribute(String name, int scope) {
        
        switch (scope) {
            
            case ENGINE_SCOPE:
                if (getBindings(ENGINE_SCOPE) != null) {
                    return getBindings(ENGINE_SCOPE).remove(name);
                }
                return null;
                
            case GLOBAL_SCOPE:
                if (getBindings(GLOBAL_SCOPE) != null) {
                    return getBindings(GLOBAL_SCOPE).remove(name);
                }
                return null;
                
            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    public void setAttribute(String name, Object value, int scope) {        
        
        switch (scope) {
            
            case ENGINE_SCOPE:
                engineScope.put(name, value);
                return;
                
            case GLOBAL_SCOPE:
                if (globalScope != null) {
                    globalScope.put(name, value);
                }
                return;
                
            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }
    
    /** {@inheritDoc} */
    public Writer getWriter() {
        return writer;
    }

    /** {@inheritDoc} */
    public Reader getReader() {
        return reader;
    }

    /** {@inheritDoc} */    
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /** {@inheritDoc} */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /** {@inheritDoc} */    
    public Writer getErrorWriter() {
        return errorWriter;
    }
    
    /** {@inheritDoc} */
    public void setErrorWriter(Writer writer) {
        this.errorWriter = writer;
    }

    public int getAttributesScope(String name) {
        if (engineScope.containsKey(name)) {
            return ENGINE_SCOPE;
        } else if (globalScope != null && globalScope.containsKey(name)) {
            return GLOBAL_SCOPE;
        } else {
            return -1;
        }
    }

    public Bindings getBindings(int scope) {
        if (scope == ENGINE_SCOPE) {
            return engineScope;
        } else if (scope == GLOBAL_SCOPE) {
            return globalScope;
        } else {
            throw new IllegalArgumentException("Illegal scope value.");
        }        
    }
    
    /** {@inheritDoc} */
    public List<Integer> getScopes() {
        return scopes;
    }
    
    private static List<Integer> scopes;
    static {
        scopes = new ArrayList<Integer>(2);
        scopes.add(ENGINE_SCOPE);
        scopes.add(GLOBAL_SCOPE);
        scopes = Collections.unmodifiableList(scopes);
    }
}