package com.matjazmuhic.ga;

import java.util.Random;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Util;

public class GeneticUtil 
{
	private static int randomModifier = 3;
	private static Random r = new Random();
	
	public static OrganismTree crossover(OrganismTree tree1, OrganismTree tree2)
	{
		BasicNode root1 = (BasicNode)tree1.getRoot();
		BlockNode node1 = getNextRandomNode(root1);
		
		
		int rnd = r.nextInt(randomModifier);
		while(rnd!=0)
		{
			if(node1.hasChildren())
			{
				node1 = getNextRandomNode(node1);
				rnd = r.nextInt(randomModifier);
			}
			else
			{
				break;
			}
			System.out.println("Rnd = "+rnd+" hasChildren = "+node1.hasChildren());
		}
		
		int node1pos = ((BasicNode)node1.getParent()).getChildPositionByGeometryId(node1.getGeometryId());
		OrganismTree subTree = getRandomSubTree(tree2);
		BlockNode newRoot = new BlockNode(subTree.getRoot().getDimensions(), Util.getRandomJointProps());
		subTree.setRoot(newRoot);
		node1.getParent().getChildren()[node1pos] = subTree.getRoot();
		
		return tree1;
	}
	
	private static OrganismTree getRandomSubTree(OrganismTree organismTree)
	{
		BasicNode root = (BasicNode)organismTree.getRoot();
		BlockNode node = getNextRandomNode(root);
		
		while(r.nextInt(randomModifier)!=0)
		{
			if(node.hasChildren())
			{
				node = getNextRandomNode(node);
			}
			else
			{
				break;
			}
		}
		
		OrganismTree newTree = new OrganismTree();
		node.setRoot(null);
		node.setParent(null);
		newTree.setRoot(node);
		
		return newTree;
		
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
