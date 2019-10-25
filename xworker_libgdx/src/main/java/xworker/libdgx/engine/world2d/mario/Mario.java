package xworker.libdgx.engine.world2d.mario;

import com.badlogic.gdx.math.Vector2;

public class Mario {
	static float WIDTH;
	static float HEIGHT;
	static float MAX_VELOCITY = 10f;
	static float JUMP_VELOCITY = 40f;
	static float DAMPING = 0.87f;

	static enum State {
		Standing, Walking, Jumping
	}

	final Vector2 position = new Vector2();
	final Vector2 velocity = new Vector2();
	State state = State.Walking;
	float stateTime = 0;
	boolean facesRight = true;
	boolean grounded = false;
}
