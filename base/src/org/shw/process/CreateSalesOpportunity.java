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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MOpportunity;
import org.compiere.model.MPayment;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
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

public class CreateSalesOpportunity  extends SvrProcess
{	

	Timestamp p_CloseDate = null;
			
    
    @Override    
    protected void prepare()
    {    	
    	ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("CloseDate"))
				p_CloseDate = para.getParameterAsTimestamp();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	MRequest req = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
    	MOpportunity opp = new MOpportunity(getCtx(), 0, get_TrxName());
    	opp.setAD_Org_ID(req.getAD_Org_ID());
    	opp.setC_BPartner_ID(req.getC_BPartner_ID());
    	opp.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
    	opp.setProbability(Env.ONEHUNDRED);
    	opp.setC_SalesStage_ID(1000000);
    	opp.set_ValueOfColumn("R_Request_ID", getRecord_ID());
    	opp.setExpectedCloseDate(p_CloseDate);
    	opp.setOpportunityAmt(Env.ZERO);
    	opp.setC_Currency_ID(100);
    	opp.saveEx();
    	req.set_ValueOfColumn("C_Opportunity_ID", opp.getC_Opportunity_ID());
    	req.saveEx();
    	return "";
    }
    
    protected void zoom (int AD_Window_ID, MQuery zoomQuery)
	{}	//	zoom
    

}
