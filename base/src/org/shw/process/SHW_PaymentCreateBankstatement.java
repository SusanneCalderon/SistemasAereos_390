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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.MAllocationLine;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MCash;
import org.compiere.model.MCashLine;
import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MProject;
import org.compiere.model.Query;
import org.compiere.model.X_C_CashLine;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_PaymentCreateBankstatement  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	
    	
    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	List<MPayment> payments = new Query(getCtx(), MPayment.Table_Name, "tendertype <> 'X' and docstatus in( 'CO') ", get_TrxName())
    		.setClient_ID()
    		.setOrderBy("datetrx, created")
    		.list();
    	for (MPayment pay:payments)
    	{
    	    if (pay.getC_BankAccount_ID() <= 0)
    	    	return"";
    	    
    	    MBankStatement bs = new MBankStatement((MBankAccount)pay.getC_BankAccount(), false);
    		String StringDate = new SimpleDateFormat("yyyy-mm-dd").format(pay.getDateTrx()); 
    	    bs.setStatementDate(pay.getDateTrx());
    	    bs.setName(pay.getC_BankAccount().getC_Bank().getName() + " " + pay.getC_BankAccount().getAccountNo() + StringDate);
    	    bs.saveEx();
    	    MBankStatementLine bsl = new MBankStatementLine(bs, 10);
    	    bsl.setPayment(pay);
    	    bsl.saveEx();
    	    if (bs.processIt("CO"))
    	    {
    	    	bs.setDocStatus("CO");
    	    	
    	    }
    	    bs.processIt("CO");		
    	}
    	return "";
	}
	
    

}
