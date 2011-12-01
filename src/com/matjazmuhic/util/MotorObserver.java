package com.matjazmuhic.util;

import java.util.Observable;
import java.util.Observer;

import com.jme3.bullet.joints.HingeJoint;

public class MotorObserver implements Observer
{
	HingeJoint hingeJoint;
	JointProperties jointProperties;
	
	public MotorObserver(HingeJoint hj, JointProperties jp)
	{
		this.hingeJoint = hj;
		this.jointProperties = jp;
	}
	
	@Override
	public void update(Observable observable, Object obj)
	{
		if(jointProperties.getTimeA()==(Integer)obj)
		{
			System.out.println("enabling motor");
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity(), jointProperties.getMotorMaxImpulse());
		}
		else if(jointProperties.getTimeB()==(Integer)obj)
		{
			System.out.println("reverse enabling motor");
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity()*(-1), jointProperties.getMotorMaxImpulse());
		}
	}

}