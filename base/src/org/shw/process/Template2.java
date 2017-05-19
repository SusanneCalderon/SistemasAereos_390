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
import java.util.Properties;

import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.eevolution.model.MHRMovement;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class Template2  extends SvrProcess
{	

	private int P_R_Request_ID = 0;
	private int  P_C_Charge_ID = 0;
	private int P_C_BPartner_ID = 0;
	private String alias = "";
	
			
    
    @Override    
    protected void prepare()
    {/*    	
    	ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MRequest.COLUMNNAME_R_Request_ID))
				P_R_Request_ID = para.getParameterAsInt();
			else if (name.equals(MCharge.COLUMNNAME_C_Charge_ID))
				P_C_Charge_ID = para.getParameterAsInt();
				
			else
				;
		}

    */}
    
      
    @Override
    protected String doIt() throws Exception
    {
    	Properties A_Ctx  = getCtx();
    	String A_TrxName = get_TrxName();
    	int 		A_AD_PInstance_ID = getAD_PInstance_ID();
    	int C_Charge_ID = 0;
    	List<MHRMovement> m_records = null;
    	String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=HR_Movement.HR_Movement_ID)";
		m_records = new Query(A_Ctx, MHRMovement.Table_Name, whereClause, A_TrxName)
											.setParameters(A_AD_PInstance_ID)
											.setClient_ID()
											.list();
		BigDecimal totalToPay = Env.ZERO;
		String description = "";
		for (MHRMovement move:m_records)
		{
			totalToPay = totalToPay.add(move.getAmount());
			description = description.concat(", " + move.getC_BPartner().getName());
		}
		MRequest req = new MRequest(A_Ctx, P_R_Request_ID, A_TrxName);
		req.setRequestAmt(totalToPay);
		req.setSummary(description);
		req.set_ValueOfColumn("C_Charge_ID", P_C_Charge_ID);
		req.setC_BPartner_ID(P_C_BPartner_ID);
		req.saveEx();
    	return "";
    }
    /*protected String doIt() throws Exception
    {
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = A_TrxName;
    	Properties A_Ctx = A_Ctx;
    	List<MOrder> ordersFrom = new Query(A_Ctx, MOrder.Table_Name, "c_project_ID =?", A_TrxName)
    	.setParameters(P_C_Project_ID)
    	.list();
    	for (MOrder orderFrom:ordersFrom)
    	{
    		MOrder orderNeu = MOrder.copyFrom(orderFrom, Env.getContextAsDate(A_Ctx, "#Date"),
    				orderFrom.getC_DocTypeTarget_ID(), orderFrom.isSOTrx(), false, false, A_TrxName);

    		orderNeu.setC_Project_ID(A_Record_ID);
    		for (MOrderLine oLine:orderNeu.getLines())
    		{
    			oLine.setC_Project_ID(A_Record_ID);
    			oLine.saveEx();
    		}
    		orderNeu.saveEx();
    	}
    	return "";
    }*/
    
    protected void zoom (int AD_Window_ID, MQuery zoomQuery)
	{/*
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
	*/}	//	zoom
    

}
