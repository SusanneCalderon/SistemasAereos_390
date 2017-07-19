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

/** Generated Model for C_RFQ_C_Bpartner
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0 - $Id$ */
public class X_C_RFQ_C_Bpartner extends PO implements I_C_RFQ_C_Bpartner, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20161129L;

    /** Standard Constructor */
    public X_C_RFQ_C_Bpartner (Properties ctx, int C_RFQ_C_Bpartner_ID, String trxName)
    {
      super (ctx, C_RFQ_C_Bpartner_ID, trxName);
      /** if (C_RFQ_C_Bpartner_ID == 0)
        {
			setC_RFQ_C_Bpartner_ID (0);
        } */
    }

    /** Load Constructor */
    public X_C_RFQ_C_Bpartner (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_C_RFQ_C_Bpartner[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set C_RFQ_C_Bpartner ID.
		@param C_RFQ_C_Bpartner_ID C_RFQ_C_Bpartner ID	  */
	public void setC_RFQ_C_Bpartner_ID (int C_RFQ_C_Bpartner_ID)
	{
		if (C_RFQ_C_Bpartner_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_RFQ_C_Bpartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_RFQ_C_Bpartner_ID, Integer.valueOf(C_RFQ_C_Bpartner_ID));
	}

	/** Get C_RFQ_C_Bpartner ID.
		@return C_RFQ_C_Bpartner ID	  */
	public int getC_RFQ_C_Bpartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_RFQ_C_Bpartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getC_RfQ() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getC_RfQ_ID(), get_TrxName());	}

	/** Set RfQ.
		@param C_RfQ_ID 
		Request for Quotation
	  */
	public void setC_RfQ_ID (int C_RfQ_ID)
	{
		if (C_RfQ_ID < 1) 
			set_Value (COLUMNNAME_C_RfQ_ID, null);
		else 
			set_Value (COLUMNNAME_C_RfQ_ID, Integer.valueOf(C_RfQ_ID));
	}

	/** Get RfQ.
		@return Request for Quotation
	  */
	public int getC_RfQ_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_RfQ_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}