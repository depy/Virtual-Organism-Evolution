package com.matjazmuhic.physics;

import com.matjazmuhic.util.SimpleVector;

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
	private int timePeriod;
	private String name;
	
	public JointProperties() 
	{
	}
	
	public JointProperties(SimpleVector axis1, SimpleVector axis2, float lowerLimit, float upperLimit, boolean collisions, float motorTargetVelocity, float motorMaxImpulse, int timeA, int timeB, int timePeriod, String name) 
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
		this.timePeriod = timePeriod;
		this.name = name;
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

	public int getTimePeriod() 
	{
		return timePeriod;
	}

	//Needed for xml serialization
	public void setAxis1(SimpleVector axis1)
	{
		this.axis1 = axis1;
	}

	public void setAxis2(SimpleVector axis2) 
	{
		this.axis2 = axis2;
	}

	public void setLowerLimit(float lowerLimit) 
	{
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(float upperLimit) 
	{
		this.upperLimit = upperLimit;
	}

	public void setCollisions(boolean collisions) 
	{
		this.collisions = collisions;
	}

	public void setMotorTargetVelocity(float motorTargetVelocity) 
	{
		this.motorTargetVelocity = motorTargetVelocity;
	}

	public void setMotorMaxImpulse(float motorMaxImpulse) 
	{
		this.motorMaxImpulse = motorMaxImpulse;
	}

	public void setTimeA(long timeA) 
	{
		this.timeA = timeA;
	}

	public void setTimeB(long timeB) 
	{
		this.timeB = timeB;
	}
	
	public void setTimePeriod(int timePeriod)
	{
		this.timePeriod = timePeriod;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
}
