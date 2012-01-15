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
				performanceData.put(o.getName(), startPosition);
			}
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("warmupTime")));
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("performanceTime")));
			for(Organism o: app.getOrganismList())
			{
				Vector3f endPosition = o.getOrganismJme().getNode().getWorldBound().getCenter().clone();
				Vector3f startPosition = performanceData.get(o.getName());
				float score  = endPosition.distance(startPosition);
				o.getOrganismTree().setScore(score);
				OrganismRepository.getInstance().save(o.getOrganismTree(), generationNum);
			}
			
			performanceData.clear();
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			
		}
		
		return null;
	}

}
