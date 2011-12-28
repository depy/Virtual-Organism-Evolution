package com.matjazmuhic.persistence;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matjazmuhic.Organism;
import com.matjazmuhic.tree.OrganismTree;

import de.lessvoid.nifty.tools.StopWatch;

public class OrganismRepository 
{
	static Map<Integer, Collection<OrganismTree>> storage; 
	
	public OrganismRepository()
	{
		this.storage = new HashMap<Integer, Collection<OrganismTree>>();
	}
	
	public static void writeToXml(OrganismTree f, String filename)
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

    public static OrganismTree readFromXml(String filename)
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
    
    public static void save(OrganismTree oTree, int generation)
    {
    	Collection<OrganismTree> organisms = storage.get(generation);
    	
    	if(organisms==null)
    	{
    		organisms = new ArrayList<OrganismTree>();
    		storage.put(generation, organisms);
    	}
    	
    	organisms.add(oTree);
    }
    
}
