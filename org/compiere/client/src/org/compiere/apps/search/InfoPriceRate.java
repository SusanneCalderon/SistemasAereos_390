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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ColorUIResource;

import org.adempiere.plaf.AdempierePLAF;
import org.compiere.apps.AEnv;
import org.compiere.apps.ALayout;
import org.compiere.apps.ALayoutConstraint;
import org.compiere.grid.ed.VDate;
import org.compiere.grid.ed.VLookup;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.MiniTable;
import org.compiere.model.MCity;
import org.compiere.model.MColumn;
import org.compiere.model.MCountry;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.shw.model.MLGProductPriceRate;

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
public class InfoPriceRate extends Info
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
	protected InfoPriceRate(Frame frame, boolean modal, int WindowNo, String value,
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
	public InfoPriceRate(Frame frame, boolean modal, int WindowNo, int record_id, String value, 
			boolean multiSelection, boolean saveResults, String whereClause)
	
	
	{
		super (frame, modal, WindowNo, "r", "SearchPriceRate", multiSelection, saveResults, whereClause);
		log.info( "InfoRoute");
		setTitle(Msg.getMsg(Env.getCtx(), "InfoPriceRate"));
		//
		StringBuffer where = new StringBuffer("r.IsActive='Y'");
		if (whereClause.length() > 0)
			where.append(" AND ").append(Util.replace(whereClause, "SearchPriceRate.", "r."));
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
	private CLabel lCityTo = new CLabel(Msg.translate(Env.getCtx(), "LG_CityTo_ID"));
	private VLookup fLG_CityTo_ID;
	
	// Valid From: from-to
	private CLabel lValidFromFrom = new CLabel(Msg.translate(Env.getCtx(), "ValidFrom"));
	private VDate fValidFromFrom = new VDate("ValidFrom", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidFrom"));
	private CLabel lValidFromTo = new CLabel("-  ");
	private VDate fValidFromTo = new VDate("ValidFrom", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidFrom"));

	// Valid To: from-to
	private CLabel lValidToFrom = new CLabel(Msg.translate(Env.getCtx(), "ValidTo"));
	private VDate fValidToFrom = new VDate("ValidTo", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidTo"));
	private CLabel lValidToTo = new CLabel("-  ");
	private VDate fValidToTo = new VDate("ValidTo", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "ValidTo"));
	
	private CLabel lTransportType = new CLabel(Msg.translate(Env.getCtx(), "LG_TransportType"));
	private VLookup fTransportType;

	private CLabel lCountryFrom = new CLabel(Msg.translate(Env.getCtx(), "LG_CountryFrom_ID"));
	private VLookup fCountryFrom;
	private CLabel lCountryTo = new CLabel(Msg.translate(Env.getCtx(), "LG_CountryTo_ID"));
	private VLookup fLG_CountryTo_ID;

	private CLabel lAirportFrom = new CLabel(Msg.translate(Env.getCtx(), "LG_AirPort_Origin_ID"));
	private VLookup fAirPortFrom;
	private CLabel lAirPortTo = new CLabel(Msg.translate(Env.getCtx(), "LG_AirPort_Destiny_ID"));
	private VLookup fLG_AirportTo_ID;
	//private VCheckBox fcheckReceipt = new VCheckBox ("IsReceipt", false, false, true, Msg.translate(Env.getCtx(), "OnlyReceipt"), "", false);
	//private VCheckBox fcheckPayment = new VCheckBox ("IsReceipt", false, false, true, Msg.translate(Env.getCtx(), "OnlyPayment"), "", false);
	

	private CPanel tablePanel = new CPanel();
	private CTabbedPane jTab  = new CTabbedPane();
	private MiniTable detailTbl = new MiniTable();
	private String m_sqlDetail;

	/** From Clause             */
	private static String s_From = " SearchPriceRate r";
	/** Order Clause             */
	private static String s_Order = "2,3,4";
	private int m_LG_ProductPriceRate_ID = 0;

	/**  Array of Column Info    */
	private static final Info_Column[] s_Layout = {
		new Info_Column(" ", "r.lg_ProductPricerate_ID", IDColumn.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Name"),	"r.Name", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Agente"),	"r.agentname", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Tipo de Servicio"),	"r.productName", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "LG_CityFrom_ID"),"cityFrom", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "LG_CityTo_ID"), "CityTo", String.class),		
		new Info_Column(Msg.translate(Env.getCtx(), "lg_countryfrom_id"),"countryFrom", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "lg_countryto_id"), "countryTo", String.class),		
		new Info_Column(Msg.translate(Env.getCtx(), "ValidFrom"),"r.ValidFrom", Timestamp.class),
		new Info_Column(Msg.translate(Env.getCtx(), "ValidTo"),"r.ValidTo", Timestamp.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Shipping Mode"),"r.shippingMode", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Tipo de Transporte"),"r.transportType", String.class),
		new Info_Column(Msg.translate(Env.getCtx(), "Tipo de Ruta"),"r.routeType", String.class)

	};
	
	

	/**
	 *	Static Setup - add fields to parameterPanel
	 */
	private void statInit()
	{
		fCityFrom = new VLookup("C_City_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MCity.Table_Name, MCity.COLUMNNAME_C_City_ID), 
						DisplayType.Search));
		lCityFrom.setLabelFor(fCityFrom);
		fCityFrom.setBackground(AdempierePLAF.getInfoBackground());
		fCityFrom.addActionListener(this);
		
		/*
		fBPartner_ID = new VLookup("C_BPartner_ID", false, false, true,
			MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
					MColumn.getColumn_ID(MPayment.Table_Name, MPayment.COLUMNNAME_C_BPartner_ID), 
					DisplayType.Search));
		lBPartner_ID.setLabelFor(fBPartner_ID);
		fBPartner_ID.setBackground(AdempierePLAF.getInfoBackground());
		fBPartner_ID.addActionListener(this);*/
		fLG_CityTo_ID = new VLookup("C_City_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MCity.Table_Name, MCity.COLUMNNAME_C_City_ID), 
						DisplayType.Search));
		lCityTo.setLabelFor(fLG_CityTo_ID);
		fLG_CityTo_ID.setBackground(AdempierePLAF.getInfoBackground());
		fLG_CityTo_ID.addActionListener(this);
		
		
		fCountryFrom = new VLookup("C_Country_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MCountry.Table_Name, MCountry.COLUMNNAME_C_Country_ID), 
						DisplayType.Search));		
		lCountryFrom.setLabelFor(fCountryFrom);
		fCountryFrom.setBackground(AdempierePLAF.getInfoBackground());
		fCountryFrom.addActionListener(this);
		
		fLG_CountryTo_ID = new VLookup("C_Country_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MCountry.Table_Name, MCountry.COLUMNNAME_C_Country_ID), 
						DisplayType.Search));
		lCountryTo.setLabelFor(fLG_CountryTo_ID);
		fLG_CountryTo_ID.setBackground(AdempierePLAF.getInfoBackground());
		fLG_CountryTo_ID.addActionListener(this);
		

		fLG_AirportTo_ID = new VLookup("LG_Airport_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID("LG_AirPort", "LG_AirPort_ID"), 
						DisplayType.Search));
		lAirPortTo.setLabelFor(fLG_AirportTo_ID);
		fLG_AirportTo_ID.setBackground(AdempierePLAF.getInfoBackground());
		fLG_AirportTo_ID.addActionListener(this);

		fAirPortFrom= new VLookup("LG_Airport_ID", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID("LG_AirPort", "LG_AirPort_ID"), 
						DisplayType.Search));
		lAirportFrom.setLabelFor(fAirPortFrom);
		fAirPortFrom.setBackground(AdempierePLAF.getInfoBackground());
		fAirPortFrom.addActionListener(this);
		
		// Valid From: from-to
		lValidFromFrom.setLabelFor(fValidFromFrom);
		fValidFromFrom.setBackground(AdempierePLAF.getInfoBackground());
		fValidFromFrom.setToolTipText(Msg.translate(Env.getCtx(), "ValidFrom"));
		fValidFromFrom.addActionListener(this);
		lValidFromTo.setLabelFor(fValidFromTo);
		fValidFromTo.setBackground(AdempierePLAF.getInfoBackground());
		fValidFromTo.setToolTipText(Msg.translate(Env.getCtx(), "ValidFrom"));
		fValidFromTo.addActionListener(this);
		
		// Valid To: from-to
		lValidToFrom.setLabelFor(fValidToFrom);
		fValidToFrom.setBackground(AdempierePLAF.getInfoBackground());
		fValidToFrom.setToolTipText(Msg.translate(Env.getCtx(), "ValidTo"));
		fValidToFrom.addActionListener(this);
		lValidToTo.setLabelFor(fValidToTo);
		fValidToTo.setBackground(AdempierePLAF.getInfoBackground());
		fValidToTo.setToolTipText(Msg.translate(Env.getCtx(), "ValidTo"));
		fValidToTo.addActionListener(this);
		//
		fTransportType = new VLookup("LG_TransportType", false, false, true,
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 
						MColumn.getColumn_ID(MProject.Table_Name, "LG_TransportType"), 
						DisplayType.List));
		lTransportType.setLabelFor(fTransportType);
		fTransportType.setBackground(AdempierePLAF.getInfoBackground());
		fTransportType.addActionListener(this);

		CPanel datePanelFrom = new CPanel();
		datePanelFrom.setLayout(new ALayout(0, 0, true));
		datePanelFrom.add(fValidFromFrom, new ALayoutConstraint(0,0));
		datePanelFrom.add(lValidFromTo, null);
		datePanelFrom.add(fValidFromTo, null);

		CPanel datePanelTo = new CPanel();
		datePanelTo.setLayout(new ALayout(0, 0, true));
		datePanelTo.add(fValidToFrom, new ALayoutConstraint(0,0));
		datePanelTo.add(lValidToTo, null);
		datePanelTo.add(fValidToTo, null);
		

		CPanel countryPanel = new CPanel();
		countryPanel.setLayout(new ALayout(0, 0, true));
		countryPanel.add(fCountryFrom, new ALayoutConstraint(0,0));
		countryPanel.add(fLG_CountryTo_ID, new ALayoutConstraint(0,1));

		//  1st Row
		p_criteriaGrid.add(lCityFrom, new ALayoutConstraint(0,0));
		p_criteriaGrid.add(fCityFrom);
		p_criteriaGrid.add(lCityTo);
		p_criteriaGrid.add(fLG_CityTo_ID);
		
		//  2nd Row
		p_criteriaGrid.add(lCountryFrom, new ALayoutConstraint(1,0));
		p_criteriaGrid.add(fCountryFrom);
		p_criteriaGrid.add(lCountryTo);
		p_criteriaGrid.add(fLG_CountryTo_ID);
		
		//  3rd Row
		p_criteriaGrid.add(lAirportFrom, new ALayoutConstraint(2,0));
		p_criteriaGrid.add(fAirPortFrom);
		p_criteriaGrid.add(lAirPortTo);
		p_criteriaGrid.add(fLG_AirportTo_ID);

		//  4th Row
		p_criteriaGrid.add(lValidFromFrom, new ALayoutConstraint(3,0));
		p_criteriaGrid.add(datePanelFrom, null);
		p_criteriaGrid.add(lTransportType);
		p_criteriaGrid.add(fTransportType);

		//  5th Row
		p_criteriaGrid.add(lValidToFrom, new ALayoutConstraint(4,0));
		p_criteriaGrid.add(datePanelTo, null);

		//p_criteriaGrid.add(lCityTo, new ALayoutConstraint(1,0));
		//p_criteriaGrid.add(fLG_CityTo_ID);


		jTab.addTab(Msg.translate(Env.getCtx(), "Detail"), new JScrollPane(detailTbl));
		jTab.setPreferredSize(new Dimension(INFO_WIDTH, SCREEN_HEIGHT > 600 ? 250 : 105));
		tablePanel.setPreferredSize(new Dimension(INFO_WIDTH, SCREEN_HEIGHT > 600 ? 255 : 110));
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(jTab, BorderLayout.CENTER);


		//  Contact Tab
		ColumnInfo[] s_layoutDetail= new ColumnInfo[]{
				new ColumnInfo(" ", "LG_ProductPriceRateLine_ID", IDColumn.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Hasta Vol."), "BreakValueVolume", BigDecimal.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Precio Vol."), "priceVolume", BigDecimal.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Hasta Unid.."), "BreakValue", BigDecimal.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Precio Unid."), "PriceList", BigDecimal.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Cargos"), "(select name from m_product p where p.m_product_ID = rl.m_product_0_ID) ", String.class),
				//new ColumnInfo(Msg.translate(Env.getCtx(), "Precio Cargo"), "PriceStd", String.class),
				new ColumnInfo(Msg.translate(Env.getCtx(), "Tipo"), "lg_surchaseType", String.class)
		};
		//  From Clause
		String s_sqlFrom = "rv_lg_productpricerateline rl ";
		//  Where Clause					
		String s_sqlWhere = "LG_ProductPriceRate_ID = ?  and IsActive = 'Y'";
		m_sqlDetail = detailTbl.prepareTable(s_layoutDetail, s_sqlFrom, s_sqlWhere, false, "rl");
		detailTbl.setPreferredSize(new Dimension(INFO_WIDTH, SCREEN_HEIGHT > 600 ? 255 : 110));
		detailTbl.setRowSelectionAllowed(true);
		detailTbl.setMultiSelection(false);
		detailTbl.addMouseListener(this);
		detailTbl.getSelectionModel().addListSelectionListener(this);
		detailTbl.setShowTotals(false);
		detailTbl.autoSize();
		detailTbl.setBackground(new ColorUIResource(251,248,241));
		detailTbl.setForeground(new ColorUIResource(251,0,0));


		//  Details Panel
		p_detailTaskPane.setTitle(Msg.translate(Env.getCtx(), "Detalles"));
		p_detailTaskPane.add(tablePanel, BorderLayout.CENTER);
		p_detailTaskPane.setVisible(true);


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
        	MLGProductPriceRate p = new MLGProductPriceRate(Env.getCtx(),record_id, trxName);
    		p = null;
    		Trx.get(trxName, false).close();
        } 
        else  // Try to find other criteria in the context
        {
			String id;
			
			{
				//  LG_ProductPriceRate
				id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "LG_ProductPriceRate", true);
				if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
				{
					fieldID = new Integer(id).intValue();
		        	String trxName = Trx.createTrxName();
		        	MLGProductPriceRate r = new MLGProductPriceRate(Env.getCtx(),record_id, trxName);
		    		r = null;
		    		Trx.get(trxName, false).close();
				}
				//  LG_CityFrom_ID
				id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "LG_CityFrom_ID", true);
				if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
					fCityFrom.setValue(new Integer(id));
				
				//  LG_TransportType
				id = Env.getContext(Env.getCtx(), p_WindowNo, p_TabNo, "LG_TransportType", true);
				if (id != null && id.length() != 0 && (new Integer(id).intValue() > 0))
					fTransportType.setValue(new Integer(id));
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
		//
		if (fTransportType.getValue() != null && fTransportType.getValue().toString().length()>0)
			sql.append(" AND r.TransportType=?");		
		
		if (fValidFromFrom.getValue() != null || fValidFromTo.getValue() != null)
		{
			Timestamp fromFrom = (Timestamp)fValidFromFrom.getValue();
			Timestamp fromTo = (Timestamp)fValidFromTo.getValue();
			if (fromFrom == null && fromTo != null)
				sql.append(" AND TRUNC(r.ValidFrom, 'DD') <= ?");
			else if (fromFrom != null && fromTo == null)
				sql.append(" AND TRUNC(r.ValidFrom, 'DD') >= ?");
			else if (fromFrom != null && fromTo != null)
				sql.append(" AND (TRUNC(r.ValidFrom, 'DD') >= ? AND TRUNC(r.ValidFrom, 'DD')<= ?) ");
		}		
		
		if (fValidToFrom.getValue() != null || fValidToTo.getValue() != null)
		{
			Timestamp from = (Timestamp)fValidToFrom.getValue();
			Timestamp to = (Timestamp)fValidToTo.getValue();
			if (from == null && to != null)
				sql.append(" AND TRUNC(r.ValidTo, 'DD') <= ?");
			else if (from != null && to == null)
				sql.append(" AND TRUNC(r.ValidTo, 'DD') >= ?");
			else if (from != null && to != null)
				sql.append(" AND (TRUNC(r.ValidTo, 'DD') >= ? AND TRUNC(r.ValidTo, 'DD')<= ?) ");
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
		if (fValidFromFrom.getValue() != null || fValidFromTo.getValue() != null)
		{
			Timestamp fromFrom = (Timestamp)fValidFromFrom.getValue();
			Timestamp fromTo = (Timestamp)fValidFromTo.getValue();
			log.fine("Date From=" + fromFrom + ", To=" + fromTo);
			if (fromFrom == null && fromTo != null)
				pstmt.setTimestamp(index++, fromTo);
			else if (fromFrom != null && fromTo == null)
				pstmt.setTimestamp(index++, fromFrom);
			else if (fromFrom != null && fromTo != null)
			{
				pstmt.setTimestamp(index++, fromFrom);
				pstmt.setTimestamp(index++, fromTo);
			}
		}
		if (fValidToFrom.getValue() != null || fValidToTo.getValue() != null)
		{
			Timestamp toFrom = (Timestamp)fValidToFrom.getValue();
			Timestamp toTo = (Timestamp)fValidToTo.getValue();
			log.fine("Date From=" + toFrom + ", To=" + toTo);
			if (toFrom == null && toTo != null)
				pstmt.setTimestamp(index++, toTo);
			else if (toFrom != null && toTo == null)
				pstmt.setTimestamp(index++, toFrom);
			else if (toFrom != null && toTo != null)
			{
				pstmt.setTimestamp(index++, toFrom);
				pstmt.setTimestamp(index++, toTo);
			}
		}
		//
		if (fTransportType.getValue() != null && fTransportType.getValue().toString().length()>0)
		{
			String id = fTransportType.getDisplay();
			//pstmt.setString(index++,(String) fTransportType.getValue());
			pstmt.setString(index++, id);
			log.fine("TransportType=" + id);
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
				fValidFromFrom.hasChanged()	||
				fValidFromTo.hasChanged() 	||
				fValidToFrom.hasChanged()	||
				fValidToTo.hasChanged() 	||
				fTransportType.hasChanged()
				);
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
		fValidFromFrom.set_oldValue();
		fValidFromTo.set_oldValue();
		fValidToFrom.set_oldValue();
		fValidToTo.set_oldValue();
		fTransportType.set_oldValue();
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
		fValidFromFrom.setValue(nullObject);
		fValidFromTo.setValue(nullObject);
		fValidToFrom.setValue(nullObject);
		fValidToTo.setValue(nullObject);
		fTransportType.setValue(null);
	}
	private void refresh()
	{
		//  Invoke later to not delay events.
		SwingUtilities.invokeLater(new Runnable(){public void run()
		{
					
			PreparedStatement pstmt = null;
			ResultSet rs = null;
	    	
			//  Contact tab	
			log.finest(m_sqlDetail);
			try
			{
				pstmt = DB.prepareStatement(m_sqlDetail, null);
				pstmt.setInt(1, m_LG_ProductPriceRate_ID);
				rs = pstmt.executeQuery();
				detailTbl.loadTable(rs);
				rs.close();
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, m_sqlDetail, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			
		}});
		

		
	}	//	refresh

	protected void recordSelected(int key)
	{
		m_LG_ProductPriceRate_ID = getSelectedRowKey();
		refresh();
		p_detailTaskPane.setCollapsed(false);
		return;
	}
}   //  InfoPayment
