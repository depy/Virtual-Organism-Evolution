package com.matjazmuhic.util;

import java.util.List;
import java.util.Random;

import javax.management.RuntimeErrorException;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

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
		float f = r.nextInt(15)+5;
		return new Dimensions(r.nextFloat()*f, r.nextFloat()*f, r.nextFloat()*f);
	}
	
	public static JointProperties getRandomJointProps(int timePeriod, int timeInterval)
	{
		SimpleVector axis1 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		SimpleVector axis2 = new SimpleVector(r.nextFloat(), r.nextFloat(), r.nextFloat());
		float lowerLimit = (r.nextFloat()*1.57f)-1.57f;
		float upperLimit = r.nextFloat()*1.57f;	
		boolean collisions = true;
		float motorTargetVelocity = r.nextFloat()*100f;
		float motorMaxImpulse = getRandomFloatTenth()*5.0f;
		//float motorMaxImpulse = r.nextFloat();
		System.out.println("Motor max impulse = "+motorMaxImpulse);
		int timeRange = timePeriod / timeInterval;
		int timeA = r.nextInt(timeRange);
		int timeB = r.nextInt(timeRange-timeA);
		
		return new JointProperties(axis1, axis2, lowerLimit, upperLimit, collisions, motorTargetVelocity, motorMaxImpulse, timeA, timeB);
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
	
	public static JmeObject createJmeNode(Dimensions d, AssetManager assetManager, BulletAppState bulletAppState, String name)
	{
		Box b = new Box(d.x, d.y, d.z);
		Geometry geometry = new Geometry(name, b);
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.randomColor());
		geometry.setMaterial(material);
		geometry.addControl(new RigidBodyControl());
		geometry.getControl(RigidBodyControl.class).setPhysicsLocation(geometry.getLocalTranslation());
		bulletAppState.getPhysicsSpace().add(geometry);
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
	
	public static  boolean collidesWithOtherNodes(Geometry geometry, Node sceneNode)
	{
		List<Spatial> spatials = sceneNode.getChildren();
		
		for(int i=0; i<spatials.size(); i++)
		{
			BoundingVolume boundingVolume = spatials.get(i).getWorldBound();
			CollisionResults results = new CollisionResults();
			geometry.collideWith(boundingVolume, results);
			if (results.size() > 0) 
			{
			    return true;
			}
		}
		return false;
	}
	
}
