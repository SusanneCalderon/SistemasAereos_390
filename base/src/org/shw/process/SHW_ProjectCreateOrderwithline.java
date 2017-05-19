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
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MRequest;
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

public class SHW_ProjectCreateOrderwithline  extends SvrProcess
{
	private int 	P_C_BPartner_ID = 0;
	private int 	P_User1_ID = 0;
	private int 	P_M_Product_ID=0;
	private int 	P_R_RequestType_ID=0;
	private int 	P_SalesRep_ID = 0;
	private int 	P_C_Tax_ID=0;
    
    @Override    
    protected void prepare()
    {  
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MOrder.COLUMNNAME_C_BPartner_ID))
				P_C_BPartner_ID = para.getParameterAsInt();
			else if (name.equals(MOrder.COLUMNNAME_User1_ID))
				P_User1_ID = para.getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				P_M_Product_ID = para.getParameterAsInt();
			else if (name.equals("R_RequestType_ID"))
				P_R_RequestType_ID = para.getParameterAsInt();
			else if (name.equals("SalesRep_ID"))
				P_SalesRep_ID = para.getParameterAsInt();
			else if (name.equals("C_Tax_ID"))
				P_C_Tax_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);  
		}
    }
    
    
    
    @Override
    protected String doIt() throws Exception
    {
    	Properties A_Ctx = getCtx();
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = get_TrxName();

    	String Docbasetype = "POO";
    	MProject project = new MProject(A_Ctx, A_Record_ID, A_TrxName);
    	String whereClause = "c_project_ID=?";
    	List<MProject> projects = new Query(getCtx(), MProject.Table_Name, whereClause, get_TrxName())
    	.setParameters(project.getC_Project_ID())
    	.list();
    	for (MProject detailproject:projects)
    	{
    		MOrder order = new MOrder(A_Ctx, 0, A_TrxName);
    		order.setAD_Org_ID(detailproject.getAD_Org_ID());
    		order.setC_Campaign_ID(detailproject.getC_Campaign_ID());
    		//order.
    		order.setC_Project_ID(detailproject.getC_Project_ID());
    		order.setDescription(detailproject.getName());
    		Timestamp ts = detailproject.getDateContract();
    		if (ts != null)
    			order.setDateOrdered (ts);
    		ts = project.getDateFinish();
    		if (ts != null)
    			order.setDatePromised (ts);
    		//
    		MBPartner bpartner = null;
    			order.setC_BPartner_ID(P_C_BPartner_ID);
    			bpartner = new MBPartner(A_Ctx, P_C_BPartner_ID, A_TrxName);
    			MUser[] contacts = bpartner.getContacts(true);
    			if (contacts.length>0)
    				order.setAD_User_ID(contacts[0].getAD_User_ID());
    			order.setSalesRep_ID(Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"));
    			int c_DocType_ID = 0;
    			c_DocType_ID = bpartner.get_ValueAsInt("C_DoctypePO_ID")<=0?  MDocType.getDocType(Docbasetype):
    					bpartner.get_ValueAsInt("C_DoctypePO_ID");
    			order.setC_DocTypeTarget_ID(c_DocType_ID);
    			order.setIsSOTrx(false);
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
    		if (P_User1_ID ==0)
    			order.setUser1_ID(detailproject.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));
    		else 
    			order.setUser1_ID(P_User1_ID);
    		order.setC_Activity_ID(detailproject.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
    		order.setDescription(detailproject.getDescription());
    		order.set_ValueOfColumn("LG_Route_ID", detailproject.get_ValueAsInt("LG_Route_ID"));

    		order.set_ValueOfColumn("DocumentoDeTransporte", detailproject.get_Value("DocumentoDeTransporte"));
    		order.set_ValueOfColumn("CodigoDeDeclaracion", detailproject.get_Value("CodigoDeDeclaracion"));
    		order.set_ValueOfColumn("ReferenciaDeDeclaracion", detailproject.get_Value("ReferenciaDeDeclaracion"));
    		order.set_ValueOfColumn("Provider", detailproject.get_Value("Provider"));
    		order.set_ValueOfColumn("ProviderPO", detailproject.get_Value("ProviderPO"));
    		order.saveEx();
    		createOrderLine(order);
    		if (P_R_RequestType_ID != 0)
    		{
    			if (processOrder(order))
    				createRequest(order);
    				
    		}
    	}
    	return  "";
    }

    private boolean createOrderLine(MOrder order)
    {
    	MOrderLine oLine = new MOrderLine(order);
    	oLine.setM_Product_ID(P_M_Product_ID);
    	oLine.setQty(Env.ONE);
    	MProduct prod = new MProduct(getCtx(),P_M_Product_ID	, get_TrxName());
    	oLine.setC_UOM_ID(prod.getC_UOM_ID());
    	oLine.setC_Tax_ID(P_C_Tax_ID);    	
    	
    	
    	oLine.setHeaderInfo(order);
    	oLine.setPrice();
    	oLine.saveEx();/*
    	
    		String sql = "SELECT plv.M_PriceList_Version_ID "
    				+ "FROM M_PriceList_Version plv "
    				+ "WHERE plv.M_PriceList_ID=? "						//	1
    				+ " AND plv.ValidFrom <= ? "
    				+ "ORDER BY plv.ValidFrom DESC";
    		//	Use newest price list - may not be future
    		MProductPricing pp = new MProductPricing (P_M_Product_ID	, order.getC_BPartner_ID()	, Env.ONE, false);
    		pp.setM_PriceList_ID(order.getM_PriceList_ID());
    	int M_PriceList_Version_ID = DB.getSQLValueEx(null, sql, order.getM_PriceList_ID()	, order.getDateOrdered());
    	pp.setM_PriceList_Version_ID(M_PriceList_Version_ID); 
    	pp.setPriceDate(order.getDateOrdered());
    	//		
    	oLine.setPrice();*/

    	return true;
    }
    
    private boolean processOrder(MOrder order)
    {
    	if (!order.processIt("CO"))
    	{
    		log.warning("completeOrder - failed: " + order);
    		addLog("completeInvoice - failed: " + order); // Elaine 2008/11/25
    		return false;
    	}
    	return true;
    }
    

    private boolean createRequest(MOrder order)
    {
    	MRequest req = new MRequest(getCtx(), P_SalesRep_ID, P_R_RequestType_ID, "Generado por ", false, get_TrxName());
    	req.setC_Project_ID(order.getC_Project_ID());
    	req.setC_Activity_ID(order.getC_Activity_ID());
    	req.setRequestAmt(order.getGrandTotal());
    	req.setC_Order_ID(order.getC_Order_ID());
    	req.setR_RequestType_ID(P_R_RequestType_ID);
    	req.saveEx();
    	return true;
    }
    
    
    private String script ()
    {
    	Properties A_Ctx = getCtx();
    	int A_Record_ID = getRecord_ID();
    	String A_TrxName = get_TrxName();

    	String Docbasetype = "POO";
    	MProject project = new MProject(A_Ctx, A_Record_ID, A_TrxName);
    	MOrder order = new MOrder(A_Ctx, 0, A_TrxName);
    	order.setAD_Org_ID(project.getAD_Org_ID());
    	order.setC_Campaign_ID(project.getC_Campaign_ID());
    	//order.
    	order.setC_Project_ID(project.getC_Project_ID());
    	order.setDescription(project.getName());
    	order.setDateOrdered (Env.getContextAsDate(A_Ctx, "#Date"));
    	order.setDatePromised (Env.getContextAsDate(A_Ctx, "#Date"));
    	MBPartner bpartner = null;
    	order.setC_BPartner_ID(P_C_BPartner_ID);
    	bpartner = new MBPartner(A_Ctx, P_C_BPartner_ID, A_TrxName);
    	MUser[] contacts = bpartner.getContacts(true);
    	if (contacts.length>0)
    		order.setAD_User_ID(contacts[0].getAD_User_ID());
    	order.setSalesRep_ID(Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"));
    	int c_DocType_ID = 0;
    	c_DocType_ID = bpartner.get_ValueAsInt("C_DoctypePO_ID")<=0?  MDocType.getDocType(Docbasetype):
    		bpartner.get_ValueAsInt("C_DoctypePO_ID");
    	order.setC_DocTypeTarget_ID(c_DocType_ID);
    	order.setIsSOTrx(false);
    	order.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
    	order.setM_Warehouse_ID(project.getM_Warehouse_ID());
    	if (order.isSOTrx()){
    		order.setM_PriceList_ID(bpartner.getM_PriceList_ID());
    		order.setC_PaymentTerm_ID(bpartner.getC_PaymentTerm_ID());
    	}
    	else {        	
    		order.setM_PriceList_ID(bpartner.getPO_PriceList_ID());
    		order.setC_PaymentTerm_ID(bpartner.getPO_PaymentTerm_ID());
    	}
    	if (P_User1_ID ==0)
    		order.setUser1_ID(project.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));
    	else 
    		order.setUser1_ID(P_User1_ID);
    	order.setC_Activity_ID(project.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
    	order.setDescription(project.getDescription());
    	order.set_ValueOfColumn("LG_Route_ID", project.get_ValueAsInt("LG_Route_ID"));

    	order.set_ValueOfColumn("DocumentoDeTransporte", project.get_Value("DocumentoDeTransporte"));
    	order.set_ValueOfColumn("CodigoDeDeclaracion", project.get_Value("CodigoDeDeclaracion"));
    	order.set_ValueOfColumn("ReferenciaDeDeclaracion", project.get_Value("ReferenciaDeDeclaracion"));
    	order.set_ValueOfColumn("Provider", project.get_Value("Provider"));
    	order.set_ValueOfColumn("ProviderPO", project.get_Value("ProviderPO"));
    	order.saveEx();
    	//createOrderLine(order);
    	MOrderLine oLine = new MOrderLine(order);
    	oLine.setM_Product_ID(P_M_Product_ID);
    	oLine.setQty(Env.ONE);
    	MProduct prod = new MProduct(A_Ctx,P_M_Product_ID	, A_TrxName);
    	oLine.setC_UOM_ID(prod.getC_UOM_ID());
    	oLine.setC_Tax_ID(P_C_Tax_ID);    	    
    	oLine.setHeaderInfo(order);
    	oLine.setPrice();
    	oLine.saveEx();
    	//Create Request
    	if (P_R_RequestType_ID != 0)
    	{
    		if (!order.processIt("CO"))
    		{
    			log.warning("completeOrder - failed: " + order);
    			addLog("completeInvoice - failed: " + order); // Elaine 2008/11/25
    			return "No es posible completar la orden";
    		}
    		order.setDocStatus("CO");
    		order.saveEx();

    		MRequest req = new MRequest(A_Ctx, P_SalesRep_ID, P_R_RequestType_ID, "Generado por ", false, A_TrxName);
    		req.setC_Project_ID(order.getC_Project_ID());
    		req.setC_Activity_ID(order.getC_Activity_ID());
    		req.setRequestAmt(order.getGrandTotal());
    		req.setC_Order_ID(order.getC_Order_ID());
    		req.setR_RequestType_ID(P_R_RequestType_ID);
    		req.saveEx();

    	}
    	return  "";
    }
    
    
    
    
}
