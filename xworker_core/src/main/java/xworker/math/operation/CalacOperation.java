package xworker.math.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小学数学的巧填运算符号。
 * 
 * @author zyx
 *
 */
public class CalacOperation {
	List<Value> results = new ArrayList<Value>();
	Map<Integer, List<Value>> indexCache = new HashMap<Integer, List<Value>>();
	Map<Integer, Map<Integer, Map<Integer, Integer>>> valueCache = new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>();
	int totalValueCount = 0;
	int maxCount;
	
	public  CalacOperation(int number, int maxCount) {
		for(int i=0; i<maxCount; i++) {
			addValue(new Value(number, i));
		}
		this.maxCount = maxCount;
	}
	
	protected void addValue(Value value) {		
		List<Value> indexValues = indexCache.get(value.index);
		if(indexValues == null) {
			indexValues = new ArrayList<Value>();
			indexCache.put(value.index, indexValues);
			indexValues.add(value);
			totalValueCount ++;
		}else {
			for(Value v : indexValues) {
				if(v.index == value.index && v.count == value.count && v.value == value.value) {// && v.formula.equals(value.formula)) {
					return;
				}
			}
			
			indexValues.add(value);
			totalValueCount++;
		}
	}
	
	public boolean exists(int index, int count, int value) {
		Map<Integer, Map<Integer, Integer>> countCache = valueCache.get(index);
		if(countCache == null) {
			countCache = new HashMap<Integer, Map<Integer, Integer>>();
			valueCache.put(index, countCache);
			
			Map<Integer, Integer>  valueCache = new HashMap<Integer, Integer>();
			valueCache.put(value, value);
			countCache.put(count,  valueCache);
			
			return false;
		}else {
			Map<Integer, Integer>  valueCache = countCache.get(count);
			if(valueCache == null) {
				valueCache = new HashMap<Integer, Integer>();
				valueCache.put(value, value);
				countCache.put(count,  valueCache);
				return false;
			}else {
				if(valueCache.get(value) == null) {
					valueCache.put(value, value);
					return false;
				}
			}
		}
		
		return true;
	}
	
	public CalacOperation(boolean combine, int ... numbers) {
		if(combine == false) {
			for(int i=0; i<numbers.length; i++) {
				int number = numbers[i];
				addValue(new Value(number, i));
			}
			
			this.maxCount = numbers.length;
		}
	}
	
	public void calc() {		
		results.clear();
		while(true) {
			int count = totalValueCount;
			for(int i=0; i<maxCount; i++) {
				List<Value> indexValues = indexCache.get(i);
				//System.out.println("index " + i + " : " + indexValues.size());
				if(indexValues != null) {
					for(int n=0; n<indexValues.size(); n++) {
						Value v1 = indexValues.get(n);
						
						List<Value> nextIndexValues = indexCache.get(v1.index + v1.count);
						if(nextIndexValues != null) {
							//long start = System.currentTimeMillis();							
							int size = nextIndexValues.size();
							for(int k=v1.lastIndex; k<size; k++) {
							//for(int k=v1.lastIndex; k<nextIndexValues.size(); k++) {
							//for(int k=0; k<nextIndexValues.size(); k++) {
								Value v2 = nextIndexValues.get(k);
								v1.calc(this, v2, maxCount);
							}
							
							//v1.lastIndex = nextIndexValues.size() - 1; 
							v1.lastIndex = size - 1;
							
							//System.out.println("index " + i + " : " + indexValues.size() + ", index " + (v1.index + v1.count) + 
							//		" : "+ nextIndexValues.size() + " time:" + (System.currentTimeMillis() - start));
						}
					}
				}
			}
			
			//System.out.println(totalValueCount);
			if(count == totalValueCount) {
				break;
			}
		}		
			
		List<Value> values = indexCache.get(0);
        for(Value value : values) {			
        	if(value.count == maxCount) {
        		results.add(value);
        	}
        }
        
        Collections.sort(results);
	}
	
	public Value getResult(int resultValue) {
		for(Value value : results) {
			if(value.value == resultValue) {
				return value;
			}
		}
		
		return null;
	}
	
	public void printResults() {
		for(Value value : results) {
			System.out.println(value.formula + " = " + value.value);
		}
	}
	
	static class Value implements java.lang.Comparable<Value>{
		int value;
		int count;
		int index;
		String formula;
		int lastIndex;
		
		public Value(int value, int index) {
			this.value = value;
			count = 1;
			formula = String.valueOf(value);// + "_" + index;
			this.index = index;
		}
		
		public Value(int value, int count, String formula, int index) {
			this.value = value;
			this.count = count;
			this.formula = formula;
			this.index = index;
		}
		
		public void calc(CalacOperation co , Value v, int maxCount) {
			if(count + v.count > maxCount) {
				return;
			}
			if(this.index + count != v.index) {
				return;
			}
			if(count + v.count == maxCount && this.index != 0) {
				return;
			}
			
			createNewValue(co, this.value + v.value, " + ", v);
			createNewValue(co, this.value - v.value, " - ", v);
			createNewValue(co, this.value * v.value, " × ", v);
			if(v.value != 0) {
				double dv = 1.0d * this.value / v.value;
				int iv = (int) dv;
				if(iv == dv) {
					createNewValue(co, this.value / v.value, " ÷ ", v);
				}
			}
		}
		
		private void createNewValue(CalacOperation co, int newValue, String op, Value v) {			
			int newCount = this.count + v.count;
			if(!co.exists(this.index, newCount, newValue)) {
				StringBuilder sb = new StringBuilder();
				if(newCount < co.maxCount) {
					sb.append("(");
				}
				sb.append( this.formula );
				sb.append(op);
				sb.append(v.formula);
				if(newCount < co.maxCount) {
					sb.append(")");
				}
				String newFormula = sb.toString();
				Value newV = new Value(newValue, newCount, newFormula, this.index);
				co.addValue(newV);
			}
		}

		@Override
		public int compareTo(Value o) {
			if(value < o.value) {
				return -1;
			}else if(value == o.value) {
				return 0;
			}else {
				return 1;
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			CalacOperation co = new CalacOperation(8, 5);
			//CalacOperation co = new CalacOperation(false, 1, 2, 3,4);
			co.calc();
			co.printResults();
			System.out.println(co.results.size());
			System.out.println("Total time=" + (System.currentTimeMillis() - start));
			
		}catch(Exception e) {
			e.printStackTrace();			
		}
	}
}
