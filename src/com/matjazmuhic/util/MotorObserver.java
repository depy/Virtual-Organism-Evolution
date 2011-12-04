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
		hingeJoint.getBodyA().activate();
		hingeJoint.getBodyB().activate();
		if(jointProperties.getTimeA()==(Integer)obj)
		{
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity(), jointProperties.getMotorMaxImpulse());
		}
		else if(jointProperties.getTimeB()==(Integer)obj)
		{
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity()*(-1), jointProperties.getMotorMaxImpulse());
		}
	}

}