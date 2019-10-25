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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class AggregateCreator {
	private static Logger log = LoggerFactory.getLogger(AggregateCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
        //数据列表
    	Iterable<Object> listData = (Iterable<Object> ) OgnlUtil.getValue(self.getString("listName"), actionContext);
        if(listData == null){
            log.info("Aggregate: listData is null, listName=" + self.getString("listName"));
            return null;
        }
        
        List<Thing> gfields = (List<Thing>) self.get("AggregateField@");
        
        //执行聚合操作
        Map<String, Map<String, Object>> tempResult = new HashMap<String, Map<String, Object>>();
        Map<String, Object> distinctValues = new HashMap<String, Object>();
        String distinctData = "1";
        for(Object data : listData){
            for(Thing gf : gfields){
            	String gfname = gf.getString("name");
            	Map<String, Object> rs = tempResult.get(gfname);
                if(rs == null){
                    rs = new HashMap<String, Object>();
                    tempResult.put(gfname, rs);
                    rs.put("sum", 0);
                    rs.put("count", 0);
                    rs.put("max", 0);
                    rs.put("min", 0);
                }
                
                //为方便和统一，先一次性算出所有的值
                Number sumValue = (Number) rs.get("sum");                
                Double sum = sumValue.doubleValue();
                Integer count = (Integer) rs.get("count");
                Comparable max = (Comparable) rs.get("max");
                Comparable min = (Comparable) rs.get("min");
        
                String aggregateExpression = gf.getString("aggregateExpression");
                if("*".equals(aggregateExpression)){
                    count++;
                }else{
                    Object value = null;
                    if(aggregateExpression == null || "".equals(aggregateExpression)){
                        log.warn("Aggregate: aggregateExpression is null");
                        value = 0;
                    }else{
                        value = OgnlUtil.getValue(aggregateExpression, data);
                    }
                    
                    if(gf.getBoolean("aggregateDistinct") == true){
                        if(distinctValues.get(value) != null){
                            continue;
                        }else{
                            distinctValues.put(String.valueOf(value), distinctData);
                        }
                    }
                    
                    if(value != null){
                        count++;
                        
                        if(value instanceof Comparable){
                        	Comparable cvalue = (Comparable) value;
                        
	                        if(max == null){
	                        	max = cvalue;
	                        }else if(max.compareTo(cvalue) < 0){
	                        	max = cvalue;
	                        }
	                        if(min == null){
	                        	min = cvalue;
	                        }else if(min.compareTo(cvalue) > 0){
	                        	min = cvalue;
	                        }
	                        
	                        Double dv = 0.0;                               
	                        try{
	                            dv = UtilData.getDouble(value, 0);
	                            sum = sum + dv;                                     
	                        }catch(Exception e){
	                        }
                        }
                    }
                }
                
                rs.put("sum", sum);
                rs.put("count", count);
                rs.put("max", max);
                rs.put("min", min);
            }
        }
        
        //汇总结果
        Map<String, Object> result = new HashMap<String, Object>();
        //log.info("tempResult=" + tempResult);
        for(Thing gf : gfields){
        	String gfname = gf.getString("name");
        	
            Map<String, Object> r = tempResult.get(gfname);
            String aggregateFunction = gf.getString("aggregateFunction");
            String type = gf.getString("type");
            String aggregateType = gf.getString("aggregateType");
            Double sum = (Double) r.get("sum");
            Integer count = (Integer) r.get("count");
            Comparable max = (Comparable) r.get("max");
            Comparable min = (Comparable) r.get("min");
            
            if("count".equals(aggregateFunction)){
                if("int".equals(aggregateType)){
                    result.put(gfname, (Integer) r.get("count"));
                }else{
                    result.put(gfname, r.get("count"));
                }
            }else if("avg".equals(aggregateFunction)){
                Double avg = 0.0;

                if(count > 0){
                    avg = sum / count;
                }
                if("int".equals(type)){
                    result.put(gfname,  avg.intValue());
                }else{
                    result.put(gfname, avg);
                }
            }else if("sum".equals(aggregateFunction)){
                if("int".equals(type)){
                    result.put(gfname, sum.intValue());
                }else{
                    result.put(gfname, sum);
                }
            }else if("max".equals(aggregateFunction)){
                if("int".equals(type)){
                    result.put(gfname, UtilData.getInt(max, 0));
                }else{
                    result.put(gfname, max);
                }
            }else if("min".equals(aggregateFunction)){
                if("int".equals(type)){
                    result.put(gfname, UtilData.getInt(min, 0));
                }else{
                    result.put(gfname, min);
                }
                break;  
            }
        }
        
        String varName = self.getString("varName");
        if(varName != null && !"".equals(varName)){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            bindings.put(varName, result);
        }
        
        //log.info("result=" + result);
        return result;
    }

}