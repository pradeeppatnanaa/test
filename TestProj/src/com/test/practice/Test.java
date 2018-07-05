package com.test.practice;

public class Test
{

	public static void main(final String[] args)
	{
		try
		{
			final int s = 20;
			System.out.println("Hiii this is print statement" + s / 20);
			//main(args, false);
		}
		catch (final Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
	}

	//	public static void main(final String[] args,final boolean exist)
	//	{
	//		System.out.println("Main is called by passing an exist " + exist);
	//	}


}
