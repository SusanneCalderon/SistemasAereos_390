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
import java.util.ArrayList;

import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MProject;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class InvoiceUpdateProject  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
		MInvoice invoice = new MInvoice(getCtx(), getRecord_ID(), get_TrxName());
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(invoice.getC_Invoice_ID());
		String updatesql = "update c_order o set isinvoiced = 'Y' " + 
				" where docstatus in ('CO', 'CL') " + 
				" and c_order_ID in " +
				" (select c_order_ID from adempiere.c_orderline ol where c_orderline_ID " +
				" in (select c_orderline_ID from c_invoiceline where qtyinvoiced = qtyordered and c_invoice_ID=?))";	
		int no = DB.executeUpdateEx(updatesql, params.toArray(), get_TrxName());
		for (MInvoiceLine ivl:invoice.getLines())
		{
			MProject project = (MProject)ivl.getC_Project();
			BigDecimal rate = Env.ONE;
			if (project.getC_Currency_ID() != invoice.getC_Currency_ID())
				rate = MConversionRate.getRate(invoice.getC_Currency_ID(), project.getC_Currency_ID(), invoice.getDateInvoiced(), 
					104, invoice.getAD_Client_ID(), 0);
			if (invoice.isSOTrx())
			{
				//project.setInvoicedAmt(project.getInvoicedAmt().add(invoice.getGrandTotal()));
				BigDecimal openAmt = (BigDecimal)project.get_Value("OpenAmt");
				project.set_ValueOfColumn("OpenAmt", openAmt.add(rate.multiply(invoice.getGrandTotal())));

				BigDecimal lineNetAmt = (BigDecimal)project.get_Value("LineNetAmt");
				project.set_ValueOfColumn("LineNetAmt", lineNetAmt.add(rate.multiply(ivl.getLineNetAmt())));
				if (invoice.getC_Project_ID() <=0)
					project.setInvoicedAmt(project.getInvoicedAmt().add(ivl.getLineTotalAmt()));
			}
			else
			{
				BigDecimal Cost = (BigDecimal)project.get_Value("Cost");
				project.set_ValueOfColumn("Cost", Cost.add(ivl.getLineNetAmt()));
			}
			BigDecimal Cost = (BigDecimal)(project.get_Value("Cost"));
			BigDecimal lineNetAmt = (BigDecimal)project.get_Value("LineNetAmt");
			BigDecimal distributedAmt = (BigDecimal)(project.get_Value("distributedAmt"));
			project.setProjectBalanceAmt(lineNetAmt.subtract(Cost).subtract(distributedAmt));
			project.saveEx();
		}

		return "";
	}
    

}
