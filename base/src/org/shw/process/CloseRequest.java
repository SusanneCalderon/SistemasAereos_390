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

import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MRequest;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class CloseRequest  extends SvrProcess
{	
    
    @Override    
    protected void prepare()
    {    	

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	int r_status_ID = 0;
    	Boolean processed = true;
    	MOrder order = new MOrder(getCtx(), getRecord_ID(), get_TrxName());
    	if (!order.getDocStatus().equals(MOrder.DOCSTATUS_Completed))
    		return "";
       	if (order.getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder))
       	{
       		if (Env.getAD_Client_ID(getCtx())== 1000001)
       			r_status_ID = 1000023;
       		else if (Env.getAD_Client_ID(getCtx()) == 1000012)
       			r_status_ID = 1000022;
       		processed = false;
       	}

       	else if (order.getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_OnCreditOrder))
       	{
       		if (Env.getAD_Client_ID(getCtx())== 1000001)
       			r_status_ID = 1000007;
       		else if (Env.getAD_Client_ID(getCtx()) == 1000012)
       			r_status_ID = 1000013;
       	}
       	if (r_status_ID == 0)
       		return "";
    	String whereClause = "c_order_ID =? and r_requesttype_ID in (1000002,1000004) and processed = 'N'";
    	ArrayList<Object> params = new ArrayList<Object>();
    	params.add(getRecord_ID());
    	List<MRequest> reqs = new Query(getCtx(), MRequest.Table_Name, whereClause, get_TrxName())
    		.setClient_ID()
    		.setOnlyActiveRecords(true)
    		.setParameters(params.toArray())
    		.list();
    	for (MRequest req:reqs)
    	{
    		String sqlupdate = "update r_request set r_status_ID=?, processed =? where r_request_ID=?";
    		params.clear();
    		params.add(r_status_ID);
    		params.add(processed);
    		params.add(req.getR_Request_ID());
    		int no = DB.executeUpdateEx(sqlupdate, params.toArray(), get_TrxName());			
    	}
    	return "";
    }
    

}
