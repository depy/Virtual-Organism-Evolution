package com.matjazmuhic.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.Util;

public class BasicNode implements IBlockNode, Serializable
{	
	private static final long serialVersionUID = -4793425915946087029L;
	
	private int numAllNodes;
	private ITreeNode[] children;
	private Dimensions dimensions;
	private int numChildren;
	private BasicNode root;
	private String geometryId;
	
	public BasicNode()
	{
	}
	
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
			this.getRoot().setNumAllNodes(this.getRoot().getNumAllNodes()+1);
		}
	}
	
	public void removeChild(int position)
	{
		if(this.children[position]!=null && !(this.children[position] instanceof OccupiedNode))
		{
			this.children[position]=null;
			this.numChildren--;
			this.getRoot().setNumAllNodes(this.getRoot().getNumAllNodes()-1);
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
	
	public int getChildPositionByGeometryId(String geometryId)
	{
		for(int i=0; i<8; i++)
		{
			if(this.children[i]!=null)
			{
				BlockNode child = (BlockNode)this.children[i];
				if(child.getGeometryId()==geometryId)
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public BlockNode getRandomChild()
	{
		List<BlockNode> childList = new ArrayList<BlockNode>();
		Random r = new Random();
		
		for(int i=0; i<8; i++)
		{
			if(this.getChildren()[i]!=null && !(this.getChildren()[i] instanceof OccupiedNode))
			{
				childList.add((BlockNode) this.getChildren()[i]);
			}
		}
		
		int numChildren = childList.size();
		if(numChildren>0)
		{
			return (BlockNode)childList.get(r.nextInt(numChildren));
		}
		else
		{
			return null;
		}
	}

	public int getNumAllNodes()
	{
		return numAllNodes;
	}

	public void setNumAllNodes(int numAllNodes) 
	{
		this.numAllNodes = numAllNodes;
	}

	//Needed for xml serialization
	public void setChildren(ITreeNode[] children) 
	{
		this.children = children;
	}

	public void setDimensions(Dimensions dimensions) 
	{
		this.dimensions = dimensions;
	}

	public void setNumChildren(int numChildren) 
	{
		this.numChildren = numChildren;
	}

	public void setGeometryId(String geometryId) 
	{
		this.geometryId = geometryId;
	}
	
}
