package com.matjazmuhic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.lwjgl.Sys;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.matjazmuhic.ga.GaManager;
import com.matjazmuhic.persistence.OrganismRepository;
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.tree.OrganismTree;
import com.matjazmuhic.util.Judge;
import com.matjazmuhic.util.KeyInputActionListener;

public class OrganismEvolution extends SimpleApplication
{
	boolean headless;
	boolean testsStarted;
	BulletAppState bulletAppState;
	Map<String, List<Material>> materialsStore = new HashMap<String, List<Material>>();
	List<Thread> judgesList;
	List<Organism> organismList;
	KeyInputActionListener keyActionListener;
	int numGenerations;
	int populationSize;
	Camera mainCam;
	Vector3f startPosition = null;
	GaManager gaManager;
	int generationNum = 0;
	ExecutorService organismExecutor;
	ExecutorService judgesExecutor;
	List<Future<Float>> resultsFuturesList;
	
	public OrganismEvolution()
	{
		keyActionListener = new KeyInputActionListener(this);
		
	}
	
	public static void main(String[] args) 
	{
		OrganismEvolution app = new OrganismEvolution();
		app.headless = Boolean.parseBoolean(PropertiesStore.getIstance().get("headless"));
		app.judgesList = new ArrayList<Thread>();
		app.organismList = new ArrayList<Organism>();
		app.numGenerations = Integer.valueOf(PropertiesStore.getIstance().get("numGenerations"));
		app.populationSize = Integer.valueOf(PropertiesStore.getIstance().get("populationSize"));
	
		app.organismExecutor = Executors.newFixedThreadPool(app.populationSize);
		app.judgesExecutor = Executors.newFixedThreadPool(app.populationSize);
		
		app.resultsFuturesList = new ArrayList<Future<Float>>();
		
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024,768);
		app.setSettings(settings);
		app.setShowSettings(false);
		
		app.materialsStore.put("materials", new ArrayList<Material>());

		if(app.headless)
		{
			app.start(JmeContext.Type.Headless);
		}
		else
		{
			app.start();
		}
		
	}

	@Override
	public void simpleInitApp()
	{		
		initCamera();
		initPhysics();
		keyActionListener.mapKeys();
		
		/* With GA */
		
		makePopulation(populationSize);
		gaManager = new GaManager(this);
		
		
		/* Generate 1 */
		/*
		Node organismNode = new Node();
		rootNode.attachChild(organismNode);
		Organism organism;
		organism = OrganismFactory.getInstance(this).createRandomOrganism(organismNode);
		organismList.add(organism);
		addFloor(organismNode);
		Judge judge = new Judge(organism, generationNum);
		Thread judgeThread = new Thread(judge);
		judgesList.add(judgeThread);
		judgeThread.start();
		*/
		
		/* Generate multiple */
		/*
		for(int i=0; i<100; i++)
		{
			Node organismNode = new Node();
			rootNode.attachChild(organismNode);
			Organism organism;
			organism = OrganismFactory.getInstance(this).createRandomOrganism(organismNode);
		
			organism.move(new Vector3f(0.0f, i*250.0f, 0.0f));
			organismList.add(organism);
			addFloor(organismNode);
			
		}	
		
		for(Organism organism: organismList)
		{
			organism.notifyDestroy();
			organism = null;
		}

		rootNode.detachAllChildren();
		
		for(int i=0; i<100; i++)
		{
			Node organismNode = new Node();
			rootNode.attachChild(organismNode);
			Organism organism;
			organism = OrganismFactory.getInstance(this).createRandomOrganism(organismNode);
			organism.move(new Vector3f(0.0f, i*250.0f, 0.0f));
			organismList.add(organism);
			addFloor(organismNode);
			
		}	
		*/
		
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

		boolean finished = false;

		if(testsStarted)
		{
			for(Future<Float> f: resultsFuturesList)
			{
				finished = f.isDone();
			}

			if(finished)
			{
				organismExecutor.shutdownNow();
				judgesExecutor.shutdownNow();
				organismExecutor = Executors.newFixedThreadPool(populationSize);
				judgesExecutor = Executors.newFixedThreadPool(populationSize);

				if(generationNum<=numGenerations)
				{					
					testsStarted=false;
					List<OrganismTree> newGen = gaManager.step(generationNum);
					
					/* Spring cleaning */
					resultsFuturesList.clear();
					
					for(Organism organism: organismList)
					{
						organism.notifyDestroy();
						for(HingeJoint hjTemp: organism.getOrganismJme().jointsMap.keySet())
						{
							bulletAppState.getPhysicsSpace().remove(hjTemp);
						}	
						organism = null;
					}
					organismList.clear();
					rootNode.detachAllChildren();
					
					/* Make new population */
					makePopulation(newGen);	
				}
			}
		}	
		
	};
	
	@Override
	public void destroy() 
	{
		OrganismRepository.getInstance().printResults();
		
		for(Thread t: judgesList)
		{
			t.interrupt();
		}
		
		for(Organism organism: organismList)
		{
			organism.notifyDestroy();
		}
		
		organismExecutor.shutdownNow();
		judgesExecutor.shutdownNow();
		
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
	
	private void addFloor(Node node)
	{
		Vector3f min = ((BoundingBox)node.getWorldBound()).getMin(null);
		Box f = new Box(Vector3f.ZERO, 100000.0f, 2.0f, 100000.f);
		Geometry floor = new Geometry(UUID.randomUUID().toString(), f);
		floor.setShadowMode(ShadowMode.Receive);
		Material matf = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		matf.setColor("Color", ColorRGBA.LightGray);
		floor.setMaterial(matf);
		floor.move(0, min.y-50.0f, 0);
		RigidBodyControl fc = new RigidBodyControl(0.0f);
		fc.setCollisionGroup(1);
		floor.addControl(fc);
		floor.getControl(RigidBodyControl.class).setFriction(50.0f);
		fc.setPhysicsLocation(floor.getWorldTranslation());
		bulletAppState.getPhysicsSpace().add(fc);
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
		return materialsStore;
	}

	public Camera getMainCam() 
	{
		return mainCam;
	}
	
	public void makePopulation(int size)
	{
		generationNum++;
		
		for(int i=0; i<size; i++)
		{
			
			Node organismNode = new Node();
			rootNode.attachChild(organismNode);
			Organism organism;
			organism = OrganismFactory.getInstance(this).createRandomOrganism(organismNode);
			organismExecutor.execute(organism.getOrganismJme().getOrganismTimer());
			organism.move(new Vector3f(0.0f, i*250.0f, 0.0f));
			organismList.add(organism);
			addFloor(organismNode);
			
			Callable<Float> judge = new Judge(organism, generationNum);
			Future<Float> fJudge = judgesExecutor.submit(judge);
			resultsFuturesList.add(fJudge);
		}	
		testsStarted = true;
	}
	
	public void makePopulation(List<OrganismTree> oTrees)
	{
		generationNum++;
		
		for(int i=0; i<oTrees.size(); i++)
		{
			
			Node organismNode = new Node();
			rootNode.attachChild(organismNode);
			Organism organism;
			organism = OrganismFactory.getInstance(this).createFromTree(oTrees.get(i), organismNode);
			organismExecutor.execute(organism.getOrganismJme().getOrganismTimer());
			organism.move(new Vector3f(0.0f, i*250.0f, 0.0f));
			organismList.add(organism);
			addFloor(organismNode);
			
			Callable<Float> judge = new Judge(organism, generationNum);
			Future<Float> fJudge = judgesExecutor.submit(judge);
			resultsFuturesList.add(fJudge);
		}	
		testsStarted = true;
	}
	
	public List<Organism> getOrganismList() 
	{
		return organismList;
	}	
	
}
