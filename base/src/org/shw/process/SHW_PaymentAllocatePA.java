/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2016 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/

package org.shw.process;

import java.util.List;
import java.util.Properties;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/** Generated Process for (SHW_PaymentAllocatePA)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class SHW_PaymentAllocatePA extends SHW_PaymentAllocatePAAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{

		String A_TrxName = get_TrxName();
		Properties A_Ctx = getCtx();
		int A_Record_ID = getRecord_ID();
		int A_AD_PInstance_ID = getAD_PInstance_ID();
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
                " AND T_Selection.T_Selection_ID=c_Payment.C_Payment_ID)";
		int[] c_payment_IDs = new Query(A_Ctx, MPayment.Table_Name, whereClause, A_TrxName)
                                            .setParameters(A_AD_PInstance_ID)
                                            .setClient_ID()
                                            .getIDs();
		for (int C_Payment_ID:c_payment_IDs)
		{
			MPayment payment = new MPayment(A_Ctx, C_Payment_ID, A_TrxName);
			//	Allocate to multiple Payments based on entry
			MPaymentAllocate[] pAllocs = MPaymentAllocate.get(payment);
			if (pAllocs.length == 0)
				return "";
			int count = DB.getSQLValueEx(A_TrxName, "select count(*) from c_allocationline where c_Payment_ID =?", payment.getC_Payment_ID());
			if (payment.isAllocated() || count >0)
				return "";

			MAllocationHdr alloc = new MAllocationHdr(getCtx(), false, 
					payment.getDateTrx(), payment.getC_Currency_ID(), 
					Msg.translate(A_Ctx, "C_Payment_ID")	+ ": " + payment.getDocumentNo(), 
					A_TrxName);
			alloc.setAD_Org_ID(payment.getAD_Org_ID());
			if (!alloc.save())
			{
				log.severe("P.Allocations not created");
				return "";
			}
			//	Lines
			for (MPaymentAllocate pa:pAllocs)
			{
				MAllocationLine aLine = null;
				if (payment.isReceipt())
					aLine = new MAllocationLine (alloc, pa.getAmount(), 
							pa.getDiscountAmt(), pa.getWriteOffAmt(), pa.getOverUnderAmt());
				else
					aLine = new MAllocationLine (alloc, pa.getAmount().negate(), 
							pa.getDiscountAmt().negate(), pa.getWriteOffAmt().negate(), pa.getOverUnderAmt().negate());
				//SHW
				aLine.setC_Payment_ID(payment.getC_Payment_ID());
				if (pa.getC_Invoice_ID() != 0)
				{
					aLine.setDocInfo(pa.getC_BPartner_ID(), 0, pa.getC_Invoice_ID());
					aLine.setPaymentInfo(payment.getC_Payment_ID(), 0);				
				}
				else if (pa.get_ValueAsInt("C_Charge_ID") != 0)
				{
					aLine.set_CustomColumn("C_Charge_ID", pa.get_ValueAsInt("C_Charge_ID"));
					//aLine.set_CustomColumn("ChargeAmt", chargeAmt);
					aLine.setC_BPartner_ID(pa.getC_Payment().getC_BPartner_ID());
					aLine.set_CustomColumn("C_Project_ID", pa.get_ValueAsInt("C_Project_ID"));
				}
				if (!aLine.save(get_TrxName()))
					log.warning("P.Allocations - line not saved");
				else
				{
					pa.setC_AllocationLine_ID(aLine.getC_AllocationLine_ID());
					pa.saveEx();
				}
			}
			//	Should start WF
			alloc.processIt(DocAction.ACTION_Complete);

		}
		return "";

	}
}