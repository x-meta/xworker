package xworker.javafx.scene;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Point3D;
import javafx.scene.AccessibleRole;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import xworker.javafx.util.JavaFXUtils;

public class NodeActions {
	public static void init(Node node, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("id")){
            node.setId(thing.getString("id"));
        }
        if(thing.valueExists("accessibleHelp")){
            node.setAccessibleHelp(thing.getString("accessibleHelp"));
        }
        if(thing.valueExists("accessibleRoleDescription")){
            node.setAccessibleRoleDescription(thing.getString("accessibleRoleDescription"));
        }
        if(thing.valueExists("accessibleRole")){
            node.setAccessibleRole(AccessibleRole.valueOf(thing.getString("accessibleRole")));
        }
        if(thing.valueExists("accessibleText")){
            node.setAccessibleText(thing.getString("accessibleText"));
        }
        if(thing.valueExists("blendMode")){
            node.setBlendMode(BlendMode.valueOf(thing.getString("blendMode")));
        }
        if(thing.valueExists("cacheHint")){
            node.setCacheHint(CacheHint.valueOf(thing.getString("cacheHint")));
        }
        if(thing.valueExists("cache")){
            node.setCache(thing.getBoolean("cache"));
        }
        if(thing.valueExists("cursor")){
        	Cursor cursor = JavaFXUtils.getCursor(thing.getString("cursor"));
        	if(cursor != null) {
        		node.setCursor(cursor);
        	}
        }
        if(thing.valueExists("depthTest")){
            node.setDepthTest(DepthTest.valueOf(thing.getString("depthTest")));
        }
        if(thing.valueExists("disable")){
            node.setDisable(thing.getBoolean("disable"));
        }
        if(thing.valueExists("focusTraversable")){
            node.setFocusTraversable(thing.getBoolean("focusTraversable"));
        }
        if(thing.valueExists("layoutX")){
            node.setLayoutX(thing.getDouble("layoutX"));
        }
        if(thing.valueExists("layoutY")){
            node.setLayoutY(thing.getDouble("layoutY"));
        }
        if(thing.valueExists("managed")){
            node.setManaged(thing.getBoolean("managed"));
        }
        if(thing.valueExists("mouseTransparent")){
            node.setMouseTransparent(thing.getBoolean("mouseTransparent"));
        }
        if(thing.valueExists("nodeOrientation")){
            node.setNodeOrientation(NodeOrientation.valueOf(thing.getString("nodeOrientation")));
        }
        if(thing.valueExists("opacity")){
            node.setOpacity(thing.getDouble("opacity"));
        }
        if(thing.valueExists("pickOnBounds")){
            node.setPickOnBounds(thing.getBoolean("pickOnBounds"));
        }
        if(thing.valueExists("rotate")){
            node.setRotate(thing.getDouble("rotate"));
        }
        if(thing.valueExists("rotationAxis")){
        	Point3D point = JavaFXUtils.getPoint3D(thing.getString("rotationAxis"));
        	if(point != null) {
        		node.setRotationAxis(point);
        	}
        }
        if(thing.valueExists("scaleX")){
            node.setScaleX(thing.getDouble("scaleX"));
        }
        if(thing.valueExists("scaleY")){
            node.setScaleY(thing.getDouble("scaleY"));
        }
        if(thing.valueExists("scaleZ")){
            node.setScaleZ(thing.getDouble("scaleZ"));
        }
        if(thing.valueExists("style")){
            node.setStyle(thing.getString("style"));
        }
        if(thing.valueExists("translateX")){
            node.setTranslateX(thing.getDouble("translateX"));
        }
        if(thing.valueExists("translateY")){
            node.setTranslateY(thing.getDouble("translateY"));
        }
        if(thing.valueExists("translateZ")){
            node.setTranslateZ(thing.getDouble("translateZ"));
        }
        if(thing.valueExists("visible")){
            node.setVisible(thing.getBoolean("visible"));
        }

	}
}
