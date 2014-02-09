package com.johnkapri.java3dgame;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ShaderProgram {

	public static ShaderProgram current;
	
	public static final String[] lightBlockNames = {
		"Light.enabled".toLowerCase(),
		"Light.position".toLowerCase(),
		"Light.direction".toLowerCase(),
		"Light.ambient".toLowerCase(),
		"Light.diffuse".toLowerCase(),
		"Light.specular".toLowerCase(),
		"Light.intensity".toLowerCase(),
		"Light.spotAngle".toLowerCase()
	};

	private String vertShader;
	private String fragShader;
	private int programId;
	private boolean loaded;
	private boolean inUse;

	public ShaderProgram(String vertShader, String fragShader) {
		this.fragShader = fragShader;
		this.vertShader = vertShader;
	}

	public int getId() {
		return programId;
	}

	public void use(boolean b) {
		if (b) {
			if (current != null) {
				current.inUse = false;
			}
			current = this;
			glUseProgram(getId());
			inUse = true;
		} else {
			current = null;
			glUseProgram(0);
			inUse = false;
		}
	}

	public void load() {
		if (!loaded) {
			try {
				programId = createShaderProgram(vertShader, fragShader);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
	}

	public void unload() {
		glDeleteProgram(getId());
		loaded = false;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isInUse() {
		return inUse;
	}

	public int getUniformPosition(String name) {
		if (loaded) {
			return glGetUniformLocation(getId(), name);
		}
		return 0;
	}

	private static int loadVertexShader(File f) throws IOException {
		int pointer = -1;
		long start = System.currentTimeMillis();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		StringBuilder source = new StringBuilder();

		// Read shader source
		String line;
		while ((line = reader.readLine()) != null) {
			source.append(line).append("\n");
		}
		reader.close();

		// Create and compile shader
		pointer = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(pointer, source);
		glCompileShader(pointer);

		// Check if the shader was compiles successfully
		if ((glGetShaderi(pointer, GL_COMPILE_STATUS)) == GL_FALSE) {
			System.err.println("Vertex shader " + f.getName()
					+ " didn't compile!");
			glDeleteShader(pointer);
			pointer = -1;
		} else {
			System.out.println("Loaded vertex shader \'"
					+ f.getParentFile().getName() + File.separator
					+ f.getName() + "\'");
			System.out.println("   It took:       "
					+ (System.currentTimeMillis() - start) + "ms");
			System.out.println("   Source length: "
					+ glGetShaderi(pointer, GL_SHADER_SOURCE_LENGTH));
		}

		return pointer;
	}

	private static int loadFragmentShader(File f) throws IOException {
		int pointer = -1;
		long start = System.currentTimeMillis();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		StringBuilder source = new StringBuilder();

		// Read shader source
		String line;
		while ((line = reader.readLine()) != null) {
			source.append(line).append("\n");
		}
		reader.close();

		// Create and compile shader
		pointer = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(pointer, source);
		glCompileShader(pointer);

		// Check if the shader was compiles successfully
		if ((glGetShaderi(pointer, GL_COMPILE_STATUS)) == GL_FALSE) {
			System.err.println("Fragment shader " + f.getName()
					+ " didn't compile!");
			glDeleteShader(pointer);
			pointer = -1;
		} else {
			System.out.println("Loaded fragment shader \'"
					+ f.getParentFile().getName() + File.separator
					+ f.getName() + "\'");
			System.out.println("   It took:       "
					+ (System.currentTimeMillis() - start) + "ms");
			System.out.println("   Source length: "
					+ glGetShaderi(pointer, GL_SHADER_SOURCE_LENGTH));
		}

		return pointer;
	}

	private static int createShaderProgram(String vertexShaderPath,
			String fragmentShaderPath) throws IOException {
		int pointer = 0;

		// Load and compile shaders
		int vShader = loadVertexShader(new File(vertexShaderPath));
		int fShader = loadFragmentShader(new File(fragmentShaderPath));

		// Create shader program
		pointer = glCreateProgram();
		glAttachShader(pointer, vShader);
		glAttachShader(pointer, fShader);
		glLinkProgram(pointer);
		glValidateProgram(pointer);

		return pointer;
	}
}
