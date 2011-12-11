package com.matjazmuhic.util;

import java.util.List;
import java.util.UUID;
import com.jme3.input.controls.ActionListener;
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
	
	@Override
    public void onAction(String name, boolean pressed, float tpf) 
    {
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
    		
    		List<Material> materials = app.getStore().get("materials");
    		
    		for(Material m: materials)
    		{
    			m.getAdditionalRenderState().setWireframe(showWireFrame);
    		}
    	}
    	if(name.equals("camForwards"))
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, 0f, 10f)));
    	}
    	if(name.equals("camBackwards") && !pressed)
    	{
    		app.getMainCam().setLocation(app.getMainCam().getLocation().subtract(new Vector3f(0f, 0f, -10f)));
    	}

    	if(name.equals("saveOrganismToXml") && !pressed)
    	{
    		UUID uuid = UUID.randomUUID();
    		System.out.println("Saving organism to "+uuid.toString()+".xml");
    		Util.write(app.organism.getOrganismTree(), uuid.toString()+".xml");
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
