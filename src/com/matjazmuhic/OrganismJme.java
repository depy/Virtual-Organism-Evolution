package com.matjazmuhic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jme3.bullet.joints.HingeJoint;
import com.jme3.scene.Node;
import com.matjazmuhic.util.JointProperties;

public class OrganismJme
{
	Node node;
	Map<HingeJoint, JointProperties> jointsMap;	
	List<Thread> timerThreads = null;
	
	public OrganismJme(Node node, Map<HingeJoint, JointProperties> jointsMap) 
	{
		this.node = node;
		this.jointsMap = jointsMap;
		this.timerThreads = new ArrayList<Thread>();
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
