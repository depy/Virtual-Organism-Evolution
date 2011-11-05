package com.matjazmuhic.util;

public class SimpleVector 
{
	private float x;
	private float y;
	private float z;
	
	public SimpleVector()
	{
	}
	
	public SimpleVector(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() 
	{
		return x;
	}

	public float getY() 
	{
		return y;
	}

	public float getZ() 
	{
		return z;
	}
	
	//Needed for xml serialization
	public void setX(float x) 
	{
		this.x = x;
	}

	public void setY(float y) 
	{
		this.y = y;
	}

	public void setZ(float z) 
	{
		this.z = z;
	}
	
}
