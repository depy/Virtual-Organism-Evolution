package com.matjazmuhic.util;

import java.util.List;
import java.util.Map;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.matjazmuhic.OrganismEvolution;

public class KeyInputActionListener implements ActionListener
{
	boolean showWireFrame = false;
	private Map<String, List<Material>> store;
	
	public KeyInputActionListener(OrganismEvolution app)
	{
		this.store = app.getStore();
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
    		
    		List<Material> materials = store.get("materials");
    		
    		for(Material m: materials)
    		{
    			m.getAdditionalRenderState().setWireframe(showWireFrame);
    		}
    	}
    }
}
