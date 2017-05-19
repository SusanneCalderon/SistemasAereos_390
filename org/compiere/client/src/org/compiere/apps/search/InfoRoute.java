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
package org.compiere.apps.search;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.adempiere.plaf.AdempierePLAF;
import org.compiere.apps.AEnv;
import org.compiere.apps.ALayout;
import org.compiere.apps.ALayoutConstraint;
import org.compiere.grid.ed.VCheckBox;
import org.compiere.grid.ed.VDate;
import org.compiere.grid.ed.VLookup;
import org.compiere.grid.ed.VNumber;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.MCity;
import org.compiere.model.MColumn;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MQuery;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.shw.model.MLGRoute;

/**
 *  Info Payment
 *
 *  @author Jorg Janke
 *  @version  $Id: InfoPayment.java,v 1.2 2006/07/30 00:51:27 jjanke Exp $
 *
 * @author Michael McKay, 
 * 				<li>ADEMPIERE-72 VLookup and Info Window improvements
 * 					https://adempiere.atlassian.net/browse/ADEMPIERE-72
 * 				<li>release/380 fix event listeners
 */
public class InfoRoute extends Info
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2917241055484901704L;

	/**
	 *  Detail Protected Constructor
	 *  @param frame parent frame
	 *  @param modal modal
	 *  @param WindowNo window no
	 *  @param value query value
	 *  @param multiSelection multiple selections
	 *  @param whereClause whwre clause
	 */
	@Deprecated
	protected InfoRoute(Frame frame, boolean modal, int WindowNo, String value,
		boolean multiSelection, String whereClause)
	{
		this(frame, modal, WindowNo, 0, value, 
				multiSelection, true, whereClause);
	}
	
	/**
	 *  Detail Protected Constructor
	 *  @param frame parent frame
	 *  @param modal modal
	 *  @param WindowNo window no
	 *  @param record_id The record ID to find
	 *  @param value query value to find, exclusive of record_id
	 *  @param multiSelection multiple selections
	 *  @param saveResults  True if results will be saved, false for info only
	 *  @param whereClause where clause
	 */
	public InfoRoute(Frame frame, boolean modal, int WindowNo, int record_id, String value, 
			boolean multiSelection, boolean saveResults, String whereClause)
	{
		super (frame, modal, WindowNo, "r", "LG_Route_ID", multiSelection, saveResults, whereClause);
		log.info( "InfoRoute");
		setTitle(Msg.getMsg(Env.getCtx(), "InfoRoute"));
		//
		StringBuffer where = new StringBuffer("r.IsActive='Y'");
		if (whereClause.length() > 0)
			where.append(" AND ").append(Util.replace(whereClause, "LG_Route.", "r."));
		setWhereClause(where.toString());
		setTableLayout(s_Layout);
		setFromClause(s_From);
		setOrderClause(s_Order);
		//
		setShowTotals(true);
		//
		statInit();
		initInfo (record_id, value);

		//  To get the focus after the table update
		
		//	AutoQuery
		if(autoQuery() || record_id != 0 || (value != null && value.length() > 0 && value != "%"))
			executeQuery();
		
		p_loadedOK = true;

		AEnv.positionCenterWindow(frame, this);
	}   //  InfoPayment

	/** SalesOrder Trx          */
	private boolean 	m_isSOTrx = false;

	//  Static Info
	private int fieldID = 0;
	//
	private CLabel lCityFrom = new CLabel(Msg.translate(Env.getCtx(), "LG_CityFrom_ID"));
	private VLookup fCityFrom;
	//private CLabel lBPartner_ID = new CLabel(Msg.translate(Env.getCtx(), "BPartner"));
	//private VLookup fBPartner_ID;
	//

	//SHW
	private CLabel lCityTo = new CLabel(Msg.translate(Env.getCtx(), "LG_CityTo_ID"));
	private VLookup fLG_CityTo_ID;
	private CLabel lValidFrom = new CLabel(Msg.translate(Env.getCtx(), "DateTrx"));
	private VDate fValidFrom = new VDate("ValidFrom", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidFrom"));
	private CLabel lValidTo = new CLabel("-  ");
	private VDate fValidTo = new VDate("ValidTo", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidTo"));
	//private VCheckBox fcheckReceipt = new VCheckBox ("IsReceipt", false, false, true, Msg.translate(Env.getCtx(), "OnlyReceipt"), "", false);
	//private VCheckBox fcheckPayment = new VCheckBox ("IsReceipt", false, false, true, Msg.translate(Env.getCtx(), "OnlyPayment"), "", false);

	/** From Clause             */
	private static String s_From = " shw_routefinder r";
	/** Order Clause             */
	private static String s_Order = "2,3,4";

	/**  Array of Column Info    */
	private String colSqlTransportType ;
	private static final Info_Column[] s_Layout = {
		new Info_Column(" ", "r.LG_Route_ID", IDColumn.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Name"),	"r.Name", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "LG_CityFrom_ID"),"(SELECT ct.name from c_City ct WHERE ct.C_City_ID = r.LG_CityFrom_ID)", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "LG_CityTo_ID"), "(SELECT Name FROM C_City ct where ct.C_City_ID = r.LG_CityTo_ID)", String.class),		
		new Info_Column(Msg.translate(Env.getCtx(), "lg_countryfrom_id"),"(SELECT ct.name from c_country ct WHERE ct.C_Country_ID = r.lg_countryfrom_id)", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "lg_countryto_id"), "(SELECT Name FROM C_Country ct where ct.C_Country_ID = r.lg_countryto_id)", String.class),		
		new Info_Column(Msg.translate(Env.getCtx(), "ValidFrom"),"r.ValidFrom", Timestamp.class),
		new Info_Column(Msg.translate(Env.getCtx(), "ValidTo"),"r.ValidTo", Timestamp.class),
		new Info_Column(Msg.translate(Env.getCtx(), "LG_TransportType"), "r.transporttype", String.class),	
		new Info_Column(Msg.translate(Env.getCtx(), "LG_RouteType"), "r.routetype", String.class)		

	};

	/**
	 *	Static Setup - add fields to parameterPanel
	 */
	private void statInit()
	{
		//
		fCityFrom = new VLookup("C_City_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MCity.Table_Name, MCity.COLUMNNAME_C_City_ID), 
						DisplayType.Search));
		lCityFrom.setLabelFor(fCityFrom);
		fCityFrom.setBackground(AdempierePLAF.getInfoBackground());
		fCityFrom.addActionListener(this);/*
		fBPartner_ID = new VLookup("C_BPartner_ID", false, false, true,
			MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
					MColumn.getColumn_ID(MPayment.Table_Name, MPayment.COLUMNNAME_C_BPartner_ID), 
					DisplayType.Search));
		lBPartner_ID.setLabelFor(fBPartner_ID);
		fBPartner_ID.setBackground(AdempierePLAF.getInfoBackground());
		fBPartner_ID.addActionListener(this);*/
		
		//

		fLG_CityTo_ID = new VLookup("C_City_ID", false, false, true,
			MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
					MColumn.getColumn_ID(MCity.Table_Name, MCity.COLUMNNAME_C_City_ID), 
					DisplayType.Search));
		lCityTo.setLabelFor(fLG_CityTo_ID);
		fLG_CityTo_ID.setBackground(AdempierePLAF.getInfoBackground());
		fLG_CityTo_ID.addActionListener(this);
		//
		lValidFrom.setLabelFor(fValidFrom);
		fValidFrom.setBackground(AdempierePLAF.getInfoBackground());
		fValidFrom.setToolTipText(Msg.translate(Env.getCtx(), "ValidFrom"));
		fValidFrom.addActionListener(this);
		lValidTo.setLabelFor(fValidTo);
		fValidTo.setBackground(AdempierePLAF.getInfoBackground());
		fValidTo.setToolTipText(Msg.translate(Env.getCtx(), "ValidTo"));
		fValidTo.addActionListener(this);
		//
		CPanel datePanel = new CPanel();
		
		datePanel.setLayout(new ALayout(0, 0, true));
		datePanel.add(fValidFrom, new ALayoutConstraint(0,0));
		datePanel.add(lValidTo, null);
		datePanel.add(fValidTo, null);
		//
		/*//  First Row
		p_criteriaGrid.add(lBPartner_ID, null);
		p_criteriaGrid.add(fBPartner_ID, null);*/
		//  2nd Row
		p_criteriaGrid.add(lCityFrom, new ALayoutConstraint(1,0));
		p_criteriaGrid.add(fCityFrom);
		p_criteriaGrid.add(lValidFrom, null);
		p_criteriaGrid.add(datePanel, null);
		//  3rd Row

		p_criteriaGrid.add(lCityTo, new ALayoutConstraint(2,0));
		p_criteriaGrid.add(fLG_CityTo_ID);
	}	//	statInit

	/**
	 *	General Init
	 *	@return true, if success
	 */
	protected void initInfo (int record_id, String value)
	{
		//
		if (!(record_id == 0) && value != null && value.length() > 0)
		{
			log.severe("Received both a record_id and a value: " + record_id + " - " + value);
		}

		//  Set values
        if (!(record_id == 0))  // A record is defined
        {
        	fieldID = record_id;
        	String trxName = Trx.createTrxName();
        	MPayment p = new MPayment(Env.getCtx(),record_id, trxName);
    		p = null;
    		Trx.get(trxName, false).close();
        } 
        else  // Try to find other criteria in the context
        {
			String id;
			
			/*//  C_BPartner_ID
			id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "C_BPartner_ID", true);
			if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
				fBPartner_ID.setValue(new Integer(id));*/
			
			//  The value passed in from the field
			if (value != null && value.length() > 0)
			{
				;
			}
			else
			{
				//  C_Payment_ID
				id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "LG_Route_ID", true);
				if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
				{
					fieldID = new Integer(id).intValue();
		        	String trxName = Trx.createTrxName();
		        	MLGRoute r = new MLGRoute(Env.getCtx(),record_id, trxName);
		    		r = null;
		    		Trx.get(trxName, false).close();
				}
				//  C_BankAccount_ID
				id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "LG_CityFrom_ID", true);
				if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
					fCityFrom.setValue(new Integer(id));
			}
        }
		//
		return;
	}	//	initInfo

	
	/**************************************************************************
	 *	Construct SQL Where Clause and define parameters
	 *  (setParameters needs to set parameters)
	 *  Includes first AND
	 *  @return sql where clause
	 */
	protected String getSQLWhere()
	{
		StringBuffer sql = new StringBuffer();

		//  => ID
		if(isResetRecordID())
			fieldID = 0;
		if(!(fieldID == 0))
			sql.append(" AND r.lg_Route_ID = ?");
		//
		/*//
		if (fBPartner_ID.getValue() != null)
			sql.append(" AND r.C_BPartner_ID=?");*/
		//
		if (fCityFrom.getValue() != null)
			sql.append(" AND r.LG_CityFrom_ID=?");
		//
		//SHW
		if (fLG_CityTo_ID.getValue() != null)
			sql.append(" AND r.LG_CityTo_ID=?");
		
		
		if (fValidFrom.getValue() != null || fValidTo.getValue() != null)
		{
			Timestamp from = (Timestamp)fValidFrom.getValue();
			Timestamp to = (Timestamp)fValidTo.getValue();
			if (from == null && to != null)
				sql.append(" AND TRUNC(r.ValidTo, 'DD') <= ?");
			else if (from != null && to == null)
				sql.append(" AND TRUNC(r.ValidFrom, 'DD') >= ?");
			else if (from != null && to != null)
				sql.append(" AND (TRUNC(r.ValidFrom, 'DD') >= ? AND TRUNC(r.ValidFrom, 'DD')<= ?) ");
		}
		//
		
		

		log.fine(sql.toString());
		return sql.toString();
	}	//	getSQLWhere

	/**
	 *  Set Parameters for Query.
	 *  (as defined in getSQLWhere)
	 *  @param pstmt statement
	 *  @param forCount for counting records
	 *  @throws SQLException
	 */
	protected void setParameters(PreparedStatement pstmt, boolean forCount) throws SQLException
	{
		int index = 1;
		//  => ID
		if (!(fieldID == 0))
			pstmt.setInt(index++, fieldID);
		//
		/*//
		if (fBPartner_ID.getValue() != null)
		{
			Integer id = (Integer)fBPartner_ID.getValue();
			pstmt.setInt(index++, id.intValue());
			log.fine("BPartner=" + id);
		}*/
		//
		if (fCityFrom.getValue() != null)
		{
			Integer id = (Integer)fCityFrom.getValue();
			pstmt.setInt(index++, id.intValue());
			log.fine("LG_CityFrom_ID=" + id);
		}
		//

		if (fLG_CityTo_ID.getValue() != null)
		{
			Integer id = (Integer)fLG_CityTo_ID.getValue();
			pstmt.setInt(index++, id.intValue());
			log.fine("Project=" + id);
		}
		if (fValidFrom.getValue() != null || fValidTo.getValue() != null)
		{
			Timestamp from = (Timestamp)fValidFrom.getValue();
			Timestamp to = (Timestamp)fValidTo.getValue();
			log.fine("Date From=" + from + ", To=" + to);
			if (from == null && to != null)
				pstmt.setTimestamp(index++, to);
			else if (from != null && to == null)
				pstmt.setTimestamp(index++, from);
			else if (from != null && to != null)
			{
				pstmt.setTimestamp(index++, from);
				pstmt.setTimestamp(index++, to);
			}
		}
	}   //  setParameters

	/**************************************************************************
	 *	(Button) Action Listener & Popup Menu
	 *  @param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		super.actionPerformed(e);
	}

	/**
	 *	Zoom
	 */
	protected void zoom(int record_ID)
	{
		log.info( "InfoRoute.zoom");
		Integer LG_Route_ID = record_ID;
		if (LG_Route_ID == null)
			return;
		MQuery query = new MQuery("LG_Route");
		query.addRestriction("LG_Route_ID", MQuery.EQUAL, LG_Route_ID);
		query.setRecordCount(1);
		int AD_WindowNo = getAD_Window_ID("LG_Route", m_isSOTrx );
		zoom (AD_WindowNo, query);
	}	//	zoom

	/**
	 *	Has Zoom
	 *  @return true
	 */
	protected boolean hasZoom()
	{
		return true;
	}	//	hasZoom
	
	/**
	 * Does the parameter panel have outstanding changes that have not been
	 * used in a query?
	 * @return true if there are outstanding changes.
	 */
	protected boolean hasOutstandingChanges()
	{
		//  All the tracked fields
		return(
				fCityFrom.hasChanged()	||
				fLG_CityTo_ID.hasChanged() ||
				//fBPartner_ID.hasChanged()	||
				fValidFrom.hasChanged()	||
				fValidTo.hasChanged());
	}
	/**
	 * Record outstanding changes by copying the current
	 * value to the oldValue on all fields
	 */
	protected void setFieldOldValues()
	{
		fCityFrom.set_oldValue();
		fLG_CityTo_ID.set_oldValue();
		//fBPartner_ID.set_oldValue();
		fValidFrom.set_oldValue();
		fValidTo.set_oldValue();
		return;
	}
    /**
	 *  Clear all fields and set default values in check boxes
	 */
	protected void clearParameters()
	{
		//  Clear fields and set defaults
		Object nullObject = null;
		//fBPartner_ID.setValue(null);
		fCityFrom.setValue(null);
		fValidFrom.setValue(nullObject);
		fValidTo.setValue(nullObject);
	}
}   //  InfoPayment
