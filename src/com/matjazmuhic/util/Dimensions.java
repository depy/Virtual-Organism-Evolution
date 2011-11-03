package com.matjazmuhic.util;

public class Dimensions 
{
	public float x;
	public float y;
	public float z;
	
	public Dimensions(float width, float height, float length)
	{
		this.x = width;
		this.y = height;
		this.z = length;
	}

	@Override
	public String toString()
	{
		return "w: "+this.x+"\th: "+this.y+"\tl: "+this.z;
		
	}
}
