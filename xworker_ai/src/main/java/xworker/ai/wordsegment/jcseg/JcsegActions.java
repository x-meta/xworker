package xworker.ai.wordsegment.jcseg;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class JcsegActions {
	/** 默认的字典 */
	private static SoftReference<ADictionary> defaultDictionary = null;
	/** 默认的分词配置 */
	private static SoftReference<JcsegTaskConfig> defaultTaskConfig = null;
	
	public static String[] getSegment(String text) {
		if(text == null || "".equals(text)){
			return new String[0];
		}
		
		try{
			JcsegTaskConfig config = getJcsegTaskConfig(null);
			ADictionary dic = getJcsegDictionary();
			ISegment seg = SegmentFactory.createJcseg(JcsegTaskConfig.SIMPLE_MODE,	new Object[]{config, dic});
			seg.reset(new StringReader(text));
			IWord word = null;
			List<IWord> words = new ArrayList<IWord>();
			while((word = seg.next()) != null){
				words.add(word);
			}
			
			String[] strs = new String[words.size()];
			for(int i=0;i<words.size(); i++){
				word = words.get(i);
				strs[i] = word.getValue();
			}
			return strs;
		}catch(Exception e){
			return new String[0];
		}
	}
	
	public static List<IWord> runSegment(ActionContext actionContext) throws JcsegException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		int mode = JcsegTaskConfig.SIMPLE_MODE;
		String modeStr = self.getStringBlankAsNull("mode");
		if("SIMPLE_MODE".equals(modeStr)){
			mode = JcsegTaskConfig.SIMPLE_MODE;
		}else if("COMPLEX_MODE".equals(modeStr)){
			mode = JcsegTaskConfig.COMPLEX_MODE;
		}else if("DETECT_MODE".equals(modeStr)){
			mode = JcsegTaskConfig.DETECT_MODE;
		}
		
		JcsegTaskConfig config = (JcsegTaskConfig) self.doAction("getJcsegTaskConfig", actionContext);
		if(config == null){
			throw new ActionException("JcsegTaskConfig is null, thing=" + self.getMetadata().getPath());
		}
		ADictionary dic = (ADictionary) self.doAction("getJcsegDictionary", actionContext);
		if(dic == null){
			throw new ActionException("ADictionary is null, thing=" + self.getMetadata().getPath());
		}
		String text = (String) self.doAction("getText", actionContext);
		ISegment seg = SegmentFactory.createJcseg(mode,	new Object[]{config, dic});
		seg.reset(new StringReader(text));
		IWord word = null;
		List<IWord> words = new ArrayList<IWord>();
		while((word = seg.next()) != null){
			words.add(word);
		}

		return words;
	}
	
	public synchronized static JcsegTaskConfig getJcsegTaskConfig(ActionContext actionContext){
		World world = World.getInstance();
		
		JcsegTaskConfig config = null;
		if(defaultTaskConfig != null){
			config = defaultTaskConfig.get();
		}
		
		if(config == null){
			Thing configThing = world.getThing("_local.ai.wordsegment.JcsegTaskConfig");
			if(configThing == null){
				configThing = new Thing("xworker.ai.wordsegment.jcseg.JcsegTaskConfig");
				configThing.initDefaultValue();
				configThing.saveAs("_local", "_local.ai.wordsegment.JcsegTaskConfig");
			}
			config = (JcsegTaskConfig) configThing.doAction("create", actionContext);
			defaultTaskConfig = new SoftReference<JcsegTaskConfig>(config);
		}
		
		return config;
	}
	
	public static ADictionary getJcsegDictionary(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ADictionary dictionary = null;
		if(defaultDictionary != null){
			dictionary = defaultDictionary.get();
		}
		if(dictionary == null){
			JcsegTaskConfig config = (JcsegTaskConfig) self.doAction("getJcsegTaskConfig", actionContext);
			dictionary = DictionaryFactory.createDefaultDictionary(config);
			defaultDictionary = new SoftReference<ADictionary>(dictionary);
		}
		
		return dictionary;
	}
	
	public static ADictionary getJcsegDictionary(){
		ADictionary dictionary = null;
		if(defaultDictionary != null){
			dictionary = defaultDictionary.get();
		}
		if(dictionary == null){
			JcsegTaskConfig config =getJcsegTaskConfig(new ActionContext());
			dictionary = DictionaryFactory.createDefaultDictionary(config);
			defaultDictionary = new SoftReference<ADictionary>(dictionary);
		}
		
		return dictionary;
	}
	
	public static String getText(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String textVar = self.getStringBlankAsNull("textVar");
		if(textVar != null){
			return (String) OgnlUtil.getValue(self, "textVar", textVar, actionContext);
		}
		
		return self.getStringBlankAsNull("text");
	}
	
	/**
	 * 创建taskConfig。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static JcsegTaskConfig create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JcsegTaskConfig config = new JcsegTaskConfig(World.getInstance().getPath() + "/config/jsceg/jsceg.properties");
		config.MAX_LENGTH = self.getInt("MAX_LENGTH");
		config.MIX_CN_LENGTH = self.getInt("MIX_CN_LENGTH");
		config.I_CN_NAME = self.getBoolean("I_CN_NAME");
		config.MAX_CN_LNADRON = self.getInt("MAX_CN_LNADRON");
		config.PPT_MAX_LENGTH = self.getInt("PPT_MAX_LENGTH");
		config.LOAD_CJK_PINYIN = self.getBoolean("LOAD_CJK_PINYIN");
		config.APPEND_CJK_PINYIN = self.getBoolean("APPEND_CJK_PINYIN");
		config.LOAD_CJK_SYN = self.getBoolean("LOAD_CJK_SYN");
		config.APPEND_CJK_SYN = self.getBoolean("APPEND_CJK_SYN");
		config.LOAD_CJK_POS = self.getBoolean("LOAD_CJK_POS");
		config.CLEAR_STOPWORD = self.getBoolean("CLEAR_STOPWORD");
		config.CNNUM_TO_ARABIC = self.getBoolean("CNNUM_TO_ARABIC");
		config.CNFRA_TO_ARABIC = self.getBoolean("CNFRA_TO_ARABIC");
		config.KEEP_UNREG_WORDS = self.getBoolean("KEEP_UNREG_WORDS");
		config.EN_SECOND_SEG = self.getBoolean("EN_SECOND_SEG");
		config.NAME_SINGLE_THRESHOLD = self.getInt("NAME_SINGLE_THRESHOLD");
		String lexiconPath = self.getStringBlankAsNull("lexiconPath");
		String paths[] = null;
		String path = World.getInstance().getPath() + "/config/jsceg/lexicon/";
		if(lexiconPath != null){
			String ps[] = lexiconPath.split("[,]");
			paths = new String[ps.length + 1];
			paths[0] = path;
			for(int i=0;i<ps.length; i++){
				ps[i + 1] = ps[i];
			}
		}else{
			paths = new String[1];
			paths[0] = path;
		}
		config.setLexiconPath(paths);
		return config;
	}
}
