package com.matjazmuhic.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;

public class TestMyPhy extends SimpleApplication
{
	BulletAppState bulletAppState;
	BasicShadowRenderer bsr;
	HingeJoint joint1;
	Node organism;
	float motorDirection = 1.0f;
	boolean simulationStoped = true;
	Thread t = null;
	
	public static void main(String[] args)
	{
		TestMyPhy tmp = new TestMyPhy();
		tmp.start();
	}

	private ActionListener actionListener = new ActionListener()
	{	
		@Override
		public void onAction(String name, boolean keyPressed, float tpf)
		{
			if(name.equals("toggleMotor") && !keyPressed)
			{
				if(motorDirection == 1.0f)
				{
					motorDirection = -1.0f;
				}
				else
				{
					motorDirection = 1.0f;
				}
				joint1.enableMotor(true, 10.0f*motorDirection, 5.0f);
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
		t = new Thread(
				new Runnable() {
					
					@Override
					public void run() {
						while(true)
						{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(motorDirection == 1.0f)
						{
							motorDirection = -1.0f;
						}
						else
						{
							motorDirection = 1.0f;
						}
						joint1.enableMotor(true, 10.0f*motorDirection, 5.0f);
						}
					}
				}
		);
		
		inputManager.addMapping("toggleMotor", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("startSim", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("stopSim", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addListener(actionListener, "toggleMotor");
		inputManager.addListener(actionListener, "startSim");
		inputManager.addListener(actionListener, "stopSim");
		
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
		rootNode.addLight(sun);
		
		/*
		bsr = new BasicShadowRenderer(assetManager, 256);
		bsr.setDirection(sun.getDirection().normalizeLocal());
		viewPort.addProcessor(bsr);
		rootNode.setShadowMode(ShadowMode.Off);
		*/
		
		cam.setLocation(new Vector3f(0.0f, 40.0f, 80.0f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		bulletAppState = new BulletAppState();
		bulletAppState.setSpeed(1.0f);
		stateManager.attach(bulletAppState);
		
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, -60.0f, 0.0f));
		bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
		bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		
		organism = new Node("organism");
		
		Box f = new Box(Vector3f.ZERO, 50.0f, 0.1f, 50.f);
		Geometry floor = new Geometry("floor", f);
		floor.setShadowMode(ShadowMode.Receive);
		Material matf = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		matf.setBoolean("m_UseMaterialColors", true);
		matf.setColor("m_Ambient", ColorRGBA.Black);
		matf.setColor("m_Diffuse", ColorRGBA.White);
		matf.setColor("m_Specular", ColorRGBA.White);
		matf.setFloat("m_Shininess", 12);
		floor.setMaterial(matf);
		RigidBodyControl fc = new RigidBodyControl(0.0f);
		floor.addControl(fc);
		fc.setPhysicsLocation(floor.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(floor);
		organism.attachChild(floor);
		
		
		Box b = new Box(5, 1, 1);
		Geometry box = new Geometry("box1", b);
		box.setLocalTranslation(-6.0f, 2.0f, 0.0f);
		box.setShadowMode(ShadowMode.CastAndReceive);
		Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat1.setBoolean("m_UseMaterialColors", true);
		mat1.setColor("m_Ambient", ColorRGBA.Black);
		mat1.setColor("m_Diffuse", ColorRGBA.Cyan);
		mat1.setColor("m_Specular", ColorRGBA.White);
		mat1.setFloat("m_Shininess", 12);
		box.setMaterial(mat1);
		box.addControl(new RigidBodyControl(20.0f));
		box.getControl(RigidBodyControl.class).setPhysicsLocation(box.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(box);
		organism.attachChild(box);
		
		
		Box b2 = new Box(5, 1, 1);
		Geometry box2 = new Geometry("box2", b2);
		box2.setLocalTranslation(6.0f, 2.0f, 0.0f);
		box2.setShadowMode(ShadowMode.CastAndReceive);
		Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat2.setBoolean("m_UseMaterialColors", true);
		mat2.setColor("m_Ambient", ColorRGBA.Black);
		mat2.setColor("m_Diffuse", ColorRGBA.Cyan);
		mat2.setColor("m_Specular", ColorRGBA.White);
		mat2.setFloat("m_Shininess", 12);
		box2.setMaterial(mat2);
		box2.addControl(new RigidBodyControl(20.0f));
		box2.getControl(RigidBodyControl.class).setPhysicsLocation(box2.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(box2);
		organism.attachChild(box2);


		rootNode.attachChild(organism);

		joint1 = new HingeJoint(box.getControl(RigidBodyControl.class),
				 				box2.getControl(RigidBodyControl.class),
				 				new Vector3f( 5.0f, -1.0f, -1.0f),
				 				new Vector3f( -5.0f, 1.0f, 1.0f),
				 				new Vector3f(1.0f, 0.5f, 0f).normalizeLocal() ,
				 				new Vector3f(0.25f, 0.1f, 1f).normalizeLocal()
				 				);

		
		joint1.setCollisionBetweenLinkedBodys(true);
		joint1.setLimit(-3f, 3f);
		bulletAppState.getPhysicsSpace().add(joint1);
		rootNode.attachChild(organism);
		
		bulletAppState.setEnabled(false);
		t.start();
	}
	
	@Override
	public void simpleUpdate(float tpf)
	{
		System.out.println("");
	}

}
