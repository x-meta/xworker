/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.swt.help.base;

import org.xmeta.ActionContext;

public class shellActions {
    public static void refreshButtonSelection(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }
        
        threadTree.removeAll();
        def rootData = ["name":root.getName()];
        def treeItem = threadTree.updateRow(null, rootData);
        visit(root, threadTree, treeItem);
        treeItem.setExpanded(true);
        
        def visit(group, tree, item){
            int numThreads = group.activeCount();
            Thread[] threads = new Thread[numThreads*2];
            numThreads = group.enumerate(threads, false);
            
            // Enumerate each thread in `group'
            for (i in 0..numThreads-1) {
                def thread = threads[i]
                def threadData = ["name":thread.getName(), "status":"" + thread.getState(), "thread":thread];
                tree.updateRow(item, threadData, true);
            }
            
            // Get thread subgroups of `group'
            int numGroups = group.activeGroupCount();
            ThreadGroup[] groups = new ThreadGroup[numGroups*2];
            numGroups = group.enumerate(groups, false);
            
            // Recursively visit each subgroup
            if(numGroups > 0){
                for (i in 0..numGroups-1) {
                    def subGroup = groups[i]
                    def gdata = ["name":subGroup.getName()];
                    def subItem = tree.updateRow(item, gdata, true);
                    visit(groups[i], tree, subItem);
                    subItem.setExpanded(true);
                }
            }
        }*/
    }

    public static void killButtonSelection(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        
        if(threadTree.getSelection().length == 0) return;
        
        def th = threadTree.getSelection()[0].getData().thread;
        if(th instanceof Thread){
            MessageBox box = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
            box.setText("操作信息");
            box.setMessage("确实要终止选中的线程么?");
            if(SWT.OK == box.open()){        
                th.stop();
            }
            
            refreshButtonSelection.handleEvent(null);
        }*/
    }

    public static void memoryRefreshButtonSelection(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        
        //import org.xmeta.cache.UtilCache;
           
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage noheapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        
        performanceText.setText("");
        performanceText.append("内存堆使用:\n");
        performanceText.append("    初始化: " + byteCountToDisplaySize(heapUsage.getInit()));
        performanceText.append("\n    已使用: " + byteCountToDisplaySize(heapUsage.getUsed()));
        performanceText.append("\n    已提交: " + byteCountToDisplaySize(heapUsage.getCommitted()));
        performanceText.append("\n    最大量: " + byteCountToDisplaySize(heapUsage.getMax()));
        
        performanceText.append("\n\n内存非堆使用:\n");
        performanceText.append("    初始化: " + byteCountToDisplaySize(noheapUsage.getInit()));
        performanceText.append("\n    已使用: " + byteCountToDisplaySize(noheapUsage.getUsed()));
        performanceText.append("\n    已提交: " + byteCountToDisplaySize(noheapUsage.getCommitted()));
        performanceText.append("\n    最大量: " + byteCountToDisplaySize(noheapUsage.getMax()));
        
        //获取瞬态事物的数量
        def transiendSize = world.transientThingManager.getSize();
        performanceText.append("\n\n瞬态事物数量: " + transiendSize);
        
        //获取缓存数量
        //performanceText.append("\n\n" + UtilCache.toStaticsString());
        
        def String byteCountToDisplaySize(long size) {
            long ONE_KB = 1024;
            long ONE_MB = ONE_KB * ONE_KB;
            long ONE_GB = ONE_KB * ONE_MB;
            String displaySize;
        
            if (size / ONE_GB > 1) {
                displaySize = String.valueOf(size / ONE_GB) + " GB";
            } else if (size / ONE_MB > 1) {
                displaySize = String.valueOf(size / ONE_MB) + " MB";
            } else if (size / ONE_KB > 1) {
                displaySize = String.valueOf(size / ONE_KB) + " KB";
            } else {
                displaySize = String.valueOf(size) + " bytes";
            }
            return displaySize;
        }*/
    }

}