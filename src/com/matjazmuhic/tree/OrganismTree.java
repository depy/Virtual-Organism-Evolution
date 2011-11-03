package com.matjazmuhic.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.matjazmuhic.OrganismEvolution;
import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.Position;
import com.matjazmuhic.util.Util;
import com.matjazmuhic.tree.BasicNode;

public class OrganismTree 
{
	IBlockNode root;
	Node organismNode;
	AssetManager assetManager;
	BulletAppState bulletAppState;
	
	int maxDepth = 3;
	int maxNodes = 10;
	int chanceToCreateNode = 3; // 2 = 50%, 3 = 33%, 4 = 25%, ...
	
	float jointOffset = 0.0f;
	int jointTimePeriod;
	int jointTimeInterval;
	boolean collisionBetweenLinkedBodys = false;
	
	Random r;
	Map<HingeJoint, JointProperties> jointsMap;
	Map<String, List<Material>> store;
	boolean passWithoutChance = false;
	int numNodes = 1;
	
	public OrganismTree(Dimensions rootDimensions, Node node, OrganismEvolution app)
	{
		this.root = new BasicNode(rootDimensions);
		this.organismNode = node;
		this.assetManager = app.getAssetManager();
		this.bulletAppState = app.getBulletAppState();
		this.store = app.getStore();
		this.jointTimePeriod = app.getTimePeriod();
		this.jointTimeInterval = app.getTimeInterval();
		r = new Random();
		jointsMap = new HashMap<HingeJoint, JointProperties>();
		Util.JmeObject jmeObject = Util.createJmeNode(root.getDimensions(), assetManager, bulletAppState, root.getGeometryId());
		store.get("materials").add(jmeObject.material);
		organismNode.attachChild(jmeObject.geometry);
	}
	
	public IBlockNode getRoot()
	{
		return root;
	}
		
	public Node getOrganismNode()
	{
		return organismNode;
	}
	
	private void createRecursively(IBlockNode node, Node sceneNode, int depth)
	{
		if(depth <= this.maxDepth)
		{
			for(int i=0; i<6; i++)
			{
				if(sceneNode.getChildren().size()>=maxNodes)
				{
					break;
				}

				if(i==5 && numNodes==1)
				{
					passWithoutChance = true;
				}
				
				if(r.nextInt(chanceToCreateNode)==0 || passWithoutChance)
				{
					Dimensions d = Util.getRandomDimensions();
					JointProperties jp = Util.getRandomJointProps(jointTimePeriod, jointTimeInterval);
					BlockNode newNode= new BlockNode(d, jp);
					node.addChild(newNode, i);

					Util.JmeObject jmeObject = Util.createJmeNode(newNode.getDimensions(), assetManager, bulletAppState, newNode.getGeometryId()); 
					store.get("materials").add(jmeObject.material);
					
					Geometry geometry = jmeObject.geometry;
					Spatial parentSpatial = sceneNode.getChild(node.getGeometryId());
					Position p = Position.getPosition(i);
					Position pi = Position.getPosition(Util.getInversePosition(i));
							
					translateGeometry(geometry, parentSpatial, node, newNode, p, pi);
					
					boolean collidesWithOtherNodes = Util.collidesWithOtherNodes(geometry, sceneNode);
					if(collidesWithOtherNodes)
					{
						continue;
					}

					makeJoint(geometry, parentSpatial, node, newNode, jp, p, pi);
					sceneNode.attachChild(geometry);
					
					numNodes++;

					createRecursively(newNode, sceneNode, depth+1);
					passWithoutChance = false;
				}
				else
				{
					node.getChildren()[i] = null;
				}
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
	
	private void makeJoint(Geometry geometry, Spatial parentSpatial, IBlockNode parentNode, BlockNode newNode, JointProperties jp, Position p, Position pi)
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
		bulletAppState.getPhysicsSpace().add(joint);
		jointsMap.put(joint, jp);
	}
		
	public void createRandom()
	{			
		if(root!=null)
		{
			createRecursively(root, organismNode, 0);
			System.out.println("Created organism with "+numNodes+" nodes...");
		}
		else
		{
			throw new RuntimeException("Can't create random organism if it is already created! (root != NULL)");
		}
	}

	public Map<HingeJoint, JointProperties> getJointsMap() 
	{
		return jointsMap;
	}
		
	/*
	//Create from data ?
	private void createNodes(IBlockNode node, Node sceneNode)
	{
		if(node.hasChildren())
		{
			ITreeNode[] children = node.getChildren();
			for(int i=0; i<children.length; i++)
			{
				if(children[i]!=null)
				{
					IBlockNode child = (IBlockNode) children[i];
					if(child instanceof OccupiedNode)
					{
						
					}
					else
					{
						//create geometry and stuff
						Position p = Position.getPosition(i);
						//translate and stuff
						//add to sceneNode
						createNodes(child, sceneNode);
					}
				}
			}
		}
	}
	*/
	
}
