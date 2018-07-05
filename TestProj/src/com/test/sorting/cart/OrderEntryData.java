package com.test.sorting.cart;

/**
 *
 */
public class OrderEntryData
{
	/**
	 *
	 */
	public String patientName;
	/**
	 *
	 */
	public ProductData productData;

	public String eyeSight;

	/**
	 * @return the eyeSight
	 */
	public String getEyeSight()
	{
		return eyeSight;
	}

	/**
	 * @param eyeSight
	 *           the eyeSight to set
	 */
	public void setEyeSight(final String eyeSight)
	{
		this.eyeSight = eyeSight;
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName()
	{
		return patientName;
	}

	/**
	 * @param patientName
	 *           the patientName to set
	 */
	public void setPatientName(final String patientName)
	{
		this.patientName = patientName;
	}

	/**
	 * @return the productData
	 */
	public ProductData getProductData()
	{
		return productData;
	}

	/**
	 * @param productData
	 *           the productData to set
	 */
	public void setProductData(final ProductData productData)
	{
		this.productData = productData;
	}

}
