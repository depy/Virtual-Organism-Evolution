package com.matjazmuhic.util;

import java.util.Observable;
import com.matjazmuhic.persistence.PropertiesStore;

public class OrganismTimer extends Observable implements Runnable
{	
	private boolean finished = false;
	
	public OrganismTimer()
	{
	}
	
	@Override
	public void run() 
	{
		try
		{
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("warmupTime")));
			
			while(!finished)
			{
				setChanged();
				notifyObservers();
				Thread.sleep(Integer.parseInt(PropertiesStore.getIstance().get("timerTimeInterval")));
			}
		}
		catch(InterruptedException e)
		{

		}
	}
	
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
}