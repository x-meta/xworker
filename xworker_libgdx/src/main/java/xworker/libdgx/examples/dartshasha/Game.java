package xworker.libdgx.examples.dartshasha;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class Game extends InputAdapter implements ApplicationListener {
	Stage stage;
	Group projectiles;
	Image man;
	TextureAtlas atlas;
	TargetGroup targetGroup;
	FPSLogger fpsLogger = new FPSLogger();
	int fps = 0;
	
	@Override
	public void create() {
		stage = new Stage(new ScalingViewport(Scaling.fit, 480,320));

		// 创建FPS标签
		LabelStyle labelStyle = new LabelStyle(new BitmapFont(), Color.BLACK); // 创建一个Label样式，使用默认黑色字体
		Label label = new Label("FPS:", labelStyle); // 创建标签，显示的文字是FPS：
		label.setName("fpsLabel"); // 设置标签名称为fpsLabel
		label.setY(0); // 设置Y为0，即显示在最下面
		label.setX(480 - label.getWidth()); // 设置X值，显示为最后一个字紧靠屏幕最右侧
		stage.addActor(label); // 将标签添加到舞台

		label.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				Label label = (Label) stage.getRoot().findActor("fpsLabel"); // 获取名为fpsLabel的标签
				label.setText("FPS:" + fps);
				label.setX(480 - label.getWidth()); // 更新X值以保证显示位置正确性
				return false;
			}
		});

		// 创建忍者
		atlas = new TextureAtlas("asserts/examples/shasha/pack/default.pack");

		man = new Image(atlas.findRegion("Player")); // 获取图册中的Player.png并创建image对象
		man.setX(0);
		man.setY(160 - man.getHeight() / 2); // 设置Y值，以让图片在中间显示
		stage.addActor(man); // 将主角添加到舞台

		// 创建怪兽
		targetGroup = new TargetGroup(atlas.findRegion("Target"));
		stage.addActor(targetGroup);

		// 飞镖组
		projectiles = new Group();
		stage.addActor(projectiles); // 添加飞镖组到舞台

		// 输入
		InputMultiplexer multiplexer = new InputMultiplexer(); // 多输入接收器
		multiplexer.addProcessor(this); // 添加自身作为接收
		multiplexer.addProcessor(stage); // 添加舞台
		Gdx.input.setInputProcessor(multiplexer); // 设置多输入接收器为接收器

		stage.addAction(new Action() {

			@Override
			public boolean act(float delta) {
				// 飞镖的移除判
				Actor[] projectile = projectiles.getChildren().begin(); // 获取Actor数组
				for (int j = 0; j < projectiles.getChildren().size; j++) { // 移除判断
					Actor actor = projectile[j];
					if (!ProjectileFactory.checkAlive(actor)) {
						projectiles.removeActor(actor);
					}
				}

				// 开始处理飞镖
				Actor[] targets = targetGroup.getChildren().begin();
				for (int i = 0; i < projectiles.getChildren().size; i++) {
					Actor actor = projectile[i];
					for (int j = 0; j < targetGroup.getChildren().size; j++) {
						Actor target = targets[j];
						if (ProjectileFactory.attackAlive(target, actor)) {
							targetGroup.removeActor(target);
							projectiles.removeActor(actor);
							break;
						}
					}
				}

				// 如果飞镖已经飞到则刪除
				projectile = projectiles.getChildren().begin();
				for (int j = 0; j < projectiles.getChildren().size; j++) {
					Actor actor = projectile[j];
					if (!ProjectileFactory.checkAlive(actor)) {
						projectiles.removeActor(actor);
					}
				}

				return false;
			}

		});
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 vector3 = new Vector3(screenX, screenY, 0);
		stage.getCamera().unproject(vector3); // 坐标转化
		projectiles.addActor(ProjectileFactory.createProjectile(
				atlas.findRegion("Projectile"), man, vector3)); // 添加新飞镖到飞镖组
		return true;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		fps = Gdx.graphics.getFramesPerSecond();
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
