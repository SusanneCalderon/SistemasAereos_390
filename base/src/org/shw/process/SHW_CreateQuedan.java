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
import java.util.ArrayList;
import java.util.List;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceBatch;
import org.compiere.model.MInvoiceBatchLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MQuery;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_CreateQuedan  extends SvrProcess
{	
	protected List<MInvoice> m_records = null;
	private Timestamp p_paydate = null;
    @Override    
    protected void prepare()
    {    	

        ProcessInfoParameter[] parameters = getParameter();
        for (ProcessInfoParameter parameter : parameters) {

            String name = parameter.getParameterName();
            if (parameter.getParameter() == null)
                ;
            if (name.equals("PayDate"))
            	p_paydate = parameter.getParameterAsTimestamp();
        }
    }
    
      
    @Override
    protected String doIt() throws Exception
    {

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=C_Invoice.C_INVOICE_ID)";
		m_records = new Query(getCtx(), MInvoice.Table_Name, whereClause, get_TrxName())
											.setParameters(getAD_PInstance_ID())
											.setClient_ID()
											.list();
		List<MInvoiceBatch> m_invoicebatches = null;
		m_invoicebatches  = new ArrayList<MInvoiceBatch>();
		
		if (getRecord_ID() != 0)
		{
			String whereClause_Invoice = "c_invoice_ID = " + getRecord_ID();
			MInvoiceBatchLine ibl = new Query(getCtx(), MInvoiceBatchLine.Table_Name,
					whereClause_Invoice, get_TrxName())
				.first();
			if (ibl != null)
				return "Ya está incluida en el quedan no." + ibl.getC_InvoiceBatch().getDocumentNo();
			MInvoice inv = new MInvoice(getCtx(), getRecord_ID(), get_TrxName());
			m_records.add(inv);
		}
		
		for (MInvoice invoice:m_records)
		{
			String sql = "select c_invoicebatch_ID from c_invoicebatchline ibl where c_invoice_ID = " + invoice.getC_Invoice_ID();
			int ibatch_ID = DB.getSQLValueEx(get_TrxName(), sql);
			MInvoiceBatch ib = new MInvoiceBatch(getCtx(), ibatch_ID, get_TrxName());
			if (ibatch_ID != 0)
			{
				ib.setDateDoc(	new Timestamp (System.currentTimeMillis()));
				ib.set_ValueOfColumn("isQuedan", true);
				ib.set_ValueOfColumn("PayDate", p_paydate);
				ib.setIsSOTrx(invoice.isSOTrx());
				ib.setC_Currency_ID(invoice.getC_Currency_ID());
				ib.setAD_Org_ID(invoice.getAD_Org_ID());
				ib.setControlAmt(invoice.getGrandTotal());
				ib.setDocumentAmt(invoice.getGrandTotal());
				ib.setDocumentNo("Quedan " + invoice.getDocumentNo());
				ib.set_ValueOfColumn("IsApproved", true);
				ib.setSalesRep_ID(invoice.getSalesRep_ID());
				ib.set_ValueOfColumn("C_PaymentTerm_ID", invoice.getC_PaymentTerm_ID());
				Timestamp datedue = null;
						//MPaymentTerm.dueDate(invoice.getC_PaymentTerm_ID(), ib.getDateDoc());
				ib.set_ValueOfColumn("DateConfirm", datedue);
				ib.set_ValueOfColumn("C_BPartner_ID", invoice.getC_BPartner_ID());
				ib.saveEx();	
				int c_tax_ID = 0;
				for (MInvoiceLine ivl:invoice.getLines())
				{
					c_tax_ID = ivl.getC_Tax_ID();
					break;
				}
				MInvoiceBatchLine ibl = new MInvoiceBatchLine(getCtx(), 0, get_TrxName());
				ibl.setAD_Org_ID(ib.getAD_Org_ID());
				ibl.setC_InvoiceBatch_ID(ib.getC_InvoiceBatch_ID());
				ibl.setC_Invoice_ID(invoice.getC_Invoice_ID());
				ibl.setDateInvoiced(invoice.getDateInvoiced());
				ibl.setDocumentNo(invoice.getDocumentNo());
				ibl.setPriceEntered(invoice.getGrandTotal());
				ibl.setIsTaxIncluded(false);
				ibl.setLineNetAmt(invoice.getTotalLines());
				ibl.setLineTotalAmt(invoice.getGrandTotal());
				ibl.setC_BPartner_ID(invoice.getC_BPartner_ID());
				ibl.setC_Tax_ID(c_tax_ID);
				ibl.saveEx();
			}
			m_invoicebatches.add(ib);
		}		

		String whereClauseWindow = "c_invoicebatch_ID in (";
		for (MInvoiceBatch	 ib: m_invoicebatches)
		{
			whereClauseWindow = whereClauseWindow + ib.getC_InvoiceBatch_ID() + ",";
		}
		whereClauseWindow = whereClauseWindow.substring(0, whereClauseWindow.length() -1) + ")";
			MQuery query = new MQuery("");
			MTable table = new MTable(getCtx(), MInvoiceBatch.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(m_invoicebatches.size());
			int AD_WindowNo = table.getPO_Window_ID();
			commitEx();
			zoom (AD_WindowNo, query);
			
	    	return "";
	    

		
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
