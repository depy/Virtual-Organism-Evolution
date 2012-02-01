package com.matjazmuhic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.physics.JointProperties;
import com.matjazmuhic.physics.MotorObserver;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.tree.BlockNode;
import com.matjazmuhic.tree.IBlockNode;
import com.matjazmuhic.tree.OccupiedNode;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.Position;
import com.matjazmuhic.util.Util;

public class OrganismFactory 
{
	Random r;
	int numNodes = 1;
	private static OrganismFactory instance = null;
	private static OrganismEvolution app = null;
	int maxDepth = Integer.parseInt(PropertiesStore.getIstance().get("maxDepth"));
	int maxOrganismNodes = Integer.parseInt(PropertiesStore.getIstance().get("maxOrganismNodes"));
	int tempMaxNodes;
	
	int chanceToCreateNode = Integer.parseInt(PropertiesStore.getIstance().get("chanceToCreateNode"));
	float jointOffset = Float.parseFloat(PropertiesStore.getIstance().get("jointOffset"));
	boolean collisionBetweenLinkedBodys = Boolean.parseBoolean(PropertiesStore.getIstance().get("collisionBetweenLinkedBodys"));
	
	private OrganismFactory()
	{
		this.init();
	}
	
	public static OrganismFactory getInstance(OrganismEvolution organismEvolutionApp)
	{
		if(instance==null)
		{
			instance = new OrganismFactory();
		}
		app = organismEvolutionApp;
		return instance;
	}

	public void init()
	{
		r = new Random();
	}
	
	public Organism createRandomOrganism(Node node)
	{
		numNodes = 1;
		tempMaxNodes = r.nextInt(maxOrganismNodes-3)+3;
		Map<HingeJoint, JointProperties> jointsMap = new HashMap<HingeJoint, JointProperties>();
		OrganismTree oTree = new OrganismTree();
		OrganismJme oJme = new OrganismJme(node, jointsMap);
		Organism organism = new Organism(oTree, oJme, app.getDictionary().getRandomName());
		createRandomRoot(organism);
		createRecursively(organism.getOrganismTree().getRoot(), node, 0, organism.getOrganismJme().getJointsMap());
		for(Map.Entry<HingeJoint, JointProperties> entry: oJme.getJointsMap().entrySet())
		{
			HingeJoint hj = entry.getKey();
			JointProperties jp = entry.getValue();
			MotorObserver mo = new MotorObserver(hj, jp);
			oJme.getOrganismTimer().addObserver(mo);
		}	
		return organism;
	}
	
	private void createRandomRoot(Organism organism)
	{
		IBlockNode root = new BasicNode(Util.getRandomDimensions());
		((BasicNode)root).setNumAllNodes(1);
		organism.getOrganismTree().setRoot(root);
		Util.JmeObject jmeObject = Util.createJmeNode(root.getDimensions(), app, root.getGeometryId());
		
		organism.getOrganismJme().getNode().attachChild(jmeObject.geometry);
	}
	
	private void createRecursively(IBlockNode node, Node sceneNode, int depth, Map<HingeJoint, JointProperties> jointsMap)
	{
		if(depth <= this.maxDepth && numNodes<=tempMaxNodes)
		{
			List<BlockNode> addedChildren = new ArrayList<BlockNode>();
			for(int i=0; i<8; i++)
			{
				boolean ticketToPass = (numNodes==1 && i==7);
				
				if(((r.nextInt(chanceToCreateNode)==0) && numNodes<tempMaxNodes) || ticketToPass)
				{
					boolean collidesWithOtherNodes = true;
					
					Dimensions d = Util.getRandomDimensions();
					JointProperties jp = Util.getRandomJointProps();
					BlockNode newNode= new BlockNode(d, jp, i);
					node.addChild(newNode, i);

					Util.JmeObject jmeObject = Util.createJmeNode(newNode.getDimensions(), app, newNode.getGeometryId()); 
					app.getMaterialsStore().add(jmeObject.material);
					
					Geometry geometry = jmeObject.geometry;
					Spatial parentSpatial = sceneNode.getChild(node.getGeometryId());
					Position p = Position.getPosition(i);
					Position pi = Position.getPosition(Util.getInversePosition(i));
							
					translateGeometry(geometry, parentSpatial, node, newNode, p, pi);
					collidesWithOtherNodes = Util.collidesWithOtherNodes(geometry, sceneNode, node.getGeometryId());
					if(!collidesWithOtherNodes)
					{
						HingeJoint joint = makeJoint(geometry, parentSpatial, node, newNode, jp, p, pi);
						jointsMap.put(joint, jp);
						sceneNode.attachChild(geometry);
						
						numNodes++;
						addedChildren.add(newNode);
						
					}
					else
					{
						node.removeChild(i);
					}
					
				}
				else
				{
					node.getChildren()[i] = null;
				}
			}
			for(BlockNode child: addedChildren)
			{
				createRecursively(child, sceneNode, depth+1, jointsMap);
				depth--;
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
		Organism organism = new Organism(organismTree, oJme, app.getDictionary().getRandomName());
			
		createRoot(organismTree, oJme);
		createRecursivelyFromTree(organism.getOrganismTree().getRoot(), oJme.getNode(), organism.getOrganismJme().getJointsMap());
		
		for(Map.Entry<HingeJoint, JointProperties> entry: oJme.getJointsMap().entrySet())
		{
			HingeJoint hj = entry.getKey();
			JointProperties jp = entry.getValue();
			MotorObserver mo = new MotorObserver(hj, jp);

			oJme.getOrganismTimer().addObserver(mo);
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
			app.getMaterialsStore().add(jmeObject.material);
			
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
		Vector3f parentTranslation = parentSpatial.getLocalTranslation();
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
 				Util.simpleVectorToVector3f(jp.getAxis1()).normalize(),
 				Util.simpleVectorToVector3f(jp.getAxis2()).normalize()
 				);
		
		
		joint.setCollisionBetweenLinkedBodys(collisionBetweenLinkedBodys);
		joint.setLimit(jp.getLowerLimit(), jp.getUpperLimit());
		app.getBulletAppState().getPhysicsSpace().add(joint);
		return joint;
	}
}
