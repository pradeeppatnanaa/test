package com.test.practice;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("javadoc")
public class TestListCompareString
{

	//	public static void main(final String[] args)
	//	{
	//		//		final List<String> list = new ArrayList<String>();
	//		//		list.add("AE01");
	//		//		//list.add("GE02");
	//		//		//	list.add("GE01");
	//		//
	//		//		final List<String> pincodeErrorCodesList = new ArrayList<>();
	//		//		pincodeErrorCodesList.add("AE01");
	//		//		pincodeErrorCodesList.add("GE01");
	//		//		pincodeErrorCodesList.add("GE02");
	//		//
	//		//		//		for (final String string : list)
	//		//		//		{
	//		//		//			if (string.startsWith("GE") || string.startsWith("AE"))
	//		//		//			{
	//		//		//				System.out.println("TRUE");
	//		//		//			}
	//		//		//		}
	//		//		//System.out.println("List values if it contains: " + list.startsWith("GE")); // true
	//		//		System.out.println("List values if it contains: " + list.contains(pincodeErrorCodesList));
	//
	//		final String stateName = "US-AL";
	//		System.out.println("Length is: " + stateName.length());
	//		System.out.println("new string is: " + StringUtils.substring(stateName, 3, stateName.length()));
	//
	//	}


	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final List<String> pincodeErrorCodesList = new ArrayList<>();
		pincodeErrorCodesList.add("AE01");
		pincodeErrorCodesList.add("GE01");
		pincodeErrorCodesList.add("GE02");

		for (final String string : pincodeErrorCodesList)
		{
			if (string.equals("GE02"))
			{
				pincodeErrorCodesList.remove(string);
				//break;
			}
		}
		System.out.println("done!!!!!!" + pincodeErrorCodesList);
	}
}
