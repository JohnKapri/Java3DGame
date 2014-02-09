package com.johnkapri.java3dgame.gfx.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import com.johnkapri.java3dgame.Main;

public class ModelManager {
	
	private Map<String, Model> models = new HashMap<String, Model>();
	private boolean glContextReady = false;
	private List<String> que = new ArrayList<String>();
	
	public ModelManager() {
		File[] fs = getModelDir().listFiles();
		for(File f : fs) {
			if(f.getName().endsWith(".obj")) {
				//new ModelLoader(this, f.getName().replaceAll(".obj", ""));
			}
		}
	}
	
	public void loadModel(String name) {
		
		if(glContextReady) {
			new ModelLoader(this, name);
		} else {
			que.add(name);
		}
	}
	
	public void notifyAboutGL(boolean b) {
		glContextReady = b;
		if(b) {
			for(String s : que) {
				loadModel(s);
			}
			que.clear();
		}
	}
	
	public Model getModel(String name) {
		if(!hasModel(name)) {
			return null;
		}
		return models.get(name);
	}
		
	private synchronized void addModel(String s, Model m) {
		//Model.createVBOs(m);
		models.put(s, m);
	}
	
	public boolean hasModel(String s) {
		return models.containsKey(s);
	}
	
	public static File getModelDir() {
		return new File("res/models/");
	}
	
	private class ModelLoader implements Runnable{
		private File f;
		private String name;
		private ModelManager manager;
		private Thread t;
		
		public ModelLoader(ModelManager m, String s) {
			name = s;
			manager = m;
			if(m.hasModel(s)) {
				return;
			}
			if(!s.endsWith(".obj")) {
				s = s + ".obj";
			}
			f = new File(ModelManager.getModelDir(), s);
			if(!f.exists()) {
				return;
			}
			t = new Thread(this, Main.NAME + "_MODELLOADER_" + f.getName());
			t.start();
		}

		@Override
		public void run() {
			try {
				manager.addModel(name, Model.loadModelFromOBJ(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
