package xworker.ai.neuroph.data;

import java.util.List;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class DataSetNormalizer {
	public static DataSet denormalize(ActionContext actionContext){
		return null;
	}
	
	public static void updateParams(ActionContext actionContext){
		
	}
	
	public static DataSet normalize(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		List<Thing> childs = self.getChilds();
		//检查参数是否都已经设置好
		checkChildsParams(childs);
		
		//获取新的数据源输入和输出个数
		int inputsCount = getInputsCount(childs);
		int outputsCount = getOutputsCount(childs);
	
		DataSet newSet = new DataSet(inputsCount, outputsCount);		
		//执行Normalize的操作		
		DataSet dataSet = (DataSet) actionContext.get("dataSet");
		for(DataSetRow row : dataSet.getRows()){
			double[] values = new double[inputsCount + outputsCount];
			
			int index = 0;
			for(int i=0; i<childs.size(); i++){
				Thing child = childs.get(i);
				String name = child.getThingName();
				if("DecimalScaleNormalizer".equals(name)){
					double scaleFactor = child.getDouble("scaleFactor");
					values[index] = getValue(row, i) / scaleFactor;
					index++;
				}else if("MaxMinNormalizer".equals(name)){
					double max = child.getDouble("max");
					double min = child.getDouble("min");
					values[index] = (getValue(row, i) - min ) / (max - min);
					index++;
				}else if("MaxNormalizer".equals(name)){
					double max = child.getDouble("max");
					values[index] = getValue(row, i) / Math.abs(max);
					index++;
				}else if("RangeNormalizer".equals(name)){
					throw new ActionException("RangeNormalizer is not implemented");
				}else if("ConstantsNormalizer".equals(name)){
					int digit = child.getInt("digit");
					int value = (int) getValue(row, i);
					//采用左低右高的方法
					for(int n=0; n<digit; n++){
						values[index] = 1 & value;
						value = value >> 1;
						index++;
					}
				}else if("NoNormalizer".equals(name)){
					values[index] = getValue(row, i);
					index++;
				}
			}
			
			DataSetRow newRow = new DataSetRow();
			double[] is = new double[inputsCount];
			double[] os = new double[outputsCount];
			System.arraycopy(values, 0, is, 0, is.length);
			System.arraycopy(values, is.length, os, 0, os.length);
			newRow.setInput(is);
			newRow.setDesiredOutput(os);
			
			newSet.addRow(newRow);
		}
		
		return newSet;
	}
	
	public static double getValue(DataSetRow row, int index){
		if(index < row.getInput().length){
			return row.getInput()[index];
		}else{
			return row.getDesiredOutput()[index -row.getInput().length];
		}
	}
	
	public static int getInputsCount(List<Thing> childs){
		return getCounts(childs, false);
	}
	
	public static int getOutputsCount(List<Thing> childs){
		return getCounts(childs, true);
	}
	
	public static int getCounts(List<Thing> childs, boolean isOutput){
		int count = 0;
		for(Thing child : childs){
			if(isOutput && child.getBoolean("output") == false){
				continue;
			}else if(!isOutput && child.getBoolean("output") == true){
				continue;
			}
			
			String name = child.getThingName();
			if("ConstantsNormalizer".equals(name)){
				count = count + child.getInt("digit");
			}else{
				count = count + 1;
			}
		}
		
		return count;
	}
	
	public static void checkChildsParams(List<Thing> childs){
		for(Thing child : childs){
			String name = child.getThingName();
			if("DecimalScaleNormalizer".equals(name)){
				if(child.getStringBlankAsNull("scaleFactor") == null){
					throw new ActionException("scaleFactor param not seted, path=" + child.getMetadata().getPath());
				}
			}else if("MaxMinNormalizer".equals(name)){
				if(child.getStringBlankAsNull("max") == null){
					throw new ActionException("max param not seted, path=" + child.getMetadata().getPath());
				}
				if(child.getStringBlankAsNull("min") == null){
					throw new ActionException("min param not seted, path=" + child.getMetadata().getPath());
				}
			}else if("MaxNormalizer".equals(name)){
				if(child.getStringBlankAsNull("max") == null){
					throw new ActionException("max param not seted, path=" + child.getMetadata().getPath());
				}
			}else if("RangeNormalizer".equals(name)){
				if(child.getStringBlankAsNull("highLimit") == null){
					throw new ActionException("highLimit param not seted, path=" + child.getMetadata().getPath());
				}
			}else if("ConstantsNormalizer".equals(name)){
				if(child.getStringBlankAsNull("digit") == null){
					throw new ActionException("digit param not seted, path=" + child.getMetadata().getPath());
				}
			}
		}
	}
}
