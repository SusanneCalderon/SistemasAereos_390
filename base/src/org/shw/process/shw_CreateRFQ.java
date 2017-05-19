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

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MDocType;
import org.compiere.model.MOpportunity;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MRfQ;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class shw_CreateRFQ  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	MRequest req = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
    	MOpportunity opp = new MOpportunity(getCtx(), req.get_ValueAsInt(MOpportunity.COLUMNNAME_C_Opportunity_ID), get_TrxName());
    	MRfQ rfq = new MRfQ(getCtx(), 0, get_TrxName());
    	rfq.setSalesRep_ID(opp.getSalesRep_ID());
    	rfq.setDateResponse(Env.getContextAsDate(getCtx(), "@#Date@"));
    	rfq.setC_RfQ_Topic_ID(1000002);
    	rfq.setQuoteType("S");
    	rfq.setName(opp.getC_BPartner().getName() + " " + opp.getSalesRep().getName());
    	rfq.setC_Currency_ID(100);
    	rfq.saveEx();
		String whereClauseWindow  = "c_rfq_ID=" + rfq.getC_RfQ_ID();
		MQuery query = new MQuery("");
		MTable table = new MTable(getCtx(), MRfQ.Table_ID, get_TrxName());
		query.addRestriction(whereClauseWindow);
		query.setRecordCount(1);
		int AD_WindowNo = table.getAD_Window_ID();

		int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
		MSession session = new MSession(getCtx(), ad_session_ID, null);
		
		if (session.getWebSession() == null ||session.getWebSession().length() == 0)
		{
			commitEx();
			zoom (AD_WindowNo, query);
			return "";
			
		}
    	return rfq.getName();
    }
    

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
