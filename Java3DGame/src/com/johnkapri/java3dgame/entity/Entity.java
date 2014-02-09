package com.johnkapri.java3dgame.entity;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.johnkapri.java3dgame.scene.World;

public abstract class Entity {

	public Vector3f position;
	private Vector3f rotation;
	private Vector3f velocity;
	private Vector3f acceleration;
	
	protected Entity() {
		position = new Vector3f();
		rotation = new Vector3f();
		velocity = new Vector3f();
		acceleration = new Vector3f();
	}
	
	protected Entity(float x, float y, float z) {
		this();
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void tick(World w, float delta) {
		
	}
	
	public abstract void render();
	
	public void onRemove() {
		
	}
	
	protected void moveWithRotation(Vector3f movement) {
		//TODO: Create actual movement
	}
	
	public float x() {
		return position.x;
	}
	
	public float y() {
		return position.y;
	}
	
	public float z() {
		return position.z;
	}
	
	public void move(float dx, float dy, float dz) {
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}
	
	public Matrix4f getModelMatrix() {
		Matrix4f m = new Matrix4f();
		Vector3f v = new Vector3f();
		position.negate(v);
		m.translate(position);
		return m;
	}
}
