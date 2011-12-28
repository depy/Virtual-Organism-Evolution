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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.matjazmuhic.util.Judge;
import com.matjazmuhic.util.KeyInputActionListener;

public class OrganismEvolution extends SimpleApplication
{
	boolean headless = false;
	boolean showDistance = false;
	String testName;
	float distance;
	private BulletAppState bulletAppState;
	private Map<String, List<Material>> store = new HashMap<String, List<Material>>();
	Geometry floor = null;
	Camera mainCam;
	Vector3f startPosition = null;
	List<Thread> judgeThreads;
	List<Organism> organismList;
	
	public OrganismEvolution()
	{
	}
	
	public static void main(String[] args) 
	{
		OrganismEvolution app = new OrganismEvolution();
		
		app.judgeThreads = new ArrayList<Thread>();
		app.organismList = new ArrayList<Organism>();
		
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024,768);
		
		app.setSettings(settings);
		app.setShowSettings(false);
		app.store.put("materials", new ArrayList<Material>());

		if(app.headless)
		{
			app.start(JmeContext.Type.Headless);
		}
		else
		{
			app.start();
		}
		
	}
	
	private ActionListener actionListener = new KeyInputActionListener(this);

	@Override
	public void simpleInitApp()
	{
		Logger.getLogger("com.jme3").setLevel(Level.OFF);
		
		mapKeys();
		initCamera();
		initPhysics();	
		
		/* Generate */
		
		for(int i=0; i<10; i++)
		{
			
			Node organismNode = new Node();
			rootNode.attachChild(organismNode);
			Organism organism = new OrganismFactory(this).createRandomOrganism(organismNode);	
			organismList.add(organism);
			organism.move(new Vector3f(0.0f, i*200.0f, 0.0f));
			addFloor(organismNode);
			
			/*
			Judge judge = new Judge(organism);
			Thread judgeThread = new Thread(judge);
			judgeThreads.add(judgeThread);
			judgeThread.start();
			*/

		}
		
		
		/* Write to XML */
		//Util.write(organism.getOrganismTree(), "test1.xml");
		
		/* Read from XML */
		/*
		OrganismTree oTree = OrganismRepository.readFromXml("27225eb7-d95d-48db-94f2-c72eb4fd5ce2");
		System.out.println(organismNode);
		organism = organismFactory.createFromTree(oTree, organismNode);
		*/
		
		/* Read 2 from XML and crossover */
		/*
		OrganismTree oTree1 = Util.read("fc0d7129-46a5-43c7-8eb7-93f8c6950044.xml");
		OrganismTree oTree2 = Util.read("9d67d6b3-9b5c-43d5-81d7-b5d43f230781.xml");
		OrganismTree newTree = GeneticUtil.crossover(oTree1, oTree2);
		
		System.out.println(organismNode);
		organism = organismFactory.createFromTree(newTree, organismNode);
		*/
		
		
	}
	
	@Override
	public void update() 
	{
		super.update();
	};
	
	@Override
	public void destroy() 
	{
		for(Thread t: judgeThreads)
		{
			t.interrupt();
		}
		
		for(Organism organism: organismList)
		{
			organism.notifyDestroy();
		}
		
		super.destroy();
		
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
		
		inputManager.addMapping("camUp", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addListener(actionListener, "camUp");
		
		inputManager.addMapping("camDown", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addListener(actionListener, "camDown");
		
		inputManager.addMapping("saveOrganismToXml", new KeyTrigger(KeyInput.KEY_X));
		inputManager.addListener(actionListener, "saveOrganismToXml");
		
		inputManager.addMapping("togglePhysics", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(actionListener, "togglePhysics");
		
		inputManager.addMapping("testMove", new KeyTrigger(KeyInput.KEY_L));
		inputManager.addListener(actionListener, "testMove");
		
	}
	
	private void addFloor(Node node)
	{
		Vector3f min = ((BoundingBox)node.getWorldBound()).getMin(null);
		Box f = new Box(Vector3f.ZERO, 500.0f, 2.0f, 500.f);
		floor = new Geometry("floor", f);
		floor.setShadowMode(ShadowMode.Receive);
		Material matf = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matf.setColor("Color", ColorRGBA.LightGray);
		floor.setMaterial(matf);
		floor.move(0, min.y-20.0f, 0);
		RigidBodyControl fc = new RigidBodyControl(0.0f);
		floor.addControl(fc);
		floor.getControl(RigidBodyControl.class).setFriction(10.0f);
		fc.setPhysicsLocation(floor.getWorldTranslation());
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

	public Camera getMainCam() 
	{
		return mainCam;
	}
	
}
