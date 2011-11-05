package com.matjazmuhic;

import com.matjazmuhic.tree.OrganismTree;

public class Organism 
{
	private OrganismTree organismTree;
	private OrganismJme organismJme;
	
	public Organism(OrganismTree organismTree, OrganismJme organismJme) 
	{
		this.organismTree = organismTree;
		this.organismJme = organismJme;
	}

	public OrganismTree getOrganismTree()
	{
		return organismTree;
	}

	public OrganismJme getOrganismJme()
	{
		return organismJme;
	}

	public void setOrganismTree(OrganismTree organismTree)
	{
		this.organismTree = organismTree;
	}
	
	
	
}
