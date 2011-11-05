package com.matjazmuhic;

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
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.IBlockNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.OrganismTimer;
import com.matjazmuhic.util.Position;
import com.matjazmuhic.util.Util;

public class OrganismFactory 
{
	int maxDepth = 3;
	int maxNodes = 10;
	int chanceToCreateNode = 3; // 2 = 50%, 3 = 33%, 4 = 25%, ...
	
	float jointOffset = 0.0f;
	int jointTimePeriod = 5000;
	int jointTimeInterval = 250;
	boolean collisionBetweenLinkedBodys = false;
	
	Random r;
	boolean passWithoutChance = false;
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
	}
	
	public Organism createRandomOrganism(Node node)
	{
		Map<HingeJoint, JointProperties> jointsMap = new HashMap<HingeJoint, JointProperties>();
		OrganismTree oTree = new OrganismTree();
		OrganismTimer oTimer = new OrganismTimer(jointTimePeriod, jointTimeInterval);
		OrganismJme oJme = new OrganismJme(node, jointsMap, oTimer);
		Organism organism = new Organism(oTree, oJme);
			
		createRoot(organism);
		createRecursively(organism.getOrganismTree().getRoot(), node, maxDepth, organism.getOrganismJme().getJointsMap());
		
		return organism;
	}
	
	private void createRoot(Organism organism)
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

					Util.JmeObject jmeObject = Util.createJmeNode(newNode.getDimensions(), app, newNode.getGeometryId()); 
					app.getStore().get("materials").add(jmeObject.material);
					
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

					HingeJoint joint = makeJoint(geometry, parentSpatial, node, newNode, jp, p, pi);
					jointsMap.put(joint, jp);
					sceneNode.attachChild(geometry);
					
					numNodes++;

					createRecursively(newNode, sceneNode, depth+1, jointsMap);
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
