package xworker.libdgx.examples.dartshasha;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TargetGroup extends Group {
    public TargetGroup(AtlasRegion region) {
        super();
        
        int minY = 0;
        int maxY = (int) (320 - region.getRegionHeight());
        int tempY = MathUtils.random(minY, maxY);
        
        for (int i = 0; i < 3; i++) {
            Image image = new Image(region);
            image.setX(480 - image.getWidth());
            // 开始判断Y值是否符合要求
            boolean flag = false;
            do {
                flag = false;
                tempY = MathUtils.random(minY, maxY); // 生成Y值
 
                Actor[] actors = this.getChildren().begin(); // 获取当前已有的怪兽对象
                for (int j = 0; j < this.getChildren().size; j++) {
                    Actor tempActor = actors[j];
                    if (tempY == tempActor.getY()) { // 如果Y值相等，比如重合，所以舍弃,重新生成
                        flag = true;
                        break;
                    } else if (tempY < tempActor.getY()) { // 如果生成的Y值小于当前怪兽的Y值，则判断生成的Y值加上高度后是否合适
                        if ((tempY + region.getRegionHeight()) >= tempActor
                                .getY()) {
                            flag = true;
                            break;
                        }
                    } else { // 如果生成的Y值大于当前怪兽的Y值，则判断当前怪兽的Y值加上高度后是否合适
                        if (tempY <= (tempActor.getY() + region
                                .getRegionHeight())) {
                            flag = true;
                            break;
                        }
                    }
                }
            } while (flag);
            image.setY(tempY);
            
            this.addMove(image, MathUtils.random(3f, 8f)); //怪兽移动效果
            this.addActor(image); //添加到组中
        }
    }
    
    public void addMove(Actor actor, float time) {
        actor.addAction(Actions.moveTo(0, actor.getY(), time));
    }
    
}
