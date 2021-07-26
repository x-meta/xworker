package xworker.examples.lang.thingloader;

import org.xmeta.annotation.ActionField;

public class SwtExample {
    @ActionField
    public org.eclipse.swt.browser.Browser browser;
    @ActionField
    public org.eclipse.swt.widgets.Button closeButton;
    @ActionField
    public org.eclipse.swt.widgets.Shell shell;

    public void init(){
        browser.setUrl("https://www.xworker.org");
    }

    public void close(){
        shell.dispose();
    }
}
