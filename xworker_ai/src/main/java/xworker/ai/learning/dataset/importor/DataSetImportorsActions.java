package xworker.ai.learning.dataset.importor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.UtilString;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import xworker.dataObject.DataObject;

public class DataSetImportorsActions {
	@SuppressWarnings("unchecked")
	public static void initCsv(ActionContext actionContext) throws IOException{
		Table fieldsTable = (Table) actionContext.get("fieldsTable");
		fieldsTable.setData("datas", null);		
		fieldsTable.setData("numbers", null);
		fieldsTable.setData("constants", null);
		fieldsTable.setData("fields", null);
		
		Thing form = (Thing) actionContext.get("form");
		Map<String, String> values = (Map<String, String>) form.doAction("getValues", actionContext);
		
		String delimiter = values.get("delimiter");
		char d = '\t';
		if("\\t".equals(delimiter)){
			d = '\t';
		}else if("\\u".equals(delimiter)){
			d = ' ';
		}else{
			d = delimiter.charAt(0);
		}
		
		File file = (File) actionContext.get("file");
		
		CsvReader csv = new CsvReader(file.getAbsolutePath());
		csv.setDelimiter(d);
		csv.setTrimWhitespace(true);
		csv.setSkipEmptyRecords(true);
		
		//标题列
		int columnsCount = 0;
		List<DataObject> fields = new ArrayList<DataObject>();
		if("true".equals(values.get("heads"))){
			csv.readRecord();
			
			columnsCount = csv.getColumnCount();
			for(int i=0; i<columnsCount;i++){
				DataObject obj = new DataObject("xworker.ai.learning.dataset.importor.DataSetImportor/@mainComposite/@fieldsTable/@tableDataStore/@dataObjects/@AbstractDataObject");
				obj.put("id", csv.get(i));
				fields.add(obj);
			}
		}
		
		//导入和所有的数据
		List<String[]> dataStrs = new ArrayList<String[]>();
		while(csv.readRecord()){
			if(columnsCount == 0){
				columnsCount = csv.getColumnCount();
				for(int i=0; i<columnsCount;i++){
					DataObject obj = new DataObject("xworker.ai.learning.dataset.importor.DataSetImportor/@mainComposite/@fieldsTable/@tableDataStore/@dataObjects/@AbstractDataObject");
					obj.put("id", "" + (i + 1));
					fields.add(obj);
				}
			}
			String[] datas = new String[columnsCount];
			for(int i=0; i<columnsCount;i++){
				datas[i] = csv.get(i);
			}
			
			dataStrs.add(datas);
		}
		
		//转换成double[]
		boolean[] numbers = new boolean[columnsCount];
		for(int i =0; i<numbers.length; i++){
			numbers[i] = true; //默认都设置成数字
		}
		
		List<double[]> datas = new ArrayList<double[]>();
		for(String[] data : dataStrs){
			double[] ddata = new double[data.length];
			
			for(int i=0; i<data.length; i++){
				if(numbers[i]){
					try{
						ddata[i] = Double.parseDouble(data[i]);
					}catch(Exception e){
						numbers[i] = false;
					}
				}
			}
			
			datas.add(ddata);
		}
		
		List<String>[] constants = new ArrayList[numbers.length];
		//设置非数字类型的列，转化成常量
		for(int i=0; i<numbers.length; i++){
			DataObject field = fields.get(i);
			if(numbers[i] == false){
				field.put("isNumber", "false");
				
				//不是数字类型的
				Map<String, String> context = new HashMap<String, String>();
				List<String> clist = new ArrayList<String>();
				
				for(int n=0; n<dataStrs.size(); n++){
					String[] sdata = dataStrs.get(n);
					String str = sdata[i];
					
					if(context.get(str) == null){
						clist.add(str);
						context.put(str, str);
					}
				}
				Collections.sort(clist);
				constants[i] = clist;
				
				//设置值
				for(int n=0; n<dataStrs.size(); n++){
					String[] sdata = dataStrs.get(n);
					String str = sdata[i];
					double[] ddata = datas.get(n);
					ddata[i] = clist.indexOf(str);					
				}
			}else{
				field.put("isNumber", "true");
			}
		}
		
		fieldsTable.setData("datas", datas);		
		fieldsTable.setData("numbers", numbers);
		fieldsTable.setData("constants", constants);
		fieldsTable.setData("fields", fields);
	
		//重新刷新
		Thing tableDataStore = (Thing) actionContext.get("tableDataStore");
		tableDataStore.doAction("reload", actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void doImport(ActionContext actionContext) throws IOException{
		Table fieldsTable = (Table) actionContext.get("fieldsTable");
		
		List<double[]> datas = (List<double[]>) fieldsTable.getData("datas");		
		boolean[] numbers = (boolean[]) fieldsTable.getData("numbers");
		List<String>[] constants  = (List<String>[]) fieldsTable.getData("constants");
		List<DataObject> fields = (List<DataObject>) fieldsTable.getData("fields");
		
		//检查CSV是否已经加载
		if(datas == null || fields == null || numbers == null || constants == null){
			showMessage("还未正确加载CSV数据", SWT.ICON_WARNING, actionContext);
			return;
		}
		
		//检查常量是否已经设置
		for(int i=0; i<fields.size(); i++){
			if(numbers[i] == false){
				DataObject field = fields.get(i);
				if(UtilString.isNull(field.getString("name"))){
					showMessage("字段名不能为空！", SWT.ICON_WARNING, actionContext);
					return;
				}
				
				if(UtilString.isNull(field.getString("constantsName")) && UtilString.isNull(field.getString("constantsPath"))){
					showMessage("存在非数字的常量字段，必须设置常量的名称英文或指定常量的数据对象路径！", SWT.ICON_WARNING, actionContext);
					return;
				}
			}
		}
	
		//检查保存到的路径
		Thing form = (Thing) actionContext.get("form");
		Map<String, String> values = (Map<String, String>) form.doAction("getValues", actionContext);
		
		String thingManagerName = values.get("thingManager");
		String savePath = values.get("savePath");
		String dataSetName = values.get("dataSetName");
		
		if(UtilString.isNull(dataSetName)){
			showMessage("数据集的名称不能为空！", SWT.ICON_WARNING, actionContext);
			return;
		}
		
		World world = World.getInstance();
		ThingManager thingManager = world.getThingManager(thingManagerName);
		if(thingManager == null || !(thingManager instanceof FileThingManager)){
			showMessage("要保存到的事物管理不存在，或它不是一个FileThingManager，当前只支持保存到文件系统中！", SWT.ICON_WARNING, actionContext);
			return;
		}
		
		if(UtilString.isNull(savePath)){
			showMessage("保存目录不能为空！", SWT.ICON_WARNING, actionContext);
			return;
		}
		
		//保存数据
		saveCsvDatas(dataSetName, thingManagerName, savePath, datas);
		
		//生成数据集数据对象
		generateDataObject(dataSetName, thingManagerName, savePath, numbers, fields);
		
		//生成常量
		for(int i=0; i<numbers.length; i++){
			if(numbers[i] == false){
				DataObject field = fields.get(i);
				
				if(!UtilString.isNull(field.getString("constantsName")) ){
					generateConstants(field.getString("constantsName"), thingManagerName, savePath, constants[i]);
				}
			}
		}
		
		showMessage("导入成功！", SWT.ICON_INFORMATION, actionContext);
	}

	public static void saveCsvDatas(String name, String thingManager, String path, List<double[]> datas) throws IOException{
		List<String[]> dlist = new ArrayList<String[]>();
		
		for(int i=0; i<datas.size(); i++){
			double[] dd = datas.get(i);
			String[] ds = new String[dd.length];
			
			for(int n = 0; n<ds.length; n++){
				ds[n] = String.valueOf(dd[n]);				
			}
			
			dlist.add(ds);
		}
		
		saveCsvDatastrs(name, thingManager, path, dlist);
	}
	
	public static void saveCsvDatastrs(String name, String thingManager, String path, List<String[]> datas) throws IOException{
		FileThingManager manager = (FileThingManager) World.getInstance().getThingManager(thingManager);
		File file = new File(manager.getFilePath(), path.replace('.', '/') + "/" + name + ".csv");
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		
		CsvWriter csv = new CsvWriter(file.getAbsolutePath());
		try{
			csv.setDelimiter(',');
			
			for(String[] data : datas){
				csv.writeRecord(data);
			}
		}finally{
			csv.close();
		}
	}

	public static String getCsvFilePath(String name, String thingManager, String path){
		FileThingManager manager = (FileThingManager) World.getInstance().getThingManager(thingManager);
		File file = new File(manager.getFilePath(), path.replace('.', '/') + "/" + name + ".csv");
		
		return file.getAbsolutePath();
	}
	
	public static void generateDataObject(String name, String thingManager, String path, boolean[] numbers,  List<DataObject> fields){
		Thing dataObject = new Thing("xworker.dataObject.java.CsvDataObject");
		dataObject.put("name", name);
		dataObject.put("filePath", getCsvFilePath(name, thingManager, path));
		dataObject.put("delimiter", ",");
		dataObject.put("haveHeaders", "false");
		
		for(int i=0; i<fields.size(); i++){
			//属性
			DataObject field = fields.get(i);
			
			Thing attr = new Thing("xworker.dataObject.java.ListDataObject/@attribute");
			attr.put("name", field.get("name"));
			attr.put("propertyPath", "c" + i);
			
			if(numbers[i] == false){
				//如果是常量，那么建立关联
				attr.put("inputtype", "select"); //下拉输入框
				String constantsName = field.getString("constantsName");
				String relationThingPath = null;
				if(constantsName != null && !"".equals(constantsName)){
					relationThingPath = path + "." + constantsName;
				}else{
					relationThingPath = field.getString("constantsPath");
				}
				attr.put("relationDataObject", relationThingPath);				
				attr.put("relationValueField", "id");
				attr.put("relationLabelField", "name");
			}
			//attr.put("type", "double");
			
			dataObject.addChild(attr);
		}
		
		dataObject.saveAs(thingManager, path + "." + name);
	}
	
	public static void generateConstants(String name, String thingManager, String path, List<String> datas) throws IOException{
		//首先保存csv数据
		List<String[]> dlist = new ArrayList<String[]>();
		for(int i=0; i<datas.size(); i++){
			String[] data = new String[2];
			data[0] = String.valueOf(i * 1d);
			data[1] = datas.get(i);
			dlist.add(data);
		}		
		saveCsvDatastrs(name, thingManager, path, dlist);
		
		//保存常量事物
		Thing dataObject = new Thing("xworker.dataObject.java.CsvDataObject");
		dataObject.put("name", name);
		dataObject.put("filePath", getCsvFilePath(name, thingManager, path));
		dataObject.put("delimiter", ",");
		dataObject.put("haveHeaders", "false");
		dataObject.put("valueName", "id");
		dataObject.put("displayName", "name");
		
		//标识
		Thing attr = new Thing("xworker.dataObject.java.ListDataObject/@attribute");
		attr.put("name", "id");
		attr.put("propertyPath", "c0");		
		//attr.put("type", "double");
		dataObject.addChild(attr);
		
		//名称
		attr = new Thing("xworker.dataObject.java.ListDataObject/@attribute");
		attr.put("name", "name");
		attr.put("propertyPath", "c1");				
		dataObject.addChild(attr);
		
		dataObject.saveAs(thingManager, path + "." + name);
	}
	
	public static void showMessage(String message, int style, ActionContext actionContext){
		Table fieldsTable = (Table) actionContext.get("fieldsTable");
		Shell shell = fieldsTable.getShell();
		
		MessageBox box = new MessageBox(shell, style | SWT.OK);
		box.setText("导入CSV数据");
		box.setMessage(message);
		box.open();
	}
}
