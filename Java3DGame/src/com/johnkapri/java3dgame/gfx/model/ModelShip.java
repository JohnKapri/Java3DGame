package com.johnkapri.java3dgame.gfx.model;

import java.awt.geom.Line2D;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ModelShip {

	private Line2D[] lines = new Line2D[9];
	
	public ModelShip() {
		lines[0] = new Line2D.Float(0, 3 / 4, -1 / 4, 1 / 4);
		lines[1] = new Line2D.Float(-1 / 4, 1 / 4, -3 / 4, 1 / 4);
		lines[2] = new Line2D.Float(-3 / 4, 1 / 4, -4 / 4, -2 / 4);
		lines[3] = new Line2D.Float(-4 / 4, -2 / 4, -2.5F / 4, -1 / 4);
		lines[4] = new Line2D.Float(-2.5F / 4, -1 / 4, 2.5F / 4, -1 / 4);
		lines[5] = new Line2D.Float(0, 3 / 4, 1 / 4, 1 / 4);
		lines[6] = new Line2D.Float(1 / 4, 1 / 4, 3 / 4, 1 / 4);
		lines[7] = new Line2D.Float(3 / 4, 1 / 4, 4 / 4, -2 / 4);
		lines[8] = new Line2D.Float(4 / 4, -2 / 4, 2.5F / 4, -1 / 4);
	}
	
	public void renderShip(float x, float y, float tilt, float scale) {
//		GL11.glTranslatef(x, y, 0);
//		GL11.glRotatef(tilt, 0, 0.5F, 0.5F);
//		GL11.glScalef(scale, scale, scale);
		
		for(Line2D l : lines) {
			drawLine(l.getP1().getX(), l.getP1().getY(), l.getP2().getX(), l.getP2().getY());
		}
		
//		GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		
//		GL11.glTranslatef(-x, -y, 0);
//		GL11.glRotatef(-tilt, 0, 0.5F, 0.5F);
//		GL11.glScalef(1/scale, 1/scale, 1/scale);
	}
	
	public static void drawLine(double x1, double d, double e, double f) {
		GL11.glBegin(GL11.GL_LINE);
		//GL11.glLineWidth(10.0F);
		System.out.println("Drawing line...");
		GL11.glVertex2d(x1, d);
		GL11.glVertex2d(e, f);
		
		GL11.glEnd();
	}
}
