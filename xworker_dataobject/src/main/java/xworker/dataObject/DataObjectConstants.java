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
package xworker.dataObject;

/**
 * DataObject中的常量。
 * 
 * 很多变量放在Map中或者本身就是Map，因此定义变量名的常量。
 * 
 * @author Administrator
 *
 */
public class DataObjectConstants { 
	/** 数据对象相关 */
	public static final String THEDATA = "theData";
	
	/** PageInfo相关的变量 */
	public static final String PAGEINFO_SORT = "sort";
	public static final String PAGEINFO_DIR = "dir";
	public static final String PAGEINFO_TOTALCOUNT = "totalCount";
	public static final String PAGEINFO_MSG = "msg";
	public static final String PAGEINFO_SUCCESS = "success";
	public static final String PAGEINFO_DATAS = "datas";
	public static final String PAGEINFO_LIMIT = "limit";
	public static final String PAGEINFO_START = "start";
	public static final String PAGEINFO_PAGEINFO = "pageInfo";	
	public static final String PAGEINFO_SORT_EXTEND = "_extend";
	public static final String PAGEINFO_PAGESIZE = "pageSize";
	
	/** 条件相关变量 */
	public static final String CONDITION_DATA = "conditionData";
	public static final String CONDITION_CONFIG = "conditionConfig";
	
	/** 排序 */
	public static final String SORT_FIELD = "storeSortField";
	public static final String SORT_DIR = "storeSortDir";
	
	/** 编辑方式 */
	public static final String EDIT_TYPE_EDIT = "edit";
	public static final String EDIT_TYPE_CREATE = "create";
	public static final String EDIT_TYPE_VIEW = "view";
	public static final String EDIT_TYPE_GRID = "grid";
	public static final String EDIT_TYPE_QUERY = "query";
	
	public static final String LIST_ORGINAL_DATA = "__ListDataObject_orginal_data__";
}