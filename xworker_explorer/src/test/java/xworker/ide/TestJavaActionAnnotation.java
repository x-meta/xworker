package xworker.ide;
/*
 * Mavan会执行测试，不能引入swt包，所以打包xworker会失败，这里先注释掉。
 *
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.annotation.ActionParams;

@ActionClass(creator="newInstance")
public class TestJavaActionAnnotation {
	@ActionField(name="shell")
	Shell shellAbc;
	
	@ActionField
	Browser browser;
		
	@ActionParams(names="urlText")
	public void urlTextSelection(Text urlText, ActionContext actionContext) {
		browser.setUrl(urlText.getText());
	}
	
	public void okSelection(ActionContext actionContext) {
		System.out.println("okButton session ");
	}
		
	public void cancelSelection() {
		System.out.println("cancelButton session ");
		shellAbc.dispose();
	}
	
	public static Object newInstance(ActionContext actionContext) {
		return new TestJavaActionAnnotation();
	}
	
	public static void main(String[] args) {
		try {
			
			World world = World.getInstance();			
			world.init("./xworker/");
			
			Thing thing = World.getInstance().getThing("_local.test.core.actions.javaaction.TestAnnotation");
			thing.doAction("run");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
} */
