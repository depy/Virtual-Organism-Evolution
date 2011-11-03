package com.matjazmuhic.test;

import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.matjazmuhic.tree.BasicNode;
import com.matjazmuhic.util.Dimensions;
import com.matjazmuhic.util.Position;
import com.matjazmuhic.util.Util;

public class TestNodes extends SimpleApplication
{
	public BulletAppState bulletAppState;
	public boolean wireframe = false;
	public Material mat1;
	public Material mat2;
	public Material mat3;
	public Material matf;
	public Node organismNode;
	public Geometry floor;
	public boolean colliding = false;
	public BasicShadowRenderer bsr;
	float motorDirection = 1.0f;
	boolean simulationStoped = true;
	HingeJoint joint1;
	HingeJoint joint2;
	
	public static void main(String[] args)
	{
		TestNodes testNodes = new TestNodes();
		testNodes.start();
	}
	
	@Override
	public void simpleUpdate(float tpf) 
	{
	}

	private ActionListener actionListener = new ActionListener() {
	    @Override
	    public void onAction(String name, boolean pressed, float tpf) {
	      // toggle wireframe
	      if (name.equals("toggle wireframe") && !pressed) 
	      {
	        wireframe = !wireframe; // toggle boolean
	        mat1.getAdditionalRenderState().setWireframe(wireframe);
	        mat2.getAdditionalRenderState().setWireframe(wireframe);
	        mat3.getAdditionalRenderState().setWireframe(wireframe);
	        matf.getAdditionalRenderState().setWireframe(wireframe);
	      }
	      if(name.equals("toggleMotor") && !pressed)
			{
				if(motorDirection == 1.0f)
				{
					motorDirection = -1.0f;
				}
				else
				{
					motorDirection = 1.0f;
				}
				joint1.enableMotor(true, 10.0f*motorDirection, 1.0f);
				joint2.enableMotor(true, 10.0f*motorDirection, 1.0f);
			}
			if(name.equals("startSim"))
			{
				bulletAppState.setEnabled(true);
				System.out.println("Enabling bullet app state...");
			}
			if(name.equals("stopSim"))
			{
				bulletAppState.setEnabled(false);
				System.out.println("Disabling bullet app state...");
			}
	    }
	  };
	
	@Override
	public void simpleInitApp() 
	{	
		inputManager.addMapping("toggleMotor", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("startSim", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("stopSim", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addListener(actionListener, "toggleMotor");
		inputManager.addListener(actionListener, "startSim");
		inputManager.addListener(actionListener, "stopSim");
				
		bulletAppState = new BulletAppState();
		bulletAppState.setSpeed(1.0f);
		bulletAppState.setEnabled(false);
		stateManager.attach(bulletAppState);
		
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, -60.0f, 0.0f));
		bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
		bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		
		inputManager.addMapping("toggle wireframe", new KeyTrigger(KeyInput.KEY_T));
	    inputManager.addListener(this.actionListener, "toggle wireframe");
	    
		cam.setLocation(new Vector3f(20f, 20f, 50f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		organismNode = new Node("organismNode");
		
		Box b1 = new Box(1, 2, 10);
		Geometry box1 = new Geometry("box1", b1);
		mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat1.setColor("Color", ColorRGBA.randomColor());
		box1.setMaterial(mat1);
		
		organismNode.attachChild(box1);
		
		Box b2 = new Box(2, 3, 5);
		Geometry box2 = new Geometry("box2", b2);
		mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setColor("Color", ColorRGBA.randomColor());
		box2.setMaterial(mat2);

		organismNode.attachChild(box2);
		
		Box b3 = new Box(1, 1, 1);
		Geometry box3 = new Geometry("box3", b3);
		mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat3.setColor("Color", ColorRGBA.randomColor());
		box3.setMaterial(mat3);

		organismNode.attachChild(box3);
		
		float box1x = ((BoundingBox)box1.getWorldBound()).getXExtent();
		float box1y = ((BoundingBox)box1.getWorldBound()).getYExtent();
		float box1z = ((BoundingBox)box1.getWorldBound()).getZExtent();
		
		float box2x = ((BoundingBox)box2.getWorldBound()).getXExtent();
		float box2y = ((BoundingBox)box2.getWorldBound()).getYExtent();
		float box2z = ((BoundingBox)box2.getWorldBound()).getZExtent();
		
		float box3x = ((BoundingBox)box3.getWorldBound()).getXExtent();
		float box3y = ((BoundingBox)box3.getWorldBound()).getYExtent();
		float box3z = ((BoundingBox)box3.getWorldBound()).getZExtent();
		
		Position p4 = Position.getPosition(4);
		Position p4i = Position.getPosition(Util.getInversePosition(4));
		Position p5 = Position.getPosition(5);
		Position p5i = Position.getPosition(Util.getInversePosition(5));
		
		Vector3f posTranslVec = new Vector3f(p4.x*(box1x+box2x), p4.y*(box1y+box2y), p4.z*(box1z+box2z));
		
		Vector3f posTranslVec2 = new Vector3f(p5.x*(box1x+box3x), p5.y*(box1y+box3y), p5.z*(box1z+box3z));

		Vector3f box2lTransl = box2.getLocalTranslation();		
		Vector3f box2desiredTransl = box2lTransl.add(posTranslVec);
		
		box2.setLocalTranslation(box2desiredTransl);
		
		Vector3f box3lTransl = box3.getLocalTranslation();		
		Vector3f box3desiredTransl = box3lTransl.add(posTranslVec2);
		
		box3.setLocalTranslation(box3desiredTransl);
		
		Box f = new Box(Vector3f.ZERO, 1000.0f, 0.1f, 1000.f);
		floor = new Geometry("floor", f);
		floor.setShadowMode(ShadowMode.Receive);
		matf = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matf.setColor("Color", ColorRGBA.LightGray);
		floor.setMaterial(matf);
		floor.setLocalTranslation(0, -5, 0);
		RigidBodyControl fc = new RigidBodyControl(0.0f);
		floor.addControl(fc);
		fc.setPhysicsLocation(floor.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(floor);
		floor.setShadowMode(ShadowMode.Receive);
		rootNode.attachChild(floor);
		
		Vector3f jvec1 = new Vector3f((p4.x*(box1x+0.1f)), (p4.y*(box1y+0.1f)), (p4.z*(box1z+0.1f)));
		Vector3f jvec2 = new Vector3f((p4i.x*(box2x+0.1f)), (p4i.y*(box2y+0.1f)), (p4i.z*(box2z+0.1f)));
		
		Vector3f jvec1_2 = new Vector3f((p5.x*(box1x+0.1f)), (p5.y*(box1y+0.1f)), (p5.z*(box1z+0.1f)));
		Vector3f jvec3 = new Vector3f((p5i.x*(box3x+0.1f)), (p5i.y*(box3y+0.1f)), (p5i.z*(box3z+0.1f)));
		
		box1.addControl(new RigidBodyControl());
		box1.getControl(RigidBodyControl.class).setPhysicsLocation(box1.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(box1);
		
		box2.addControl(new RigidBodyControl());
		box2.getControl(RigidBodyControl.class).setPhysicsLocation(box2.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(box2);
		
		box3.addControl(new RigidBodyControl());
		box3.getControl(RigidBodyControl.class).setPhysicsLocation(box3.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(box3);
		
		rootNode.attachChild(organismNode);
		
		joint1 = new HingeJoint(box1.getControl(RigidBodyControl.class),
 				box2.getControl(RigidBodyControl.class),
 				jvec1,
 				jvec2,
 				Vector3f.UNIT_Z ,
 				Vector3f.UNIT_Z
 				);
		
		joint1.setCollisionBetweenLinkedBodys(true);
		joint1.setLimit(-0.57f, 0.57f);
		bulletAppState.getPhysicsSpace().add(joint1);
		
		joint2 = new HingeJoint(box1.getControl(RigidBodyControl.class),
 				box3.getControl(RigidBodyControl.class),
 				jvec1_2,
 				jvec3,
 				Vector3f.UNIT_Y,
 				Vector3f.UNIT_Y
 				);
		
		joint2.setCollisionBetweenLinkedBodys(true);
		joint2.setLimit(-0.57f, 0.57f);
		bulletAppState.getPhysicsSpace().add(joint2);
		//boljši rezultat z enotskimi vektorji
		
		/*
		joint1 = new HingeJoint(box1.getControl(RigidBodyControl.class),
 				box2.getControl(RigidBodyControl.class),
 				jvec1,
 				jvec2,
 				new Vector3f(1.0f, 0.5f, 0f).normalizeLocal() ,
 				new Vector3f(0.25f, 0.1f, 1f).normalizeLocal()
 				);
		
		joint1.setCollisionBetweenLinkedBodys(true);
		joint1.setLimit(-0.57f, 0.57f);
		bulletAppState.getPhysicsSpace().add(joint1);
		
		joint2 = new HingeJoint(box1.getControl(RigidBodyControl.class),
 				box3.getControl(RigidBodyControl.class),
 				jvec1_2,
 				jvec3,
 				new Vector3f(0.2f, 1.0f, 0f).normalizeLocal() ,
 				new Vector3f(0.25f, 0.0f, 0.3f).normalizeLocal()
 				);
		
		joint2.setCollisionBetweenLinkedBodys(true);
		joint2.setLimit(-0.57f, 0.57f);
		bulletAppState.getPhysicsSpace().add(joint2);
		*/

		//lowerTheOrganism();
		
		List<Spatial> children = organismNode.getChildren();
		for(int i = 0; i<children.size(); i++)
		{
			Spatial s = children.get(i);
			System.out.println(((BoundingBox)s.getWorldBound()).getExtent(null));
			System.out.println(children.get(i).getWorldTranslation());
		}
	}
	
	public boolean calculateCollisions(Geometry geometry, BoundingVolume boundingVolume)
	{			
		CollisionResults results = new CollisionResults();
		geometry.collideWith(boundingVolume, results);
		System.out.println("Calculating collisions...\n------------------------");
		if (results.size() > 0) 
		{
		    CollisionResult closest  = results.getClosestCollision();
		    System.out.println("What was hit? " + closest.getGeometry().getName());
		    System.out.println("------------------------\n");
		    return true;
		}

	    System.out.println("No collision detected");
	    System.out.println("------------------------\n");
	    return false;
	}
	
	public BasicNode createBox(Node sceneNode, Dimensions d)
	{
		Box box = new Box(d.x, d.y, d.z);
		Geometry geometry = new Geometry("box", box);
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.randomColor());
		geometry.setMaterial(material);
		geometry.addControl(new RigidBodyControl());
		geometry.getControl(RigidBodyControl.class).setPhysicsLocation(geometry.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(geometry);
		sceneNode.attachChild(geometry);
		
		return null;
	}
	
	public void lowerTheOrganism()
	{
		while(!colliding)
		{
			organismNode.setLocalTranslation(organismNode.getLocalTranslation().add(0f, -0.1f, 0f));
			colliding = calculateCollisions(floor, organismNode.getWorldBound());
		}
		organismNode.setLocalTranslation(organismNode.getLocalTranslation().add(0f, 0.1f, 0f));
	}
}
