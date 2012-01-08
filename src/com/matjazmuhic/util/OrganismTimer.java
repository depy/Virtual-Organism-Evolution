package com.matjazmuhic.util;

import java.util.Observable;
import com.matjazmuhic.OrganismEvolution;
import com.matjazmuhic.persistence.PropertiesStore;

public class OrganismTimer extends Observable implements Runnable
{
	int timerCounter = 0;
	int interval; 
	int period;
	OrganismEvolution app;
	
	public OrganismTimer(int periodInMs, int timeInterval)
	{
		this.period = periodInMs;
		this.interval = timeInterval;
	}
	
	@Override
	public void run() 
	{
		try
		{
			Thread.sleep(Integer.valueOf(PropertiesStore.getIstance().get("warmupTime")));
			
			while(true)
			{
				setChanged();
				notifyObservers(timerCounter);
				Thread.sleep(interval);
				timerCounter++;
				
				if(interval*timerCounter>=period)
				{
					timerCounter = 0;
				}
			}
		}
		catch(InterruptedException e)
		{

		}
	}
}