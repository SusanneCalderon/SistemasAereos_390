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

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.model.MGLCategory;
import org.compiere.model.MInvoice;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrder;
import org.compiere.model.MProject;
import org.compiere.model.MProjectLine;
import org.compiere.model.MRequest;
import org.compiere.model.ProductCost;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
 
/**
 *  Close Project.
 *
 *	@author Jorg Janke
 *	@version $Id: ProjectClose.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 *
 * @author Teo Sarca, wwww.arhipac.ro
 * 			<li>FR [ 2791635 ] Use saveEx whenever is possible
 * 				https://sourceforge.net/tracker/?func=detail&aid=2791635&group_id=176962&atid=879335
 */
public class SHW_ProjectClose extends SvrProcess
{
	/**	Project from Record			*/
	private int 		m_C_Project_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		m_C_Project_ID = getRecord_ID();
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (translated text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		MProject project = new MProject (getCtx(), m_C_Project_ID, get_TrxName());
		log.info("doIt - " + project);
		
		String result = "";
		result = closeProjectTramite();
		if (result != "")
			return result;

		MProjectLine[] projectLines = project.getLines();
		if (MProject.PROJECTCATEGORY_WorkOrderJob.equals(project.getProjectCategory())
			|| MProject.PROJECTCATEGORY_AssetProject.equals(project.getProjectCategory()))
		{
			/** @todo Check if we should close it */
		}
		if ("M".equals(project.getProjectCategory()))
			distributeProjectCost(project);
			
		//	Close lines
		for (int line = 0; line < projectLines.length; line++)
		{
			projectLines[line].setProcessed(true);
			projectLines[line].saveEx();
		}

		project.setProcessed(true);
		project.saveEx();

		return "";
	}	//	doIt
	
	private String closeProjectTramite()
	{
		Boolean isTerminated = false;
		BigDecimal recordCount = Env.ZERO;
		String result = "";
		String whereClause = " C_Project_ID=? and Docstatus in ('DR','IP') and issotrx = ?";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getRecord_ID());
		params.add(true);
		recordCount = new Query(getCtx(), MOrder.Table_Name, whereClause, get_TrxName())
			.setParameters(params.toArray())
			.aggregate("*", Query.AGGREGATE_COUNT);
		if (recordCount.compareTo(Env.ZERO) != 0)
			result = result + " Hay ordenes de venta que no estan completadas";
		params.clear();

		params.add(getRecord_ID());
		params.add(false);

		recordCount = new Query(getCtx(), MOrder.Table_Name, whereClause, get_TrxName())
			.setParameters(params.toArray())
			.aggregate("*", Query.AGGREGATE_COUNT);
		if (recordCount.compareTo(Env.ZERO) != 0)
			result = result + " Hay ordenes de compra que no estan completadas";
		
		whereClause = " c_project_ID = ? and r_status_ID in "
				+ " (select r_status_ID from r_status rs where isclosed = 'N' and rs.r_status_ID = r.r_status_ID) ";
		

		recordCount = new Query(getCtx(), MRequest.Table_Name, whereClause, get_TrxName())
			.setParameters(getRecord_ID())
			.aggregate("*", Query.AGGREGATE_COUNT);
		if (recordCount.compareTo(Env.ZERO) != 0)
			result = result + " Hay solicitudes pendientes.";
		return result;
	}

	private String distributeProjectCost(MProject project)
	{
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("c_project_ID in ");
		whereClause.append("select c_project_ID from c_project where c_projectparent_ID =?");
		BigDecimal projectValue = new Query(getCtx(), MInvoice.Table_Name, whereClause.toString(), get_TrxName())
			.setOnlyActiveRecords(true)
			.setParameters(m_C_Project_ID)
			.aggregate(MInvoice.COLUMNNAME_TotalLines, Query.AGGREGATE_SUM);
		List<MInvoice> purchaseInvoices = new Query(getCtx(), MInvoice.Table_Name, "c_project_ID=?", get_TrxName())
			.setOnlyActiveRecords(true)
			.setParameters(m_C_Project_ID)
			.list();
		MAcctSchema[] accts = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
		for (MInvoice purchaseInvoice:purchaseInvoices)
		{
			for (MAcctSchema as:accts)
			{
				MJournal journal = createJournal(project, as);
				
			}
			
		}
			/*//NeuBuchung mit Projekt
			MJournalLine jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(ivl.getM_Product_ID());
			jLine.setC_Project_ID(ivl.getC_Project_ID());
			jLine.setC_BPartner_ID(ivl.getC_Invoice().getC_BPartner_ID());
			jLine.setC_Activity_ID(ivl.getC_Invoice().getC_Activity_ID());
			jLine.setC_Campaign_ID(ivl.getC_Invoice().getC_Campaign_ID());
			ProductCost pc = new ProductCost (ivl.getCtx(),ivl.getM_Product_ID()	, 0,ivl.get_TrxName());		
			MAccount revenue = pc.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
			jLine.setAccount_ID(revenue.getAccount_ID());
			jLine.setAmtSourceCr(calcIncome);
			jLine.set_ValueOfColumn("C_InvoiceLine_ID", ivl.getC_InvoiceLine_ID());
			jLine.saveEx();
			//Rueckbuchung

			jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(pprl.getLG_ProductPriceRate().getM_Product_ID());
			jLine.setC_BPartner_ID(ivl.getC_Invoice().getC_BPartner_ID());
			jLine.setC_Activity_ID(ivl.getC_Invoice().getC_Activity_ID());
			jLine.setC_Campaign_ID(ivl.getC_Invoice().getC_Campaign_ID());
			pc = new ProductCost (ivl.getCtx(),pprl.getM_Product_ID()	, 0,ivl.get_TrxName());			
			jLine.setAccount_ID(pc.getAccount(ProductCost.ACCTTYPE_P_Revenue, as).getAccount_ID());
			jLine.setAmtSourceDr(calcIncome);
			jLine.set_ValueOfColumn("C_InvoiceLine_ID", ivl.getC_InvoiceLine_ID());
			jLine.saveEx();
		
		journal.processIt(MJournal.DOCACTION_Complete);
		journal.setDocAction(MJournal.DOCACTION_Close);
		journal.saveEx();*/
		
		return "";
	}
	
	private MJournal createJournal(MProject project , MAcctSchema as)
	{
		MJournal	journal = new MJournal(getCtx(), 0, get_TrxName());
			int glcat_ID = MGLCategory.getDefault(getCtx(), MGLCategory.CATEGORYTYPE_Manual).getGL_Category_ID();
			int c_doctype_ID = new Query(getCtx(), MDocType.Table_Name	, "gl_category_ID=?", get_TrxName())
				.setParameters(glcat_ID)
				.setOnlyActiveRecords(true)
				.firstId();
			journal.setC_Currency_ID(100);
			journal.setC_AcctSchema_ID(as.get_ID());
			journal.setC_ConversionType_ID(114);
			journal.setDescription("");
			journal.setGL_Category_ID(glcat_ID);
			journal.setC_DocType_ID(c_doctype_ID);
			journal.setPostingType("A");
			journal.setDocumentNo(DB.getDocumentNo(getAD_Client_ID(), MJournal.Table_Name, get_TrxName()));
			journal.setDescription(project.getName());
			journal.saveEx();
		return journal;
	}

}	//	ProjectClose
