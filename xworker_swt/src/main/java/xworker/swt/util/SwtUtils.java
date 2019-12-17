/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
//import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.actions.MenuActions;
import xworker.swt.browser.BrowserCallback;
import xworker.swt.custom.StyledTextProxy;
import xworker.swt.design.Designer;
import xworker.swt.model.Model;
import xworker.swt.model.ModelManager;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.widgets.ControlActions;
import xworker.util.StringUtils;
import xworker.util.XWorkerUtils;

public class SwtUtils {	
	private static Logger log = LoggerFactory.getLogger(SwtUtils.class);
	private static Map<String ,Integer> swtKeys = new HashMap<String, Integer>();
	public static final int ABOVE = 1;
	public static final int BELOW = 2;
	public static final int INSIDE = 3;
	public static final int REPLACE = 4;
	/**
	 * 用于组件初始化时调用者传入到默认初始化style。
	 */
	private static ThreadLocal<ActionContext> creatorInitStyleLocal = new ThreadLocal<ActionContext>();
	private static Boolean isRWT = null;
	static{
		swtKeys.put("ABORT", SWT.ABORT);
		swtKeys.put("Activate", SWT.Activate);
		swtKeys.put("ALPHA", SWT.ALPHA);
		swtKeys.put("ALT", SWT.ALT);
		swtKeys.put("APPLICATION_MODAL", SWT.APPLICATION_MODAL);
		swtKeys.put("Arm", SWT.Arm);
		swtKeys.put("ARROW", SWT.ARROW);
		swtKeys.put("ARROW_DOWN", SWT.ARROW_DOWN);
		swtKeys.put("ARROW_LEFT", SWT.ARROW_LEFT);
		swtKeys.put("ARROW_RIGHT", SWT.ARROW_RIGHT);
		swtKeys.put("ARROW_UP", SWT.ARROW_UP);
		swtKeys.put("BACKGROUND", SWT.BACKGROUND);
		swtKeys.put("BALLOON", SWT.BALLOON);
		swtKeys.put("BAR", SWT.BAR);
		swtKeys.put("BEGINNING", SWT.BEGINNING);
		swtKeys.put("BITMAP", SWT.BITMAP);
		swtKeys.put("BOLD", SWT.BOLD);
		swtKeys.put("BORDER", SWT.BORDER);
		swtKeys.put("BORDER_DASH", SWT.BORDER_DASH);
		swtKeys.put("BORDER_DOT", SWT.BORDER_DOT);
		swtKeys.put("BORDER_SOLID", SWT.BORDER_SOLID);
		swtKeys.put("BOTTOM", SWT.BOTTOM);
		swtKeys.put("BREAK", SWT.BREAK);
		swtKeys.put("BS", (int) SWT.BS);
		swtKeys.put("BUTTON_MASK", SWT.BUTTON_MASK);
		swtKeys.put("BUTTON1", SWT.BUTTON1);
		swtKeys.put("BUTTON2", SWT.BUTTON2);
		swtKeys.put("BUTTON3", SWT.BUTTON3);
		swtKeys.put("BUTTON4", SWT.BUTTON4);
		swtKeys.put("BUTTON5", SWT.BUTTON5);
		swtKeys.put("CALENDAR", SWT.CALENDAR);
		swtKeys.put("CANCEL", SWT.CANCEL);
		swtKeys.put("CAP_FLAT", SWT.CAP_FLAT);
		swtKeys.put("CAP_ROUND", SWT.CAP_ROUND);
		swtKeys.put("CAP_SQUARE", SWT.CAP_SQUARE);
		swtKeys.put("CAPS_LOCK", SWT.CAPS_LOCK);
		swtKeys.put("CASCADE", SWT.CASCADE);
		swtKeys.put("CENTER", SWT.CENTER);
		swtKeys.put("CHECK", SWT.CHECK);
		swtKeys.put("CLIP_CHILDREN", SWT.CLIP_CHILDREN);
		swtKeys.put("CLIP_SIBLINGS", SWT.CLIP_SIBLINGS);
		swtKeys.put("Close", SWT.Close);
		swtKeys.put("CLOSE", SWT.CLOSE);
		swtKeys.put("Collapse", SWT.Collapse);
		swtKeys.put("COLOR_BLACK", SWT.COLOR_BLACK);
		swtKeys.put("COLOR_BLUE", SWT.COLOR_BLUE);
		swtKeys.put("COLOR_CYAN", SWT.COLOR_CYAN);
		swtKeys.put("COLOR_DARK_BLUE", SWT.COLOR_DARK_BLUE);
		swtKeys.put("COLOR_DARK_CYAN", SWT.COLOR_DARK_CYAN);
		swtKeys.put("COLOR_DARK_GRAY", SWT.COLOR_DARK_GRAY);
		swtKeys.put("COLOR_DARK_GREEN", SWT.COLOR_DARK_GREEN);
		swtKeys.put("COLOR_DARK_MAGENTA", SWT.COLOR_DARK_MAGENTA);
		swtKeys.put("COLOR_DARK_RED", SWT.COLOR_DARK_RED);
		swtKeys.put("COLOR_DARK_YELLOW", SWT.COLOR_DARK_YELLOW);
		swtKeys.put("COLOR_GRAY", SWT.COLOR_GRAY);
		swtKeys.put("COLOR_GREEN", SWT.COLOR_GREEN);
		swtKeys.put("COLOR_INFO_BACKGROUND", SWT.COLOR_INFO_BACKGROUND);
		swtKeys.put("COLOR_INFO_FOREGROUND", SWT.COLOR_INFO_FOREGROUND);
		swtKeys.put("COLOR_LIST_BACKGROUND", SWT.COLOR_LIST_BACKGROUND);
		swtKeys.put("COLOR_LIST_FOREGROUND", SWT.COLOR_LIST_FOREGROUND);
		swtKeys.put("COLOR_LIST_SELECTION", SWT.COLOR_LIST_SELECTION);
		swtKeys.put("COLOR_LIST_SELECTION_TEXT", SWT.COLOR_LIST_SELECTION_TEXT);
		swtKeys.put("COLOR_MAGENTA", SWT.COLOR_MAGENTA);
		swtKeys.put("COLOR_RED", SWT.COLOR_RED);
		swtKeys.put("COLOR_TITLE_BACKGROUND", SWT.COLOR_TITLE_BACKGROUND);
		swtKeys.put("COLOR_TITLE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		swtKeys.put("COLOR_TITLE_FOREGROUND", SWT.COLOR_TITLE_FOREGROUND);
		swtKeys.put("COLOR_TITLE_INACTIVE_BACKGROUND", SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		swtKeys.put("COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
		swtKeys.put("COLOR_TITLE_INACTIVE_FOREGROUND", SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
		swtKeys.put("COLOR_WHITE", SWT.COLOR_WHITE);
		swtKeys.put("COLOR_WIDGET_BACKGROUND", SWT.COLOR_WIDGET_BACKGROUND);
		swtKeys.put("COLOR_WIDGET_BORDER", SWT.COLOR_WIDGET_BORDER);
		swtKeys.put("COLOR_WIDGET_DARK_SHADOW", SWT.COLOR_WIDGET_DARK_SHADOW);
		swtKeys.put("COLOR_WIDGET_FOREGROUND", SWT.COLOR_WIDGET_FOREGROUND);
		swtKeys.put("COLOR_WIDGET_HIGHLIGHT_SHADOW", SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		swtKeys.put("COLOR_WIDGET_LIGHT_SHADOW", SWT.COLOR_WIDGET_LIGHT_SHADOW);
		swtKeys.put("COLOR_WIDGET_NORMAL_SHADOW", SWT.COLOR_WIDGET_NORMAL_SHADOW);
		swtKeys.put("COLOR_YELLOW", SWT.COLOR_YELLOW);
		swtKeys.put("COMMAND", SWT.COMMAND);
		swtKeys.put("COMPOSITION_CHANGED", SWT.COMPOSITION_CHANGED);
		swtKeys.put("COMPOSITION_OFFSET", SWT.COMPOSITION_OFFSET);
		swtKeys.put("COMPOSITION_SELECTION", SWT.COMPOSITION_SELECTION);
		swtKeys.put("CONTROL", SWT.CONTROL);
		swtKeys.put("CR", (int) SWT.CR);
		swtKeys.put("CTRL", SWT.CTRL);
		swtKeys.put("CURSOR_APPSTARTING", SWT.CURSOR_APPSTARTING);
		swtKeys.put("CURSOR_ARROW", SWT.CURSOR_ARROW);
		swtKeys.put("CURSOR_CROSS", SWT.CURSOR_CROSS);
		swtKeys.put("CURSOR_HAND", SWT.CURSOR_HAND);
		swtKeys.put("CURSOR_HELP", SWT.CURSOR_HELP);
		swtKeys.put("CURSOR_IBEAM", SWT.CURSOR_IBEAM);
		swtKeys.put("CURSOR_NO", SWT.CURSOR_NO);
		swtKeys.put("CURSOR_SIZEALL", SWT.CURSOR_SIZEALL);
		swtKeys.put("CURSOR_SIZEE", SWT.CURSOR_SIZEE);
		swtKeys.put("CURSOR_SIZEN", SWT.CURSOR_SIZEN);
		swtKeys.put("CURSOR_SIZENE", SWT.CURSOR_SIZENE);
		swtKeys.put("CURSOR_SIZENESW", SWT.CURSOR_SIZENESW);
		swtKeys.put("CURSOR_SIZENS", SWT.CURSOR_SIZENS);
		swtKeys.put("CURSOR_SIZENW", SWT.CURSOR_SIZENW);
		swtKeys.put("CURSOR_SIZENWSE", SWT.CURSOR_SIZENWSE);
		swtKeys.put("CURSOR_SIZES", SWT.CURSOR_SIZES);
		swtKeys.put("CURSOR_SIZESE", SWT.CURSOR_SIZESE);
		swtKeys.put("CURSOR_SIZESW", SWT.CURSOR_SIZESW);
		swtKeys.put("CURSOR_SIZEW", SWT.CURSOR_SIZEW);
		swtKeys.put("CURSOR_SIZEWE", SWT.CURSOR_SIZEWE);
		swtKeys.put("CURSOR_UPARROW", SWT.CURSOR_UPARROW);
		swtKeys.put("CURSOR_WAIT", SWT.CURSOR_WAIT);
		swtKeys.put("DATE", SWT.DATE);
		swtKeys.put("DBCS", SWT.DBCS);
		swtKeys.put("Deactivate", SWT.Deactivate);
		swtKeys.put("DEFAULT", SWT.DEFAULT);
		swtKeys.put("DefaultSelection", SWT.DefaultSelection);
		swtKeys.put("Deiconify", SWT.Deiconify);
		swtKeys.put("DEL", (int) SWT.DEL);
		swtKeys.put("DELIMITER_SELECTION", SWT.DELIMITER_SELECTION);
		swtKeys.put("DIALOG_TRIM", SWT.DIALOG_TRIM);
		swtKeys.put("Dispose", SWT.Dispose);
		swtKeys.put("DM_FILL_BACKGROUND", SWT.DM_FILL_BACKGROUND);
		swtKeys.put("DM_FILL_NONE", SWT.DM_FILL_NONE);
		swtKeys.put("DM_FILL_PREVIOUS", SWT.DM_FILL_PREVIOUS);
		swtKeys.put("DM_UNSPECIFIED", SWT.DM_UNSPECIFIED);
		swtKeys.put("DOUBLE_BUFFERED", SWT.DOUBLE_BUFFERED);
		swtKeys.put("DOWN", SWT.DOWN);
		swtKeys.put("DRAG", SWT.DRAG);
		swtKeys.put("DragDetect", SWT.DragDetect);
		swtKeys.put("DRAW_DELIMITER", SWT.DRAW_DELIMITER);
		swtKeys.put("DRAW_MNEMONIC", SWT.DRAW_MNEMONIC);
		swtKeys.put("DRAW_TAB", SWT.DRAW_TAB);
		swtKeys.put("DRAW_TRANSPARENT", SWT.DRAW_TRANSPARENT);
		swtKeys.put("DROP_DOWN", SWT.DROP_DOWN);
		swtKeys.put("EMBEDDED", SWT.EMBEDDED);
		swtKeys.put("END", SWT.END);
		swtKeys.put("EraseItem", SWT.EraseItem);
		swtKeys.put("ERROR", SWT.ERROR);
		swtKeys.put("ERROR_CANNOT_BE_ZERO", SWT.ERROR_CANNOT_BE_ZERO);
		swtKeys.put("ERROR_CANNOT_GET_COUNT", SWT.ERROR_CANNOT_GET_COUNT);
		swtKeys.put("ERROR_CANNOT_GET_ENABLED", SWT.ERROR_CANNOT_GET_ENABLED);
		swtKeys.put("ERROR_CANNOT_GET_ITEM", SWT.ERROR_CANNOT_GET_ITEM);
		swtKeys.put("ERROR_CANNOT_GET_ITEM_HEIGHT", SWT.ERROR_CANNOT_GET_ITEM_HEIGHT);
		swtKeys.put("ERROR_CANNOT_GET_SELECTION", SWT.ERROR_CANNOT_GET_SELECTION);
		swtKeys.put("ERROR_CANNOT_GET_TEXT", SWT.ERROR_CANNOT_GET_TEXT);
		swtKeys.put("ERROR_CANNOT_INVERT_MATRIX", SWT.ERROR_CANNOT_INVERT_MATRIX);
		swtKeys.put("ERROR_CANNOT_SET_ENABLED", SWT.ERROR_CANNOT_SET_ENABLED);
		swtKeys.put("ERROR_CANNOT_SET_MENU", SWT.ERROR_CANNOT_SET_MENU);
		swtKeys.put("ERROR_CANNOT_SET_SELECTION", SWT.ERROR_CANNOT_SET_SELECTION);
		swtKeys.put("ERROR_CANNOT_SET_TEXT", SWT.ERROR_CANNOT_SET_TEXT);
		swtKeys.put("ERROR_DEVICE_DISPOSED", SWT.ERROR_DEVICE_DISPOSED);
		swtKeys.put("ERROR_FAILED_EXEC", SWT.ERROR_FAILED_EXEC);
		swtKeys.put("ERROR_FAILED_LOAD_LIBRARY", SWT.ERROR_FAILED_LOAD_LIBRARY);
		swtKeys.put("ERROR_GRAPHIC_DISPOSED", SWT.ERROR_GRAPHIC_DISPOSED);
		swtKeys.put("ERROR_INVALID_ARGUMENT", SWT.ERROR_INVALID_ARGUMENT);
		swtKeys.put("ERROR_INVALID_FONT", SWT.ERROR_INVALID_FONT);
		swtKeys.put("ERROR_INVALID_IMAGE", SWT.ERROR_INVALID_IMAGE);
		swtKeys.put("ERROR_INVALID_PARENT", SWT.ERROR_INVALID_PARENT);
		swtKeys.put("ERROR_INVALID_RANGE", SWT.ERROR_INVALID_RANGE);
		swtKeys.put("ERROR_INVALID_SUBCLASS", SWT.ERROR_INVALID_SUBCLASS);
		swtKeys.put("ERROR_IO", SWT.ERROR_IO);
		swtKeys.put("ERROR_ITEM_NOT_ADDED", SWT.ERROR_ITEM_NOT_ADDED);
		swtKeys.put("ERROR_ITEM_NOT_REMOVED", SWT.ERROR_ITEM_NOT_REMOVED);
		swtKeys.put("ERROR_MENU_NOT_BAR", SWT.ERROR_MENU_NOT_BAR);
		swtKeys.put("ERROR_MENU_NOT_DROP_DOWN", SWT.ERROR_MENU_NOT_DROP_DOWN);
		swtKeys.put("ERROR_MENU_NOT_POP_UP", SWT.ERROR_MENU_NOT_POP_UP);
		swtKeys.put("ERROR_MENUITEM_NOT_CASCADE", SWT.ERROR_MENUITEM_NOT_CASCADE);
		swtKeys.put("ERROR_NO_GRAPHICS_LIBRARY", SWT.ERROR_NO_GRAPHICS_LIBRARY);
		swtKeys.put("ERROR_NO_HANDLES", SWT.ERROR_NO_HANDLES);
		swtKeys.put("ERROR_NO_MORE_CALLBACKS", SWT.ERROR_NO_MORE_CALLBACKS);
		swtKeys.put("ERROR_NOT_IMPLEMENTED", SWT.ERROR_NOT_IMPLEMENTED);
		swtKeys.put("ERROR_NULL_ARGUMENT", SWT.ERROR_NULL_ARGUMENT);
		swtKeys.put("ERROR_THREAD_INVALID_ACCESS", SWT.ERROR_THREAD_INVALID_ACCESS);
		swtKeys.put("ERROR_UNSPECIFIED", SWT.ERROR_UNSPECIFIED);
		swtKeys.put("ERROR_UNSUPPORTED_DEPTH", SWT.ERROR_UNSUPPORTED_DEPTH);
		swtKeys.put("ERROR_UNSUPPORTED_FORMAT", SWT.ERROR_UNSUPPORTED_FORMAT);
		swtKeys.put("ERROR_WIDGET_DISPOSED", SWT.ERROR_WIDGET_DISPOSED);
		swtKeys.put("ESC", (int) SWT.ESC);
		swtKeys.put("Expand", SWT.Expand);
		swtKeys.put("F1", SWT.F1);
		swtKeys.put("F10", SWT.F10);
		swtKeys.put("F11", SWT.F11);
		swtKeys.put("F12", SWT.F12);
		swtKeys.put("F13", SWT.F13);
		swtKeys.put("F14", SWT.F14);
		swtKeys.put("F15", SWT.F15);
		swtKeys.put("F2", SWT.F2);
		swtKeys.put("F3", SWT.F3);
		swtKeys.put("F4", SWT.F4);
		swtKeys.put("F5", SWT.F5);
		swtKeys.put("F6", SWT.F6);
		swtKeys.put("F7", SWT.F7);
		swtKeys.put("F8", SWT.F8);
		swtKeys.put("F9", SWT.F9);
		swtKeys.put("FILL", SWT.FILL);
		swtKeys.put("FILL_EVEN_ODD", SWT.FILL_EVEN_ODD);
		swtKeys.put("FILL_WINDING", SWT.FILL_WINDING);
		swtKeys.put("FLAT", SWT.FLAT);
		swtKeys.put("FOCUSED", SWT.FOCUSED);
		swtKeys.put("FocusIn", SWT.FocusIn);
		swtKeys.put("FocusOut", SWT.FocusOut);
		swtKeys.put("FOREGROUND", SWT.FOREGROUND);
		swtKeys.put("FULL_SELECTION", SWT.FULL_SELECTION);
		swtKeys.put("H_SCROLL", SWT.H_SCROLL);
		swtKeys.put("HardKeyDown", SWT.HardKeyDown);
		swtKeys.put("HardKeyUp", SWT.HardKeyUp);
		swtKeys.put("Help", SWT.Help);
		swtKeys.put("HELP", SWT.HELP);
		swtKeys.put("Hide", SWT.Hide);
		swtKeys.put("HIDE_SELECTION", SWT.HIDE_SELECTION);
		swtKeys.put("HIGH", SWT.HIGH);
		swtKeys.put("HOME", SWT.HOME);
		swtKeys.put("HORIZONTAL", SWT.HORIZONTAL);
		swtKeys.put("HOT", SWT.HOT);
		swtKeys.put("ICON", SWT.ICON);
		swtKeys.put("ICON_ERROR", SWT.ICON_ERROR);
		swtKeys.put("ICON_INFORMATION", SWT.ICON_INFORMATION);
		swtKeys.put("ICON_QUESTION", SWT.ICON_QUESTION);
		swtKeys.put("ICON_WARNING", SWT.ICON_WARNING);
		swtKeys.put("ICON_WORKING", SWT.ICON_WORKING);
		swtKeys.put("Iconify", SWT.Iconify);
		swtKeys.put("IGNORE", SWT.IGNORE);
		swtKeys.put("IMAGE_BMP", SWT.IMAGE_BMP);
		swtKeys.put("IMAGE_BMP_RLE", SWT.IMAGE_BMP_RLE);
		swtKeys.put("IMAGE_COPY", SWT.IMAGE_COPY);
		swtKeys.put("IMAGE_DISABLE", SWT.IMAGE_DISABLE);
		swtKeys.put("IMAGE_GIF", SWT.IMAGE_GIF);
		swtKeys.put("IMAGE_GRAY", SWT.IMAGE_GRAY);
		swtKeys.put("IMAGE_ICO", SWT.IMAGE_ICO);
		swtKeys.put("IMAGE_JPEG", SWT.IMAGE_JPEG);
		swtKeys.put("IMAGE_OS2_BMP", SWT.IMAGE_OS2_BMP);
		swtKeys.put("IMAGE_PNG", SWT.IMAGE_PNG);
		swtKeys.put("IMAGE_TIFF", SWT.IMAGE_TIFF);
		swtKeys.put("IMAGE_UNDEFINED", SWT.IMAGE_UNDEFINED);
		swtKeys.put("ImeComposition", SWT.ImeComposition);
		swtKeys.put("INDETERMINATE", SWT.INDETERMINATE);
		swtKeys.put("INHERIT_DEFAULT", SWT.INHERIT_DEFAULT);
		swtKeys.put("INHERIT_FORCE", SWT.INHERIT_FORCE);
		swtKeys.put("INHERIT_NONE", SWT.INHERIT_NONE);
		swtKeys.put("INSERT", SWT.INSERT);
		swtKeys.put("ITALIC", SWT.ITALIC);
		swtKeys.put("JOIN_BEVEL", SWT.JOIN_BEVEL);
		swtKeys.put("JOIN_MITER", SWT.JOIN_MITER);
		swtKeys.put("JOIN_ROUND", SWT.JOIN_ROUND);
		swtKeys.put("KEY_MASK", SWT.KEY_MASK);
		swtKeys.put("KEYCODE_BIT", SWT.KEYCODE_BIT);
		swtKeys.put("KeyDown", SWT.KeyDown);
		swtKeys.put("KEYPAD_0", SWT.KEYPAD_0);
		swtKeys.put("KEYPAD_1", SWT.KEYPAD_1);
		swtKeys.put("KEYPAD_2", SWT.KEYPAD_2);
		swtKeys.put("KEYPAD_3", SWT.KEYPAD_3);
		swtKeys.put("KEYPAD_4", SWT.KEYPAD_4);
		swtKeys.put("KEYPAD_5", SWT.KEYPAD_5);
		swtKeys.put("KEYPAD_6", SWT.KEYPAD_6);
		swtKeys.put("KEYPAD_7", SWT.KEYPAD_7);
		swtKeys.put("KEYPAD_8", SWT.KEYPAD_8);
		swtKeys.put("KEYPAD_9", SWT.KEYPAD_9);
		swtKeys.put("KEYPAD_ADD", SWT.KEYPAD_ADD);
		swtKeys.put("KEYPAD_CR", SWT.KEYPAD_CR);
		swtKeys.put("KEYPAD_DECIMAL", SWT.KEYPAD_DECIMAL);
		swtKeys.put("KEYPAD_DIVIDE", SWT.KEYPAD_DIVIDE);
		swtKeys.put("KEYPAD_EQUAL", SWT.KEYPAD_EQUAL);
		swtKeys.put("KEYPAD_MULTIPLY", SWT.KEYPAD_MULTIPLY);
		swtKeys.put("KEYPAD_SUBTRACT", SWT.KEYPAD_SUBTRACT);
		swtKeys.put("KeyUp", SWT.KeyUp);
		swtKeys.put("LAST_LINE_SELECTION", SWT.LAST_LINE_SELECTION);
		swtKeys.put("LEAD", SWT.LEAD);
		swtKeys.put("LEFT", SWT.LEFT);
		swtKeys.put("LEFT_TO_RIGHT", SWT.LEFT_TO_RIGHT);
		swtKeys.put("LF", (int) SWT.LF);
		swtKeys.put("LINE_CUSTOM", SWT.LINE_CUSTOM);
		swtKeys.put("LINE_DASH", SWT.LINE_DASH);
		swtKeys.put("LINE_DASHDOT", SWT.LINE_DASHDOT);
		swtKeys.put("LINE_DASHDOTDOT", SWT.LINE_DASHDOTDOT);
		swtKeys.put("LINE_DOT", SWT.LINE_DOT);
		swtKeys.put("LINE_SOLID", SWT.LINE_SOLID);
		swtKeys.put("LONG", SWT.LONG);
		swtKeys.put("LOW", SWT.LOW);
		swtKeys.put("MAX", SWT.MAX);
		swtKeys.put("MeasureItem", SWT.MeasureItem);
		swtKeys.put("MEDIUM", SWT.MEDIUM);
		swtKeys.put("MENU", SWT.MENU);
		swtKeys.put("MenuDetect", SWT.MenuDetect);
		swtKeys.put("MIN", SWT.MIN);
		swtKeys.put("MIRRORED", SWT.MIRRORED);
		swtKeys.put("MOD1", SWT.MOD1);
		swtKeys.put("MOD2", SWT.MOD2);
		swtKeys.put("MOD3", SWT.MOD3);
		swtKeys.put("MOD4", SWT.MOD4);
		swtKeys.put("MODELESS", SWT.MODELESS);
		swtKeys.put("MODIFIER_MASK", SWT.MODIFIER_MASK);
		swtKeys.put("Modify", SWT.Modify);
		swtKeys.put("MouseDoubleClick", SWT.MouseDoubleClick);
		swtKeys.put("MouseDown", SWT.MouseDown);
		swtKeys.put("MouseEnter", SWT.MouseEnter);
		swtKeys.put("MouseExit", SWT.MouseExit);
		swtKeys.put("MouseHover", SWT.MouseHover);
		swtKeys.put("MouseMove", SWT.MouseMove);
		swtKeys.put("MouseUp", SWT.MouseUp);
		swtKeys.put("MouseWheel", SWT.MouseWheel);
		swtKeys.put("Move", SWT.Move);
		swtKeys.put("MOVEMENT_CHAR", SWT.MOVEMENT_CHAR);
		swtKeys.put("MOVEMENT_CLUSTER", SWT.MOVEMENT_CLUSTER);
		swtKeys.put("MOVEMENT_WORD", SWT.MOVEMENT_WORD);
		swtKeys.put("MOVEMENT_WORD_END", SWT.MOVEMENT_WORD_END);
		swtKeys.put("MOVEMENT_WORD_START", SWT.MOVEMENT_WORD_START);
		swtKeys.put("MOZILLA", SWT.MOZILLA);
		swtKeys.put("MULTI", SWT.MULTI);
		swtKeys.put("NATIVE", SWT.NATIVE);
		swtKeys.put("NO", SWT.NO);
		swtKeys.put("NO_BACKGROUND", SWT.NO_BACKGROUND);
		swtKeys.put("NO_FOCUS", SWT.NO_FOCUS);
		swtKeys.put("NO_MERGE_PAINTS", SWT.NO_MERGE_PAINTS);
		swtKeys.put("NO_RADIO_GROUP", SWT.NO_RADIO_GROUP);
		swtKeys.put("NO_REDRAW_RESIZE", SWT.NO_REDRAW_RESIZE);
		swtKeys.put("NO_SCROLL", SWT.NO_SCROLL);
		swtKeys.put("NO_TRIM", SWT.NO_TRIM);
		swtKeys.put("None", SWT.None);
		swtKeys.put("NONE", SWT.NONE);
		swtKeys.put("NORMAL", SWT.NORMAL);
		swtKeys.put("NULL", SWT.NULL);
		swtKeys.put("NUM_LOCK", SWT.NUM_LOCK);
		swtKeys.put("OFF", SWT.OFF);
		swtKeys.put("OK", SWT.OK);
		swtKeys.put("ON", SWT.ON);
		swtKeys.put("ON_TOP", SWT.ON_TOP);
		swtKeys.put("OPEN", SWT.OPEN);
		swtKeys.put("PAGE_DOWN", SWT.PAGE_DOWN);
		swtKeys.put("PAGE_UP", SWT.PAGE_UP);
		swtKeys.put("Paint", SWT.Paint);
		swtKeys.put("PaintItem", SWT.PaintItem);
		swtKeys.put("PASSWORD", SWT.PASSWORD);
		swtKeys.put("PATH_CLOSE", SWT.PATH_CLOSE);
		swtKeys.put("PATH_CUBIC_TO", SWT.PATH_CUBIC_TO);
		swtKeys.put("PATH_LINE_TO", SWT.PATH_LINE_TO);
		swtKeys.put("PATH_MOVE_TO", SWT.PATH_MOVE_TO);
		swtKeys.put("PATH_QUAD_TO", SWT.PATH_QUAD_TO);
		swtKeys.put("PAUSE", SWT.PAUSE);
		swtKeys.put("PAUSED", SWT.PAUSED);
		swtKeys.put("PHONETIC", SWT.PHONETIC);
		swtKeys.put("POP_UP", SWT.POP_UP);
		swtKeys.put("PRIMARY_MODAL", SWT.PRIMARY_MODAL);
		swtKeys.put("PRINT_SCREEN", SWT.PRINT_SCREEN);
		swtKeys.put("PUSH", SWT.PUSH);
		swtKeys.put("RADIO", SWT.RADIO);
		swtKeys.put("READ_ONLY", SWT.READ_ONLY);
		swtKeys.put("Resize", SWT.Resize);
		swtKeys.put("RESIZE", SWT.RESIZE);
		swtKeys.put("RETRY", SWT.RETRY);
		swtKeys.put("RIGHT", SWT.RIGHT);
		swtKeys.put("RIGHT_TO_LEFT", SWT.RIGHT_TO_LEFT);
		swtKeys.put("ROMAN", SWT.ROMAN);
		swtKeys.put("SAVE", SWT.SAVE);
		swtKeys.put("SCROLL_LINE", SWT.SCROLL_LINE);
		swtKeys.put("SCROLL_LOCK", SWT.SCROLL_LOCK);
		swtKeys.put("SCROLL_PAGE", SWT.SCROLL_PAGE);
		swtKeys.put("SEARCH", SWT.SEARCH);
		swtKeys.put("SELECTED", SWT.SELECTED);
		swtKeys.put("Selection", SWT.Selection);
		swtKeys.put("SEPARATOR", SWT.SEPARATOR);
		swtKeys.put("SetData", SWT.SetData);
		swtKeys.put("Settings", SWT.Settings);
		swtKeys.put("SHADOW_ETCHED_IN", SWT.SHADOW_ETCHED_IN);
		swtKeys.put("SHADOW_ETCHED_OUT", SWT.SHADOW_ETCHED_OUT);
		swtKeys.put("SHADOW_IN", SWT.SHADOW_IN);
		swtKeys.put("SHADOW_NONE", SWT.SHADOW_NONE);
		swtKeys.put("SHADOW_OUT", SWT.SHADOW_OUT);
		swtKeys.put("SHELL_TRIM", SWT.SHELL_TRIM);
		swtKeys.put("SHIFT", SWT.SHIFT);
		swtKeys.put("SHORT", SWT.SHORT);
		swtKeys.put("Show", SWT.Show);
		swtKeys.put("SIMPLE", SWT.SIMPLE);
		swtKeys.put("SINGLE", SWT.SINGLE);
		swtKeys.put("SMOOTH", SWT.SMOOTH);
		swtKeys.put("SYSTEM_MODAL", SWT.SYSTEM_MODAL);
		swtKeys.put("TAB", (int) SWT.TAB);
		swtKeys.put("TIME", SWT.TIME);
		swtKeys.put("TITLE", SWT.TITLE);
		swtKeys.put("TOGGLE", SWT.TOGGLE);
		swtKeys.put("TOOL", SWT.TOOL);
		swtKeys.put("TOP", SWT.TOP);
		swtKeys.put("TRAIL", SWT.TRAIL);
		swtKeys.put("TRANSPARENCY_ALPHA", SWT.TRANSPARENCY_ALPHA);
		swtKeys.put("TRANSPARENCY_MASK", SWT.TRANSPARENCY_MASK);
		swtKeys.put("TRANSPARENCY_NONE", SWT.TRANSPARENCY_NONE);
		swtKeys.put("TRANSPARENCY_PIXEL", SWT.TRANSPARENCY_PIXEL);
		swtKeys.put("TRANSPARENT", SWT.TRANSPARENT);
		swtKeys.put("Traverse", SWT.Traverse);
		swtKeys.put("TRAVERSE_ARROW_NEXT", SWT.TRAVERSE_ARROW_NEXT);
		swtKeys.put("TRAVERSE_ARROW_PREVIOUS", SWT.TRAVERSE_ARROW_PREVIOUS);
		swtKeys.put("TRAVERSE_ESCAPE", SWT.TRAVERSE_ESCAPE);
		swtKeys.put("TRAVERSE_MNEMONIC", SWT.TRAVERSE_MNEMONIC);
		swtKeys.put("TRAVERSE_NONE", SWT.TRAVERSE_NONE);
		swtKeys.put("TRAVERSE_PAGE_NEXT", SWT.TRAVERSE_PAGE_NEXT);
		swtKeys.put("TRAVERSE_PAGE_PREVIOUS", SWT.TRAVERSE_PAGE_PREVIOUS);
		swtKeys.put("TRAVERSE_RETURN", SWT.TRAVERSE_RETURN);
		swtKeys.put("TRAVERSE_TAB_NEXT", SWT.TRAVERSE_TAB_NEXT);
		swtKeys.put("TRAVERSE_TAB_PREVIOUS", SWT.TRAVERSE_TAB_PREVIOUS);
		swtKeys.put("UNDERLINE_DOUBLE", SWT.UNDERLINE_DOUBLE);
		swtKeys.put("UNDERLINE_ERROR", SWT.UNDERLINE_ERROR);
		swtKeys.put("UNDERLINE_SINGLE", SWT.UNDERLINE_SINGLE);
		swtKeys.put("UNDERLINE_SQUIGGLE", SWT.UNDERLINE_SQUIGGLE);
		swtKeys.put("UP", SWT.UP);
		swtKeys.put("V_SCROLL", SWT.V_SCROLL);
		swtKeys.put("Verify", SWT.Verify);
		swtKeys.put("VERTICAL", SWT.VERTICAL);
		swtKeys.put("VIRTUAL", SWT.VIRTUAL);
		swtKeys.put("WRAP", SWT.WRAP);
		swtKeys.put("YES", SWT.YES);
	}
	
	public static int getSWT(String name){
		Integer v = swtKeys.get(name);
		if(v != null){
			return v.intValue();
		}else{
			return SWT.NONE;
		}
	}
	
	public static Set<String> getSWTKeys(){
		return swtKeys.keySet();
	}
	
	public static void addDisposeViewer(Widget parent, final String name){
		parent.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent event) {
				log.info(name + ":" + event.widget + " is disposed!");
			}
			
		});
	}
	
	public static void addDisposeData(Widget parent, String name, Widget widget){
		ResourceDisposeListener listener = (ResourceDisposeListener) parent.getData("_disposer");
		if(listener == null){
			listener = new ResourceDisposeListener();
			parent.setData("_disposer", listener);			
		}
		listener.add(name, widget);
	}
	
	/**
	 * 根据一个控件的位置和大小设置弹出窗口的位置。
	 * 
	 * @param shell 要弹出的窗口
	 * @param relateLocation 控件的location
	 * @param relateSize 控件的大小
	 */
	public static void setShellRelateLocation(Shell shell, Point relateLocation, Point relateSize){
    	Point point = relateLocation;

    	Rectangle shellRect = shell.getBounds();
    	Display display = shell.getDisplay();
    	//Monitor monitor = shell.getMonitor();
    	int clientHeight = display.getClientArea().height;
    	int clientWidth = display.getClientArea().width;
    	int width = shellRect.width;
    	int height = shellRect.height;
    	Point x = getRelateWidth(width, clientWidth, point.x, point.x + relateSize.x);
    	Point y = getRelateHeight(height, clientHeight, point.y, point.y + relateSize.y);
    	shell.setLocation(x.y, y.y);
    	shell.setSize(x.x, y.x);    	
	}
	
	public static Point getRelateWidth(int width, int clientWidth, int x1, int x2){
		int w1 = clientWidth - width - x1;
		int w2 = x2 - width;
	    if(w1 >= 0){
	    	return new Point(width, x1);
	    }else if(w2 >= 0){
	    	return new Point(width, x2 - width);
	    }else if(w1 > w2){
	    	return new Point(width + w1, x1);
	    }else{
	    	return new Point(width + w2, 0);
	    }		
	}
	
	public static Point getRelateHeight(int height, int clientHeight, int y1 , int y2){
		int w1 = clientHeight - y2 - height;
	    int w2 = y1 - height;
	    if(w1 >= 0){
	    	//下面可以显示
	    	return new Point(height, y2);
	    }else if(w2 >= 0){
	    	//上面可以显示
	    	return new Point(height, y1 - height);
	    }else if(w1 > w2){
	    	//下面优先显示
	    	return new Point(height + w1, y2);
	    }else{
	    	//上面优先显示
	    	return new Point(height + w2, 0);
	    }
	}
	
	/**
	 * 居中一个Shell。
	 * 
	 * @param shell
	 */
	public static void centerShell(Shell shell){
		Display display = Display.getCurrent();
		Rectangle rec = display.getClientArea();
		Rectangle srec = shell.getBounds();
		int x = rec.width / 2 - srec.width / 2;
		int y = rec.height / 2 - srec.height / 2;
		shell.setLocation (new Point(x, y));
	}
	
	public static Dialog createDialog(final Shell shell, final ActionContext actionContext){
		return new Dialog(shell){		
			@SuppressWarnings("unused")
			public Object open(){
				shell.open();
				
				final Display display = shell.getDisplay();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) display.sleep();
				}
				
				return actionContext.get("result");
			}
		};
	}
	/**
	 * 获得加速键，如Shift+X.
	 * 
	 * @param acceleratorStr
	 * @return
	 */
	public static int getAccelerator(String acceleratorStr){
		if(acceleratorStr == null || "".equals(acceleratorStr)) return 0;
		
		String[] s = acceleratorStr.split("[+]");
		int value = 0;
		for(int i=0; i<s.length; i++){
			value = value | getKey(s[i]);
		}
		
		return value;
	}
	
	public static int getKey(String key){
		key = key.toUpperCase().trim();
		
		int v = getSWT(key);
		if(v != 0){
			return v;
		}
		
		if(key.length() == 1){
			return (char) key.getBytes()[0];
		}else{
			return 0;
		}
	}
	
	/**
	 * 以对话框的方式打开一个Shell，直到shell关闭为止。
	 * 
	 * 注意RWT下返回为null，要监听返回值，使用带SwtDialogCallback参数的方法。
	 * 
	 * @param shell
	 * @param actionContext
	 * @return
	 */
	public static Object openAsDialog(Shell shell, ActionContext actionContext){
		SwtDialog swtDialog = new SwtDialog(shell, actionContext);
		return swtDialog.open();
		/*
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		return actionContext.get("result");*/		
	}
	
	/**
	 * 以对话框的方式打开一个Shell。
	 * 
	 * @param shell
	 * @param actionContext
	 * @param callback
	 */
	public static void openAsDialog(Shell shell, ActionContext actionContext, SwtDialogCallback callback){
		SwtDialog swtDialog = new SwtDialog(shell, actionContext);
		swtDialog.open(callback);
	}
	
	/**
	 * 动态读取一个输出流的内容到一个Text。
	 * 
	 * @param in
	 * @param text
	 */
	public static void readStreamToText(final InputStream in, final Text text){
		Thread th = new Thread(new Runnable(){
			public void run() {
				Display display = text.getDisplay();
				display.asyncExec(new Runnable(){
					public void run(){						
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						String line = null;
						try {
							while((line = reader.readLine()) != null){
								text.append(line + "\n");
							}
						} catch (IOException e) {
							text.append(e.getLocalizedMessage());
						}
					}
				});
			}			
		});
		
		th.start();
	}
	
	/**
	 * 在指定的widget的位置打开菜单。
	 * 
	 * @param widget
	 * @param menu
	 */
	public static void showMenuByWidget(Widget widget, Menu menu) {
		Rectangle rect = MenuActions.getBounds(widget);
		
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = MenuActions.getParent(widget).toDisplay(pt);

		if(menu != null){
		    menu.setLocation(pt.x, pt.y);
		    //menu.update();
		    menu.setVisible(true);
		}
	}
	
	/**
	 * 取字符串数组。
	 * 
	 * @param value
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static String[] getStringArray(String value, ActionContext actionContext) throws OgnlException{
		if(value == null || "".equals(value.trim())){
			return new String[0];
		}
		
		if(value.startsWith("[")){
			//脚本定义的字符串数组
			return (String[]) OgnlUtil.getValue(value, actionContext);
		}else{
			return (String[]) actionContext.get(value);			
		}
	}
		
	public static Object getObject(String value, ActionContext actionContext){
		if(value == null || "".equals(value)){
			return null;
		}
		
		String v = value;
		boolean constant = false;
		if(v.startsWith("\"")){
			//按常量处理
			constant = true;
			//去第一个"
			v = v.substring(1, v.length());
		}
		
		if(v.endsWith("\"")){
			//去最后一个"
			v = v.substring(0, v.length() - 1);
		}
		
		if(constant){
			return v;
		}else{
			return actionContext.get(v);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void setValue(Object value, Object control, String pattern) throws ParseException{
		if(control == null) {
			return;
		}
		
		Model model = ModelManager.getModel(control.getClass());
		if(model != null) {
			model.setValue(control, value, pattern, pattern);
			return;
		}
		
		if(control instanceof Text){
			Text t = (Text) control;
	        t.setText(UtilData.format(value, pattern));
	    }else if(StyledTextProxy.isStyledText(control)){	    	
	    	StyledTextProxy.setText(control, UtilData.format(value, pattern));
	    }else if(control instanceof Label){
	    	Label l = (Label) control;
	    	l.setText(UtilData.format(value, pattern));
	        l.pack();
	    }else if(control instanceof Button){
	    	Button b = (Button) control;
	    	
	    	if((b.getStyle() & SWT.RADIO) != 0){
	    		//是radio
	    	}else if((b.getStyle() & SWT.CHECK) != 0){
	    		//是checkBox
	    		if(value instanceof Boolean){
	    			Boolean bv = (Boolean) value;
	    			if(bv.booleanValue()){
	    				b.setSelection(true);
	    			}else{
	    				b.setSelection(false);
	    			}
	    		}else if(value instanceof String){
	    			String sv = (String) value;
	    			
	    			if("TRUE".equals(sv.toUpperCase()) || "1".equals(sv)){
	    				b.setSelection(true);
	    			}else{
	    				b.setSelection(false);
	    			}	    			
	    		}else if(value != null){
	    			b.setSelection(true);
	    		}else{
	    			b.setSelection(false);
	    		}
	    	}else{
	    		b.setText(UtilData.format(value, pattern));
	    	}
	    }else if(control instanceof DateTime){
	    	Date dateValue;
	        if(value instanceof Date){
	            dateValue = (Date) value;
			}else if(value == null){
				dateValue = new Date(0);
			}else{
	            dateValue = (Date) UtilData.parse(value.toString(), "Date", pattern);
	        }

	        DateTime dt = (DateTime) control;
	        GregorianCalendar c = new GregorianCalendar();
	        c.setTime(dateValue);
		    dt.setYear(c.get(Calendar.YEAR));
		    dt.setMonth(c.get(Calendar.MONTH));
		    dt.setDay(c.get(Calendar.DAY_OF_MONTH));
	    }else if(control instanceof Thing){
	    	Thing dataControl = (Thing) control;
	    	Map<String, Object> data = new HashMap<String, Object>();
	    	data.put("value", value);
	    	dataControl.doAction("setValue", new ActionContext(), data);
	    }else if(control instanceof Combo){
	    	Combo combo = (Combo) control;
	    	if(combo.getData() instanceof List){
	    		List datas = (List) combo.getData();
	    		for(int i=0; i<datas.size(); i++){
	    			if(value != null && value.equals(datas.get(i))){
	    				combo.select(i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    	if(value != null){
	    		combo.setText(value.toString());
	    	}
	    	//combo.setText(value);
	    
	    }else if(control instanceof CCombo){
	    	CCombo combo = (CCombo) control;
	    	if(combo.getData() instanceof List){
	    		List datas = (List) combo.getData();
	    		for(int i=0; i<datas.size(); i++){
	    			if(value != null && value.equals(datas.get(i))){
	    				combo.select(i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    	if(value != null){
	    		combo.setText(value.toString());
	    	}
	    	//combo.setText(value);
	    }else if(control instanceof Composite){
	    	//是readio或者checkBox选择
	    	Composite composite = (Composite) control;
	    	Control[] children = composite.getChildren();
	    	String vs[] = null;
	    	if(value instanceof String){
	    		vs = ((String) value).split("[,]");
	    	}
	    	
	    	if(vs == null){
		    	for(int i=0; i<children.length; i++){
		    		if(children[i].getData() != null && children[i].getData().equals(value) && children[i] instanceof Button){
		    			((Button) children[i]).setSelection(true);
		    		}
		    	}
	    	}else{
	    		for(int i=0; i<children.length; i++){
	    			for(int n=0; n<vs.length; n++){
			    		if(children[i].getData() != null && children[i].getData().equals(vs[n]) && children[i] instanceof Button){
			    			((Button) children[i]).setSelection(true);
			    		}	
	    			}
		    	}
	    	}
	    }
	}
	
	/**
	 * 从SWT界面上取数据。
	 * 
	 * @param control
	 * @param type
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("rawtypes")
	public static Object getValue(Object control, String type, String pattern) throws ParseException{
		if(control == null) {
			return null;
		}
		
		
		if(type == null){
			type = "string";
		}
		
		Model model = ModelManager.getModel(control.getClass());
		if(model != null) {
			Object value = model.getValue(control, type, pattern);
			return value;
		}
		
		type = type.toLowerCase();
		if(control instanceof Thing){
			Thing thing = (Thing) control;
			return thing.doAction("getValue");
		}else if(control instanceof ActionContainer){
			ActionContainer ac = (ActionContainer) control;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", type);
			params.put("pattern", pattern);
			return ac.doAction("getValue", params);
		}
		
		if(!(control instanceof Widget)){
			//如果control不是swt控件，那么可能就是数据本身，直接返回
			return control;
		}
		
		if(control instanceof Text || StyledTextProxy.isStyledText(control)){
			if(control instanceof Text){
				Text t = (Text) control;
				return UtilData.parse(t.getText().trim(), type, pattern);
			}else if(StyledTextProxy.isStyledText(control)){
				return UtilData.parse(StyledTextProxy.getText(control).trim(), type, pattern);
			}
	    }else if(control instanceof Label){
	    	Label l = (Label) control;
	    	return UtilData.parse(l.getText().trim(), type, pattern);
	    }else if(control instanceof Button){
	    	Button b = (Button) control;
	    	if(b.getData("selected") != null && b.getSelection()){
	    		return b.getData("selected");
	    	}
	    	
	    	if(b.getData("unSelected") != null && !b.getSelection()){
	    		return b.getData("unSelected");
	    	}
	    	if("boolean".equals(type)){
	    		if(b.getSelection()){
	    			return true;
	    		}else{
	    			return false;
	    		}
	    	}else{
	    		if(b.getSelection()){
	    			return b.getData();
	    		}else{
	    			return null;
	    		}
	    	}	    	
	    }else if(control instanceof DateTime){
	        DateTime d = (DateTime) control;
	        return new GregorianCalendar(d.getYear(), d.getMonth(), d.getDay()).getTime();
	        //String time = d.getYear() + "-" + (d.getMonth()+1) + "-" + d.getDay();
	        //return UtilData.parse(time, type, pattern);	        
	    }else if(control instanceof CCombo || control instanceof Combo){
	    	//下拉选择
	    	if(control instanceof CCombo){
	    		CCombo combo = (CCombo) control;
	    		/*
	    		if((combo.getStyle() & SWT.READ_ONLY) == 0){
	    			//如果是可以修改内容的，那么返回的应该是字符串
	    			return combo.getText();
	    		}else{
	    		*/
    			int index = combo.getSelectionIndex();
    			if(index != -1){
    				Object data = combo.getData();    				
    				if(!(data instanceof List)){
    					return combo.getText() ;
    				}else{
    					List datas = (List) data;
    					if(index < datas.size()){
    						return datas.get(index);
    					}else{
    						return combo.getText();
    					}
    				}
    			}else{
    				return combo.getText();
    			}
	    	}else if(control instanceof Combo){
	    		Combo combo = (Combo) control;	    		
	    		if((combo.getStyle() & SWT.READ_ONLY) == 0){
	    			//如果是可以修改内容的，那么返回的应该是字符串
	    			return combo.getText();
	    		}else{
	    			int index = combo.getSelectionIndex();
	    			if(index != -1){
	    				Object data = combo.getData();
	    				if(!(data instanceof List)){
	    					return combo.getText();
	    				}else{
	    					List datas = (List) data;
	    					if(index < datas.size()){
	    						return datas.get(index);
	    					}else{
	    						return combo.getText();
	    					}
	    				}
	    			}else{
	    				return combo.getText();
	    			}
	    		}
	    	}
	    }else if(control instanceof Composite){
	    	//如果控件是Composite，那么可能是单项选择或者多项选择，此时composite下都应该是Button
	    	Composite composite = (Composite) control;
	    	Control[] controls = composite.getChildren();
	    	List<Button> selectedControls = new ArrayList<Button>();
	    	for(int i=0; i<controls.length; i++){
	    		if(controls[i] instanceof Button){
	    			Button b = (Button) controls[i];
	    			if(b.getSelection()){
	    				selectedControls.add(b);
	    			}
	    		}
	    	}
	    	
	    	if("string".equals(type)){
	    		String value = "";
	    		for(Iterator<Button> iter = selectedControls.iterator(); iter.hasNext();){
	    			Button b = iter.next();
	    			if(!"".equals(value)){
	    				value = value + ",";	    				
	    			}
	    			value = value + b.getData().toString();
	    		}
	    		
	    		return value;
	    	}else if("list".equals(type)){
	    		List<Object> values = new ArrayList<Object>();
	    		for(Iterator<Button> iter = selectedControls.iterator(); iter.hasNext();){
	    			Button b = iter.next();
	    			values.add(b.getData());
	    		}
	    	}else{
	    		//取第一个值
	    		if(selectedControls.size() == 0){
	    			return null;
	    		}else{
	    			Button b = selectedControls.get(0);
	    			return b.getData();	    			
	    		}
	    	}
	    }
		
		return null;
	}
	
	public static boolean validate(Object control, String type, String pattern, boolean required){
		if(!(control instanceof Control)){
			//只是普通的数据
			return true;
		}
		
		if(control instanceof Text){
			Text t = (Text) control;
			return validateText(t.getText(), type, pattern, required);        
	    }else if(control instanceof Label){
	    	Label l = (Label) control;
	    	return validateText(l.getText(), type, pattern, required);   
	    }else if(control instanceof Button){
	    	Button b = (Button) control;
	    	return validateText(b.getText(), type, pattern, required);   
	    }else if(control instanceof DateTime){
	        return true;    
	    }else if(control instanceof Thing){
	    	Thing dataControl = (Thing) control;
	    	try{
	    		Object result = dataControl.doAction("validate");
	    		if(result instanceof String){
	    			if("success".equals(result))
	    				return true;
	    			else
	    				return false;
	    		}else{
	    			return true;
	    		}
	    	}catch(Exception e){
	    		return false;
	    	}
	    }
		
		return true;
	}
	
	/**
	 * 把事物显示到一个tree上，同时树节点的setData设置的是事物本身。
	 * 
	 * @param thing 事物
	 * @param parent 树
 	 * @param actionContext 变量上下文
	 */
	public static void showThingOnTree(Thing thing, Tree parent, ActionContext actionContext){
		TreeItem item = new TreeItem(parent, SWT.NONE);
		item.setData(thing);
		XWorkerTreeUtil.initItem(item, thing, actionContext);
		
		for(Thing child : thing.getChilds()){
			showThingOnTreeItem(child, item, actionContext);
		}
	}
	
	/**
	 * 把事物显示到一个treeItem上，同时树节点的setData设置的是事物本身。
	 * 
	 * @param thing 事物
	 * @param parent 树节点
	 * @param actionContext 变量上下文
 	 */
	public static void showThingOnTreeItem(Thing thing, TreeItem parent, ActionContext actionContext){
		TreeItem item = new TreeItem(parent, SWT.NONE);
		item.setData(thing);
		XWorkerTreeUtil.initItem(item, thing, actionContext);
		
		for(Thing child : thing.getChilds()){
			showThingOnTreeItem(child, item, actionContext);
		}
	}
	
	/**
	 * 返回SWT运行的环境，返回DEFAULT或RAP。
	 * @return
	 */
	public static String getEnviroment() {
		if(isRWT()) {
			return "RAP";
		}else {
			return "DEFAULT";
		}
	}
	
	private static boolean validateText(String text, String type, String pattern, boolean required){	
		if(text == null || "".equals(text.trim())){
			if(required){
				return false;
			}else{
				return true;
			}
		}
	
		try{
			NumberFormat nf = NumberFormat.getInstance();
			if("Integer".equals(type)){				 
				//if(NumberFormat.getIntegerValidator.getInstance().validate(text, pattern) != null){
				nf.parse(text);
			}
			
			if("Long".equals(type)){
				nf.parse(text);
			}
			
			if("Double".equals(type)){
				nf.parse(text);
			}
			
			if("Float".equals(type)){
				nf.parse(text);
			}
			
			if("Date".equals(type)){
				SimpleDateFormat.getInstance().parse(text);
			}
			
			if("Time".equals(type)){
				SimpleDateFormat.getTimeInstance().parse(text);
			}
			
			if("BigDecimal".equals(type)){
				nf.parse(text);
			}
			
			if("BigInteger".equals(type)){
				nf.parse(text);
			}
			
			if("Byte".equals(type)){
				nf.parse(text);
			}
			
			if("Short".equals(type)){
				nf.parse(text);
			}
			
			UtilData.parse(text, type, pattern);	
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 向浏览器中设置事物的文档。
	 * 
	 * @param path
	 * @param browser
	 */
	public static void setThingDesc(String path, Browser browser) {
		setThingDesc(World.getInstance().getThing(path), browser);
	}
	
	/**
	 * 向浏览器中设置事物的文档。
	 * 
	 * @param thing
	 * @param browser
	 */
	public static void setThingDesc(Thing thing, Browser browser) {
		if(thing == null) {
			browser.setText("");
			return;
		}
		
		if(XWorkerUtils.hasXWorker()) {
			browser.setUrl(XWorkerUtils.getThingDescUrl(thing));
		}else {
			Thing realThing = thing;
			while(true){
			    String description = realThing.getStringBlankAsNull("description");
			    if((description == null || "".equals(description.trim())) && realThing.getBoolean("inheritDescription")){
			    	//是否是继承描述
			    	List<Thing> extendsThing = realThing.getExtends();
			    	if(extendsThing.size() > 0){
			    		realThing = extendsThing.get(0);
			    	}else{
			    	    break;
			    	}
				}else{
				    break;
				}
			}
			
			String description = realThing.getStringBlankAsNull("description");
			if(description != null) {
				browser.setText(description);
			}else {
				browser.setText("");
			}
		}
	}
	
	/**
	 * 通过names对应关系从value赋值到target上。
	 * 
	 * @param value
	 * @param names
	 * @param actionContext
	 */
	public static void setValues(Object value, String[][] names, ActionContext actionContext){
		for(int i=0; i<names.length; i++){
			Object v = OgnlUtil.getValue(names[i][0], value);
			if(v == null) v = "";
			
			Object t = actionContext.get(names[i][1]);
			if(t instanceof Text){
				Text text = (Text) t;
				text.setText(v.toString());
			}else if(t instanceof Label){
				Label l = (Label) t;
				l.setText(v.toString());
				l.pack();
			}else if(t instanceof Button){
				Button b = (Button) t;
				b.setText(v.toString());
			}else if(t instanceof DateTime){
				DateTime d = (DateTime) t;
				if(v instanceof Date){
					Date dv = (Date) v;
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(dv);
					d.setYear(c.get(Calendar.YEAR));
					d.setMonth(c.get(Calendar.MONTH));
					d.setDay(c.get(Calendar.DAY_OF_MONTH));
				}
			}else if(! (t instanceof Control)){
				//target.setProperty(names[i][1], v);
				actionContext.put(names[i][1], v);
			}
			
		}
	}
	
	public static Object getValues(String className, String[][] names, ActionContext actionContext) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Object values = Class.forName(className).getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[] {});
		
		for(int i=0; i<names.length; i++){
			Object t = actionContext.get(names[i][1]);
			if(t instanceof Text){
				Text text = (Text) t;
				OgnlUtil.setValue(names[i][0], values, text.getText().trim());
			}else if(t instanceof Label){
				Label l = (Label) t;
				OgnlUtil.setValue(names[i][0], values, l.getText().trim());
			}else if(t instanceof Button){
				Button b = (Button) t;
				OgnlUtil.setValue(names[i][0], values, b.getText().trim());
			}else if(t instanceof DateTime){
				DateTime d = (DateTime) t;
				String time = d.getYear() + "-" + d.getMonth() + "-" + d.getDay();
			
				OgnlUtil.setValue(names[i][0], values, time);
			}else if(! (t instanceof Control)){
				OgnlUtil.setValue(names[i][0], values, t);
			}			
		}
		
		return values;
	}
	/**
	 * 从目标读取数据。
	 * 
	 * @param names
	 * @param actionContext
	 * @return
	 */
	public static Map<String, Object> getValues(String[][] names, ActionContext actionContext){
		Map<String, Object> values = new HashMap<String, Object>();
		for(int i=0; i<names.length; i++){
			Object t = actionContext.get(names[i][1]);
			if(t instanceof Text){
				Text text = (Text) t;
				values.put(names[i][0], text.getText().trim());					
			}else if(t instanceof Label){
				Label l = (Label) t;
				values.put(names[i][0], l.getText().trim());
			}else if(t instanceof Button){
				Button b = (Button) t;
				values.put(names[i][0], b.getText().trim());
			}else  if(t instanceof DateTime){
				DateTime d = (DateTime) t;
				String time = d.getYear() + "-" + (d.getMonth() + 1) + "-" + d.getDay();
			
				values.put(names[i][0], time);
			}else if(! (t instanceof Control)){
				values.put(names[i][0], t);
			}			
		}
		
		return values;
	}
	
	/**
	 * 把一个控件转化为Image。
	 * 
	 * @param control
	 * @return
	 */
	public static Image toImage(Control control){
		Image image = new Image(control.getDisplay(), control.getBounds());		
		GC gc = new GC(image);
		try{
			control.print(gc);
		}finally{
			gc.dispose();
		}
		return image;
	}
	
	public static Image createWaterMarker(Image image, Font font, int degree, int alpha, String text){
		Image newImage = new Image(image.getDevice(), (ImageData) image.getImageData().clone());
		GC gc = new GC(newImage);
		try{
			if(font != null){
				gc.setFont(font);
			}
			gc.setAlpha(alpha);
			
			if(degree != 0){
				Transform t = new Transform(newImage.getDevice());
				t.rotate(1f * degree);
				gc.setTransform(t);
			}
			int width = newImage.getBounds().width;
			int height = newImage.getBounds().height;
			int charWidth = 0;
			for(char c : text.toCharArray()){
				charWidth += gc.getCharWidth(c);
			}
			int charHeight = gc.getFontMetrics().getHeight();
			
			int widthSpace = 3;
			int hegithSpace = 5;
			int i = - 4 * width / (charWidth *  widthSpace);			
			while(true){
				int n = - 4 * height / (charHeight * hegithSpace);
				while(true){					
					gc.drawText(text, i * charWidth * widthSpace, n * charHeight * hegithSpace, true);
					
					n++;
					if(n * charHeight * hegithSpace > height * 4){
						break;
					}
				}
				i++;
				
				if(i * charWidth * widthSpace > width * 4){
					break;
				}
			}
			
		}finally{
			gc.dispose();
		}
		return newImage;
	}
	
	/**
	 * 把控件转化为JPEG图片。
	 * 
	 * @param control
	 * @param fileName
	 */
	public static void toImage(Control control, String fileName, int type){
		Image image = toImage(control);
		try {
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[]{image.getImageData()};
			loader.save(fileName, type);
		}finally {
			image.dispose();
		}
	}
	
	/**
	 * 兼容RWT的MessageBox的打开方法。
	 * 
	 * @param box 
	 * @param actionContext
	 * @param callBack 可以为null
	 */
	public static void showMessageBox(MessageBox box, ActionContext actionContext, DialogCallback callback) {
		SwtUtils.openDialog(box, callback, actionContext);
	}
	
	/**
	 * 把图片保存到文件中。
	 * 
	 * @param image
	 * @param file
	 */
	public static void saveImage(Image image, File file){
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		String ext = fileName.substring(index + 1, fileName.length()).trim().toLowerCase();
		int style = 0;
		if("bmp".equals(ext)){
			style = SWT.IMAGE_BMP;
		}else if("gif".equals(ext)){
			style = SWT.IMAGE_GIF;
		}else if("jpg".equals(ext) || "jpeg".equals(ext)){
			style = SWT.IMAGE_JPEG;
		}else if("ico".equals(ext)){
			style = SWT.IMAGE_ICO;
		}else if("png".equals(ext)){
			style = SWT.IMAGE_PNG;
		}else if("tiff".equals(ext) || "tif".equals(ext)){
			style = SWT.IMAGE_TIFF;
		}else{
			throw new ActionException("unsuport image type " + ext);
		}
		
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[]{image.getImageData()};
		loader.save(file.getAbsolutePath(), style);
	}
	
	public static String saveImageToWebroot(String controlPath){
		Thing control = World.getInstance().getThing(controlPath);
		Thing shellThing = ControlActions.getShellThing(control);
		if(shellThing == null){
        	//创建一个临时的
        	Thing thing = new Thing("xworker.swt.widgets.Shell");
        	thing.set("width", "640");
        	thing.set("height", "480");
        	thing.set("name", "__shell__");
        	thing.getMetadata().setPath(control.getMetadata().getPath());
        	Thing layout = new Thing("xworker.swt.layout.FillLayout");
        	thing.addChild(layout);
        	thing.getMetadata().setLastModified(control.getMetadata().getLastModified());
        	thing.addChild(control, false);
        	        	
        	shellThing = thing;
        	//System.out.println("Lastmodified=" + thing.getMetadata().getLastModified() + ":" + control.getMetadata().getLastModified());
        }
		
		return saveImageToWebroot(shellThing, control.getMetadata().getName());
	}
	
	public static String saveImageToWebroot(Thing shellThing, String controlName){
		
		String url = "temp/" + 
				shellThing.getMetadata().getPath() + "." + controlName;
		url = url.replace('.', '/');
		String path = World.getInstance().getPath() + "/webroot/" + url + ".png";
		
		boolean modified = true;
		File file = new File(path);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}else{
			if(file.lastModified() >= shellThing.getMetadata().getLastModified()){
				modified = false;
			}
		}
		
		//System.out.println("Modified=" + modified);
		if(modified){
			//创建shell
			ActionContext ac = new ActionContext();		
			Display display = Display.getCurrent();
			boolean displayDispose = false;
			if(display == null){
				display = new Display();
				displayDispose = true;
			}
			ac.put("parent", display);
			Shell shell = (Shell) shellThing.doAction("create", ac);	
			shell.layout();
			try{
				Control control = null;
				control = (Control) OgnlUtil.getValue(controlName, ac);					
				if(control != null){
					long time = System.currentTimeMillis();
					while (!shell.isDisposed ()) {
				        if (!display.readAndDispatch ()){
				        	try{
				        		Thread.sleep(300);
				        	}catch(Exception e){				        		
				        	}
				        	//display.sleep ();
				        }
				        if(System.currentTimeMillis() - time > 5000){
				        	break;
				        }
				    }
					toImage(control, path, SWT.IMAGE_PNG);
					
					url = XWorkerUtils.getWebUrl() + url + ".png";
				}
			}finally{
				shell.dispose();
				
				if(displayDispose){
					display.dispose();
				}
			}
		}
		
		return path;
	}
	
	/**
	 * 保存控件图片到webroot的temp目录下，并返回图片的文件。
	 * 
	 * @param shellPath
	 * @param controlName
	 * @return
	 */
	public static String saveImageToWebroot(String shellPath, String controlName){
		//获取shell事物
		World world = World.getInstance();
		Thing shellThing = world.getThing(shellPath);
		return saveImageToWebroot(shellThing, controlName);
	}
	
	/**
	 * 打开一个文本输入窗口。
	 * 
	 * @param parent 父Shell
	 * @param title 标题
	 * @param message 要显示的消息
	 * @param text 初始化的文本内容
	 * @return 输入的文本，如果选择取消返回null
	 */
	public static String openInputDialog(Shell parent, String title, String message, String text){
		ActionContext ac = new ActionContext();
		ac.put("parent", parent);
		ac.put("title", title);
		ac.put("message", message);
		ac.put("value", text);
		
		Thing thing = World.getInstance().getThing("xworker.swt.util.InputDialog");
		Shell shell = (Shell) thing.doAction("create", ac);
		
		InputDialogActions.inputInit(ac);
		
		return (String) SwtDialog.open(shell, ac);
	}
	
	/**
	 * 打开一个事物表单对话框，返回编辑后的值。
	 * 
	 * @param parent
	 * @param title
	 * @param formThing
	 * @param values
	 * @return
	 */
	public static Map<String, Object> openThingFormDialog(Shell parent, String title, Thing formThing, Map<String,Object> values){
		Thing thing = new Thing(formThing.getMetadata().getPath());
		if(values != null){
			thing.getAttributes().putAll(values);
		}
		
		return openThingFormDialog(parent, title, thing);
	}	
	
	/**
	 * 打开一个事物表单对话框，返回编辑后的值。
	 * 
	 * @param parent
	 * @param title
	 * @param thing
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> openThingFormDialog(Shell parent, String title, Thing thing){
		ActionContext ac = new ActionContext();
		ac.put("parent", parent);
		ac.put("title", title);
		ac.put("thing", thing);
		
		Thing dialogThing = World.getInstance().getThing("xworker.swt.xworker.dialogs.ThingFormDialog");
		Shell shell = (Shell) dialogThing.doAction("create", ac);
		
		return (Map<String, Object> ) SwtDialog.open(shell, ac);
	}
	
	/**
	 * 过滤事物的LayoutData，如果事物包含LayoutData切和父容器的不一致，那么就会移除，否则保留。
	 * 
	 * @param parent
	 * @param thing
	 * 
	 * @return 如果有移除的返回true，否则返回false
	 */
	public static boolean trimLayoutData(Composite parent, Thing thing){
		Layout layout = parent.getLayout();
		if(layout == null){
			return false;
		}
		
		String layoutName = layout.getClass().getSimpleName();
		String dataName = null;
		if("FormLayout".equals(layoutName)){
			dataName = "FormData";
		}else if("GridLayout".equals(layoutName)){
			dataName = "GridData";
		}else if("RowLayout".equals(layoutName)){
			dataName = "RowData";
		}
		boolean r = false;
		List<Thing> datas = thing.getChilds("LayoutData");
		for(Thing data : datas){
			if(dataName == null || data.isThingByName(dataName) == false){
				thing.removeChild(data);
				r = true;
			}
		}
		
		return r;
	}
	
	/**
	 * 获取事物上符合容器布局的LayoutData。
	 * 
	 * @param parent
	 * @param thing
	 * @return
	 */
	public static Thing getLayoutData(Composite parent, Thing thing){
		Layout layout = parent.getLayout();
		if(layout == null){
			return null;
		}
		
		String layoutName = layout.getClass().getSimpleName();
		String dataName = null;
		if("FormLayout".equals(layoutName)){
			dataName = "FormData";
		}else if("GridLayout".equals(layoutName)){
			dataName = "GridData";
		}else if("RowLayout".equals(layoutName)){
			dataName = "RowData";
		}
		List<Thing> datas = thing.getChilds("LayoutData");
		for(Thing data : datas){
			if(dataName != null && data.isThingByName(dataName)){
				return data;
			}
		}
		
		return null;
	}
	
	/**
	 * 根据容器的布局类型创建符合布局的LayoutData。
	 * 
	 * @param parent
	 * @return
	 */
	public static Thing createLayoutData(Composite parent){
		Layout layout = parent.getLayout();
		if(layout == null){
			return null;
		}
		
		String layoutName = layout.getClass().getSimpleName();
		if("FormLayout".equals(layoutName)){
			return new Thing("xworker.swt.layout.FormData");
		}else if("GridLayout".equals(layoutName)){
			return new Thing("xworker.swt.layout.GridData");
		}else if("RowLayout".equals(layoutName)){
			return new Thing("xworker.swt.layout.RowData");
		}
		
		return null;
	}
	
	public static void throwActinException(String msg, String msgEn, ActionContext actionContext){
		String m;
		try {
			m = StringUtils.getString("lang:d=" + msg + "&en=" + msgEn, actionContext);
			throw new ActionException(m);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	public static void layout(Control control){
		if(control == null){
			return;
		}
		
		if(control instanceof Composite){
			((Composite) control).layout();
		}
		Composite parent = control.getParent();
		if(parent != null){
			if(parent instanceof ExpandBar){
				for(ExpandItem item : ((ExpandBar) parent).getItems()){
					Control c = item.getControl();
					if(c == control){
						Point size = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						item.setHeight(size.y);
						break;
					}
				}
			}else{
				parent.layout();			
				if(parent.getParent() != null){
					parent.getParent().layout();
				}
			}
			
		}
	}
	
	/**
	 * 返回是否运行在Eclpise的RWT环境下。
	 * 
	 * @return
	 */
	public static boolean isRWT() {
		if(isRWT == null) {
			try {
				Thing swt = World.getInstance().getThing("xworker.swt.SWT");
				isRWT = swt.doAction("isRWTWebClient", new ActionContext());
				if(isRWT == null) {
					isRWT = false;
				}
			}catch(Throwable t) {
				//log.error("Check isRWT error", t);
				isRWT = false;
			}
		}
		
		return isRWT;
	}
	
	/**
	 * 创建不支持RAP的提示。如果一个控件不支持RAP那么可以用此方法方便的创建一个不支持RAP的提示。
	 * 
	 * @param self
	 * @param actionContext
	 * @return null表示支持RAP，否则表示不支持RAP
	 */
	public static Composite createNoSupportRAP(Thing self, ActionContext actionContext) {
		if(isRWT()) {
			ThingCompositeCreator creator = new ThingCompositeCreator(self, actionContext);
	        creator.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.app.prototypes.NotSupportRAP/@notSupportComposite"));        
	        return (Composite) creator.create();
		}else {
			return null;
		}

	}

	public static void openMessageBox(MessageBox messageBox, DialogCallback callback, ActionContext actionContext) {
		if(SwtUtils.isRWT()) {
			Thing swt = World.getInstance().getThing("xworker.swt.SWT");
			swt.doAction("openMessageBoxRWT", actionContext, "messageBox", messageBox, "callback", callback);
		}else {
			messageBox.open();
		}
	}
	
	public static Object evaluateBrowserScript(Browser browser, String script, BrowserCallback callback, ActionContext actionContext) {
		try {
			if(SwtUtils.isRWT()) {
				Thing swt = World.getInstance().getThing("xworker.swt.SWT");
				return swt.doAction("rwtBrowserEvaluate", actionContext, "browser", browser, "script", script, "callback", callback);
			}else {
				if(browser.isDisposed()) {
					return null;
				}
				return browser.evaluate(script);
			}
		}catch(Exception e) {
			log.error("Executor browser script error", e);
			return null;
		}
	}
	
	public static void checkLayoutError(Composite composite) {
		if(composite.getLayout() instanceof FillLayout) {
			//简单检查FillLayout
			for(Control child : composite.getChildren()) {
				if(child.getLayoutData() != null) {
					System.out.println("Layout error " + child.getData(Designer.DATA_THING));
				}
				
				if(child instanceof Composite) {
					checkLayoutError((Composite) child);
				}
			}
		}
	}
	
	/**
	 * 为兼容RWT而设置的打开对话框的方法。
	 * 
	 * @param dialog
	 * @param callback
	 * @param actionContext
	 */
	public static void openDialog(Dialog dialog, DialogCallback callback, ActionContext actionContext) {
		if(SwtUtils.isRWT()) {
			Thing swt = World.getInstance().getThing("xworker.swt.SWT");
			swt.doAction("rwtOpenDialog", actionContext, "dialog", dialog, "callback", callback);
		}else {
			try {
				Method open = dialog.getClass().getMethod("open", new Class<?>[] {});
				if(open != null) {
					Object result = open.invoke(dialog, new Object[] {});
					if(callback != null) {
						if(result instanceof Integer) {
							callback.dialogClosed((Integer) result);
						}else {
							callback.dialogClosed(SWT.OK);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 			
		}
	}
	
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y,  rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte)rgb.red;
				green[i] = (byte)rgb.green;
				blue[i] = (byte)rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	public static Image createImage(Control parent, String value, ActionContext actionContext) {
		if(parent == null || parent.isDisposed() || value == null || "".equals(value)) {
			return null;
		}
		
		actionContext.push().put("parent", parent);
		try {
			return  (Image) StyleSetStyleCreator.createResource(value, 
	                "xworker.swt.graphics.Image", "imageFile", actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public static Color createColor(Control parent, String value, ActionContext actionContext) {
		if(parent == null || parent.isDisposed() || value == null || "".equals(value)) {
			return null;
		}
		
		actionContext.push().put("parent", parent);
		try {
			return (Color) StyleSetStyleCreator.createResource(value, "xworker.swt.graphics.Color", "rgb", actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public static Font createFont(Control parent, String value, ActionContext actionContext) {
		if(parent == null || parent.isDisposed() || value == null || "".equals(value)) {
			return null;
		}
		
		actionContext.push().put("parent", parent);
		try {
			return  (Font) StyleSetStyleCreator.createResource(value, "xworker.swt.graphics.Font", "fontData", actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public static Color getColor(String th_nodeColor){
		return XWorkerTreeUtil.getColor(th_nodeColor);
	}
	
	public static Image getIcon(Thing thing, Control tree, ActionContext actionContext, boolean findSelfIcon){ 
		return XWorkerTreeUtil.getIcon(thing, tree, actionContext, findSelfIcon);
	}
	
	public static Image getIcon(Thing thing, Control tree, ActionContext actionContext){
		return XWorkerTreeUtil.getIcon(thing, tree, actionContext);
	}
	
	/**
	 * pushInitStyle()和popInitStyle()需要成对出现。
	 */
	public static void pushInitStyle() {
		ActionContext actionContext = creatorInitStyleLocal.get();
		if(actionContext == null) {
			actionContext = new ActionContext();
			creatorInitStyleLocal.set(actionContext);
		}
		
		actionContext.push();
	}
	
	public static void popInitStyle() {
		ActionContext actionContext = creatorInitStyleLocal.get();
		if(actionContext != null) {
			actionContext.pop();
		}
	}
	
	
	/**
	 * 设置初始化样式，子控件创建时会使用该style作为初始值。一般用于复合控件，用于扩展原型中样式定义。
	 * 
	 * 调用之前pushInitStyle()，必须在finally里调用popInitStyle()方法，以保证样式及时被清空。
	 * 
	 * @param style
	 */
	public static void setInitStyle(String path, int style) {
		ActionContext actionContext = creatorInitStyleLocal.get();
		if(actionContext != null) {
			actionContext.peek().put(path, style);
		}
		//creatorInitStyleLocal.set(style);
	}
	
	/**
	 * 获取初始化样式，并移除设置。如果没有返回SWT.NONE。
	 * 
	 * @return
	 */
	public static int getInitStyle(String path) {
		ActionContext actionContext = creatorInitStyleLocal.get();
		if(actionContext != null) {
			Integer style = actionContext.getObject(path);
			if(style != null) {
				return style;
			}
		}
		
		return SWT.NONE;
		/*
		Integer style = creatorInitStyleLocal.get();
		creatorInitStyleLocal.remove();
		if(style != null) {
			return style;
		}else {
			return SWT.NONE;
		}*/
	}
	
	/**
	 * 使用cls的name作为key从widget的数据里获取对象，并转成T类型的对象返回。
	 * @param widget
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRegist(Widget widget, Class<T> cls) {
		String key = cls.getName();
		return (T) widget.getData(key);
	}
	
	/**
	 * 把对象用cls的name作为key保存到widget的数据中。
	 * 
	 * @param widget
	 * @param obj
	 * @param cls
	 */
	public static void regist(Widget widget, Object obj , Class<?> cls) {
		widget.setData(cls.getName(), obj);
	}
	
	/**
	 * 创建复合组件的创建器，用于创建复合模型的SWT组件。
	 * 
	 * @param self
	 * @param actionContext
	 * @return
	 */
	public static ThingCompositeCreator createCompositeCreator(Thing self, ActionContext actionContext) {
		return new ThingCompositeCreator(self, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static Object runIAction(String className, Thing thing, ActionContext actionContext) {
		try {
			Class<IAction> cls = (Class<IAction>) Class.forName(className);
			IAction action = cls.getConstructor(new Class<?>[0]).newInstance(new Object[0]);
			return action.run(thing, actionContext);
		}catch(Exception e) {
			throw new ActionException("runIAction error", e);
		}
	}
}