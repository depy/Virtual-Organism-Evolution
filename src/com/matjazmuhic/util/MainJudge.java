package com.matjazmuhic.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jme3.math.Vector3f;
import com.matjazmuhic.Organism;
import com.matjazmuhic.OrganismEvolution;
import com.matjazmuhic.persistence.OrganismRepository;
import com.matjazmuhic.persistence.PropertiesStore;

public class MainJudge implements Callable<Float>
{
	Map<String, Vector3f> performanceData;
	OrganismEvolution app;
	int generationNum;
	
	public MainJudge(OrganismEvolution app, int generationNum)
	{
		performanceData = new HashMap<String, Vector3f>();
		this.app = app;
		this.generationNum = generationNum;
	}

	@Override
	public Float call() throws Exception
	{
		try
		{
			for(Organism o: app.getOrganismList())
			{
				Vector3f startPosition  = (o.getOrganismJme().getNode().getWorldBound()).getCenter().clone();
				performanceData.put(o.getOrganismTree().getName(), startPosition);
			}
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("warmupTime")));
			//app.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, -400.0f, 0.0f));
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("performanceTime")));
			for(Organism o: app.getOrganismList())
			{
				Vector3f endPosition = o.getOrganismJme().getNode().getWorldBound().getCenter().clone();
				Vector3f startPosition = performanceData.get(o.getOrganismTree().getName());
				float score  = endPosition.distance(startPosition);
				
				o.getOrganismTree().addScore(score);
				OrganismRepository.getInstance().save(o.getOrganismTree(), generationNum);
			}
			
			performanceData.clear();
			//app.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			System.out.println("Interrupted!");
		}
		
		return null;
	}

}
