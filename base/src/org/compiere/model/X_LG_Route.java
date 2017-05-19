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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for LG_Route
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0RC - $Id$ */
public class X_LG_Route extends PO implements I_LG_Route, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140902L;

    /** Standard Constructor */
    public X_LG_Route (Properties ctx, int LG_Route_ID, String trxName)
    {
      super (ctx, LG_Route_ID, trxName);
      /** if (LG_Route_ID == 0)
        {
			setLG_Route_ID (0);
        } */
    }

    /** Load Constructor */
    public X_LG_Route (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_LG_Route[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
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

	public I_LG_Region getLG_RegionFrom() throws RuntimeException
    {
		return (I_LG_Region)MTable.get(getCtx(), I_LG_Region.Table_Name)
			.getPO(getLG_RegionFrom_ID(), get_TrxName());	}

	/** Set LG_RegionFrom_ID.
		@param LG_RegionFrom_ID LG_RegionFrom_ID	  */
	public void setLG_RegionFrom_ID (int LG_RegionFrom_ID)
	{
		if (LG_RegionFrom_ID < 1) 
			set_Value (COLUMNNAME_LG_RegionFrom_ID, null);
		else 
			set_Value (COLUMNNAME_LG_RegionFrom_ID, Integer.valueOf(LG_RegionFrom_ID));
	}

	/** Get LG_RegionFrom_ID.
		@return LG_RegionFrom_ID	  */
	public int getLG_RegionFrom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_RegionFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_LG_Region getLG_RegionTo() throws RuntimeException
    {
		return (I_LG_Region)MTable.get(getCtx(), I_LG_Region.Table_Name)
			.getPO(getLG_RegionTo_ID(), get_TrxName());	}

	/** Set LG_RegionTo_ID.
		@param LG_RegionTo_ID LG_RegionTo_ID	  */
	public void setLG_RegionTo_ID (int LG_RegionTo_ID)
	{
		if (LG_RegionTo_ID < 1) 
			set_Value (COLUMNNAME_LG_RegionTo_ID, null);
		else 
			set_Value (COLUMNNAME_LG_RegionTo_ID, Integer.valueOf(LG_RegionTo_ID));
	}

	/** Get LG_RegionTo_ID.
		@return LG_RegionTo_ID	  */
	public int getLG_RegionTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LG_RegionTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LG_Route ID.
		@param LG_Route_ID LG_Route ID	  */
	public void setLG_Route_ID (int LG_Route_ID)
	{
		if (LG_Route_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_LG_Route_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_LG_Route_ID, Integer.valueOf(LG_Route_ID));
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

	/** LG_RouteType AD_Reference_ID=1000005 */
	public static final int LG_ROUTETYPE_AD_Reference_ID=1000005;
	/** Owne Route = O */
	public static final String LG_ROUTETYPE_OwneRoute = "O";
	/** Coloader = C */
	public static final String LG_ROUTETYPE_Coloader = "C";
	/** Set LG_RouteType.
		@param LG_RouteType LG_RouteType	  */
	public void setLG_RouteType (String LG_RouteType)
	{

		set_Value (COLUMNNAME_LG_RouteType, LG_RouteType);
	}

	/** Get LG_RouteType.
		@return LG_RouteType	  */
	public String getLG_RouteType () 
	{
		return (String)get_Value(COLUMNNAME_LG_RouteType);
	}

	/** LG_TransportType AD_Reference_ID=1000004 */
	public static final int LG_TRANSPORTTYPE_AD_Reference_ID=1000004;
	/** Air = A */
	public static final String LG_TRANSPORTTYPE_Air = "A";
	/** Overland = L */
	public static final String LG_TRANSPORTTYPE_Overland = "L";
	/** SeaWay = S */
	public static final String LG_TRANSPORTTYPE_SeaWay = "S";
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