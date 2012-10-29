package com.github.limdingwen.SpaceCubes.Collision;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;

public class BoxCollider extends Collider {
	public Vector3f point1;
	public Vector3f point2;
	
	public BoxCollider(Vector3f p1, Vector3f p2) {
		if (p1 != null && p2 != null) { // Prevent NullException			
			point1 = p1;
			point2 = p2;
		}
		else {
			point1 = new Vector3f();
			point2 = new Vector3f();
		}
	}
	
	public void render(int glRenderMode) {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, glRenderMode);
		
		GL11.glLoadIdentity();
		
		RenderEngine.camera.lookThrough();
		
		Vector3f size =
				new Vector3f(
						point2.x - point1.x,
						point2.y - point1.y,
						point2.z - point1.z);
		
		GL11.glTranslatef(point1.x + size.x / 2, point1.y + size.y / 2, point1.z + size.z / 2);
		
	//	PrimitivesEngine.clColorCube(size.x, 0, 1, 0, true, true, true, true, true, true, false);
	}
}
