package xworker.javafx.scene.paint;

import javafx.scene.paint.Color;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.HashMap;
import java.util.Map;

public class ColorActions {
    public static Map<String, Color> colors = new HashMap<>();
    static{
        colors.put("ALICEBLUE", Color.ALICEBLUE);
        colors.put("ANTIQUEWHITE", Color.ANTIQUEWHITE);
        colors.put("AQUA", Color.AQUA);
        colors.put("AQUAMARINE", Color.AQUAMARINE);
        colors.put("AZURE", Color.AZURE);
        colors.put("BEIGE", Color.BEIGE);
        colors.put("BISQUE", Color.BISQUE);
        colors.put("BLACK", Color.BLACK);
        colors.put("BLANCHEDALMOND", Color.BLANCHEDALMOND);
        colors.put("BLUE", Color.BLUE);
        colors.put("BLUEVIOLET", Color.BLUEVIOLET);
        colors.put("BROWN", Color.BROWN);
        colors.put("BURLYWOOD", Color.BURLYWOOD);
        colors.put("CADETBLUE", Color.CADETBLUE);
        colors.put("CHARTREUSE", Color.CHARTREUSE);
        colors.put("CHOCOLATE", Color.CHOCOLATE);
        colors.put("CORAL", Color.CORAL);
        colors.put("CORNFLOWERBLUE", Color.CORNFLOWERBLUE);
        colors.put("CORNSILK", Color.CORNSILK);
        colors.put("CRIMSON", Color.CRIMSON);
        colors.put("CYAN", Color.CYAN);
        colors.put("DARKBLUE", Color.DARKBLUE);
        colors.put("DARKCYAN", Color.DARKCYAN);
        colors.put("DARKGOLDENROD", Color.DARKGOLDENROD);
        colors.put("DARKGRAY", Color.DARKGRAY);
        colors.put("DARKGREEN", Color.DARKGREEN);
        colors.put("DARKGREY", Color.DARKGREY);
        colors.put("DARKKHAKI", Color.DARKKHAKI);
        colors.put("DARKMAGENTA", Color.DARKMAGENTA);
        colors.put("DARKOLIVEGREEN", Color.DARKOLIVEGREEN);
        colors.put("DARKORANGE", Color.DARKORANGE);
        colors.put("DARKORCHID", Color.DARKORCHID);
        colors.put("DARKRED", Color.DARKRED);
        colors.put("DARKSALMON", Color.DARKSALMON);
        colors.put("DARKSEAGREEN", Color.DARKSEAGREEN);
        colors.put("DARKSLATEBLUE", Color.DARKSLATEBLUE);
        colors.put("DARKSLATEGRAY", Color.DARKSLATEGRAY);
        colors.put("DARKSLATEGREY", Color.DARKSLATEGREY);
        colors.put("DARKTURQUOISE", Color.DARKTURQUOISE);
        colors.put("DARKVIOLET", Color.DARKVIOLET);
        colors.put("DEEPPINK", Color.DEEPPINK);
        colors.put("DEEPSKYBLUE", Color.DEEPSKYBLUE);
        colors.put("DIMGRAY", Color.DIMGRAY);
        colors.put("DIMGREY", Color.DIMGREY);
        colors.put("DODGERBLUE", Color.DODGERBLUE);
        colors.put("FIREBRICK", Color.FIREBRICK);
        colors.put("FLORALWHITE", Color.FLORALWHITE);
        colors.put("FORESTGREEN", Color.FORESTGREEN);
        colors.put("FUCHSIA", Color.FUCHSIA);
        colors.put("GAINSBORO", Color.GAINSBORO);
        colors.put("GHOSTWHITE", Color.GHOSTWHITE);
        colors.put("GOLD", Color.GOLD);
        colors.put("GOLDENROD", Color.GOLDENROD);
        colors.put("GRAY", Color.GRAY);
        colors.put("GREEN", Color.GREEN);
        colors.put("GREENYELLOW", Color.GREENYELLOW);
        colors.put("GREY", Color.GREY);
        colors.put("HONEYDEW", Color.HONEYDEW);
        colors.put("HOTPINK", Color.HOTPINK);
        colors.put("INDIANRED", Color.INDIANRED);
        colors.put("INDIGO", Color.INDIGO);
        colors.put("IVORY", Color.IVORY);
        colors.put("KHAKI", Color.KHAKI);
        colors.put("LAVENDER", Color.LAVENDER);
        colors.put("LAVENDERBLUSH", Color.LAVENDERBLUSH);
        colors.put("LAWNGREEN", Color.LAWNGREEN);
        colors.put("LEMONCHIFFON", Color.LEMONCHIFFON);
        colors.put("LIGHTBLUE", Color.LIGHTBLUE);
        colors.put("LIGHTCORAL", Color.LIGHTCORAL);
        colors.put("LIGHTCYAN", Color.LIGHTCYAN);
        colors.put("LIGHTGOLDENRODYELLOW", Color.LIGHTGOLDENRODYELLOW);
        colors.put("LIGHTGRAY", Color.LIGHTGRAY);
        colors.put("LIGHTGREEN", Color.LIGHTGREEN);
        colors.put("LIGHTGREY", Color.LIGHTGREY);
        colors.put("LIGHTPINK", Color.LIGHTPINK);
        colors.put("LIGHTSALMON", Color.LIGHTSALMON);
        colors.put("LIGHTSEAGREEN", Color.LIGHTSEAGREEN);
        colors.put("LIGHTSKYBLUE", Color.LIGHTSKYBLUE);
        colors.put("LIGHTSLATEGRAY", Color.LIGHTSLATEGRAY);
        colors.put("LIGHTSLATEGREY", Color.LIGHTSLATEGREY);
        colors.put("LIGHTSTEELBLUE", Color.LIGHTSTEELBLUE);
        colors.put("LIGHTYELLOW", Color.LIGHTYELLOW);
        colors.put("LIME", Color.LIME);
        colors.put("LIMEGREEN", Color.LIMEGREEN);
        colors.put("LINEN", Color.LINEN);
        colors.put("MAGENTA", Color.MAGENTA);
        colors.put("MAROON", Color.MAROON);
        colors.put("MEDIUMAQUAMARINE", Color.MEDIUMAQUAMARINE);
        colors.put("MEDIUMBLUE", Color.MEDIUMBLUE);
        colors.put("MEDIUMORCHID", Color.MEDIUMORCHID);
        colors.put("MEDIUMPURPLE", Color.MEDIUMPURPLE);
        colors.put("MEDIUMSEAGREEN", Color.MEDIUMSEAGREEN);
        colors.put("MEDIUMSLATEBLUE", Color.MEDIUMSLATEBLUE);
        colors.put("MEDIUMSPRINGGREEN", Color.MEDIUMSPRINGGREEN);
        colors.put("MEDIUMTURQUOISE", Color.MEDIUMTURQUOISE);
        colors.put("MEDIUMVIOLETRED", Color.MEDIUMVIOLETRED);
        colors.put("MIDNIGHTBLUE", Color.MIDNIGHTBLUE);
        colors.put("MINTCREAM", Color.MINTCREAM);
        colors.put("MISTYROSE", Color.MISTYROSE);
        colors.put("MOCCASIN", Color.MOCCASIN);
        colors.put("NAVAJOWHITE", Color.NAVAJOWHITE);
        colors.put("NAVY", Color.NAVY);
        colors.put("OLDLACE", Color.OLDLACE);
        colors.put("OLIVE", Color.OLIVE);
        colors.put("OLIVEDRAB", Color.OLIVEDRAB);
        colors.put("ORANGE", Color.ORANGE);
        colors.put("ORANGERED", Color.ORANGERED);
        colors.put("ORCHID", Color.ORCHID);
        colors.put("PALEGOLDENROD", Color.PALEGOLDENROD);
        colors.put("PALEGREEN", Color.PALEGREEN);
        colors.put("PALETURQUOISE", Color.PALETURQUOISE);
        colors.put("PALEVIOLETRED", Color.PALEVIOLETRED);
        colors.put("PAPAYAWHIP", Color.PAPAYAWHIP);
        colors.put("PEACHPUFF", Color.PEACHPUFF);
        colors.put("PERU", Color.PERU);
        colors.put("PINK", Color.PINK);
        colors.put("PLUM", Color.PLUM);
        colors.put("POWDERBLUE", Color.POWDERBLUE);
        colors.put("PURPLE", Color.PURPLE);
        colors.put("RED", Color.RED);
        colors.put("ROSYBROWN", Color.ROSYBROWN);
        colors.put("ROYALBLUE", Color.ROYALBLUE);
        colors.put("SADDLEBROWN", Color.SADDLEBROWN);
        colors.put("SALMON", Color.SALMON);
        colors.put("SANDYBROWN", Color.SANDYBROWN);
        colors.put("SEAGREEN", Color.SEAGREEN);
        colors.put("SEASHELL", Color.SEASHELL);
        colors.put("SIENNA", Color.SIENNA);
        colors.put("SILVER", Color.SILVER);
        colors.put("SKYBLUE", Color.SKYBLUE);
        colors.put("SLATEBLUE", Color.SLATEBLUE);
        colors.put("SLATEGRAY", Color.SLATEGRAY);
        colors.put("SLATEGREY", Color.SLATEGREY);
        colors.put("SNOW", Color.SNOW);
        colors.put("SPRINGGREEN", Color.SPRINGGREEN);
        colors.put("STEELBLUE", Color.STEELBLUE);
        colors.put("TAN", Color.TAN);
        colors.put("TEAL", Color.TEAL);
        colors.put("THISTLE", Color.THISTLE);
        colors.put("TOMATO", Color.TOMATO);
        colors.put("TRANSPARENT", Color.TRANSPARENT);
        colors.put("TURQUOISE", Color.TURQUOISE);
        colors.put("VIOLET", Color.VIOLET);
        colors.put("WHEAT", Color.WHEAT);
        colors.put("WHITE", Color.WHITE);
        colors.put("WHITESMOKE", Color.WHITESMOKE);
        colors.put("YELLOW", Color.YELLOW);
        colors.put("YELLOWGREEN", Color.YELLOWGREEN);
    }

    public static Color create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Color color = null;
        String key = self.getStringBlankAsNull("color");
        if(key != null){
            color = colors.get(key);
        }

        if(color == null){
            String web = self.getStringBlankAsNull("webColor");
            if(web != null){
                color = Color.web(web);
            }
        }

        actionContext.g().put(self.getMetadata().getName(), color);

        return color;
    }
}
