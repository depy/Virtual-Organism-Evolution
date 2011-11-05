package com.matjazmuhic.util;

public class Dimensions 
{
	public float x;
	public float y;
	public float z;
	
	public Dimensions()
	{
	}
	
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

	//Needed for xml serialization
	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY() 
	{
		return y;
	}

	public void setY(float y) 
	{
		this.y = y;
	}

	public float getZ() 
	{
		return z;
	}

	public void setZ(float z)
	{
		this.z = z;
	}
	
	
}
