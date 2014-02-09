package com.johnkapri.java3dgame.scene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import com.johnkapri.java3dgame.ShaderProgram;

public class Light {

	private byte enabled = GL_FALSE;
	public Vector3f position;
	private Vector3f direction;
	private Vector3f ambient;
	private Vector3f diffuse;
	private Vector3f specular;
	private Vector3f color; // Doesn't get sent to shader
	private float angle;
	private float shininess;
	private boolean flicker; // Not used
	private float flickeriness; // Not used
	
	public Light() {
		position = new Vector3f();
		ambient = new Vector3f();
		diffuse = new Vector3f();
		specular = new Vector3f();
		color = new Vector3f();
		direction = new Vector3f();
		shininess = 100;
	}
	
	public Light(Vector3f pos) {
		this();
		position = pos;
	}
	
	public Light(Vector3f pos, Vector3f color, float ambient, float diffuse, float specular) {
		this(pos);
		this.color = color;
		setComponent(this.ambient, ambient);
		setComponent(this.diffuse, diffuse);
		setComponent(this.specular, specular);
	}
	
	public Light(Vector3f pos, Vector3f direction, Vector3f color, float ambient, float diffuse, float specular, float angle) {
		this(pos, color, ambient, diffuse, specular);
		this.direction = direction;
		this.angle = angle;
	}
	
	public Light setEnabled(boolean b) {
		if(b) {
			this.enabled = GL_TRUE;
		} else {
			this.enabled = GL_FALSE;
		}
		return this;
	}
	
	private void setComponent(Vector3f component, float value) {
		component = new Vector3f(color.x * value, color.y * value, color.z * value);
	}
	
	public FloatBuffer getLightBuffer() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(15);
		position.store(buf);
		direction.store(buf);
		ambient.store(buf);
		diffuse.store(buf);
		specular.store(buf);
		//buf.put(shininess);
		//buf.put(angle);
		//buf.flip();
		
		ByteBuffer bbuf = BufferUtils.createByteBuffer(15 * 4);
		for(int i = 0; i < 15; i++) {
			bbuf.putFloat(buf.get(i));
		}
		//bbuf.put(enabled);
		bbuf.flip();
		
		return buf;
	}
	
	public ByteBuffer getLightBuffer2() {
		//int[] uniformIndices = new int[ShaderProgram.lightBlockNames.length];
		IntBuffer uniformIndices = BufferUtils.createIntBuffer(ShaderProgram.lightBlockNames.length);
		glGetUniformIndices(ShaderProgram.current.getId(), ShaderProgram.lightBlockNames, uniformIndices);
		
		IntBuffer uniformOffsets = BufferUtils.createIntBuffer(ShaderProgram.lightBlockNames.length);
		glGetActiveUniforms(ShaderProgram.current.getId(), uniformIndices, GL_UNIFORM_OFFSET, uniformOffsets);
		
		//int[] offset = uniformOffsets.array();
		
		ByteBuffer buf = BufferUtils.createByteBuffer(68);
		//buf.putFloat((float) enabled);
//		buf.putFloat(uniformOffsets.get(1), position.x);
//		buf.putFloat(uniformOffsets.get(1), position.y);
//		buf.putFloat(uniformOffsets.get(1), position.z);
//		buf.putFloat(uniformOffsets.get(2), direction.x);
//		buf.putFloat(uniformOffsets.get(2), direction.y);
//		buf.putFloat(uniformOffsets.get(2), direction.z);
//		buf.putFloat(uniformOffsets.get(3), ambient.x);
//		buf.putFloat(uniformOffsets.get(3), ambient.y);
//		buf.putFloat(uniformOffsets.get(3), ambient.z);
//		buf.putFloat(uniformOffsets.get(4), diffuse.x);
//		buf.putFloat(uniformOffsets.get(4), diffuse.y);
//		buf.putFloat(uniformOffsets.get(4), diffuse.z);
//		buf.putFloat(uniformOffsets.get(5), specular.x);
//		buf.putFloat(uniformOffsets.get(5), specular.y);
//		buf.putFloat(uniformOffsets.get(5), specular.z);
//		buf.putFloat(uniformOffsets.get(6), shininess);
//		buf.putFloat(uniformOffsets.get(7), angle);
		
		buf.position(uniformOffsets.get(0));
		buf.putFloat((float) enabled);	
		
		buf.position(uniformOffsets.get(1));
		buf.putFloat(position.x);
		buf.putFloat(position.y);
		buf.putFloat(position.z);
				
		buf.position(uniformOffsets.get(2));
		buf.putFloat(direction.x);
		buf.putFloat(direction.y);
		buf.putFloat(direction.z);
		
		buf.position(uniformOffsets.get(3));
		buf.putFloat(ambient.x);
		buf.putFloat(ambient.y);
		buf.putFloat(ambient.z);

		buf.position(uniformOffsets.get(4));
		buf.putFloat(diffuse.x);
		buf.putFloat(diffuse.y);
		buf.putFloat(diffuse.z);

		buf.position(uniformOffsets.get(5));
		buf.putFloat(specular.x);
		buf.putFloat(specular.y);
		buf.putFloat(specular.z);

		buf.position(uniformOffsets.get(6));
		buf.putFloat(shininess);
		buf.position(uniformOffsets.get(7));
		buf.putFloat(angle);
		
		buf.flip();
		
		return buf;
	}
	
	public void bufferLightTo(int bufferId) {
		IntBuffer uniformIndices = BufferUtils.createIntBuffer(ShaderProgram.lightBlockNames.length);
		glGetUniformIndices(ShaderProgram.current.getId(), ShaderProgram.lightBlockNames, uniformIndices);
		
		IntBuffer uniformOffsets = BufferUtils.createIntBuffer(ShaderProgram.lightBlockNames.length);
		glGetActiveUniforms(ShaderProgram.current.getId(), uniformIndices, GL_UNIFORM_OFFSET, uniformOffsets);
		
		
	}
}
