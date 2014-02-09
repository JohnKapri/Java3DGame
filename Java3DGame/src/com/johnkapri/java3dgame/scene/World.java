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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.johnkapri.java3dgame.ShaderProgram;
import com.johnkapri.java3dgame.entity.Entity;
import com.johnkapri.java3dgame.entity.EntityLight;

public class World {

	public static final int MAX_ENTITY_COUNT = 100;
	public static final int MAX_LIGHT_SOURCES = 10;

	private List<Entity> entities = new CopyOnWriteArrayList<Entity>();
	private Light[] lights;
	private Entity light;
	public Vector3f lightPos = new Vector3f();
	
	public World() {
		lights = new Light[MAX_LIGHT_SOURCES];
		for(int i = 0; i < lights.length; i++) {
			lights[i] = new Light();
		}
		lights[0] = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 0, 1), 0.2f, 1.0f, 0.4f).setEnabled(true);
	}

	public void tick(float delta) {
		if (light == null) {
			light = new EntityLight(0, 0, 0);
		}
		light.position = lightPos;
		lights[0].position = lightPos;
		for (Entity e : entities) {
			e.tick(this, delta);
		}
		if (ShaderProgram.current != null) {
			glUniform3f(ShaderProgram.current.getUniformPosition("light_pos"),
					lightPos.x, lightPos.y, lightPos.z);
		}
		updateLightUBO();
	}

	public void render() {
		for (Entity e : entities) {
			e.render();
		}
		light.render();
	}

	public boolean addEntity(Entity e) {
		if (entities.contains(e) || entities.size() >= MAX_ENTITY_COUNT) {
			return false;
		}
		entities.add(e);
		return true;
	}

	public boolean removeEntity(Entity e) {
		if (entities.contains(e)) {
			entities.remove(e);
			return true;
		}
		return false;
	}

	public Entity[] getEntitiesWithin(Vector3f position, float radius) {
		return getEntitiesWithinExcept(null, position, radius);
	}

	public Entity[] getEntitiesWithinExcept(Entity es, Vector3f position,
			float radius) {
		List<Entity> ents = new ArrayList<Entity>();
		for (Entity e : entities) {
			if ((es == null || (es != null && es != e))
					&& (Vector3f.sub(e.position, position, null)).length() <= radius) {
				ents.add(e);
			}
		}
		return (Entity[]) ents.toArray();
	}
	
	public Entity getClosestEntity(Entity ent) {
		float closestR = -1;
		Entity closest = null;
		for(Entity e : entities) {
			if(e != ent) {
				float distance = (Vector3f.sub(e.position, ent.position, null)).length();
				if(closestR == -1 || distance <= closestR) {
					closest = e;
					closestR = distance;
				}
			}
		}
		
		return closest;
	}
	
	public float getDistance(Entity e1, Entity e2) {
		return Vector3f.sub(e1.position, e2.position, null).length();
	}

	public boolean hasEntity(Entity e) {
		if (entities.contains(e)) {
			return true;
		}
		return false;
	}
	
	private void updateLightUBO() {
		int ubo = glGenBuffers();
		glBindBuffer(GL_UNIFORM_BUFFER, ubo);
		int light = glGetUniformBlockIndex(ShaderProgram.current.getId(), "Light");
		glUniformBlockBinding(ShaderProgram.current.getId(), light, 2);
		glBindBufferBase(GL_UNIFORM_BUFFER, 2, ubo);
		glBufferData(GL_UNIFORM_BUFFER, lights[0].getLightBuffer2(), GL_STREAM_READ);
	}
}
