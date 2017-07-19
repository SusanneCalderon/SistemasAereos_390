/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2016 ADempiere Foundation, All Rights Reserved.         *
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
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.compiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for LG_Request_ProductPriceRate
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_LG_Request_ProductPriceRate extends PO implements I_LG_Request_ProductPriceRate, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170718L;

    /** Standard Constructor */
    public X_LG_Request_ProductPriceRate (Properties ctx, int LG_Request_ProductPriceRate_ID, String trxName)
    {
      super (ctx, LG_Request_ProductPriceRate_ID, trxName);
      /** if (LG_Request_ProductPriceRate_ID == 0)
        {
			setLG_Request_ProductPriceRate_ID (0);
        } */
    }

    /** Load Constructor */
    public X_LG_Request_ProductPriceRate (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_LG_Request_ProductPriceRate[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set dimensionalWeight.
		@param dimensionalWeight dimensionalWeight	  */
	public void setdimensionalWeight (BigDecimal dimensionalWeight)
	{
		set_Value (COLUMNNAME_dimensionalWeight, dimensionalWeight);
	}

	/** Get dimensionalWeight.
		@return dimensionalWeight	  */
	public BigDecimal getdimensionalWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_dimensionalWeight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Generate To.
		@param GenerateTo 
		Generate To
	  */
	public void setGenerateTo (String GenerateTo)
	{
		set_Value (COLUMNNAME_GenerateTo, GenerateTo);
	}

	/** Get Generate To.
		@return Generate To
	  */
	public String getGenerateTo () 
	{
		return (String)get_Value(COLUMNNAME_GenerateTo);
	}

	public I_LG_ProductPriceRate getLG_ProductPriceRate() throws RuntimeException
    {
		return (I_LG_ProductPriceRate)MTable.get(getCtx(), I_LG_ProductPriceRate.Table_Name)
			.getPO(getLG_ProductPriceRate_ID(), get_TrxName());	}

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

	/** Set LG_Request_ProductPriceRate ID.
		@param LG_Request_ProductPriceRate_ID LG_Request_ProductPriceRate ID	  */
	public void setLG_Request_ProductPriceRate_ID (int LG_Request_ProductPriceRate_ID)
	{
		if (LG_Request_ProductPriceRate_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_LG_Request_ProductPriceRate_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_LG_Request_ProductPriceRate_ID, Integer.valueOf(LG_Request_ProductPriceRate_ID));
	}

	/** Get LG_Request_ProductPriceRate ID.
		@return LG_Request_ProductPriceRate ID	  */
	public int getLG_Request_ProductPriceRate_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_Request_ProductPriceRate_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** LG_TransportType AD_Reference_ID=1000004 */
	public static final int LG_TRANSPORTTYPE_AD_Reference_ID=1000004;
	/** AER LCL (carga aérea) = 1 */
	public static final String LG_TRANSPORTTYPE_AERLCLCargaAérea = "1";
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
	/** Set LG_TransportType.
		@param LG_TransportType LG_TransportType	  */
	public void setLG_TransportType (String LG_TransportType)
	{

		set_Value (COLUMNNAME_LG_TransportType, LG_TransportType);
	}

	/** Get LG_TransportType.
		@return LG_TransportType	  */
	public String getLG_TransportType () 
	{
		return (String)get_Value(COLUMNNAME_LG_TransportType);
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

	public org.compiere.model.I_R_Request getR_Request() throws RuntimeException
    {
		return (org.compiere.model.I_R_Request)MTable.get(getCtx(), org.compiere.model.I_R_Request.Table_Name)
			.getPO(getR_Request_ID(), get_TrxName());	}

	/** Set Request.
		@param R_Request_ID 
		Request from a Business Partner or Prospect
	  */
	public void setR_Request_ID (int R_Request_ID)
	{
		if (R_Request_ID < 1) 
			set_Value (COLUMNNAME_R_Request_ID, null);
		else 
			set_Value (COLUMNNAME_R_Request_ID, Integer.valueOf(R_Request_ID));
	}

	/** Get Request.
		@return Request from a Business Partner or Prospect
	  */
	public int getR_Request_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_R_Request_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Volume.
		@param Volume 
		Volume of a product
	  */
	public void setVolume (BigDecimal Volume)
	{
		set_Value (COLUMNNAME_Volume, Volume);
	}

	/** Get Volume.
		@return Volume of a product
	  */
	public BigDecimal getVolume () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Volume);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Weight.
		@param Weight 
		Weight of a product
	  */
	public void setWeight (BigDecimal Weight)
	{
		set_Value (COLUMNNAME_Weight, Weight);
	}

	/** Get Weight.
		@return Weight of a product
	  */
	public BigDecimal getWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Weight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}