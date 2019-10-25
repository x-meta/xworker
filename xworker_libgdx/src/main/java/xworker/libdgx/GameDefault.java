package xworker.libdgx;

import com.badlogic.gdx.Game;

/**
 * 用用管理
 * @author Administrator
 *
 */
public class GameDefault extends Game{
	
	public GameDefault(){		
	}

	@Override
	public void create() {		
	}

	@Override
	public void dispose() {
		super.dispose();
		
		if(getScreen() != null){
			getScreen().dispose();
		}
	}
	
	
}
