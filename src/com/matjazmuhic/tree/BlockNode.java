package com.matjazmuhic.tree;

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
	
	public void remove()
	{
		int pos = ((BasicNode)this.parent).getChildPositionByGeometryId(this.getGeometryId());
		this.parent.removeChild(pos);
	}
	
	public void setAsRoot()
	{
		BlockNode self = this;
		
		setRootRecursively(self, self);
	}
	
	private void setRootRecursively(BlockNode selfRef, BlockNode currentNode)
	{
		BlockNode node = currentNode;

		ITreeNode[] children = node.getChildren();
			
		for(int i=0; i<children.length; i++)
		{
			BasicNode child = (BasicNode)children[i];
			child.setRoot(selfRef);
			setRootRecursively(selfRef, node);
		}
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
