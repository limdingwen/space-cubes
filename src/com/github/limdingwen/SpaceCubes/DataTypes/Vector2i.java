package com.github.limdingwen.SpaceCubes.DataTypes;

public class Vector2i {
	public int x;
	public int y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2i() {
		x = 0;
		y = 0;
	}
	
	public String toString() {
		return x + "," + y;
	}
	
	public boolean equals(Vector2i vector) {
		return (x == vector.x && y == vector.y);
	}
}
