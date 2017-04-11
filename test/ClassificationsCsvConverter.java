/**
 * 
 */
package com.ssl.mdm.product.file.convert.csv.impl;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.csvreader.CsvWriter;
import com.ssl.mdm.constants.SslmdmintegrationConstants;
import com.ssl.mdm.dao.ProductFeedDao;
import com.ssl.mdm.dto.ClassificationDTO;
import com.ssl.mdm.dto.ProductDTO;
import com.ssl.mdm.dto.ProductsDTO;
import com.ssl.mdm.product.file.convert.csv.FileConversionStrategy;
import com.ssl.mdm.product.util.DTOUtils;
import com.ssl.mdm.product.util.SSLCSVUtils;


/**
 * @author Techouts
 *
 */
public class ClassificationsCsvConverter implements FileConversionStrategy<List<File>, ProductsDTO>
{
	private static Logger LOG = Logger.getLogger(ClassificationsCsvConverter.class);
	private static final String CLASSIFICATION_PRODUCT_FILENAME = "ssl.mdm.classification.csvfile.name";
	private static final String CLASSIFICATION_ASSIGNMENT_FILENAME = "ssl.mdm.classification.assignment.csvfile.name";
	private static final String CLASSIFICATION_ATTRIBUTE_VALUE_FILENAME = "ssl.mdm.classification.attribute.value.csvfile.name";
	/** ProductFeed Dao. **/
	private ProductFeedDao productFeedDao;
	/** Product Feed Validator. **/
	private Validator productFeedValidator;
	private Validator classificationValidator;
	/**SearchRestriction Service**/
	private SearchRestrictionService searchRestrictionService;

	/**
	 * @Descrption Create the Classification CSV file from Product DTO
	 * 
	 * @param productsDto
	 * @return List<File>
	 * 
	 */
	@Override
	public List<File> convertTocsv(ProductsDTO productsDto) throws IOException
	{

		List<File> classficationFiles = new ArrayList<File>();
		CsvWriter csvWriter = null;
		Date fileDate=new Date();
		try
		{
			LOG.info("Preparing the Classification CSV to test");
			//creates the csvwriter from the  dir and filename
			csvWriter = SSLCSVUtils.createCSVWriter(Config.getParameter(CLASSIFICATION_PRODUCT_FILENAME),fileDate,SslmdmintegrationConstants.constants.CSV_IMPEX_SEPARATOR);
			if (csvWriter == null)
			{
				return null;
			}
			for (ProductDTO product : productsDto.getProducts())
			{
				final Errors errors = new BeanPropertyBindingResult(product, "productDto");
				/*****************************
				 * Validating Product DTO Mandatory fileds
				 *****************************/
				getProductFeedValidator().validate(product, errors);
				if (errors.hasErrors())
				{
					SSLCSVUtils.generateErrorLog(errors, product);
					LOG.error("Classification does't update/create  due to Missing mandatory fields in ProductDTO or non valid data types in product DTO ");
				}
				else
				{
					List<ClassificationDTO> classificationList=new ArrayList<ClassificationDTO>();
					for(ClassificationDTO classification:product.getClassificationAttributes()){
					 final Errors classificationErrors = new BeanPropertyBindingResult(classification, "classification");
					 getClassificationValidator().validate(classification, classificationErrors);
					 if(classificationErrors.hasErrors()){
						 SSLCSVUtils.generateErrorLog(classificationErrors, classification);
					 }else
					 {
						 classificationList.add(classification);
					 }
					}
					product.setClassificationAttributes(classificationList);
					//preparing the classification csv Header 
					LOG.info("Praparing the Classification CSV HEADER ");
					if(CollectionUtils.isNotEmpty(product.getClassificationAttributes())){
					String classifcationClass=writeClassificationHeaderToCsv(product, csvWriter, classficationFiles);
					if(StringUtils.isNotEmpty(classifcationClass)){
						//preparing the classification csv Data 
						LOG.info("Praparing the Classification CSV  DATA");
						writeClassificationDataToCSV(product, csvWriter);
					 }
					else{
						LOG.info("Classification Class not Found for given Classifcation attributes");
						csvWriter=null;
						return classficationFiles;
					}
					}
				}
			}
		}
		catch (Exception ex)
		{
			LOG.error("Exception occured while Creating the Classification CSV ", ex);
		}
		finally
		{
			if (csvWriter != null)
			{
				csvWriter.flush();
				csvWriter.close();
			}
		}
		classficationFiles.add(SSLCSVUtils.getFile(Config.getParameter(CLASSIFICATION_PRODUCT_FILENAME),fileDate));

		return classficationFiles;
	}

	/**
	 * @DESCRIPTION write the classification CSV Row values
	 * 
	 * @param product
	 * @param csvWriter
	 * @throws IOException
	 */
	private void writeClassificationDataToCSV(ProductDTO product, CsvWriter csvWriter) throws IOException
	{
		csvWriter.write(SslmdmintegrationConstants.constants.APPAREL_PRODUCT);
		csvWriter.write(product.getCode());
		csvWriter.write(product.getGlobalAttributes().getApprovalStatus());
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.write("y");
		//preparing the values  for dynamically created classification header
		for (ClassificationDTO classification : product.getClassificationAttributes())
		{
			csvWriter.write(classification.getValue());
		}
		csvWriter.endRecord();
	}

	/**
	 * @DESCRIPTION write the classification CSV Header
	 * 
	 * @param product
	 * @param csvWriter
	 * @param classficationFiles
	 * @throws IOException
	 */
	private String writeClassificationHeaderToCsv(ProductDTO product, CsvWriter csvWriter, List<File> classficationFiles)
			throws IOException
	{
		//static header
		StringBuilder header = new StringBuilder(
				"UPDATE ApparelProduct;code[unique=true];approvalstatus(code)[allownull=true];$catalogVersion;attributionFlag");
		String classificationClass = null;
		CsvWriter assignmentCSVWriter = null;
		CsvWriter attributeValueCSVWriter = null;
		Date fileDate = new Date();
		try
		{
			//dynamically creating the header for each classification
			for (ClassificationDTO classification : product.getClassificationAttributes())
			{

				//get ClassAttributeAssignment from  the the category Codes list and attribute type
				searchRestrictionService.disableSearchRestrictions();
				ClassAttributeAssignmentModel classAttributeAssignment = getProductFeedDao().findClassAttributeAssignment(
						DTOUtils.getCategoryCodesFromCategory(product.getCategory()),
						Collections.singletonList(classification.getAttributeName()));
				searchRestrictionService.enableSearchRestrictions();
				if (classAttributeAssignment != null)
				{
					ClassificationAttributeValueModel classificationAttributeValue = getProductFeedDao().findAttributeValue(
							classification.getValue());
					//if classificationAttributeValue is not exist then creating the new csv file to insert the new attributevalue
					if (classificationAttributeValue == null)
					{
						if (attributeValueCSVWriter == null)
							attributeValueCSVWriter = SSLCSVUtils.createCSVWriter(
									Config.getParameter(CLASSIFICATION_ATTRIBUTE_VALUE_FILENAME), fileDate,SslmdmintegrationConstants.constants.CSV_TEXT_SEPARATOR);
						LOG.info("ClassificationAttributeValue is not exist  by this code [" + classification.getValue()
								+ " ] creating the ClassificationAttributeValue CSV file");
						//preparing the data to ClassificationAttributeValue CSV
						writeClassAttributeValueDataToCSV(classification.getValue(), attributeValueCSVWriter);
					}
					List<String> attributeValues = new ArrayList<String>();
					for (ClassificationAttributeValueModel ClassificationAttrValue : classAttributeAssignment.getAttributeValues())
					{
						attributeValues.add(ClassificationAttrValue.getCode());
					}
					if (!attributeValues.contains(classification.getValue()))
					{
						//ClassificationClassAttributeAssignment is not exist by attribute type and attribute value and creating the 
						//ClassificationClassAttributeAssignment CSV
						if (assignmentCSVWriter == null)
							assignmentCSVWriter = SSLCSVUtils.createCSVWriter(Config.getParameter(CLASSIFICATION_ASSIGNMENT_FILENAME),
									fileDate,SslmdmintegrationConstants.constants.CSV_TEXT_SEPARATOR);
						LOG.info("ClassificationClassAttributeAssignment not contain the  this ClassificationAttributeValue ["
								+ classification.getValue() + " ] creating the ClassificationClassAttributeAssignment CSV file");
						// peaparing the ClassificationClassAttributeAssignment CSV Header
						writeClassAttributeAssignmentDataToCSV(classAttributeAssignment.getClassificationClass().getCode(),
								classAttributeAssignment.getClassificationAttribute().getCode(), classification.getValue(),
								assignmentCSVWriter);
					}
					classificationClass = classAttributeAssignment.getClassificationClass().getCode();
				}
				//appending the dynamically created header to static header
				header.append(";@" + classification.getAttributeName() + "[$clAttrModifiers,class=" + classificationClass + "]");

			}
		}
		catch (Exception ex)
		{
			searchRestrictionService.enableSearchRestrictions();
			LOG.error(
					"Exception occured while preparing the ClassificationClassAttributeAssignment CSV  and ClassificationAttributeValue",
					ex);
		}
		finally
		{
			if (assignmentCSVWriter != null)
			{
				assignmentCSVWriter.flush();
				assignmentCSVWriter.close();
				classficationFiles.add(SSLCSVUtils.getFile(Config.getParameter(CLASSIFICATION_ASSIGNMENT_FILENAME), fileDate));
			}
			if(attributeValueCSVWriter!=null){
				attributeValueCSVWriter.flush();
				attributeValueCSVWriter.close();
				classficationFiles.add(SSLCSVUtils.getFile(Config.getParameter(CLASSIFICATION_ATTRIBUTE_VALUE_FILENAME), fileDate));
			}
		}
	
	
		LOG.info("Classification CSV HEADER \n" + header.toString());
		csvWriter.write(header.toString());
		csvWriter.endRecord();
		return classificationClass;
	}

	/**
	 * @DESCRIPTION write the Classification Attribute value CSV Data
	 * 
	 * @param value
	 * @param attributeValueCSVWriter
	 * @throws IOException
	 */
	private void writeClassAttributeValueDataToCSV(String value, CsvWriter attributeValueCSVWriter) throws IOException
	{
		attributeValueCSVWriter.write(value);//0
		attributeValueCSVWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);//1
		attributeValueCSVWriter.write(value);//2
		attributeValueCSVWriter.endRecord();
	}

	/**
	 * @DESCRIPTION write the ClassAttributeAssignment CSV Data
	 * 
	 * @param classificationClass
	 * @param classificationAttribute
	 * @param classificationvalue
	 * @param csvWriter
	 * @throws IOException
	 */
	private void writeClassAttributeAssignmentDataToCSV(String classificationClass, String classificationAttribute,
			String classificationvalue, CsvWriter csvWriter) throws IOException
	{
		csvWriter.write(classificationClass);
		csvWriter.write(classificationAttribute);
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.write(classificationvalue);
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.write(SslmdmintegrationConstants.constants.EMPTY_STRING);
		csvWriter.endRecord();
	}

	/**
	 * @return the productFeedDao
	 */
	public ProductFeedDao getProductFeedDao()
	{
		return productFeedDao;
	}

	/**
	 * @param productFeedDao
	 *           the productFeedDao to set
	 */
	public void setProductFeedDao(ProductFeedDao productFeedDao)
	{
		this.productFeedDao = productFeedDao;
	}

	/**
	 * @return the productFeedValidator
	 */
	public Validator getProductFeedValidator()
	{
		return productFeedValidator;
	}

	/**
	 * @param productFeedValidator
	 *           the productFeedValidator to set
	 */
	public void setProductFeedValidator(Validator productFeedValidator)
	{
		this.productFeedValidator = productFeedValidator;
	}

	/**
	 * @return the classificationValidator
	 */
	public Validator getClassificationValidator()
	{
		return classificationValidator;
	}

	/**
	 * @param classificationValidator the classificationValidator to set
	 */
	public void setClassificationValidator(Validator classificationValidator)
	{
		this.classificationValidator = classificationValidator;
	}

	/**
	 * @return the searchRestrictionService
	 */
	public SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	/**
	 * @param searchRestrictionService the searchRestrictionService to set
	 */
	public void setSearchRestrictionService(SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}
}
