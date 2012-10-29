package com.github.limdingwen.SpaceCubes.Rendering;

import com.github.limdingwen.SpaceCubes.Main;
import org.lwjgl.opengl.GL11;

public class PrimitivesEngine
{
	public static float sideDarkness = 0.4F;
	public static float bottomDarkness = 0.5F;
	public static float shadowedDarkness = 0.3F;

	public static void init()
	{
	}

	public static void clColorCube(float size, float r, float g, float b, boolean front, boolean left, boolean right, boolean up, boolean bottom, boolean back, boolean shadowed, float offx, float offy, float offz)
	{
		// CRITICAL SECTION (1/60) OPEN GL INVOLVED
		
		GL11.glBegin(GL11.GL_QUADS);
		float rr = r;
		float gg = g;
		float bb = b;

		if (shadowed) {
			rr -= shadowedDarkness;
			gg -= shadowedDarkness;
			bb -= shadowedDarkness;
		}
		
		GL11.glColor3f(rr, gg, bb);

		if (up) {
			GL11.glVertex3f(-size + offx, size + offy, -size + offz);
			GL11.glVertex3f(size + offx, size + offy, -size + offz);
			GL11.glVertex3f(size + offx, size + offy, size + offz);
			GL11.glVertex3f(-size + offx, size + offy, size + offz);
			Main.verts += 4;
		}

		rr = r;
		gg = g;
		bb = b;
		
		rr -= sideDarkness;
		gg -= sideDarkness;
		bb -= sideDarkness;
		
		if (rr < 0.0F) rr = 0.0F;
		if (gg < 0.0F) gg = 0.0F;
		if (bb < 0.0F) bb = 0.0F;

		GL11.glColor3f(rr, gg, bb);

		if (left) {
			GL11.glVertex3f(-size + offx, size + offy, size + offz);
			GL11.glVertex3f(-size + offx, size + offy, -size + offz);
			GL11.glVertex3f(-size + offx, -size + offy, -size + offz);
			GL11.glVertex3f(-size + offx, -size + offy, size + offz);
			Main.verts += 4;
		}

		if (right) {
			GL11.glVertex3f(size + offx, size + offy, size + offz);
			GL11.glVertex3f(size + offx, size + offy, -size + offz);
			GL11.glVertex3f(size + offx, -size + offy, -size + offz);
			GL11.glVertex3f(size + offx, -size + offy, size + offz);
			Main.verts += 4;
		}

		if (back) {
			GL11.glVertex3f(-size + offx, size + offy, -size + offz);
			GL11.glVertex3f(size + offx, size + offy, -size + offz);
			GL11.glVertex3f(size + offx, -size + offy, -size + offz);
			GL11.glVertex3f(-size + offx, -size + offy, -size + offz);
			Main.verts += 4;
		}

		if (front) {
			GL11.glVertex3f(-size + offx, size + offy, size + offz);
			GL11.glVertex3f(size + offx, size + offy, size + offz);
			GL11.glVertex3f(size + offx, -size + offy, size + offz);
			GL11.glVertex3f(-size + offx, -size + offy, size + offz);

			Main.verts += 4;
		}

		rr = r;
		gg = g;
		bb = b;
		
		rr -= bottomDarkness;
		gg -= bottomDarkness;
		bb -= bottomDarkness;
		
		if (rr < 0.0F) rr = 0.0F;
		if (gg < 0.0F) gg = 0.0F;
		if (bb < 0.0F) bb = 0.0F;

		GL11.glColor3f(rr, gg, bb);

		if (bottom) {
			GL11.glVertex3f(-size + offx, -size + offy, -size + offz);
			GL11.glVertex3f(size + offx, -size + offy, -size + offz);
			GL11.glVertex3f(size + offx, -size + offy, size + offz);
			GL11.glVertex3f(-size + offx, -size + offy, size + offz);
			Main.verts += 4;
		}

		GL11.glEnd();

		Main.blocks += 1;
	}
}