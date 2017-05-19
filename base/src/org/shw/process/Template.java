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
import java.util.List;
import java.util.Properties;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
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

public class Template  extends SvrProcess
{	

			
	Timestamp P_DateAcct = null;
	int P_C_Charge_ID = 0;
    @Override    
    protected void prepare()
    {    	
	    	ProcessInfoParameter[] parameters = getParameter();
			for (ProcessInfoParameter para : parameters) {
				String name = para.getParameterName();
				if (para.getParameter() == null)
					;
				else if (name.equals(MInvoice.COLUMNNAME_DateAcct))
					P_DateAcct = para.getParameterAsTimestamp();
				else if (name.equals(MInvoice.COLUMNNAME_C_Charge_ID))
						P_C_Charge_ID = para.getParameterAsInt();
					
				else
					;
			 }
	    
    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	//import java.util.List;
    	//import org.compiere.model.MOrderLine;
    	//import org.compiere.model.MOrder;
    	//import org.compiere.model.Query;
    	//import org.compiere.util.Env;

    			String A_TrxName = get_TrxName();
    			Properties A_Ctx = getCtx();
    			int A_Record_ID = getRecord_ID();
    			int A_AD_PInstance_ID = getAD_PInstance_ID();
    	        List<MInvoice> m_records = null;
    	        String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
    	                " AND T_Selection.T_Selection_ID=c_Invoice.C_Invoice_ID)";
    	        m_records = new Query(A_Ctx, MInvoice.Table_Name, whereClause, A_TrxName)
    	                                            .setParameters(A_AD_PInstance_ID)
    	                                            .setClient_ID()
    	                                            .list();
    	       /* if (A_Record_ID > 0) 
    	        {
    	            MOrderLine line = new MOrderLine(A_Ctx, A_Record_ID, A_TrxName);
    	            m_records.add(line);
    	        }*/
    	        MAllocationHdr allocation = null;
    	        for (MInvoice invoice:m_records)
    	        {
    	    		//	Nothing to do
    	        	BigDecimal openAmt = invoice.getOpenAmt();
    	    		if (openAmt == null || openAmt.signum() == 0)
    	    			continue;
    	    		//
    	    		
    	    		//	Invoice
    	    		if (!invoice.isSOTrx())
    	    			openAmt = openAmt.negate();
    	    		
    	    		//	Allocation
    	    		if (allocation == null)
    	    		{
    	    			allocation = new MAllocationHdr (A_Ctx, true, 
    	    				P_DateAcct	, invoice.getC_Currency_ID(), "WriteOff", A_TrxName);
    	    			allocation.setAD_Org_ID(invoice.getAD_Org_ID());
    	    			if (!allocation.save())
    	    			{
    	    				return "Cannot Create allocation";
    	    			}
    	    		}
    	    		//	Payment
    	    		    	    		//	Line
    	    		MAllocationLine allocationLine = null;
    	    		allocationLine = new MAllocationLine (allocation, Env.ZERO,
    	    				Env.ZERO, openAmt, Env.ZERO);
    	    		allocationLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
    	    		allocationLine.setC_Charge_ID(P_C_Charge_ID);
    	    		allocationLine.saveEx();
    	    		if (allocation.processIt(MInvoice.DOCACTION_Complete))
    	    			allocation.saveEx();
    	    		
    	    	}
    	        return "";
	}
    
    protected String doItPayment() throws Exception
    {
    	//import java.util.List;
    	//import org.compiere.model.MOrderLine;
    	//import org.compiere.model.MOrder;
    	//import org.compiere.model.Query;
    	//import org.compiere.util.Env;

    			String A_TrxName = get_TrxName();
    			Properties A_Ctx = getCtx();
    			int A_Record_ID = getRecord_ID();
    			int A_AD_PInstance_ID = getAD_PInstance_ID();
    	        List<MPayment> m_records = null;
    	        String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
    	                " AND T_Selection.T_Selection_ID=c_Payment.C_Payment_ID)";
    	        m_records = new Query(A_Ctx, MPayment.Table_Name, whereClause, A_TrxName)
    	                                            .setParameters(A_AD_PInstance_ID)
    	                                            .setClient_ID()
    	                                            .list();
    	       /* if (A_Record_ID > 0) 
    	        {
    	            MOrderLine line = new MOrderLine(A_Ctx, A_Record_ID, A_TrxName);
    	            m_records.add(line);
    	        }*/
    	        MAllocationHdr allocation = null;
    	        for (MPayment payment:m_records)
    	        {
    	    		//	Nothing to do
    	        	BigDecimal openAmt = payment.getPayAmt().subtract(payment.getAllocatedAmt());
    	    		if (openAmt == null || openAmt.signum() == 0)
    	    			continue;
    	    		//
    	    		
    	    		//	Invoice
    	    		if (!payment.isReceipt())
    	    			openAmt = openAmt.negate();
    	    		
    	    		//	Allocation
    	    		if (allocation == null)
    	    		{
    	    			allocation = new MAllocationHdr (A_Ctx, true, 
    	    				P_DateAcct	, payment.getC_Currency_ID(), "WriteOff", A_TrxName);
    	    			allocation.setAD_Org_ID(payment.getAD_Org_ID());
    	    			if (!allocation.save())
    	    			{
    	    				return "Cannot Create allocation";
    	    			}
    	    		}
    	    		//	Payment
    	    		    	    		//	Line
    	    		MAllocationLine allocationLine = null;
    	    		allocationLine = new MAllocationLine (allocation, Env.ZERO,
    	    				Env.ZERO, openAmt, Env.ZERO);
    	    		allocationLine.setC_Payment_ID(payment.getC_Payment_ID());
    	    		allocationLine.setC_Charge_ID(P_C_Charge_ID);
    	    		allocationLine.saveEx();
    	    		if (allocation.processIt(MInvoice.DOCACTION_Complete))
    	    			allocation.saveEx();
    	    		
    	    	}
    	        return "";
	}
    

}
