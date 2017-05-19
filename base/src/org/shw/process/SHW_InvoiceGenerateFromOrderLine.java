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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MSession;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.eevolution.grid.Browser;

/**
 *	Generate Invoices
 *	
 *  @author SHW
 *  @version $Id: SHW_InvoiceGenerate.java,v 1.2 2015/01/24 00:51:01 mc Exp $
 */
public class SHW_InvoiceGenerateFromOrderLine extends SvrProcess
{
	/**	Date Invoiced			*/
	private Timestamp	p_DateInvoiced = null;
	/** Consolidate				*/
	private boolean		p_ConsolidateDocument = true;
	/** Invoice Document Action	*/
	private String		p_docAction = "";
	
	/**	The current Invoice	*/
	private MInvoice 	m_invoice = null;
	/**	The current Shipment	*/
	private MInOut	 	m_ship = null;
	/** Number of Invoices		*/
	private int			m_created = 0;
	/**	Line Number				*/
	private int			m_line = 0;
	/**	Business Partner		*/
	private MBPartner	m_bp = null;
	protected List<MOrderLine> m_records = null;
	protected List<MInvoice> m_invoices = null;
	protected List<MOrder> ordersToInvoice = null;
	protected List<MInOut> shipments = null;
	protected MInOut m_shipment = null;
	private MOrder m_order = null;
	private Boolean IsSoTrx = false;
	private int P_C_Invoice_ID = 0;
	protected LinkedHashMap<Integer, LinkedHashMap<String, Object>> m_values = null;
	private String alias = "";
	private int errorcount = 0;
	private String error = "";
	private int successcount = 0;
	private int ordercount = 0;
	private int p_C_Doctype_ID = 0;
	
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
			else if (name.equals("ConsolidateDocument"))
				p_ConsolidateDocument = para.getParameterAsBoolean();
			else if (name.equals("DocAction"))
				p_docAction = para.getParameterAsString();
			else if (name.equals(MInvoice.COLUMNNAME_DateInvoiced))
				p_DateInvoiced = para.getParameterAsTimestamp();
			else if (name.equals(MAccount.COLUMNNAME_Alias))
				alias = para.getParameterAsString();
			else if (name.equals(MInvoice.COLUMNNAME_C_Invoice_ID))
				P_C_Invoice_ID = para.getParameterAsInt();
			else if (name.equals(MDocType.COLUMNNAME_C_DocType_ID))
				p_C_Doctype_ID = para.getParameterAsInt();
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

		StringBuffer orderClause = new StringBuffer();
		if (!p_ConsolidateDocument)
			orderClause.append("C_BPartner_ID, C_Project_ID, c_doctypetarget_ID");
		else 
			orderClause.append("C_BPartner_ID");
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=c_orderLine.C_OrderLine_ID)";
		m_records = new Query(getCtx(), MOrderLine.Table_Name, whereClause, get_TrxName())
		.setParameters(getAD_PInstance_ID())
		.setOrderBy(orderClause.toString())
		.setClient_ID()
		.list();

		setColumnsValues();
		m_invoices = new ArrayList<MInvoice>();
		ordersToInvoice = new ArrayList<MOrder>();
		shipments = new ArrayList<MInOut>();
		for (MOrderLine orderLine: m_records) 
		{	
			m_order = (MOrder)orderLine.getC_Order();
			Boolean isadded =  false;
			for (MOrder order:ordersToInvoice)
			{
				if (order.getC_Order_ID() ==  m_order.getC_Order_ID())
				{
					isadded = true;
					break;
				}
			}
			if (!isadded)
				ordersToInvoice.add(m_order);
		}
		for(MOrder order:ordersToInvoice)
		{
			try
			{
				generate(order);				
			}
			catch (Exception e)
			{
				errorcount = errorcount + 1;
				error = error + order.getDocumentNo();
				continue;
			}
		}
		completeInvoice();
		String whereClauseWindow = "c_invoice_ID in (";
		for (MInvoice inv:m_invoices)
		{
			whereClauseWindow = whereClauseWindow + inv.getC_Invoice_ID() + ",";
			IsSoTrx = inv.isSOTrx();
		}
		whereClauseWindow = whereClauseWindow.substring(0, whereClauseWindow.length() -1) + ")";
		MQuery query = new MQuery("");
		MTable table = new MTable(getCtx(), MInvoice.Table_ID, get_TrxName());
		query.addRestriction(whereClauseWindow);
		query.setRecordCount(m_invoices.size());
		int AD_WindowNo = 0;
		if (IsSoTrx)
			AD_WindowNo = table.getAD_Window_ID();
		else
			AD_WindowNo = table.getPO_Window_ID();

		int ad_session_ID  = Env.getContextAsInt(getCtx(), "AD_Session_ID");
		MSession session = new MSession(getCtx(), ad_session_ID, null);

		if (session.getWebSession() == null ||session.getWebSession().length() == 0)
		{
			commitEx();
			zoom (AD_WindowNo, query);
			return "";

		}

		String result = "Fact. No";
		for (MInvoice inv:m_invoices)
		{
			Env.setContext(getCtx(), "@WhereClause@", whereClause);
			result = result + ", " + inv.getDocumentInfo();
		}
		return result;
	}	//	doIt
	
	
	/**
	 * 	Generate Shipments
	 * 	@param pstmt order query 
	 *	@return info
	 */
	private String generate (MOrder order) throws Exception
	{
		shipments = new ArrayList<MInOut>();
		shipments = new Query(getCtx(), MInOut.Table_Name, "C_Order_ID=? and docstatus in ('CO','CL')", get_TrxName())
		.setParameters(order.getC_Order_ID())
		.list();
		createShipment(order,null);
		for (MInOut ship:shipments)
		{
			if (!ship.isComplete()		//	ignore incomplete or reversals 
					|| ship.getDocStatus().equals(MInOut.DOCSTATUS_Reversed))
				continue;
			MInOutLine[] shipLines = ship.getLines(true);
			for (int j = 0; j < shipLines.length; j++)
			{
				MInOutLine shipLine = shipLines[j];
				MOrderLine oLine = (MOrderLine)shipLine.getC_OrderLine();
				if (oLine == null || !m_records.contains(oLine))
					continue;	

				Boolean isInvoiced = shipLine.isInvoiced();
				if (shipLine.getC_OrderLine_ID()!= 0)
				{
					if (shipLine.getC_OrderLine().getQtyInvoiced()
							.compareTo(shipLine.getC_OrderLine().getQtyOrdered()) != 0)
						isInvoiced = false;
				}
				shipLine.getC_OrderLine().getQtyInvoiced();
				if (!isInvoiced)
					createLine (m_order, ship, shipLine);
			}
			m_line += 10;
		}



		return "";
	}	//	generate
	
	

	/**
	 * 	Create Invoice Line from Shipment
	 *	@param order order
	 *	@param ship shipment header
	 *	@param sLine shipment line
	 */
	private void createLine (MOrder order, MInOut ship, MInOutLine sLine)
	{
		if (m_invoice == null)
		{
			if (P_C_Invoice_ID != 0)
				m_invoice = new MInvoice(getCtx(), P_C_Invoice_ID, get_TrxName());
			else
			{
				m_invoice = new MInvoice (order, 0, p_DateInvoiced);
				m_invoice.setDescription(order.getC_Project().getValue() + "/");
				if (order.getPaymentRule()!= "" || order.getPaymentRule() != null)
					m_invoice.setPaymentRule(order.getPaymentRule());
				if (order.getC_PaymentTerm_ID() > 0)
					m_invoice.setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());
				if (p_C_Doctype_ID != 0)
					m_invoice.setC_DocTypeTarget_ID(p_C_Doctype_ID);
				m_invoice.saveEx();			
				
			}
		}
		else
		{
			if(!m_invoice.getDescription().contains(order.getC_Project().getValue())){
				m_invoice.setDescription(m_invoice.getDescription() + " "+ order.getC_Project().getValue() + "/");
				m_invoice.saveEx();
			}		
		}
			
		//	Create Shipment Comment Line
		if (m_ship == null 
			|| m_ship.getM_InOut_ID() != ship.getM_InOut_ID())
		{

			if (m_bp == null || m_bp.getC_BPartner_ID() != ship.getC_BPartner_ID())
				m_bp = new MBPartner (getCtx(), ship.getC_BPartner_ID(), get_TrxName());
			
			//	Reference: Delivery: 12345 - 12.12.12
			MClient client = MClient.get(getCtx(), order.getAD_Client_ID ());
			String AD_Language = client.getAD_Language();
			if (client.isMultiLingualDocument() && m_bp.getAD_Language() != null)
				AD_Language = m_bp.getAD_Language();
			if (AD_Language == null)
				AD_Language = Language.getBaseAD_Language();
			m_ship = ship;
		}
		//
		//
		BigDecimal qtyToInvoice = sLine.getMovementQty();
		BigDecimal qtyentered = sLine.getC_OrderLine().getQtyEntered();
		Integer repeat = 1;
		//
		for (int j = 0; j < repeat; j++)	
		{
			MInvoiceLine iLine = new MInvoiceLine (m_invoice);
			iLine.setShipLine(sLine);
			//	Qty = Delivered	
			if (sLine.sameOrderLineUOM())
				iLine.setQtyEntered(qtyToInvoice);
			else
				iLine.setQtyEntered(qtyToInvoice);
			iLine.setQtyInvoiced(qtyToInvoice);
			if (iLine.getUser1_ID() ==0)
			{
				MProject pr = (MProject)iLine.getC_Project();
				iLine.setUser1_ID(pr.get_ValueAsInt("User1_ID"));
			}
			if (!iLine.save(get_TrxName()))
			{
				throw new IllegalStateException("Could not create Invoice Line");
			}					
			//	Link
			log.fine(iLine.toString());
		}
		sLine.setIsInvoiced(true);
		
		if (!sLine.save())
			throw new IllegalStateException("Could not update Shipment Line");

			Boolean isadded =  false;
			for (MInvoice inv:m_invoices)
			{
				if (inv.getC_Invoice_ID() ==  m_invoice.getC_Invoice_ID())
				{
					isadded = true;
					break;
				}
			}
			if (!isadded)
				m_invoices.add(m_invoice);
	}	//	createLine

	
	/**
	 * 	Complete Invoice
	 */
	private void completeInvoice()
	{
		if (m_invoice != null)
		{
			if (m_invoice.isSOTrx())
			{
				m_invoice.set_ValueOfColumn("isSplitInvoice", true);
			}
			SplitInvoice();
			if (m_invoice.getLines(true).length ==0)
			{
				m_invoice.setC_Order_ID(0);
				m_invoice.delete(true);
				m_invoice = null;
				m_ship = null;
				m_line = 0;
				return;
			}
			if (p_docAction.equals(MOrder.DOCACTION_Complete) || p_docAction.equals(MOrder.DOCACTION_Prepare))
			{
				if (!m_invoice.processIt(p_docAction))
				{
					log.warning("completeInvoice - failed: " + m_invoice);
					addLog("completeInvoice - failed: " + m_invoice); // Elaine 2008/11/25
				}
			}

			m_invoice.saveEx();

			addLog(m_invoice.getC_Invoice_ID(), m_invoice.getDateInvoiced(), null, m_invoice.getDocumentNo());
			m_created++;
		}
		m_invoice = null;
		m_ship = null;
		m_line = 0;
	}	//	completeInvoice
	


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
	/**
	 * 	Correct Invoice
	 */
	
	private MInOut createShipment(MOrder order, Timestamp movementDate)
	{
		
		MInOut shipment = new MInOut (order, order.getC_DocType().getC_DocTypeShipment_ID(), movementDate);
		if (!shipment.save(get_TrxName()))
		{
			return null;
		}
		//
		for (MOrderLine oLine:m_records)
		{
			if (oLine.getC_Order_ID() != order.getC_Order_ID())
				continue;
			BigDecimal MovementQty = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered());
			LinkedHashMap<String, Object> values = m_values.get(oLine.get_ID());
			for (Entry<String, Object> entry : values.entrySet()) {
				String columnName = entry.getKey();
				
					if (columnName.equals(alias + "_QtyToDeliver"))
					{
						BigDecimal qtyTodeliver = (BigDecimal)entry.getValue();
						if (qtyTodeliver.compareTo(MovementQty) <0)
							MovementQty = qtyTodeliver;
					}
				}				 
			if (MovementQty.compareTo(Env.ZERO)==0)
				continue;
			MInOutLine ioLine = new MInOutLine(shipment);
			//	Qty = Ordered - Delivered
			//	Location
			int M_Locator_ID = MStorage.getM_Locator_ID (oLine.getM_Warehouse_ID(), 
					oLine.getM_Product_ID(), oLine.getM_AttributeSetInstance_ID(), 
					MovementQty, get_TrxName());
			if (M_Locator_ID == 0)		//	Get default Location
			{
				MWarehouse wh = MWarehouse.get(getCtx(), oLine.getM_Warehouse_ID());
				M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
			}
			//
			ioLine.setOrderLine(oLine, M_Locator_ID, MovementQty);
			ioLine.setQty(MovementQty);
			ioLine.setC_Project_ID(oLine.getC_Project_ID());
			if (oLine.getQtyEntered().compareTo(oLine.getQtyOrdered()) != 0)
				ioLine.setQtyEntered(MovementQty
					.multiply(oLine.getQtyEntered())
					.divide(oLine.getQtyOrdered(), 6, BigDecimal.ROUND_HALF_UP));
			if (!ioLine.save(get_TrxName()))
			{
				return null;
			}
		}
		//	Manually Process Shipment

		MInOutLine[] lines = shipment.getLines(true);
		if (lines == null || lines.length == 0)
		{
			shipment.deleteEx(true);
			return null;
		}
		if (!shipment.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException("Error al procesar entrega " + order.getDocumentNo());
		shipment.saveEx(get_TrxName());
		if (!MOrder.DOCSTATUS_Completed.equals(shipment.getDocStatus()))
		{
			return null;
		}
		shipments.add(shipment);
		return shipment;
	}	//	createShipment
	
	private LinkedHashMap<Integer, LinkedHashMap<String, Object>> setColumnsValues() {
		if (m_values != null)
			return m_values;

		m_values = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();

		for (MOrderLine record : m_records) {
			m_values.put(
					record.get_ID(),
					Browser.getBrowseValues(getAD_PInstance_ID(), null,
							record.get_ID(), null));
		}
		return m_values;
	}
	private String SplitInvoice()
	{ 
    	
    	Boolean isSplitInvoice = m_invoice.get_ValueAsBoolean("isSplitInvoice");
    	if (!isSplitInvoice)
    	{
    		m_invoice.renumberLines(10);
    		return"";
    	}
		MDocType dt = (MDocType)m_invoice.getC_DocType();
		if (dt.get_ValueAsBoolean("isSplitInvoice"))
			return"";
		int c_doctype_ID = new Query(getCtx(), MDocType.Table_Name, "isSplitInvoice ='Y'", get_TrxName())
			.setOnlyActiveRecords(true)
			.setClient_ID()
			.firstId();

		MInvoice NdD = new MInvoice((MOrder)m_invoice.getC_Order(), c_doctype_ID, m_invoice.getDateInvoiced());
		NdD.saveEx();
		for (MInvoiceLine ivl:m_invoice.getLines(true))
		{
			if (ivl.getC_OrderLine_ID() <= 0)
				continue;
			MOrderLine oLine = (MOrderLine)ivl.getC_OrderLine();
			if (oLine.get_ValueAsBoolean("isSplitInvoice"))
			{
				ivl.setC_Invoice_ID(NdD.getC_Invoice_ID());
				ivl.saveEx();
			}
		}
		m_invoice.saveEx();
		m_invoice.renumberLines(10);
		NdD.saveEx();
		
		if (NdD.getLines(true).length ==0)
		{
			NdD.setC_Order_ID(0);
			NdD.delete(true);
		}
		else
		{
			NdD.renumberLines(10);
			if (NdD.processIt(p_docAction))
			{
				NdD.setDocAction("CO");
				NdD.saveEx();
				m_invoices.add(NdD);
			}			
		}
		return "";
	}

	

	

	
}	//	InvoiceGenerate
