/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
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
 * Copyright (C) 2003-2012 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): victor.perez@e-evolution.com www.e-evolution.com   		  *
 *****************************************************************************/
package org.shw.process;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.compiere.model.MCharge;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MRequest;
import org.compiere.model.MTaxCategory;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.eevolution.grid.Browser;

/**
 * Scheduling Activities
 * 
 * @author Systemhaus Westfalia
 */

public class SHW_CreateOrderLine_Payment extends SvrProcess {

	protected List<MPayment> m_records = null;
	protected LinkedHashMap<Integer, LinkedHashMap<String, Object>> m_values = null;
	protected 						int processRecords = 0;
	private 						int p_C_Charge_ID = 0;
	private 						int p_C_Order_ID =0;
	private 						MOrder order = null;
	private 						BigDecimal p_DistributionAmt = Env.ZERO;
	
	/**
	 * Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MCharge.COLUMNNAME_C_Charge_ID))
				p_C_Charge_ID = para.getParameterAsInt();
			else if (name.equals(MOrder.COLUMNNAME_C_Order_ID))
				p_C_Order_ID = para.getParameterAsInt();
			else if (name.equals("DistributionAmt"))
				p_DistributionAmt = para.getParameterAsBigDecimal();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		setColumnsValues();
	} // prepare

	/**
	 * Perform process.
	 * 
	 * @return Message (clear text)
	 * @throws Exception
	 *             if not successful
	 */
	protected String doIt() throws Exception {
		if (p_C_Order_ID==0) 
			return "No se escogió una Orden!";
		
		order = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());
		for (MPayment pay : getRecords()) 
		{
			GenerateLine(pay);	
		}
		return"Líneas añadidas a Orden: " + order.getDocumentNo();
	} // doIt
	
	private void saveBrowseValues(PO po, String alias) {

		LinkedHashMap<String, Object> values = m_values.get(po.get_ID());

		for (Entry<String, Object> entry : values.entrySet()) {
			String columnName = entry.getKey();
			if (columnName.contains(alias.toUpperCase() + "_")) {
				columnName = columnName.substring(columnName.indexOf("_") + 1);
				if(entry.getValue() != null)
					po.set_ValueOfColumn(columnName, entry.getValue());
			}
		}

	}

	private List<MPayment> getRecords() {
		if (m_records != null)
			return m_records;

		String whereClause = 
				"EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
		         "AND T_Selection.T_Selection_ID=C_Payment.C_Payment_ID)";
		m_records = new Query(getCtx(), MPayment.Table_Name, whereClause,
				get_TrxName()).setClient_ID()
				.setParameters(getAD_PInstance_ID()).list();
		return m_records;
	}

	private LinkedHashMap<Integer, LinkedHashMap<String, Object>> setColumnsValues() {
		if (m_values != null)
			return m_values;

		m_values = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();

		for (MPayment record : getRecords()) {
			m_values.put(
					record.get_ID(),
					Browser.getBrowseValues(getAD_PInstance_ID(), null,
							record.get_ID(), null));
		}
		return m_values;
	}
	private void GenerateLine(MPayment pay)
	{
		if (p_DistributionAmt.compareTo(Env.ZERO) == 0)
			p_DistributionAmt = pay.getPayAmt();
		String chargeName ="";
		MOrderLine oLine = new MOrderLine(order);
		int chargeID;
		if (p_C_Charge_ID==0) 
			chargeID = pay.getC_Charge_ID();
		else
			chargeID = p_C_Charge_ID;
		
		if (chargeID==0) {
			return; // Keine Charge als Parameter noch im Payment > raus
		}
		else {
			oLine.setC_Charge_ID(chargeID);
			MCharge charge = new MCharge(getCtx(), chargeID, get_TrxName());
			chargeName = charge.getName();	
			MTaxCategory tc = (MTaxCategory)charge.getC_TaxCategory();
			oLine.setC_Tax_ID(tc.getDefaultTax().getC_Tax_ID());
		}

		oLine.setQty(Env.ONE);
		oLine.setPrice(p_DistributionAmt);
		oLine.set_ValueOfColumn(MPayment.COLUMNNAME_C_Payment_ID, pay.getC_Payment_ID());
		oLine.setDescription("# de Pago: " + pay.getDocumentNo());
		oLine.setC_Project_ID(pay.getC_Project_ID());
		oLine.saveEx();
		pay.set_ValueOfColumn("isInvoiced", true);
		pay.setDescription(pay.getDescription() == null?"": 
			pay.getDescription() + " Asignado en orden: " + order.getDocumentNo() );
		if (pay.get_ValueAsInt("R_Request_ID") > 0)
		{
			MRequest req = new MRequest(getCtx(), pay.get_ValueAsInt("R_Request_ID"), get_TrxName());
			req.set_ValueOfColumn("C_OrderLine_ID", oLine.getC_OrderLine_ID());
			req.saveEx();			
		}
		pay.saveEx();
	}
} // Import Inventory Move
