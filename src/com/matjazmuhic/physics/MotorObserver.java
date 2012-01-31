package com.matjazmuhic.physics;

import java.util.Observable;
import java.util.Observer;

import com.jme3.bullet.joints.HingeJoint;
import com.matjazmuhic.persistence.PropertiesStore;

public class MotorObserver implements Observer
{
	private HingeJoint hingeJoint;
	private JointProperties jointProperties;
	private int timerCounter;
	private int timerInterval;
	public MotorObserver(HingeJoint hj, JointProperties jp)
	{
		this.hingeJoint = hj;
		this.jointProperties = jp;
		this.timerCounter = 0;
		this.timerInterval = Integer.valueOf(PropertiesStore.getIstance().get("timerTimeInterval"));
	}
	
	@Override
	public void update(Observable observable, Object obj)
	{	
		timerCounter++;
		
		if(timerInterval*timerCounter>=jointProperties.getTimePeriod())
		{
			timerCounter = 0;
		}
		
		if(jointProperties.getTimeA()==timerCounter)
		{
			hingeJoint.getBodyA().activate();
			hingeJoint.getBodyB().activate();
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity(), jointProperties.getMotorMaxImpulse());
		}
		else if(jointProperties.getTimeB()==timerCounter)
		{
			hingeJoint.getBodyA().activate();
			hingeJoint.getBodyB().activate();
			hingeJoint.enableMotor(true, jointProperties.getMotorTargetVelocity()*(-1), jointProperties.getMotorMaxImpulse());
		}

	}

}