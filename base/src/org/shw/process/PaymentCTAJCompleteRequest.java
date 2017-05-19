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
import java.util.List;

import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MRequest;
import org.compiere.model.MTaxCategory;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class PaymentCTAJCompleteRequest  extends SvrProcess
{	
	private MPayment pay = null;

	@Override    
	protected void prepare()
	{    	

	}


	@Override
	protected String doIt() throws Exception
	{
		pay = new MPayment(getCtx(), getRecord_ID(), get_TrxName());
		List<MRequest> reqs = new Query(getCtx(), MRequest.Table_Name, "c_payment_ID=?", get_TrxName())
		.setParameters(pay.getC_Payment_ID())
		.list();
		if (reqs == null || reqs.isEmpty())
			return "";

		for (MRequest req:reqs)
		{
			if (req.getC_Order_ID() <=0)
				return "";
			if (req.getR_Status().isClosed())
				return "";
			if (pay.getC_Charge_ID()!= 0)
				CreateCTAJPayment(req);
			else
				CreateCTAJPaymentAllocs(req);
		}
		int r_status = 0;
		if (Env.getAD_Client_ID(Env.getCtx()) == 1000001)
			r_status = 1000001;
		else if (Env.getAD_Client_ID(Env.getCtx()) == 1000012)
			r_status = 1000011;
		if (r_status == 0)
			return "";
		for (MRequest req:reqs)
		{
			req.setR_Status_ID(r_status);
			req.setSalesRep_ID(req.getCreatedBy());
			req.saveEx();
		}
		return "";
	}
	
	private void CreateInvoicePayment(MRequest req)
	{
		MOrder order = (MOrder)req.getC_Order();
		BigDecimal p_DistributionAmt = pay.getPayAmt();
		MOrderLine oLine = new MOrderLine(order);
		MInvoice invoice = (MInvoice)pay.getC_Invoice();
		for (MInvoiceLine ivl:invoice.getLines())
		{
			if (ivl.getC_Charge_ID()<=0)
				continue;
			MTaxCategory tc = (MTaxCategory)ivl.getC_Charge().getC_TaxCategory();
			oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());
			oLine.setQty(Env.ONE);
			oLine.setPrice(p_DistributionAmt);
			oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
			oLine.setC_Project_ID(pay.getC_Project_ID());
			MBPartner bpartner = (MBPartner)oLine.getC_Order().getC_BPartner();
			oLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
			oLine.saveEx();			
		}
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + order.getDocumentNo() );
		pay.saveEx();
	}
	

	private void CreateCTAJPayment(MRequest req)
	{
		MOrder order = (MOrder)req.getC_Order();
		BigDecimal p_DistributionAmt = pay.getPayAmt();
		MOrderLine oLine = new Query(getCtx(), MOrderLine.Table_Name, "c_order_ID=? and c_payment_ID=?", get_TrxName())
			.setParameters(order.getC_Order_ID(), pay.getC_Payment_ID())
			.first();
		if (oLine != null)
			return ;
		oLine = new MOrderLine(order);
		int chargeID;
		chargeID = pay.getC_Charge_ID();
		oLine.setC_Charge_ID(chargeID);
		MCharge charge = new MCharge(getCtx(), chargeID, get_TrxName());
		MTaxCategory tc = (MTaxCategory)charge.getC_TaxCategory();
		oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());

		oLine.setQty(Env.ONE);
		oLine.setPrice(p_DistributionAmt);
		oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
		oLine.setC_Project_ID(pay.getC_Project_ID());
		MBPartner bpartner = (MBPartner)oLine.getC_Order().getC_BPartner();
		oLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
		oLine.saveEx();
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + order.getDocumentNo() );
		pay.set_ValueOfColumn("ControlAmt", p_DistributionAmt);
		pay.saveEx();

	}	

	private void CreateCTAJPaymentAllocs(MRequest req)
	{
		MOrder order = (MOrder)req.getC_Order();
		BigDecimal p_DistributionAmt = pay.getPayAmt();
		int chargeID;
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(pay);
		for (MPaymentAllocate alloc:pAllocs)
		{
			MOrderLine oLine = new Query(getCtx(), MOrderLine.Table_Name, "c_order_ID=? and c_payment_ID=?", get_TrxName())
			.setParameters(order.getC_Order_ID(), pay.getC_Payment_ID())
			.first();
			if (oLine != null)
				return ;
			oLine = new MOrderLine(order);
			chargeID = alloc.get_ValueAsInt("C_Charge_ID");
			oLine.setC_Charge_ID(chargeID);
			MCharge charge = new MCharge(getCtx(), chargeID, get_TrxName());
			MTaxCategory tc = (MTaxCategory)charge.getC_TaxCategory();
			oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());
			oLine.setQty(Env.ONE);
			oLine.setPrice(p_DistributionAmt);
			oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
			oLine.setC_Project_ID(pay.getC_Project_ID());
			MBPartner bpartner = (MBPartner)oLine.getC_Order().getC_BPartner();
			oLine.set_ValueOfColumn("isSplitInvoice", bpartner.get_ValueAsBoolean("isSplitInvoice"));
			oLine.saveEx();
		}
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + order.getDocumentNo() );
		pay.set_ValueOfColumn("ControlAmt", p_DistributionAmt);
		pay.saveEx();

	}




}
