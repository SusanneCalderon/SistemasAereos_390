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

import java.sql.Timestamp;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MCharge;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_CreateNdDFromRequest  extends SvrProcess


{	

	int p_C_DocType_ID = 0;
    @Override    
    protected void prepare()
    {    	

        ProcessInfoParameter[] parameters = getParameter();
        for (ProcessInfoParameter parameter : parameters) {

            String name = parameter.getParameterName();
            if (name.equals(MDocType.COLUMNNAME_C_DocType_ID))
            	p_C_DocType_ID = parameter.getParameterAsInt();
            if (parameter.getParameter() == null)
                ;
        }
    }
    
      
    @Override
    protected String doIt() throws Exception
    {

    	MRequest req = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
    	MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());  	
    	invoice.setAD_Org_ID(req.getC_Project().getAD_Org_ID());
    	invoice.setDateInvoiced(new Timestamp (System.currentTimeMillis()));
    	invoice.setC_Project_ID(req.getC_Project_ID());
    	invoice.setC_Currency_ID(100);
    	invoice.setC_Activity_ID(req.getC_Activity_ID());
    	invoice.setC_Campaign_ID(req.getC_Campaign_ID());
    	MProject prj = (MProject)req.getC_Project();
    	invoice.setUser1_ID(prj.get_ValueAsInt("User1_ID"));
    	invoice.set_ValueOfColumn(MRequest.COLUMNNAME_R_Request_ID, req.getR_Request_ID());
    	invoice.setIsSOTrx(false);
    	invoice.setDescription(req.getSummary());

    	if (req.get_ValueAsInt("C_BPartnerVendor_ID") == 0)
    		return "Hay que definir el Proveedor";
    	invoice.setC_BPartner_ID(req.get_ValueAsInt("C_BPartnerVendor_ID"));
		//int c_doctype_ID = new Query(getCtx(), MDocType.Table_Name, "lower(name) = 'nota de débito de proveedor' and docbasetype = 'API'", get_TrxName())
		////	.setClient_ID()
		//	.firstId();
		invoice.setC_DocTypeTarget_ID(p_C_DocType_ID);
		invoice.saveEx();
     	//pay.setIsReceipt(false);
    	int c_charge_ID = req.get_ValueAsInt(MCharge.COLUMNNAME_C_Charge_ID);
    	if (c_charge_ID != 0)
    	{
    		MInvoiceLine ivl = new MInvoiceLine(invoice);
    		ivl.setC_Charge_ID(req.get_ValueAsInt(MCharge.COLUMNNAME_C_Charge_ID));
    		ivl.setQty(Env.ONE);
    		ivl.setPrice(req.getRequestAmt());
    		ivl.setC_Project_ID(req.getC_Project_ID());
    		ivl.set_ValueOfColumn("isSalesOrderImmediate", true);
    		ivl.saveEx();
    	}
    	req.setC_Invoice_ID(invoice.getC_Invoice_ID());
		int r_status = 0;
		if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
			r_status = 1000031;
		else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
			r_status = 1000030;
		if (r_status == 0)
			return "";
		//for (MRequest req:reqs)1000030
		

		//{
		req.setR_Status_ID(r_status);
		req.setSalesRep_ID(req.getCreatedBy());
		req.saveEx();
    		
    		
		String whereClauseWindow = "c_invoice_ID = " + invoice.getC_Invoice_ID();
		MQuery query = new MQuery("");
		MTable table = new MTable(getCtx(), MInvoice.Table_ID, get_TrxName());
		query.addRestriction(whereClauseWindow);
		query.setRecordCount(1);
		int AD_WindowNo = table.getPO_Window_ID();

		int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
		MSession session = new MSession(getCtx(), ad_session_ID, null);
		
		if (session.getWebSession() == null ||session.getWebSession().length() == 0)
		{
			commitEx();
			zoom (AD_WindowNo, query);
			return "";
			
		}
		return invoice.getDocumentInfo();
    }
    //MSession session = new MSession(getCtx(), Env.getContext(getCtx(), "), trxName)
    

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
    
}
