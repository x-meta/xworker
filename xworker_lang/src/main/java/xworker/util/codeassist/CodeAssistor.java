package xworker.util.codeassist;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.VariableDesc;

import java.util.*;

public class CodeAssistor implements VariableProvider{
    ActionContext actionContext;

    Map<String, VariableDesc> varCache = new HashMap<String, VariableDesc>();

    /**
     * 通常是代码的类型。
     */
    String textAssistor;

    public CodeAssistor(ActionContext actionContext){
        this.actionContext = actionContext;
    }

    /**
     * 放入一个变量描述到缓存。如果name或cls为null，返回null。
     *
     * @param name
     * @param cls
     * @return
     */
    public VariableDesc putCache(String name, Class<?> cls){
        if(name == null || "".equals(name) || cls == null) {
            return null;
        }

        VariableDesc desc = VariableDesc.create(name, cls);
        varCache.put(name, desc);
        return desc;
    }

    /**
     * 把一个变量描述添加到指定的模型里。
     *
     * @param thing
     * @param varDesc
     */
    public static void addVariableDesc(Thing thing, VariableDesc varDesc){
        if(thing == null || varDesc == null){
            return;
        }

        Thing descs = thing.getThing("VariablesDescs@0");
        if(descs == null) {
            descs = new Thing("xworker.lang.MetaThing/@VariablesDesc");
            thing.addChild(descs);

        }

        boolean have = false;
        String name = varDesc.getName();
        for(Thing desc : descs.getChilds()) {
            if("Variable".equals(desc.getThingName()) && name.equals(desc.getString("varName"))) {
                //修改已经有的
                desc.put("type", varDesc.getType());
                desc.put("className", varDesc.getClassName());
                desc.put("name", varDesc.getName());
                desc.put("varName", varDesc.getName());
                if(varDesc.getThing() != null){
                    desc.put("thing", varDesc.getThing().getMetadata().getPath());
                }
                have = true;
                break;
            }
        }

        if(!have) {
            Thing desc = new Thing("xworker.lang.MetaThing/@VariablesDesc/@Object");
            desc.put("type", varDesc.getType());
            desc.put("className", varDesc.getClassName());
            desc.put("name", varDesc.getName());
            desc.put("varName", varDesc.getName());
            if(varDesc.getThing() != null){
                desc.put("thing", varDesc.getThing().getMetadata().getPath());
            }
            descs.addChild(desc);
        }
    }

    public static void parseFunctionCall(String text, int index, int methodIndex, List<String> lists, Stack<char[]> matchs){
        if(index < 0){
            if(methodIndex > index){
                lists.add(0, text.substring(0, methodIndex));
            }

            return;
        }

        char c = text.charAt(index);
        if(c == ')'){
            matchs.push(new char[]{'('});
            parseFunctionCall(text, index - 1, -1, lists, matchs);
        }else if(c == '('){
            if(matchs.size() > 0){
                matchs.pop();
                parseFunctionCall(text, index - 1, index, lists, matchs);
            }else{
                if(methodIndex > index){
                    lists.add(0, text.substring(index + 1, methodIndex));
                }
                return;
            }
        }else if(c == ' ' || c == '\n' || c == ':'){
            if(methodIndex > index){
                lists.add(0, text.substring(index + 1, methodIndex));
            }

            if(matchs.size() == 0){
                return;
            }

            parseFunctionCall(text, index - 1, -1, lists, matchs);
        }else if(c == ';'){
            if(methodIndex > index){
                String str = text.substring(index, methodIndex);
                str = str.trim();
                if(!"".equals(str)){
                    lists.add(0, str);
                }
            }
            return;
        }else if(c == '.'){
            if(methodIndex > index){
                String str = text.substring(index + 1, methodIndex);
                str = str.trim();
                if(!"".equals(str)){
                    lists.add(0, str);
                }
            }

            parseFunctionCall(text, index - 1, index, lists, matchs);
        }else{
            parseFunctionCall(text, index - 1, methodIndex, lists, matchs);
        }
    }

    public List<CodeAssitContent> getClassContents(Thing thing, String text, int index, ActionContext actionContext){
        List<String> statements = new ArrayList<String>();
        parseFunctionCall(text, index - 1, index, statements, new Stack<char[]>());

        return CodeHelper.getObjectContents(text, index, statements, thing, actionContext);
    }

    /**
     * 返回指定文本的索引位置的词。
     *
     * @param text
     * @param index
     * @return
     */
    public String getCurrentWord(String text, int index){
        String str = text;
        String objectStr = "";
        for(int i = index - 1; i >= 0; i--){
            if(i < 0){
                break;
            }

            char c = str.charAt(i);
            if(c == '\n' || c == ' ' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == '.'){
                break;
            }

            objectStr = c + objectStr;
        }

        return objectStr;
    }


    /**
     * 返回文本索引处的所有而可选的辅助内容。
     *
     * @param thing
     * @param text
     * @param index
     * @param actionContext
     * @return
     */
    public List<CodeAssitContent> getAssistContents(Thing thing, String text, int index, ActionContext actionContext){
        return CodeHelper.getHelpContents(textAssistor, text, index, thing, actionContext);
    }

    public String getTextAssistor() {
        return textAssistor;
    }

    /**
     * 指定文本辅助器的名称，如sql、databind、reactors等。文本辅助器的名字是由TextAssistor对象自定义的，具体参看CodeHelper。
     *
     * @see CodeHelper
     * @param textAssistor
     */
    public void setTextAssistor(String textAssistor) {
        this.textAssistor = textAssistor;
    }

    @Override
    public List<VariableDesc> getVariables(String code, int offset, List<String> statements, Thing thing,
                                           ActionContext actionContext) {
        List<VariableDesc> vars = new ArrayList<VariableDesc>();
        for(String key : varCache.keySet()) {
            vars.add(varCache.get(key));
        }

        return vars;
    }
}
