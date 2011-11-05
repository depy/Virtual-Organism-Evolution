package com.matjazmuhic;

import java.util.Map;

import com.jme3.bullet.joints.HingeJoint;
import com.jme3.scene.Node;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.OrganismTimer;

public class OrganismJme
{
	Node node;
	Map<HingeJoint, JointProperties> jointsMap;
	OrganismTimer ot;
	
	public OrganismJme(Node node, Map<HingeJoint, JointProperties> jointsMap, OrganismTimer ot) 
	{
		this.node = node;
		this.jointsMap = jointsMap;
		this.ot = ot;
	}

	public Node getNode()
	{
		return node;
	}

	public Map<HingeJoint, JointProperties> getJointsMap() 
	{
		return jointsMap;
	}

	public OrganismTimer getOt() 
	{
		return ot;
	}
	
	
}
