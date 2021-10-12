package xworker.app.model.tree;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeModelItem {
    public static final String FONT_BOLD = "BOLD";
    public static final String FONT_ITALIC = "ITALIC";
    public static final String FONT_BOLD_ITALIC = "BOLD_ITALIC";

    /** TreeItem的打开和关闭的状态缓存，全局 */
    private final static Map<String, Map<String, Boolean>> expandStatusCache = new HashMap<>();

    TreeModel treeModel;
    TreeModelItem parent;
    List<TreeModelItem> items;

    String tabId;
    String dataId;
    String url;
    String id;
    String text;
    String cls;
    boolean leaf;
    String href;
    boolean allowChildren;
    boolean allowDrag;
    boolean allowDrop;
    boolean checked;
    boolean disabled;
    boolean dragable;
    boolean editable;
    boolean expandable;
    boolean expanded;
    boolean hidden;
    String hrefTarget;
    String icon;
    String iconCls;
    boolean isTarget;
    String qtip;
    boolean singleClickExpanded;
    boolean separator;
    Object source;
    boolean childInited = false;
    String foreground;
    String background;
    String fontName;
    int fontSize;
    String fontStyle;

    public TreeModelItem(TreeModel treeModel, TreeModelItem parent){
        this.treeModel = treeModel;
        this.parent = parent;
    }

    public TreeModelItem(TreeModel treeModel, TreeModelItem parent, Map<String, Object> item){
        this(treeModel, parent);

        this.source = item;
        this.tabId = UtilData.getString(item.get("tabId"), null);
        this.dataId = UtilData.getString(item.get("dataId"), null);
        this.url = UtilData.getString(item.get("url"), null);
        this.id = treeModel.getThing().getMetadata().getPath() + "|" + UtilData.getString(item.get("id"), null);
        this.text = UtilData.getString(item.get("text"), null);
        this.cls = UtilData.getString(item.get("cls"), null);
        this.foreground = UtilData.getString(item.get("foreground"), null);
        this.background = UtilData.getString(item.get("background"), null);
        this.fontStyle = UtilData.getString(item.get("fontStyle"), null);
        this.fontName = UtilData.getString(item.get("fontName"), null);
        this.fontSize = UtilData.getInt(item.get("fontSize"), 0);

        this.allowChildren = UtilData.getBoolean(item.get("allowChildren"), true);
        this.allowDrag = UtilData.getBoolean(item.get("allowDrag"), true);
        this.allowDrop = UtilData.getBoolean(item.get("allowDrop"), true);
        this.checked = UtilData.getBoolean(item.get("checked"), false);
        this.disabled = UtilData.getBoolean(item.get("disabled"), false);
        this.dragable = UtilData.getBoolean(item.get("dragable"), true);
        this.editable = UtilData.getBoolean(item.get("editable"), true);
        this.expandable = UtilData.getBoolean(item.get("expandable"), true);
        Object expanded = item.get("expanded");
        if(expanded == null){
            initExpandStatus();
        }else {
            this.expanded = UtilData.getBoolean(item.get("expanded"), false);
        }
        this.hidden = UtilData.getBoolean(item.get("hidden"), false);
        this.isTarget = UtilData.getBoolean(item.get("isTarget"), false);
        this.singleClickExpanded = UtilData.getBoolean(item.get("singleClickExpanded"), true);
        this.leaf = UtilData.getBoolean(item.get("leaf"), false);
        this.separator = UtilData.getBoolean(item.get("leaf"), separator);

        this.hrefTarget = UtilData.getString(item.get("hrefTarget"), null);
        this.icon = UtilData.getString(item.get("icon"), null);
        this.iconCls = UtilData.getString(item.get("iconCls"), null);
        this.qtip = UtilData.getString(item.get("qtip"), null);
    }

    public TreeModelItem(TreeModel treeModel, TreeModelItem parent, Thing item){
        this(treeModel, parent);

        this.source = item;
        this.tabId = item.getString("tabId");
        this.dataId = item.getString("dataId");
        this.url = item.getString("url");
        this.id = treeModel.getThing().getMetadata().getPath() + "|" + item.getMetadata().getPath();
        this.text = item.getMetadata().getLabel();
        this.cls = item.getString("cls");
        this.foreground = item.getString("foreground");
        this.background = item.getString("background");
        this.fontStyle = item.getString("fontStyle");
        this.fontName = item.getString("fontName");
        this.fontSize = item.getInt("fontSize", 0);

        this.allowChildren = item.getBoolean("allowChildren");
        this.allowDrag = item.getBoolean("allowDrag");
        this.allowDrop = item.getBoolean("allowDrop");
        this.checked = item.getBoolean("checked");
        this.disabled = item.getBoolean("disabled");
        this.dragable = item.getBoolean("dragable");
        this.editable = item.getBoolean("editable");
        this.expandable = item.getBoolean("expandable");
        if(!item.valueExists("expanded")){
            initExpandStatus();
        }else {
            this.expanded = item.getBoolean("expanded");
        }
        this.hidden = item.getBoolean("hidden");
        this.isTarget = item.getBoolean("isTarget");
        this.singleClickExpanded = item.getBoolean("singleClickExpanded");
        this.separator = item.getBoolean("separator");

        this.leaf = item.getBoolean("leaf");
        this.hrefTarget = item.getString("hrefTarget");
        this.icon = item.getString("icon");
        this.iconCls = item.getString("iconCls");
        this.qtip = item.getString("qtip");

    }

    public void insert(List<TreeModelItem> items, int index){
        if(this.items == null){
            this.items = items;
            return;
        }

        if(index <0 || index >= this.items.size()){
            this.items.addAll(items);
        }else{
            this.items.addAll(index, items);
        }
    }

    public void setExpandStatusCache(boolean expanded){
        String modelPath = treeModel.getThing().getMetadata().getPath();
        Map<String, Boolean> modelCache = expandStatusCache.computeIfAbsent(modelPath, k -> new HashMap<>());

        modelCache.put(id, expanded);
    }

    private void initExpandStatus(){
        this.expanded = false;

        String modelPath = treeModel.getThing().getMetadata().getPath();
        Map<String, Boolean> modelCache = expandStatusCache.get(modelPath);
        if(modelCache != null){
            Boolean expand = modelCache.get(id);
            if(expand != null){
                this.expanded = expand;
            }
        }
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isAllowChildren() {
        return allowChildren;
    }

    public void setAllowChildren(boolean allowChildren) {
        this.allowChildren = allowChildren;
    }

    public boolean isAllowDrag() {
        return allowDrag;
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
    }

    public boolean isAllowDrop() {
        return allowDrop;
    }

    public void setAllowDrop(boolean allowDrop) {
        this.allowDrop = allowDrop;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDragable() {
        return dragable;
    }

    public void setDragable(boolean dragable) {
        this.dragable = dragable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getHrefTarget() {
        return hrefTarget;
    }

    public void setHrefTarget(String hrefTarget) {
        this.hrefTarget = hrefTarget;
    }

    public String getIcon() {
        if(icon == null || icon.isEmpty()){
            if(childInited){
                if(items != null && items.size() > 0){
                    return "icons/folder.gif";
                }else{
                    return "icons/page_white.gif";
                }
            }else{
                return "icons/folder.gif";
            }
        }

        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean target) {
        isTarget = target;
    }

    public String getQtip() {
        return qtip;
    }

    public void setQtip(String qtip) {
        this.qtip = qtip;
    }

    public boolean isSingleClickExpanded() {
        return singleClickExpanded;
    }

    public void setSingleClickExpanded(boolean singleClickExpanded) {
        this.singleClickExpanded = singleClickExpanded;
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public ActionContext getActionContext() {
        return treeModel.getActionContext();
    }

    public TreeModelItem getParent() {
        return parent;
    }

    public List<TreeModelItem> getItems() {
        return items;
    }

    /**
     * 如果items不为null，那么设置子节点为已初始化，否则改为子节点未初始化。
     *
     * @param items 要设置的条目
     */
    public void setItems(List<TreeModelItem> items){
        if(items == null){
            this.items = null;
            this.childInited = false;
        }else {
            this.childInited = true;
            this.items = items;
        }
    }

    public boolean isSeparator() {
        return separator;
    }

    public void setSeparator(boolean separator) {
        this.separator = separator;
    }

    public <T> T getSource() {
        return (T) source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public boolean isChildInited() {
        return childInited;
    }
}
