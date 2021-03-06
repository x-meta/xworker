/*path:xworker.swt.app.views.Outline/@outlineMainComposite/@init*/
package xworker.swt.app.views.Outline.p_2127391459;

import xworker.lang.executor.Executor;
if(editorContainer != null){
    editorContainer.setOutlineContainer(outlineContainer);
}else{
    Executor.warn("Outline", "editorContainer is null");
}