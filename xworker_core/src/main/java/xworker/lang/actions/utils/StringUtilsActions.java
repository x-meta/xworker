package xworker.lang.actions.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilJava;

public class StringUtilsActions {
	public static boolean isEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isEmpty(cs);
	}
	
	public static boolean isNotEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isNotEmpty(cs);
	}
	
	public static boolean isBlank(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isBlank(cs);
	}
	
	public static boolean isNotBlank(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isNotBlank(cs);
	}
	
	public static String trim(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.trim(str);
	}
	
	public static String trimToNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.trimToNull(str);
	}
	
	public static String trimToEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.trimToEmpty(str);
	}
	
	public static String strip(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		String stripChars = (String) self.doAction("getStripChars", actionContext);
		return StringUtils.strip(str, stripChars);
	}
	
	public static String stripToNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.stripToNull(str);
	}
	
	public static String stripToEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.stripToEmpty(str);
	}
	
	public static String stripStart(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		String stripChars = (String) self.doAction("getStripChars", actionContext);
		return StringUtils.stripStart(str, stripChars);
	}
	
	public static String stripEnd(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		String stripChars = (String) self.doAction("getStripChars", actionContext);
		return StringUtils.stripEnd(str, stripChars);
	}
	
	public static String[] stripAll(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String[] strs = (String[]) self.doAction("getStrs", actionContext);
		String stripChars = (String) self.doAction("getStripChars", actionContext);
		return StringUtils.stripAll(strs, stripChars);
	}
	
	public static String stripAccents(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str = (String) self.doAction("getStr", actionContext);
		return StringUtils.stripAccents(str);
	}
	
	public static boolean equals(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.equals(cs1, cs2);
	}
	
	public static boolean equalsIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.equalsIgnoreCase(cs1, cs2);
	}
	
	public static int indexOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.indexOf(cs1, cs2, startPos);
	}
	
	public static int ordinalIndexOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.ordinalIndexOf(cs1, cs2, startPos);
	}
	
	public static int indexOfIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.indexOfIgnoreCase(cs1, cs2, startPos);
	}
	
	public static int lastIndexOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.lastIndexOf(cs1, cs2, startPos);
	}
	
	public static int lastOrdinalIndexOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.lastOrdinalIndexOf(cs1, cs2, startPos);
	}
	
	public static int lastIndexOfIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		Integer startPos = (Integer) self.doAction("getStartPos", actionContext);
		return StringUtils.lastIndexOfIgnoreCase(cs1, cs2, startPos);
	}
	
	public static boolean contains(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.contains(cs1, cs2);
	}
	
	public static boolean containsIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.containsIgnoreCase(cs1, cs2);
	}
	
	public static int indexOfAny(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		String searchChars  = (String) self.doAction("getSearchChars", actionContext);
		return StringUtils.indexOfAny(cs1,searchChars);
	}
	
	public static boolean containsAny(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.containsAny(cs1, cs2);
	}
	
	public static int indexOfAnyBut(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.indexOfAnyBut(cs1, cs2);
	}
	
	public static boolean containsOnly(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs", actionContext);
		String validChars  = (String) self.doAction("getValidChars", actionContext);
		return StringUtils.containsOnly(cs1, validChars);
	}
	
	public static boolean containsNone(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs", actionContext);
		String invalidChars  = (String) self.doAction("getInvalidChars", actionContext);
		return StringUtils.containsNone(cs1, invalidChars);
	}
	
	public static int indexOfAnyStr(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		CharSequence[] invalidChars  = (CharSequence[]) self.doAction("getSearchStrs", actionContext);
		return StringUtils.indexOfAny(cs, invalidChars);
	}
	
	public static String[] searchStrs(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String strs = UtilData.getString(self, "searchStrs", actionContext);
		return strs.split("[,]");
	}
	
	public static int lastIndexOfAnyStr(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		CharSequence[] invalidChars  = (CharSequence[]) self.doAction("getSearchStrs", actionContext);
		return StringUtils.lastIndexOfAny(cs, invalidChars);
	}
	
	public static String substring(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		Integer start  = (Integer) self.doAction("getStart", actionContext);
		Integer end  = (Integer) self.doAction("getEnd", actionContext);
		return StringUtils.substring(str, start, end);
	}	

	public static String left(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		Integer len  = (Integer) self.doAction("getLen", actionContext);
		return StringUtils.left(str, len);
	}
	
	public static String right(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		Integer len  = (Integer) self.doAction("getLen", actionContext);
		return StringUtils.right(str, len);
	}
	
	public static String mid(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		Integer pos  = (Integer) self.doAction("getPos", actionContext);
		Integer len  = (Integer) self.doAction("getLen", actionContext);
		return StringUtils.mid(str, pos, len);
	}	
	
	public static String substringBefore(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		return StringUtils.substringBefore(str, separator);
	}
	
	public static String substringAfter(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		return StringUtils.substringAfter(str, separator);
	}
	
	public static String substringBeforeLast(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		return StringUtils.substringBeforeLast(str, separator);
	}
	
	public static String substringAfterLast(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		return StringUtils.substringAfterLast(str, separator);
	}
	
	public static String substringBetween(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String tag  = (String) self.doAction("getTag", actionContext);
		return StringUtils.substringBetween(str, tag);
	}
	
	public static String substringBetweenOpenClose(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String open  = (String) self.doAction("getOpen", actionContext);
		String close  = (String) self.doAction("getClose", actionContext);
		return StringUtils.substringBetween(str, open, close);
	}
	
	public static String[] substringsBetween(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String open  = (String) self.doAction("getOpen", actionContext);
		String close  = (String) self.doAction("getClose", actionContext);
		return StringUtils.substringsBetween(str, open, close);
	}
	
	public static String[] split(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separatorChars  = (String) self.doAction("getSeparatorChars", actionContext);
		Integer max  = (Integer) self.doAction("getMax", actionContext);
		return StringUtils.split(str, separatorChars, max);
	}
	
	public static String[] splitByWholeSeparator(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		Integer max  = (Integer) self.doAction("getMax", actionContext);
		return StringUtils.splitByWholeSeparator(str, separator, max);
	}
	
	public static String[] splitByWholeSeparatorPreserveAllTokens(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		Integer max  = (Integer) self.doAction("getMax", actionContext);
		return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max);
	}
	
	public static String[] splitPreserveAllTokens(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separatorChars  = (String) self.doAction("getSeparatorChars", actionContext);
		Integer max  = (Integer) self.doAction("getMax", actionContext);
		return StringUtils.splitPreserveAllTokens(str, separatorChars, max);
	}
	
	public static String[] splitByCharacterType(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.splitByCharacterType(str);
	}
	
	public static String[] splitByCharacterTypeCamelCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.splitByCharacterTypeCamelCase(str);
	}
	
	public static String join(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String separator  = (String) self.doAction("getSeparator", actionContext);
		Iterable<Object> iterable = UtilJava.getIterable(self.doAction("getIterable", actionContext));
		return StringUtils.join(iterable, separator);
	}
	
	public static String deleteWhitespace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.deleteWhitespace(str);
	}
	
	public static String removeStart(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String remove  = (String) self.doAction("getRemove", actionContext);
		return StringUtils.removeStart(str, remove);
	}
	
	public static String removeStartIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String remove  = (String) self.doAction("getRemove", actionContext);
		return StringUtils.removeStartIgnoreCase(str, remove);
	}
	
	public static String removeEnd(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String remove  = (String) self.doAction("getRemove", actionContext);
		return StringUtils.removeEnd(str, remove);
	}
	
	public static String removeEndIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String remove  = (String) self.doAction("getRemove", actionContext);
		return StringUtils.removeEndIgnoreCase(str, remove);
	}
	
	public static String remove(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String remove  = (String) self.doAction("getRemove", actionContext);
		return StringUtils.remove(str, remove);
	}
	
	public static String replace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String text  = (String) self.doAction("getText", actionContext);
		String searchString  = (String) self.doAction("getSearchString", actionContext);
		String replacement  = (String) self.doAction("getReplacement", actionContext);
		Integer max  = (Integer) self.doAction("getMax", actionContext);
		return StringUtils.replace(text, searchString, replacement, max);
	}
	
	public static String replaceEach(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String text  = (String) self.doAction("getText", actionContext);
		String[] searchList  = (String[]) self.doAction("getSearchList", actionContext);
		String[] replacementList  = (String[]) self.doAction("getReplacementList", actionContext);
		return StringUtils.replaceEach(text, searchList, replacementList);
	}
	
	public static String replaceEachRepeatedly(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String text  = (String) self.doAction("getText", actionContext);
		String[] searchList  = (String[]) self.doAction("getSearchList", actionContext);
		String[] replacementList  = (String[]) self.doAction("getReplacementList", actionContext);
		return StringUtils.replaceEachRepeatedly(text, searchList, replacementList);
	}
	
	public static String replaceChars(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String searchChars  = (String) self.doAction("getSearchChars", actionContext);
		String replaceChars  = (String) self.doAction("getReplaceChars", actionContext);
		return StringUtils.replaceChars(str, searchChars, replaceChars);
	}
	
	public static String chomp(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.chomp(str);
	}
	
	public static String chop(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.chop(str);
	}
	
	public static String repeat(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String separator  = (String) self.doAction("getSeparator", actionContext);
		Integer repeat  = (Integer) self.doAction("getRepeat", actionContext);
		return StringUtils.repeat(str, separator, repeat);
	}
	
	public static String rightPad(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String padStr  = (String) self.doAction("getPadStr", actionContext);
		Integer size  = (Integer) self.doAction("getSize", actionContext);
		return StringUtils.rightPad(str, size, padStr);
	}
	
	public static String leftPad(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String padStr  = (String) self.doAction("getPadStr", actionContext);
		Integer size  = (Integer) self.doAction("getSize", actionContext);
		return StringUtils.leftPad(str, size, padStr);
	}
	
	public static int length(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.length(cs);
	}
	
	public static String center(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String padStr  = (String) self.doAction("getPadStr", actionContext);
		Integer size  = (Integer) self.doAction("getSize", actionContext);
		return StringUtils.center(str, size, padStr);
	}
	
	public static String upperCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.upperCase(str);
	}
	
	public static String lowerCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.lowerCase(str);
	}
	
	public static String capitalize(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.capitalize(str);
	}
	
	public static String uncapitalize(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.uncapitalize(str);
	}
	
	public static String swapCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.swapCase(str);
	}
	
	public static int countMatches(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.countMatches(cs1, cs2);
	}
	
	public static boolean isAlpha(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAlpha(cs);
	}
	
	public static boolean isAlphaSpace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAlphaSpace(cs);
	}
	
	public static boolean isAlphanumeric(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAlphanumeric(cs);
	}
	
	public static boolean isAlphanumericSpace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAlphanumericSpace(cs);
	}
	
	public static boolean isAsciiPrintable(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAsciiPrintable(cs);
	}
	
	public static boolean isNumeric(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isNumeric(cs);
	}
	
	public static boolean isNumericSpace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isNumericSpace(cs);
	}
	
	public static boolean isWhitespace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isWhitespace(cs);
	}
	
	public static boolean isAllLowerCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAllLowerCase(cs);
	}
	
	public static boolean isAllUpperCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs  = (CharSequence) self.doAction("getCs", actionContext);
		return StringUtils.isAllUpperCase(cs);
	}
	
	public static String defaultString(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String defaultStr  = (String) self.doAction("getDefaultStr", actionContext);
		return StringUtils.defaultString(str, defaultStr);
	}
	
	public static String reverse(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.reverse(str);
	}
	
	public static String abbreviate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		Integer offset  = (Integer) self.doAction("getOffset", actionContext);
		Integer maxWidth  = (Integer) self.doAction("getMaxWidth", actionContext);
		return StringUtils.abbreviate(str, offset, maxWidth);
	}
	
	public static String abbreviateMiddle(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		String middle  = (String) self.doAction("getMiddle", actionContext);
		Integer length  = (Integer) self.doAction("getLength", actionContext);
		return StringUtils.abbreviateMiddle(str, middle, length);
	}
	
	public static String difference(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str1  = (String) self.doAction("getStr1", actionContext);
		String str2  = (String) self.doAction("getStr2", actionContext);
		return StringUtils.difference(str1, str2);
	}
	
	public static int indexOfDifference(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.indexOfDifference(cs1, cs2);
	}
	
	public static boolean startsWith(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.startsWith(cs1, cs2);
	}
	
	public static boolean startsWithIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.startsWithIgnoreCase(cs1, cs2);
	}
	
	public static boolean endsWith(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.endsWith(cs1, cs2);
	}
	
	public static boolean endsWithIgnoreCase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CharSequence cs1  = (CharSequence) self.doAction("getCs1", actionContext);
		CharSequence cs2  = (CharSequence) self.doAction("getCs2", actionContext);
		return StringUtils.endsWithIgnoreCase(cs1, cs2);
	}
	
	public static String normalizeSpace(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringUtils.normalizeSpace(str);
	}
	
	public static String escapeJava(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeJava(str);
	}
	
	public static String escapeEcmaScript(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeEcmaScript(str);
	}

	public static String unescapeJava(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeJava(str);
	}	
	
	public static String unescapeEcmaScript(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeEcmaScript(str);
	}	
	
	public static String escapeHtml4(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeHtml4(str);
	}	
	
	public static String escapeHtml3(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeHtml3(str);
	}
	
	public static String unescapeHtml4(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeHtml4(str);
	}
	
	public static String unescapeHtml3(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeHtml3(str);
	}
	
	@SuppressWarnings("deprecation")
	public static String escapeXml(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeXml(str);
	}
	
	public static String unescapeXml(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeXml(str);
	}
	
	public static String escapeCsv(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.escapeCsv(str);
	}
	
	public static String unescapeCsv(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String str  = (String) self.doAction("getStr", actionContext);
		return StringEscapeUtils.unescapeCsv(str);
	}
}
