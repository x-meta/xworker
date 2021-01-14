package xworker.libdgx.engine.world2d.mario;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import ognl.OgnlException;
import xworker.libdgx.engine.world2d.GameEngine2D;

/**
 * 类似超级玛丽这样的横版游戏。
 * 
 * @author Administrator
 * 
 */
public class MarioTypeGame implements GameEngine2D{
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private Animation stand;
	private Animation walk;
	private Animation jump;
	private Mario mario;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	private Array<Rectangle> tiles = new Array<Rectangle>();

	private static final float GRAVITY = -2.5f;

	Thing thing;
	ActionContext actionContext;

	public MarioTypeGame(Thing thing, ActionContext actionContext)
			throws OgnlException {
		init(thing, actionContext);
	}

	public void init(Thing thing, ActionContext actionContext)
			throws OgnlException {
		this.thing = thing;
		this.actionContext = actionContext;

		stand = UtilData.getObjectByType(thing, "standAnimation", Animation.class, actionContext);
		jump = UtilData.getObjectByType(thing, "jumpAnimation",	Animation.class, actionContext);
		walk = UtilData.getObjectByType(thing, "walAnimationk",	Animation.class, actionContext);

		float scaleUnit = 1 / thing.getFloat("sacleUnit", 16f, actionContext);
		// figure out the width and height of the mario for collision
		// detection and rendering by converting a mario frames pixel
		// size into world units (1 unit == 16 pixels)
		Mario.WIDTH = thing.getFloat("marioWidth", 16, actionContext)
				* scaleUnit;
		Mario.HEIGHT = thing.getFloat("marioHeight", 16, actionContext)
				* scaleUnit;

		// load the map, set the unit scale to 1/16 (1 unit == 16 pixels)

		map = UtilData.getObjectByType(thing, "map", TiledMap.class,
				actionContext);
		renderer = new OrthogonalTiledMapRenderer(map, scaleUnit);

		// create an orthographic camera, shows us 30x20 units of the world
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();

		// create the mario we want to move around the world
		mario = new Mario();
		mario.position.set(20, 20);
	}

	public void render(float deltaTime) {
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update the mario (process input, collision detection, position
		// update)
		updatemario(deltaTime);

		// let the camera follow the mario, x-axis only
		camera.position.x = mario.position.x;
		camera.update();

		// set the tile map rendere view based on what the
		// camera sees and render the map
		renderer.setView(camera);
		renderer.render();

		// render the mario
		rendermario(deltaTime);
	}

	private void updatemario(float deltaTime) {
		if (deltaTime == 0)
			return;
		mario.stateTime += deltaTime;

		// check input and apply to velocity & state
		if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.5f, 1))
				&& mario.grounded) {
			mario.velocity.y += Mario.JUMP_VELOCITY;
			mario.state = Mario.State.Jumping;
			mario.grounded = false;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)
				|| isTouched(0, 0.25f)) {
			mario.velocity.x = -Mario.MAX_VELOCITY;
			if (mario.grounded)
				mario.state = Mario.State.Walking;
			mario.facesRight = false;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
			mario.velocity.x = Mario.MAX_VELOCITY;
			if (mario.grounded)
				mario.state = Mario.State.Walking;
			mario.facesRight = true;
		}

		// apply gravity if we are falling
		mario.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
		if (Math.abs(mario.velocity.x) > Mario.MAX_VELOCITY) {
			mario.velocity.x = Math.signum(mario.velocity.x)
					* Mario.MAX_VELOCITY;
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(mario.velocity.x) < 1) {
			mario.velocity.x = 0;
			if (mario.grounded)
				mario.state = Mario.State.Standing;
		}

		// multiply by delta time so we know how far we go
		// in this frame
		mario.velocity.scl(deltaTime);

		// perform collision detection & response, on each axis, separately
		// if the mario is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle marioRect = rectPool.obtain();
		marioRect.set(mario.position.x, mario.position.y, Mario.WIDTH,
				Mario.HEIGHT);
		int startX, startY, endX, endY;
		if (mario.velocity.x > 0) {
			startX = endX = (int) (mario.position.x + Mario.WIDTH + mario.velocity.x);
		} else {
			startX = endX = (int) (mario.position.x + mario.velocity.x);
		}
		startY = (int) (mario.position.y);
		endY = (int) (mario.position.y + Mario.HEIGHT);
		getTiles(startX, startY, endX, endY, tiles);
		marioRect.x += mario.velocity.x;
		for (Rectangle tile : tiles) {
			if (marioRect.overlaps(tile)) {
				mario.velocity.x = 0;
				break;
			}
		}
		marioRect.x = mario.position.x;

		// if the mario is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if (mario.velocity.y > 0) {
			startY = endY = (int) (mario.position.y + Mario.HEIGHT + mario.velocity.y);
		} else {
			startY = endY = (int) (mario.position.y + mario.velocity.y);
		}
		startX = (int) (mario.position.x);
		endX = (int) (mario.position.x + Mario.WIDTH);
		getTiles(startX, startY, endX, endY, tiles);
		marioRect.y += mario.velocity.y;
		for (Rectangle tile : tiles) {
			if (marioRect.overlaps(tile)) {
				// we actually reset the mario y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (mario.velocity.y > 0) {
					mario.position.y = tile.y - Mario.HEIGHT;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
					layer.setCell((int) tile.x, (int) tile.y, null);
				} else {
					mario.position.y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					mario.grounded = true;
				}
				mario.velocity.y = 0;
				break;
			}
		}
		rectPool.free(marioRect);

		// unscale the velocity by the inverse delta time and set
		// the latest position
		mario.position.add(mario.velocity);
		mario.velocity.scl(1 / deltaTime);

		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		mario.velocity.x *= Mario.DAMPING;
	}

	private boolean isTouched(float startX, float endX) {
		// check if any finge is touch the area between startX and endX
		// startX/endX are given between 0 (left edge of the screen) and 1
		// (right edge of the screen)
		for (int i = 0; i < 2; i++) {
			float x = Gdx.input.getX(i) / (float) Gdx.graphics.getWidth();
			if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
				return true;
			}
		}
		return false;
	}

	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}

	private void rendermario(float deltaTime) {
		// based on the mario state, get the animation frame
		TextureRegion frame = null;
		switch (mario.state) {
		case Standing:
			frame = (TextureRegion) stand.getKeyFrame(mario.stateTime);
			break;
		case Walking:
			frame = (TextureRegion) walk.getKeyFrame(mario.stateTime);
			break;
		case Jumping:
			frame = (TextureRegion) jump.getKeyFrame(mario.stateTime);
			break;
		}

		// draw the mario, depending on the current velocity
		// on the x-axis, draw the mario facing either right
		// or left
		Batch batch = renderer.getBatch();
		batch.begin();
		if (mario.facesRight) {
			batch.draw(frame, mario.position.x, mario.position.y, Mario.WIDTH,
					Mario.HEIGHT);
		} else {
			batch.draw(frame, mario.position.x + Mario.WIDTH, mario.position.y,
					-Mario.WIDTH, Mario.HEIGHT);
		}
		batch.end();
	}

	public void dispose() {
	}
	
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		MarioTypeGame game = new MarioTypeGame(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), game);
		
		return game;
	}	
}
