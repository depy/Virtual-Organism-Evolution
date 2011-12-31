package com.matjazmuhic.persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesStore 
{
	private static PropertiesStore instance = null;
	Properties properties;
	
	protected PropertiesStore()
	{
		properties = new Properties();
		
		try
		{
			properties.load(new FileInputStream("resources/com/matjazmuhic/settings.properties"));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static PropertiesStore getIstance()
	{
		if(instance==null)
		{
			instance = new PropertiesStore();
		}
		return instance;
	}
	
	public String get(String key)
	{
		return instance.properties.getProperty(key);
	}
}
