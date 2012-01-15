package com.matjazmuhic.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.matjazmuhic.OrganismEvolution;
import com.matjazmuhic.persistence.OrganismRepository;
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.tree.OrganismTree;

public class GaManager
{
	private int populationSize;
	private int	mutationChance;
	private int elitePercentage;
	private int crossOverProbability;
	private List<OrganismTree> nextGen = new ArrayList<OrganismTree>();
	
	public GaManager(OrganismEvolution app)
	{
		this.populationSize = Integer.valueOf(PropertiesStore.getIstance().get("populationSize"));
		this.mutationChance = Integer.valueOf(PropertiesStore.getIstance().get("mutationChance"));
		this.elitePercentage = Integer.valueOf(PropertiesStore.getIstance().get("elitePercentage"));
		this.crossOverProbability = Integer.valueOf(PropertiesStore.getIstance().get("crossOverProbability"));
	}	
	
	public List<OrganismTree> step(int generationNum) 
	{	
		Random r = new Random();
		nextGen.clear();
		
		List<OrganismTree> currentGeneration = OrganismRepository.getInstance().getGeneration(generationNum);
		Collections.sort(currentGeneration);
		
		int numElites = populationSize*elitePercentage/100;
		for(int i=0; i<numElites; i++)
		{			
			nextGen.add(currentGeneration.get(i));
		}
		
		for(int i=0; i<(populationSize/2)-numElites; i++)
		{
				OrganismTree parent1 =  GeneticUtil.selection(currentGeneration);
				OrganismTree parent2 =  GeneticUtil.selection(currentGeneration);
				
				if(r.nextFloat()<crossOverProbability)
				{
					nextGen.add(GeneticUtil.crossover(parent1, parent2));
					nextGen.add(GeneticUtil.crossover(parent2, parent1));
				}
				
				if(r.nextFloat()<mutationChance)
				{
					nextGen.add(GeneticUtil.mutate(parent1));
					nextGen.add(GeneticUtil.mutate(parent2));
				}
				
				if(nextGen.size()>=populationSize)
				{
					break;
				}
		}
		
		if(nextGen.size()>populationSize)
		{
			nextGen.remove(nextGen.size()-1);
		}
		
		for(int i=0; i<numElites; i++)
		{			
			nextGen.add(currentGeneration.get(i));
		}

		return nextGen;
	}	
	
}
