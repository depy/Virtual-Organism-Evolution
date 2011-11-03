package com.matjazmuhic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.JointProperties;
import com.matjazmuhic.util.KeyInputActionListener;
import com.matjazmuhic.util.MotorObserver;
import com.matjazmuhic.util.OrganismTimer;
import com.matjazmuhic.util.Util;

public class OrganismEvolution extends SimpleApplication
{
	private BulletAppState bulletAppState;
	private Map<String, List<Material>> store = new HashMap<String, List<Material>>();
	OrganismTree ot1 = null;
	Geometry floor = null;
	Node organism = null;
	Thread timerThread;
	int timePeriod = 5000;
	int timeInterval = 250;
	Camera mainCam;
	Vector3f startPosition = null;
	
	public static void main(String[] args)
	{
		OrganismEvolution app = new OrganismEvolution();
		app.start();
		app.setShowSettings(false);
		app.store.put("materials", new ArrayList<Material>());
	}
	
	@Override
	public void destroy() 
	{
		timerThread.interrupt();
		super.destroy();
	}
	
	private ActionListener actionListener = new KeyInputActionListener(this);

	@Override
	public void simpleInitApp()
	{
		Logger.getLogger("com.jme3").setLevel(Level.SEVERE);
		
		mapKeys();
		initCamera();
		
		initPhysics();
		
		organism = new Node();
		ot1 = new OrganismTree(Util.getRandomDimensions(), organism, this);
		ot1.createRandom();	
		
		OrganismTimer ot = new OrganismTimer(timePeriod, timeInterval, this);
		
		for(Map.Entry<HingeJoint, JointProperties> entry: ot1.getJointsMap().entrySet())
		{
			HingeJoint hj = entry.getKey();
			JointProperties jp = entry.getValue();
			MotorObserver mo = new MotorObserver(hj, jp);
			ot.addObserver(mo);
		}
		
		timerThread = new Thread(ot);
		timerThread.start();
		
		rootNode.attachChild(organism);
		
		addFloor(organism);

	}
	
	@Override
	public void update() 
	{
		super.update();
		if(startPosition!=null)
		{
			float distance = ((BoundingBox)(organism.getWorldBound())).distanceTo(startPosition);
			if(distance > 1)
			{
				System.out.println("Distance traveled: "+distance);
			}
		}
	};
		
	private void initCamera()
	{
		cam.setLocation(new Vector3f(50f, 50f, 150f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		mainCam = cam;
	}
	
	private void initPhysics()
	{
		bulletAppState = new BulletAppState();
		bulletAppState.setSpeed(1.0f);
		bulletAppState.setEnabled(true);
		stateManager.attach(bulletAppState);
		
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, -100.0f, 0.0f));
		bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
		bulletAppState.getPhysicsSpace().enableDebug(assetManager);
	}
	
	private void mapKeys()
	{
		inputManager.addMapping("toggleShowWireframe", new KeyTrigger(KeyInput.KEY_T));
		inputManager.addListener(actionListener, "toggleShowWireframe");
	}
	
	private void addFloor(Node node)
	{
		Vector3f min = ((BoundingBox)node.getWorldBound()).getMin(null);
		Box f = new Box(Vector3f.ZERO, 1000.0f, 1.f, 1000.f);
		floor = new Geometry("floor", f);
		floor.setShadowMode(ShadowMode.Receive);
		Material matf = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matf.setColor("Color", ColorRGBA.LightGray);
		floor.setMaterial(matf);
		floor.setLocalTranslation(0, min.y-1.0f, 0);
		RigidBodyControl fc = new RigidBodyControl(0.0f);
		floor.addControl(fc);
		floor.getControl(RigidBodyControl.class).setFriction(1.0f);
		fc.setPhysicsLocation(floor.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(floor);
		floor.setShadowMode(ShadowMode.Receive);
		rootNode.attachChild(floor);
	}

	
	//Getters
	public BulletAppState getBulletAppState()
	{
		return bulletAppState;
	}

	public Map<String, List<Material>> getStore() 
	{
		return store;
	}

	public Node getOrganism()
	{
		return organism;
	}

	public int getTimePeriod() 
	{
		return timePeriod;
	}
	
	public int getTimeInterval()
	{
		return timeInterval;
	}

	public Camera getMainCam() 
	{
		return mainCam;
	}

	public Vector3f getStartPosition() 
	{
		return startPosition;
	}

	public void setStartPosition(Vector3f startPosition) 
	{
		System.out.println("Setting start position...");
		this.startPosition = startPosition;
	}

}
