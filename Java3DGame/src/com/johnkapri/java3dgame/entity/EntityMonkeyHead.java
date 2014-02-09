package com.johnkapri.java3dgame.entity;

import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.johnkapri.java3dgame.Main;
import com.johnkapri.java3dgame.ShaderProgram;

public class EntityMonkeyHead extends Entity {

	public EntityMonkeyHead(float x, float y, float z) {
		super(x, y, z);
		Main.getModelManager().loadModel("bolivia_color");
	}

	@Override
	public void render() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		this.getModelMatrix().store(buf);
		buf.flip();
		glUniformMatrix4(ShaderProgram.current.getUniformPosition("model"),
				false, buf);
		buf.clear();

		if (Main.getModelManager().getModel("bolivia_color") != null) {
			Main.getModelManager().getModel("bolivia_color").render();
		}
	}
}
