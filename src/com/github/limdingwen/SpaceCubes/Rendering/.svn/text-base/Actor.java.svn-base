package com.github.limdingwen.SpaceCubes.Rendering;

import org.lwjgl.util.vector.Vector3f;

public class Actor {
	public Vector3f[] vertices;
	public Vector3f translation;
	public Vector3f rotation;
	public int glVerticeType;
	
	public Actor(Vector3f[] v, Vector3f t, Vector3f r, int g, Stage stage) {
		vertices = v;
		translation = t;
		rotation = r;
		glVerticeType = g;
		
		if (stage != null) addToStage(stage);
	}
	
	public void addToStage(Stage stage) {
		stage.addActor(this);
	}
	
	public void removeFromStage(Stage stage) {
		stage.removeActor(this);
	}
}