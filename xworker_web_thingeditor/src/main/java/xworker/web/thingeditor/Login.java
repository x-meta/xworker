package xworker.web.thingeditor;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.UtilResource;
import org.xmeta.util.UtilString;
import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Login {
    public static LoginListener loginListener = null;

    public static void login(ActionContext actionContext){
        org.xmeta.Thing form = actionContext.getObject("form");
        org.eclipse.swt.widgets.Shell shell = actionContext.getObject("shell");
        Action error = actionContext.getObject("error");
        Action newpas = actionContext.getObject("newpas");

        Map<String, Object> values = form.doAction("getValues", actionContext);
        String lname = (String) values.get("loginName");
        String lpassword = (String) values.get("password");

        if(loginListener != null){
            if(!loginListener.login(lname, lpassword)){
                error.run(actionContext);
                return;
            }
        }else{
            Thing weblogin = World.getInstance().getThing("_local.xworker.config.WebLogin");
            if(weblogin != null) {
                if(!UtilData.isTrue(weblogin.getAction().run(actionContext))){
                    error.run(actionContext);
                    return;
                }
            }else{
                String name = XWorkerUtils.getConfigValue("XWorkerConfig", "editor.web.name");
                if (name == null || "".equals(name)) {
                    XWorkerUtils.setConfigValue("XWorkerConfig", "editor.web.name", "admin");
                }
                String password = XWorkerUtils.getConfigValue("XWorkerConfig", "editor.web.password");
                if (password == null || "".equals(password)) {
                    Random random = new Random();
                    password = String.valueOf(random.nextInt(1000000) + 10000);
                    XWorkerUtils.setConfigValue("XWorkerConfig", "editor.web.password", password);
                    newpas.run(actionContext, "password", password);
                    return;
                }
                if (!name.equals(lname) || !password.equals(lpassword)) {
                    error.run(actionContext);
                    return;
                }
            }
        }

        Session session = SessionManager.getSession( actionContext);
        session.setAttribute("xworker_thingManager", values);

        //登录成功
        ActionContext ac = new ActionContext();
        ac.put("parent", shell.getDisplay());
        Thing simpleThingEditor = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
        Shell newShell = simpleThingEditor.doAction("create", ac);
        newShell.open();
        newShell.setMaximized(true);
        shell.dispose();
    }

    public static void changeLanguage(ActionContext actionContext){

        org.xmeta.Thing form = actionContext.getObject("form");
        org.eclipse.swt.widgets.Button languageButton = actionContext.getObject("languageButton");
        org.eclipse.swt.widgets.Button loginButton = actionContext.getObject("loginButton");
        org.eclipse.swt.widgets.Shell shell = actionContext.getObject("shell");

        boolean zh = true;

        Session session = SessionManager.getSession(null);
        Locale locale = session.getLocale();
        if(locale != null && !locale.getLanguage().equals(new Locale("en").getLanguage())){
            zh = true;
        }else{
            zh = false;
        }

        if(zh == true){
            Locale l = new Locale("en", "US");
            session.setLocale(l);
            session.setI18nResource(UtilResource.getInstance(l));
        }else{
            Locale l = new Locale("zh", "CN");
            session.setLocale(l);
            session.setI18nResource(UtilResource.getInstance(l));
        }

        //重置标签
        loginButton.setText(UtilString.getString("lang:d=登录&en=Login", actionContext));
        languageButton.setText(UtilString.getString("lang:d=English&en=默认语言", actionContext));
        shell.setText(UtilString.getString("lang:d=登录&en=Login", actionContext));

        World world = World.getInstance();
        Thing formThing = world.getThing((String) form.get("extends"));
        //println formThing;
        Thing thing = formThing.getChilds().get(0);
        //println thing;
        form.doAction("setDescriptor", actionContext, "descriptor", thing);
        shell.pack();
    }
}
