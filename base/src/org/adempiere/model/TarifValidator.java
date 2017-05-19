/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.adempiere.model;

import org.compiere.model.MAttribute;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MClient;
import org.compiere.model.MRole;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.eevolution.model.MPPMRP;



/**
 *	Validator Example Implementation
 *	
 *	@author Susanne Calderon
 */
public class TarifValidator implements ModelValidator
{
	/**
	 *	Constructor.
	 */
	public TarifValidator ()
	{
		super ();
	}	//	MyValidator
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(TarifValidator.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	/** User	*/
	private int		m_AD_User_ID = -1;
	/** Role	*/
	private int		m_AD_Role_ID = -1;
	
	/**
	 *	Initialize Validation
	 *	@param engine validation engine 
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		//client = null for global validator
		if (client != null) {	
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		}
		else  {
			log.info("Initializing global validator: "+this.toString());
		}
		
		//	We want to be informed when C_Order is created/changed
		engine.addModelChange(MPPMRP.Table_Name, this);
		
		//	We want to validate Order before preparing 
		//engine.addDocValidate(MOT.Table_Name, this);
	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *	@param po persistent object
     *	@param type TYPE_
     *	@return error message or null
     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	public String modelChange (PO po, int type) throws Exception
	{
		String error = null;
		
		if (type == TYPE_AFTER_NEW || type == TYPE_AFTER_CHANGE)
		{
			
		}
	
		
		if (type == TYPE_NEW || type == TYPE_BEFORE_CHANGE)
		{}
		if (type == TYPE_BEFORE_CHANGE)
		{}

		if (type == TYPE_AFTER_CHANGE)
		{	
		}

		return error;

	}	//	modelChange
	
	/**
	 *	Validate Document.
	 *	Called as first step of DocAction.prepareIt 
     *	when you called addDocValidate for the table.
     *	Note that totals, etc. may not be correct.
	 *	@param po persistent object
	 *	@param timing see TIMING_ constants
     *	@return error message or null
	 */
	public String docValidate (PO po, int timing)
	{
		String error = null;
		log.info(po.get_TableName() + " Timing: "+timing);
		
		if (timing == TIMING_AFTER_COMPLETE) {}
		
		
		
		if (timing == TIMING_AFTER_PREPARE){}

		if (timing == TIMING_BEFORE_PREPARE) {}
		
		if (timing == TIMING_BEFORE_POST)
		{}

		return error;
	}	//	docValidate	
	
	
	
	/**
	 *	User Login.
	 *	Called when preferences are set
	 *	@param AD_Org_ID org
	 *	@param AD_Role_ID role
	 *	@param AD_User_ID user
	 *	@return error message or null
	 */
	public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info("AD_User_ID=" + AD_User_ID);
		m_AD_User_ID = AD_User_ID;
		m_AD_Role_ID = AD_Role_ID;
		return null;
	}	//	login

	/**
	 *	Get Client to be monitored
	 *	@return AD_Client_ID client
	 */
	public int getAD_Client_ID()
	{
		return m_AD_Client_ID;
	}	//	getAD_Client_ID

	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MyValidator[Order@Gardenworld");
		sb.append ("]");
		return sb.toString ();
	}	//	toString

	/**
	 * Sample Validator Before Save Properties - to set mandatory properties on users
	 * avoid users changing properties
	 */
	public void beforeSaveProperties() {
		// not for SuperUser or role SysAdmin
		if (   m_AD_User_ID == 0  // System
			|| m_AD_User_ID == 100   // SuperUser
			|| m_AD_Role_ID == 0  // System Administrator
			|| m_AD_Role_ID == 1000000)  // ECO Admin
			return;

		log.info("Setting default Properties");

		MRole role = MRole.get(Env.getCtx(), m_AD_Role_ID);
		if (!role.get_ValueAsBoolean("isshowAdvancedTab"))
		Ini.setProperty(Ini.P_SHOW_ADVANCED, false);
		// Example - if you don't want user to select auto commit property
		// Ini.setProperty(Ini.P_A_COMMIT, false);
		
		// Example - if you don't want user to select auto login
		// Ini.setProperty(Ini.P_A_LOGIN, false);

		// Example - if you don't want user to select store password
		// Ini.setProperty(Ini.P_STORE_PWD, false);

		// Example - if you want your user inherit ALWAYS the show accounting from role
		Ini.setProperty(Ini.P_SHOW_ACCT, role.isShowAcct());
		
		// Example - if you want to avoid your user from changing the working date
		/*
		Timestamp DEFAULT_TODAY =	new Timestamp(System.currentTimeMillis());
		//  Date (remove seconds)
		DEFAULT_TODAY.setHours(0);
		DEFAULT_TODAY.setMinutes(0);
		DEFAULT_TODAY.setSeconds(0);
		DEFAULT_TODAY.setNanos(0);
		Ini.setProperty(Ini.P_TODAY, DEFAULT_TODAY.toString());
		Env.setContext(Env.getCtx(), "#Date", DEFAULT_TODAY);
		*/

	}	// beforeSaveProperties
	
	

	private String SaveAttributes(PO A_PO)
	{	return "";
	}
	
	
	

	
	

	


	
	
	

	
	
	
	

	
	

	
	

	
	
	

	
	
	

}	//	MyValidator
