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

import org.compiere.model.MAllocationLine;
import org.compiere.model.MCash;
import org.compiere.model.MCashLine;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
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

public class SHW_PaymentCreateCashline  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	List<MPayment> payments = new Query(getCtx(), MPayment.Table_Name, "tendertype = 'X' and docstatus in( 'CO', 'IP') ", get_TrxName())
    		.setClient_ID()
    		.list();
    	for (MPayment pay:payments)
    	{
    		Boolean cashcomplete = false;
    		List<MCashLine> cashlines = new Query(getCtx(), MCashLine.Table_Name, "c_payment_ID=?", get_TrxName())
    			.setParameters(pay.getC_Payment_ID())
    			.list();
    		for (MCashLine cLine:cashlines)
    		{
    			if (cLine.getC_Cash().getDocStatus().equals("CO"))
    				continue;
    		}

			// Create Cash Book entry
			if ( pay.getC_CashBook_ID() <= 0 ) {
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@Mandatory@: @C_CashBook_ID@"));
				return DocAction.STATUS_Invalid;
			}
			//MCash cash = MCash.get (getCtx(), getAD_Org_ID(), getDateAcct(), getC_Currency_ID(), get_TrxName());SHW
			MCash cash = getCash(pay.getC_CashBook_ID(), pay.getDateAcct());
			if (cash == null || cash.get_ID() == 0)
			{
				return DocAction.STATUS_Invalid;
			}
			MCashLine cl = new MCashLine( cash );
			cl.setCashType(X_C_CashLine.CASHTYPE_GeneralReceipts);/*
			if (pay.isReceipt())
				cl.setCashType( X_C_CashLine.CASHTYPE_GeneralReceipts );
			else
				cl.setCashType(X_C_CashLine.CASHTYPE_GeneralExpense);
			if (pay.getC_Charge_ID() > 0)
			{
				cl.setC_Charge_ID(pay.getC_Charge_ID());
				cl.setCashType(MCashLine.CASHTYPE_Charge);
			}

			if (pay.getC_BPartner_ID() != 0 && pay.getC_Order_ID() != 0 && MPaymentAllocate.get(pay).length == 0)
			{
				cl.setCashType("A");
			}*/
			cl.setDescription("Pago #" + pay.getDocumentNo());
			cl.setC_Currency_ID( pay.getC_Currency_ID() );
			cl.setC_Payment_ID( pay.getC_Payment_ID() ); // Set Reference to payment.
			cl.set_ValueOfColumn("C_BPartner_ID", pay.getC_BPartner_ID());
			cl.set_ValueOfColumn(MPayment.COLUMNNAME_C_Project_ID, pay.getC_Project_ID());
			StringBuffer info=new StringBuffer();
			info.append("Cash journal ( ")
				.append(cash.getDocumentNo()).append(" )");	
			//	Amount
			BigDecimal amt = pay.getPayAmt();
			if (!pay.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_APPayment))
			{
				amt = amt.negate();
			}

			cl.setAmount( amt );
			//
			cl.setDiscountAmt( Env.ZERO );
			cl.setWriteOffAmt( Env.ZERO );
			cl.setIsGenerated( true );
			
			if (!cl.save(get_TrxName()))
			{
				return DocAction.STATUS_Invalid;
			}/*
			List<MAllocationLine> alos = new Query(getCtx(), MAllocationLine.Table_Name, "c_payment_ID=?", get_TrxName())
				.setParameters(pay.getC_Payment_ID())
				.list();
			for (MAllocationLine alo:alos)
			{
				alo.setC_CashLine_ID(cl.getC_CashLine_ID());
				alo.saveEx();
			}*/
		
    	}
    	return "";
	}
	private MCash getCash(int C_CashBook_ID, 
			Timestamp dateAcct)
	{
		String whereClause ="C_CashBook_ID=?"			//	#1
				+ " AND TRUNC(StatementDate, 'DD')<=?"		//	#2
				+ " AND Processed='N'";
		
		MCash retValue = new Query(getCtx(), MCash.Table_Name, whereClause, get_TrxName())
			.setParameters(C_CashBook_ID, TimeUtil.getDay(dateAcct))
			.setOrderBy("TRUNC(StatementDate, 'DD') desc")
			.first()
		;		
		if (retValue != null)
			return retValue;
		return null;
	}
	
    

}
