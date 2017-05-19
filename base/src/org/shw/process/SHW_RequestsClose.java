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
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.shw.model.MLGProductPriceRate;

/**
 *	Generate Invoices
 *	
 *  @author SHW
 *  @version $Id: SHW_InvoiceGenerate.java,v 1.2 2015/01/24 00:51:01 mc Exp $
 */
public class SHW_RequestsClose extends SvrProcess
{
	private 	int P_R_Status_ID = 0;
	protected List<MRequest> m_records = null;
	
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
			else if (name.equals(MRequest.COLUMNNAME_R_Status_ID))
				P_R_Status_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}	//	prepare

	/**
	 * 	Generate Invoices
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=R_Request.R_Request_ID)";
		m_records = new Query(getCtx(), MRequest.Table_Name, whereClause, get_TrxName())
											.setParameters(getAD_PInstance_ID())
											.setClient_ID()
											.list();
		for (MRequest req:m_records)
		{
			req.setR_Status_ID(P_R_Status_ID);
			req.saveEx();
		}

		
		return "";
	}	//	doIt
	



	
}	//	InvoiceGenerate
