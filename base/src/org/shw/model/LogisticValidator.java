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
package org.shw.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankStatement;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPInstance;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProcess;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MRequest;
import org.compiere.model.MRole;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_Order;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Trx;
import org.eevolution.model.MWMInOutBound;
import org.eevolution.model.MWMInOutBoundLine;



/**
 *	Validator Example Implementation
 *	
 *	@author Susanne Calderon
 */
public class LogisticValidator implements ModelValidator
{
	/**
	 *	Constructor.
	 */
	public LogisticValidator ()
	{
		super ();
	}	//	MyValidator
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(SAValidatorNEW.class);
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
			engine.addModelChange(MWMInOutBound.Table_Name, this);
			engine.addModelChange(MRequest.Table_Name, this);
		
		//	We want to validate Order before preparing 
		
	
		
	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *	@param po persistent object
     *	@param type TYPE_
     *	@return error message or null     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	public String modelChange (PO po, int type) throws Exception
	{
		String error = null;
		if (po.get_TableName().equals(MRequest.Table_Name))
		{
			
		}

		


		if (po.get_TableName().equals(MWMInOutBound.Table_Name))
		{

			if (type == TYPE_BEFORE_NEW)
				;
			if (type==TYPE_BEFORE_CHANGE)
			{
				if (po.is_ValueChanged("ProcessingTest"))
				error =	StartProzessSHW_ControlLoadingGuide(po);
			}
			if (type == TYPE_AFTER_NEW)
				;
			if (type == TYPE_AFTER_CHANGE)
				;
			if (type == TYPE_AFTER_DELETE)
				;
			if (type == TYPE_AFTER_DELETE)
				;
			if (type==TYPE_DELETE)
				;			
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
		MUser user = new MUser(Env.getCtx(), AD_User_ID, null);
		if (user.getC_BPartner_ID() != 0)
		{
			MBPartner bpartner = (MBPartner)user.getC_BPartner();				
			Env.setContext(Env.getCtx(), "#User1_ID", bpartner.get_ValueAsInt("User1_ID"));			
		}

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
	
	/*String updatefactoraje(PO A_PO)
	{
		MInvoiceLine invLine = (MInvoiceLine)A_PO;
		return "";
	}*/
	

	String Project_CreateWMInoutbound(PO A_PO)
	{
		MProject project = (MProject)A_PO;		
		if (project.getProjectCategory() != null || project.getProjectCategory().equals(""))
			return"";
		
		if (!project.getProjectCategory().equals("T"))
			return "";
		if (!project.isSummary())
			return "";
		MWMInOutBound inb = new Query(project.getCtx(), MWMInOutBound.Table_Name, 
				"documentno =?", project.get_TrxName())
			.setOnlyActiveRecords(true)
			.setParameters(project.getValue())
			.setClient_ID()
			.first();
		if (inb == null)
		{
			inb = new MWMInOutBound(project.getCtx(), 0, project.get_TrxName());
			inb.setDocumentNo(project.getValue());
			inb.setAD_Org_ID(project.getAD_Org_ID());
			inb.setC_DocType_ID(project.get_ValueAsInt(MDocType.COLUMNNAME_C_DocType_ID));
			inb.setIsSOTrx(true);
			inb.setM_Warehouse_ID(1000000);
			inb.saveEx();
		}
		return "";
	}
	

	String Project_CreateWMInoutboundLine(PO A_PO)
	{
		MOrderLine ol = (MOrderLine)A_PO;
		MProject project = (MProject)ol.getC_Project();		
		if (project.getProjectCategory() != null || project.getProjectCategory().equals(""))
			return"";
		
		if (!project.getProjectCategory().equals("T"))
			return "";
		if (project.isSummary() || project.get_ValueAsInt("C_Project_Parent_ID")== 0)
			return "";
		MWMInOutBound inb = new Query(project.getCtx(), MWMInOutBound.Table_Name, 
				"documentno =?", project.get_TrxName())
			.setOnlyActiveRecords(true)
			.setParameters(project.getValue())
			.setClient_ID()
			.first();
		if (inb == null)
		{
			inb = new MWMInOutBound(project.getCtx(), 0, project.get_TrxName());
			inb.setDocumentNo(project.getValue());
			inb.setAD_Org_ID(project.getAD_Org_ID());
			inb.setC_DocType_ID(project.get_ValueAsInt(MDocType.COLUMNNAME_C_DocType_ID));
			inb.setIsSOTrx(true);
			inb.setM_Warehouse_ID(1000000);
			inb.saveEx();
		}
		return "";
	}


	String StartProzessSHW_ControlLoadingGuide(PO A_PO)
	{
		Boolean test = A_PO.get_ValueAsBoolean("ProcessingTest");
		if (!test)
		return "";
		String whereClause = "AD_Table_ID=? and columnname =?";
		MColumn col = new Query(A_PO.getCtx(), MColumn.Table_Name, whereClause, A_PO.get_TrxName())
			.setParameters(A_PO.get_Table_ID(), MWMInOutBound.COLUMNNAME_Processing)
			.first();
		int AD_Process_ID = col.getAD_Process_ID();

		MPInstance instance = new MPInstance(Env.getCtx(),AD_Process_ID, 0);
		if (!instance.save())
		{
			return null;
		}
		//call process
		ProcessInfo pi = new ProcessInfo ("CLG", AD_Process_ID);
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());
		pi.setRecord_ID(A_PO.get_ID());
		pi.setIsBatch(true);
		String innerTrxName = Trx.createTrxName("CLG");
		Trx innerTrx = Trx.get(innerTrxName, true);
		MProcess worker = new MProcess(A_PO.getCtx(),AD_Process_ID,A_PO.get_TrxName());
		worker.processIt(pi, Trx.get(innerTrx.getTrxName(), true));
		A_PO.set_ValueOfColumn("ProcessingTest", "N");
		return"";
	}
	
	String WM_InoutBoundAfterComplete(PO A_PO)
	{
		MWMInOutBound iobound = (MWMInOutBound)A_PO;

		{
		String whereClause = MDocType.COLUMNNAME_DocBaseType +" =? and " + MDocType.COLUMNNAME_DocSubTypeSO + " =?";
		int C_Doctype_ID = new Query(A_PO.getCtx(), MDocType.Table_Name, whereClause, A_PO.get_TrxName())
		.setClient_ID()
		.setOnlyActiveRecords(true)
		.setParameters(MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_StandardOrder)
		.firstId();
		for (MWMInOutBoundLine ioLine:iobound.getLines(true, ""))
		{
			if (ioLine.getC_OrderLine_ID() ==0)
			{
				MOrder order = new Query(A_PO.getCtx(), MOrder.Table_Name, "C_Project_ID=? and c_bpartner_ID=?", iobound.get_TrxName())
				.setOnlyActiveRecords(true)
				.setParameters(iobound.getC_Project_ID(), ioLine.get_ValueAsInt(MOrder.COLUMNNAME_C_BPartner_ID))
				.first();
				MBPartner bPartner = new MBPartner(A_PO.getCtx(), ioLine.get_ValueAsInt(MOrder.COLUMNNAME_C_BPartner_ID), A_PO.get_TrxName());
				if (order == null)
				{
					order = new MOrder(A_PO.getCtx(), 0, A_PO.get_TrxName());
					order.setC_DocType_ID(C_Doctype_ID);
					order.setDateOrdered(Env.getContextAsDate(iobound.getCtx(), "#Date"));
					order.setC_BPartner_ID(bPartner.getC_BPartner_ID());
					order.setM_PriceList_ID(bPartner.getM_PriceList_ID());
					order.setC_Project_ID(iobound.getC_Project_ID());
					if (!OrdersetBPartner(order, bPartner.getC_BPartner_ID(), bPartner.getC_PaymentTerm_ID()))
						return "no fue posible crear la orden de venta";
					order.setSalesRep_ID(bPartner.getSalesRep_ID());
					order.saveEx();
				}
				MOrderLine oLine = new MOrderLine(order);
				oLine.setM_Product_ID(ioLine.getM_Product_ID());
				MPriceList pl = new MPriceList(iobound.getCtx(),bPartner.getM_PriceList_ID() , ioLine.get_TrxName());
				MPriceListVersion plv = pl.getPriceListVersion(order.getDateOrdered());
				MProduct prod = (MProduct)ioLine.getM_Product();
				oLine.setC_UOM_ID(ioLine.getC_UOM_ID());
				oLine.setQtyOrdered((BigDecimal)ioLine.get_Value("Weight"));
				oLine.setQtyEntered((BigDecimal)ioLine.get_Value("Weight"));
				if (prod.get_ValueAsBoolean("isLG_FreightRateProduct"))
				{
					whereClause = "m_pricelist_Version_ID=? and (c_bpartner_ID is null or c_bpartner_ID=?) " +
							" and lg_route_ID =?";
					X_LG_ProductPriceRate ppr = new Query(iobound.getCtx(), X_LG_ProductPriceRate.Table_Name, whereClause, iobound.get_TrxName())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.setParameters(plv.getM_PriceList_Version_ID(), order.getC_BPartner_ID(), 
							iobound.get_ValueAsInt(X_LG_ProductPriceRate.COLUMNNAME_LG_Route_ID))
					.setOrderBy("C_BPartner_ID ")
					.first();
					oLine.setPrice((BigDecimal)ppr.get_Value("priceWeight"));
					oLine.saveEx();
				}
				else
				{
				}
				ioLine.setC_OrderLine_ID(oLine.getC_OrderLine_ID());
				ioLine.saveEx();
			}
		}
		}
		return "";
	}Boolean OrdersetBPartner(MOrder order, int C_BPartner_ID, int C_PaymentTerm_ID)
	{
		if (C_BPartner_ID  == 0)
			return false;
		String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
			+ " COALESCE(p.M_PriceList_ID,g.M_PriceList_ID) AS M_PriceList_ID, p.PaymentRule,p.POReference,"
			+ " p.SO_Description,p.IsDiscountPrinted,"
			+ " p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule,"
			+ " p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
			+ " lship.C_BPartner_Location_ID,c.AD_User_ID,"
			+ " COALESCE(p.PO_PriceList_ID,g.PO_PriceList_ID) AS PO_PriceList_ID, p.PaymentRulePO,p.PO_PaymentTerm_ID," 
			+ " lbill.C_BPartner_Location_ID AS Bill_Location_ID, p.SOCreditStatus, "
			+ " p.SalesRep_ID "
			+ "FROM C_BPartner p"
			+ " INNER JOIN C_BP_Group g ON (p.C_BP_Group_ID=g.C_BP_Group_ID)"			
			+ " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')"
			+ " LEFT OUTER JOIN C_BPartner_Location lship ON (p.C_BPartner_ID=lship.C_BPartner_ID AND lship.IsShipTo='Y' AND lship.IsActive='Y')"
			+ " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) "
			+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";		//	#1

		boolean IsSOTrx = true;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_BPartner_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				// Sales Rep - If BP has a default SalesRep then default it
				Integer salesRep = rs.getInt("SalesRep_ID");
				if (IsSOTrx && salesRep != 0 )
				{
					order.setSalesRep_ID(salesRep);
				}
				
				//	PriceList (indirect: IsTaxIncluded & Currency)
				Integer ii = new Integer(rs.getInt(IsSOTrx ? "M_PriceList_ID" : "PO_PriceList_ID"));
				if (!rs.wasNull())
					order.setM_PriceList_ID(ii);
				else
				{	//	get default PriceList
					int i = Env.getContextAsInt(order.getCtx(), "#M_PriceList_ID");
					if (i != 0)
						order.setM_PriceList_ID(i);
				}

				//	Bill-To
				order.setBill_BPartner_ID(C_BPartner_ID);
				int bill_Location_ID = rs.getInt("Bill_Location_ID");
				order.setBill_Location_ID(bill_Location_ID);
				// Ship-To Location
				int shipTo_ID = rs.getInt("C_BPartner_Location_ID");
				order.setC_BPartner_Location_ID(shipTo_ID);

				//	Contact - overwritten by InfoBP selection
				int contID = rs.getInt("AD_User_ID");
					order.setAD_User_ID(contID);
					order.setBill_User_ID(contID);


				order.setInvoiceRule(X_C_Order.INVOICERULE_AfterDelivery);
				order.setDeliveryRule(X_C_Order.DELIVERYRULE_Availability);
				order.setPaymentRule(X_C_Order.PAYMENTRULE_OnCredit);
				if (C_PaymentTerm_ID != 0)
				order.setC_PaymentTerm_ID(C_PaymentTerm_ID);
				else
				{
					C_PaymentTerm_ID = new Query(order.getCtx(), MPaymentTerm.Table_Name, 
							"isdefault =?", order.get_TrxName())
						.setClient_ID()
						.setOnlyActiveRecords(true)
						.setParameters(true)
						.firstId();
					order.setC_PaymentTerm_ID(C_PaymentTerm_ID);
				}
				order.setInvoiceRule(MOrder.INVOICERULE_Immediate);
				order.setDeliveryRule(MOrder.DELIVERYRULE_Availability);
				order.setFreightCostRule(MOrder.FREIGHTCOSTRULE_FreightIncluded);
				order.setDeliveryViaRule(MOrder.DELIVERYVIARULE_Pickup);
				
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return false;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	
		return true;
	}
	
	
	
	


	

	


	

	

}	//	MyValidator
