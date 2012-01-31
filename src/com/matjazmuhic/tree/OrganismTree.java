package com.matjazmuhic.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrganismTree implements Serializable, Comparable<OrganismTree>
{
	private static final long serialVersionUID = 8474134536491791809L;
	
	IBlockNode root;
	int numNodes;
	float score;
	String name;
	List<Float> scoreHistory;
	
	public OrganismTree()
	{
		numNodes = 0;
		scoreHistory = new ArrayList<Float>();
	}
	
	public IBlockNode getRoot()
	{
		return root;
	}
		
	public void setRoot(IBlockNode root) 
	{
		this.root = root;
		setRootRecursively((BasicNode) root);

	}
	
	private void setRootRecursively(BasicNode node)
	{
		node.setRoot((BasicNode)this.root);
		if(node.hasChildren())
		{
			for(int i=0; i<8; i++)
			{
				if(node.getChildren()[i] instanceof OccupiedNode || node.getChildren()[i]==null)
				{
					continue;
				}
				else
				{
					BasicNode child = (BasicNode) node.getChildren()[i];
					child.setRoot((BasicNode) this.root);
					setRootRecursively(child);
				}
			}
				
		}
	}
	
	public void destroy()
	{
		destroyRecursively(this.root);
	}
	
	private void destroyRecursively(IBlockNode node)
	{
		if(node.hasChildren())
		{
			for(int i=0; i<8; i++)
			{
				if(node.getChildren()[i] instanceof OccupiedNode || node.getChildren()[i]==null)
				{
					continue;
				}
				else
				{
					
					destroyRecursively((IBlockNode) node.getChildren()[i]);
				}
			}
				
		}
		
		node = null;
	}

	public float getScore()
	{
		return score;
	}

	public void addScore(float f)
	{
		scoreHistory.add(f);
		
		float sum = 0.0f;
		for(Float temp: scoreHistory)
		{
			sum+=temp;
		}
		this.score = sum/scoreHistory.size();
	}

	@Override
	public int compareTo(OrganismTree oTree)
	{
		return (int)(oTree.score - this.score);
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public List<Float> getScoreHistory() 
	{
		return scoreHistory;
	}
		
}
