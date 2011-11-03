package com.matjazmuhic.tree;

import java.util.UUID;

import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.Util;

public class BasicNode implements IBlockNode
{	
	private ITreeNode[] children;
	private Dimensions dimensions;
	private int numChildren;
	private BasicNode root;
	private String geometryId;
	
	public BasicNode(Dimensions dimensions)
	{
		this.numChildren = 0;
		this.dimensions = dimensions;
		children = new ITreeNode[8];
		this.geometryId = UUID.randomUUID().toString();
	}
	
	
	
	public void addChild(BlockNode node, int position)
	{
		if(position>=0 && position<8 && children[position]==null)
		{
			node.setParent(this);
			children[position] = node;
			if(this.root == null)
			{
				node.setRoot(this);
			}
			else
			{
				node.setRoot(this.root);
			}
			int inversePosition = Util.getInversePosition(position);
			node.getChildren()[inversePosition] = new OccupiedNode();
			this.numChildren++;
		}
	}
	
	public boolean hasChildren()
	{
		return numChildren!=0;
	}
	
	public Dimensions getDimensions()
	{
		return this.dimensions;
	}

	public ITreeNode[] getChildren()
	{
		return children;
	}

	public int getNumChildren()
	{
		return numChildren;
	}	

	public BasicNode getRoot()
	{
		return root;
	}

	public void setRoot(BasicNode root) 
	{
		this.root = root;
	}

	public String getGeometryId()
	{
		return geometryId;
	}

	
}
