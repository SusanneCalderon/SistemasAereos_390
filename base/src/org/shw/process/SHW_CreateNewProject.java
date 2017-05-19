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
import java.util.logging.Level;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBPartner;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_CreateNewProject  extends SvrProcess
{	
	
	//private int P_C_Project_ID = 0;
	private int P_C_BPartner_ID = 0;
    
    @Override    
    protected void prepare()
    {    	
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_BPartner_ID"))
				P_C_BPartner_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = get_TrxName();
    	Properties A_Ctx = getCtx();
    	MProject template = new MProject(A_Ctx, A_Record_ID, A_TrxName);
    	MProject projectNew = new MProject(A_Ctx, 0, A_TrxName);
    	MProject.copyValues(template, projectNew);    	
    	if (P_C_BPartner_ID != 0)
    		projectNew.setC_BPartner_ID(P_C_BPartner_ID);
    	projectNew.setDateContract(Env.getContextAsDate(A_Ctx, "#Date"));
    	projectNew.saveEx();
    	List<MOrder> ordersFrom = new Query(A_Ctx, MOrder.Table_Name, "c_project_ID =?", A_TrxName)
    	.setParameters(A_Record_ID)
    	.list();
    	for (MOrder orderFrom:ordersFrom)
    	{
    		
    		MOrder orderNeu = MOrder.copyFrom(orderFrom, Env.getContextAsDate(A_Ctx, "#Date"),
    				orderFrom.getC_DocTypeTarget_ID(), orderFrom.isSOTrx(), false, false, A_TrxName);
    		if (orderFrom.isSOTrx())
    		{
    			orderNeu.setC_BPartner_ID(P_C_BPartner_ID);
    			MBPartner bpartner = new MBPartner(A_Ctx, P_C_BPartner_ID, A_TrxName);
    			int c_DocType_ID = bpartner.get_ValueAsInt("C_DocType_ID")<=0?  orderFrom.getC_DocTypeTarget_ID():
    				bpartner.get_ValueAsInt("C_DocType_ID");
    			orderNeu.setC_DocTypeTarget_ID(c_DocType_ID);
    		}
    		orderNeu.setC_Project_ID(projectNew.getC_Project_ID());
    		for (MOrderLine oLine:orderNeu.getLines())
    		{
    			oLine.setC_Project_ID(projectNew.getC_Project_ID());
    			oLine.saveEx();
    		}
    		orderNeu.saveEx();
    	}
		String result = "Proyecto. No " + projectNew.getValue() + " " + projectNew.getName();
		template.set_ValueOfColumn("help", result);
		template.saveEx();
    	String whereClauseWindow = "c_project_ID =" + projectNew.getC_Project_ID();
		MQuery query = new MQuery("");
		MTable table = new MTable(getCtx(), MProject.Table_ID, get_TrxName());
		query.addRestriction(whereClauseWindow);
		query.setRecordCount(1);
		int AD_WindowNo = 0;
			AD_WindowNo = table.getAD_Window_ID();

		int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
		MSession session = new MSession(getCtx(), ad_session_ID, null);
		
		if (session.getWebSession() == null ||session.getWebSession().length() == 0)
		{
			commitEx();
			zoom (AD_WindowNo, query);
			return result;
			
		}
		else
		{
			
		}

		return result;
		
    	
    }
    /*protected String doIt() throws Exception
    {
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = get_TrxName();
    	Properties A_Ctx = getCtx();
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
