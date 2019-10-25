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
package xworker.app.view;

/**
 * 表格中的列。
 * 
 * @author Administrator
 *
 */
public class GridColumn {
	/** 跨行 */
	public int colspan;
	/** 跨列 */
	public int rowspan;
	/** 标题 */
	public String title;
	/** 编辑属性 */
	public ViewAttribute viewAttribute;
}