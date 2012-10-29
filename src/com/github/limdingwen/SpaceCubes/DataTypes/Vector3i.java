package com.github.limdingwen.SpaceCubes.DataTypes;

public class Vector3i {
	public int x;
	public int y;
	public int z;
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return "Vector3i[" + x + "," + y + "," + z + "]";
	}
}
