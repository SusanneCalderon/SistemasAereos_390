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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MQuery;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_InvoiceAllocationCreate  extends SvrProcess
{	

	private int P_C_Payment_ID = 0;
	protected LinkedHashMap<Integer, LinkedHashMap<String, Object>> m_values = null;
	private String alias = "";
	
			
    
    @Override    
    protected void prepare()
    {    	
    	ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MPayment.COLUMNNAME_C_Payment_ID))
				P_C_Payment_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	Properties A_Ctx  = getCtx();
    	String A_TrxName = get_TrxName();
    	List<MInvoice> m_records = null;
    	String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=C_Invoice.C_Invoice_ID)";
		m_records = new Query(A_Ctx, MInvoice.Table_Name, whereClause, A_TrxName)
											.setParameters(getAD_PInstance_ID())
											.setClient_ID()
											.list();
		MAllocationHdr alloc = null;
		MPayment pay = new MPayment(A_Ctx, P_C_Payment_ID, A_TrxName);
		BigDecimal allocatedAmt = pay.getAllocatedAmt()==null?Env.ZERO:pay.getAllocatedAmt();
		BigDecimal totalToAllocate = pay.getPayAmt().subtract(allocatedAmt);
		for (MInvoice invoice:m_records)
		{
			if (alloc == null)
			{
				alloc = new MAllocationHdr (Env.getCtx(), true,	//	manual
						Env.getContextAsDate(Env.getCtx(), "#Date"), invoice.getC_Currency_ID(), 
						Env.getContext(Env.getCtx(), "#AD_User_Name"), A_TrxName);
					alloc.setAD_Org_ID(invoice.getAD_Org_ID());
					alloc.saveEx();
			}
			//LinkedHashMap<String, Object> values = m_values.get(invoice.get_ID());
			BigDecimal payAmt = invoice.getOpenAmt();/*
			for (Entry<String, Object> entry : values.entrySet()) {
				String columnName = entry.getKey();
				
					if (columnName.equals(alias + "_PayAmt"))
					{
						payAmt = (BigDecimal)entry.getValue();						
					}
				}*/
			int C_Invoice_ID = invoice.getC_Invoice_ID();
			BigDecimal AppliedAmt = payAmt;
			BigDecimal notAllcatedAmt = pay.getAllocatedAmt();
			if (totalToAllocate.compareTo(payAmt) >= 0)
				AppliedAmt = payAmt;
			else
				AppliedAmt = totalToAllocate;
			//  semi-fixed fields (reset after first invoice)
			BigDecimal DiscountAmt = Env.ZERO;
			BigDecimal WriteOffAmt = Env.ZERO;
			//	OverUnderAmt needs to be in Allocation Currency
			BigDecimal OverUnderAmt = invoice.getOpenAmt().subtract(AppliedAmt);
			MAllocationLine aLine = new MAllocationLine (alloc, AppliedAmt, 
					DiscountAmt, WriteOffAmt, OverUnderAmt);
			aLine.setDocInfo(invoice.getC_BPartner_ID(), pay.getC_Order_ID(), C_Invoice_ID);
			aLine.setPaymentInfo(pay.getC_Payment_ID(), 0);
			aLine.saveEx();

			//  Invoice variables
			String sql = "SELECT invoiceOpen(C_Invoice_ID, 0) "
					+ "FROM C_Invoice WHERE C_Invoice_ID=?";
			BigDecimal open = DB.getSQLValueBD(A_TrxName, sql, C_Invoice_ID);
			if (open != null && open.signum() == 0)	{
				sql = "UPDATE C_Invoice SET IsPaid='Y' "
						+ "WHERE C_Invoice_ID=" + C_Invoice_ID;
				int no = DB.executeUpdate(sql, A_TrxName);
			} else
				log.config("Invoice #" + invoice.getDocumentNo() + " is not paid - " + open);

    		if (pay.testAllocation())
    			pay.saveEx();
		}
    	return alloc.getDocumentNo();
    }
    
    
    /*protected String doIt() throws Exception
    {
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = A_TrxName;
    	Properties A_Ctx = A_Ctx;
    	List<MOrder> ordersFrom = new Query(A_Ctx, MOrder.Table_Name, "c_project_ID =?", A_TrxName)
    	.setParameters(P_C_Project_ID)
    	.list();
    	for (MOrder orderFrom:ordersFrom)
    	{
    		MOrder orderNeu = MOrder.copyFrom(orderFrom, Env.getContextAsDate(A_Ctx, "#Date"),
    				orderFrom.getC_DocTypeTarget_ID(), orderFrom.isSOTrx(), false, false, A_TrxName);

    		orderNeu.setC_Project_ID(A_Record_ID);
    		for (MOrderLine oLine:orderNeu.getLines())
    		{
    			oLine.setC_Project_ID(A_Record_ID);
    			oLine.saveEx();
    		}
    		orderNeu.saveEx();
    	}
    	return "";
    }*/
    
    protected void zoom (int AD_Window_ID, MQuery zoomQuery)
	{/*
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
	*/}	//	zoom
    

}
