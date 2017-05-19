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

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_ProjectCreateOrdenAjena  extends SvrProcess
{
	private Boolean P_IsSOTrx = false;
	private int 	P_C_BPartner_ID = 0;
	private int 	P_C_DocType_ID = 0;
    
    @Override    
    protected void prepare()
    {  
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MInvoice.COLUMNNAME_IsSOTrx))
				P_IsSOTrx = para.getParameterAsBoolean();
			else if (name.equals(MOrder.COLUMNNAME_C_BPartner_ID))
				P_C_BPartner_ID = para.getParameterAsInt();
			else if (name.equals(MOrder.COLUMNNAME_C_DocType_ID))
				P_C_DocType_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);  
		}
    }
    
    
    
    @Override
    protected String doIt() throws Exception
    {
    	String result = "";
		MQuery query = new MQuery("");
		MTable table = null;
		int AD_WindowNo  = 0;
    	Properties A_Ctx = getCtx();
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = get_TrxName();
    	
    	String Docbasetype = P_IsSOTrx?"SOO":"POO";
        MProject project = new MProject(A_Ctx, A_Record_ID, A_TrxName);
        String whereClause = "";
        if (project.isSummary() && P_IsSOTrx){
        	whereClause = "c_project_parent_ID=?";
        }
        else
        	whereClause = "c_project_ID=?";
        List<MProject> projects = new Query(getCtx(), MProject.Table_Name, whereClause, get_TrxName())
        	.setParameters(project.getC_Project_ID())
        	.list();
        for (MProject detailproject:projects)
        {


        	MOrder order = new MOrder(A_Ctx, 0, A_TrxName);
        	order.setAD_Org_ID(detailproject.getAD_Org_ID());
        	order.setC_Campaign_ID(detailproject.getC_Campaign_ID());
        	//order.
        	Timestamp ts = detailproject.getDateContract();
        	if (ts != null)
        		order.setDateOrdered (ts);
        	ts = project.getDateFinish();
        	if (ts != null)
        		order.setDatePromised (ts);
        	//
        	MBPartner bpartner = null;
        	{
        		order.setC_BPartner_ID(P_C_BPartner_ID);
        		bpartner = new MBPartner(A_Ctx, P_C_BPartner_ID, A_TrxName);
        		MUser[] contacts = bpartner.getContacts(true);
        		if (contacts.length>0)
        			order.setAD_User_ID(contacts[0].getAD_User_ID());
        		order.setSalesRep_ID(Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"));
        		order.setC_DocTypeTarget_ID(P_C_DocType_ID);
        		order.setIsSOTrx(false);
        	}
        	order.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
        	//
        	order.setM_Warehouse_ID(project.getM_Warehouse_ID());
        	if (order.isSOTrx()){
        		order.setM_PriceList_ID(bpartner.getM_PriceList_ID());
        		order.setC_PaymentTerm_ID(bpartner.getC_PaymentTerm_ID());
        	}
        	else {        	
        		order.setM_PriceList_ID(bpartner.getPO_PriceList_ID());
        		order.setC_PaymentTerm_ID(bpartner.getPO_PaymentTerm_ID());
        	}

        	//
        	//order.setC_DocTypeTarget_ID(MDocType.DOCSUBTYPESO_Proposal);
        	order.setUser1_ID(detailproject.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));

        	order.saveEx();
        	result = result + order.getDocumentInfo();
        	String whereClauseWindow = "c_order_ID = " +  order.getC_Order_ID();
        	
        	table = new MTable(getCtx(), MOrder.Table_ID, get_TrxName());
        	query.addRestriction(whereClauseWindow);
        	query.setRecordCount(1);
        	AD_WindowNo = table.getPO_Window_ID();	


        	int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
        	MSession session = new MSession(getCtx(), ad_session_ID, null);

        	if (session.getWebSession() == null ||session.getWebSession().length() == 0)
        	{
        		commitEx();
        		zoom (AD_WindowNo, query);
        		return "";			
        	}		
        }        
        return  "Orden ajena " + result;
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
