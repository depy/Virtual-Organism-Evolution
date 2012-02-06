package com.matjazmuhic.persistence;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ajexperience.utils.DeepCopyException;
import com.ajexperience.utils.DeepCopyUtil;
import com.matjazmuhic.tree.OrganismTree;

public class OrganismRepository 
{
	static Map<Integer, List<OrganismTree>> storage = new HashMap<Integer, List<OrganismTree>>();
	
	private static OrganismRepository instance = null;
	private DeepCopyUtil deepCopyUtil;
	protected OrganismRepository()
	{
		try
		{
			deepCopyUtil = new DeepCopyUtil();
		} 
		catch (DeepCopyException e)
		{
			e.printStackTrace();
		}
	}
	
	public static OrganismRepository getInstance()
	{
		if(instance==null)
		{
			instance = new OrganismRepository();
		}
		return instance;
	}
	
	public void writeToXml(OrganismTree f, String filename)
	{
		
        XMLEncoder encoder = null;
		try
		{
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("resources/com/matjazmuhic/organismStorage/"+filename+".xml")));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
        encoder.writeObject(f);
        encoder.close();
    }

    public OrganismTree readFromXml(String filename)
    {
        XMLDecoder decoder = null;
		try 
		{
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("resources/com/matjazmuhic/organismStorage/"+filename+".xml")));
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
        OrganismTree o = (OrganismTree)decoder.readObject();
        decoder.close();
        return o;
    }	
    
    public void save(OrganismTree oTree, int generation)
    {    	
    	//writeToXml(oTree, ("_gen"+generation)+oTree.getName());
    	List<OrganismTree> organisms;
    	
    	if(!storage.containsKey(generation))
    	{
    		organisms = new ArrayList<OrganismTree>();
    		storage.put(generation, organisms);
    	}
    	else
    	{
    		organisms = storage.get(generation);
    	}
    	
    	try
    	{
			organisms.add((OrganismTree)deepCopyUtil.deepCopy(oTree));
		} 
    	catch (DeepCopyException e)
    	{
			e.printStackTrace();
		}
    }
    
    public List<OrganismTree> getGeneration(int generationNum)
    {
    	return storage.get(generationNum);
    }
    
    public void printResults()
    {
    	for(Map.Entry<Integer, List<OrganismTree>> entry: storage.entrySet())
    	{
    		List<OrganismTree> organismList = entry.getValue();
    		
    		/*
    		System.out.println("Generation "+entry.getKey());
    		for(OrganismTree oTree: organismList)
    		{
    			System.out.println("Num scores: "+oTree.getScoreHistory().size()+" => Subject: "+oTree.getName()+" => Score: "+oTree.getScore());
    		}
    		*/
    		
    		writeToXml(organismList.get(0), "G"+entry.getKey().toString()+"_"+organismList.get(0).getName());
    	}
    }
    
}
