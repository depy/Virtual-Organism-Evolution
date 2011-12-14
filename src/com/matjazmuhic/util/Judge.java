package com.matjazmuhic.util;

import com.jme3.math.Vector3f;
import com.matjazmuhic.OrganismEvolution;

public class Judge implements Runnable
{
	Vector3f startPosition;
	Vector3f endPosition;
	OrganismEvolution app;
	
	public Judge(OrganismEvolution app)
	{
		this.app = app;
	}
	
	@Override
	public void run() 
	{
		try 
		{
			System.out.println("Judge running");
			Thread.sleep(6000);
			app.setStartPosition();
			startPosition = this.app.getStartPosition();
			System.out.println("Running test...");
			Thread.sleep(15000);
			endPosition = app.getOrganismPosition();
			float distance = endPosition.distance(startPosition);
			app.setDistance(distance);
			app.getOrganism().getOrganismTree().setScore(distance);
			
			Util.write(app.getOrganism().getOrganismTree(), "test-judge-write-1.xml");
			
			app.destroy();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
}
