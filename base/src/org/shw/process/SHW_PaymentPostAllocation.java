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
import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MRequest;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_PaymentPostAllocation  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	

    }
    
      
    @Override
    protected String doIt() throws Exception
    {

		MPayment pay = new MPayment(getCtx(), getRecord_ID(), get_TrxName());
		if (!pay.isPosted())
			return "";
		Boolean ok = false;
		List<MAllocationLine> alos = new Query(pay.getCtx(), MAllocationLine.Table_Name, "c_payment_ID=?", pay.get_TrxName())
		.setParameters(pay.getC_Payment_ID())
		.list();
		MAcctSchema[] m_ass = MAcctSchema.getClientAcctSchema(pay.getCtx(), getAD_Client_ID());

		for (MAllocationLine alo:alos)
		{
			if (alo.getC_AllocationHdr().isPosted())
				continue;
			String innerTrxName = Trx.createTrxName("CAP");
			Trx innerTrx = Trx.get(innerTrxName, true);
			String postStatus = Doc.STATUS_NotPosted; 
			Doc doc = Doc.get (m_ass, 735, alo.getC_AllocationHdr_ID(), innerTrxName);
			try
			{
				if (doc == null)
				{
					log.severe(pay.getDocumentNo() + ": No Doc for " + pay.get_TableName());
					ok = false;
				}
				else
				{
					String error = doc.post(false, false);   //  post no force/repost
					ok = (error == null);
					postStatus = doc.getPostStatus();
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, pay.getDocumentNo() + ": " + pay.get_TableName(), e);
				ok = false;
			}
			finally
			{
				if (ok)
					innerTrx.commit();
				else {
					innerTrx.rollback();
					// save the posted status error (out of trx)
					StringBuffer sqlupd = new StringBuffer("UPDATE ")
					.append(doc.get_TableName()).append(" SET Posted='").append(postStatus)
					.append("',Processing='N' ")
					.append("WHERE ")
					.append(doc.get_TableName()).append("_ID=").append(doc.get_ID());
					DB.executeUpdateEx(sqlupd.toString(), null);
				}
				innerTrx.close();
				innerTrx = null;
			}

		}
		return "";
	}
    

}
