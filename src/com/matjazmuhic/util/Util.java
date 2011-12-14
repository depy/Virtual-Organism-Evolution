package com.matjazmuhic.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import javax.management.RuntimeErrorException;

import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.matjazmuhic.OrganismEvolution;
import com.matjazmuhic.tree.OrganismTree;

public class Util 
{
	private static int timerTimeInterval = 250; // miliseconds
	
	public static class JmeObject
	{
		public Material material;
		public Geometry geometry;
		
		public JmeObject(Material material, Geometry geometry)
		{
			this.material = material;
			this.geometry = geometry;
		}
	}
	
	private static Random r = new Random();
	
	public static Dimensions getRandomDimensions()
	{
		float f = r.nextInt(15)+5;
		float g = 1;
		return new Dimensions(r.nextFloat()*f+g, r.nextFloat()*f+g, r.nextFloat()*f+g);
	}
	
	public static JointProperties getRandomJointProps()
	{
		SimpleVector axis1 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		SimpleVector axis2 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		float lowerLimit = (r.nextFloat()*1.57f)-1.57f;
		float upperLimit = r.nextFloat()*1.57f;	
		boolean collisions = false;
		float motorTargetVelocity = r.nextFloat()*80f;
		float motorMaxImpulse = getRandomFloatTenth()*4.0f;
		int timePeriod = r.nextInt(4500)+500;
		int timeInterval = timerTimeInterval;
		int timeRange = timePeriod / timeInterval;
		int timeA = r.nextInt(timeRange);
		int timeB = r.nextInt(timeRange-timeA);
		
		return new JointProperties(axis1, axis2, lowerLimit, upperLimit, collisions, motorTargetVelocity, motorMaxImpulse, timeA, timeB, timePeriod, timeInterval);
	}
	
	private static float getRandomFloatTenth()
	{
		float rf = 0.01f;
		while(rf<0.2)
		{
			rf = r.nextFloat();
		}
		return rf;
	}
	
	public static int getInversePosition(int position)
	{
		if(position>=0 && position<8)
		{
			return ~position+8;
		}
		else
		{
			Error e = new Error("Position must be between (and including) 0 and 7!");
			throw new RuntimeErrorException(e); 
		}
	}
	
	public static JmeObject createJmeNode(Dimensions d, OrganismEvolution app, String name)
	{
		Box b = new Box(d.x, d.y, d.z);
		float mass = (d.x*d.y*d.z)/90;
		Geometry geometry = new Geometry(name, b);
		Material material = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.randomColor());
		geometry.setMaterial(material);
		geometry.addControl(new RigidBodyControl());
		geometry.getControl(RigidBodyControl.class).setPhysicsLocation(geometry.getLocalTranslation());
		app.getBulletAppState().getPhysicsSpace().add(geometry);
		app.getStore().get("materials").add(material);
		return new JmeObject(material, geometry);
	}
	
	public static Vector3f simpleVectorToVector3f(SimpleVector v)
	{
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}
	
	public static Vector3f getRandomVector()
	{
		Random r = new Random();
		return new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat());
	}
	
	public static boolean collidesWithOtherNodes(Geometry geometry, Node sceneNode, String excludeName)
	{
		List<Spatial> spatials = sceneNode.getChildren();
		
		for(int i=0; i<spatials.size(); i++)
		{
			if(!spatials.get(i).getName().equals(excludeName))
			{	
				BoundingVolume boundingVolume = spatials.get(i).getWorldBound();
				CollisionResults results = new CollisionResults();
				geometry.collideWith(boundingVolume, results);
				if (results.size() > 0) 
				{
					System.out.println(geometry.getName()+" collides with "+spatials.get(i).getName());
				    return true;
				}
			}
		}
		return false;
	}
	
	public static void write(OrganismTree f, String filename)
	{
        XMLEncoder encoder = null;
		try
		{
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("resources/com/matjazmuhic/organismStorage/"+filename)));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
        encoder.writeObject(f);
        encoder.close();
    }

    public static OrganismTree read(String filename)
    {
        XMLDecoder decoder = null;
		try 
		{
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("resources/com/matjazmuhic/organismStorage/"+filename)));
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
        OrganismTree o = (OrganismTree)decoder.readObject();
        decoder.close();
        return o;
    }	
}
