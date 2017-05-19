/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for I_ProductPriceRate
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0
 */
public interface I_I_ProductPriceRate 
{

    /** TableName=I_ProductPriceRate */
    public static final String Table_Name = "I_ProductPriceRate";

    /** AD_Table_ID=3000266 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name BreakValue_0 */
    public static final String COLUMNNAME_BreakValue_0 = "BreakValue_0";

	/** Set BreakValue_0	  */
	public void setBreakValue_0 (BigDecimal BreakValue_0);

	/** Get BreakValue_0	  */
	public BigDecimal getBreakValue_0();

    /** Column name BreakValue_1 */
    public static final String COLUMNNAME_BreakValue_1 = "BreakValue_1";

	/** Set BreakValue_1	  */
	public void setBreakValue_1 (BigDecimal BreakValue_1);

	/** Get BreakValue_1	  */
	public BigDecimal getBreakValue_1();

    /** Column name BreakValue_2 */
    public static final String COLUMNNAME_BreakValue_2 = "BreakValue_2";

	/** Set BreakValue_2	  */
	public void setBreakValue_2 (BigDecimal BreakValue_2);

	/** Get BreakValue_2	  */
	public BigDecimal getBreakValue_2();

    /** Column name BreakValue_3 */
    public static final String COLUMNNAME_BreakValue_3 = "BreakValue_3";

	/** Set BreakValue_3	  */
	public void setBreakValue_3 (BigDecimal BreakValue_3);

	/** Get BreakValue_3	  */
	public BigDecimal getBreakValue_3();

    /** Column name BreakValue_4 */
    public static final String COLUMNNAME_BreakValue_4 = "BreakValue_4";

	/** Set BreakValue_4	  */
	public void setBreakValue_4 (BigDecimal BreakValue_4);

	/** Get BreakValue_4	  */
	public BigDecimal getBreakValue_4();

    /** Column name BreakValue_5 */
    public static final String COLUMNNAME_BreakValue_5 = "BreakValue_5";

	/** Set BreakValue_5	  */
	public void setBreakValue_5 (BigDecimal BreakValue_5);

	/** Get BreakValue_5	  */
	public BigDecimal getBreakValue_5();

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_UOM_Volume_ID */
    public static final String COLUMNNAME_C_UOM_Volume_ID = "C_UOM_Volume_ID";

	/** Set UOM for Volume.
	  * Standard Unit of Measure for Volume
	  */
	public void setC_UOM_Volume_ID (int C_UOM_Volume_ID);

	/** Get UOM for Volume.
	  * Standard Unit of Measure for Volume
	  */
	public int getC_UOM_Volume_ID();

	public org.compiere.model.I_C_UOM getC_UOM_Volume() throws RuntimeException;

    /** Column name C_UOM_Weight_ID */
    public static final String COLUMNNAME_C_UOM_Weight_ID = "C_UOM_Weight_ID";

	/** Set UOM for Weight.
	  * Standard Unit of Measure for Weight
	  */
	public void setC_UOM_Weight_ID (int C_UOM_Weight_ID);

	/** Get UOM for Weight.
	  * Standard Unit of Measure for Weight
	  */
	public int getC_UOM_Weight_ID();

	public org.compiere.model.I_C_UOM getC_UOM_Weight() throws RuntimeException;

    /** Column name Carrier_Name */
    public static final String COLUMNNAME_Carrier_Name = "Carrier_Name";

	/** Set Carrier_Name	  */
	public void setCarrier_Name (String Carrier_Name);

	/** Get Carrier_Name	  */
	public String getCarrier_Name();

    /** Column name chargeAmt_0 */
    public static final String COLUMNNAME_chargeAmt_0 = "chargeAmt_0";

	/** Set chargeAmt_0	  */
	public void setchargeAmt_0 (int chargeAmt_0);

	/** Get chargeAmt_0	  */
	public int getchargeAmt_0();

	public I_C_ValidCombination getchargeAm() throws RuntimeException;

    /** Column name chargeAmt_1 */
    public static final String COLUMNNAME_chargeAmt_1 = "chargeAmt_1";

	/** Set chargeAmt_1	  */
	public void setchargeAmt_1 (BigDecimal chargeAmt_1);

	/** Get chargeAmt_1	  */
	public BigDecimal getchargeAmt_1();

    /** Column name chargeAmt_2 */
    public static final String COLUMNNAME_chargeAmt_2 = "chargeAmt_2";

	/** Set chargeAmt_2	  */
	public void setchargeAmt_2 (BigDecimal chargeAmt_2);

	/** Get chargeAmt_2	  */
	public BigDecimal getchargeAmt_2();

    /** Column name chargeAmt_3 */
    public static final String COLUMNNAME_chargeAmt_3 = "chargeAmt_3";

	/** Set chargeAmt_3	  */
	public void setchargeAmt_3 (BigDecimal chargeAmt_3);

	/** Get chargeAmt_3	  */
	public BigDecimal getchargeAmt_3();

    /** Column name chargeAmt_4 */
    public static final String COLUMNNAME_chargeAmt_4 = "chargeAmt_4";

	/** Set chargeAmt_4	  */
	public void setchargeAmt_4 (BigDecimal chargeAmt_4);

	/** Get chargeAmt_4	  */
	public BigDecimal getchargeAmt_4();

    /** Column name chargeAmt_5 */
    public static final String COLUMNNAME_chargeAmt_5 = "chargeAmt_5";

	/** Set chargeAmt_5	  */
	public void setchargeAmt_5 (BigDecimal chargeAmt_5);

	/** Get chargeAmt_5	  */
	public BigDecimal getchargeAmt_5();

    /** Column name CityFrom_Name */
    public static final String COLUMNNAME_CityFrom_Name = "CityFrom_Name";

	/** Set CityFrom_Name	  */
	public void setCityFrom_Name (String CityFrom_Name);

	/** Get CityFrom_Name	  */
	public String getCityFrom_Name();

    /** Column name CityTo_Name */
    public static final String COLUMNNAME_CityTo_Name = "CityTo_Name";

	/** Set CityTo_Name	  */
	public void setCityTo_Name (String CityTo_Name);

	/** Get CityTo_Name	  */
	public String getCityTo_Name();

    /** Column name commodityValue */
    public static final String COLUMNNAME_commodityValue = "commodityValue";

	/** Set commodityValue	  */
	public void setcommodityValue (String commodityValue);

	/** Get commodityValue	  */
	public String getcommodityValue();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

    /** Column name I_ProductPriceRate_ID */
    public static final String COLUMNNAME_I_ProductPriceRate_ID = "I_ProductPriceRate_ID";

	/** Set I_ProductPriceRate ID	  */
	public void setI_ProductPriceRate_ID (int I_ProductPriceRate_ID);

	/** Get I_ProductPriceRate ID	  */
	public int getI_ProductPriceRate_ID();

    /** Column name IATACode_Destiny */
    public static final String COLUMNNAME_IATACode_Destiny = "IATACode_Destiny";

	/** Set IATACode_Destiny	  */
	public void setIATACode_Destiny (String IATACode_Destiny);

	/** Get IATACode_Destiny	  */
	public String getIATACode_Destiny();

    /** Column name IATACode_Origin */
    public static final String COLUMNNAME_IATACode_Origin = "IATACode_Origin";

	/** Set IATACode_Origin	  */
	public void setIATACode_Origin (String IATACode_Origin);

	/** Get IATACode_Origin	  */
	public String getIATACode_Origin();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsSOTrx */
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";

	/** Set Sales Transaction.
	  * This is a Sales Transaction
	  */
	public void setIsSOTrx (String IsSOTrx);

	/** Get Sales Transaction.
	  * This is a Sales Transaction
	  */
	public String getIsSOTrx();

    /** Column name IsValid */
    public static final String COLUMNNAME_IsValid = "IsValid";

	/** Set Valid.
	  * Element is valid
	  */
	public void setIsValid (boolean IsValid);

	/** Get Valid.
	  * Element is valid
	  */
	public boolean isValid();

    /** Column name LG_AirPort_Destiny_ID */
    public static final String COLUMNNAME_LG_AirPort_Destiny_ID = "LG_AirPort_Destiny_ID";

	/** Set Airport Destino	  */
	public void setLG_AirPort_Destiny_ID (int LG_AirPort_Destiny_ID);

	/** Get Airport Destino	  */
	public int getLG_AirPort_Destiny_ID();

    /** Column name LG_AirPort_Origin_ID */
    public static final String COLUMNNAME_LG_AirPort_Origin_ID = "LG_AirPort_Origin_ID";

	/** Set LG_AirPort_Origin_ID	  */
	public void setLG_AirPort_Origin_ID (int LG_AirPort_Origin_ID);

	/** Get LG_AirPort_Origin_ID	  */
	public int getLG_AirPort_Origin_ID();

    /** Column name LG_CityFrom_ID */
    public static final String COLUMNNAME_LG_CityFrom_ID = "LG_CityFrom_ID";

	/** Set LG_CityFrom_ID	  */
	public void setLG_CityFrom_ID (int LG_CityFrom_ID);

	/** Get LG_CityFrom_ID	  */
	public int getLG_CityFrom_ID();

	public org.compiere.model.I_C_City getLG_CityFrom() throws RuntimeException;

    /** Column name LG_CityTo_ID */
    public static final String COLUMNNAME_LG_CityTo_ID = "LG_CityTo_ID";

	/** Set LG_CityTo_ID	  */
	public void setLG_CityTo_ID (int LG_CityTo_ID);

	/** Get LG_CityTo_ID	  */
	public int getLG_CityTo_ID();

	public org.compiere.model.I_C_City getLG_CityTo() throws RuntimeException;

    /** Column name LG_Commodity_ID */
    public static final String COLUMNNAME_LG_Commodity_ID = "LG_Commodity_ID";

	/** Set Commodity ID	  */
	public void setLG_Commodity_ID (int LG_Commodity_ID);

	/** Get Commodity ID	  */
	public int getLG_Commodity_ID();

    /** Column name LG_ProductPriceRate_ID */
    public static final String COLUMNNAME_LG_ProductPriceRate_ID = "LG_ProductPriceRate_ID";

	/** Set LG_ProductPriceRate ID	  */
	public void setLG_ProductPriceRate_ID (int LG_ProductPriceRate_ID);

	/** Get LG_ProductPriceRate ID	  */
	public int getLG_ProductPriceRate_ID();


    /** Column name LG_RateType */
    public static final String COLUMNNAME_LG_RateType = "LG_RateType";

	/** Set LG_RateType	  */
	public void setLG_RateType (String LG_RateType);

	/** Get LG_RateType	  */
	public String getLG_RateType();

    /** Column name LG_Route_ID */
    public static final String COLUMNNAME_LG_Route_ID = "LG_Route_ID";

	/** Set LG_Route ID	  */
	public void setLG_Route_ID (int LG_Route_ID);

	/** Get LG_Route ID	  */
	public int getLG_Route_ID();


    /** Column name LG_ShippingMode */
    public static final String COLUMNNAME_LG_ShippingMode = "LG_ShippingMode";

	/** Set LG_ShippingMode	  */
	public void setLG_ShippingMode (String LG_ShippingMode);

	/** Get LG_ShippingMode	  */
	public String getLG_ShippingMode();

    /** Column name LG_ShippingMode_Destiny */
    public static final String COLUMNNAME_LG_ShippingMode_Destiny = "LG_ShippingMode_Destiny";

	/** Set LG_ShippingMode_Destiny	  */
	public void setLG_ShippingMode_Destiny (String LG_ShippingMode_Destiny);

	/** Get LG_ShippingMode_Destiny	  */
	public String getLG_ShippingMode_Destiny();

    /** Column name LG_ShippingMode_Name */
    public static final String COLUMNNAME_LG_ShippingMode_Name = "LG_ShippingMode_Name";

	/** Set LG_ShippingMode_Name	  */
	public void setLG_ShippingMode_Name (String LG_ShippingMode_Name);

	/** Get LG_ShippingMode_Name	  */
	public String getLG_ShippingMode_Name();

    /** Column name LG_ShippingMode_Origin */
    public static final String COLUMNNAME_LG_ShippingMode_Origin = "LG_ShippingMode_Origin";

	/** Set LG_ShippingMode_Origin	  */
	public void setLG_ShippingMode_Origin (String LG_ShippingMode_Origin);

	/** Get LG_ShippingMode_Origin	  */
	public String getLG_ShippingMode_Origin();

    /** Column name LG_TransportType */
    public static final String COLUMNNAME_LG_TransportType = "LG_TransportType";

	/** Set Tipo transporte	  */
	public void setLG_TransportType (String LG_TransportType);

	/** Get Tipo transporte	  */
	public String getLG_TransportType();

    /** Column name M_Product_0_ID */
    public static final String COLUMNNAME_M_Product_0_ID = "M_Product_0_ID";

	/** Set M_Product_0_ID	  */
	public void setM_Product_0_ID (int M_Product_0_ID);

	/** Get M_Product_0_ID	  */
	public int getM_Product_0_ID();

	public org.compiere.model.I_M_Product getM_Product_0() throws RuntimeException;

    /** Column name M_Product_1_ID */
    public static final String COLUMNNAME_M_Product_1_ID = "M_Product_1_ID";

	/** Set M_Product_1_ID	  */
	public void setM_Product_1_ID (int M_Product_1_ID);

	/** Get M_Product_1_ID	  */
	public int getM_Product_1_ID();

	public org.compiere.model.I_M_Product getM_Product_1() throws RuntimeException;

    /** Column name M_Product_2_ID */
    public static final String COLUMNNAME_M_Product_2_ID = "M_Product_2_ID";

	/** Set M_Product_2_ID	  */
	public void setM_Product_2_ID (int M_Product_2_ID);

	/** Get M_Product_2_ID	  */
	public int getM_Product_2_ID();

	public org.compiere.model.I_M_Product getM_Product_2() throws RuntimeException;

    /** Column name M_Product_3_ID */
    public static final String COLUMNNAME_M_Product_3_ID = "M_Product_3_ID";

	/** Set M_Product_3_ID	  */
	public void setM_Product_3_ID (int M_Product_3_ID);

	/** Get M_Product_3_ID	  */
	public int getM_Product_3_ID();

	public org.compiere.model.I_M_Product getM_Product_3() throws RuntimeException;

    /** Column name M_Product_4_ID */
    public static final String COLUMNNAME_M_Product_4_ID = "M_Product_4_ID";

	/** Set M_Product_4_ID	  */
	public void setM_Product_4_ID (int M_Product_4_ID);

	/** Get M_Product_4_ID	  */
	public int getM_Product_4_ID();

	public org.compiere.model.I_M_Product getM_Product_4() throws RuntimeException;

    /** Column name M_Product_5_ID */
    public static final String COLUMNNAME_M_Product_5_ID = "M_Product_5_ID";

	/** Set M_Product_5_ID	  */
	public void setM_Product_5_ID (int M_Product_5_ID);

	/** Get M_Product_5_ID	  */
	public int getM_Product_5_ID();

	public org.compiere.model.I_M_Product getM_Product_5() throws RuntimeException;

    /** Column name M_Shipper_ID */
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";

	/** Set Shipper.
	  * Method or manner of product delivery
	  */
	public void setM_Shipper_ID (int M_Shipper_ID);

	/** Get Shipper.
	  * Method or manner of product delivery
	  */
	public int getM_Shipper_ID();

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException;

    /** Column name MinimumAmt */
    public static final String COLUMNNAME_MinimumAmt = "MinimumAmt";

	/** Set Minimum Amt.
	  * Minimum Amount in Document Currency
	  */
	public void setMinimumAmt (BigDecimal MinimumAmt);

	/** Get Minimum Amt.
	  * Minimum Amount in Document Currency
	  */
	public BigDecimal getMinimumAmt();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name OWS */
    public static final String COLUMNNAME_OWS = "OWS";

	/** Set OWS	  */
	public void setOWS (boolean OWS);

	/** Get OWS	  */
	public boolean isOWS();

    /** Column name PriceList_0 */
    public static final String COLUMNNAME_PriceList_0 = "PriceList_0";

	/** Set PriceList_0	  */
	public void setPriceList_0 (BigDecimal PriceList_0);

	/** Get PriceList_0	  */
	public BigDecimal getPriceList_0();

    /** Column name PriceList_1 */
    public static final String COLUMNNAME_PriceList_1 = "PriceList_1";

	/** Set PriceList_1	  */
	public void setPriceList_1 (BigDecimal PriceList_1);

	/** Get PriceList_1	  */
	public BigDecimal getPriceList_1();

    /** Column name PriceList_2 */
    public static final String COLUMNNAME_PriceList_2 = "PriceList_2";

	/** Set PriceList_2	  */
	public void setPriceList_2 (BigDecimal PriceList_2);

	/** Get PriceList_2	  */
	public BigDecimal getPriceList_2();

    /** Column name PriceList_3 */
    public static final String COLUMNNAME_PriceList_3 = "PriceList_3";

	/** Set PriceList_3	  */
	public void setPriceList_3 (BigDecimal PriceList_3);

	/** Get PriceList_3	  */
	public BigDecimal getPriceList_3();

    /** Column name PriceList_4 */
    public static final String COLUMNNAME_PriceList_4 = "PriceList_4";

	/** Set PriceList_4	  */
	public void setPriceList_4 (BigDecimal PriceList_4);

	/** Get PriceList_4	  */
	public BigDecimal getPriceList_4();

    /** Column name PriceList_5 */
    public static final String COLUMNNAME_PriceList_5 = "PriceList_5";

	/** Set PriceList_5	  */
	public void setPriceList_5 (BigDecimal PriceList_5);

	/** Get PriceList_5	  */
	public BigDecimal getPriceList_5();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name routename */
    public static final String COLUMNNAME_routename = "routename";

	/** Set routename	  */
	public void setroutename (String routename);

	/** Get routename	  */
	public String getroutename();

    /** Column name Shipper_Name */
    public static final String COLUMNNAME_Shipper_Name = "Shipper_Name";

	/** Set Shipper_Name	  */
	public void setShipper_Name (String Shipper_Name);

	/** Get Shipper_Name	  */
	public String getShipper_Name();

    /** Column name surcharge_0_ID */
    public static final String COLUMNNAME_surcharge_0_ID = "surcharge_0_ID";

	/** Set surcharge_0_ID	  */
	public void setsurcharge_0_ID (int surcharge_0_ID);

	/** Get surcharge_0_ID	  */
	public int getsurcharge_0_ID();

	public org.compiere.model.I_M_Product getsurcharge_0() throws RuntimeException;

    /** Column name surcharge_1_ID */
    public static final String COLUMNNAME_surcharge_1_ID = "surcharge_1_ID";

	/** Set surcharge_1_ID	  */
	public void setsurcharge_1_ID (int surcharge_1_ID);

	/** Get surcharge_1_ID	  */
	public int getsurcharge_1_ID();

	public org.compiere.model.I_M_Product getsurcharge_1() throws RuntimeException;

    /** Column name surcharge_2_ID */
    public static final String COLUMNNAME_surcharge_2_ID = "surcharge_2_ID";

	/** Set surcharge_2_ID	  */
	public void setsurcharge_2_ID (int surcharge_2_ID);

	/** Get surcharge_2_ID	  */
	public int getsurcharge_2_ID();

	public org.compiere.model.I_M_Product getsurcharge_2() throws RuntimeException;

    /** Column name surcharge_3_ID */
    public static final String COLUMNNAME_surcharge_3_ID = "surcharge_3_ID";

	/** Set surcharge_3_ID	  */
	public void setsurcharge_3_ID (int surcharge_3_ID);

	/** Get surcharge_3_ID	  */
	public int getsurcharge_3_ID();

	public org.compiere.model.I_M_Product getsurcharge_3() throws RuntimeException;

    /** Column name surcharge_4_ID */
    public static final String COLUMNNAME_surcharge_4_ID = "surcharge_4_ID";

	/** Set surcharge_4_ID	  */
	public void setsurcharge_4_ID (int surcharge_4_ID);

	/** Get surcharge_4_ID	  */
	public int getsurcharge_4_ID();

	public org.compiere.model.I_M_Product getsurcharge_4() throws RuntimeException;

    /** Column name surcharge_5_ID */
    public static final String COLUMNNAME_surcharge_5_ID = "surcharge_5_ID";

	/** Set surcharge_5_ID	  */
	public void setsurcharge_5_ID (int surcharge_5_ID);

	/** Get surcharge_5_ID	  */
	public int getsurcharge_5_ID();

	public org.compiere.model.I_M_Product getsurcharge_5() throws RuntimeException;

    /** Column name TransferTime */
    public static final String COLUMNNAME_TransferTime = "TransferTime";

	/** Set Transfer Time.
	  * Transfer Time
	  */
	public void setTransferTime (BigDecimal TransferTime);

	/** Get Transfer Time.
	  * Transfer Time
	  */
	public BigDecimal getTransferTime();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name ValidFrom */
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";

	/** Set Valid from.
	  * Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom);

	/** Get Valid from.
	  * Valid from including this date (first day)
	  */
	public Timestamp getValidFrom();

    /** Column name ValidTo */
    public static final String COLUMNNAME_ValidTo = "ValidTo";

	/** Set Valid to.
	  * Valid to including this date (last day)
	  */
	public void setValidTo (Timestamp ValidTo);

	/** Get Valid to.
	  * Valid to including this date (last day)
	  */
	public Timestamp getValidTo();
}
