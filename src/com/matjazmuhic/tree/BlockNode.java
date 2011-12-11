package com.matjazmuhic.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.JointProperties;

public class BlockNode extends BasicNode
{
	private static final long serialVersionUID = -2223785630550397267L;

	private IBlockNode parent;
	
	private JointProperties jointProperties;
	
	public BlockNode()
	{
	}
	
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

	//Needed for xml serialization
	public void setJointProperties(JointProperties jointProperties) 
	{
		this.jointProperties = jointProperties;
	}
		
}
