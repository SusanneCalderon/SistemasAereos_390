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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_Order;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MGLCategory;
import org.compiere.model.MInventory;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MMovement;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPInstance;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProcess;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MProduction;
import org.compiere.model.MProject;
import org.compiere.model.MProjectIssue;
import org.compiere.model.MRequest;
import org.compiere.model.MRequisition;
import org.compiere.model.MRole;
import org.compiere.model.MStatus;
import org.compiere.model.MTaxCategory;
import org.compiere.model.MTree;
import org.compiere.model.MTree_Base;
import org.compiere.model.MTree_Node;
import org.compiere.model.MUOMConversion;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.ProductCost;
import org.compiere.model.Query;
import org.compiere.model.X_C_Order;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.print.MPrintFormatItem;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.www.Test2;
import org.eevolution.model.MDDOrder;
import org.eevolution.model.MHRPayroll;
import org.eevolution.model.MHRProcess;
import org.eevolution.model.MPPOrder;
import org.eevolution.model.MWMInOutBound;
import org.eevolution.model.MWMInOutBoundLine;
import org.python.antlr.ast.GeneratorExp.generators_descriptor;



/**
 *	Validator Example Implementation
 *	
 *	@author Susanne Calderon
 */
public class SAValidatorNEW implements ModelValidator
{
	/**
	 *	Constructor.
	 */
	public SAValidatorNEW ()
	{
		super ();
	}	//	MyValidator
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(SHWValidator_BASICSNEW.class);
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
			engine.addModelChange(MProject.Table_Name, this);
			engine.addModelChange(MWMInOutBound.Table_Name, this);
			engine.addModelChange(MAllocationHdr.Table_Name, this);
			engine.addModelChange(MPayment.Table_Name, this);
			engine.addModelChange(MOrderLine.Table_Name, this);
			engine.addModelChange(MPaymentAllocate.Table_Name, this);
			engine.addModelChange(MRequest.Table_Name, this);
		
		//	We want to validate Order before preparing 
			engine.addDocValidate(MOrder.Table_Name, this);
			engine.addDocValidate(MPayment.Table_Name, this);
			engine.addDocValidate(MAllocationHdr.Table_Name, this);
			engine.addDocValidate(MInvoice.Table_Name, this);
			engine.addDocValidate(MBankStatement.Table_Name, this);
		
	
		
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
		if (po.get_TableName().equals(MPayment.Table_Name))
		{

			if (type == TYPE_BEFORE_NEW)
				;
			if (type==TYPE_BEFORE_CHANGE)
				;
			if (type == TYPE_AFTER_NEW)
				;
			if (type == TYPE_AFTER_CHANGE)
				;
			if (type == TYPE_AFTER_DELETE)
				;
			if (type == TYPE_BEFORE_DELETE)
				;
			if (type==TYPE_DELETE)
				;			
		}
		if (po.get_TableName().equals(MOrderLine.Table_Name))
		{
			if (type == TYPE_BEFORE_NEW)
				;
			if (type==TYPE_BEFORE_CHANGE)
				;
			if (type == TYPE_AFTER_NEW)
				error = OrderLineUpdateControlAmt(po);	
			if (type == TYPE_AFTER_CHANGE)
			{
				error = OrderLineUpdateControlAmt(po);	
				error = updateIsInvoiced(po);
			}
			if (type == TYPE_AFTER_DELETE)
				;
			if (type == TYPE_BEFORE_DELETE)
				//error = test(po)				
				;
			if (type==TYPE_DELETE)
				error = OLineBeforeDelete(po);
		}

		if (po.get_TableName().equals(MPaymentAllocate.Table_Name))
		{

			if (type == TYPE_BEFORE_NEW)
				;
			if (type==TYPE_BEFORE_CHANGE)
				;
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
		

		if (po.get_TableName().equals(MAllocationHdr.Table_Name))
		{

			if (type == TYPE_BEFORE_NEW)
				;
			if (type==TYPE_BEFORE_CHANGE)
				;
			if (type == TYPE_AFTER_NEW)
				;
			if (type == TYPE_AFTER_CHANGE)
				;
			if (type == TYPE_AFTER_DELETE)
				//error = ProjectAllocationDelete(po);
			if (type==TYPE_DELETE)
				;			
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

		if (po.get_TableName().equals(MProject.Table_Name))
		{

			if (type == TYPE_BEFORE_NEW)
				//error = ProjectVolume_Weight(po);
				;
			if (type == TYPE_AFTER_NEW)
			{				
				error =	Project_CreateWMInoutbound(po);
				error =	UpdateDocumentation(po);
				//error = test2(po);
			}
			if (type == TYPE_AFTER_CHANGE )
			{				
				error =	Project_CreateWMInoutbound(po);
				error =	UpdateDocumentation(po);
				//error = test2(po);
			}
			if (type == TYPE_AFTER_DELETE)
				;
			if (type == TYPE_AFTER_DELETE)
				;
			if (type==TYPE_DELETE)
				;			
		}
		
		if (po.get_TableName().equals(MOrder.Table_Name))
		{
			//if (type == TYPE_BEFORE_CHANGE)
			//	error = test2(po);
		}

		if (po.get_TableName().equals(MRequest.Table_Name))
		{
			if (type == TYPE_AFTER_NEW || type == TYPE_AFTER_CHANGE)
			error = SaveRequestTree(po);
			if (type == TYPE_BEFORE_CHANGE || type == TYPE_BEFORE_NEW)
				RequestSavaParent(po);
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
		
		if (po.get_TableName().equals(MInvoice.Table_Name))
		{
			if (timing == TIMING_BEFORE_PREPARE)
			{
				//Pricing Modul
				error = InvoiceCalculateContract(po);		
				// VoidInvoice_DocType Ueberpruefen
				//OneProject_AfterPrepare Ueberpruefen
			}
			if (timing == TIMING_AFTER_PREPARE)
				;
			if (timing == TIMING_BEFORE_COMPLETE)
				;
			if (timing == TIMING_AFTER_COMPLETE)
			{
				//error = ProjectInvoiceComplete(po);
				error = OrderLineControlInvoiced(po);		
				error = InvoiceLineCreateOdV(po);		
				error = NdCUpdateControlAmt(po);	
				//Script DocNoInvoiceAfterComplete Docno fuer Annulierungsdokumente
				//Script groovy:NdDUpdateCTAJ Controlamt im Payment aktualisieren
				// BASIC 		error = InvoiceUpdateM_Product_PO(po);
				// BASIC		 error = assignPrepayment(po);
			}
			if (timing == TIMING_BEFORE_VOID)
				;
			if (timing == TIMING_AFTER_VOID)
				//Script DocNoInvoiceAfterVoid DocNo fuer annulierte Rechnungen
				;
			if (timing == TIMING_BEFORE_POST)
				;
			if (timing == TIMING_AFTER_POST)
				;
		}
		
		if (po.get_TableName().equals(MPayment.Table_Name))
		{
			if (timing == TIMING_BEFORE_PREPARE)
				//BASIC checkCashOpen(PO A_PO)
				;
			if (timing == TIMING_AFTER_PREPARE)
			{
				error = PaymentCreateOrderLine(po);				
			}
			if (timing == TIMING_BEFORE_COMPLETE)
				;
			if (timing == TIMING_AFTER_COMPLETE)
			{
				//error = ProjectPagoCuentaAjena(po);
				//error = ProjectPaymentAllocationComplete(po);	
				//Script DocNoPaymentAfterComplete Anulation
			}
			if (timing == TIMING_BEFORE_VOID)
				;
			if (timing == TIMING_AFTER_VOID || timing == TIMING_AFTER_REVERSECORRECT || timing == TIMING_AFTER_REACTIVATE)
			{
				error = DeletePayment(po);
				//Script DocNoPaymentAfterVoid DocumentNO _anulado
			}
			if (timing == TIMING_BEFORE_POST)
				;
			if (timing == TIMING_AFTER_POST)
				;
		}
		if (po.get_TableName().equals(MOrder.Table_Name))
		{
			if (timing == TIMING_BEFORE_PREPARE)
			{			
			}
			if (timing == TIMING_AFTER_PREPARE)
				;
			if (timing == TIMING_BEFORE_COMPLETE)
				;
			if (timing == TIMING_AFTER_COMPLETE)
			{
				//error = test(po);
			}
			if (timing == TIMING_AFTER_VOID 
					|| timing == TIMING_AFTER_REVERSECORRECT 
					|| timing == TIMING_AFTER_REACTIVATE)
			{
				//error = ProjectOrderVoid(po);
				//error = ProjectOrderComplete(po);
			}
				//error = ProjectOrderVoid(po);
			if (timing == TIMING_BEFORE_POST)
				;
			if (timing == TIMING_AFTER_POST)
				;			
		}
		
		if (po.get_TableName().equals(MAllocationHdr.Table_Name))
		{
			if (timing == TIMING_BEFORE_PREPARE)
			{			
			}
			if (timing == TIMING_AFTER_PREPARE)
				;
			if (timing == TIMING_BEFORE_COMPLETE)
				;
			if (timing == TIMING_AFTER_COMPLETE)
			{
				CreatePaymentFromAllocationReembolso(po);
			}
			if (timing == TIMING_BEFORE_VOID)
				;
			if (timing == TIMING_AFTER_VOID || timing == TIMING_AFTER_REVERSECORRECT || timing == TIMING_AFTER_REACTIVATE)
				//error = ProjectPaymentAllocationsVoid(po);
			if (timing == TIMING_BEFORE_POST)
				AfterPost_CorrectGL_Category(po);
			if (timing == TIMING_AFTER_POST)
				;
		}

		if (po.get_TableName().equals(MBankStatement.Table_Name))
		{
			//if (timing == TIMING_BEFORE_REACTIVATE)
				//test2(po);
		}

		if (po.get_TableName().equals(MHRProcess.Table_Name))
		{
			if (timing == TIMING_BEFORE_POST)
				;
		}
		
		
/*
		
		if (timing == TIMING_BEFORE_POST )
		{
			factAcct_UpdateDocumentNO(po);
			//PostCOGS_Invoice(po);
		}		*/
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
	}
	
	Boolean OrdersetBPartner(MOrder order, int C_BPartner_ID, int C_PaymentTerm_ID)
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
	
	
	
	private String DeletePayment(PO A_PO)
	{
		
		int c_payment_ID = A_PO.get_ID();

	    ArrayList<Object> parameters = new ArrayList<Object>();
	    parameters.add(c_payment_ID);
		List<MOrderLine> oLines = new Query(A_PO.getCtx(), MOrderLine.Table_Name, "c_payment_ID=?", A_PO.get_TrxName())
			.setParameters(parameters)
			.list();
		for (MOrderLine oLine:oLines)
		{
			if (oLine.getParent().getDocStatus().equals(MPayment.DOCSTATUS_Completed)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Closed))
				return "No es posible anular o borrar un pago a cuenta ajena que se encuentra en una orden de venta completada";
			if (oLine.getParent().getDocStatus().equals(MPayment.DOCSTATUS_Reversed)
					|| oLine.getParent().getDocStatus().equals(MPayment.DOCSTATUS_Voided)
					//|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_InProgress)
					)
				return "";
			if (oLine.get_ValueAsInt(MPayment.COLUMNNAME_C_Payment_ID)> 0)
			{	
				ArrayList<Object> deleteParameters = new ArrayList<Object>();
				deleteParameters.add(oLine.getC_OrderLine_ID());
				String sqlDelete ="Update r_request set c_orderline_ID = null where c_Orderline_ID=?";
				DB.executeUpdateEx(sqlDelete, deleteParameters.toArray(), A_PO.get_TrxName());
				oLine.deleteEx(true);
				A_PO.set_ValueOfColumn("isInvoiced", false);
				A_PO.saveEx();
			}
		}
		
		return "";
	}
	

	//Als Rule an Tabelle AD_OrgInfo
	//private String OrgInfo_UpdateWarehouse(PO A_PO)
	{/*
		MOrgInfo orgInfo = (MOrgInfo)A_PO;

		MWarehouse wh = null;
		{
			MWarehouse[] whs = MWarehouse.getForOrg(A_PO.getCtx(), orgInfo.getAD_Org_ID());
			if (whs != null && whs.length > 0)
				wh = whs[0];	//	pick first
		}
		//	New Warehouse
		if (wh == null)
		{
			MOrg org = new MOrg(A_PO.getCtx(), orgInfo.getAD_Org_ID(), A_PO.get_TrxName());
			wh = new MWarehouse(org);
			if (!wh.save(A_PO.get_TrxName()))
				return "El almacén no fue generado";
		}
		//	Create Locator
		MLocator mLoc = wh.getDefaultLocator();
		if (mLoc == null)
		{
			mLoc = new MLocator (wh, "Standard");
			mLoc.setIsDefault(true);
			mLoc.save(A_PO.get_TrxName());
		}
		
		//	Update/Save Org Info
		orgInfo.setM_Warehouse_ID(wh.getM_Warehouse_ID());
		if (!orgInfo.save(orgInfo.get_TrxName()))
			return "La información no fue actualizado";
		return "";
	*/}
	
	//
	//private String updatefactAcct_AllocationHdr(PO A_PO)
	{/*
		MAllocationHdr ahd = (MAllocationHdr)A_PO;
		Doc doc = ahd.getDoc();
		
		ArrayList<Fact> facts = doc.getFacts();
		// one fact per acctschema
		for (int i = 0; i < facts.size(); i++)
		{
			Fact fact = facts.get(i);
			MAcctSchema as = fact.getAcctSchema();
			for (MAllocationLine alo:ahd.getLines(true))
			{
				if (alo.getC_Payment_ID() <= 0)
					continue;
				if (!(alo.getC_Payment().getC_DocType_ID() == 1000342 || alo.getC_Payment().getC_DocType_ID() == 1000343))
					continue;
				List<FactLine> fLines = 
				new Query(ahd.getCtx(), MFactAcct.Table_Name, "line_ID =? and ad_table_ID = 724", A_PO.get_TrxName())
					.setParameters(alo.getC_AllocationLine_ID())
					.list();
				for (FactLine fLine:fLines)
				{
					if (fLine.getAccount().equals(doc.getAccount(Doc.ACCTTYPE_UnallocatedCash, as)))
						fLine.setAccount(as, doc.getAccount(Doc.ACCTTYPE_C_Prepayment, as));
				}
			}
		}
		return "";
	*/}

	private String UpdateDocumentation(PO A_PO)
	{
		if (A_PO.is_ValueChanged("DocumentoDeTransporte") 
				|| A_PO.is_ValueChanged("CodigoDeDeclaracion") 
				|| A_PO.is_ValueChanged("ReferenciaDeDeclaracion")
				|| A_PO.is_ValueChanged("Provider") 
				|| A_PO.is_ValueChanged("ProviderPO")				
				)
		{
			String whereClause = "c_Project_ID=" + A_PO.get_ID();
			
			List<MOrder> orders = new Query(A_PO.getCtx(), MOrder.Table_Name, whereClause, A_PO.get_TrxName())
				.list();
			for (MOrder order:orders)
			{
				order.set_ValueOfColumn("DocumentoDeTransporte", A_PO.get_Value("DocumentoDeTransporte"));
				order.set_ValueOfColumn("CodigoDeDeclaracion", A_PO.get_Value("CodigoDeDeclaracion"));
				order.set_ValueOfColumn("ReferenciaDeDeclaracion", A_PO.get_Value("ReferenciaDeDeclaracion"));
				order.set_ValueOfColumn("Provider", A_PO.get_Value("Provider"));
				order.set_ValueOfColumn("ProviderPO", A_PO.get_Value("ProviderPO"));
				order.saveEx();
			}

			List<MInvoice> invoices = new Query(A_PO.getCtx(), MInvoice.Table_Name, whereClause, A_PO.get_TrxName())
				.list();
			for (MInvoice invoice:invoices)
			{
				invoice.set_ValueOfColumn("DocumentoDeTransporte", A_PO.get_Value("DocumentoDeTransporte"));
				invoice.set_ValueOfColumn("CodigoDeDeclaracion", A_PO.get_Value("CodigoDeDeclaracion"));
				invoice.set_ValueOfColumn("ReferenciaDeDeclaracion", A_PO.get_Value("ReferenciaDeDeclaracion"));
				invoice.set_ValueOfColumn("Provider", A_PO.get_Value("Provider"));
				invoice.set_ValueOfColumn("ProviderPO", A_PO.get_Value("ProviderPO"));
				invoice.saveEx();
			}
		}
		return "";
	}

	private String ProjectInvoiceComplete(PO A_PO)
	{
	    List<Integer> ProjectIds = new ArrayList<Integer>();
		MInvoice invoice = (MInvoice)A_PO;
		for (MInvoiceLine ivl:invoice.getLines())
		{
			int c_project_ID = ivl.getC_Project_ID();
			if (c_project_ID ==0 )
				c_project_ID = ivl.getC_Invoice().getC_Project_ID();
			if (c_project_ID != 0 && !ProjectIds.contains(ivl.getC_Project_ID()))
				ProjectIds.add(c_project_ID);
		}
		
		for (int c_project_ID:ProjectIds)
		{
			MProject project = new MProject(A_PO.getCtx(), c_project_ID, A_PO.get_TrxName());
			project.updateProject();			
		}
		return "";
	}

	private String ProjectPagoCuentaAjena(PO A_PO)
	{
		int C_Project_ID = A_PO.get_ValueAsInt(MProject.COLUMNNAME_C_Project_ID);
		if (C_Project_ID <=0)
			return "";
		MProject project = new MProject(A_PO.getCtx(), C_Project_ID, A_PO.get_TrxName());
		MPayment pay = (MPayment)A_PO;
		if (!(pay.getC_DocType_ID() == 1000404 || pay.getC_DocType_ID() == 1000413))
			return "";
		BigDecimal Cost = (BigDecimal)project.get_Value("Cost");
		project.set_ValueOfColumn("Cost", Cost.add(pay.getPayAmt()));

		Cost = (BigDecimal)(project.get_Value("Cost"));
		BigDecimal distributedAmt = (BigDecimal)(project.get_Value("distributedAmt"));
		project.setProjectBalanceAmt(project.getInvoicedAmt().subtract(Cost).subtract(distributedAmt));
		project.saveEx();
		return "";
	}
	
	private String ProjectAllocationDelete(PO A_PO)
	{
		MAllocationHdr ahd = (MAllocationHdr)A_PO;
		for (MAllocationLine alo: ahd.getLines(true))
		{
			if (alo.getC_Invoice_ID() <=0)
				continue;
			if (!alo.getC_Invoice().isSOTrx())
				continue;
			MInvoice invoice = (MInvoice)alo.getC_Invoice();
			for (MInvoiceLine ivl:invoice.getLines())
			{
				MProject project = (MProject)ivl.getC_Project();
				BigDecimal OpenAmt = (BigDecimal)project.get_Value("OpenAmt");
				project.set_ValueOfColumn("OpenAmt", OpenAmt.add(ivl.getLineTotalAmt()));
			}
			
		}
		return "";
	}


	private String ProjectOrderComplete(PO A_PO)
	{
	    List<Integer> ProjectIds = new ArrayList<Integer>();
	    MOrder order = (MOrder)A_PO;
		for (MOrderLine ol:order.getLines())
		{
			int c_project_ID = ol.getC_Project_ID();
			if (c_project_ID ==0 )
				c_project_ID = ol.getC_Order().getC_Project_ID();
			if (c_project_ID != 0 && !ProjectIds.contains(ol.getC_Project_ID()))
				ProjectIds.add(c_project_ID);
		}
		
		for (int c_project_ID:ProjectIds)
		{
			MProject project = new MProject(A_PO.getCtx(), c_project_ID, A_PO.get_TrxName());
			project.updateProject();
		}
		return "";
	}
	

	private String ProjectOrderVoid(PO A_PO)
	{
		int C_Project_ID = A_PO.get_ValueAsInt(MProject.COLUMNNAME_C_Project_ID);
		if (C_Project_ID <=0)
			return "";
		MProject project = new MProject(A_PO.getCtx(), C_Project_ID, A_PO.get_TrxName());
		MOrder order = (MOrder)A_PO;
		if (order.isSOTrx())
			project.setPlannedAmt(project.getPlannedAmt().subtract(order.getGrandTotal()));
		else
			project.setCommittedAmt(project.getCommittedAmt().subtract(order.getGrandTotal()));
		project.saveEx();
		return "";
	}

	private String ProjectPaymentAllocationsVoid(PO A_PO)
	{
		MAllocationHdr ahd = (MAllocationHdr)A_PO;
		for (MAllocationLine alo:ahd.getLines(true))
		{
			if (alo.getC_Payment_ID()<=0)
				continue;
			if (alo.getC_Invoice_ID()<=0 
					|| !alo.getC_Payment().isReceipt() 
					|| alo.getC_Invoice().getDocStatus().equals("RE") 
					|| alo.getC_Invoice().getDocStatus().equals("VO"))
				continue;
			MInvoice invoice = (MInvoice)alo.getC_Invoice();
			for (MInvoiceLine ivl:invoice.getLines())
			{
				if (ivl.getC_Project_ID() == 0)
					continue;
				MProject project = (MProject)ivl.getC_Project();
				BigDecimal OpenAmt = (BigDecimal)project.get_Value("OpenAmt");
				project.set_ValueOfColumn("OpenAmt", OpenAmt.add(ivl.getLineNetAmt()));
				project.saveEx();				
			}
		}
		return "";
	}
		

	private String ProjectPaymentAllocationComplete(PO A_PO)
	{
		MPayment pay = (MPayment)A_PO;
		MAllocationHdr[] allocations = MAllocationHdr.getOfPayment(A_PO.getCtx(),pay.getC_Payment_ID(), A_PO.get_TrxName());
		for (MAllocationHdr ahd:allocations)
		{
			for (MAllocationLine alo:ahd.getLines(true))
			{
				if (alo.getC_Invoice_ID()<=0 
						|| !alo.getC_Payment().isReceipt() 
						|| alo.getC_Invoice().getDocStatus().equals("RE") 
						|| alo.getC_Invoice().getDocStatus().equals("VO"))
					continue;
					if (alo.getC_Invoice().getC_Project_ID() <=0 )
						continue;
					MProject project = (MProject)alo.getC_Invoice().getC_Project();
					BigDecimal OpenAmt = (BigDecimal)project.get_Value("OpenAmt");
					if (OpenAmt == null)
						OpenAmt = Env.ZERO;
					project.set_ValueOfColumn("OpenAmt", OpenAmt.subtract(alo.getAmount()));
					project.saveEx();
			}
			
		}
			
		return "";
	}

	


	

	public String AfterPost_CorrectGL_Category(PO po)	
	{
		MAllocationHdr ah = (MAllocationHdr)po;

		Doc doc = ah.getDoc();

		ArrayList<Fact> facts = doc.getFacts();
		// one fact per acctschema
		String description = "";
		for (Fact fact:facts)
		{
			for (FactLine fLine:fact.getLines())
			{
				description = "";
				Boolean isPayment = false;
				MAllocationLine alo = new MAllocationLine(po.getCtx(), fLine.getLine_ID(), po.get_TrxName());
				if (alo.getC_Payment_ID() !=0)
				{
					fLine.setGL_Category_ID(alo.getC_Payment().getC_DocType().getGL_Category_ID());
					
					description = "Pago: " + alo.getC_Payment().getDocumentNo();
					continue;
				}
				else if (alo.getC_CashLine_ID()!=0)
				{
					if (alo.getC_CashLine().getC_Invoice_ID() != 0)
						isPayment = alo.getC_Invoice().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_APPayment)?
								true:false;
					else 
					{
						isPayment = alo.getAmount().compareTo(Env.ZERO)>=0?true:false;
					}
					
				}
				MDocType dt = null;
				if (isPayment)
					dt = new Query(po.getCtx(), MDocType.Table_Name, "Docbasetype = 'APP'", null)
						.first();
				else
					dt = new Query(po.getCtx(), MDocType.Table_Name, "Docbasetype = 'ARR'", null)
				.first();
				fLine.setGL_Category_ID(dt.getGL_Category_ID());
				if (alo.getC_Invoice_ID() != 0)
					description = description + " Factura: " + alo.getC_Invoice().getDocumentNo();
				else if (alo.getC_Charge_ID() != 0)
					description = description + " Cargo: " + alo.getC_Charge().getName();
			}
		}	
		return "";

	}

	private String OrderLineControlInvoiced(PO A_PO)
	{ 
		MInvoice inv = (MInvoice)A_PO;
		if (!inv.isSOTrx())
			return "";
		if (inv.getC_Order_ID() != 0 && inv.getC_Order().getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_OnCreditOrder))
			return "";
		String whereClause = "c_order_ID in (select c_order_ID from c_orderline where " +
				" c_orderline_ID in (select c_orderline_ID from c_invoiceline where c_invoice_ID =?))";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(A_PO.get_ID());
		List<MOrder> orders = new Query(A_PO.getCtx(), MOrder.Table_Name, whereClause, A_PO.get_TrxName())
		.setParameters(params.toArray())
		.list();
		Boolean isinvoiced = true;
		for (MOrder order: orders)
		{ 
			for (MOrderLine oLine:order.getLines())
			{
				if (oLine.getQtyInvoiced().compareTo(oLine.getQtyOrdered()) < 0)
				{
					isinvoiced = false;
					break;
				}
			}
			order.setIsInvoiced(isinvoiced);
			order.saveEx();
			int r_status = 0;
			Boolean processed = false;
			
			if (isinvoiced)
			{
				if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
					r_status = 1000007;
				else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
					r_status = 1000013;
				processed = true;
			}
			else
			{
				if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
					r_status = 1000023;
				else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
					r_status = 1000022;
			}
			whereClause = "c_order_ID=? and r_requesttype_ID in (1000002,1000004)";
			MRequest req = new Query(inv.getCtx(), MRequest.Table_Name, whereClause, inv.get_TrxName())
			.setParameters(order.getC_Order_ID())
			.first();
			if (req == null)
				return "";
			if (r_status == 0)
				return "";
			//for (MRequest req:reqs)
			//{
			String sqlupdate = "update r_request set r_status_ID=?, processed =? where r_request_ID=?";
			params.clear();
			params.add(r_status);
			params.add(processed);
			params.add(req.getR_Request_ID());
			int no = DB.executeUpdateEx(sqlupdate, params.toArray(), inv.get_TrxName());			
			return  "";                
		}

		return "";
	}

	private String InvoiceCalculateContract(PO A_PO)
	{
		MInvoice inv = (MInvoice)A_PO;
		if (inv.getReversal_ID()>0)
			return "";
		Calendar invoice1 = TimeUtil.getToday();
		Calendar invoice2 = TimeUtil.getToday();
		if (inv.getReversal_ID() != 0 || !inv.isSOTrx())
			return"";
		for (MInvoiceLine ivlLine: inv.getLines())
    	{
			if (ivlLine.getC_Charge_ID() > 0)
				continue;
        	String whereClause = "isvalid = 'Y' and (c_bpartner_ID = ? or c_bpartner_ID is null) and LG_RateType = 'G' and m_pricelist_ID = ? " +
        			" and  exists (select 1 from lg_productpricerateline where m_product_id = ? and isactive = 'Y') ";
        	ArrayList<Object> param1 = new ArrayList<Object>();
        	param1.add(inv.getC_BPartner_ID());
        	param1.add(inv.getM_PriceList_ID());
        	param1.add(ivlLine.getM_Product_ID());
        	MLGProductPriceRate pprl = new Query(A_PO.getCtx(), X_LG_ProductPriceRate.Table_Name, whereClause, A_PO.get_TrxName())
        		.setOnlyActiveRecords(true)
        		.setParameters(param1)
        		.first();
        	if (pprl == null)
        		continue;
        	String whereClauseProducts = "("; 
        	for (MLGProductPriceRateLine ppl:pprl.getLines())
        	{
        		whereClauseProducts = whereClauseProducts + ppl.getM_Product_ID() + ",";
        	}
        	whereClauseProducts = whereClauseProducts.substring(0, whereClauseProducts.length() -1) + ")";
        	String sqlSales = "select sum(qtyinvoiced) from c_invoiceline ivl " + 
        			" inner join c_invoice i on ivl.c_invoice_ID = i.c_invoice_ID" + 
        			" where m_product_ID in " + whereClauseProducts +
        			" and c_bpartner_ID =?" + 
        			" and (i.docstatus in ('CO','CL')" + " and i.dateinvoiced between ? and ?)" +
        			" or (ivl.c_invoice_ID=? and ivl.line < ? and m_product_ID in " + whereClauseProducts + ")";
        			;
    		if (pprl.get_ValueAsString("InvoiceFrequency") != null)
    		{
    			ArrayList<Object> params = new ArrayList<Object>();
    			String frequencytype = pprl.get_ValueAsString("InvoiceFrequency");
    			if (frequencytype.equals("D"))
    			{
    				params.add(ivlLine.getC_Invoice().getC_BPartner_ID());
    				params.add(ivlLine.getC_Invoice().getDateInvoiced());
    				params.add(ivlLine.getC_Invoice().getDateInvoiced());
    				params.add(inv.getC_Invoice_ID());
    				params.add(ivlLine.getLine());
    				invoice1.setTime(ivlLine.getC_Invoice().getDateInvoiced());
    				invoice2.setTime(ivlLine.getC_Invoice().getDateInvoiced());
    			}
    			else if (frequencytype.equals("M"))	
    			{	
    				Timestamp orderDate = ivlLine.getC_Invoice().getDateInvoiced();
    				invoice1 = TimeUtil.getToday();
    				invoice1.setTime(orderDate);
    				invoice1.set(Calendar.DAY_OF_MONTH, 1);
    				invoice2 = TimeUtil.getToday();
    				invoice2.setTime(orderDate);
    				invoice2.set(Calendar.DAY_OF_MONTH, invoice1.getActualMaximum(Calendar.DAY_OF_MONTH));
    				params.add(ivlLine.getC_Invoice().getC_BPartner_ID());
    				params.add(new Timestamp (invoice1.getTimeInMillis()));
    				params.add(new Timestamp (invoice2.getTimeInMillis()));
    				params.add(inv.getC_Invoice_ID());
    				params.add(ivlLine.getLine());
    			}
    			BigDecimal qtyinvoiced = DB.getSQLValueBD(A_PO.get_TrxName(), sqlSales, params);
    			if (qtyinvoiced == null)
    				qtyinvoiced = Env.ZERO;
    			qtyinvoiced = qtyinvoiced.add(ivlLine.getQtyInvoiced());
    			String whereClause_LPR = " m_product_ID=? and lg_productpricerate_ID=? and ?> breakvalue";
    			ArrayList< Object> param2 = new ArrayList<Object>();
    			param2.add(ivlLine.getM_Product_ID());
    			param2.add(pprl.getLG_ProductPriceRate_ID());
    			param2.add(qtyinvoiced);
    			MLGProductPriceRateLine plr = new Query(A_PO.getCtx(), MLGProductPriceRateLine.Table_Name, whereClause_LPR, A_PO.get_TrxName())
    				.setParameters(param2)
    				.setOrderBy("ORDER BY BreakValue DESC")
    				.first();
    			/*
    			BigDecimal qtyToInvoice = Env.ZERO;
    			if (plr.getBreakValue().compareTo(Env.ZERO) == 0)
    				qtyToInvoice = ivlLine.getQtyEntered();
    			BigDecimal qtyleft = (plr.getBreakValue().subtract(qtyinvoiced));
    			if (qtyleft.compareTo(Env.ZERO)<=0)
    					qtyleft= Env.ZERO;
    			qtyToInvoice = ivlLine.getQtyInvoiced().subtract(qtyleft);
    			if (qtyToInvoice.compareTo(Env.ZERO) == 1)*/
    			{
    				//ivlLine.setPriceActual(plr.getPriceStd());
    				ivlLine.setPriceActual(plr.getPriceStd());
    				ivlLine.setPriceEntered(plr.getPriceStd());
    			}/*
    			else
    				ivlLine.setPriceActual(Env.ZERO);*/
    			ivlLine.saveEx();    	
    			if(ivlLine.getPriceActual().longValue() ==0)
    				distributeGenerarInvoice(plr, ivlLine,new Timestamp (invoice1.getTimeInMillis()), new Timestamp (invoice2.getTimeInMillis()));
    		}
    	}
		return "";
	}
	

	private String PaymentCreateOrderLine(PO A_PO)
	{
		MPayment pay = (MPayment)A_PO;
		if (!(pay.getC_DocType_ID() == 1000413 || pay.getC_DocType_ID() ==1000404 ))
			return "";

		if (pay.getC_Charge_ID() ==0)
		{
			CreateCTAJPaymentAllocs(pay);
			return "";
		}
		if (pay.get_ValueAsInt("R_Request_ID") == 0)
			return "";
		//List<MRequest> reqs = new Query(A_PO.getCtx(), MRequest.Table_Name, "c_payment_ID=?", A_PO.get_TrxName())
		//.setParameters(pay.getC_Payment_ID())
		//.list();
		//if (reqs == null || reqs.isEmpty())
		//	return "";
		
		//for (MRequest req:reqs)
		//{
			MRequest req = new MRequest(pay.getCtx(), pay.get_ValueAsInt("R_Request_ID") , pay.get_TrxName());
			if (req.getC_Order_ID() <=0)
				return "";
			if (req.getR_Status().isClosed())
				return "";
			//if (pay.getC_Charge_ID()!= 0)
			String result = CreateCTAJPayment(req, pay);
			if (result != "")
				return result;
			//else
			//	CreateCTAJPaymentAllocs(req, pay);
		//}
		int r_status = 0;
		if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
			r_status = 1000001;
		else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
			r_status = 1000011;
		if (r_status == 0)
			return "";
		//for (MRequest req:reqs)
		//{
			req.setR_Status_ID(r_status);
			req.setSalesRep_ID(req.getCreatedBy());
			req.saveEx();
		//}
		return "";
		}
	

	private String OrderLineUpdateControlAmt(PO A_PO)
	{
		MOrderLine oLine = (MOrderLine)A_PO;
		//for (MOrderLine oLine: order.getLines())
		{
			if (A_PO.is_ValueChanged("LineNetAmt"))
				return "";
			if (oLine.get_ValueAsInt("C_Payment_ID") == 0  && oLine.get_ValueAsInt("C_PaymentAllocate_ID")== 0)
				return "";
			if (oLine.getC_Charge_ID() == 0)
				return "";
			if (!(oLine.getC_Charge().getC_ChargeType_ID()== 1000002
					|| oLine.getC_Charge().getC_ChargeType_ID()== 1000003))
				return "";
			if (oLine.get_ValueAsInt("C_Payment_ID") != 0 && oLine.get_ValueAsInt("C_PaymentAllocate_ID") == 0 )
			{
				MPayment pay = new MPayment(A_PO.getCtx(), oLine.get_ValueAsInt("C_Payment_ID"), A_PO.get_TrxName());
				pay.set_ValueOfColumn("ControlAmt", oLine.getLineNetAmt());
				pay.saveEx();				
			}
			if (oLine.get_ValueAsInt("C_PaymentAllocate_ID") != 0 )
			{
				MPaymentAllocate alloc = new MPaymentAllocate(A_PO.getCtx(), oLine.get_ValueAsInt("C_PaymentAllocate_ID"), A_PO.get_TrxName());
				alloc.set_ValueOfColumn("ControlAmt", oLine.getLineNetAmt());
				alloc.saveEx();				
			}
		}
		return "";
	}

	private String NdCUpdateControlAmt(PO A_PO)
	{
		MInvoice ndN = (MInvoice)A_PO;
		for (MInvoiceLine iLine: ndN.getLines())
		{
			if (iLine.get_ValueAsInt("C_Payment_ID")==0 && iLine.get_ValueAsInt("C_PaymentAllocate_ID")==0)
				return "";
			if (iLine.get_ValueAsInt("C_Payment_ID")!=0 && iLine.get_ValueAsInt("C_PaymentAllocate_ID") ==0)
			{
				MPayment pay = new MPayment(A_PO.getCtx(), iLine.get_ValueAsInt("C_Payment_ID"), A_PO.get_TrxName());
				BigDecimal ctajAmt = (BigDecimal)pay.get_Value("ControlAmt");
				ctajAmt = ctajAmt.add(iLine.getLineNetAmt());
				pay.set_ValueOfColumn("ControlAmt", ctajAmt);
				pay.saveEx();				
			}
			if (iLine.get_ValueAsInt("C_PaymentAllocate_ID")!=0)
			{
				MPaymentAllocate alloc = new MPaymentAllocate(A_PO.getCtx(), iLine.get_ValueAsInt("C_PaymentAllocate_ID"), A_PO.get_TrxName());
				BigDecimal ctajAmt = (BigDecimal)alloc.get_Value("ControlAmt");
				ctajAmt = ctajAmt.add(iLine.getLineNetAmt());
				alloc.set_ValueOfColumn("ControlAmt", ctajAmt);
				alloc.saveEx();				
			}
		}
		return "";
	}
	
	
	private String distributeGenerarInvoice(MLGProductPriceRateLine pprl, MInvoiceLine ivl, Timestamp date1, Timestamp date2)
	{
		String whereClause = "exists (select 1 from c_invoiceline where M_product_ID =?) and dateinvoiced between ? and ? " +
				" and c_bpartner_ID =? and issotrx = 'Y' and docstatus in ('DR','IP','CO', 'CL')";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(pprl.getLG_ProductPriceRate().getM_Product_ID());
		params.add(date1);
		params.add(date2);
		params.add(ivl.getC_Invoice().getC_BPartner_ID());
		MInvoice invoice = new Query(ivl.getCtx(), MInvoice.Table_Name, whereClause, ivl.get_TrxName())
			.setParameters(params)
			.first();
		if (invoice == null)
			return"";
		BigDecimal calcIncome = (BigDecimal)pprl.get_Value("calculatedIncome");
		whereClause = "c_invoice_ID=?";/*
		MJournal journal = new Query(ivl.getCtx(), MJournal.Table_Name, whereClause, ivl.get_TrxName())
			.setParameters(invoice.getC_Invoice_ID())
			.first();
		if (journal == null)*/
		MJournal	journal = new MJournal(ivl.getCtx(), 0, ivl.get_TrxName());
		for (MJournalLine jl:journal.getLines(true))
		{
			if (jl.get_ValueAsInt("C_InvoiceLine_ID") == ivl.getC_InvoiceLine_ID())
				return "";
		}
		MAcctSchema[] accts = MAcctSchema.getClientAcctSchema(ivl.getCtx(), ivl.getAD_Client_ID());
		for (MAcctSchema as:accts){
			int glcat_ID = MGLCategory.getDefault(ivl.getCtx(), MGLCategory.CATEGORYTYPE_Manual).getGL_Category_ID();
			int c_doctype_ID = new Query(ivl.getCtx(), MDocType.Table_Name	, "gl_category_ID=?", ivl.get_TrxName())
				.setParameters(glcat_ID)
				.setOnlyActiveRecords(true)
				.firstId();
			journal.setC_Currency_ID(100);
			journal.setClientOrg(invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
			journal.setC_AcctSchema_ID(as.get_ID());
			journal.setC_ConversionType_ID(114);
			journal.setDescription("");
			journal.setGL_Category_ID(glcat_ID);
			journal.setC_DocType_ID(c_doctype_ID);
			journal.setPostingType("A");
			journal.setDocumentNo(DB.getDocumentNo(ivl.getAD_Client_ID(), MJournal.Table_Name, ivl.get_TrxName()));
			journal.setDescription(invoice.getDocumentInfo());
			journal.saveEx();
			//NeuBuchung mit Projekt
			MJournalLine jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(ivl.getM_Product_ID());
			jLine.setC_Project_ID(ivl.getC_Project_ID());
			jLine.setC_BPartner_ID(ivl.getC_Invoice().getC_BPartner_ID());
			jLine.setC_Activity_ID(ivl.getC_Invoice().getC_Activity_ID());
			jLine.setC_Campaign_ID(ivl.getC_Invoice().getC_Campaign_ID());
			ProductCost pc = new ProductCost (ivl.getCtx(),ivl.getM_Product_ID()	, 0,ivl.get_TrxName());		
			MAccount revenue = pc.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
			jLine.setAccount_ID(revenue.getAccount_ID());
			jLine.setAmtSourceCr(calcIncome);
			jLine.set_ValueOfColumn("C_InvoiceLine_ID", ivl.getC_InvoiceLine_ID());
			jLine.saveEx();
			//Rueckbuchung

			jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(pprl.getLG_ProductPriceRate().getM_Product_ID());
			jLine.setC_BPartner_ID(ivl.getC_Invoice().getC_BPartner_ID());
			jLine.setC_Activity_ID(ivl.getC_Invoice().getC_Activity_ID());
			jLine.setC_Campaign_ID(ivl.getC_Invoice().getC_Campaign_ID());
			pc = new ProductCost (ivl.getCtx(),pprl.getM_Product_ID()	, 0,ivl.get_TrxName());			
			jLine.setAccount_ID(pc.getAccount(ProductCost.ACCTTYPE_P_Revenue, as).getAccount_ID());
			jLine.setAmtSourceDr(calcIncome);
			jLine.set_ValueOfColumn("C_InvoiceLine_ID", ivl.getC_InvoiceLine_ID());
			jLine.saveEx();
		}
		journal.processIt(MJournal.DOCACTION_Complete);
		journal.setDocAction(MJournal.DOCACTION_Close);
		journal.saveEx();
		
		return "";
	}
	
	
	private String OLineBeforeDelete(PO A_PO)
	{
	    MOrderLine oLine = (MOrderLine)A_PO;
        if (oLine.getC_Charge_ID() == 0)
            return "";
        if (!(oLine.getC_Charge().getC_ChargeType_ID()== 1000002
                || oLine.getC_Charge().getC_ChargeType_ID()== 1000003))
            return "";
		if (oLine.get_ValueAsInt("C_Payment_ID") == 0  && oLine.get_ValueAsInt("C_PaymentAllocate_ID")== 0)
			return "";
		
		if (oLine.get_ValueAsInt("C_Payment_ID") != 0 && oLine.get_ValueAsInt("C_PaymentAllocate_ID") == 0 )
		{
	        MPayment pay = new MPayment(A_PO.getCtx(), oLine.get_ValueAsInt("C_Payment_ID"), A_PO.get_TrxName());
	        pay.set_ValueOfColumn("ControlAmt", Env.ZERO);
	        pay.saveEx();  			
		}

		if (oLine.get_ValueAsInt("C_PaymentAllocate_ID") != 0 )
		{
			MPaymentAllocate alloc = new MPaymentAllocate(A_PO.getCtx(), oLine.get_ValueAsInt("C_PaymentAllocate_ID"), A_PO.get_TrxName());
			alloc.set_ValueOfColumn("ControlAmt", Env.ZERO);
			alloc.saveEx();  			
		}
	    return "";
		
	}
	

	
	private String CreateCTAJPayment(MRequest req, MPayment pay)
	{
		MOrder order = (MOrder)req.getC_Order();
		if (req.getC_Order_ID()<= 0)
			return "la solicitud " + req.getDocumentNo() + "no tiene referencia a niguna orden";
		if (req.get_ValueAsInt("C_OrderLine_ID")!=0)
		{
			MOrderLine oLine = new MOrderLine(pay.getCtx(), req.get_ValueAsInt("C_OrderLine_ID"), pay.get_TrxName());
			oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
			oLine.saveEx();
			return "";
		}
		BigDecimal p_DistributionAmt = pay.getPayAmt();
		MOrderLine oLine = new Query(pay.getCtx(), MOrderLine.Table_Name, "c_order_ID=? and c_payment_ID=?", pay.get_TrxName())
			.setParameters(order.getC_Order_ID(), pay.getC_Payment_ID())
			.first();
		if (oLine != null)
			return "";
		oLine = new MOrderLine(order);
		int chargeID;
		chargeID = pay.getC_Charge_ID();
		oLine.setC_Charge_ID(chargeID);
		MCharge charge = new MCharge(pay.getCtx(), chargeID, pay.get_TrxName());
		MTaxCategory tc = (MTaxCategory)charge.getC_TaxCategory();
		oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());

		oLine.setQty(Env.ONE);
		oLine.setPrice(p_DistributionAmt);
		oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
		oLine.setC_Project_ID(pay.getC_Project_ID());
		MBPartner bpartner = (MBPartner)oLine.getC_Order().getC_BPartner();
		oLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
		oLine.set_ValueOfColumn("R_Request_ID", req.getR_Request_ID());
		oLine.saveEx();
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + order.getDocumentNo() );
		pay.set_ValueOfColumn("ControlAmt", p_DistributionAmt);
		pay.saveEx();
		return "";
	}	

	private void CreateCTAJPaymentAllocs(MPayment pay)
	{
		String description = "";
		int r_status = 0;
		if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
			r_status = 1000001;
		else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
			r_status = 1000011;
		if (r_status == 0)
			return ;
		int chargeID;
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(pay);
		for (MPaymentAllocate alloc:pAllocs)
		{
			if (alloc.getC_Invoice_ID() >0)
				continue;
			int r_request_ID = alloc.get_ValueAsInt("R_Request_ID");
			if (r_request_ID <=0)
				continue;
			MRequest req = new MRequest(pay.getCtx(), r_request_ID, pay.get_TrxName());
			if (req.getC_Order_ID() <=0)
				continue;
			if (req.get_ValueAsInt("C_OrderLine_ID")!=0)
			{
				MOrderLine oLine = new MOrderLine(pay.getCtx(), req.get_ValueAsInt("C_OrderLine_ID"), pay.get_TrxName());
				oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
				oLine.saveEx();
				continue;
			}
			MOrder order = (MOrder)req.getC_Order();
			MOrderLine oLine = new MOrderLine(order);
			chargeID = alloc.get_ValueAsInt("C_Charge_ID");
			if (chargeID <=0)
				continue;
			oLine.setC_Charge_ID(chargeID);
			MCharge charge = new MCharge(pay.getCtx(), chargeID, pay.get_TrxName());
			MTaxCategory tc = (MTaxCategory)charge.getC_TaxCategory();
			oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());
			oLine.setQty(Env.ONE);
			oLine.setPrice(alloc.getAmount());
			oLine.set_ValueOfColumn(MPaymentAllocate.COLUMNNAME_C_PaymentAllocate_ID, alloc.getC_PaymentAllocate_ID());
			oLine.set_ValueOfColumn(MPaymentAllocate.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
			oLine.setC_Project_ID(pay.getC_Project_ID());
			MBPartner bpartner = (MBPartner)oLine.getC_Order().getC_BPartner();
			oLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
			oLine.saveEx();
			req.setR_Status_ID(r_status);
			req.setSalesRep_ID(req.getCreatedBy());
			req.saveEx();
			description = description + " " + order.getDocumentNo();
			alloc.set_ValueOfColumn("ControlAmt", oLine.getLineNetAmt());
		}
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + description);
		pay.saveEx();

	}
	

	private String InvoiceLineCreateOdV(PO A_PO)
	{
		MInvoice invoice = (MInvoice)A_PO;
		if (!invoice.getC_DocType().getDocBaseType().equals("API"))
			return "";


		if (!(invoice.getC_DocTypeTarget_ID() == 1000441        
				|| invoice.getC_DocTypeTarget_ID() == 1000442))
			return "";

		String whereClause = "c_project_ID=? and issotrx = 'Y' and docstatus in ('DR','IP') and c_doctypetarget_ID not in (1000424, 1000375)";                
		for (MInvoiceLine iLine:invoice.getLines())                    
		{
			if (iLine.getC_OrderLine_ID() > 0)
				return "";
			if (!(iLine.get_ValueAsBoolean("isSalesOrderImmediate")&& iLine.getC_Project_ID() >=0))
				return "";
			MOrder salesOrder = new Query(A_PO.getCtx(), MOrder.Table_Name, whereClause    , A_PO.get_TrxName())
			.setParameters(iLine.getC_Project_ID())
			.setOnlyActiveRecords(true)
			.first();
			if (salesOrder != null && salesOrder.getC_Order_ID() != 0)
			{
				MOrderLine oSalesLine = new MOrderLine(salesOrder);
				oSalesLine.setC_Charge_ID(iLine.getC_Charge_ID());
				oSalesLine.setQty(iLine.getQtyEntered());
				oSalesLine.setPrice(iLine.getPriceEntered());
				oSalesLine.set_ValueOfColumn("c_invoiceline_PO_ID", iLine.getC_OrderLine_ID());
				MBPartner bpartner = (MBPartner)salesOrder.getC_BPartner();
				oSalesLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
				oSalesLine.setPriceCost(iLine.getPriceEntered());
				oSalesLine.setC_Project_ID(iLine.getC_Project_ID());
				oSalesLine.setC_Activity_ID(iLine.getC_Activity_ID());
				oSalesLine.setUser1_ID(iLine.getUser1_ID());
				oSalesLine.saveEx();                    

				iLine.set_ValueOfColumn("C_OrderLine_SO_ID", oSalesLine.getC_OrderLine_ID());
				iLine.saveEx();
			}
			else
				return "No existe una orden de venta";
		}
		whereClause = "c_invoice_ID=?";
		MRequest req = new Query(invoice.getCtx(), MRequest.Table_Name, whereClause, invoice.get_TrxName())
			.setParameters(invoice.getC_Invoice_ID())
			.first();
		if (req == null)
			return "";
		if (req.getR_Status().isClosed())
			return "";
		int r_status = 0;
		if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
			r_status = 1000001;
		else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
			r_status = 1000011;
		if (r_status == 0)
			return "";
		//for (MRequest req:reqs)
		//{
		req.setR_Status_ID(r_status);
		req.setSalesRep_ID(req.getCreatedBy());
		req.saveEx();
		return  "";                
	}
	
	

	private String CreatePaymentFromAllocationReembolso(PO A_PO)
	{
		MAllocationHdr alloc = (MAllocationHdr)A_PO;
		MPayment payOrg = null;
		MPayment pay = null;
		for (MAllocationLine alo:alloc.getLines(true))
		{
			if (alo.getC_Payment_ID() != 0)
				payOrg = (MPayment)alo.getC_Payment();
		}
		for (MAllocationLine alo:alloc.getLines(true))
		{
			if (!(alo.getC_Charge_ID() == 1000280
					|| alo.getC_Charge_ID() ==1000279))
				continue;			

	    	int defaultaccount = new Query(A_PO.getCtx(), MBankAccount.Table_Name, "", A_PO.get_TrxName())
			.setClient_ID()
			.setOnlyActiveRecords(true)
			.setOrderBy("C_Bankaccount_ID ")
			.firstId(); 
	    	BigDecimal multiplicator = payOrg.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_ARReceipt)? 
	    			Env.ONE.negate()
	    			: Env.ONE;
	    	pay = new MPayment(A_PO.getCtx(), 0, A_PO.get_TrxName());  	
	    	pay.setC_BankAccount_ID(defaultaccount);
	    	pay.setTenderType(MPayment.TENDERTYPE_Account);
	    	pay.setDateTrx(new Timestamp (System.currentTimeMillis()));
	    	pay.setIsOverUnderPayment(true);
	    	pay.setAD_Org_ID(alo.getAD_Org_ID());
	    	pay.setC_Project_ID(payOrg.getC_Project_ID());
	    	int c_charge_ID = alo.getC_Charge_ID();
	    	if (c_charge_ID == 0)
	    		return"";
	    	int c_doctype_ID = 0;
	    	//if (alo.getAD_Client_ID()== 1000001)
	    	{
	    		//c_charge_ID =1000314;
	    		String docbasetype = payOrg.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_ARReceipt)?
	    				MDocType.DOCBASETYPE_APPayment:
	    					MDocType.DOCBASETYPE_ARReceipt;
	    		c_doctype_ID = MDocType.getDocType(docbasetype);
	    		//c_doctype_ID =  payOrg.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_ARReceipt)?
	    		//		1000479:1000465;
	    	}
	    	//else if (alo.getAD_Client_ID() == 1000012)
	    	//{
	    	//	c_doctype_ID =  alo.getC_Invoice().isSOTrx()?1000481:1000464; 
	    	//}
	    	if (c_doctype_ID ==0)
	    		return "";
	    	
	    	pay.setC_DocType_ID(c_doctype_ID);

	     	pay.setIsReceipt(pay.getC_DocType().isSOTrx());
	    	pay.setC_Charge_ID(c_charge_ID);
	    	pay.setC_Currency_ID(100);
	    	pay.setC_BPartner_ID(payOrg.getC_BPartner_ID());
	    	pay.setPayAmt(alo.getAmount().multiply(multiplicator));
	    	pay.setDescription("Aviso de Pago por reembolso de anticipo");
	    	pay.set_CustomColumn("C_AllocationLine_ID", alo.getC_AllocationLine_ID());
	    	pay.saveEx();
	    	Env.setContext(Env.getCtx(),alloc.getDocumentNo(), pay.getDocumentNo());
		}
		return "";
	}

	private String updateIsInvoiced(PO A_PO)
	
	{	
		if (!A_PO.is_ValueChanged("QtyInvoiced"))
			return "";
		
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(A_PO.get_ValueAsInt("C_Order_ID"));
			
			String sql = 
					"select sum(qtyordered-qtyinvoiced) from c_orderline where c_order_ID=?";
			BigDecimal i = DB.getSQLValueBDEx(A_PO.get_TrxName(), sql, params.toArray());
			if (i.signum()==0)
			{
				sql = "Update c_order set isinvoiced = 'Y' where c_order_ID=?";
				DB.executeUpdateEx(sql, params.toArray(), A_PO.get_TrxName());
			}			
		return "";
	}
	

	private String ProjectVolume_Weight(PO A_PO)
	
	{/*
		MProject project = (MProject)A_PO;
		String ProjectCategory = project.getProjectCategory();
		if (ProjectCategory.equals("T"))
		{
			int C_Project_Parent_ID = A_PO.get_ValueAsInt("C_Project_Parent_ID");
			MProject parent = new MProject(A_PO.getCtx(), C_Project_Parent_ID, null);
			String ProjectDistribution = parent.get_ValueAsString("ProjectDistribution");
			if (ProjectDistribution .equals("V"))
			{
				BigDecimal volume = (BigDecimal)A_PO.get_Value("Volume");
				if (volume.compareTo(Env.ZERO)<= 0)
					return "Falta definir el volumen";
			}
			if (ProjectDistribution .equals("W"))
			{
				BigDecimal weight = (BigDecimal)parent.get_Value("Weight");
				if (weight.compareTo(Env.ZERO)<= 0)
					return "Falta definir el peso";
			}
		}*/
		return "";
	}
	

	private String SaveRequestTree(PO A_PO)
	
	{
		/*Properties A_Ctx = A_PO.getCtx();
		MRequest request = (MRequest)A_PO;
		Boolean isSummary = request.get_ValueAsBoolean("IsSummary");
		//if (A_PO.is_ValueChanged ("IsSummary") || request.get_ValueAsInt("R_Request_Parent_ID") != 0)
		{

			ArrayList< Object> params = new ArrayList<Object>();
			int requestparent_ID = A_PO.get_ValueAsInt("R_Request_Parent_ID");
			//if (requestparent_ID == 0)
			//	return "";
			MRequest parent = new MRequest(A_Ctx, requestparent_ID, A_PO.get_TrxName());
			StringBuffer whereClause = new StringBuffer();
			whereClause.append(MTree.COLUMNNAME_TreeType + " = ?");
			whereClause.append( " and isDefault='Y' ");
			params.add(MTree.TREETYPE_Request);
			MTree_Base tree = new Query(A_Ctx, MTree.Table_Name,whereClause.toString(), A_PO.get_TrxName())
					.setParameters(params.toArray())
					.first();
			//if (requestparent_ID == 0)
			//	return "";
			if (tree == null)
				return "";
			MTree_Node node = MTree_Node.get(tree, A_PO.get_ID());
			if (node==null)
				node = new MTree_Node(tree, A_PO.get_ID());
			node.setParent_ID(requestparent_ID);
			node.saveEx();  
		}
		if (A_PO.is_ValueChanged("R_Request_Parent_ID"))
		{
			int requestparent_ID = A_PO.get_ValueAsInt("R_Request_Parent_ID");
			if (requestparent_ID == 0)
				return "";
			MRequest parent = new MRequest(A_Ctx, requestparent_ID, A_PO.get_TrxName());
			request.set_ValueOfColumn("C_BPartner_ID", parent.get_Value("C_BPartner_ID"));
		}*/
		return "";
	}
private String RequestSavaParent(PO A_PO)
	
{/*
	MRequest request = (MRequest)A_PO;
	if (A_PO.is_ValueChanged("R_Request_Parent_ID"))
	{
		int requestparent_ID = A_PO.get_ValueAsInt("R_Request_Parent_ID");
		if (requestparent_ID == 0)
			return "";
		MRequest parent = new MRequest(A_PO.getCtx(), requestparent_ID, A_PO.get_TrxName());
		request.set_ValueOfColumn("C_BPartner_ID", parent.get_Value("C_BPartner_ID"));
		request.setR_RequestType_ID(parent.getR_RequestType_ID());
	}*/
	return "";
}


private String test (PO A_PO)
{
	String A_TrxName = A_PO.get_TrxName();
	MPrintFormatItem pfi = (MPrintFormatItem)A_PO;

	String sql = "update ad_printformatitem_trl set printname = ? where ad_printformatitem_ID = ?";
	ArrayList<Object> params = new ArrayList<>();
	params.add(pfi.getPrintName());
	params.add(pfi.getAD_PrintFormatItem_ID());
	DB.executeUpdateEx(sql, params.toArray(), A_TrxName);
	return "";
}
	
	
	
	
	
	


	
	
	

	
	

		

}	//	MyValidator
