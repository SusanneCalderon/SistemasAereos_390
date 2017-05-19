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
package org.shw.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.compiere.model.MTable;
import org.compiere.util.KeyNamePair;

/** Generated Interface for T_ProductPriceRateLine
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0
 */
public interface I_T_ProductPriceRateLine 
{

    /** TableName=T_ProductPriceRateLine */
    public static final String Table_Name = "T_ProductPriceRateLine";

    /** AD_Table_ID=3000213 */
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

    /** Column name AD_PInstance_ID */
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";

	/** Set Process Instance.
	  * Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID);

	/** Get Process Instance.
	  * Instance of the process
	  */
	public int getAD_PInstance_ID();

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException;

    /** Column name BreakValue */
    public static final String COLUMNNAME_BreakValue = "BreakValue";

	/** Set Break Value.
	  * Low Value of trade discount break level
	  */
	public void setBreakValue (BigDecimal BreakValue);

	/** Get Break Value.
	  * Low Value of trade discount break level
	  */
	public BigDecimal getBreakValue();

    /** Column name BreakValueVolume */
    public static final String COLUMNNAME_BreakValueVolume = "BreakValueVolume";

	/** Set BreakValueVolume.
	  * Low Value of trade discount break level
	  */
	public void setBreakValueVolume (BigDecimal BreakValueVolume);

	/** Get BreakValueVolume.
	  * Low Value of trade discount break level
	  */
	public BigDecimal getBreakValueVolume();

    /** Column name BreakValueWeight */
    public static final String COLUMNNAME_BreakValueWeight = "BreakValueWeight";

	/** Set BreakValueWeight.
	  * Low Value of trade discount break level
	  */
	public void setBreakValueWeight (BigDecimal BreakValueWeight);

	/** Get BreakValueWeight.
	  * Low Value of trade discount break level
	  */
	public BigDecimal getBreakValueWeight();

    /** Column name calculatedIncome */
    public static final String COLUMNNAME_calculatedIncome = "calculatedIncome";

	/** Set calculatedIncome	  */
	public void setcalculatedIncome (BigDecimal calculatedIncome);

	/** Get calculatedIncome	  */
	public BigDecimal getcalculatedIncome();

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

    /** Column name ControlAmt */
    public static final String COLUMNNAME_ControlAmt = "ControlAmt";

	/** Set Control Amount.
	  * If not zero, the Debit amount of the document must be equal this amount
	  */
	public void setControlAmt (BigDecimal ControlAmt);

	/** Get Control Amount.
	  * If not zero, the Debit amount of the document must be equal this amount
	  */
	public BigDecimal getControlAmt();

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

    /** Column name LG_ProductPriceRate_ID */
    public static final String COLUMNNAME_LG_ProductPriceRate_ID = "LG_ProductPriceRate_ID";

	/** Set LG_ProductPriceRate ID	  */
	public void setLG_ProductPriceRate_ID (int LG_ProductPriceRate_ID);

	/** Get LG_ProductPriceRate ID	  */
	public int getLG_ProductPriceRate_ID();

    /** Column name LG_ProductPriceRateLine_ID */
    public static final String COLUMNNAME_LG_ProductPriceRateLine_ID = "LG_ProductPriceRateLine_ID";

	/** Set LG_ProductPriceRateLine ID	  */
	public void setLG_ProductPriceRateLine_ID (int LG_ProductPriceRateLine_ID);

	/** Get LG_ProductPriceRateLine ID	  */
	public int getLG_ProductPriceRateLine_ID();

    /** Column name LG_Route_ID */
    public static final String COLUMNNAME_LG_Route_ID = "LG_Route_ID";

	/** Set LG_Route ID	  */
	public void setLG_Route_ID (int LG_Route_ID);

	/** Get LG_Route ID	  */
	public int getLG_Route_ID();

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

    /** Column name M_PriceList_ID */
    public static final String COLUMNNAME_M_PriceList_ID = "M_PriceList_ID";

	/** Set Price List.
	  * Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID);

	/** Get Price List.
	  * Unique identifier of a Price List
	  */
	public int getM_PriceList_ID();

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException;

    /** Column name M_PriceList_Version_ID */
    public static final String COLUMNNAME_M_PriceList_Version_ID = "M_PriceList_Version_ID";

	/** Set Price List Version.
	  * Identifies a unique instance of a Price List
	  */
	public void setM_PriceList_Version_ID (int M_PriceList_Version_ID);

	/** Get Price List Version.
	  * Identifies a unique instance of a Price List
	  */
	public int getM_PriceList_Version_ID();

	public org.compiere.model.I_M_PriceList_Version getM_PriceList_Version() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

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

    /** Column name PriceLimit */
    public static final String COLUMNNAME_PriceLimit = "PriceLimit";

	/** Set Limit Price.
	  * Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit);

	/** Get Limit Price.
	  * Lowest price for a product
	  */
	public BigDecimal getPriceLimit();

    /** Column name PriceList */
    public static final String COLUMNNAME_PriceList = "PriceList";

	/** Set List Price.
	  * List Price
	  */
	public void setPriceList (BigDecimal PriceList);

	/** Get List Price.
	  * List Price
	  */
	public BigDecimal getPriceList();

    /** Column name PriceStd */
    public static final String COLUMNNAME_PriceStd = "PriceStd";

	/** Set Standard Price.
	  * Standard Price
	  */
	public void setPriceStd (BigDecimal PriceStd);

	/** Get Standard Price.
	  * Standard Price
	  */
	public BigDecimal getPriceStd();

    /** Column name priceVolume */
    public static final String COLUMNNAME_priceVolume = "priceVolume";

	/** Set priceVolume	  */
	public void setpriceVolume (BigDecimal priceVolume);

	/** Get priceVolume	  */
	public BigDecimal getpriceVolume();

    /** Column name PriceWeight */
    public static final String COLUMNNAME_PriceWeight = "PriceWeight";

	/** Set PriceWeight	  */
	public void setPriceWeight (BigDecimal PriceWeight);

	/** Get PriceWeight	  */
	public BigDecimal getPriceWeight();

    /** Column name quantityvolumn */
    public static final String COLUMNNAME_quantityvolumn = "quantityvolumn";

	/** Set quantityvolumn	  */
	public void setquantityvolumn (BigDecimal quantityvolumn);

	/** Get quantityvolumn	  */
	public BigDecimal getquantityvolumn();

    /** Column name quantityweight */
    public static final String COLUMNNAME_quantityweight = "quantityweight";

	/** Set quantityweight	  */
	public void setquantityweight (BigDecimal quantityweight);

	/** Get quantityweight	  */
	public BigDecimal getquantityweight();

    /** Column name resultVolumn */
    public static final String COLUMNNAME_resultVolumn = "resultVolumn";

	/** Set resultVolumn	  */
	public void setresultVolumn (BigDecimal resultVolumn);

	/** Get resultVolumn	  */
	public BigDecimal getresultVolumn();

    /** Column name resultWeight */
    public static final String COLUMNNAME_resultWeight = "resultWeight";

	/** Set resultWeight	  */
	public void setresultWeight (BigDecimal resultWeight);

	/** Get resultWeight	  */
	public BigDecimal getresultWeight();

    /** Column name T_ProductPriceRateLine_ID */
    public static final String COLUMNNAME_T_ProductPriceRateLine_ID = "T_ProductPriceRateLine_ID";

	/** Set T_ProductPriceRateLine ID	  */
	public void setT_ProductPriceRateLine_ID (int T_ProductPriceRateLine_ID);

	/** Get T_ProductPriceRateLine ID	  */
	public int getT_ProductPriceRateLine_ID();

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
