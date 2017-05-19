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

/** Generated Model for LG_Region_City
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0RC - $Id$ */
public class X_LG_Region_City extends PO implements I_LG_Region_City, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140902L;

    /** Standard Constructor */
    public X_LG_Region_City (Properties ctx, int LG_Region_City_ID, String trxName)
    {
      super (ctx, LG_Region_City_ID, trxName);
      /** if (LG_Region_City_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_LG_Region_City (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_LG_Region_City[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_City getC_City() throws RuntimeException
    {
		return (org.compiere.model.I_C_City)MTable.get(getCtx(), org.compiere.model.I_C_City.Table_Name)
			.getPO(getC_City_ID(), get_TrxName());	}

	/** Set City.
		@param C_City_ID 
		City
	  */
	public void setC_City_ID (int C_City_ID)
	{
		if (C_City_ID < 1) 
			set_Value (COLUMNNAME_C_City_ID, null);
		else 
			set_Value (COLUMNNAME_C_City_ID, Integer.valueOf(C_City_ID));
	}

	/** Get City.
		@return City
	  */
	public int getC_City_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_City_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_LG_Region getLG_Region() throws RuntimeException
    {
		return (I_LG_Region)MTable.get(getCtx(), I_LG_Region.Table_Name)
			.getPO(getLG_Region_ID(), get_TrxName());	}

	/** Set LG_Region ID.
		@param LG_Region_ID LG_Region ID	  */
	public void setLG_Region_ID (int LG_Region_ID)
	{
		if (LG_Region_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_LG_Region_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_LG_Region_ID, Integer.valueOf(LG_Region_ID));
	}

	/** Get LG_Region ID.
		@return LG_Region ID	  */
	public int getLG_Region_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_Region_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}