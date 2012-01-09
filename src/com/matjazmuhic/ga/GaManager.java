package com.matjazmuhic.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.management.monitor.GaugeMonitor;

import com.matjazmuhic.Organism;
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
	private OrganismEvolution app;
	
	private List<OrganismTree> nextGen = new ArrayList<OrganismTree>();
	
	public GaManager(OrganismEvolution app)
	{
		this.populationSize = Integer.valueOf(PropertiesStore.getIstance().get("populationSize"));
		this.mutationChance = Integer.valueOf(PropertiesStore.getIstance().get("mutationChance"));
		this.elitePercentage = Integer.valueOf(PropertiesStore.getIstance().get("elitePercentage"));
		this.crossOverProbability = Integer.valueOf(PropertiesStore.getIstance().get("crossOverProbability"));
		this.app = app;
	}	
	
	public List<OrganismTree> step(int generationNum) 
	{	
		System.out.println("GA for generation "+generationNum);
		Random r = new Random();
		nextGen.clear();
		
		for(int i=0; i<=populationSize/2-1; i++)
		{
			System.out.println("Elite percentage...");
			if(i<((populationSize*elitePercentage)/100))
			{
				nextGen.add(app.getOrganismList().get(i).getOrganismTree());
			}
			OrganismTree parent1 = GeneticUtil.selection(OrganismRepository.getInstance().getGeneration(generationNum));
			OrganismTree parent2 = GeneticUtil.selection(OrganismRepository.getInstance().getGeneration(generationNum));
			
			System.out.println("Crossovers...");
			if(r.nextFloat()<crossOverProbability)
			{
				nextGen.add(GeneticUtil.crossover(parent1, parent2));
				nextGen.add(GeneticUtil.crossover(parent2, parent1));
			}
			
			System.out.println("Mutation");
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

		return nextGen;
	}	
	
}
