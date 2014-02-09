package com.johnkapri.java3dgame;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.io.File;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import com.johnkapri.java3dgame.entity.EntityMonkeyHead;
import com.johnkapri.java3dgame.gfx.model.ModelManager;
import com.johnkapri.java3dgame.scene.World;

public class Main implements Runnable{

	public static String NAME = "Java 3D Game";
	public static int WIDTH = 640;
	public static int HEIGHT = 3 * WIDTH / 4;
	public static boolean V_SYNC = true;
//	public static Model MONKEY;
//	public static int MONKEY_HIGH_LIST;
//	public static int LARA_LIST;
//	public static Model BOLIVIA;
	
	private static final float walkingSpeed = 1f;
	private static final float mouseSpeed = 2;
    private static final int maxLookUp = 85;
    private static final int maxLookDown = -85;

	private int fps = 0;
	private long lastTime = System.currentTimeMillis();
	private long lastFrame;

	private ShaderProgram shaderProgram;
	private boolean wireframe = false;
	private boolean gReleased = true;
	private boolean culling = true;
	private boolean cReleased = true;
	
	public Camera camera;
	private World world;
	private static ModelManager manager;

	private int tick;
	
	@Override
	public void run() {
		init();
		load();
				
		int ammount = 1;
		float spacing = 2.3f;
		for(int x = 0; x < ammount; x++) {
			for(int z = 0; z < ammount; z++) {
				world.addEntity(new EntityMonkeyHead((x - ammount / 2) * spacing, 0, (z - ammount / 2) * spacing));				
			}
		}
		lastFrame = getTime() - 10;
		while (!Display.isCloseRequested()) {
			inputs();
			float delta = getDelta();
			if(Mouse.isGrabbed()) {
				camera.processKeyboard(delta, 2.0f);
				camera.processMouse(mouseSpeed);
			}
			tick(delta);
			fps++;
			if(wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);	
			} else {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);								
			}
			if(culling) {
				glEnable(GL_CULL_FACE);
			} else {
				glDisable(GL_CULL_FACE);
			}
			render();
			if (System.currentTimeMillis() - lastTime >= 1000L) {
				System.out.println("FPS: " + fps);
				fps = 0;
				lastTime = System.currentTimeMillis();
			}
			Display.update();
			if(V_SYNC) {
				Display.sync(60);
			}
		}
		Display.destroy();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		shaderProgram.use(true);
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		camera.getViewMatrix().store(buf);
		buf.flip();
		glUniformMatrix4(shaderProgram.getUniformPosition("view"), false, buf);
		
		buf.clear();
		camera.getProjectionMatrix().store(buf);
		buf.flip();
		glUniformMatrix4(shaderProgram.getUniformPosition("proj"), false, buf);
		
		glColor3f(1, 1, 1);
		world.render();		
	}
	
	private void tick(float delta) {
		tick++;
		if(lastFrame + 2000 <= getTime()) {
			System.gc();
		}		
		world.tick(delta);
	}

	private void init() {
		System.out.println("*** INITIALIZING ***");
		try {
			System.out.println("Creating Display...");
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			//Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			System.out.println("   Display Mode: " + Display.getDisplayMode());
			System.out.println("   Fullscreen:   " + Display.isFullscreen());
			Display.setVSyncEnabled(V_SYNC);
			System.out.println("   Use VSync:    " + V_SYNC);
			Display.setTitle(NAME);
			Display.setResizable(false);
			Display.create();
			System.out.println("Display created!");
		} catch (Exception e) {
			System.out.println("Error setting up display");
			System.exit(1);
		}

			getModelManager().notifyAboutGL(Display.isCreated());
		System.out.println("Setting up OpenGL rendering ...");
		glClearColor(0.2F, 0.2F, 0.2F, 1);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_DEPTH);		
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_SMOOTH);
		
		camera = new EulerCamera();
		world = new World();
		
		System.out.println("Set up!");
		System.out.println("*** DONE! ***");
		System.out.println();
	}
	
	private void load() {
//		System.out.println("*** LOADING MODELS ***");
//		try {
//			BOLIVIA = Model.loadModelFromOBJ(new File("res/bolivia_color.obj"));
//			MONKEY = Model.loadModelFromOBJ(new File("res/bunny.obj"));
//		} catch (IOException e) {
//			System.err.println("Failed to load model!");
//			Display.destroy();
//			System.exit(1);
//		}
//		System.out.println("*** DONE! ***");
//		System.out.println();
		
		System.out.println("*** LOADING SHADERS ***");
		shaderProgram = new ShaderProgram("shaders/shader.vsh", "shaders/shader.fsh");	
		shaderProgram.load();
		shaderProgram.use(true);
		System.out.println("*** DONE! ***");
		System.out.println();
	}
	
	private void inputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Display.destroy();
			System.exit(0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_G)) {
			if(gReleased) {
				wireframe = !wireframe;
				gReleased = false;
			}
		} else {
			gReleased = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if(cReleased) {
				culling = !culling;
				cReleased = false;
			}
		} else {
			cReleased = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
			world.lightPos = new Vector3f(camera.x(), camera.y(), camera.z());
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			Mouse.setGrabbed(false);
			Mouse.setClipMouseCoordinatesToWindow(false);
		}
		if(Mouse.isButtonDown(0) && !Mouse.isGrabbed() && Mouse.isInsideWindow()) {
			Mouse.setGrabbed(true);
			Mouse.setClipMouseCoordinatesToWindow(true);
		}
	}
	
	private float getDelta() {
		long currentTime = getTime();
		float delta = currentTime - lastFrame;
		lastFrame = getTime();
		return delta;
	}
	
	private long getTime() {
		return (Sys.getTime() * 1000L) / Sys.getTimerResolution();
	}

	public static File getGameDir() {
		File f = new File(System.getProperty("user.home") + File.separator + "." + NAME.toLowerCase().trim() + File.separator);
		if(!f.exists()) {
			f.mkdirs();
			System.out.println("Created root directory!");
		}
		return f;
	}
	
	public static ModelManager getModelManager() {
		if(manager == null) {
			manager= new ModelManager();
		}
		return manager;
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		Thread t = new Thread(m, "MAIN GAME LOOP");
		t.start();
	}
	
	public static FloatBuffer floatBuffer(float[] values) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}
}
