package com.matjazmuhic;

public class MainTest 
{
	Runnable test1;
	
	public static void main(String[] args) throws InterruptedException
	{
		MainTest app = new MainTest();
		
		app.test1 = new OrganismEvolution("Subject 110328");
		Thread t1 = new Thread(app.test1);
		t1.run();
		t1.join();
		System.out.println("finished");
		
	}
}
