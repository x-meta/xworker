package xworker.lang.executor.requests;

import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;
import xworker.swt.util.SwtUtils;

public class SwtThingDescRequest {
    @ActionField
    public org.eclipse.swt.browser.Browser browser;

    @ActionField
    public Thing thing;

    public void init(){
        if(thing != null){
            SwtUtils.setThingDesc(thing, browser);
        }
    }
}
