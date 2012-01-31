package com.matjazmuhic.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ajexperience.utils.DeepCopyException;
import com.ajexperience.utils.DeepCopyUtil;
import com.jme3.math.Vector3f;
import com.matjazmuhic.Organism;
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.physics.JointProperties;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.IBlockNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.SimpleVector;
import com.matjazmuhic.util.Util;

public class GeneticUtil 
{
	private static int randomModifier = Integer.parseInt(PropertiesStore.getIstance().get("randomModifier"));
	private static Random r = new Random();
	private static DeepCopyUtil deepCopyUtil;
	
	static 
	{
		try 
		{
			deepCopyUtil = new DeepCopyUtil();
		} 
		catch (DeepCopyException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static synchronized OrganismTree crossover(OrganismTree tree1, OrganismTree tree2)
	{
		OrganismTree tree1copy = null;
		OrganismTree tree2copy = null;
		
		try 
		{
			tree1copy = deepCopyUtil.deepCopy(tree1);
			tree2copy = deepCopyUtil.deepCopy(tree2);
			tree1.getScoreHistory().clear();
			tree2.getScoreHistory().clear();
		} 
		catch (DeepCopyException e)
		{
			e.printStackTrace();
		}
		
		BlockNode node = getRandomNode(tree1copy);	
		int node1pos = node.getPosition();
		IBlockNode parent = node.getParent();
		BlockNode node2 = getRandomSubTree(tree2copy);
		node2.setParent(parent);
		node2.setRoot(node.getRoot());
		parent.getChildren()[node1pos] = node2;
		
		return tree1copy;
	}
	
	public static synchronized OrganismTree mutate(OrganismTree tree)
	{
		BlockNode node = getRandomNode(tree);
		
		while(node.hasChildren())
		{
			node = getNextRandomNode(node);
		}
		
		//Mutate...
		//node.setJointProperties(Util.getRandomJointProps());
	
		/* Something fishy going on here
		int choice = r.nextInt(7);
		JointProperties jp = node.getJointProperties();
		
		switch(choice) 
		{
			case 0: jp.setAxis1(modifyAxis(jp.getAxis1())); break;
			case 1: jp.setAxis2(modifyAxis(jp.getAxis2())); break;
			case 2: jp.setLowerLimit(modifyMotorLimit(jp.getLowerLimit(), "lower")); break;
			case 3: jp.setUpperLimit(modifyMotorLimit(jp.getUpperLimit(), "upper")); break;
			case 4: jp.setTimeA(modifyTimeA(jp.getTimeA(), jp.getTimeB())); break;
			case 5: jp.setTimeB(modifyTimeB(jp.getTimeB(), jp.getTimeA(), jp.getTimePeriod())); break;
			case 6: jp.setTimePeriod(modifyTimePeriod(jp.getTimePeriod())); break;
		}
		node.getJointProperties();
		*/
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
	
	
	
	//Mutation of joint properties
	
	public static synchronized SimpleVector modifyAxis(SimpleVector axis)
	{
		int c = r.nextInt(3);
		
		switch(c)
		{
			case 0: axis.setX(axis.getX()+r.nextFloat()); break;
			case 1: axis.setY(axis.getY()+r.nextFloat()); break;
			case 2: axis.setZ(axis.getZ()+r.nextFloat()); break;
		}
		
		Vector3f temp = new Vector3f(axis.getX(), axis.getY(), axis.getZ());
		
		return Util.vector3fToSimpleVector(temp);
	}
	
	public static synchronized float modifyMotorLimit(float currentValue, String which)
	{
		int operation = r.nextInt(2);
		
		// du = diff upper, dl = diff lower, _u = up, _d = down => du_d = diff upper up
		float du_d = Math.abs(currentValue);
		float du_u = (float) (1.5 - Math.abs(currentValue));
		float dl_d = (float) (1.5 - Math.abs(currentValue));
		float dl_u = Math.abs(currentValue);
		
		if(which.equals("upper"))
		{
			if(operation==0)
			{
				currentValue = currentValue + du_u;
			}
			else
			{
				currentValue = currentValue - du_d;
			}
		}
		else
		{
			if(operation==0)
			{
				currentValue = currentValue + dl_u;
			}
			else
			{
				currentValue = currentValue - dl_d;
			}
		}
		
		return currentValue;
	}
	
	public static synchronized long modifyTimeA(long timeA, long timeB)
	{
		long range = timeB - timeA;
		int addNum = r.nextInt((int) range);
		long newTimeA = timeA + addNum;
		return newTimeA;
	}
	
	public static synchronized long modifyTimeB(long timeB, long timeA, long timeRange)
	{
		long numSub = timeB - timeA;
		long numAdd = timeRange - timeB;
		
		int c = r.nextInt(2);
		
		if(c==0)
		{
			timeB = timeB + numAdd;
		}
		else
		{
			timeB = timeB - numSub + 1;
		}
		
		return timeB;
	}
	
	public static synchronized int modifyTimePeriod(int timePeriod)
	{
		/*
		System.out.println("timePeriod = "+timePeriod);
		long range = 5000 - timePeriod;
		System.out.println("range = "+range);
		int rNumAdd = r.nextInt((int) range);
		int rNumSub = r.nextInt(timePeriod);
		
		int c = r.nextInt(2);
		
		if(c==0)
		{
			range = range + rNumAdd;
		}
		else
		{
			range = range - rNumSub;
		}
		
		return (int) range;
		*/
		//System.out.println("===============> timePeriod = "+timePeriod);
		return timePeriod;
	}
	
}
