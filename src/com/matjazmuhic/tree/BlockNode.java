package com.matjazmuhic.tree;

import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.JointProperties;

public class BlockNode extends BasicNode
{
	private IBlockNode parent;
	
	private JointProperties jointProperties;
	
	public BlockNode(Dimensions dimensions, JointProperties jointProperties)
	{
		super(dimensions);
		this.jointProperties = jointProperties;
	}

	public IBlockNode getParent()
	{
		return parent;
	}

	public void setParent(IBlockNode parent)
	{
		this.parent = parent;
	}

	public JointProperties getJointProperties()
	{
		return jointProperties;
	}
	
}
