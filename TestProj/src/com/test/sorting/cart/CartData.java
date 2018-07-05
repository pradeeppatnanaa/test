package com.test.sorting.cart;

import java.util.List;
import java.util.Map;


public class CartData
{

	List<OrderEntryData> entries;
	List<OrderEntryData> entriesWithoutPatientName;
	/**
	 * Map for entries to store with patient name as group by result <br/>
	 * <br/>
	 * <i>Generated property</i> for <code>AbstractOrderData.entriesWithPatientName</code> property defined at extension
	 * <code>alconvisioncareaddon</code>.
	 */

	private Map<String, List<OrderEntryData>> entriesWithPatientName;

	/**
	 * @return the entries
	 */
	public List<OrderEntryData> getEntries()
	{
		return entries;
	}

	/**
	 * @param entries
	 *           the entries to set
	 */
	public void setEntries(final List<OrderEntryData> entries)
	{
		this.entries = entries;
	}

	/**
	 * @return the entriesWithoutPatientName
	 */
	public List<OrderEntryData> getEntriesWithoutPatientName()
	{
		return entriesWithoutPatientName;
	}

	/**
	 * @param entriesWithoutPatientName
	 *           the entriesWithoutPatientName to set
	 */
	public void setEntriesWithoutPatientName(final List<OrderEntryData> entriesWithoutPatientName)
	{
		this.entriesWithoutPatientName = entriesWithoutPatientName;
	}

	/**
	 * @return the entriesWithPatientName
	 */
	public Map<String, List<OrderEntryData>> getEntriesWithPatientName()
	{
		return entriesWithPatientName;
	}

	/**
	 * @param entriesWithPatientName
	 *           the entriesWithPatientName to set
	 */
	public void setEntriesWithPatientName(final Map<String, List<OrderEntryData>> entriesWithPatientName)
	{
		this.entriesWithPatientName = entriesWithPatientName;
	}

}
