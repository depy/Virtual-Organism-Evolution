package com.matjazmuhic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
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
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.matjazmuhic.util.Judge;
import com.matjazmuhic.util.KeyInputActionListener;

public class OrganismEvolution extends SimpleApplication implements Runnable
{
	String testName;
	float distance;
	static OrganismEvolution app = null;
	private BulletAppState bulletAppState;
	private Map<String, List<Material>> store = new HashMap<String, List<Material>>();
	Geometry floor = null;
	Node organismNode;
	Camera mainCam;
	Vector3f startPosition = null;
	public Organism organism;
	
	public OrganismEvolution(String testName)
	{
		this.testName = testName;
		app = this;
	}
	
	@Override
	public void run() 
	{
		String testName = UUID.randomUUID().toString();
		System.out.println("Running test "+testName);
		
		AppSettings settings = new AppSettings(true);
		settings.setResolution(800,600);
		
		app.setShowSettings(false);
		app.store.put("materials", new ArrayList<Material>());
		app.start(/*JmeContext.Type.Headless*/);
		
		Judge judge = new Judge(app);
		Thread judgeThread = new Thread(judge);
		judgeThread.start();

	}
	
	@Override
	public void destroy() 
	{
		organism.notifyDestroy();
		super.destroy();
	}
	
	private ActionListener actionListener = new KeyInputActionListener(this);

	@Override
	public void simpleInitApp()
	{
		Logger.getLogger("com.jme3").setLevel(Level.OFF);
		
		mapKeys();
		initCamera();
		initPhysics();	
		organismNode = new Node();
		
		/* Generate */ 
		
		OrganismFactory organismFactory = OrganismFactory.getInstance();
		organismFactory.init(this);
		organism = organismFactory.createRandomOrganism(organismNode);
		
		
		/* Write to XML */
		//Util.write(organism.getOrganismTree(), "test1.xml");
		
		/* Read from XML */
		/*
		OrganismTree oTree = Util.read("c729c0e1-c32e-423e-983c-f18541f6d8e7.xml");
		OrganismFactory organismFactory = OrganismFactory.getInstance();
		organismFactory.init(this);
		System.out.println(organismNode);
		organism = organismFactory.createFromTree(oTree, organismNode);
		*/
		
		/* Read 2 from XML and crossover */
		/*
		OrganismTree oTree1 = Util.read("fc0d7129-46a5-43c7-8eb7-93f8c6950044.xml");
		OrganismTree oTree2 = Util.read("9d67d6b3-9b5c-43d5-81d7-b5d43f230781.xml");
		OrganismTree newTree = GeneticUtil.crossover(oTree1, oTree2);
		
		OrganismFactory organismFactory = OrganismFactory.getInstance();
		organismFactory.init(this);
		System.out.println(organismNode);
		organism = organismFactory.createFromTree(newTree, organismNode);
		*/
		
		rootNode.attachChild(organism.getOrganismJme().getNode());
		addFloor(organismNode);
	}
	
	@Override
	public void update() 
	{
		super.update();
		System.out.println(startPosition);
		if(this.startPosition!=null)
		{
			Vector3f currentPos =  ((BoundingBox)(organismNode.getWorldBound())).getCenter();
			float distance = currentPos.distance(startPosition);
			//System.out.println("Distance traveled: "+distance);
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
		
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, -200.0f, 0.0f));
		bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
		bulletAppState.getPhysicsSpace().enableDebug(assetManager);
	}
	
	private void mapKeys()
	{
		inputManager.addMapping("toggleShowWireframe", new KeyTrigger(KeyInput.KEY_T));
		inputManager.addListener(actionListener, "toggleShowWireframe");
		
		inputManager.addMapping("camBackwards", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addListener(actionListener, "camBackwards");
		
		inputManager.addMapping("camForwards", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addListener(actionListener, "camForwards");
		
		inputManager.addMapping("saveOrganismToXml", new KeyTrigger(KeyInput.KEY_X));
		inputManager.addListener(actionListener, "saveOrganismToXml");
		
		inputManager.addMapping("togglePhysics", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(actionListener, "togglePhysics");
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
		floor.setLocalTranslation(0, min.y-2.0f, 0);
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

	public Organism getOrganism()
	{
		return organism;
	}

	public Camera getMainCam() 
	{
		return mainCam;
	}

	public Vector3f getStartPosition() 
	{
		return startPosition;
	}

	public void setStartPosition() 
	{
		this.startPosition = organismNode.getWorldBound().getCenter().clone();
	}

	public float getDistance()
	{
		return distance;
	}

	public void setDistance(float distance) 
	{
		this.distance = distance;
	}

	public Vector3f getOrganismPosition() 
	{
		return  ((BoundingBox)(organismNode.getWorldBound())).getCenter();
	}
	
}
