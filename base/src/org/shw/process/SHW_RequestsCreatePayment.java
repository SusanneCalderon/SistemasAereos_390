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
package org.shw.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MRequestType;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.model.X_R_Status;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.shw.model.MLGProductPriceRate;

/**
 *	Generate Invoices
 *	
 *  @author SHW
 *  @version $Id: SHW_InvoiceGenerate.java,v 1.2 2015/01/24 00:51:01 mc Exp $
 */
public class SHW_RequestsCreatePayment extends SvrProcess
{
	protected List<MRequest> m_records = null;
	private 	MPayment pay = null;
	private 	int p_C_BPartner_ID;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			if (name.equals(MBPartner.COLUMNNAME_C_BPartner_ID))
				p_C_BPartner_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}	//	prepare

	/**
	 * 	Generate Invoices
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=R_Request.R_Request_ID)";
		m_records = new Query(getCtx(), MRequest.Table_Name, whereClause, get_TrxName())
											.setParameters(getAD_PInstance_ID())
											.setClient_ID()
											.list();
		
		String whereClauseStatus = "EXISTS (SELECT 1 FROM R_RequestType rt " 
				+ " INNER JOIN R_StatusCategory sc ON (rt.R_StatusCategory_ID=sc.R_StatusCategory_ID) " 
				+ " WHERE R_Status.R_StatusCategory_ID=sc.R_StatusCategory_ID " 
				+ " AND rt.R_RequestType_ID=? and and isclosed = 'Y'";
		
		for (MRequest req:m_records)
		{
			if (pay == null)
			{
				pay = new MPayment(getCtx(), 0, get_TrxName());
		    	int defaultaccount = new Query(req.getCtx(), MBankAccount.Table_Name, "", req.get_TrxName())
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.firstId(); 
		    	int c_bpartner_ID = req.get_ValueAsInt("C_BPartnerVendor_ID");
		    	if (p_C_BPartner_ID!= 0)
		    		c_bpartner_ID = p_C_BPartner_ID;
	        	if (req.get_ValueAsInt("C_BPartnerVendor_ID") == 0)
	        		return "Hay que definir el Proveedor";
	        	pay.setC_BPartner_ID(c_bpartner_ID); 	
		    	pay.setC_BankAccount_ID(defaultaccount);
		    	pay.setDateTrx(new Timestamp (System.currentTimeMillis()));
		    	pay.setIsOverUnderPayment(true);
		    	pay.setAD_Org_ID(req.getAD_Org_ID());
		    	pay.setC_Project_ID(req.getC_Project_ID());
		    	MRequestType rtype = (MRequestType)req.getR_RequestType();		    	
		    	if (rtype.get_ValueAsInt(MPayment.COLUMNNAME_C_DocType_ID)!= 0)
		    		pay.setC_DocType_ID(rtype.get_ValueAsInt(MPayment.COLUMNNAME_C_DocType_ID));
		    	else
		    		pay.setC_DocType_ID(false);
		    	pay.setC_Currency_ID(100);
		    	pay.saveEx();				
			}
	     	MPaymentAllocate palloc = new MPaymentAllocate(getCtx(), 0,get_TrxName());
	     	palloc.setAD_Org_ID(pay.getAD_Org_ID());
	     	palloc.setC_Payment_ID(pay.getC_Payment_ID());
	        palloc.set_ValueOfColumn(MPayment.COLUMNNAME_C_Charge_ID, req.get_ValueAsInt(MCharge.COLUMNNAME_C_Charge_ID));
	        palloc.setAmount(req.getRequestAmt());
	        palloc.set_ValueOfColumn("C_Activity_ID", req.getC_Activity_ID());
	        palloc.set_ValueOfColumn(MRequest.COLUMNNAME_C_Campaign_ID, req.getC_Campaign_ID());
	    	MProject prj = (MProject)req.getC_Project();
	    	palloc.set_ValueOfColumn("User1_ID",prj.get_ValueAsInt("User1_ID"));
	    	palloc.set_ValueOfColumn("C_Project_ID",prj.getC_Project_ID());
	    	palloc.set_ValueOfColumn(MRequest.COLUMNNAME_R_Request_ID, req.getR_Request_ID());
	    	palloc.saveEx();
	    	req.setC_Payment_ID(pay.getC_Payment_ID());
	    	req.doClose();
	    	req.saveEx();
	    	}		
	    		
			String whereClauseWindow  = "c_payment_ID=" + pay.getC_Payment_ID();
			MQuery query = new MQuery("");
			MTable table = new MTable(getCtx(), MPayment.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(1);
			int AD_WindowNo = table.getAD_Window_ID();
			commitEx();
			zoom (AD_WindowNo, query);
			
	    	return "";
	    

		
	}	//	doIt


	protected void zoom (int AD_Window_ID, MQuery zoomQuery)
	{
		final AWindow frame = new AWindow();
		if (!frame.initWindow(AD_Window_ID, zoomQuery))
			return;
		AEnv.addToWindowManager(frame);
		//	VLookup gets info after method finishes
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(50);
				}
				catch (Exception e)
				{
				}
				AEnv.showCenterScreen(frame);
			}
		}.start();
	}	//	zoom
    


	
}	//	InvoiceGenerate
