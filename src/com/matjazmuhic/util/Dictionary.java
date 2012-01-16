package com.matjazmuhic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary 
{
	List<String> wordList;
	
	public Dictionary(String filePath)
	{
		wordList = readDictFromFile(filePath);
	}
	
	private List<String> readDictFromFile(String filePath)
	{
		List<String> temp = new ArrayList<String>();
		File f = new File(filePath);
		BufferedReader buffReader = null;
		
		try
		{
			buffReader = new BufferedReader(new FileReader(f));
			String word = null;
			
			while((word = buffReader.readLine())!=null)
			{
				temp.add(word);
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(buffReader != null)
			{
				try 
				{
					buffReader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return temp;
	}
	
	public String getRandomName()
	{
		Random r = new Random();
		int wlSize = r.nextInt(wordList.size());
		
		return wordList.get(r.nextInt(wlSize))+r.nextInt(10)+wordList.get(r.nextInt(wlSize));
	}
}
