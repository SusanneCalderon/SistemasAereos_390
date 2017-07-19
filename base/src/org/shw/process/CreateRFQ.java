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

import org.compiere.model.MBPartner;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MRfQ;
import org.compiere.model.MRfQLine;
import org.compiere.model.MRfQLineQty;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.model.X_C_RFQ_C_Bpartner;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class CreateRFQ  extends SvrProcess
{	

	protected List<MBPartner> m_records = null;
	Timestamp p_DateResponse = null;
	int 				p_M_Product_ID = 0;
	Boolean 		p_IsUpdateable = true;
			
    
    @Override    
    protected void prepare()
    {    	
    	ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameterName().equals("DateResponse"))
				p_DateResponse = para.getParameterAsTimestamp();

			else if (name.equals("M_Product_ID"))
				p_M_Product_ID = para.getParameterAsInt();
			else if (para.getParameterName().equals("IsUpdateable"))
				p_IsUpdateable = para.getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
    	String sql = "select c_rfq_ID from c_RfQ where R_Request_ID = ?";
    	int C_RfQ_ID = DB.getSQLValueEx(get_TrxName(), sql, getRecord_ID());
    	if (C_RfQ_ID<0 )
    		C_RfQ_ID = 0;
    	if (C_RfQ_ID != 0 && !p_IsUpdateable)
    		return "Ya existe una SCP para esta solicitud";
    	String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
    			" AND T_Selection.T_Selection_ID=C_BPartner.C_BPartner_ID)";
    	m_records = new Query(getCtx(), MBPartner.Table_Name, whereClause, get_TrxName())
    										.setParameters(getAD_PInstance_ID())
    										.setClient_ID()
    										.list();
    	
    	MRequest req = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
    	MRfQ rfq = new MRfQ(getCtx(), C_RfQ_ID, get_TrxName());
    	if (rfq.get_ID() == 0)
    	{
        	rfq.setName(req.getDocumentNo() + " " + req.getSalesRep().getName());
        	rfq.setDateResponse(p_DateResponse);
        	rfq.setQuoteType("A");
        	rfq.setIsActive(true);
        	rfq.setIsDirectLoad(false);
        	rfq.setIsInvitedVendorsOnly(false);
        	rfq.setIsQuoteAllQty(true);
        	rfq.setIsQuoteTotalAmt(true);
        	rfq.setIsRfQResponseAccepted(false);
        	rfq.setIsSelfService(false);
        	rfq.set_ValueOfColumn("R_Request_ID", req.getR_Request_ID());
        	rfq.setC_Currency_ID(100);
        	rfq.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
        	rfq.saveEx();    		
    	}
    	MRfQLine rfqLine = new MRfQLine(rfq);
    	if (p_M_Product_ID !=0)
    		rfqLine.setM_Product_ID(p_M_Product_ID);
    	if (req.get_ValueAsInt("LG_AirPort_Destiny_ID") != 0)
    		rfqLine.set_ValueOfColumn("LG_AirPort_Destiny_ID",req.get_ValueAsInt("LG_AirPort_Destiny_ID"));
    	if (req.get_ValueAsInt("LG_AirPort_Origin_ID") != 0)
    		rfqLine.set_ValueOfColumn("LG_AirPort_Origin_ID",req.get_ValueAsInt("LG_AirPort_Origin_ID"));
    	if (req.get_ValueAsInt("LG_CityFrom_ID") != 0)    	
    		rfqLine.set_ValueOfColumn("LG_CityFrom_ID",req.get_ValueAsInt("LG_CityFrom_ID"));
    	if (req.get_ValueAsInt("LG_CityTo_ID") != 0)    	
    		rfqLine.set_ValueOfColumn("LG_CityTo_ID",req.get_ValueAsInt("LG_CityTo_ID"));
    	if (req.get_ValueAsInt("c_country_origin_ID") != 0)    	
    		rfqLine.set_ValueOfColumn("lg_countryfrom_id",req.get_ValueAsInt("c_country_origin_ID"));
    	if (req.get_ValueAsInt("c_country_destiny_id") != 0)    	
    		rfqLine.set_ValueOfColumn("lg_countryto_id",req.get_ValueAsInt("c_country_destiny_id"));
    	rfqLine.saveEx();
    	MRfQLineQty rfqquant = new MRfQLineQty(rfqLine);
    	rfqquant.setC_UOM_ID(100);
    	if (req.get_ValueAsInt("C_UOM_Volume_ID")!= 0)
    		rfqquant.setC_UOM_ID(req.get_ValueAsInt("C_UOM_Volume_ID"));
    	if (req.get_ValueAsInt("C_UOM_Weight_ID")!= 0)
    		rfqquant.setC_UOM_ID(req.get_ValueAsInt("C_UOM_Weight_ID"));
    	if (req.get_ValueAsString("LG_TransportType").length() >0)
    		rfqLine.set_ValueOfColumn("LG_TransportType", req.get_ValueAsString("LG_TransportType"));
    	
    	rfqquant.setQty((BigDecimal)req.get_Value("CumulatedQty"));
    	rfqquant.saveEx();
    	ArrayList<Object> params = new ArrayList<Object>();
    	for (MBPartner bpartner : m_records)
    	{
    		params.clear();
    		sql = "select count(*) from C_RFQ_C_Bpartner where C_RfQ_ID =? and C_BPartner_ID=? ";
    		params.add(rfq.getC_RfQ_ID());
    		params.add(bpartner.getC_BPartner_ID());
    		int no = DB.getSQLValueEx(get_TrxName(), sql, params);
    		if (no != 0)
    			continue;
    		X_C_RFQ_C_Bpartner rfq_bp = new X_C_RFQ_C_Bpartner(getCtx(), 0, get_TrxName());
    		rfq_bp.setC_BPartner_ID(bpartner.getC_BPartner_ID());
    		rfq_bp.setC_RfQ_ID(rfq.getC_RfQ_ID());
    		rfq_bp.saveEx();
    	}
    	req.set_ValueOfColumn("C_RfQ_ID", rfq.getC_RfQ_ID());
    	req.saveEx();
    	return "";
    }
    
    protected void zoom (int AD_Window_ID, MQuery zoomQuery)
	{}	//	zoom
    

}
