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
package org.compiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for I_ProductPriceRate
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0 - $Id$ */
public class X_I_ProductPriceRate extends PO implements I_I_ProductPriceRate, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170103L;

    /** Standard Constructor */
    public X_I_ProductPriceRate (Properties ctx, int I_ProductPriceRate_ID, String trxName)
    {
      super (ctx, I_ProductPriceRate_ID, trxName);
      /** if (I_ProductPriceRate_ID == 0)
        {
			setI_IsImported (false);
// N
			setI_ProductPriceRate_ID (0);
			setIsValid (true);
// Y
			setLG_RateType (null);
// R
			setOWS (false);
// N
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_I_ProductPriceRate (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_ProductPriceRate[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set BreakValue_0.
		@param BreakValue_0 BreakValue_0	  */
	public void setBreakValue_0 (BigDecimal BreakValue_0)
	{
		set_Value (COLUMNNAME_BreakValue_0, BreakValue_0);
	}

	/** Get BreakValue_0.
		@return BreakValue_0	  */
	public BigDecimal getBreakValue_0 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_0);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValue_1.
		@param BreakValue_1 BreakValue_1	  */
	public void setBreakValue_1 (BigDecimal BreakValue_1)
	{
		set_Value (COLUMNNAME_BreakValue_1, BreakValue_1);
	}

	/** Get BreakValue_1.
		@return BreakValue_1	  */
	public BigDecimal getBreakValue_1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValue_2.
		@param BreakValue_2 BreakValue_2	  */
	public void setBreakValue_2 (BigDecimal BreakValue_2)
	{
		set_Value (COLUMNNAME_BreakValue_2, BreakValue_2);
	}

	/** Get BreakValue_2.
		@return BreakValue_2	  */
	public BigDecimal getBreakValue_2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValue_3.
		@param BreakValue_3 BreakValue_3	  */
	public void setBreakValue_3 (BigDecimal BreakValue_3)
	{
		set_Value (COLUMNNAME_BreakValue_3, BreakValue_3);
	}

	/** Get BreakValue_3.
		@return BreakValue_3	  */
	public BigDecimal getBreakValue_3 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_3);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValue_4.
		@param BreakValue_4 BreakValue_4	  */
	public void setBreakValue_4 (BigDecimal BreakValue_4)
	{
		set_Value (COLUMNNAME_BreakValue_4, BreakValue_4);
	}

	/** Get BreakValue_4.
		@return BreakValue_4	  */
	public BigDecimal getBreakValue_4 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_4);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BreakValue_5.
		@param BreakValue_5 BreakValue_5	  */
	public void setBreakValue_5 (BigDecimal BreakValue_5)
	{
		set_Value (COLUMNNAME_BreakValue_5, BreakValue_5);
	}

	/** Get BreakValue_5.
		@return BreakValue_5	  */
	public BigDecimal getBreakValue_5 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BreakValue_5);
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
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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

	/** Set Carrier_Name.
		@param Carrier_Name Carrier_Name	  */
	public void setCarrier_Name (String Carrier_Name)
	{
		set_Value (COLUMNNAME_Carrier_Name, Carrier_Name);
	}

	/** Get Carrier_Name.
		@return Carrier_Name	  */
	public String getCarrier_Name () 
	{
		return (String)get_Value(COLUMNNAME_Carrier_Name);
	}

	public I_C_ValidCombination getchargeAm() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getchargeAmt_0(), get_TrxName());	}

	/** Set chargeAmt_0.
		@param chargeAmt_0 chargeAmt_0	  */
	public void setchargeAmt_0 (int chargeAmt_0)
	{
		set_Value (COLUMNNAME_chargeAmt_0, Integer.valueOf(chargeAmt_0));
	}

	/** Get chargeAmt_0.
		@return chargeAmt_0	  */
	public int getchargeAmt_0 () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_chargeAmt_0);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set chargeAmt_1.
		@param chargeAmt_1 chargeAmt_1	  */
	public void setchargeAmt_1 (BigDecimal chargeAmt_1)
	{
		set_Value (COLUMNNAME_chargeAmt_1, chargeAmt_1);
	}

	/** Get chargeAmt_1.
		@return chargeAmt_1	  */
	public BigDecimal getchargeAmt_1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_chargeAmt_1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set chargeAmt_2.
		@param chargeAmt_2 chargeAmt_2	  */
	public void setchargeAmt_2 (BigDecimal chargeAmt_2)
	{
		set_Value (COLUMNNAME_chargeAmt_2, chargeAmt_2);
	}

	/** Get chargeAmt_2.
		@return chargeAmt_2	  */
	public BigDecimal getchargeAmt_2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_chargeAmt_2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set chargeAmt_3.
		@param chargeAmt_3 chargeAmt_3	  */
	public void setchargeAmt_3 (BigDecimal chargeAmt_3)
	{
		set_Value (COLUMNNAME_chargeAmt_3, chargeAmt_3);
	}

	/** Get chargeAmt_3.
		@return chargeAmt_3	  */
	public BigDecimal getchargeAmt_3 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_chargeAmt_3);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set chargeAmt_4.
		@param chargeAmt_4 chargeAmt_4	  */
	public void setchargeAmt_4 (BigDecimal chargeAmt_4)
	{
		set_Value (COLUMNNAME_chargeAmt_4, chargeAmt_4);
	}

	/** Get chargeAmt_4.
		@return chargeAmt_4	  */
	public BigDecimal getchargeAmt_4 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_chargeAmt_4);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set chargeAmt_5.
		@param chargeAmt_5 chargeAmt_5	  */
	public void setchargeAmt_5 (BigDecimal chargeAmt_5)
	{
		set_Value (COLUMNNAME_chargeAmt_5, chargeAmt_5);
	}

	/** Get chargeAmt_5.
		@return chargeAmt_5	  */
	public BigDecimal getchargeAmt_5 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_chargeAmt_5);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set CityFrom_Name.
		@param CityFrom_Name CityFrom_Name	  */
	public void setCityFrom_Name (String CityFrom_Name)
	{
		set_Value (COLUMNNAME_CityFrom_Name, CityFrom_Name);
	}

	/** Get CityFrom_Name.
		@return CityFrom_Name	  */
	public String getCityFrom_Name () 
	{
		return (String)get_Value(COLUMNNAME_CityFrom_Name);
	}

	/** Set CityTo_Name.
		@param CityTo_Name CityTo_Name	  */
	public void setCityTo_Name (String CityTo_Name)
	{
		set_Value (COLUMNNAME_CityTo_Name, CityTo_Name);
	}

	/** Get CityTo_Name.
		@return CityTo_Name	  */
	public String getCityTo_Name () 
	{
		return (String)get_Value(COLUMNNAME_CityTo_Name);
	}

	/** Set commodityValue.
		@param commodityValue commodityValue	  */
	public void setcommodityValue (String commodityValue)
	{
		set_Value (COLUMNNAME_commodityValue, commodityValue);
	}

	/** Get commodityValue.
		@return commodityValue	  */
	public String getcommodityValue () 
	{
		return (String)get_Value(COLUMNNAME_commodityValue);
	}

	/** Set Imported.
		@param I_IsImported 
		Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsImported);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set I_ProductPriceRate ID.
		@param I_ProductPriceRate_ID I_ProductPriceRate ID	  */
	public void setI_ProductPriceRate_ID (int I_ProductPriceRate_ID)
	{
		if (I_ProductPriceRate_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_ProductPriceRate_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_ProductPriceRate_ID, Integer.valueOf(I_ProductPriceRate_ID));
	}

	/** Get I_ProductPriceRate ID.
		@return I_ProductPriceRate ID	  */
	public int getI_ProductPriceRate_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_ProductPriceRate_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set IATACode_Destiny.
		@param IATACode_Destiny IATACode_Destiny	  */
	public void setIATACode_Destiny (String IATACode_Destiny)
	{
		set_Value (COLUMNNAME_IATACode_Destiny, IATACode_Destiny);
	}

	/** Get IATACode_Destiny.
		@return IATACode_Destiny	  */
	public String getIATACode_Destiny () 
	{
		return (String)get_Value(COLUMNNAME_IATACode_Destiny);
	}

	/** Set IATACode_Origin.
		@param IATACode_Origin IATACode_Origin	  */
	public void setIATACode_Origin (String IATACode_Origin)
	{
		set_Value (COLUMNNAME_IATACode_Origin, IATACode_Origin);
	}

	/** Get IATACode_Origin.
		@return IATACode_Origin	  */
	public String getIATACode_Origin () 
	{
		return (String)get_Value(COLUMNNAME_IATACode_Origin);
	}

	/** IsSOTrx AD_Reference_ID=319 */
	public static final int ISSOTRX_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISSOTRX_Yes = "Y";
	/** No = N */
	public static final String ISSOTRX_No = "N";
	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (String IsSOTrx)
	{

		set_Value (COLUMNNAME_IsSOTrx, IsSOTrx);
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public String getIsSOTrx () 
	{
		return (String)get_Value(COLUMNNAME_IsSOTrx);
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

	/** Set Airport Destino.
		@param LG_AirPort_Destiny_ID Airport Destino	  */
	public void setLG_AirPort_Destiny_ID (int LG_AirPort_Destiny_ID)
	{
		if (LG_AirPort_Destiny_ID < 1) 
			set_Value (COLUMNNAME_LG_AirPort_Destiny_ID, null);
		else 
			set_Value (COLUMNNAME_LG_AirPort_Destiny_ID, Integer.valueOf(LG_AirPort_Destiny_ID));
	}

	/** Get Airport Destino.
		@return Airport Destino	  */
	public int getLG_AirPort_Destiny_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_AirPort_Destiny_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LG_AirPort_Origin_ID.
		@param LG_AirPort_Origin_ID LG_AirPort_Origin_ID	  */
	public void setLG_AirPort_Origin_ID (int LG_AirPort_Origin_ID)
	{
		if (LG_AirPort_Origin_ID < 1) 
			set_Value (COLUMNNAME_LG_AirPort_Origin_ID, null);
		else 
			set_Value (COLUMNNAME_LG_AirPort_Origin_ID, Integer.valueOf(LG_AirPort_Origin_ID));
	}

	/** Get LG_AirPort_Origin_ID.
		@return LG_AirPort_Origin_ID	  */
	public int getLG_AirPort_Origin_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_AirPort_Origin_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_City getLG_CityFrom() throws RuntimeException
    {
		return (org.compiere.model.I_C_City)MTable.get(getCtx(), org.compiere.model.I_C_City.Table_Name)
			.getPO(getLG_CityFrom_ID(), get_TrxName());	}

	/** Set LG_CityFrom_ID.
		@param LG_CityFrom_ID LG_CityFrom_ID	  */
	public void setLG_CityFrom_ID (int LG_CityFrom_ID)
	{
		if (LG_CityFrom_ID < 1) 
			set_Value (COLUMNNAME_LG_CityFrom_ID, null);
		else 
			set_Value (COLUMNNAME_LG_CityFrom_ID, Integer.valueOf(LG_CityFrom_ID));
	}

	/** Get LG_CityFrom_ID.
		@return LG_CityFrom_ID	  */
	public int getLG_CityFrom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_CityFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_City getLG_CityTo() throws RuntimeException
    {
		return (org.compiere.model.I_C_City)MTable.get(getCtx(), org.compiere.model.I_C_City.Table_Name)
			.getPO(getLG_CityTo_ID(), get_TrxName());	}

	/** Set LG_CityTo_ID.
		@param LG_CityTo_ID LG_CityTo_ID	  */
	public void setLG_CityTo_ID (int LG_CityTo_ID)
	{
		if (LG_CityTo_ID < 1) 
			set_Value (COLUMNNAME_LG_CityTo_ID, null);
		else 
			set_Value (COLUMNNAME_LG_CityTo_ID, Integer.valueOf(LG_CityTo_ID));
	}

	/** Get LG_CityTo_ID.
		@return LG_CityTo_ID	  */
	public int getLG_CityTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_CityTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Commodity ID.
		@param LG_Commodity_ID Commodity ID	  */
	public void setLG_Commodity_ID (int LG_Commodity_ID)
	{
		if (LG_Commodity_ID < 1) 
			set_Value (COLUMNNAME_LG_Commodity_ID, null);
		else 
			set_Value (COLUMNNAME_LG_Commodity_ID, Integer.valueOf(LG_Commodity_ID));
	}

	/** Get Commodity ID.
		@return Commodity ID	  */
	public int getLG_Commodity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_Commodity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** LG_RateType AD_Reference_ID=3000209 */
	public static final int LG_RATETYPE_AD_Reference_ID=3000209;
	/** General = G */
	public static final String LG_RATETYPE_General = "G";
	/** Route = R */
	public static final String LG_RATETYPE_Route = "R";
	/** Set LG_RateType.
		@param LG_RateType LG_RateType	  */
	public void setLG_RateType (String LG_RateType)
	{

		set_Value (COLUMNNAME_LG_RateType, LG_RateType);
	}

	/** Get LG_RateType.
		@return LG_RateType	  */
	public String getLG_RateType () 
	{
		return (String)get_Value(COLUMNNAME_LG_RateType);
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

	/** LG_ShippingMode AD_Reference_ID=3000245 */
	public static final int LG_SHIPPINGMODE_AD_Reference_ID=3000245;
	/** CY/CY = 1 */
	public static final String LG_SHIPPINGMODE_CYCY = "1";
	/** CFS/CY = 2 */
	public static final String LG_SHIPPINGMODE_CFSCY = "2";
	/** CY/CFS = 3 */
	public static final String LG_SHIPPINGMODE_CYCFS = "3";
	/** CFS/CFS = 4 */
	public static final String LG_SHIPPINGMODE_CFSCFS = "4";
	/** Set LG_ShippingMode.
		@param LG_ShippingMode LG_ShippingMode	  */
	public void setLG_ShippingMode (String LG_ShippingMode)
	{

		set_Value (COLUMNNAME_LG_ShippingMode, LG_ShippingMode);
	}

	/** Get LG_ShippingMode.
		@return LG_ShippingMode	  */
	public String getLG_ShippingMode () 
	{
		return (String)get_Value(COLUMNNAME_LG_ShippingMode);
	}

	/** Set LG_ShippingMode_Destiny.
		@param LG_ShippingMode_Destiny LG_ShippingMode_Destiny	  */
	public void setLG_ShippingMode_Destiny (String LG_ShippingMode_Destiny)
	{
		set_Value (COLUMNNAME_LG_ShippingMode_Destiny, LG_ShippingMode_Destiny);
	}

	/** Get LG_ShippingMode_Destiny.
		@return LG_ShippingMode_Destiny	  */
	public String getLG_ShippingMode_Destiny () 
	{
		return (String)get_Value(COLUMNNAME_LG_ShippingMode_Destiny);
	}

	/** Set LG_ShippingMode_Name.
		@param LG_ShippingMode_Name LG_ShippingMode_Name	  */
	public void setLG_ShippingMode_Name (String LG_ShippingMode_Name)
	{
		set_Value (COLUMNNAME_LG_ShippingMode_Name, LG_ShippingMode_Name);
	}

	/** Get LG_ShippingMode_Name.
		@return LG_ShippingMode_Name	  */
	public String getLG_ShippingMode_Name () 
	{
		return (String)get_Value(COLUMNNAME_LG_ShippingMode_Name);
	}

	/** Set LG_ShippingMode_Origin.
		@param LG_ShippingMode_Origin LG_ShippingMode_Origin	  */
	public void setLG_ShippingMode_Origin (String LG_ShippingMode_Origin)
	{
		set_Value (COLUMNNAME_LG_ShippingMode_Origin, LG_ShippingMode_Origin);
	}

	/** Get LG_ShippingMode_Origin.
		@return LG_ShippingMode_Origin	  */
	public String getLG_ShippingMode_Origin () 
	{
		return (String)get_Value(COLUMNNAME_LG_ShippingMode_Origin);
	}

	/** LG_TransportType AD_Reference_ID=1000004 */
	public static final int LG_TRANSPORTTYPE_AD_Reference_ID=1000004;
	/** AER LCL (carga aérea) = 1 */
	public static final String LG_TRANSPORTTYPE_AERLCLCargaAereo = "1";
	/** AER Express Courier = 0 */
	public static final String LG_TRANSPORTTYPE_AERExpressCourier = "0";
	/** TER LTL (fur/cont mismo dest) = 5 */
	public static final String LG_TRANSPORTTYPE_TERLTLFurContMismoDest = "5";
	/** TER FTL = 4 */
	public static final String LG_TRANSPORTTYPE_TERFTL = "4";
	/** TER LTL = 6 */
	public static final String LG_TRANSPORTTYPE_TERLTL = "6";
	/** MAR LCL = 3 */
	public static final String LG_TRANSPORTTYPE_MARLCL = "3";
	/** MAR FCL = 2 */
	public static final String LG_TRANSPORTTYPE_MARFCL = "2";
	/** Set Tipo transporte.
		@param LG_TransportType Tipo transporte	  */
	public void setLG_TransportType (String LG_TransportType)
	{

		set_Value (COLUMNNAME_LG_TransportType, LG_TransportType);
	}

	/** Get Tipo transporte.
		@return Tipo transporte	  */
	public String getLG_TransportType () 
	{
		return (String)get_Value(COLUMNNAME_LG_TransportType);
	}

	public org.compiere.model.I_M_Product getM_Product_0() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_0_ID(), get_TrxName());	}

	/** Set M_Product_0_ID.
		@param M_Product_0_ID M_Product_0_ID	  */
	public void setM_Product_0_ID (int M_Product_0_ID)
	{
		if (M_Product_0_ID < 1) 
			set_Value (COLUMNNAME_M_Product_0_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_0_ID, Integer.valueOf(M_Product_0_ID));
	}

	/** Get M_Product_0_ID.
		@return M_Product_0_ID	  */
	public int getM_Product_0_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_0_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product_1() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_1_ID(), get_TrxName());	}

	/** Set M_Product_1_ID.
		@param M_Product_1_ID M_Product_1_ID	  */
	public void setM_Product_1_ID (int M_Product_1_ID)
	{
		if (M_Product_1_ID < 1) 
			set_Value (COLUMNNAME_M_Product_1_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_1_ID, Integer.valueOf(M_Product_1_ID));
	}

	/** Get M_Product_1_ID.
		@return M_Product_1_ID	  */
	public int getM_Product_1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product_2() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_2_ID(), get_TrxName());	}

	/** Set M_Product_2_ID.
		@param M_Product_2_ID M_Product_2_ID	  */
	public void setM_Product_2_ID (int M_Product_2_ID)
	{
		if (M_Product_2_ID < 1) 
			set_Value (COLUMNNAME_M_Product_2_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_2_ID, Integer.valueOf(M_Product_2_ID));
	}

	/** Get M_Product_2_ID.
		@return M_Product_2_ID	  */
	public int getM_Product_2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product_3() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_3_ID(), get_TrxName());	}

	/** Set M_Product_3_ID.
		@param M_Product_3_ID M_Product_3_ID	  */
	public void setM_Product_3_ID (int M_Product_3_ID)
	{
		if (M_Product_3_ID < 1) 
			set_Value (COLUMNNAME_M_Product_3_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_3_ID, Integer.valueOf(M_Product_3_ID));
	}

	/** Get M_Product_3_ID.
		@return M_Product_3_ID	  */
	public int getM_Product_3_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_3_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product_4() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_4_ID(), get_TrxName());	}

	/** Set M_Product_4_ID.
		@param M_Product_4_ID M_Product_4_ID	  */
	public void setM_Product_4_ID (int M_Product_4_ID)
	{
		if (M_Product_4_ID < 1) 
			set_Value (COLUMNNAME_M_Product_4_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_4_ID, Integer.valueOf(M_Product_4_ID));
	}

	/** Get M_Product_4_ID.
		@return M_Product_4_ID	  */
	public int getM_Product_4_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_4_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product_5() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_5_ID(), get_TrxName());	}

	/** Set M_Product_5_ID.
		@param M_Product_5_ID M_Product_5_ID	  */
	public void setM_Product_5_ID (int M_Product_5_ID)
	{
		if (M_Product_5_ID < 1) 
			set_Value (COLUMNNAME_M_Product_5_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_5_ID, Integer.valueOf(M_Product_5_ID));
	}

	/** Get M_Product_5_ID.
		@return M_Product_5_ID	  */
	public int getM_Product_5_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_5_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException
    {
		return (org.compiere.model.I_M_Shipper)MTable.get(getCtx(), org.compiere.model.I_M_Shipper.Table_Name)
			.getPO(getM_Shipper_ID(), get_TrxName());	}

	/** Set Shipper.
		@param M_Shipper_ID 
		Method or manner of product delivery
	  */
	public void setM_Shipper_ID (int M_Shipper_ID)
	{
		if (M_Shipper_ID < 1) 
			set_Value (COLUMNNAME_M_Shipper_ID, null);
		else 
			set_Value (COLUMNNAME_M_Shipper_ID, Integer.valueOf(M_Shipper_ID));
	}

	/** Get Shipper.
		@return Method or manner of product delivery
	  */
	public int getM_Shipper_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Shipper_ID);
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

	/** Set OWS.
		@param OWS OWS	  */
	public void setOWS (boolean OWS)
	{
		set_Value (COLUMNNAME_OWS, Boolean.valueOf(OWS));
	}

	/** Get OWS.
		@return OWS	  */
	public boolean isOWS () 
	{
		Object oo = get_Value(COLUMNNAME_OWS);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set PriceList_0.
		@param PriceList_0 PriceList_0	  */
	public void setPriceList_0 (BigDecimal PriceList_0)
	{
		set_Value (COLUMNNAME_PriceList_0, PriceList_0);
	}

	/** Get PriceList_0.
		@return PriceList_0	  */
	public BigDecimal getPriceList_0 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_0);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList_1.
		@param PriceList_1 PriceList_1	  */
	public void setPriceList_1 (BigDecimal PriceList_1)
	{
		set_Value (COLUMNNAME_PriceList_1, PriceList_1);
	}

	/** Get PriceList_1.
		@return PriceList_1	  */
	public BigDecimal getPriceList_1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList_2.
		@param PriceList_2 PriceList_2	  */
	public void setPriceList_2 (BigDecimal PriceList_2)
	{
		set_Value (COLUMNNAME_PriceList_2, PriceList_2);
	}

	/** Get PriceList_2.
		@return PriceList_2	  */
	public BigDecimal getPriceList_2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList_3.
		@param PriceList_3 PriceList_3	  */
	public void setPriceList_3 (BigDecimal PriceList_3)
	{
		set_Value (COLUMNNAME_PriceList_3, PriceList_3);
	}

	/** Get PriceList_3.
		@return PriceList_3	  */
	public BigDecimal getPriceList_3 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_3);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList_4.
		@param PriceList_4 PriceList_4	  */
	public void setPriceList_4 (BigDecimal PriceList_4)
	{
		set_Value (COLUMNNAME_PriceList_4, PriceList_4);
	}

	/** Get PriceList_4.
		@return PriceList_4	  */
	public BigDecimal getPriceList_4 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_4);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList_5.
		@param PriceList_5 PriceList_5	  */
	public void setPriceList_5 (BigDecimal PriceList_5)
	{
		set_Value (COLUMNNAME_PriceList_5, PriceList_5);
	}

	/** Get PriceList_5.
		@return PriceList_5	  */
	public BigDecimal getPriceList_5 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList_5);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set routename.
		@param routename routename	  */
	public void setroutename (String routename)
	{
		set_Value (COLUMNNAME_routename, routename);
	}

	/** Get routename.
		@return routename	  */
	public String getroutename () 
	{
		return (String)get_Value(COLUMNNAME_routename);
	}

	/** Set Shipper_Name.
		@param Shipper_Name Shipper_Name	  */
	public void setShipper_Name (String Shipper_Name)
	{
		set_Value (COLUMNNAME_Shipper_Name, Shipper_Name);
	}

	/** Get Shipper_Name.
		@return Shipper_Name	  */
	public String getShipper_Name () 
	{
		return (String)get_Value(COLUMNNAME_Shipper_Name);
	}

	public org.compiere.model.I_M_Product getsurcharge_0() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_0_ID(), get_TrxName());	}

	/** Set surcharge_0_ID.
		@param surcharge_0_ID surcharge_0_ID	  */
	public void setsurcharge_0_ID (int surcharge_0_ID)
	{
		if (surcharge_0_ID < 1) 
			set_Value (COLUMNNAME_surcharge_0_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_0_ID, Integer.valueOf(surcharge_0_ID));
	}

	/** Get surcharge_0_ID.
		@return surcharge_0_ID	  */
	public int getsurcharge_0_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_0_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getsurcharge_1() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_1_ID(), get_TrxName());	}

	/** Set surcharge_1_ID.
		@param surcharge_1_ID surcharge_1_ID	  */
	public void setsurcharge_1_ID (int surcharge_1_ID)
	{
		if (surcharge_1_ID < 1) 
			set_Value (COLUMNNAME_surcharge_1_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_1_ID, Integer.valueOf(surcharge_1_ID));
	}

	/** Get surcharge_1_ID.
		@return surcharge_1_ID	  */
	public int getsurcharge_1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getsurcharge_2() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_2_ID(), get_TrxName());	}

	/** Set surcharge_2_ID.
		@param surcharge_2_ID surcharge_2_ID	  */
	public void setsurcharge_2_ID (int surcharge_2_ID)
	{
		if (surcharge_2_ID < 1) 
			set_Value (COLUMNNAME_surcharge_2_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_2_ID, Integer.valueOf(surcharge_2_ID));
	}

	/** Get surcharge_2_ID.
		@return surcharge_2_ID	  */
	public int getsurcharge_2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getsurcharge_3() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_3_ID(), get_TrxName());	}

	/** Set surcharge_3_ID.
		@param surcharge_3_ID surcharge_3_ID	  */
	public void setsurcharge_3_ID (int surcharge_3_ID)
	{
		if (surcharge_3_ID < 1) 
			set_Value (COLUMNNAME_surcharge_3_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_3_ID, Integer.valueOf(surcharge_3_ID));
	}

	/** Get surcharge_3_ID.
		@return surcharge_3_ID	  */
	public int getsurcharge_3_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_3_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getsurcharge_4() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_4_ID(), get_TrxName());	}

	/** Set surcharge_4_ID.
		@param surcharge_4_ID surcharge_4_ID	  */
	public void setsurcharge_4_ID (int surcharge_4_ID)
	{
		if (surcharge_4_ID < 1) 
			set_Value (COLUMNNAME_surcharge_4_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_4_ID, Integer.valueOf(surcharge_4_ID));
	}

	/** Get surcharge_4_ID.
		@return surcharge_4_ID	  */
	public int getsurcharge_4_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_4_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getsurcharge_5() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getsurcharge_5_ID(), get_TrxName());	}

	/** Set surcharge_5_ID.
		@param surcharge_5_ID surcharge_5_ID	  */
	public void setsurcharge_5_ID (int surcharge_5_ID)
	{
		if (surcharge_5_ID < 1) 
			set_Value (COLUMNNAME_surcharge_5_ID, null);
		else 
			set_Value (COLUMNNAME_surcharge_5_ID, Integer.valueOf(surcharge_5_ID));
	}

	/** Get surcharge_5_ID.
		@return surcharge_5_ID	  */
	public int getsurcharge_5_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_surcharge_5_ID);
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