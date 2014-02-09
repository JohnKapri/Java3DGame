package com.johnkapri.java3dgame.entity;

import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.johnkapri.java3dgame.Main;
import com.johnkapri.java3dgame.ShaderProgram;

public class EntityLight extends Entity {

	public EntityLight(float x, float y, float z) {
		super(x, y, z);
		Main.getModelManager().loadModel("bunny");
	}

	@Override
	public void render() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		this.getModelMatrix().store(buf);
		buf.flip();
		glUniformMatrix4(ShaderProgram.current.getUniformPosition("model"),
				false, buf);
		buf.clear();

		GL11.glScalef(0.5f, 0.5f, 0.5f);
		if (Main.getModelManager().getModel("bunny") != null) {
			Main.getModelManager().getModel("bunny").render();
		}
		GL11.glScalef(2, 2, 2);
	}
}
