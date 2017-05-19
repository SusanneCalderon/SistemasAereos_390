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

import org.compiere.model.MCashBook;
import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProject;
import org.compiere.model.MUOMConversion;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.report.MReportLine;
import org.compiere.report.MReportSource;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class UpdateProjectResult  extends SvrProcess
{	
    

	protected List<MProject> m_records = null;
	private Boolean  p_OnlyReportUpdate = false;
    @Override    
    protected void prepare()
    {    	

		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("OnlyReportUpdate"))
				p_OnlyReportUpdate = para.getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	int record_ID = getRecord_ID();
    	
    	if (record_ID != 0)
    	{
    		MProject project = new MProject(getCtx(), record_ID, get_TrxName());
    		m_records = new ArrayList<MProject>();
    		m_records.add(project);
    	}
    	else
    	{
    		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
    				" AND T_Selection.T_Selection_ID=C_Project.C_Project_ID)";
    		m_records = new Query(getCtx(), MProject.Table_Name, whereClause, get_TrxName())
    											.setParameters(getAD_PInstance_ID())
    											.setClient_ID()
    											.list();    		
    	}
		
		for (MProject project:m_records)
		{
			if (p_OnlyReportUpdate)
				updateReportSource(project);
			else
			{
				test2(project);
				project.updateProject();
			}
		}
    	return "";
    }
    
    private BigDecimal SHW_CostActual(MProject project)
    {
    	String expresion = "LineNetAmtRealInvoiceLine(c_invoiceline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_invoice_ID in (select c_invoice_ID from c_invoice where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'N')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MInvoiceLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.getC_Project_ID())
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }

    private BigDecimal SHW_CostPlanned(MProject project)
    {
    	String expresion = "linenetamt - taxAmtReal(c_Orderline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_order_ID in (select c_order_ID from c_order where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'N')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MOrderLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.getC_Project_ID())
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }

    private BigDecimal SHW_CostPlanned_Heritage(MProject project)
    {
    	BigDecimal result = Env.ZERO;
    	String expresion = "linenetamt - taxAmtReal(c_Orderline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_order_ID in (select c_order_ID from c_order where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'N')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal costAll = Env.ZERO;
    	costAll = new Query(getCtx(), MOrderLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.get_ValueAsInt("C_Project_Parent_ID"))
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	BigDecimal revenueAll = SHW_RevenuePlanned_Sons(project.get_ValueAsInt("C_Project_Parent_ID"));
    	if (revenueAll.longValue()==0)
    		return Env.ZERO;
    	BigDecimal revenueThis = SHW_RevenuePlanned(project);
    	BigDecimal share = revenueThis.divide(revenueAll,5, BigDecimal.ROUND_HALF_DOWN);
    	result = costAll.multiply(share);
    	return result;
    }
    

    private BigDecimal SHW_CostActual_Heritage(MProject project)
    {
    	BigDecimal result = Env.ZERO;
    	String expresion = "LineNetAmtRealInvoiceLine(c_invoiceline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_invoice_ID in (select c_invoice_ID from c_invoice where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'N')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal costAll = Env.ZERO;
    	costAll = new Query(getCtx(), MInvoiceLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.get_ValueAsInt("C_Project_Parent_ID"))
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	BigDecimal revenueAll = SHW_RevenueActual_Sons(project.get_ValueAsInt("C_Project_Parent_ID"));
    	BigDecimal revenueThis = SHW_RevenueActual(project);
    	if (revenueAll.longValue()==0)
    		return Env.ZERO;
    	BigDecimal share = revenueThis.divide(revenueAll,5, BigDecimal.ROUND_HALF_DOWN);
    	result = costAll.multiply(share);
    	return result;
    }
    
    

    private BigDecimal SHW_RevenueActual(MProject project)
    {
    	String expresion = "LineNetAmtRealInvoiceLine(c_invoiceline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_invoice_ID in (select c_invoice_ID from c_invoice where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'Y')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MInvoiceLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.getC_Project_ID())
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }

    private BigDecimal SHW_RevenueActual_Sons(int c_project_parent_ID)
    {
    	String expresion = "LineNetAmtRealInvoiceLine(c_invoiceline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_invoice_ID in (select c_invoice_ID from c_invoice where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'Y')");
    	whereClause.append( " and c_project_ID in (select c_project_ID from c_project where c_project_parent_ID =?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MInvoiceLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(c_project_parent_ID)
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }

    private BigDecimal SHW_RevenuePlanned(MProject project)
    {
    	String expresion = "linenetamt - taxAmtReal(c_Orderline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_order_ID in (select c_order_ID from c_order where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'Y')");
    	whereClause.append( " and c_project_ID in (?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MOrderLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(project.getC_Project_ID())
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }
    

    private BigDecimal SHW_RevenuePlanned_Sons(int c_Project_Parent_ID)
    {
    	String expresion = "linenetamt - taxAmtReal(c_Orderline_ID)";
    	StringBuffer whereClause = new StringBuffer();
    	whereClause.append("c_order_ID in (select c_order_ID from c_order where docstatus in ('CO','CL','IP') ");
    	whereClause.append(" AND issotrx = 'Y')");
    	whereClause.append( " and c_project_ID in (select c_project_ID from c_project where c_project_parent_ID =?)");
    	BigDecimal result = Env.ZERO;
    	result = new Query(getCtx(), MOrderLine.Table_Name, whereClause.toString(), get_TrxName())
    		.setParameters(c_Project_Parent_ID)
    		.aggregate(expresion, Query.AGGREGATE_SUM);
    	return result;
    }
    
    private String updateReportSource(MProject pr)
    {
    	pr.updateWeightVolume();
    	pr.createDistribution();
    	//commit();
    	
    	return "";
    }
    
private String test2(PO A_PO)
	
	{	
		MProject project = (MProject)A_PO;

		if (!(project.getProjectCategory().equals("M")
				|| project.getProjectCategory().equals("M")))
			return "";
		
		MProject parent = new MProject(project.getCtx(), A_PO.get_ValueAsInt("C_Project_ID"), A_PO.get_TrxName());
		List<MProject> sons = new Query(A_PO.getCtx(), MProject.Table_Name, "C_Project_Parent_ID=?", A_PO.get_TrxName())
		.setParameters(A_PO.get_ValueAsInt("C_Project_ID"))
		.list();		
		BigDecimal totalWeight = Env.ZERO;
		BigDecimal totalVolume = Env.ZERO;
		for (MProject son:sons)
		{
			BigDecimal rateWeight = MUOMConversion.convert(son.get_ValueAsInt("C_UOM_ID"), 
					parent.get_ValueAsInt("C_UOM_ID"), (BigDecimal)son.get_Value("WeightEntered"), true);

			BigDecimal rateVolume = MUOMConversion.convert(son.get_ValueAsInt("C_UOM_Volume_ID"), 
					parent.get_ValueAsInt("C_UOM_Volume_ID"), (BigDecimal)son.get_Value("VolumeEntered"), true);
			son.set_ValueOfColumn("Weight", rateWeight);
			son.set_ValueOfColumn("Volume", rateVolume);
			totalWeight = totalWeight.add(rateWeight);
			totalVolume = totalVolume.add(rateVolume);
			son.saveEx();
		}
		parent.set_ValueOfColumn("Weight", totalWeight);
		parent.set_ValueOfColumn("Volume", totalVolume);
		parent.saveEx();
		return "";		
	}

    
    

}
