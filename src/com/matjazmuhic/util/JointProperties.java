package com.matjazmuhic.util;

public class JointProperties
{
	private SimpleVector axis1;
	private SimpleVector axis2;
	private float lowerLimit;
	private float upperLimit;
	private boolean collisions;
	private float motorTargetVelocity;
	private float motorMaxImpulse;
	private long timeA;
	private long timeB;
	
	public JointProperties(SimpleVector axis1, SimpleVector axis2, float lowerLimit, float upperLimit, boolean collisions, float motorTargetVelocity, float motorMaxImpulse, int timeA, int timeB) 
	{
		this.axis1 = axis1;
		this.axis2 = axis2;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.collisions = collisions;
		this.motorTargetVelocity = motorTargetVelocity;
		this.motorMaxImpulse = motorMaxImpulse;
		this.timeA = timeA;
		this.timeB = timeB;
	}

	public SimpleVector getAxis1()
	{
		return axis1;
	}

	public SimpleVector getAxis2() 
	{
		return axis2;
	}

	public float getLowerLimit() 
	{
		return lowerLimit;
	}

	public float getUpperLimit()
	{
		return upperLimit;
	}

	public boolean isCollisions() 
	{
		return collisions;
	}

	public float getMotorTargetVelocity()
	{
		return motorTargetVelocity;
	}

	public float getMotorMaxImpulse() 
	{
		return motorMaxImpulse;
	}

	public long getTimeA() 
	{
		return timeA;
	}

	public long getTimeB() 
	{
		return timeB;
	}
	
}
