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

/** Generated Interface for LG_Route
 *  @author Adempiere (generated) 
 *  @version Release 3.8.0RC
 */
public interface I_LG_Route 
{

    /** TableName=LG_Route */
    public static final String Table_Name = "LG_Route";

    /** AD_Table_ID=1000003 */
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

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

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

    /** Column name LG_RegionFrom_ID */
    public static final String COLUMNNAME_LG_RegionFrom_ID = "LG_RegionFrom_ID";

	/** Set LG_RegionFrom_ID	  */
	public void setLG_RegionFrom_ID (int LG_RegionFrom_ID);

	/** Get LG_RegionFrom_ID	  */
	public int getLG_RegionFrom_ID();

	public I_LG_Region getLG_RegionFrom() throws RuntimeException;

    /** Column name LG_RegionTo_ID */
    public static final String COLUMNNAME_LG_RegionTo_ID = "LG_RegionTo_ID";

	/** Set LG_RegionTo_ID	  */
	public void setLG_RegionTo_ID (int LG_RegionTo_ID);

	/** Get LG_RegionTo_ID	  */
	public int getLG_RegionTo_ID();

	public I_LG_Region getLG_RegionTo() throws RuntimeException;

    /** Column name LG_Route_ID */
    public static final String COLUMNNAME_LG_Route_ID = "LG_Route_ID";

	/** Set LG_Route ID	  */
	public void setLG_Route_ID (int LG_Route_ID);

	/** Get LG_Route ID	  */
	public int getLG_Route_ID();

    /** Column name LG_RouteType */
    public static final String COLUMNNAME_LG_RouteType = "LG_RouteType";

	/** Set LG_RouteType	  */
	public void setLG_RouteType (String LG_RouteType);

	/** Get LG_RouteType	  */
	public String getLG_RouteType();

    /** Column name LG_TransportType */
    public static final String COLUMNNAME_LG_TransportType = "LG_TransportType";

	/** Set LG_TransportType	  */
	public void setLG_TransportType (String LG_TransportType);

	/** Get LG_TransportType	  */
	public String getLG_TransportType();

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
