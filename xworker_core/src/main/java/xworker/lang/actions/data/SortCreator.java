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
package xworker.lang.actions.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class SortCreator {
	//private static Logger log = LoggerFactory.getLogger(SortCreator.class);
	private static final String TAG = SortCreator.class.getName();
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object run(ActionContext actionContext) throws OgnlException{    	
        final Thing self = (Thing) actionContext.get("self");
        
        Object listObj = self.doAction("getList", actionContext);
        String sortFields = (String) self.doAction("getSortFields", actionContext);
        String dirs = (String) self.doAction("getDirs", actionContext);
        
        List list = getList(listObj);
        if(sortFields == null || "".equals(sortFields)){
        	Collections.sort((List<Comparable>) list);
        	return list;
        }
        
        final String[] sfs = sortFields.split("[,]");
        final String ds[] = dirs != null ? dirs.split("[,]") : new String[]{};
       
        Collections.sort(list, new Comparator<Object>(){
			@Override
			public int compare(Object a, Object b) {
				try{				
					int r = 0;
					for(int i=0; i<sfs.length; i++){
						String sortField = sfs[i];
						String dir = "ASC";
						if(i < ds.length){
							dir = ds[i];
						}
						
						r = SortCreator.compare(self, a, b, sortField, dir);
						if(r != 0){
							break;
						}
					}
					
					return r;
				}catch(Exception e){
					Executor.warn(TAG, "compare data error", e);
				}
				return 0;
			}
        	
        });

        //for(data in listData){
        //    log.info("sorted data: " + data);
        //}
        
        return list;
    }

    @SuppressWarnings("rawtypes")
	public static List getList(Object list){
    	if(list instanceof List){
    		return (List) list;
    	}else{
    		List<Object> l = new ArrayList<Object>();
    		Iterable<Object> iter = UtilData.getIterable(list);
    		for(Object obj : iter){
    			l.add(obj);
    		}
    		
    		return l;
    	}
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Thing self, Object a, Object b, String sortField, String dir){
    	Comparable av = a == null ? null : getValue(self, sortField, a);
		Comparable bv = b == null ? null : getValue(self, sortField, b);
        if(av == null && bv == null){
            return 0;
        }else if(av == null && bv != null){
            return "DESC".equals(dir) ? 1 : -1;
        }else if(av != null && bv == null){
            return "DESC".equals(dir) ? -1 : 1;
        }else{
            return "DESC".equals(dir) ? -av.compareTo(bv) : av.compareTo(bv);
        }   
    }
    
    @SuppressWarnings("rawtypes")
	public static Comparable getValue(Thing self, String sortField, Object obj){
    	try{
    		return (Comparable) OgnlUtil.getValue(sortField, obj);
    	}catch(Exception e){
    		Executor.warn(TAG, "Sort get value error, action=" + self.getMetadata().getPath(), e);
    		return null;
    	}
    }
}