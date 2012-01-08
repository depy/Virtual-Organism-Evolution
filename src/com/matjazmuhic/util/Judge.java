package com.matjazmuhic.util;

import java.util.UUID;

import javax.management.RuntimeErrorException;

import com.jme3.math.Vector3f;
import com.matjazmuhic.Organism;
import com.matjazmuhic.persistence.OrganismRepository;
import com.matjazmuhic.persistence.PropertiesStore;

public class Judge implements Runnable
{
	Vector3f startPosition;
	Vector3f endPosition;
	Organism organism;
	int generationNum;
	
	public Judge(Organism organism, int generationNum)
	{
		this.organism = organism;
		this.generationNum = generationNum;
	}
	
	@Override
	public void run() 
	{
		try 
		{
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("warmupTime")));
			startPosition = (organism.getOrganismJme().getNode().getWorldBound()).getCenter().clone();
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("performanceTime")));
			endPosition = (organism.getOrganismJme().getNode().getWorldBound()).getCenter().clone();
			float distance = endPosition.distance(startPosition);
			if(distance==0.0f)
			{
				throw new Error(startPosition.toString()+" "+endPosition.toString());
			}
			organism.getOrganismTree().setScore(distance);
			String name = UUID.randomUUID().toString();
			OrganismRepository.getInstance().save(organism.getOrganismTree(), generationNum);
			System.out.println(name+" scored: "+distance);
		}
		catch (InterruptedException e) 
		{
			//Expected
		}
	}
	
}
