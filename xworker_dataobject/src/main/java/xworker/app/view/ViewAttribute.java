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

import org.xmeta.Thing;

/**
 * 数据对象属性转化为界面需求时的代理对象。
 * 
 * @author Administrator
 *
 */
public class ViewAttribute {
	/** 属性的原始定义，参看：xworker.dataObject.Attribute */
	public Thing attribute;
	/** 属性的编辑定义，参看：xworker.dataObject.AttributeEditConfig */
	public Thing viewConfig;
}