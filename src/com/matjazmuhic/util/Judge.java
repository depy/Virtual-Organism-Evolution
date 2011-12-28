package com.matjazmuhic.util;

import java.util.UUID;

import com.jme3.math.Vector3f;
import com.matjazmuhic.Organism;
import com.matjazmuhic.OrganismEvolution;
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
			System.out.println("Distance: "+distance);
			organism.getOrganismTree().setScore(distance);
			String name = UUID.randomUUID().toString();
			OrganismRepository.writeToXml(organism.getOrganismTree(), name);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
}
