package com.github.limdingwen.SpaceCubes.Collision;

import org.lwjgl.util.vector.Vector3f;

public class Collision {
	// As per now, CubeLand collision only supports box-point collision. - 4/10/2012
	
	public static boolean isBoxColliding(BoxCollider c, Vector3f point) {
		if (c == null || point == null) {
			//System.out.println("Collision cannot find boxcollider! BOXCOLLIDER == NULL");
			return false;
		}
		
		float cX1 = Math.min(c.point1.x, c.point2.x);
		float cY1 = Math.min(c.point1.y, c.point2.y);
		float cZ1 = Math.min(c.point1.z, c.point2.z);
		float cX2 = Math.max(c.point1.x, c.point2.x);
		float cY2 = Math.max(c.point1.y, c.point2.y);
		float cZ2 = Math.max(c.point1.z, c.point2.z);
		
		float pX = point.x;
		float pY = point.y;
		float pZ = point.z;
		
		return ((pX >= cX1) && // X More
				(pX <= cX2) && // X Less
				(pY >= cY1) && // Y More
				(pY <= cY2) && // Y Less
				(pZ >= cZ1) && // Z More
				(pZ <= cZ2)); // Z Less
	}
}
