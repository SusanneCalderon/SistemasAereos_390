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
/** Generated Model - DO NOT CHANGE */
package org.shw.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.Env;

/** Generated Model for T_ProductPriceRateLine
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0 - $Id$ */
public class X_T_ProductPriceRateLine extends PO implements I_T_ProductPriceRateLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20150604L;

    /** Standard Constructor */
    public X_T_ProductPriceRateLine (Properties ctx, int T_ProductPriceRateLine_ID, String trxName)
    {
      super (ctx, T_ProductPriceRateLine_ID, trxName);
      /** if (T_ProductPriceRateLine_ID == 0)
        {
			setBreakValue (Env.ZERO);
			setIsValid (true);
// Y
			setLG_ProductPriceRate_ID (0);
			setM_Product_ID (0);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setPriceStd (Env.ZERO);
			setpriceVolume (Env.ZERO);
// 0
			setPriceWeight (Env.ZERO);
// 0
			setT_ProductPriceRateLine_ID (0);
        } */
    }

    /** Load Constructor */
    public X_T_ProductPriceRateLine (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_T_ProductPriceRateLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PInstance)MTable.get(getCtx(), org.compiere.model.I_AD_PInstance.Table_Name)
			.getPO(getAD_PInstance_ID(), get_TrxName());	}

	/** Set Process Instance.
		@param AD_PInstance_ID 
		Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID)
	{
		if (AD_PInstance_ID < 1) 
			set_Value (COLUMNNAME_AD_PInstance_ID, null);
		else 
			set_Value (COLUMNNAME_AD_PInstance_ID, Integer.valueOf(AD_PInstance_ID));
	}

	/** Get Process Instance.
		@return Instance of the process
	  */
	public int getAD_PInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Break Value.
		@param BreakValue 
		Low Value of trade discount break level
	  */
	public void setBreakValue (BigDecimal BreakValue)
	{
		set_Value (COLUMNNAME_BreakValue, BreakValue);
	}

	/** Get Break Value.
		@return Low Value of trade discount break level
	  */
	public BigDecimal getBreakValue () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValueVolume.
		@param BreakValueVolume 
		Low Value of trade discount break level
	  */
	public void setBreakValueVolume (BigDecimal BreakValueVolume)
	{
		set_Value (COLUMNNAME_BreakValueVolume, BreakValueVolume);
	}

	/** Get BreakValueVolume.
		@return Low Value of trade discount break level
	  */
	public BigDecimal getBreakValueVolume () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValueVolume);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValueWeight.
		@param BreakValueWeight 
		Low Value of trade discount break level
	  */
	public void setBreakValueWeight (BigDecimal BreakValueWeight)
	{
		set_Value (COLUMNNAME_BreakValueWeight, BreakValueWeight);
	}

	/** Get BreakValueWeight.
		@return Low Value of trade discount break level
	  */
	public BigDecimal getBreakValueWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValueWeight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set calculatedIncome.
		@param calculatedIncome calculatedIncome	  */
	public void setcalculatedIncome (BigDecimal calculatedIncome)
	{
		set_Value (COLUMNNAME_calculatedIncome, calculatedIncome);
	}

	/** Get calculatedIncome.
		@return calculatedIncome	  */
	public BigDecimal getcalculatedIncome () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_calculatedIncome);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Control Amount.
		@param ControlAmt 
		If not zero, the Debit amount of the document must be equal this amount
	  */
	public void setControlAmt (BigDecimal ControlAmt)
	{
		set_Value (COLUMNNAME_ControlAmt, ControlAmt);
	}

	/** Get Control Amount.
		@return If not zero, the Debit amount of the document must be equal this amount
	  */
	public BigDecimal getControlAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ControlAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_UOM getC_UOM_Volume() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_Volume_ID(), get_TrxName());	}

	/** Set UOM for Volume.
		@param C_UOM_Volume_ID 
		Standard Unit of Measure for Volume
	  */
	public void setC_UOM_Volume_ID (int C_UOM_Volume_ID)
	{
		if (C_UOM_Volume_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_Volume_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_Volume_ID, Integer.valueOf(C_UOM_Volume_ID));
	}

	/** Get UOM for Volume.
		@return Standard Unit of Measure for Volume
	  */
	public int getC_UOM_Volume_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_Volume_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM_Weight() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_Weight_ID(), get_TrxName());	}

	/** Set UOM for Weight.
		@param C_UOM_Weight_ID 
		Standard Unit of Measure for Weight
	  */
	public void setC_UOM_Weight_ID (int C_UOM_Weight_ID)
	{
		if (C_UOM_Weight_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_Weight_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_Weight_ID, Integer.valueOf(C_UOM_Weight_ID));
	}

	/** Get UOM for Weight.
		@return Standard Unit of Measure for Weight
	  */
	public int getC_UOM_Weight_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_Weight_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Valid.
		@param IsValid 
		Element is valid
	  */
	public void setIsValid (boolean IsValid)
	{
		set_Value (COLUMNNAME_IsValid, Boolean.valueOf(IsValid));
	}

	/** Get Valid.
		@return Element is valid
	  */
	public boolean isValid () 
	{
		Object oo = get_Value(COLUMNNAME_IsValid);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LG_ProductPriceRate ID.
		@param LG_ProductPriceRate_ID LG_ProductPriceRate ID	  */
	public void setLG_ProductPriceRate_ID (int LG_ProductPriceRate_ID)
	{
		if (LG_ProductPriceRate_ID < 1) 
			set_Value (COLUMNNAME_LG_ProductPriceRate_ID, null);
		else 
			set_Value (COLUMNNAME_LG_ProductPriceRate_ID, Integer.valueOf(LG_ProductPriceRate_ID));
	}

	/** Get LG_ProductPriceRate ID.
		@return LG_ProductPriceRate ID	  */
	public int getLG_ProductPriceRate_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_ProductPriceRate_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LG_ProductPriceRateLine ID.
		@param LG_ProductPriceRateLine_ID LG_ProductPriceRateLine ID	  */
	public void setLG_ProductPriceRateLine_ID (int LG_ProductPriceRateLine_ID)
	{
		if (LG_ProductPriceRateLine_ID < 1) 
			set_Value (COLUMNNAME_LG_ProductPriceRateLine_ID, null);
		else 
			set_Value (COLUMNNAME_LG_ProductPriceRateLine_ID, Integer.valueOf(LG_ProductPriceRateLine_ID));
	}

	/** Get LG_ProductPriceRateLine ID.
		@return LG_ProductPriceRateLine ID	  */
	public int getLG_ProductPriceRateLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_ProductPriceRateLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LG_Route ID.
		@param LG_Route_ID LG_Route ID	  */
	public void setLG_Route_ID (int LG_Route_ID)
	{
		if (LG_Route_ID < 1) 
			set_Value (COLUMNNAME_LG_Route_ID, null);
		else 
			set_Value (COLUMNNAME_LG_Route_ID, Integer.valueOf(LG_Route_ID));
	}

	/** Get LG_Route ID.
		@return LG_Route ID	  */
	public int getLG_Route_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_Route_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Minimum Amt.
		@param MinimumAmt 
		Minimum Amount in Document Currency
	  */
	public void setMinimumAmt (BigDecimal MinimumAmt)
	{
		set_Value (COLUMNNAME_MinimumAmt, MinimumAmt);
	}

	/** Get Minimum Amt.
		@return Minimum Amount in Document Currency
	  */
	public BigDecimal getMinimumAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MinimumAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getM_PriceList_ID(), get_TrxName());	}

	/** Set Price List.
		@param M_PriceList_ID 
		Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1) 
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList_Version getM_PriceList_Version() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList_Version)MTable.get(getCtx(), org.compiere.model.I_M_PriceList_Version.Table_Name)
			.getPO(getM_PriceList_Version_ID(), get_TrxName());	}

	/** Set Price List Version.
		@param M_PriceList_Version_ID 
		Identifies a unique instance of a Price List
	  */
	public void setM_PriceList_Version_ID (int M_PriceList_Version_ID)
	{
		if (M_PriceList_Version_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_PriceList_Version_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_PriceList_Version_ID, Integer.valueOf(M_PriceList_Version_ID));
	}

	/** Get Price List Version.
		@return Identifies a unique instance of a Price List
	  */
	public int getM_PriceList_Version_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_Version_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Limit Price.
		@param PriceLimit 
		Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit)
	{
		set_Value (COLUMNNAME_PriceLimit, PriceLimit);
	}

	/** Get Limit Price.
		@return Lowest price for a product
	  */
	public BigDecimal getPriceLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price.
		@param PriceList 
		List Price
	  */
	public void setPriceList (BigDecimal PriceList)
	{
		set_Value (COLUMNNAME_PriceList, PriceList);
	}

	/** Get List Price.
		@return List Price
	  */
	public BigDecimal getPriceList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Standard Price.
		@param PriceStd 
		Standard Price
	  */
	public void setPriceStd (BigDecimal PriceStd)
	{
		set_Value (COLUMNNAME_PriceStd, PriceStd);
	}

	/** Get Standard Price.
		@return Standard Price
	  */
	public BigDecimal getPriceStd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceStd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set priceVolume.
		@param priceVolume priceVolume	  */
	public void setpriceVolume (BigDecimal priceVolume)
	{
		set_Value (COLUMNNAME_priceVolume, priceVolume);
	}

	/** Get priceVolume.
		@return priceVolume	  */
	public BigDecimal getpriceVolume () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_priceVolume);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceWeight.
		@param PriceWeight PriceWeight	  */
	public void setPriceWeight (BigDecimal PriceWeight)
	{
		set_Value (COLUMNNAME_PriceWeight, PriceWeight);
	}

	/** Get PriceWeight.
		@return PriceWeight	  */
	public BigDecimal getPriceWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceWeight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set quantityvolumn.
		@param quantityvolumn quantityvolumn	  */
	public void setquantityvolumn (BigDecimal quantityvolumn)
	{
		set_Value (COLUMNNAME_quantityvolumn, quantityvolumn);
	}

	/** Get quantityvolumn.
		@return quantityvolumn	  */
	public BigDecimal getquantityvolumn () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_quantityvolumn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set quantityweight.
		@param quantityweight quantityweight	  */
	public void setquantityweight (BigDecimal quantityweight)
	{
		set_Value (COLUMNNAME_quantityweight, quantityweight);
	}

	/** Get quantityweight.
		@return quantityweight	  */
	public BigDecimal getquantityweight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_quantityweight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set resultVolumn.
		@param resultVolumn resultVolumn	  */
	public void setresultVolumn (BigDecimal resultVolumn)
	{
		set_Value (COLUMNNAME_resultVolumn, resultVolumn);
	}

	/** Get resultVolumn.
		@return resultVolumn	  */
	public BigDecimal getresultVolumn () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_resultVolumn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set resultWeight.
		@param resultWeight resultWeight	  */
	public void setresultWeight (BigDecimal resultWeight)
	{
		set_Value (COLUMNNAME_resultWeight, resultWeight);
	}

	/** Get resultWeight.
		@return resultWeight	  */
	public BigDecimal getresultWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_resultWeight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set T_ProductPriceRateLine ID.
		@param T_ProductPriceRateLine_ID T_ProductPriceRateLine ID	  */
	public void setT_ProductPriceRateLine_ID (int T_ProductPriceRateLine_ID)
	{
		if (T_ProductPriceRateLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_T_ProductPriceRateLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_T_ProductPriceRateLine_ID, Integer.valueOf(T_ProductPriceRateLine_ID));
	}

	/** Get T_ProductPriceRateLine ID.
		@return T_ProductPriceRateLine ID	  */
	public int getT_ProductPriceRateLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_T_ProductPriceRateLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Transfer Time.
		@param TransferTime 
		Transfer Time
	  */
	public void setTransferTime (BigDecimal TransferTime)
	{
		set_Value (COLUMNNAME_TransferTime, TransferTime);
	}

	/** Get Transfer Time.
		@return Transfer Time
	  */
	public BigDecimal getTransferTime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TransferTime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Valid from.
		@param ValidFrom 
		Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom)
	{
		set_Value (COLUMNNAME_ValidFrom, ValidFrom);
	}

	/** Get Valid from.
		@return Valid from including this date (first day)
	  */
	public Timestamp getValidFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidFrom);
	}

	/** Set Valid to.
		@param ValidTo 
		Valid to including this date (last day)
	  */
	public void setValidTo (Timestamp ValidTo)
	{
		set_Value (COLUMNNAME_ValidTo, ValidTo);
	}

	/** Get Valid to.
		@return Valid to including this date (last day)
	  */
	public Timestamp getValidTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidTo);
	}
}