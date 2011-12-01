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
	
	public OrganismJme(Node node, Map<HingeJoint, JointProperties> jointsMap) 
	{
		this.node = node;
		this.jointsMap = jointsMap;
	}

	public Node getNode()
	{
		return node;
	}

	public Map<HingeJoint, JointProperties> getJointsMap() 
	{
		return jointsMap;
	}	
	
}
