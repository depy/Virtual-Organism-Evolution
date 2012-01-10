package com.matjazmuhic.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.matjazmuhic.Organism;
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.IBlockNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Util;

public class GeneticUtil 
{
	private static int randomModifier = Integer.parseInt(PropertiesStore.getIstance().get("randomModifier"));
	private static Random r = new Random();
	
	public static synchronized OrganismTree crossover(OrganismTree tree1, OrganismTree tree2)
	{
		BlockNode node = getRandomNode(tree1);	
		int node1pos = node.getPosition();
		IBlockNode parent = node.getParent();
		BlockNode node2 = getRandomSubTree(tree2);
		node2.setParent(parent);
		node2.setRoot(node.getRoot());
		System.out.println("parent.getChildren()[node1pos] => "+parent.getChildren()[node1pos]);
		System.out.println("node2 => "+node2);
		parent.getChildren()[node1pos] = node2;
		
		return tree1;
	}
	
	public static synchronized OrganismTree mutate(OrganismTree tree)
	{
		BlockNode node = getRandomNode(tree);
		
		while(node.hasChildren())
		{
			node = getRandomNode(tree);
		}
		
		//Mutate...
		//node.remove();
		node.setJointProperties(Util.getRandomJointProps());
		
		return tree;
	}
	
	private static synchronized BlockNode getRandomSubTree(OrganismTree organismTree)
	{
		BlockNode node = getRandomNode(organismTree);
		
		node.setRoot(null);
		node.setParent(null);
		
		return node;
		
	}
	
	private static synchronized BlockNode getRandomNode(OrganismTree tree)
	{
		BlockNode node = getNextRandomNode((BasicNode)tree.getRoot());
		
		int rnd = r.nextInt(randomModifier);
		while(rnd!=0)
		{
			if(node.hasChildren())
			{
				node = getNextRandomNode(node);
				rnd = r.nextInt(randomModifier);
			}
			else
			{
				break;
			}
		}
		
		return node;
	}
	
	private static synchronized BlockNode getNextRandomNode(BasicNode node)
	{
		if(node.hasChildren())
		{
			return node.getRandomChild();
		}
		else
		{
			return (BlockNode)node;
		}
	}
	
	//Roulette selection
	public static synchronized OrganismTree selection(List<OrganismTree> population) 
	{
		Random r = new Random();
		
		int sumH = 0;
		
		for(int i=0; i<population.size(); i++)
		{
			sumH += population.get(i).getScore();
		}
		
		int i = 0;
		int random = r.nextInt(sumH);
		int partialSum = 0;
		
		while(random>partialSum)
		{
			partialSum+=population.get(i).getScore();
			
			if(i==population.size()-1)
			{
				break;
			}
			
			i++;
		}
		
		return population.get(i);
	}
	
}
