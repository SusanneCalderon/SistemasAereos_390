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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for LG_Request_ProductPriceRate
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0 - $Id$ */
public class X_LG_Request_ProductPriceRate extends PO implements I_LG_Request_ProductPriceRate, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20161130L;

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
	public static final String LG_TRANSPORTTYPE_AERLCLCargaAER = "1";
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
}