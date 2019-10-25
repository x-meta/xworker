Ext.ns('Ext.xworker.form');    
Ext.xworker.form.SearchOpen = Ext.extend(Ext.form.TwinTriggerField, {
//Ext.xworker.form.SearchOpen = Ext.extend(Ext.form.TriggerField, {
    initComponent: function(config) {
        Ext.xworker.form.SearchOpen.superclass.initComponent.call(this);
        Ext.apply(this, config);
        this.on('specialkey', function(f, e) {
            if (e.getKey() == e.ENTER) {
                this.onTrigger2Click(this.selectId, this.selectText);
            }
        }, this);
    },

    validationEvent: false,
    validateOnBlur: false,
    trigger2Class: 'x-form-search-trigger', //一个额外的CSS类，用来定义触发按钮样式
    triggerClass: 'x-form-search-trigger', //一个额外的CSS类，用来定义触发按钮样式
    hideTrigger1: true,
    //width: 500,
    hasSearch: false,
    paramName: 'query',
    windowNs: '',
    windowUrl:'',

    onTrigger2Click: function() {  
        if (this.disabled == true) {
            return;
        }            
        
        var xworker = Ext.ns('Ext.xworker');   
        xworker.remote.openWindow(this.windowNs, this.windowUrl, this, this);
    },
    
    onResize: function(w, h) {
		Ext.form.TriggerField.superclass.onResize.call(this, w, h);
		var tw = this.getTriggerWidth();
		if (Ext.isNumber(w)) {
			this.el.setWidth(300);
		}
		var elWidth = this.el.getWidth();
		this.wrap.setWidth(300);
		this.wrap.setWidth(elWidth ? (elWidth + tw) : w);
		//this.setWidth(300);
	},
	
	getTriggerWidth: function() {
		var tw = this.trigger.getWidth();
		if (!this.hideTrigger && tw === 0) {
			tw = this.defaultTriggerWidth;
		}
		return tw;
	}
	
});

//XWorker定义的有关远程装载控件的方法
Ext.onReady(function() {
    Ext.QuickTips.init();

	Ext.BLANK_IMAGE_URL='js/extjs/resources/images/default/s.gif';
	    
    var xworker = Ext.ns('Ext.xworker');    
    if(!xworker.util){
        xworker.util = new function(){
            /**
             * 删除选中的表格数据。
             */
            this.removeGridSelectedRows = function(grid, title, message){
                var deleteFn = function(btn){
                    if(btn == 'yes'){
                        var records  = Ext.xworker.util.getGridSelectedRows(grid);
                        var store = grid.store;
                        var selModel = grid.getSelectionModel();
                        if(selModel.hasPrevious){
                            if(selModel.hasPrevious()){
                                selModel.selectPrevious();
                            }else if(selModel.hasNext()){
                                selModel.selectNext();
                            }
                        }
                        store.remove(records);
                    }
                };
                if(title && message && message != ""){
                    Ext.MessageBox.confirm(title, message, deleteFn);
                }else{
                    var records  = Ext.xworker.util.getGridSelectedRows(grid);
                    var store = grid.store;
                    var selModel = grid.getSelectionModel();
                    if(selModel.hasPrevious){
                        if(selModel.hasPrevious()){
                            selModel.selectPrevious();
                        }else if(selModel.hasNext()){
                            selModel.selectNext();
                        }
                    }
                    store.remove(records);
                }                
            };
            
            /**
             * 返回表格中选中的行。
             */
            this.getGridSelectedRows = function(grid){
                var store = grid.store;
                var selModel = grid.getSelectionModel();
                if(selModel.getSelections){
                    var records  = selModel.getSelections();
                    return records;
                }else if(selModel.getSelectedCell){
                    var cells = selModel.getSelectedCell();
                    var rows = [];
                    var rowIndexs = [];
                    if(cells && cells.length > 0){
                        for(n=0; n<cells.length; n++){
                            var have = false;
                            for(i=0; i<rowIndexs.length; i++){
                                if(rowIndexs[i] == cells[n]){
                                    have = true;
                                    break;
                                }
                            }
                            if(!have){
                                rowIndexs[rowIndexs.length] = cells[n];
                                rows[rows.length] = store.getAt(cells[n]);
                            }
                            
                            //跳过列
                            n++;
                        }
                    }
                    
                    return rows;
                }
                
                return [];
            };
            
            /**
             * 远程循环。
             * 
             * options{
             * //需要传入的参数
             *     url: string
             *     records: object/array,
             *     success: function(item, index, totalCount, records, response),
             *     failure: function(item, index, totalCount, records, response),
             *     
             * //临时保存的参数
             *     item: object,
             *     index: number,
             *     totalCount: number
             *     
             * }
             */
            this.remoteEach = function(options){
            	var records = options.records;
            	
            	//初始化index和totalCount
            	var index = options.index;
            	if(!Ext.isNumber(index)){
            		index = 0;
            		options.index = index;
            	}
            	var totalCount = options.totalCount;
            	if(!Ext.isNumber(totalCount)){
            		if(Ext.isArray(records)){
            		    totalCount = records.length;
            		}else{
            			totalCount = 1;
            		}
            		options.totalCount = totalCount;
            	}
            	
            	var item = records;
            	if(Ext.isArray(records)){
            		item = records[index];
            	}
            	options.item = item;
            	
            	<!-- 请求数据 -->
            	Ext.Ajax.request({
            	    url: options.url,
            	    params: item,
            	    options: options,
            	    success: function(response, options){
            	        var obj = Ext.decode(response.responseText);
            	        response.result = obj;
            	        var op = options.options;
            	        if(Ext.isFunction(op.success)){
            	        	op.success(op, response);
            	        }
            	        
            	        if(op.index < op.totalCount - 1){
            	        	op.index = op.index + 1;
            	        	var xworker = Ext.ns('Ext.xworker');    
            	        	xworker.util.remoteEach(op);
            	        }
            	    },
            	    failure: function(response, options){
              	        var op = options.options;
            	        if(Ext.isFunction(op.failure)){
            	        	op.failure(op, response);
            	        }
            	        
            	        if(op.index < op.totalCount - 1){
            	        	op.index = op.index + 1;
            	        	var xworker = Ext.ns('Ext.xworker');    
            	        	xworker.util.remoteEach(op);
            	        }
            	    }     
            	});
            };
        };
    };
    
    if(!xworker.remote){
        xworker.remote = new function(){
        	this.appendScirpt = function(url, options){
        		 var script = document.createElement('script');
        		 script.type = 'text/javascript';
        		 script.src = url;
        		 
        		 script.options = options;
        		 script.onload = options.successFn;
        		 script.onerror = options.failureFn;
        		 script.params = options.values;
        		 //var head= document.getElementsByTagName('head')[0];
        		 //head.appendChild(script);
        		 document.body.appendChild(script);
        	};
        	
            /**
             * 仅打开一个窗口。
             */
            this.openWindow = function(windowName, url){
            };
            
            /**
             * 打开一个查询窗体.
             * 查询窗体都有一个名字空间，是在Ext.xworker.queryWindow的名字空间下。         
             * 每个查询窗体的名字空间都应有一个xwr_setValues(values)的方法，以便设置参数到查询的表单中。
             * 查询用的Store变量将会以变量名parentStore放到窗体下。
             *
             * @param queryWindowNs 查询窗体的名字空间
             * @param url 查询窗体的url
             * @param fn 执行插入数据的方法
             * @param values 初始化参数
             */
            this.openQueryWindow = function(queryWindowNs, url, fn, values){
                xworker.remote.openWindow(queryWindowNs, url, fn, values);
            };
            
            /**
             * 打开一个Ext.Window。
             * @param windowNs Window的名字空间
             * @param url Window的远程URL
             * @param fn 打开窗口者的回调函数，回调函数放在被打开的窗口的parentFn属性中。
             * @param values 被打开窗口可以使用的初始化数据
             */
            this.openWindow = function(windowNs, url, fn, values){
                var xwqw = Ext.ns(windowNs);
                if(!xwqw.item || Ext.isEmpty(xwqw.item) || xwqw.item.closeAction == "close"){
                	Ext.Msg.wait("正在加载，请稍后...");
                	if(Ext.isChrome){
	                	xworker.remote.appendScirpt(url, {
		                	successFn: function(){
	                		    var options = this.options;
		                		var xwqw = Ext.ns(options.windowNs);
		                		if(xwqw){
		                			Ext.Msg.hide(); //关闭提示信息
		                			
	                                xwqw.item.doLayout();
	                                xwqw.item.parentFn = options.fn;
	                                xwqw.item.show();
	                                if(options.values && xwqw.init){
	                                    xwqw.init(options.values);
	                                }
	                            }else{
	                                Ext.Msg.alert('提示', 'URL没有返回窗体！url=' + options.url);
	                            }
	                		},
	                		failureFn: function(){
	                			var options = this.options;
	                			Ext.Msg.alert("打开", "加载远程JavaScript失败，请稍后重试！");
	                		},
	                		url: url,
	                		values: values,
	                		windowNs: windowNs,
	                		fn: fn
	                	});
                	}else{
	                    Ext.Ajax.request({   
	                        method: 'GET',   
	                        disableCaching: true,   
	                        url: url,  
	                        fn: fn,
	                        params: values,
	                        values: values,
	                        success: function(response, options) {    
	                            //alert(response.responseText);
	                            eval(response.responseText);   	                            
	                            
	                            var xwqw = Ext.ns(windowNs);
	                            if(xwqw){                    
	                            	Ext.Msg.hide(); //关闭提示信息
	                            	
	                                xwqw.item.doLayout();
	                                xwqw.item.parentFn = options.fn;
	                                xwqw.item.show();
	                                if(options.values && xwqw.init){
	                                    xwqw.init(options.values);
	                                }
	                            }else{
	                                Ext.Msg.alert('提示', 'URL没有返回窗体！url=' + url);
	                            }
	                        },
	                        failure: function(response) {
	                            Ext.Msg.alert('提示', '打开窗体失败！');
	                        }
	                    }); 
                	}
                }else{                	
                    if(values && xwqw.init){
                        xwqw.init(values);
                    }
                    xwqw.item.parentFn = fn;
                    xwqw.item.show();
                }
            };
            
            /**
             * 在一个组件下添加一个远程组件。
             */
            this.addComponet = function(parentId, replace, componentNs, url, values){
                var xwqw = Ext.ns(componentNs);
                if(!xwqw.item || Ext.isEmpty(xwqw.item)){
                	Ext.Msg.wait("正在加载，请稍后...");
                	if(Ext.isChrome){
	                	xworker.remote.appendScirpt(url, {
		                	successFn: function(){
	                		    var options = this.options;
		                		var xwqw = Ext.ns(options.componentNs);
		                		if(xwqw){
		                			Ext.Msg.hide(); //关闭提示信息
		                			
		                			var parent = Ext.getCmp(options.parentId);
		                			if(options.replace){
		                				parent.removeAll(true);
		                			}
		                			
		                			parent.add(xwqw.item);
		                			parent.layout();
	                                if(options.values && xwqw.init){
	                                    xwqw.init(options.values);
	                                }
	                            }else{
	                                Ext.Msg.alert('提示', 'URL没有返回窗体！url=' + options.url);
	                            }
	                		},
	                		failureFn: function(){
	                			var options = this.options;
	                			Ext.Msg.alert("打开", "加载远程JavaScript失败，请稍后重试！");
	                		},
	                		url: url,
	                		values: values,
	                		componentNs: componentNs,
	                		parentId: parentId,
	                		replace:replace
	                	});
                	}else{
	                    Ext.Ajax.request({   
	                        method: 'GET',   
	                        disableCaching: true,   
	                        url: url,  
	                        params: values,
	                        values: values,
	                        parentId: parentId,
	                		replace:replace,
	                        success: function(response, options) {    
	                            //alert(response.responseText);
	                            eval(response.responseText);   
	                            
	                            var xwqw = Ext.ns(componentNs);
	                            if(xwqw){                    
	                            	Ext.Msg.hide(); //关闭提示信息
	                            	var parent = Ext.getCmp(options.parentId);
		                			if(options.replace){
		                				parent.removeAll(true);
		                			}
		                			
		                			parent.add(xwqw.item);
		                			parent.layout();
	                                if(options.values && xwqw.init){
	                                    xwqw.init(options.values);
	                                }
	                            }else{
	                                Ext.Msg.alert('提示', 'URL没有返回窗体！url=' + url);
	                            }
	                        },
	                        failure: function(response) {
	                            Ext.Msg.alert('提示', '打开窗体失败！');
	                        }
	                    }); 
                	}
                }else{                	
                    if(values && xwqw.init){
                        xwqw.init(values);
                    }
                }
            };
            
            /**
             * 打开一个弹出编辑器，用于输入控件的打开弹出编辑窗体。
             *
             * @param editorNs 弹出编辑器的名字空间            
             * @param url 弹出编辑器的URL地址
             * @param parentField 父输入控件，会以parentField属性名放到被打开的窗体中
             * @param values 初始化参数
             */
            this.openPopEditor = function(editorNs, url, parentField, values){
                var xwqw = Ext.ns(editorNs);
                if(!xwqw.item || Ext.isEmpty(xwqw.item)){
                    Ext.Ajax.request({   
                        method: 'GET',   
                        disableCaching: true,   
                        url: url,  
                        parentField: parentField,
                        values: values,
                        success: function(response, options) {    
                            //alert(response.responseText);
                            eval(response.responseText);   
                            
                            var xwqw = Ext.ns(editorNs);
                            if(xwqw){                            
                                xwqw.item.doLayout();
                                xwqw.item.parentField = options.parentField;
                                xwqw.item.show();
                                if(xwqw.init){
                                    xwqw.init(options.values);
                                }
                            }else{
                                Ext.Msg.alert('提示', 'URL没有返回窗体！url=' + url);
                            }
                        },
                        failure: function(response) {
                      
                        	Ext.Msg.alert('提示', '打开窗体失败！');
                        }
                    }); 
                }else{
                    if(xwqw.init){
                        xwqw.init(values);
                    }
                    xwqw.item.parentField = parentField;
                    xwqw.item.show();
                }
            };

            /**
             * 打开一个为输入框选择单行数据的查询窗体。
             */
            this.openSelectInputDataWindow = function(windowName, url, parentItem, params){
            };
            
            /**
             * 打开一个为Store选择数据的查询窗体。
             */
            this.openInsertStoreDatasWindow = function(windowName, url, store, params){
            };
            
            this.openTabFirst = function(tabItemNs, tab, params){
            	var xwqw = Ext.ns(tabItemNs);
        		tab.setActiveTab(xwqw.tabItem);	
                xwqw.init(params); 
            };
            
            /*
             * 打开tab的条目。
             */
            this.openTabItem = function(tabItemNs, url, tab, params){
                var xwqw = Ext.ns(tabItemNs);
                if(!xwqw.item || !tab.get(xwqw.item.id)){
                	Ext.Msg.wait("正在加载，请稍后...");
                	if(Ext.isChrome){                		
	                	xworker.remote.appendScirpt(url, {
		                	successFn: function(){
	                		    var options = this.options;
		                		var xwqw = Ext.ns(options.tabItemNs);
		                        if(xwqw){                            
		                        	Ext.Msg.hide(); //关闭提示信息
		                        	
		                            xwqw.item.doLayout();
		                            var tabItem = options.tab.add(xwqw.item);
		                            tabItem.border = false;
		                            options.tab.doLayout();
		                            options.tab.setActiveTab(tabItem);	
		                            xwqw.tabItem = tabItem;
		                            if(options.values && xwqw.init){
		                                xwqw.init(options.values);
		                            }
		                        }else{
		                            Ext.Msg.alert('提示', 'URL没有返回Tab条目！url=' + options.url);
		                        }
	                		},
	                		failureFn: function(){
	                			var options = this.options;
	                			Ext.Msg.alert("打开", "加载远程JavaScript失败，请稍后重试！");
	                		},
	                		url: url,
	                		tab: tab,
	                		values: params,
	                		tabItemNs: tabItemNs
	                	});
                	}else{
	                    Ext.Ajax.request({   
	                        method: 'GET',   
	                        disableCaching: true,   
	                        url: url,  
	                        tab: tab,
	                        values: params,
	                        success: function(response, options) {    
	                            //alert(response.responseText);
	                            eval(response.responseText);   
	                            
	                            var xwqw = Ext.ns(tabItemNs);
	                            if(xwqw){                       
	                            	Ext.Msg.hide(); //关闭提示信息
	                            	
	                                xwqw.item.doLayout();
	                                var tabItem = options.tab.add(xwqw.item);
	                                tabItem.border = false;
	                                options.tab.doLayout();
	                                options.tab.setActiveTab(tabItem);	
	                                xwqw.tabItem = tabItem;
	                                if(options.values && xwqw.init){
	                                    xwqw.init(options.values);
	                                }
	                            }else{
	                                Ext.Msg.alert('提示', 'URL没有返回Tab条目！url=' + url);
	                            }
	                        },
	                        failure: function(response) {
	                            Ext.Msg.alert('提示', '打开Tab选项框失败！');
	                        }
	                    }); 
                    }
                }else{
                    tab.setActiveTab(xwqw.tabItem);	
                    xwqw.init(params);                    
                }
            };
        };
    };    
    
    if(!xworker.design){
    	xworker.design = new function(){
    		this.setUrlParam = function(oldurl,paramname,pvalue){     
    	        var reg = new RegExp("(\\?|&)" + paramname +"=([^&]*)(&|$)","gi");     
    	        var pst=oldurl.match(reg);     

    	        if((pst==undefined) || (pst==null)){    
    	            return   oldurl+((oldurl.indexOf("?")==-1)?"?":"&")+paramname+"="+pvalue;     
    	        }   

    	        var   t=pst[0];     
    	        var   retxt=t.substring(0,t.indexOf("=")+1)+pvalue;     

    	        if(t.charAt(t.length-1)=='&'){
    	        	retxt+="&";     
    	        }
    	        
    	        return  oldurl.replace(reg,retxt);     
    	    },     
    		
    		this.refresh = function(id, thing){
    		    Ext.Ajax.request({
    		        url: 'do?sc=' + thing,
    		        id: id,
    		        success: function(response, options){
    		            var obj = Ext.decode(response.responseText);
    		            var xworker = Ext.ns('Ext.xworker');  
    		            if(obj.refresh){	            	
    		            	var url = location.href;
    		            	url = xworker.design.setUrlParam(url, "xworker_desisgn_refresh_id", id);
    		            	location.href = url;
    		            	return;
    		            }
    		            
    		            var element = Ext.getDom(options.id);
    		            if(element){
    		            	element.innerHTML = obj.html;
    		            }    	
    		            
    		            xworker.design.selectNode(id);
    		        }    		        
    		    });
    		},
    		
    		this.remove = function(id, thing){
    		    Ext.Ajax.request({
    		        url: 'do?action=remove&sc=' + thing,
    		        id:id,
    		        success: function(response, options){
    		            var obj = Ext.decode(response.responseText);
    		            
    		            var element = Ext.getDom(options.id);
    		            if(element){
    		            	element.parentNode.removeChild(element);
    		            	
    		            	var xworker = Ext.ns('Ext.xworker');  
    		            	xworker.design.initDesignTree();
    		            }    	    		            
    		        }    		        
    		    });
    		},

    		this.initDesignTree = function(){
    			//获取树控件
    			var tree = Ext.getCmp('xworker_design_window_treePanel');
    			var rootNode = tree.getRootNode();

    			//移除已有的节点
    			rootNode.removeAll(true);
    			rootNode.thing = '';
    			
    			//添加可以设计的控件列表
    			var ds = Ext.query("div[xworker_design=true]");
    			for(i=0; i<ds.length; i++){
    			    var parentNode = this.findParentNode(rootNode, ds[i]);
    			    this.createNode(parentNode, ds[i]);
    			}
    			
    			rootNode.expandChildNodes(true);
    		},
    		
    		this.findParentNode = function(parentNode, dom){
    			var parentThing = parentNode.thing;
    			var currentThing = dom.attributes.thing.value;
   			
    			if(parentThing == '' || currentThing.indexOf(parentThing) == 0){
    			    var child = parentNode.firstChild;
    			    while(child != null){
    			    	var node = this.findParentNode(child, dom);
    			    	if(node != null){
    			    		return node;
    			    	}
    			    	
    			    	child = child.nextSibling;
    			    }
    			    
    			    return parentNode;
    			}else{
    				return null;
    			}
    		},
    		
    		this.createNode = function(parentNode, dom){
    			var node = new Ext.tree.TreeNode({
			        id:dom.id,
			        text:dom.attributes.xworker_label.value
			    });
			    node.thing = dom.attributes.thing.value;
			    parentNode.appendChild(node);
    		},
    		
    		this.selectDesignNode = function(id){
    			var win = Ext.getCmp('xworker_design_window');
    			var oldSelection = win.oldSelection;
    			if(oldSelection != null){   
    			    oldSelection.style.border = oldSelection.oldStyleBorder;
    			}

    			var selection = Ext.getDom(id);
    			selection.oldStyleBorder = selection.style.border;
    			selection.style.border='1px solid red';
    			win.oldSelection = selection;
    		},
    		
    		/**
    		 * 选中一个指定的节点。
    		 */
    		this.selectNode = function(id){
    			var tree = Ext.getCmp('xworker_design_window_treePanel');
    			var rootNode = tree.getRootNode();
    			
    			this.selectNodeById(tree, rootNode, id);
    		},
    		
    		this.selectNodeById = function(tree, parentNode, id){
    			var child = parentNode.firstChild;
 			    while(child != null){
 			    	if(id == child.id){
 			    		child.select();
 			    		this.selectDesignNode(id);
 			    	    var cmps = 'xworker_design_window_editButton,xworker_design_window_updateButton,xworker_design_window_deleteButton'.split(',');
 			    	    for(i=0; i<cmps.length; i++){
 			    	        var cmp = Ext.getCmp(cmps[i]);
 			    	        if(cmp){
 			    	            cmp.setDisabled(false);
 			    	        }
 			    	    }
 			    	    return true;
 			    	}
 			    	
 			    	if(this.selectNodeById(tree, child, id)){
 			    		return true;
 			    	}else{
 			    		child = child.nextSibling;
 			    	} 			    	
 			    }
 			    
 			    return false;
    		}
    	};
    };
    
    //注册设计当前界面的事件
    Ext.getBody().addKeyMap({
    	key: 'd',
    	ctrl: true,
    	alt: true,
    	handler: function(){
    		//打开远程编辑器
    	    var xr = Ext.ns('Ext.xworker.remote');
      	    xr.openWindow('XWorker_Design_DesignWindow', 
    	        'do?sc=xworker.html.design.extjs.XWorker_Design_DesignWindow&' + '',
    	        null,
    	        {});
    	}
    });
    
    //如果网页包含了正在设计的节点，那么打开设计界面
    if(location.href.indexOf("xworker_desisgn_refresh_id") != -1){
    	var xr = Ext.ns('Ext.xworker.remote');
 	   
        var query = location.search.substring(1).split('&');
        var id = "";
        for(var i=0;i<query.length;i++){
            var kv = query[i].split('=');
            if(kv[0] == "xworker_desisgn_refresh_id"){
            	id = kv[1];
            	break;
            }
        }
	    xr.openWindow('XWorker_Design_DesignWindow', 
	        'do?sc=xworker.html.design.extjs.XWorker_Design_DesignWindow&' + '',
	        null,
	        {selectedId: id});
    }
});