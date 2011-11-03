package com.matjazmuhic.util;

import javax.management.RuntimeErrorException;

public class Position 
{
	private static Position[] instances = new Position[8];
	public int x,y,z;
	
	private Position()
	{
		
	}
	
	private Position(byte[] pos)
	{
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
	}
	
	public static Position getPosition(int pos)
	{
		if(pos>=0 && pos<=7)
		{
			if(instances[pos]==null)
			{
				instances[pos]= new Position(binPosToDir(pos)); 
			}
			return instances[pos];
		}
		Error e = new Error("Position must be between (and including) 0 and 7!");
		throw new RuntimeErrorException(e);
	}
	
	private static byte[] binPosToDir(int pos)
	{
		byte[] result = new byte[3];
		result[0]=numToQuotient(pos, 1);
		result[1]=numToQuotient(pos, 2);
		result[2]=numToQuotient(pos, 4);
		
		return result;
	}
	
	private static byte numToQuotient(int num, int xorWith)
	{
		int inverse = ((~num)+8);
		int xor = (inverse^xorWith);
		if(xor+xorWith==inverse)
			return 1;
		else
			return -1;
	}
	
	@Override
	public String toString() 
	{
		return this.x+"\t"+this.y+"\t"+this.z;
	}
}
