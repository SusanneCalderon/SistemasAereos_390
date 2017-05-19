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

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MRequestType;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.shw.model.MLGRoute;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class Order_AfterPrepare_CreateRequest  extends SvrProcess
{	
	private int				p_C_UOM_Volumen_ID 	= 0;
	private int 			p_C_UOM_Weight_ID 	= 0;
	private BigDecimal 		p_qtyWeight 		= Env.ZERO;
	private BigDecimal 		p_qtyVolumen 		= Env.ZERO;
    
    @Override    
    protected void prepare()
    {    	

        ProcessInfoParameter[] parameters = getParameter();
        for (ProcessInfoParameter parameter : parameters) {

            String name = parameter.getParameterName();
            if (parameter.getParameter() == null)
                ;
            else if (name.equals("C_UOM_Volume_ID"))
            	p_C_UOM_Volumen_ID = parameter.getParameterAsInt();
             else if (name.equals("C_UOM_Weight_ID"))
            	p_C_UOM_Weight_ID = parameter.getParameterAsInt();
             else if (name.equals("QtyVolume"))
            	p_qtyVolumen = parameter.getParameterAsBigDecimal();
             else if (name.equals("QtyWeight"))
            	p_qtyWeight = parameter.getParameterAsBigDecimal();
        }
    }
    
      
    @Override
    protected String doIt() throws Exception
    {

    	MRequest req = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
    	if (req.get_ValueAsInt("C_BPartnerVendor_ID") == 0)
    		return "Hay que definir el Proveedor";
    	int defaultaccount = new Query(req.getCtx(), MBankAccount.Table_Name, "", req.get_TrxName())
		.setClient_ID()
		.setOnlyActiveRecords(true)
		.firstId(); 
    	MPayment pay = new MPayment(getCtx(), 0, get_TrxName());  	
    	pay.setC_BankAccount_ID(defaultaccount);
    	pay.setDateTrx(new Timestamp (System.currentTimeMillis()));
    	//payselection.setDocumentNo(m_CxP.getC_BPartner().getValue() + " " + m_CxP.getDocumentNo());
    	//payselection.setDocumentNo(m_CxP.getC_BPartner().getName() + " " + m_CxP.getDocumentNo());
    	pay.setAD_Org_ID(req.getAD_Org_ID());
    	pay.setC_Project_ID(req.getC_Project_ID());
    	pay.setC_BPartner_ID(req.get_ValueAsInt("C_BPartnerVendor_ID"));
    	MRequestType rtype = (MRequestType)req.getR_RequestType();
    	
    	if (rtype.get_ValueAsInt(MPayment.COLUMNNAME_C_DocType_ID)!= 0)
    		pay.setC_DocType_ID(rtype.get_ValueAsInt(MPayment.COLUMNNAME_C_DocType_ID));
    	else
    		pay.setC_DocType_ID(false);
     	//pay.setIsReceipt(false);
    	pay.setC_Charge_ID(req.get_ValueAsInt(MCharge.COLUMNNAME_C_Charge_ID));
    	pay.setC_Currency_ID(100);
    	pay.setPayAmt(req.getRequestAmt());
    	pay.setC_Activity_ID(req.getC_Activity_ID());
    	pay.setC_Campaign_ID(req.getC_Campaign_ID());
    	MProject prj = (MProject)req.getC_Project();
    	pay.setUser1_ID(prj.get_ValueAsInt("User1_ID"));
    	pay.set_ValueOfColumn(MRequest.COLUMNNAME_R_Request_ID, req.getR_Request_ID());
    	pay.saveEx();
    	req.setC_Payment_ID(pay.getC_Payment_ID());
    	req.saveEx();
		String whereClauseWindow = "c_invoice_ID in (";
		whereClauseWindow = "c_payment_ID=" + pay.getC_Payment_ID();
		MQuery query = new MQuery("");
		MTable table = new MTable(getCtx(), MPayment.Table_ID, get_TrxName());
		query.addRestriction(whereClauseWindow);
		query.setRecordCount(1);
		int AD_WindowNo = table.getAD_Window_ID();
		commitEx();
		zoom (AD_WindowNo, query);
		
    	return "";
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
