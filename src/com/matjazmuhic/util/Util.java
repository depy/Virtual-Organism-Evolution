package com.matjazmuhic.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.management.RuntimeErrorException;
import com.jme3.bounding.BoundingBox;
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
import com.matjazmuhic.persistence.PropertiesStore;
import com.matjazmuhic.physics.JointProperties;

public class Util 
{	
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
		float f = r.nextInt(18)+2;
		float g = 1;
		return new Dimensions(r.nextFloat()*f+g, r.nextFloat()*f+g, r.nextFloat()*f+g);
	}
	
	public static JointProperties getRandomJointProps()
	{
		SimpleVector axis1 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		SimpleVector axis2 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		float lowerLimit = ((r.nextFloat()*1.5f)*(-1))-0.1f;
		float upperLimit = (r.nextFloat()*1.5f)+0.1f;	
		
		boolean collisions = false;
		//float motorTargetVelocity = r.nextFloat()*30f;
		//float motorMaxImpulse = getRandomFloatTenth()*5.0f;
		float motorTargetVelocity = 50f;
		float motorMaxImpulse = 10.0f;
		int timePeriod = r.nextInt(4500)+500;
		int timeRange =  timePeriod;
		int timeA = r.nextInt(timeRange-100)+100;
		int timeB = r.nextInt(timeRange-100)+timeA+100;
		if(timeA == timeB)
		{
			System.out.println("\n\nOMG OMG OMG!\n\n timeA = "+timeA+" timeB = "+timeB+"\n\n");
		}
		return new JointProperties(axis1, axis2, lowerLimit, upperLimit, collisions, motorTargetVelocity, motorMaxImpulse, timeA, timeB, timePeriod);
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
	
	public static synchronized JmeObject createJmeNode(Dimensions d, OrganismEvolution app, String name)
	{
		float massFactor = 0.5f;
		Box b = new Box(d.x, d.y, d.z);
		float mass = (d.x*d.y*d.z)*massFactor;
		
		Geometry geometry = new Geometry(name, b);
		geometry.setModelBound(new BoundingBox());
		geometry.updateModelBound();
		Material material = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.randomColor());
		geometry.setMaterial(material);
		RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
		rigidBodyControl.setCollisionGroup(1);
		geometry.addControl(rigidBodyControl);
		geometry.getControl(RigidBodyControl.class).setPhysicsLocation(geometry.getLocalTranslation());
		app.getBulletAppState().getPhysicsSpace().add(geometry);
		app.getMaterialsStore().add(material);

		return new JmeObject(material, geometry);
	}
	
	public static Vector3f simpleVectorToVector3f(SimpleVector v)
	{
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}
	
	public static SimpleVector vector3fToSimpleVector(Vector3f v)
	{
		return new SimpleVector(v.getX(), v.getY(), v.getZ());
	}
	
	public static Vector3f getRandomVector()
	{
		Random r = new Random();
		return new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat());
	}
	
	public static synchronized boolean collidesWithOtherNodes(Geometry geometry, Node sceneNode, String excludeName)
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

}
