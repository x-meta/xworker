package xworker.libdgx.functions;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/**
 * 具有一个舞台的应用。
 * 
 * @author Administrator
 *
 */
public class StageApplication extends ApplicationAdapter {
    Stage stage;
    int width;
    int height;
    boolean keepAspectRatio;
    
    public StageApplication(int width, int height, boolean keepAspectRatio){
    	this.width = width;
    	this.height = height;
    	this.keepAspectRatio = keepAspectRatio;
    }
    
    public Stage getStage(){
    	return stage;
    }
    
    @Override
    public void create() {        
    	if(keepAspectRatio){
    		stage = new Stage(new ScalingViewport(Scaling.stretch, 480,320));
    	}else{
    		stage = new Stage(new ScalingViewport(Scaling.fit, 480,320));
    	}
    }
 
    @Override
    public void dispose() {
        stage.dispose();
    }
 
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}

