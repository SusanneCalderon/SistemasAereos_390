/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2016 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/

package org.shw.process;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MOpportunity;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProductPricing;
import org.compiere.model.MProject;
import org.compiere.model.MProjectType;
import org.compiere.model.MRequest;
import org.compiere.model.MUser;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.shw.model.MLGProductPriceRate;
import org.shw.model.MLGRoute;

/** Generated Process for (shw_ConvertQuotationToProject)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class shw_ConvertQuotationToProject extends shw_ConvertQuotationToProjectAbstract
{
	MProject projectFather = null;
	MOpportunity opp = null;
	MOrder original  = null;
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		original = new MOrder (getCtx(), getRecord_ID(), get_TrxName());
		opp = (MOpportunity)original.getC_Opportunity();
		MOrder newOrder = CopyOrder();
		MProject project = null;
		for (int key:getSelectionKeys())
		{
			Boolean isDefault = getSelectionAsBoolean(key, "OLINE_isLeadingProject");
			if (!isDefault)
				continue;
			MOrderLine oLine = new MOrderLine(getCtx(), key, get_TrxName());
			MOrderLine newLine = new MOrderLine(newOrder);
			MOrderLine.copyValues(oLine, newLine); 
			newLine.setC_Order_ID(newOrder.getC_Order_ID());
		    project = CreateProject(key, newLine);
			newLine.setC_Project_ID(project.getC_Project_ID());
			newLine.setProcessed(false);
			newLine.saveEx();			

			MOrder purchaseOrder = CreatePurchaseOrder(getSelectionAsInt(key, "OLINE_C_BPartnerPO_ID"), projectFather);
			MOrderLine poLine = createpoLine(purchaseOrder, newLine);
			newLine.set_ValueOfColumn("C_OrderLine_SO_ID", poLine.getC_OrderLine_ID());
			poLine.set_ValueOfColumn("C_OrderLine_PO_ID", newLine.getC_OrderLine_ID());
			newLine.saveEx();
			oLine.setC_Project_ID(projectFather.getC_Project_ID());
			oLine.saveEx();
		}

		for (int key:getSelectionKeys())
		{
			Boolean isDefault = getSelectionAsBoolean(key, "OLINE_isLeadingProject");
			if (isDefault)
				continue;
			MOrderLine oLine = new MOrderLine(getCtx(), key, get_TrxName());
			MOrderLine newLine = new MOrderLine(newOrder);
			MOrderLine.copyValues(oLine, newLine); 
			newLine.setC_Order_ID(newOrder.getC_Order_ID());
			newLine.setC_Project_ID(project.getC_Project_ID());
			newLine.saveEx();			
			MOrder purchaseOrder = CreatePurchaseOrder(getSelectionAsInt(key, "OLINE_C_BPartnerPO_ID"), project);
			MOrderLine poLine = createpoLine(purchaseOrder, newLine);
			newLine.set_ValueOfColumn("C_OrderLine_SO_ID", poLine.getC_OrderLine_ID());
			poLine.set_ValueOfColumn("C_OrderLine_PO_ID", newLine.getC_OrderLine_ID());
			newLine.saveEx();
			oLine.setC_Project_ID(project.getC_Project_ID());
			oLine.saveEx();
		}
		original.setC_Project_ID(project.getC_Project_ID());
		original.saveEx();
		newOrder.setC_Project_ID(project.getC_Project_ID());
		newOrder.saveEx();
		return "";
	}
	

	private MProject CreateProject(int key, MOrderLine orderLine)throws Exception
	{
		int c_Project_ID = getSelectionAsInt(key, "OLINE_C_ProjectFather_ID");
		MProject project = null;
		
		if (c_Project_ID > 0)
		{
			projectFather = new MProject(getCtx(), c_Project_ID, get_TrxName());
		}
		if (projectFather == null)
		{
			projectFather = new MProject(getCtx(), 0, get_TrxName());
			projectFather.setC_BPartner_ID(getSelectionAsInt(key, "OLINE_C_BPartnerPO_ID"));
			projectFather.setDateContract(new Timestamp (System.currentTimeMillis()));
			projectFather.setC_Currency_ID(orderLine.getC_Order().getC_Currency_ID());
			projectFather.setName(projectFather.getC_BPartner().getName());
			projectFather.setSalesRep_ID(orderLine.getC_Order().getSalesRep_ID());
			projectFather.setIsSummary(true);
			MProjectType type = new MProjectType (getCtx(), getSelectionAsInt(key, "OLINE_C_ProjectTypeFather_ID"), get_TrxName());
			if (type.getC_ProjectType_ID() == 0)
				throw new IllegalArgumentException("Project Type not found C_ProjectType_ID=" );
			//	Set & Copy if Service
			projectFather.setDescription(projectFather.getName());
			projectFather.setProjectType(type);
			projectFather.saveEx();

			
		}
		project = new MProject(getCtx(), 0, get_TrxName());
		int LG_ProductPriceRate_ID = orderLine.get_ValueAsInt("LG_ProductPriceRate_ID");
		MLGProductPriceRate ppl = new MLGProductPriceRate(getCtx(), LG_ProductPriceRate_ID, get_TrxName());
		project.set_ValueOfColumn("LG_Route_ID", ppl.getLG_Route_ID());
		MBPartner m_bpartner = (MBPartner)orderLine.getC_Order().getC_BPartner();
		project.setC_BPartner_ID(orderLine.getC_Order().getC_BPartner_ID());
		project.setDateContract(projectFather.getDateContract());
		project.setC_Currency_ID(orderLine.getC_Order().getC_Currency_ID());
		project.set_ValueOfColumn(MOrder.COLUMNNAME_C_Activity_ID,m_bpartner.get_Value(MOrder.COLUMNNAME_C_Activity_ID));
		project.set_ValueOfColumn(MOrder.COLUMNNAME_User1_ID,   m_bpartner.get_Value(MOrder.COLUMNNAME_User1_ID));
		project.setName(m_bpartner.getName());
		project.setSalesRep_ID(orderLine.getC_Order().getSalesRep_ID());
		BigDecimal volume = Env.ZERO;
		BigDecimal weight = Env.ZERO;
		if (opp != null && opp.get_ID()!= 0)
		{
			int r_request_ID = opp.get_ValueAsInt("R_Request_ID");
			MRequest req = new MRequest(getCtx(), r_request_ID, get_TrxName());
			project.set_ValueOfColumn("Volume", (BigDecimal)req.get_Value("Volume"));
			project.set_ValueOfColumn("Weight", (BigDecimal)req.get_Value("Weight"));
			project.set_ValueOfColumn("C_UOM_Volume_ID", req.get_ValueAsInt("C_UOM_Volume_ID"));
			project.set_ValueOfColumn("C_UOM_ID", req.get_ValueAsInt("C_UOM_Weight_ID"));
		}
		project.set_ValueOfColumn("C_Project_Parent_ID", projectFather.getC_Project_ID());
		MProjectType type = new MProjectType (getCtx(), getSelectionAsInt(key, "OLINE_C_ProjectType_ID"), get_TrxName());
		if (type.getC_ProjectType_ID() == 0)
			throw new IllegalArgumentException("Project Type not found C_ProjectType_ID=");

		//	Set & Copy if Service
		project.setProjectType(type);
		if (!project.save())
			throw new Exception ("@Error@");
		return project;
	}
	
	private MOrder CopyOrder ()
	{

		MDocType dt = MDocType.get(getCtx(), getDocumentTypeId());
		if (dt.get_ID() == 0)
			throw new IllegalArgumentException("No DocType");
		Timestamp	p_DateDoc = new Timestamp (System.currentTimeMillis());
		//
		
		
		MOrder newOrder = new MOrder (getCtx(), 0, get_TrxName());	
		MOrder.copyValues(original, newOrder);
		newOrder.setC_DocTypeTarget_ID(getDocumentTypeId());
		String documentNo = DB.getDocumentNo(getDocumentTypeId(), get_TrxName(),false);
		newOrder.setDocumentNo(documentNo);
		newOrder.setDocAction("IP");
		newOrder.setDocStatus("DR");
		newOrder.setProcessed(false);
		boolean OK = newOrder.save();
		if (!OK)
			throw new IllegalStateException("Could not create new Order");
		//
		//
			return newOrder;
}	

	private MOrder CreatePurchaseOrder (int providerID, MProject project)
	{

		MOrder order = new MOrder(getCtx()	, 0, get_TrxName());
		order.setAD_Org_ID(project.getAD_Org_ID());
		order.setC_Campaign_ID(project.getC_Campaign_ID());
		//order.
		order.setC_Project_ID(project.getC_Project_ID());
		order.setDescription(project.getName());
		Timestamp ts = project.getDateContract();
		if (ts != null)
			order.setDateOrdered (ts);
		ts = project.getDateFinish();
		if (ts != null)
			order.setDatePromised (ts);
		//
		MBPartner bpartner = null;
		{
			order.setC_BPartner_ID(providerID);
			bpartner = new MBPartner(getCtx()	, providerID, get_TrxName());
			MUser[] contacts = bpartner.getContacts(true);
			if (contacts.length>0)
				order.setAD_User_ID(contacts[0].getAD_User_ID());
			order.setSalesRep_ID(Env.getContextAsInt(Env.getCtx(), "#AD_User_ID"));
			int c_DocType_ID = 0;
				c_DocType_ID = bpartner.get_ValueAsInt("C_DoctypePO_ID")<=0?  MDocType.getDocType("POO"):
					bpartner.get_ValueAsInt("C_DoctypePO_ID");
			order.setC_DocTypeTarget_ID(c_DocType_ID);
			order.setIsSOTrx(false);
		}
		order.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
		//
		order.setM_Warehouse_ID(project.getM_Warehouse_ID());
		{        	
			order.setM_PriceList_ID(bpartner.getPO_PriceList_ID());
			order.setC_PaymentTerm_ID(bpartner.getPO_PaymentTerm_ID());
		}
			order.setUser1_ID(project.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));
		order.setC_Activity_ID(project.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
		order.setDescription(project.getDescription());
		order.set_ValueOfColumn("LG_Route_ID", project.get_ValueAsInt("LG_Route_ID"));

		order.set_ValueOfColumn("DocumentoDeTransporte", project.get_Value("DocumentoDeTransporte"));
		order.set_ValueOfColumn("CodigoDeDeclaracion", project.get_Value("CodigoDeDeclaracion"));
		order.set_ValueOfColumn("ReferenciaDeDeclaracion", project.get_Value("ReferenciaDeDeclaracion"));
		order.set_ValueOfColumn("Provider", project.get_Value("Provider"));
		order.set_ValueOfColumn("ProviderPO", project.get_Value("ProviderPO"));

		order.saveEx();
		return order;
	}
	private MOrderLine createpoLine(MOrder purchaseOrder, MOrderLine orgLine)
	{
		
		MOrderLine poLine = new MOrderLine(purchaseOrder);
		poLine.setC_Charge_ID(orgLine.getC_Charge_ID());
		poLine.setQty(orgLine.getQtyEntered());
		poLine.setPrice(Env.ZERO);
		poLine.set_ValueOfColumn("c_invoiceline_PO_ID", orgLine.getC_OrderLine_ID());
		MBPartner bpartner = (MBPartner)purchaseOrder.getC_BPartner();
		poLine.setPrice(purchaseOrder.getM_PriceList_ID());
		poLine.setC_Project_ID(orgLine.getC_Project_ID());
		poLine.setC_Activity_ID(orgLine.getC_Activity_ID());
		poLine.setUser1_ID(orgLine.getUser1_ID());
		poLine.saveEx();                    

		return poLine;
	}

}