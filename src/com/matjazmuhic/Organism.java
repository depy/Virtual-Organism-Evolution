package com.matjazmuhic;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.matjazmuhic.tree.IBlockNode;
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
	
	public void notifyDestroy()
	{
		for(Thread t: organismJme.timerThreads)
		{
			t.interrupt();
		}
	}
	
	public void move(Vector3f location)
	{
		Node node = organismJme.getNode();
		for(Spatial s: node.getChildren())
		{
			s.move(location);
			s.getControl(RigidBodyControl.class).setPhysicsLocation(s.getLocalTranslation());
		}
	}
	
}
