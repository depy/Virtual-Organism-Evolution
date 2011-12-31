package com.matjazmuhic.ga;

import java.util.Random;

import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Util;

public class GeneticUtil 
{
	private static int randomModifier = Integer.parseInt(PropertiesStore.getIstance().get("randomModifier"));
	private static Random r = new Random();
	
	public static OrganismTree crossover(OrganismTree tree1, OrganismTree tree2)
	{
		BlockNode node = getRandomNode(tree1);		
		BlockNode node2 = getRandomSubTree(tree2);
		node2.setParent(node.getParent());
		node2.setRoot(node.getRoot());
		int node1pos = ((BasicNode)node.getParent()).getChildPositionByGeometryId(node.getGeometryId());
		node.getParent().getChildren()[node1pos] = node2;
		
		return tree1;
	}
	
	public static void mutate(OrganismTree tree)
	{
		BlockNode node = getRandomNode(tree);
		
		while(node.hasChildren())
		{
			node = getRandomNode(tree);
		}
		node.remove();
		
	}
	
	private static BlockNode getRandomSubTree(OrganismTree organismTree)
	{
		BlockNode node = getRandomNode(organismTree);
		
		node.setRoot(null);
		node.setParent(null);
		
		return node;
		
	}
	
	private static BlockNode getRandomNode(OrganismTree tree)
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
	
	private static BlockNode getNextRandomNode(BasicNode node)
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
	
}
