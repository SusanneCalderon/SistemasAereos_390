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
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MProject;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_ChangeDocumentNo  extends SvrProcess
{
    String p_DocumentNo = "";
    
    @Override    
    protected void prepare()
    {    	
		ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(MInvoice.COLUMNNAME_DocumentNo))
				p_DocumentNo = para.getParameterAsString();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
    }
    
    
    
    @Override
    protected String doIt() throws Exception
    {	
    	MInvoice invoice = new MInvoice(getCtx(), getRecord_ID(), get_TrxName());
    	invoice.setDocumentNo(p_DocumentNo);
    	invoice.saveEx();
    	
    	return "";
    }
    
    
    
    
}
