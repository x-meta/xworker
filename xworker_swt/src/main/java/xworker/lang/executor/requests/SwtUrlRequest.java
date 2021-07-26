package xworker.lang.executor.requests;

import org.xmeta.annotation.ActionField;

public class SwtUrlRequest {
    @ActionField
    public org.eclipse.swt.browser.Browser browser;

    @ActionField
    public String url;

    public void init(){
        if(url != null){
            browser.setUrl(url);
        }
    }
}
