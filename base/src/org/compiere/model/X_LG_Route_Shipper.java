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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for LG_Route_Shipper
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0RC - $Id$ */
public class X_LG_Route_Shipper extends PO implements I_LG_Route_Shipper, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140902L;

    /** Standard Constructor */
    public X_LG_Route_Shipper (Properties ctx, int LG_Route_Shipper_ID, String trxName)
    {
      super (ctx, LG_Route_Shipper_ID, trxName);
      /** if (LG_Route_Shipper_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_LG_Route_Shipper (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_LG_Route_Shipper[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_LG_Route getLG_Route() throws RuntimeException
    {
		return (I_LG_Route)MTable.get(getCtx(), I_LG_Route.Table_Name)
			.getPO(getLG_Route_ID(), get_TrxName());	}

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
			set_ValueNoCheck (COLUMNNAME_M_Shipper_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Shipper_ID, Integer.valueOf(M_Shipper_ID));
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
}