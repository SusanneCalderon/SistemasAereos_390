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
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MProcess;
import org.compiere.model.MRequest;
import org.compiere.model.MTaxCategory;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class PaymentCTAJ_AfterPrepare extends SvrProcess
{	
	private MPayment pay = null;

	@Override    
	protected void prepare()
	{    	

	}


	@Override
	protected String doIt() throws Exception
	{
		int AD_Process_ID = 1000062;
		MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
		if (!instance.save())
		{
			return null;
		}
		//call process
		ProcessInfo pi = new ProcessInfo ("Create Orderline", AD_Process_ID);
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());

		instance.setRecord_ID(getRecord_ID());
		pi.setIsBatch(true);
		MProcess worker = new MProcess(getCtx(),AD_Process_ID,get_TrxName());
		worker.processIt(pi, Trx.get(get_TrxName(), true));
		return"";
	}



}
