package com.test.practice;

import java.util.ArrayList;
import java.util.List;




public class ListToString
{
	public static void main(final String[] args)
	{
		final List<String> listString = new ArrayList<>();
		listString.add("2019");
		//		listString.add("2018");
		//		listString.add("2017");
		//		listString.add("2016");
		//		listString.add("2015");
		//		listString.add("2014");

		final String[] stringArray1 = null;
		System.out.println("Lenght " + stringArray1);

		final String[] stringArray = listString.toString().split(",");
		System.out.println("array string" + listString.toString());
		System.out.println("Value: " + stringArray.length);
	}

}
