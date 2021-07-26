package xworker.lang.executor.requests;

import org.xmeta.annotation.ActionField;

public class SwtHtmlRequest {
    @ActionField
    public org.eclipse.swt.browser.Browser browser;

    @ActionField
    public String html;

    public void init(){
        if(html != null){
            browser.setText(html);
        }
    }
}
