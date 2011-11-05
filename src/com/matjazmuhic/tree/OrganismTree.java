package com.matjazmuhic.tree;

import java.io.Serializable;

public class OrganismTree implements Serializable
{
	private static final long serialVersionUID = 8474134536491791809L;
	
	IBlockNode root;
	
	public OrganismTree()
	{
	}
	
	public IBlockNode getRoot()
	{
		return root;
	}
		
	public void setRoot(IBlockNode root) 
	{
		this.root = root;
	}
		
}
