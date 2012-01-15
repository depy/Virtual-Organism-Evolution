package com.matjazmuhic.util;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.matjazmuhic.OrganismEvolution;

public class KeyInputActionListener implements ActionListener
{
	private boolean showWireFrame = false;
	private boolean physicsEnabled = true;
	private OrganismEvolution app;
	
	public KeyInputActionListener(OrganismEvolution app)
	{
		this.app = app;
	}
	
	public void mapKeys()
	{
		InputManager inputManager = app.getInputManager();
		
		inputManager.addMapping("toggleShowWireframe", new KeyTrigger(KeyInput.KEY_T));
		inputManager.addListener(this, "toggleShowWireframe");
		
		inputManager.addMapping("camBackwards", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addListener(this, "camBackwards");
		
		inputManager.addMapping("camForwards", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addListener(this, "camForwards");
		
		inputManager.addMapping("camUp", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addListener(this, "camUp");
		
		inputManager.addMapping("camDown", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addListener(this, "camDown");
		
		inputManager.addMapping("camLeft", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addListener(this, "camLeft");
		
		inputManager.addMapping("camRight", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addListener(this, "camRight");
		
		inputManager.addMapping("saveOrganismToXml", new KeyTrigger(KeyInput.KEY_X));
		inputManager.addListener(this, "saveOrganismToXml");
		
		inputManager.addMapping("togglePhysics", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(this, "togglePhysics");
		
		inputManager.addMapping("test", new KeyTrigger(KeyInput.KEY_H));
		inputManager.addListener(this, "test");
		
	}
	
	@Override
    public void onAction(String name, boolean pressed, float tpf) 
    {
		float camMoveStep = 20f;
    	if(name.equals("toggleShowWireframe"))
    	{
    		if(showWireFrame)
			{
    			showWireFrame = false;
			}
			else
			{
				showWireFrame = true;
			}
    		
    		for(Material m: app.getMaterialsStore())
    		{
    			m.getAdditionalRenderState().setWireframe(showWireFrame);
    		}
    	}
    	if(name.equals("camForwards"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, 0f, camMoveStep)));
    	}
    	if(name.equals("camBackwards"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, 0f, -camMoveStep)));
    	}
    	if(name.equals("camUp"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, camMoveStep, 0f)));
    	}
    	if(name.equals("camDown"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, -camMoveStep, 0f)));
    	}
    	if(name.equals("camLeft"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(camMoveStep, 0f, 0f)));
    	}
    	if(name.equals("camRight"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(-camMoveStep, 0f, 0f)));
    	}
    	
    	if(name.equals("togglePhysics") && !pressed)
    	{
    		if(physicsEnabled)
    		{
    			app.getBulletAppState().setEnabled(false);
    			physicsEnabled = false;
    		}
    		else
    		{
    			app.getBulletAppState().setEnabled(true);
    			physicsEnabled = true;
    		}
    	}
    	
    }

}
