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

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MProject;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_OrderLineChangeOrder  extends SvrProcess
{
    int P_C_Order_ID = 0;
    
    @Override    
    protected void prepare()
    {    	
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MOrder.COLUMNNAME_C_Order_ID))
				P_C_Order_ID = para.getParameterAsInt();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
    }
    
    
    
    @Override
    protected String doIt() throws Exception
    {	
    	Properties A_Ctx = getCtx();
    	String A_TrxName = get_TrxName();
    	int A_AD_PInstance_ID = getAD_PInstance_ID();
    	
    	
    	List<MOrderLine> m_records = null;
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=c_orderline.c_orderline_ID)";
		m_records = new Query(A_Ctx, MOrderLine.Table_Name, whereClause, A_TrxName)
											.setParameters(A_AD_PInstance_ID)
											.setClient_ID()
											.list();
		for (MOrderLine oLine:m_records)
		{
			MOrder newOrder = new MOrder(A_Ctx, P_C_Order_ID, A_TrxName);
			if (newOrder.getC_BPartner_ID() != oLine.getC_Order().getC_BPartner_ID())
				return "La orden escogida tiene otro SdN";
			MOrderLine newoLine = new MOrderLine(A_Ctx, 0, A_TrxName);
			MOrderLine.copyValues(oLine, newoLine);
			newoLine.setC_Order_ID(P_C_Order_ID);
			newoLine.saveEx();
			oLine.setQtyEntered(Env.ZERO);
			oLine.setQtyReserved(Env.ZERO);
			oLine.setQtyOrdered(Env.ZERO);
			oLine.deleteEx(true);	

            if (newOrder.getDocStatus().equals(MOrder.DOCSTATUS_InProgress))
            		newOrder.processIt(MOrder.DOCACTION_Prepare);
            if (oLine.getC_Order().getDocStatus().equals(MOrder.DOCSTATUS_InProgress))
            {
            	MOrder orderOld = (MOrder)oLine.getC_Order();
            	orderOld.processIt(MOrder.DOCACTION_Prepare);
            }
		}
    	return "";
    }
    
    
    
    
}
