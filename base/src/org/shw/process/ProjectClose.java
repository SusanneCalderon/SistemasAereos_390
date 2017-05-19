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

import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.model.I_GL_DistributionLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDistribution;
import org.compiere.model.MDistributionLine;
import org.compiere.model.MDocType;
import org.compiere.model.MGLCategory;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MProject;
import org.compiere.model.MProjectLine;
import org.compiere.model.PO;
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
 * @author Susanne Calderon
 * 			<li>FR [ 2791635 ] Use saveEx whenever is possible
 * 				https://sourceforge.net/tracker/?func=detail&aid=2791635&group_id=176962&atid=879335
 */
public class ProjectClose extends SvrProcess
{
	/**	Project from Record			*/
	private int 		m_C_Project_ID = 0;
	private MProject 	project = null;
	private Boolean 	p_isshipped = false;
	private MDistribution 	distribution = null;
	private ArrayList<MOrder> ordersToPost = new ArrayList<MOrder>();
	private ArrayList<MInvoice> invoicesToPost = new ArrayList<MInvoice>();

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			if (name.equals("isshipped"))
				p_isshipped = para.getParameterAsBoolean();
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
		project = new MProject (getCtx(), m_C_Project_ID, get_TrxName());
		String result = "";
		{
			result = openInSale(project);
			if (!result.equals(""))
				return result;
			result = ProjectOpeninPurchase(project);
			if (!result.equals(""))
				return result;
		}
		if (project.getProjectCategory().equals("M"))
		{
			if (getChildren().length==0)
			return "El proyecto no tiene proyectos hijo";
			distributeProject();
		}
		
		log.info("doIt - " + project);
		if (p_isshipped)
		{
			project.set_ValueOfColumn("isshipped", p_isshipped);
			project.saveEx();
			return "";
		}
		MProjectLine[] projectLines = project.getLines();
		if (MProject.PROJECTCATEGORY_WorkOrderJob.equals(project.getProjectCategory())
			|| MProject.PROJECTCATEGORY_AssetProject.equals(project.getProjectCategory()))
		{
			/** @todo Check if we should close it */
		}

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
	

	
	private MProject[] getChildren()
	{
		String whereClause = " C_Project_Parent_ID =?";
		List<MProject> list = new Query(getCtx(), MProject.Table_Name, whereClause,get_TrxName())
			.setApplyAccessFilter(true)
			.setParameters(m_C_Project_ID)
			.list();
		return list.toArray(new MProject[list.size()]);
	}

	private String openInSale(MProject project)
	{
		List<MOrderLine> oLines = new Query(getCtx(), MOrderLine.Table_Name, "C_Project_ID=? ", get_TrxName())
			.setParameters(project.getC_Project_ID())
			.list();
		for (MOrderLine oLine:oLines)
		{
			if (!oLine.getParent().isSOTrx())
				continue;
			if (oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Voided)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Reversed))
				continue;
			if (oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Drafted)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_InProgress)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Invalid))
				return "El projecto hijo " + project.getValue() + " tiene OdV no completadas";
		}
		return "";
	}
	

	private String ProjectOpeninPurchase(MProject project)
	{
		List<MOrderLine> oLines = new Query(getCtx(), MOrderLine.Table_Name, "C_Project_ID=? ", get_TrxName())
			.setParameters(project.getC_Project_ID())
			.list();
		for (MOrderLine oLine:oLines)
		{
			if (oLine.getParent().isSOTrx())
				continue;
			if (oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Voided)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Reversed))
				continue;
			if (oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Drafted)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_InProgress)
					|| oLine.getParent().getDocStatus().equals(MOrder.DOCSTATUS_Invalid))
				return "El projecto " + project.getValue() + " tiene OdC no completadas";
		}
		return "";
	}

	
	private String distributeProject()
	{
		String result = "";
		
		for (MAcctSchema as:MAcctSchema.getClientAcctSchema(getCtx(), project.getAD_Client_ID()))
		{	

			ArrayList<Object> params = new ArrayList<Object>();
			params.add(project.getC_Project_ID());
			params.add(project.getName());
			params.add(as.getC_AcctSchema_ID());
			distribution = new Query(getCtx(), MDistribution.Table_Name, "c_Project_ID=? and name = ? and c_acctschema_ID=?", get_TrxName())
			.setParameters(params)
			.first();
			if (distribution == null)
				createDistribution(as);	
			distributeOrders(as);
			distributeInvoices(as);
		}
		for (MOrder order:ordersToPost)
		{
			Doc doc = Doc.get(MAcctSchema.getClientAcctSchema(getCtx(), project.getAD_Client_ID()), 
					MOrder.Table_ID	, order.getC_Order_ID(), get_TrxName());
			try
			{
				if (doc == null)
				{
					log.severe(getName() + ": No Doc for " + MOrder.Table_Name);
				}
				else
				{
					String error = doc.post(false, false);   //  post no force/repost
					String postStatus = doc.getPostStatus();
					result = result + " " + error + " " + postStatus;
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, getName() + ": " + MOrder.Table_Name, e);
			}
			finally
			{				
			}
		}
		for (MInvoice invoice:invoicesToPost)
		{
			Doc doc = Doc.get(MAcctSchema.getClientAcctSchema(getCtx(), project.getAD_Client_ID()), 
					MInvoice.Table_ID	, invoice.getC_Invoice_ID(), get_TrxName());
			try
			{
				if (doc == null)
				{
					log.severe(getName() + ": No Doc for " + MInvoice.Table_Name);
				}
				else
				{
					String error = doc.post(false, false);   //  post no force/repost
					String postStatus = doc.getPostStatus();
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, getName() + ": " + MOrder.Table_Name, e);
			}
			finally
			{				
			}
		}
		return result;
	}
	private void createDistribution(MAcctSchema as)
	{
		String ProjectDistribution = project.get_ValueAsString("ProjectDistribution");
		if (ProjectDistribution == null || ProjectDistribution == "")
			ProjectDistribution = "I";
		if (distribution == null)
		{
			distribution = new MDistribution(getCtx()	, 0, get_TrxName());
			distribution.setAnyProject(false);
			distribution.setC_Project_ID(project.getC_Project_ID());
			distribution.setName(project.getName());
			distribution.setC_AcctSchema_ID(as.getC_AcctSchema_ID());
			distribution.saveEx();
		}
		List<MProject> projectsOfFather = new Query(getCtx(), MProject.Table_Name, "C_Project_Parent_ID=?", get_TrxName())
		.setParameters(project.getC_Project_ID())
		.list();
    	BigDecimal weight_father = (BigDecimal)project.get_Value("Weight");
    	BigDecimal Volume_father = (BigDecimal)project.get_Value("Volume");
		for (MProject projectson: projectsOfFather)
		{
			BigDecimal weight = (BigDecimal)projectson.get_Value("Weight");
			BigDecimal volume = (BigDecimal)projectson.get_Value("volume");
			BigDecimal share = ProjectDistribution.equals("W")?
					weight.divide(weight_father, 5, BigDecimal.ROUND_HALF_DOWN)
					: volume.divide(Volume_father, 5, BigDecimal.ROUND_HALF_DOWN);
			share = share.multiply(Env.ONEHUNDRED);
			MDistributionLine dLine = new MDistributionLine(getCtx(), 0, get_TrxName());
			dLine.setGL_Distribution_ID(distribution.getGL_Distribution_ID());
			dLine.setOverwriteProject(true);
			dLine.setC_Project_ID(projectson.getC_Project_ID());
			dLine.setPercent(share);
			distribution.setPercentTotal(distribution.getPercentTotal().add(share));
			dLine.saveEx();			
		}
		distribution.saveEx();
		BigDecimal diff = Env.ONEHUNDRED.subtract(distribution.getPercentTotal());
		if (diff.compareTo(Env.ONEHUNDRED)!=0)
		{
			final String whereClause = I_GL_DistributionLine.COLUMNNAME_GL_Distribution_ID+"=?";
			MDistributionLine maxLine = new Query(getCtx(),I_GL_DistributionLine.Table_Name,whereClause,get_TrxName())
			.setParameters(distribution.getGL_Distribution_ID())
			.setOrderBy(MDistributionLine.COLUMNNAME_Percent + " desc")
			.first();
			maxLine.setPercent(maxLine.getPercent().add(diff));
			maxLine.saveEx();
		}
		distribution.validate();
	}
	
	public boolean clearAccounting(MAcctSchema accountSchema, PO model) 
	{
		final String sqlUpdate = "UPDATE " + model.get_TableName() + " SET Posted = 'N' WHERE "+ model.get_TableName() + "_ID=?";
		int no = DB.executeUpdate(sqlUpdate, new Object[] {model.get_ID()}, false , model.get_TrxName());
		//Delete account
		final String sqldelete = "DELETE FROM Fact_Acct WHERE Record_ID =? AND AD_Table_ID=?";		
		no = DB.executeUpdate (sqldelete ,new Object[] { model.get_ID(),
				model.get_Table_ID() }, false , model.get_TrxName());
		return true;
	}
	
	private String createJournal(MAcctSchema as ,MOrderLine oLine)
	{

		MJournal	journal = new MJournal(getCtx(), 0, get_TrxName());
		for (MJournalLine jl:journal.getLines(true))
		{
		}
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
			journal.setDescription(oLine.getParent().getDocumentInfo() + " " + oLine.getLine());
			journal.saveEx();
			//NeuBuchung mit Projekt
			MJournalLine jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(oLine.getM_Product_ID());
			jLine.setC_Project_ID(oLine.getC_Project_ID());
			jLine.setC_BPartner_ID(oLine.getParent().getC_BPartner_ID());
			jLine.setC_Activity_ID(oLine.getParent().getC_Activity_ID());
			jLine.setC_Campaign_ID(oLine.getParent().getC_Campaign_ID());
			ProductCost pc = new ProductCost (getCtx(),oLine.getM_Product_ID()	, 0,get_TrxName());		
			MAccount expense = pc.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
			jLine.setAccount_ID(expense.getAccount_ID());
			jLine.setAmtSourceCr(oLine.getLineNetAmt());
			jLine.saveEx();
			//Rueckbuchung
			for (MDistributionLine dLine:distribution.getLines(true))
			{				
			jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(oLine.getM_Product_ID());
			
			jLine.setC_BPartner_ID(oLine.getParent().getC_BPartner_ID());
			jLine.setC_Activity_ID(oLine.getParent().getC_Activity_ID());
			jLine.setC_Campaign_ID(oLine.getParent().getC_Campaign_ID());			
			jLine.setAccount_ID(expense.getAccount_ID());
			jLine.setAmtSourceDr(dLine.getPercent().divide(Env.ONEHUNDRED).multiply(oLine.getLineNetAmt()));
			jLine.saveEx();

			}
		//journal.processIt(MJournal.DOCACTION_Complete);
		//journal.setDocAction(MJournal.DOCACTION_Close);
		journal.saveEx();
		return "";
	}
	
	private String createJournal(MAcctSchema as ,MInvoiceLine oLine)
	{

		MJournal	journal = new MJournal(getCtx(), 0, get_TrxName());
		for (MJournalLine jl:journal.getLines(true))
		{
		}
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
			journal.setDescription(oLine.getParent().getDocumentInfo() + " " + oLine.getLine());
			journal.saveEx();
			//NeuBuchung mit Projekt
			MJournalLine jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(oLine.getM_Product_ID());
			jLine.setC_Project_ID(oLine.getC_Project_ID());
			jLine.setC_BPartner_ID(oLine.getParent().getC_BPartner_ID());
			jLine.setC_Activity_ID(oLine.getParent().getC_Activity_ID());
			jLine.setC_Campaign_ID(oLine.getParent().getC_Campaign_ID());
			ProductCost pc = new ProductCost (getCtx(),oLine.getM_Product_ID()	, 0,get_TrxName());		
			MAccount expense = pc.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
			jLine.setAccount_ID(expense.getAccount_ID());
			jLine.setAmtSourceCr(oLine.getLineNetAmt());
			jLine.saveEx();
			//Rueckbuchung
			for (MDistributionLine dLine:distribution.getLines(true))
			{				
			jLine = new MJournalLine(journal);
			jLine.setM_Product_ID(oLine.getM_Product_ID());
			
			jLine.setC_BPartner_ID(oLine.getParent().getC_BPartner_ID());
			jLine.setC_Activity_ID(oLine.getParent().getC_Activity_ID());
			jLine.setC_Campaign_ID(oLine.getParent().getC_Campaign_ID());			
			jLine.setAccount_ID(expense.getAccount_ID());
			jLine.setAmtSourceDr(dLine.getPercent().divide(Env.ONEHUNDRED).multiply(oLine.getLineNetAmt()));
			jLine.saveEx();

			}
		//journal.processIt(MJournal.DOCACTION_Complete);
		//journal.setDocAction(MJournal.DOCACTION_Close);
		journal.saveEx();
		return "";
	}
	
	private String distributeOrders(MAcctSchema as)
	{
		
		String whereClause = "C_Project_ID=?";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(project.getC_Project_ID());
		List<MOrderLine> oLines = new Query(getCtx(), MOrderLine.Table_Name, whereClause, get_TrxName())
		.setParameters(params)
		.setOrderBy("C_Order_ID ")
		.list();
		if (oLines == null)
			return"";
		for (MOrderLine oLine:oLines)
		{
			if ((oLine.getC_Order().getDocStatus().equals(MOrder.DOCSTATUS_Drafted)
					|| oLine.getC_Order().getDocStatus().equals(MOrder.DOCSTATUS_InProgress)
					|| oLine.getC_Order().getDocStatus().equals(MOrder.DOCSTATUS_Invalid)))
				continue;
			if (MPeriod.isOpen(getCtx(), oLine.getC_Order().getDateAcct()
					, oLine.getC_Order().getC_DocType().getDocBaseType(), oLine.getAD_Org_ID()))
			{

				Boolean isadded = false;
				for (MOrder order:ordersToPost)
				{
					if (order.getC_Order_ID() ==  oLine.getC_Order_ID())
					{
						isadded = true;
						break;
					}
				}
				if (!isadded)
					ordersToPost.add(oLine.getParent());
			}
			else
			{
				createJournal(as, oLine);						
			}
		}
		for (MOrder order:ordersToPost)
		{
			clearAccounting(as, order);
		}
		return "";
	}
	

	private String distributeInvoices(MAcctSchema as)
	{
		String whereClause = "C_Project_ID=?";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(project.getC_Project_ID());
		List<MInvoiceLine> oLines = new Query(getCtx(), MInvoiceLine.Table_Name, whereClause, get_TrxName())
		.setParameters(params)
		.setOrderBy("C_Invoice_ID ")
		.list();
		if (oLines == null)
			return"";
		for (MInvoiceLine oLine:oLines)
		{
			if ((oLine.getC_Invoice().getDocStatus().equals(MOrder.DOCSTATUS_Drafted)
					|| oLine.getC_Invoice().getDocStatus().equals(MOrder.DOCSTATUS_InProgress)
					|| oLine.getC_Invoice().getDocStatus().equals(MOrder.DOCSTATUS_Invalid)))
				continue;
			if (MPeriod.isOpen(getCtx(), oLine.getC_Invoice().getDateAcct()
					, oLine.getC_Invoice().getC_DocType().getDocBaseType(), oLine.getAD_Org_ID()))
			{

				Boolean isadded = false;
				for (MInvoice invoice:invoicesToPost)
				{
					if (invoice.getC_Invoice_ID() ==  oLine.getC_Invoice_ID())
					{
						isadded = true;
						break;
					}
				}
				if (!isadded)
					invoicesToPost.add(oLine.getParent());
			}
			else
			{
				createJournal(as, oLine);						
			}
			for (MInvoice invoice:invoicesToPost)
			{
				clearAccounting(as, invoice);
			}
		}
		return "";
	
	}
	
	


		}	//	ProjectClose
