package com.matjazmuhic.util;

import java.util.UUID;

import com.jme3.math.Vector3f;
import com.matjazmuhic.Organism;
import com.matjazmuhic.persistence.OrganismRepository;

public class Judge implements Runnable
{
	Vector3f startPosition;
	Vector3f endPosition;
	Organism organism;
	
	public Judge(Organism organism)
	{
		this.organism = organism;
	}
	
	@Override
	public void run() 
	{
		try 
		{
			Thread.sleep(6000);
			startPosition = (organism.getOrganismJme().getNode().getWorldBound()).getCenter().clone();
			Thread.sleep(15000);
			endPosition = (organism.getOrganismJme().getNode().getWorldBound()).getCenter().clone();
			float distance = endPosition.distance(startPosition);
			organism.getOrganismTree().setScore(distance);
			String name = UUID.randomUUID().toString();
			OrganismRepository.getInstance().save(organism.getOrganismTree(), 1);
			System.out.println(name+" scored: "+distance);
		}
		catch (InterruptedException e) 
		{
			//Expected
		}
	}
	
}
