package com.matjazmuhic;

import java.util.List;
import java.util.Map;

import com.jme3.bullet.joints.HingeJoint;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.OrganismTimer;

public class OrganismJme
{
	Node node;
	Map<HingeJoint, JointProperties> jointsMap;	
	
	OrganismTimer organismTimer;
	
	public OrganismJme(Node node, Map<HingeJoint, JointProperties> jointsMap) 
	{
		this.node = node;
		this.jointsMap = jointsMap;
		this.organismTimer = new OrganismTimer();
	
	}

	public Node getNode()
	{
		return node;
	}

	public Map<HingeJoint, JointProperties> getJointsMap() 
	{
		return jointsMap;
	}

	public OrganismTimer getOrganismTimer() 
	{
		return organismTimer;
	}	
	
}
