package com.johnkapri.java3dgame.gfx.model;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.johnkapri.java3dgame.ShaderProgram;

public class Model {
	List<Vector3f> vertices = new ArrayList<Vector3f>();
	List<Vector2f> textures = new ArrayList<Vector2f>();
	List<Vector3f> normals = new ArrayList<Vector3f>();
	private List<Mesh> meshes = new ArrayList<Mesh>();
	private int[] renderListPointers;
	// private int[] vboHandle;
	private Vector3f[] colors;
	private boolean loaded = false;
	boolean vboLoaded;
	boolean renderListLoaded;
	int vao;

	public boolean isLoaded() {
		return loaded;
	}

	public static Model loadModelFromOBJ(File f) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Model model = new Model();

		long start = System.currentTimeMillis();
		String line;
		int failedLinesCounter = 0;
		int meshes = 0;
		int verticies = 0;
		int faces = 0;
		int texCoords = 0;
		int normals = 0;
		Mesh currentMesh = new Mesh();
		currentMesh.color = new Vector3f(0.8f, 0.2f, 1.0f);
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				// Don't deal with comments
			} else if (line.startsWith("o ")) {
				if (currentMesh != null) {
					model.meshes.add(currentMesh);
				}
				currentMesh = new Mesh();
				currentMesh.color = parseColor(line.substring(2, line.length()));
				meshes++;
			} else if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.vertices.add(new Vector3f(x, y, z));
				verticies++;
			} else if (line.startsWith("vt ")) {
				float s = Float.valueOf(line.split(" ")[1]);
				float t = Float.valueOf(line.split(" ")[2]);
				model.textures.add(new Vector2f(s, t));
				texCoords++;
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.normals.add(new Vector3f(x, y, z));
				normals++;
			} else if (line.startsWith("f ")) {
				String[] s = line.split(" ");
				Vector3f vertexIndices = new Vector3f(Float.valueOf(s[1]
						.split("/")[0]), Float.valueOf(s[2].split("/")[0]),
						Float.valueOf(s[3].split("/")[0]));
				Face face = new Face(vertexIndices);
				if (s[1].split("/")[1].length() > 0) {
					Vector3f textureIndices = new Vector3f(Float.valueOf(s[1]
							.split("/")[1]), Float.valueOf(s[2].split("/")[1]),
							Float.valueOf(s[3].split("/")[1]));
					face.addTextureIndices(textureIndices);
				}
				if (s[1].split("/")[2].length() > 0) {
					Vector3f normalIndices = new Vector3f(Float.valueOf(s[1]
							.split("/")[2]), Float.valueOf(s[2].split("/")[2]),
							Float.valueOf(s[3].split("/")[2]));
					face.addNormalIndices(normalIndices);
				}
				currentMesh.faces.add(face);
				faces++;
			} else {
				failedLinesCounter++;
			}
		}
		if (currentMesh != null) {
			model.meshes.add(currentMesh);
		}

		System.out.println("Parsed model file \'" + f.getParentFile().getName()
				+ File.separator + f.getName() + "\'");
		System.out.println("   It took " + (System.currentTimeMillis() - start)
				+ "ms for");
		System.out.println("   " + meshes + " meshes with");
		System.out.println("   Vertices: " + verticies);
		System.out.println("   TexCoord: " + texCoords);
		System.out.println("   Normals:  " + normals);
		System.out.println("   Faces:    " + faces);

		if (failedLinesCounter > 0) {
			System.out.println("   and " + failedLinesCounter
					+ " lines could not be parsed!");
		}
		reader.close();

		model.colors = new Vector3f[model.meshes.size()];
		for (int i = 0; i < model.meshes.size(); i++) {
			model.colors[i] = model.meshes.get(i).color;
			// System.out.println(model.meshes.get(i).color.toString());
		}

		// start = System.currentTimeMillis();
		// System.out.println("   Baking " + model.meshes.size() +
		// " meshes...");
		// model.createRenderLists();
		// System.out.println("   Done in " + (System.currentTimeMillis() -
		// start)
		// + "ms!");
		start = System.currentTimeMillis();
		System.out.println("   Creating VBOs...");
		//createVBOs(model);
		System.out.println("   Done in " + (System.currentTimeMillis() - start)
				+ "ms!");
		
		//model.freeRessources();

		model.loaded = true;
		return model;
	}

	private void createRenderLists() {
		renderListPointers = new int[meshes.size()];

		for (int i = 0; i < meshes.size(); i++) {
			renderListPointers[i] = glGenLists(1);
			glNewList(renderListPointers[i], GL_COMPILE);
			meshes.get(i).renderMesh(this);
			glEndList();
		}
		renderListLoaded = true;
	}
	
	private void freeRessources() {
		this.vertices.clear();
		this.vertices = null;
		this.normals.clear();
		this.normals = null;
		this.textures.clear();
		this.textures = null;
	}

	public static void createVBOs(Model model) {
		// model.vboHandle = new int[model.meshes.size()];

		for (int i = 0; i < model.meshes.size(); i++) {
			Mesh m = model.meshes.get(i);
			m.vboHandle = glGenBuffers();
			FloatBuffer data = BufferUtils
					.createFloatBuffer(m.faces.size() * 9 * 4);
			for (Face f : m.faces) {
				data.put(asFloats(model.vertices.get((int) f.vertex.x - 1)));
				data.put(asFloats(model.normals.get((int) f.normal.x - 1)));
				data.put(asFloats(model.vertices.get((int) f.vertex.y - 1)));
				data.put(asFloats(model.normals.get((int) f.normal.y - 1)));
				data.put(asFloats(model.vertices.get((int) f.vertex.z - 1)));
				data.put(asFloats(model.normals.get((int) f.normal.z - 1)));
			}
			data.flip();
			glBindBuffer(GL_ARRAY_BUFFER, m.vboHandle);
			glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
			model.colors[i] = m.color;
		}
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		model.vao = glGenVertexArrays();
		model.vboLoaded = true;
	}

	private static float[] asFloats(Vector3f v) {
		return new float[] { v.x, v.y, v.z };
	}

	public void render() {
		if (vboLoaded) {
			for (int i = 0; i < meshes.size(); i++) {
				glBindVertexArray(vao);
				glBindBuffer(GL_ARRAY_BUFFER, meshes.get(i).vboHandle);
				glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
				glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				if (ShaderProgram.current != null) {
					glUniform3f(
							ShaderProgram.current.getUniformPosition("color"),
							colors[i].x, colors[i].y, colors[i].z);
				}
				glDrawArrays(GL_TRIANGLES, 0, meshes.get(i).faces.size()
						* (3 + 3));
			}
		} else if (renderListLoaded) {
			for (int i = 0; i < renderListPointers.length; i++) {
				glColor3f(colors[i].x, colors[i].y, colors[i].z);
				glCallList(renderListPointers[i]);
			}
		} else {
			createVBOs(this);
		}
	}

	private static Vector3f parseColor(String s) {
		Vector3f color = new Vector3f(0.8f, 0.8f, 0.8f);
		if (s != null) {
			s = s.split("_")[0];
			s.trim().toLowerCase();
			switch (s) {
			case "blue":
				color = new Vector3f(0.2f, 0.2f, 1.0f);
				break;
			case "brown":
				color = new Vector3f(0.6f, 0.4f, 0.12f);
				break;
			case "green":
				color = new Vector3f(0.1f, 0.8f, 0.0f);
				break;
			case "highlight":
				color = new Vector3f(1.0f, 1.0f, 0.1f);
				break;
			case "turcuise":
				color = new Vector3f(0.2f, 0.8f, 1.0f);
				break;
			}
		}
		return color;
	}
}

class Mesh {
	List<Face> faces = new ArrayList<Face>();
	Vector3f color;
	int vboHandle;

	public void renderMesh(Model m) {
		glBegin(GL_TRIANGLES);
		for (Face f : faces) {
			if (f.hasNormals()) {
				Vector3f n1 = m.normals.get((int) f.normal.x - 1);
				glNormal3f(n1.x, n1.y, n1.z);
			}
			if (f.hasTexCoords()) {
				Vector2f t1 = m.textures.get((int) f.texture.x - 1);
				glTexCoord2f(t1.x, t1.y);
			}
			Vector3f v1 = m.vertices.get((int) f.vertex.x - 1);
			glVertex3f(v1.x, v1.y, v1.z);

			if (f.hasNormals()) {
				Vector3f n2 = m.normals.get((int) f.normal.y - 1);
				glNormal3f(n2.x, n2.y, n2.z);
			}
			if (f.hasTexCoords()) {
				Vector2f t2 = m.textures.get((int) f.texture.x - 1);
				glTexCoord2f(t2.x, t2.y);
			}
			Vector3f v2 = m.vertices.get((int) f.vertex.y - 1);
			glVertex3f(v2.x, v2.y, v2.z);

			if (f.hasNormals()) {
				Vector3f n3 = m.normals.get((int) f.normal.z - 1);
				glNormal3f(n3.x, n3.y, n3.z);
			}
			if (f.hasTexCoords()) {
				Vector2f t3 = m.textures.get((int) f.texture.x - 1);
				glTexCoord2f(t3.x, t3.y);
			}
			Vector3f v3 = m.vertices.get((int) f.vertex.z - 1);
			glVertex3f(v3.x, v3.y, v3.z);
		}
		glEnd();
	}
}

class Face {
	Vector3f vertex;
	Vector3f texture;
	Vector3f normal;

	public Face(Vector3f vertex) {
		this.vertex = vertex;
	}

	boolean hasTexCoords() {
		return texture != null;
	}

	boolean hasNormals() {
		return normal != null;
	}

	Face addNormalIndices(Vector3f normal) {
		this.normal = normal;
		return this;
	}

	Face addTextureIndices(Vector3f texture) {
		this.texture = texture;
		return this;
	}
}
