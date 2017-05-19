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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCash;
import org.compiere.model.MCashBook;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MQuery;
import org.compiere.model.MRequestType;
import org.compiere.model.MSession;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.TimeUtil;

/**
 *	Generate Invoices
 *	
 *  @author SHW
 *  @version $Id: SHW_InvoiceGenerate.java,v 1.2 2015/01/24 00:51:01 mc Exp $
 */
public class SHW_NdCGenerate extends SvrProcess
{
	/**	Date Invoiced			*/
	
	/**	The current Invoice	*/

	protected List<MPayment> m_records = null;
	protected List<MInvoice> m_invoices = null;
	private int p_c_doctype_ID = 0;
	private Timestamp p_DateInvoiced = null;
	private int p_C_CashBook_ID=0;
	private Boolean p_IsCreatePayment = false;
	private String result = "Fact. No";
	private String whereClauseWindow = "";
	
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
			else if (name.equals(MInvoice.COLUMNNAME_C_DocType_ID))
				p_c_doctype_ID = para.getParameterAsInt();
			else if (name.equals(MCashBook.COLUMNNAME_C_CashBook_ID))
				p_C_CashBook_ID = para.getParameterAsInt();
			else if (name.equals("IsCreatePayment"))
				p_IsCreatePayment = para.getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		//	Login Date
		if (p_DateInvoiced == null)
			p_DateInvoiced = Env.getContextAsDate(getCtx(), "#Date");
		if (p_DateInvoiced == null)
			p_DateInvoiced = new Timestamp(System.currentTimeMillis());
	}	//	prepare

	/**
	 * 	Generate Invoices
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{

		MQuery query = new MQuery("");
		MTable table = null;
		int AD_WindowNo  = 0;
		String error = "";
		
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=c_payment.c_payment_ID)";
		m_records = new Query(getCtx(), MPayment.Table_Name, whereClause, get_TrxName())
											.setParameters(getAD_PInstance_ID())
											.setClient_ID()
											.list();
		if (p_IsCreatePayment)
		{
			error = generatePayment();
			if (!error.equals(""))
				return error;
			table = new MTable(getCtx(), MPayment.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(1);
			AD_WindowNo = table.getAD_Window_ID();	
		}
		else if (p_C_CashBook_ID >0 && p_c_doctype_ID ==0)
		{
			error = generateCashLine();
			if (!error.equals(""))
				return error;
			table = new MTable(getCtx(), MPayment.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(1);
			AD_WindowNo = table.getAD_Window_ID();	
		}
		else if (p_C_CashBook_ID == 0 && p_c_doctype_ID  >0)
		{
			m_invoices = new ArrayList<MInvoice>();
			error = generate();
			if (!error.equals(""))
				return error;

			whereClauseWindow = "c_invoice_ID in (";
			for (MInvoice inv:m_invoices)
			{
				whereClauseWindow = whereClauseWindow + inv.getC_Invoice_ID() + ",";
			}
			whereClauseWindow = whereClauseWindow.substring(0, whereClauseWindow.length() -1) + ")";
			table = new MTable(getCtx(), MInvoice.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(m_invoices.size());
			AD_WindowNo = table.getPO_Window_ID();	
			MDocType dt = new MDocType(getCtx(), p_c_doctype_ID, get_TrxName());
			if (dt.getDocBaseType().equals("ARI"))
				AD_WindowNo = table.getAD_Window_ID();
			for (MInvoice inv:m_invoices)
			{
				Env.setContext(getCtx(), "@WhereClause@", whereClause);
				result = result + ", " + inv.getDocumentInfo();
			}		
		}
		
			
		int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
		MSession session = new MSession(getCtx(), ad_session_ID, null);
		
		if (session.getWebSession() == null ||session.getWebSession().length() == 0)
		{
			commitEx();
			zoom (AD_WindowNo, query);
			return "";			
		}		
		return result;
	}	//	doIt
	
	
	/**
	 * 	Generate Shipments
	 * 	@param pstmt order query 
	 *	@return info
	 */
	private String generate () throws Exception
	{
		BigDecimal diff = Env.ZERO;
		for (MPayment pay:m_records)
		try
		{
			if (MPaymentAllocate.get(pay).length != 0)
			{
				for (MPaymentAllocate alloc:MPaymentAllocate.get(pay))
				{
					BigDecimal amount = alloc.getAmount();
					BigDecimal controlAmt = (BigDecimal)alloc.get_Value("ControlAmt");
					diff = amount.subtract(controlAmt);
					if (diff.compareTo(Env.ZERO) == 0)
						continue;
					MInvoice inv = new MInvoice(getCtx(), 0, get_TrxName());
					inv.setAD_Org_ID(pay.getAD_Org_ID());
					inv.setC_DocTypeTarget_ID(p_c_doctype_ID);
					
					inv.setC_Project_ID(alloc.get_ValueAsInt(MPayment.COLUMNNAME_C_Project_ID));
					inv.setC_Activity_ID(alloc.get_ValueAsInt(MPayment.COLUMNNAME_C_Activity_ID));
					inv.setUser1_ID(alloc.get_ValueAsInt(MPayment.COLUMNNAME_User1_ID));
					inv.setC_Campaign_ID(alloc.get_ValueAsInt(MPayment.COLUMNNAME_C_Campaign_ID));
					inv.setC_BPartner_ID(pay.getC_BPartner_ID());
					inv.setC_Currency_ID(pay.getC_Currency_ID());
					inv.setM_PriceList_ID(pay.getC_BPartner().getPO_PriceList_ID());
					Boolean issotrx = inv.getC_DocTypeTarget().getDocBaseType().equals("ARI")?true:false;
					inv.setIsSOTrx(issotrx);
					inv.saveEx();
					MInvoiceLine ivl = new MInvoiceLine(inv);
					ivl.setC_Charge_ID(alloc.get_ValueAsInt(MPayment.COLUMNNAME_C_Charge_ID));
					ivl.setQty(Env.ONE);
					ivl.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
					ivl.setPrice(diff);
					ivl.setC_Project_ID(inv.getC_Project_ID());
					ivl.setC_Activity_ID(inv.getC_Activity_ID());
					ivl.setUser1_ID(inv.getUser1_ID());
					ivl.setC_Campaign_ID(inv.getC_Campaign_ID());
					ivl.saveEx();
					m_invoices.add(inv);
				}
				return "";
			}
			MInvoice inv = new MInvoice(getCtx(), 0, get_TrxName());
			inv.setC_DocTypeTarget_ID(p_c_doctype_ID);
			inv.setAD_Org_ID(pay.getAD_Org_ID());
			inv.setIsSOTrx(false);
			inv.setC_Project_ID(pay.getC_Project_ID());
			inv.setC_Activity_ID(pay.getC_Activity_ID());
			inv.setUser1_ID(pay.getUser1_ID());
			inv.setC_Campaign_ID(pay.getC_Campaign_ID());
			inv.setC_BPartner_ID(pay.getC_BPartner_ID());
			inv.setC_Currency_ID(pay.getC_Currency_ID());
			if (pay.getC_BPartner().getPO_PriceList_ID() == 0)
				return "El proveedor no tiene Lista de Precios asignado";
			inv.setM_PriceList_ID(pay.getC_BPartner().getPO_PriceList_ID());
			Boolean issotrx = inv.getC_DocTypeTarget().getDocBaseType().equals("ARI")?true:false;
			inv.setIsSOTrx(issotrx);
			inv.saveEx();
			MInvoiceLine ivl = new MInvoiceLine(inv);
			ivl.setC_Charge_ID(pay.getC_Charge_ID());
			ivl.setQty(Env.ONE);
			BigDecimal controlamt = (BigDecimal)pay.get_Value("ControlAmt");
			if (controlamt == null)
				controlamt = Env.ZERO;
			diff = pay.getPayAmt().subtract(controlamt);
			ivl.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
			ivl.setPrice(diff);
			ivl.setC_Project_ID(pay.getC_Project_ID());
			ivl.setC_Activity_ID(pay.getC_Activity_ID());
			ivl.setUser1_ID(pay.getUser1_ID());
			ivl.setC_Campaign_ID(pay.getC_Campaign_ID());
			ivl.saveEx();
			m_invoices.add(inv);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "", e);
			return e.getMessage();
		}
		return "";
	}	//	generate
	
	

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
	
	private String generateCashLine()
	{
		BigDecimal diff = Env.ZERO;int defaultaccount = new Query(getCtx(), MBankAccount.Table_Name, "", get_TrxName())
		.setClient_ID()
		.setOnlyActiveRecords(true)
		.firstId();
		MCash cash = getCash(p_C_CashBook_ID, p_DateInvoiced);
		if (cash==null)
			return "La caja está cerrada";
		for (MPayment pay:m_records)
		try
		{
			if (MPaymentAllocate.get(pay).length != 0)
			{
				for (MPaymentAllocate alloc:MPaymentAllocate.get(pay))
				{
					BigDecimal amount = alloc.getAmount();
					BigDecimal controlAmt = (BigDecimal)alloc.get_Value("ControlAmt");
					diff = amount.subtract(controlAmt);
					if (diff.compareTo(Env.ZERO) == 0)
						continue;
				}
				return "";
			}
			BigDecimal controlamt = (BigDecimal)pay.get_Value("ControlAmt"); 
			MPayment paydiff = new MPayment(getCtx(), 0, get_TrxName()); 
			paydiff.setTenderType(MPayment.TENDERTYPE_Cash);
			paydiff.setC_CashBook_ID(p_C_CashBook_ID);
			paydiff.setC_BankAccount_ID(defaultaccount);
			paydiff.setDateTrx(new Timestamp (System.currentTimeMillis()));
			paydiff.setIsOverUnderPayment(true);
			paydiff.setAD_Org_ID(pay.getAD_Org_ID());
			paydiff.setC_Project_ID(pay.getC_Project_ID());
			paydiff.setC_DocType_ID(true);
			paydiff.setC_BPartner_ID(pay.getC_BPartner_ID());
			paydiff.setC_Charge_ID(pay.getC_Charge_ID());
			paydiff.setC_Currency_ID(100);
			paydiff.set_ValueOfColumn("shw_tenderType_Cash", MPayment.TENDERTYPE_Cash);
			if (controlamt == null)
				controlamt = Env.ZERO;
			diff = pay.getPayAmt().subtract(controlamt);
			paydiff.setPayAmt(diff);
			paydiff.saveEx();
			result = result + paydiff.getDocumentInfo();
			whereClauseWindow = "c_payment_ID=" + paydiff.getC_Payment_ID();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "", e);
			return e.getMessage();
		}
		return "";
	
	}
	

	private MCash getCash(int C_CashBook_ID, 
			Timestamp dateAcct)
	{
		String whereClause ="C_CashBook_ID=?"			//	#1
				+ " AND TRUNC(StatementDate, 'DD')<=?"		//	#2
				+ " AND Processed='N'";
		
		MCash retValue = new Query(getCtx(), MCash.Table_Name, whereClause, get_TrxName())
			.setParameters(C_CashBook_ID, TimeUtil.getDay(dateAcct))
			.setOrderBy("TRUNC(StatementDate, 'DD') desc")
			.first()
		;		
		if (retValue != null)
			return retValue;
		return null;
	}
	
	
	private String generatePayment()
	{
		BigDecimal diff = Env.ZERO;
		BigDecimal difftotal = Env.ZERO;
		int defaultaccount = new Query(getCtx(), MBankAccount.Table_Name, "", get_TrxName())
		.setClient_ID()
		.setOnlyActiveRecords(true)
		.firstId();
		MPayment paydiff = null; 
		for (MPayment pay:m_records)
		try
		{
			if (paydiff == null)
			{
				paydiff = new MPayment(getCtx(), 0, get_TrxName());
				paydiff.setTenderType(MPayment.TENDERTYPE_Account);
				paydiff.setC_CashBook_ID(p_C_CashBook_ID);
				paydiff.setC_BankAccount_ID(defaultaccount);
				paydiff.setDateTrx(new Timestamp (System.currentTimeMillis()));
				paydiff.setIsOverUnderPayment(true);
				paydiff.setAD_Org_ID(pay.getAD_Org_ID());
				paydiff.setC_DocType_ID(true);
				paydiff.setC_BPartner_ID(pay.getC_BPartner_ID());
				paydiff.setC_Currency_ID(100);
				paydiff.saveEx();
			}
			if (MPaymentAllocate.get(pay).length != 0)
			{
				for (MPaymentAllocate alloc:MPaymentAllocate.get(pay))
				{
					BigDecimal amount = alloc.getAmount();
					BigDecimal controlAmt = (BigDecimal)alloc.get_Value("ControlAmt");
					diff = amount.subtract(controlAmt);
					if (diff.compareTo(Env.ZERO) == 0)
						continue;
				}
				return "";
			}
			MPaymentAllocate payalloc = new MPaymentAllocate(getCtx(), 0, get_TrxName());
			payalloc.setC_Payment_ID(paydiff.getC_Payment_ID());
			BigDecimal controlamt = (BigDecimal)pay.get_Value("ControlAmt"); 
			if (controlamt == null)
				controlamt = Env.ZERO;
			diff = pay.getPayAmt().subtract(controlamt);
			payalloc.set_CustomColumn(MPayment.COLUMNNAME_C_Charge_ID, pay.getC_Charge_ID());
			payalloc.setAmount(diff);
			payalloc.set_CustomColumn(MPayment.COLUMNNAME_C_Project_ID, pay.getC_Project_ID());
			payalloc.setAD_Org_ID(pay.getAD_Org_ID());
			payalloc.saveEx();
			difftotal = difftotal.add(diff);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "", e);
			return e.getMessage();
		}
		paydiff.setPayAmt(difftotal);
		paydiff.saveEx();
		result = result + paydiff.getDocumentInfo();
		whereClauseWindow = "c_payment_ID=" + paydiff.getC_Payment_ID();
		return "";
	
	}
	
	
	
}
