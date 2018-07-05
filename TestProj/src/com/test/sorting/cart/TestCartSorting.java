package com.test.sorting.cart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;



public class TestCartSorting
{

	public static void main(final String[] args)
	{
		final CartData cartData = loadCartData();

		final List<OrderEntryData> entriesWithoutPatientName = new ArrayList<OrderEntryData>();
		class CaseInsensitiveComparator implements Comparator<String>
		{
			public int compare(final String first, final String second)
			{
				return first.compareToIgnoreCase(second);
			}
		}
		final Map<String, List<OrderEntryData>> entriesWithPatientNameMap = new TreeMap<>(new CaseInsensitiveComparator());



		for (final OrderEntryData orderEntryData : cartData.getEntries())
				{
			if (StringUtils.isNotBlank(orderEntryData.getPatientName()))
					{
				final Set<String> keys = entriesWithPatientNameMap.keySet();
				if (CollectionUtils.isEmpty(keys))
						{
							addToMap(entriesWithPatientNameMap, orderEntryData);
					//		            				for (final String key : keys)
					//		            				{
					//
					//									if (key.equalsIgnoreCase(orderEntryData.getPatientName()))
					//										{
					//											System.out.println("Patient name matched so adding to existing map" + key);
					//											final List<OrderEntryData> valueList = entriesWithPatientNameMap.get(orderEntryData.getPatientName());
					//											valueList.add(orderEntryData);
					//											entriesWithPatientNameMap.put(orderEntryData.getPatientName(), valueList);
					//											System.out.println(
					//													"List size in the map " + entriesWithPatientNameMap.get(orderEntryData.getPatientName()).size());
					//										}
					//								}

						}
						else
						{
					System.out.println("Map have keys so searching for  patient name in existing map "
							+ orderEntryData.getPatientName().toLowerCase());

					final List<OrderEntryData> valueList = entriesWithPatientNameMap
							.get(orderEntryData.getPatientName().toLowerCase());
					if (CollectionUtils.isEmpty(valueList))
					{
						addToMap(entriesWithPatientNameMap, orderEntryData);
					}
					else
					{
						System.out.println("There is a matching key for the patient name  [ "
								+ orderEntryData.getPatientName().toLowerCase() + " so adding to that key");
						valueList.add(orderEntryData);
						entriesWithPatientNameMap.put(orderEntryData.getPatientName().toLowerCase(), valueList);
					}

						}
					}

			else
			{
				entriesWithoutPatientName.add(orderEntryData);

			}
				}

		//		for (final OrderEntryData orderEntryData : cartData.getEntries())
		//		{
		//			if (StringUtils.isNotBlank(orderEntryData.getPatientName()))
		//			{
		//				if (entriesWithPatientNameMap.containsKey(orderEntryData.getPatientName()))
		//				{
		//				final Set<String> keys = entriesWithPatientNameMap.keySet();
		//				for (final String key : keys)
		//				{
		//
		//						if (key.toLowerCase().equalsIgnoreCase(orderEntryData.getPatientName()))
		//					{
		//						System.out.println("Patient name matched so adding to existing map" + key);
		//						final List<OrderEntryData> valueList = entriesWithPatientNameMap.get(orderEntryData.getPatientName());
		//						valueList.add(orderEntryData);
		//						entriesWithPatientNameMap.put(orderEntryData.getPatientName(), valueList);
		//						System.out
		//								.println("List size in the map " + entriesWithPatientNameMap.get(orderEntryData.getPatientName()).size());
		//					}
		//					else
		//					{
		//						System.out.println("Map have keys so searching for  patient name in existing map "
		//									+ orderEntryData.getPatientName());
		//
		//						final List<OrderEntryData> valueList = entriesWithPatientNameMap
		//									.get(orderEntryData.getPatientName());
		//						if (CollectionUtils.isEmpty(valueList))
		//						{
		//							addToMap(entriesWithPatientNameMap, orderEntryData);
		//						}
		//						else
		//						{
		//							System.out.println("There is a matching key for the patient name  [ "
		//									+ orderEntryData.getPatientName().toLowerCase() + " so adding to that key");
		//							valueList.add(orderEntryData);
		//								entriesWithPatientNameMap.put(orderEntryData.getPatientName(), valueList);
		//						}
		//
		//					}
		//				}
		//				}
		//				else
		//				{
		//					addToMap(entriesWithPatientNameMap, orderEntryData);
		//				}
		//
		//			}
		//
		//			else
		//			{
		//				entriesWithoutPatientName.add(orderEntryData);
		//
		//			}
		//		}
		if (CollectionUtils.isNotEmpty(entriesWithoutPatientName))
		{
			cartData.setEntriesWithoutPatientName(sortByProductNameAndEyeSight(entriesWithoutPatientName));
		}
		if (MapUtils.isNotEmpty(entriesWithPatientNameMap))
		{
			final Collection<List<OrderEntryData>> valuesInMap = entriesWithPatientNameMap.values();
			for (final List<OrderEntryData> list : valuesInMap)
			{
				sortByProductNameAndEyeSight(list);
			}
			cartData.setEntriesWithPatientName(entriesWithPatientNameMap);
		}

		printCartData(cartData);
	}

	public static void printCartData(final CartData cartData)
	{
		final Map<String, List<OrderEntryData>> entries = cartData.getEntriesWithPatientName();
		for (final Map.Entry<String, List<OrderEntryData>> entry : entries.entrySet())
		{
			System.out.println("PatientName : " + entry.getKey());
			System.out.println(" Value : ");
			entry.getValue();
			for (final OrderEntryData entryData : entry.getValue())
			{
				System.out.println(" " + entryData.getProductData().getBaseProductName() + " "
						+ (null != entryData.getEyeSight() ? entryData.getEyeSight() : ""));
			}

		}
		System.out.println("++++++++++++++++++WithoutPatient Name");
		for (final OrderEntryData entryData : cartData.getEntriesWithoutPatientName())
		{
			System.out.println((null != entryData.getEyeSight() ? entryData.getEyeSight() : "") + "  "
					+ entryData.getProductData().getBaseProductName());
		}


	}

	private static void addToMap(final Map<String, List<OrderEntryData>> entriesWithPatientNameMap,
			final OrderEntryData orderEntryData)
	{
		System.out.println(
				"Map don't have any keys with patient name so newly adding to  map " + orderEntryData.getPatientName());
		final List<OrderEntryData> entriesListWithPatientName = new ArrayList<OrderEntryData>();
		entriesListWithPatientName.add(orderEntryData);
		entriesWithPatientNameMap.put(orderEntryData.getPatientName(), entriesListWithPatientName);
	}


	private static List<OrderEntryData> sortByProductName(final List<OrderEntryData> entries)
	{

		entries.sort((final OrderEntryData oe1, final OrderEntryData oe2) -> oe1.productData.getBaseProductName()
				.compareTo(oe2.productData.getBaseProductName()));
		return entries;
	}

	//	private static void order(final List<OrderEntryData> entries)
	//	{
	//		entries.sort(Comparator.comparing(OrderEntryData::getOrderDate).thenComparing(Order::getId));
	//	}
	private static List<OrderEntryData> sortByProductNameAndEyeSight(final List<OrderEntryData> entries)
	{
		//		final Comparator<OrderEntryData> comparator = (o1, o2) -> o1.getProductData().getBaseProductName()
		//				.compareTo(o2.getProductData().getBaseProductName());
		//		comparator.thenComparing((o1, o2) -> o1.eyeSight.compareTo(o2.eyeSight));
		//
		//		entries.sort(comparator);
		//		return entries;
		Collections.sort(entries, new Comparator<OrderEntryData>()
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{

				final String productName1 = o1.getProductData().getBaseProductName();
				final String productName2 = o2.getProductData().getBaseProductName();
				final int sComp = productName1.compareTo(productName2);

				if (sComp != 0)
				{
					return sComp;
				}

				if (StringUtils.isNotBlank(o1.getEyeSight()) && StringUtils.isNotBlank(o2.getEyeSight()))
				{
					final String eyeSight1 = o1.getEyeSight();
					final String eyeSight2 = o2.getEyeSight();
					return (eyeSight1.compareTo(eyeSight2)) * (-1);
				}
				return sComp;
			}
		});

		return entries;
	}

	private static CartData loadCartData()
	{
		final CartData c = new CartData();
		final List<OrderEntryData> entries = new ArrayList<>();

		final OrderEntryData oe1 = new OrderEntryData();
		oe1.setPatientName("anish Adarakar");
		final ProductData p1 = new ProductData();
		p1.setBaseProductName("ABILIES TOTAL1® - 30 Pack");
		oe1.setProductData(p1);
		oe1.setEyeSight("Left");
		oe1.setProductData(p1);

		final OrderEntryData oe2 = new OrderEntryData();
		oe2.setPatientName("Test Patient");
		final ProductData p2 = new ProductData();
		p2.setBaseProductName("DAILIES® AquaComfort Plus® - 30 Pack");
		oe2.setEyeSight("Left");
		oe2.setProductData(p2);

		final OrderEntryData oe3 = new OrderEntryData();
		oe3.setPatientName("Karibasappa G");
		final ProductData p3 = new ProductData();
		//p3.setBaseProductName("DAILIES® AquaComfort Plus® - 30 Pack");
		oe3.setEyeSight("Right");
		oe3.setProductData(p3);

		final OrderEntryData oe4 = new OrderEntryData();
		oe4.setPatientName("Anish Adarakar");
		final ProductData p4 = new ProductData();
		p4.setBaseProductName("ABILIES TOTAL1® - 30 Pack");
		//oe4.setEyeSight("Right");
		oe4.setProductData(p4);

		final OrderEntryData oe5 = new OrderEntryData();
		oe5.setPatientName("Abhishek");
		final ProductData p5 = new ProductData();
		p5.setBaseProductName("DAILIES TOTAL1® - 30 Pack");
		oe5.setEyeSight("Left");
		oe5.setProductData(p5);

		final OrderEntryData oe6 = new OrderEntryData();
		final ProductData p6 = new ProductData();
		p6.setBaseProductName("DAILIES TOTAL1® Multifocal - 90 Pack - Med");
		oe6.setEyeSight("Right");
		oe6.setProductData(p6);

		final OrderEntryData oe7 = new OrderEntryData();
		final ProductData p7 = new ProductData();
		p7.setBaseProductName("DAILIES TOTAL1® Multifocal - 30 Pack - Med");
		oe7.setEyeSight("Right");
		oe7.setProductData(p7);
		entries.add(oe1);
		entries.add(oe2);
		entries.add(oe3);
		entries.add(oe4);
		entries.add(oe5);

		entries.add(oe6);
		entries.add(oe7);
		c.setEntries(entries);
		return c;
	}

}
