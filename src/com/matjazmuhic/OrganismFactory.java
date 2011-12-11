package com.matjazmuhic;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.IBlockNode;
import com.matjazmuhic.tree.OccupiedNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.MotorObserver;
import com.matjazmuhic.util.OrganismTimer;
import com.matjazmuhic.util.Position;
import com.matjazmuhic.util.Util;

public class OrganismFactory 
{
	int maxDepth = 10;
	int maxOrganismNodes = 10;
	int maxNodes;
	int chanceToCreateNode = 2;
	
	float jointOffset = 0.0f;
	int jointTimePeriod = 5000;
	int jointTimeInterval = 250;
	
	boolean collisionBetweenLinkedBodys = false;
	
	Random r;

	int numNodes = 1;
	
	private static OrganismFactory instance = null;
	private OrganismEvolution app = null;
	
	private OrganismFactory()
	{
		
	}
	
	public static OrganismFactory getInstance()
	{
		if(instance==null)
		{
			instance = new OrganismFactory();
		}

		return instance;
	}
	
	public void init(OrganismEvolution app)
	{
		this.app = app;
		r = new Random();
		maxNodes = r.nextInt(maxOrganismNodes);
	}
	
	public Organism createRandomOrganism(Node node)
	{
		Map<HingeJoint, JointProperties> jointsMap = new HashMap<HingeJoint, JointProperties>();
		OrganismTree oTree = new OrganismTree();
		OrganismJme oJme = new OrganismJme(node, jointsMap);
		Organism organism = new Organism(oTree, oJme);
			
		createRandomRoot(organism);
		createRecursively(organism.getOrganismTree().getRoot(), node, 0, organism.getOrganismJme().getJointsMap());
		
		for(Map.Entry<HingeJoint, JointProperties> entry: oJme.getJointsMap().entrySet())
		{
			HingeJoint hj = entry.getKey();
			JointProperties jp = entry.getValue();
			MotorObserver mo = new MotorObserver(hj, jp);
			OrganismTimer organismTimer = new OrganismTimer(jp.getTimePeriod(), jp.getTimeInterval());
			organismTimer.addObserver(mo);
			Thread t = new Thread(organismTimer);
			t.start();
			oJme.timerThreads.add(t);
		}	
		System.out.println("Num nodes: "+numNodes+"  Num joints: "+oJme.getJointsMap().size());
		
		return organism;
	}
	
	private void createRandomRoot(Organism organism)
	{
		IBlockNode root = new BasicNode(Util.getRandomDimensions());
		organism.getOrganismTree().setRoot(root);
		Util.JmeObject jmeObject = Util.createJmeNode(root.getDimensions(), app, root.getGeometryId());
		
		organism.getOrganismJme().getNode().attachChild(jmeObject.geometry);
	}
	
	private void createRecursively(IBlockNode node, Node sceneNode, int depth, Map<HingeJoint, JointProperties> jointsMap)
	{
		if(depth <= this.maxDepth)
		{
			System.out.println("Depth:"+depth);
			for(int i=0; i<6; i++)
			{
				/*
				if(sceneNode.getChildren().size()>=maxNodes)
				{
					break;
				}
				*/
				/*
				if(i==5 && numNodes==1)
				{
					passWithoutChance = true;
				}
				*/
				if(r.nextInt(chanceToCreateNode)!=0 && numNodes<maxNodes)
				{
					Dimensions d = Util.getRandomDimensions();
					JointProperties jp = Util.getRandomJointProps();
					BlockNode newNode= new BlockNode(d, jp);
					node.addChild(newNode, i);

					Util.JmeObject jmeObject = Util.createJmeNode(newNode.getDimensions(), app, newNode.getGeometryId()); 
					app.getStore().get("materials").add(jmeObject.material);
					
					Geometry geometry = jmeObject.geometry;
					Spatial parentSpatial = sceneNode.getChild(node.getGeometryId());
					Position p = Position.getPosition(i);
					Position pi = Position.getPosition(Util.getInversePosition(i));
							
					translateGeometry(geometry, parentSpatial, node, newNode, p, pi);
					
					boolean collidesWithOtherNodes = Util.collidesWithOtherNodes(geometry, sceneNode);
					if(!collidesWithOtherNodes)
					{
						System.out.println(i+" does not collide....");
						HingeJoint joint = makeJoint(geometry, parentSpatial, node, newNode, jp, p, pi);
						jointsMap.put(joint, jp);
						sceneNode.attachChild(geometry);
						
						numNodes++;
						createRecursively(newNode, sceneNode, depth+1, jointsMap);
					}
					else
					{
						node.getChildren()[i]=null;
					}
					
				}
				else
				{
					node.getChildren()[i] = null;
				}
			}
		}
	}
	
	private void createRoot(OrganismTree organismTree, OrganismJme organismJme)
	{
		IBlockNode root = organismTree.getRoot();
		Util.JmeObject jmeObject = Util.createJmeNode(root.getDimensions(), app, root.getGeometryId());
		
		organismJme.getNode().attachChild(jmeObject.geometry);
	}
	
	public Organism createFromTree(OrganismTree organismTree, Node sceneNode)
	{
		Map<HingeJoint, JointProperties> jointsMap = new HashMap<HingeJoint, JointProperties>();
		OrganismJme oJme = new OrganismJme(sceneNode, jointsMap);
		Organism organism = new Organism(organismTree, oJme);
			
		createRoot(organismTree, oJme);
		createRecursivelyFromTree(organism.getOrganismTree().getRoot(), oJme.getNode(), organism.getOrganismJme().getJointsMap());
		
		for(Map.Entry<HingeJoint, JointProperties> entry: oJme.getJointsMap().entrySet())
		{
			HingeJoint hj = entry.getKey();
			JointProperties jp = entry.getValue();
			MotorObserver mo = new MotorObserver(hj, jp);
			OrganismTimer organismTimer = new OrganismTimer(jp.getTimePeriod(), jp.getTimeInterval());
			organismTimer.addObserver(mo);
			Thread t = new Thread(organismTimer);
			t.start();
			oJme.timerThreads.add(t);
		}	
		
		return organism;
	}
	
	private void createRecursivelyFromTree(IBlockNode node, Node sceneNode, Map<HingeJoint, JointProperties> jointsMap)
	{
		for(int i=0; i<node.getChildren().length; i++)
		{		
			if(node.getChildren()[i]==null || node.getChildren()[i] instanceof OccupiedNode)
			{
				continue;
			}
			
			BlockNode childNode = (BlockNode)node.getChildren()[i];

			
			JointProperties jp = childNode.getJointProperties();
	
			Util.JmeObject jmeObject = Util.createJmeNode(childNode.getDimensions(), app, childNode.getGeometryId()); 
			app.getStore().get("materials").add(jmeObject.material);
			
			Geometry geometry = jmeObject.geometry;
			Spatial parentSpatial = sceneNode.getChild(node.getGeometryId());
			Position p = Position.getPosition(i);
			Position pi = Position.getPosition(Util.getInversePosition(i));
					
			translateGeometry(geometry, parentSpatial, node, childNode, p, pi);

			HingeJoint joint = makeJoint(geometry, parentSpatial, node, childNode, jp, p, pi);
			jointsMap.put(joint, jp);
			sceneNode.attachChild(geometry);
			
			if(childNode.hasChildren())
			{
				createRecursivelyFromTree(childNode, sceneNode, jointsMap);
			}
		}
	}
	
	private void translateGeometry(Geometry geometry, Spatial parentSpatial, IBlockNode node, BlockNode newNode, Position p, Position pi)
	{
		Dimensions parentDim = node.getDimensions();
		Dimensions newNodeDim = newNode.getDimensions();
		Vector3f translationVector = new Vector3f(p.x*(parentDim.x+newNodeDim.x), p.y*(parentDim.y+newNodeDim.y), p.z*(parentDim.z+newNodeDim.z));
		Vector3f parentTranslation = parentSpatial.getWorldTranslation();
		Vector3f desiredTranslation = parentTranslation.add(translationVector);
		geometry.setLocalTranslation(desiredTranslation);
		geometry.getControl(RigidBodyControl.class).setPhysicsLocation(geometry.getLocalTranslation());
	}
	
	private HingeJoint makeJoint(Geometry geometry, Spatial parentSpatial, IBlockNode parentNode, BlockNode newNode, JointProperties jp, Position p, Position pi)
	{
		Dimensions newNodeDim = newNode.getDimensions();
		Dimensions parentDim = parentNode.getDimensions();
		Vector3f jPivotA = new Vector3f((pi.x*(newNodeDim.x+jointOffset)), (pi.y*(newNodeDim.y+jointOffset)), (pi.z*(newNodeDim.z+jointOffset)));
		Vector3f jPivotB = new Vector3f((p.x*(parentDim.x+jointOffset)), (p.y*(parentDim.y+jointOffset)), (p.z*(parentDim.z+jointOffset)));
		
		HingeJoint joint = new HingeJoint(
				geometry.getControl(RigidBodyControl.class),
 				parentSpatial.getControl(RigidBodyControl.class),
 				jPivotA,
 				jPivotB,
 				Util.getRandomVector().normalize(),
 				Util.getRandomVector().normalize()
 				);
		
		joint.setCollisionBetweenLinkedBodys(collisionBetweenLinkedBodys);
		joint.setLimit(-1.57f, 1.57f);
		app.getBulletAppState().getPhysicsSpace().add(joint);
		return joint;
	}
}
