package com.matjazmuhic.test;

import java.util.Random;

public class TestTimeline 
{
	public static void main(String[] args)
	{
		for(int i=0; i<10; i++)
		{
			int x = 100;
			
			Random r = new Random();
			int timeF = r.nextInt(x);
			int timeB = r.nextInt(x-timeF)+timeF;
			
			System.out.println(timeF+"\t"+timeB);
		}
	}
	
}
