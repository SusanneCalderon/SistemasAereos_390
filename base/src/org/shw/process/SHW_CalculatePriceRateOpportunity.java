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
import java.util.Properties;
import java.util.logging.Level;

import javax.sql.RowSet;

import org.compiere.apps.AEnv;
import org.compiere.apps.AWindow;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOpportunity;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MQuery;
import org.compiere.model.MRequest;
import org.compiere.model.MRequestType;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MUOM;
import org.compiere.model.MUOMConversion;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportCtl;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.ValueNamePair;
import org.eevolution.model.MSalesHistory;
import org.eevolution.model.X_T_BOMLine;
import org.shw.model.MLGProductPriceRate;
import org.shw.model.MLGProductPriceRateLine;
import org.shw.model.MLGRoute;
import org.shw.model.X_T_ProductPriceRateLine;

/**
 *	Generate Invoices
 *	
 *  @author SHW
 *  @version $Id: SHW_InvoiceGenerate.java,v 1.2 2015/01/24 00:51:01 mc Exp $
 */
public class SHW_CalculatePriceRateOpportunity extends SvrProcess
{
	private int			p_c_UOM_Volumn_ID 						= 0;
	private int			p_c_UOM_Weight_ID 						= 0;
	private Boolean		p_onlyreport 							= false;
	private Boolean		p_createNewPricerate 					= false;
	private Boolean		p_createQuotation	 					= false;
	private Boolean		p_CrearCotazacion_TarifaCompra	 		= false;
	private int			p_M_PriceList_ID						= 0;
	private	int			p_C_BPartner_ID							= 0;
	private BigDecimal	p_qtyVolumn								= Env.ZERO;
	private BigDecimal	p_qtyWeight								= Env.ZERO;
	private BigDecimal	p_List_Discount							= Env.ZERO;
	private int 		p_C_Project_ID							= 0;

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
			if (name.equals(MLGProductPriceRateLine.COLUMNNAME_C_UOM_Volume_ID))
				p_c_UOM_Volumn_ID = para.getParameterAsInt();
			else if (name.equals(MLGProductPriceRateLine.COLUMNNAME_C_UOM_Weight_ID))
				p_c_UOM_Weight_ID = para.getParameterAsInt();
			else if (name.equals("onlyreport"))
				p_onlyreport = para.getParameterAsBoolean();
			else if (name.equals("createNewPricerate"))
				p_createNewPricerate = para.getParameterAsBoolean();
			else if (name.equals(MPriceList.COLUMNNAME_M_PriceList_ID))
				p_M_PriceList_ID = para.getParameterAsInt();
			else if (name.equals(MBPartner.COLUMNNAME_C_BPartner_ID))
				p_C_BPartner_ID = para.getParameterAsInt();
			else if (name.equals("quantityvolumn"))
				p_qtyVolumn = para.getParameterAsBigDecimal();
			else if (name.equals("quantityweight"))
				p_qtyWeight = para.getParameterAsBigDecimal();
			else if (name.equals("createQuotation"))
				p_createQuotation = para.getParameterAsBoolean();
			else if (name.equals(MProject.COLUMNNAME_C_Project_ID))
				p_C_Project_ID = para.getParameterAsInt();
			else if (name.equals("CrearCotazaci�n_TarifaCompra"))
				p_CrearCotazacion_TarifaCompra = para.getParameterAsBoolean();
			else if (name.equals("List_Discount"))
				p_List_Discount = para.getParameterAsBigDecimal();
			

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
				" AND T_Selection.T_Selection_ID=LG_ProductPriceRate.LG_ProductPriceRate_ID)";
		List<MLGProductPriceRate>m_records = new Query(getCtx(), MLGProductPriceRate.Table_Name, whereClause, get_TrxName())
		.setParameters(getAD_PInstance_ID())
		.setClient_ID()
		.list();
		if(m_records.size()> 1 && p_createNewPricerate)
			return "Solamente hay que escoger una tarifa";

		if (p_onlyreport)
		{
			for (MLGProductPriceRate pr:m_records)
			{
		    	Boolean found = false;
				for (MLGProductPriceRateLine ppl:pr.getLinesOrderByVolumn(""))
				{
					X_T_ProductPriceRateLine tp = new X_T_ProductPriceRateLine(getCtx(), 0, get_TrxName());
					tp.setAD_PInstance_ID(getAD_PInstance_ID());
					tp.setAD_Org_ID(pr.getAD_Org_ID());
					tp.setLG_ProductPriceRate_ID(ppl.getLG_ProductPriceRate_ID());
					tp.setLG_ProductPriceRateLine_ID(ppl.getLG_ProductPriceRateLine_ID());
					tp.setLG_Route_ID(pr.getLG_Route_ID());
					tp.setM_Product_ID(ppl.getM_Product_ID());
					tp.setM_PriceList_ID(pr.get_ValueAsInt(MPriceList.COLUMNNAME_M_PriceList_ID));
					tp.setBreakValueVolume(ppl.getBreakValueVolume());
					tp.setBreakValue(ppl.getBreakValue());
					tp.setBreakValueWeight(ppl.getBreakValueWeight());
					tp.setC_UOM_Volume_ID(ppl.getC_UOM_Volume_ID());
					tp.setC_UOM_Weight_ID(ppl.getC_UOM_Weight_ID());
					tp.setpriceVolume(ppl.getpriceVolume());
					tp.setquantityvolumn(p_qtyVolumn);
					tp.setquantityweight(p_qtyWeight);
					if (found || ppl.getM_Product_ID() != pr.getM_Product_ID())
						tp.setresultVolumn(Env.ZERO);
					else
					{
				    	BigDecimal qtyconverted = p_qtyVolumn;
				    	if (ppl.getC_UOM_Volume_ID() != p_c_UOM_Volumn_ID)
							qtyconverted = MUOMConversion.convert(p_c_UOM_Volumn_ID, ppl.getC_UOM_Volume_ID(), p_qtyVolumn, true);
						if (qtyconverted.compareTo(ppl.getBreakValueVolume()) == -1)
							tp.setresultVolumn(Env.ZERO);
						else
						{
							BigDecimal price = ppl.getpriceVolume().multiply(qtyconverted);
							tp.setresultVolumn(price.compareTo(ppl.getMinimumAmt())>=0?price:ppl.getMinimumAmt());
							found = true;
						}
						
					}
					tp.saveEx();
				}

				//commitEx();
				for (MLGProductPriceRateLine ppl:pr.getLinesOrderByWeight(""))
				{
			    	found = false;
					X_T_ProductPriceRateLine tp = new Query(getCtx(), X_T_ProductPriceRateLine.Table_Name, 
							"LG_ProductPriceRateLine_ID=? and ad_pinstance_ID=?", get_TrxName())
					.setParameters(ppl.getLG_ProductPriceRateLine_ID(), getAD_PInstance_ID())
					.first();
					if (tp == null)
						return "Algo malo";
					if (found|| ppl.getM_Product_ID() != pr.getM_Product_ID()){
						tp.setresultWeight(Env.ZERO);
						continue;
					}
					tp.setPriceWeight(ppl.getPriceWeight());
			    	BigDecimal qtyconverted = p_qtyWeight;
			    	if (ppl.getC_UOM_Weight_ID() != p_c_UOM_Weight_ID)
						qtyconverted = MUOMConversion.convert(p_c_UOM_Weight_ID, ppl.getC_UOM_Weight_ID(), p_qtyWeight, true);
			    	if (qtyconverted == null)
			    		continue;
			    	if (qtyconverted.compareTo(ppl.getBreakValueWeight())>= 1)
					{
						BigDecimal price = ppl.getPriceWeight().multiply(qtyconverted);
						tp.setresultWeight(price.compareTo(ppl.getMinimumAmt())>=0?price:ppl.getMinimumAmt());
						found = true;
					}
					else
						tp.setPriceWeight(Env.ZERO);
					tp.saveEx();
				}

			}
			commitEx();
			print();
		}
		else if (p_createNewPricerate)
		{
			String whereClauseWindow  = "lg_productpricerate_ID in (";

			List<MLGProductPriceRate> m_pricerates = null;
			m_pricerates = new ArrayList<MLGProductPriceRate>();
			MLGProductPriceRate pr = m_records.get(0);
			MLGProductPriceRate prNew = MLGProductPriceRate.copyFrom(m_records.get(0),Env.getContextAsDate(Env.getCtx(), 0, "#Date")
					, true,p_C_BPartner_ID > 0?p_C_BPartner_ID:0,  get_TrxName());
			prNew.set_ValueOfColumn(MPriceList.COLUMNNAME_M_PriceList_ID, p_M_PriceList_ID);
			prNew.set_ValueOfColumn("M_PriceList_ID", p_M_PriceList_ID);
			prNew.saveEx();
			prNew.copyLinesFrom(pr);
			whereClauseWindow = whereClauseWindow + pr.getLG_ProductPriceRate_ID()+ ",";
			whereClauseWindow = whereClauseWindow.substring(0, whereClauseWindow.length() -1) + ")";
			MQuery query = new MQuery("");
			MTable table = new MTable(getCtx(), MLGProductPriceRate.Table_ID, get_TrxName());
			query.addRestriction(whereClauseWindow);
			query.setRecordCount(1);
			int AD_WindowNo = 1000026;
			commitEx();
			zoom (AD_WindowNo, query);
		}
		
		else if (p_createQuotation)
		{
			MOpportunity opp = new MOpportunity(getCtx(), getRecord_ID(), get_TrxName());
			MLGProductPriceRate pr = m_records.get(0);
			if (!pr.getLG_Route().getLG_RouteType().equals("O")|| !pr.get_ValueAsBoolean("isStructured"))
				return "Tiene que escoger una ruta propia y con tarifa estructurada para esta función";
			if (p_c_UOM_Volumn_ID == 0 && p_c_UOM_Weight_ID == 0)
				return "Falta la definición de los parametros para volumen o peso";
			//MProject project = new MProject(getCtx(), p_C_Project_ID, get_TrxName());
	        MOrder order = new MOrder(getCtx()	, 0, get_TrxName());
	        order.setAD_Org_ID(opp.getAD_Org_ID());

	    	int c_bpartner_ID = Env.getContextAsInt(getCtx(), "@C_BPartner_ID@");
	        order.setC_Campaign_ID(opp.getC_Campaign_ID());
	        order.setC_Opportunity_ID(getRecord_ID());
	        //order.
	        //order.setC_Project_ID(opp.getC_Project_ID());
	        //order.setDescription(opp.getName());
	        Timestamp ts = Env.getContextAsDate(getCtx(), "#Date");
	        if (ts != null)
	            order.setDateOrdered (ts);
	        //ts = project.getDateFinish();
	        //if (ts != null)
	           order.setDatePromised (ts);
	        //
	        MBPartner bpartner = null;
	        	order.setC_BPartner_ID(opp.getC_BPartner_ID());
	            bpartner = (MBPartner)opp.getC_BPartner();
	            order.setAD_User_ID(opp.getAD_User_ID());
	            order.setSalesRep_ID(opp.getSalesRep_ID());
	            order.setIsSOTrx(true);
	            String whereClauseDocType = "docbasetype = 'SOO' and docsubtypeso = 'ON'";
	            int c_DocType_ID = new Query(getCtx(), MDocType.Table_Name, whereClauseDocType, get_TrxName())
	            	.setClient_ID()
	            	.setOnlyActiveRecords(true)
	            	.firstId();
	       
	            order.setC_DocTypeTarget_ID(c_DocType_ID);
	        	order.setIsSOTrx(true);
	        order.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
	        //
	        order.setM_Warehouse_ID(opp.get_ValueAsInt("M_Warehouse_ID"));
	        if (order.isSOTrx())
	        	order.setM_PriceList_ID(bpartner.getM_PriceList_ID());
	        else order.setM_PriceList_ID(bpartner.getPO_PriceList_ID());
	        order.setC_PaymentTerm_ID(bpartner.getC_PaymentTerm_ID());
	        //
	        //order.setC_DocTypeTarget_ID(MDocType.DOCSUBTYPESO_Proposal);
	        order.setUser1_ID(opp.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));
	        order.setC_Activity_ID(opp.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
	        order.setDescription(opp.getDescription());
	       order.set_ValueOfColumn("LG_Route_ID", pr.get_ValueAsInt("LG_Route_ID"));
	       opp.set_ValueOfColumn("LG_Route_ID", pr.get_ValueAsInt("LG_Route_ID"));
	       opp.set_ValueOfColumn("LG_ProductPriceRate_ID", pr.getLG_ProductPriceRate_ID());
	       //order.set_ValueOfColumn("DocumentoDeTransporte", opp.get_Value("DocumentoDeTransporte"));
	       //order.set_ValueOfColumn("CodigoDeDeclaracion", project.get_Value("CodigoDeDeclaracion"));
	       //order.set_ValueOfColumn("ReferenciaDeDeclaracion", project.get_Value("ReferenciaDeDeclaracion"));
	       //order.set_ValueOfColumn("Provider", project.get_Value("Provider"));
	       //order.set_ValueOfColumn("ProviderPO", project.get_Value("ProviderPO"));
	       order.saveEx();
	       opp.saveEx();
	       Boolean found = false;
	       for (MLGProductPriceRateLine ppl:pr.getLines())
	       {
	    	   if (ppl.getM_Product_ID() != pr.getM_Product_ID())
	    	   {
	    		   MOrderLine oLine = new MOrderLine(order);
	    		   oLine.setM_Product_ID(ppl.getM_Product_ID());
	    		   oLine.setQty(Env.ONE);
	    		   oLine.setC_UOM_ID(ppl.getM_Product().getC_UOM_ID());
	    		   oLine.setPrice(ppl.getPriceStd());
	    		   oLine.set_ValueOfColumn(MLGProductPriceRate.COLUMNNAME_LG_ProductPriceRate_ID, pr.getLG_ProductPriceRate_ID());
	    		   oLine.set_ValueOfColumn(MLGRoute.COLUMNNAME_LG_Route_ID, pr.getLG_Route_ID());
	    		   oLine.saveEx();
	    	   }
	    	   else
	    	   {
	    		   if (found)
	    			   continue;
	    		   BigDecimal priceVolumn = pr.calculateVolumePrice(p_c_UOM_Volumn_ID, p_qtyVolumn);
	    		   BigDecimal priceWeight = pr.calculateWeightPrice(p_c_UOM_Weight_ID, p_qtyWeight);
	    		   priceVolumn = priceVolumn.compareTo(ppl.getMinimumAmt()) == -1? ppl.getMinimumAmt(): priceVolumn;
	    		   priceWeight = priceWeight.compareTo(ppl.getMinimumAmt()) == -1? ppl.getMinimumAmt(): priceWeight;
	    		   BigDecimal price = priceVolumn.compareTo(priceWeight)==-1?priceWeight:priceVolumn;
	    		   MOrderLine oLine = new MOrderLine(order);
	    		   oLine.setM_Product_ID(ppl.getM_Product_ID());
	    		   oLine.setQty(Env.ONE);
	    		   oLine.setC_UOM_ID(ppl.getM_Product().getC_UOM_ID());
	    		   oLine.setPrice(price);
	    		   oLine.saveEx();
	    		   found = true;
	    	   }	    		   
	       }
	        
	        return  "Documento creado: " + order.getDocumentNo();
	    
		}
		
		if (p_CrearCotazacion_TarifaCompra){
			MOpportunity opp = new MOpportunity(getCtx(), getRecord_ID(), get_TrxName());
			MLGProductPriceRate pr = m_records.get(0);
			if (pr.getLG_Route().getLG_RouteType().equals("O")&& pr.get_ValueAsBoolean("isStructured"))
				return "No se puede generar una OdC para ruta propia y tarifa estructurada";
			if (p_c_UOM_Volumn_ID == 0 && p_c_UOM_Weight_ID == 0)
				return "Falta la definición de los parametros para volumen o peso";
			String whereClauseBP = "c_bpartner_ID in (select c_bpartner_ID from LG_Route_Agent where lg_route_ID=?)";
			List<MBPartner> bps = new Query(getCtx(), MBPartner.Table_Name, whereClauseBP, get_TrxName())
			.setParameters(pr.getLG_Route_ID())
			.setOnlyActiveRecords(true)
			.list();
			for (MBPartner bpartner:bps)
			{
				//MProject project = new MProject(getCtx(), p_C_Project_ID, get_TrxName());
				MOrder order = new MOrder(getCtx()	, 0, get_TrxName());
				order.setAD_Org_ID(opp.getAD_Org_ID());

				order.setC_Campaign_ID(opp.getC_Campaign_ID());
				order.setC_Opportunity_ID(getRecord_ID());
				Timestamp ts = Env.getContextAsDate(getCtx(), "#Date");
				if (ts != null)
					order.setDateOrdered (ts);
				order.setDatePromised (ts);
				//
				order.setC_BPartner_ID(bpartner.getC_BPartner_ID());
				order.setSalesRep_ID(Env.getContextAsInt(getCtx(), "#AD_User_ID"));
				order.setIsSOTrx(false);
				int c_DocType_ID = bpartner.get_ValueAsInt("C_DoctypePO_ID");

				order.setC_DocTypeTarget_ID(c_DocType_ID);
				order.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
				//
				order.setM_Warehouse_ID(opp.get_ValueAsInt("M_Warehouse_ID"));
				if (order.isSOTrx())
					order.setM_PriceList_ID(bpartner.getM_PriceList_ID());
				else order.setM_PriceList_ID(bpartner.getPO_PriceList_ID());
				order.setC_PaymentTerm_ID(bpartner.getC_PaymentTerm_ID());
				order.setUser1_ID(opp.get_ValueAsInt(MOrder.COLUMNNAME_User1_ID));
				order.setC_Activity_ID(opp.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
				order.setDescription(opp.getDescription());
				order.set_ValueOfColumn("LG_Route_ID", pr.get_ValueAsInt("LG_Route_ID"));
				opp.set_ValueOfColumn("LG_Route_ID", pr.get_ValueAsInt("LG_Route_ID"));
				order.saveEx();
				opp.saveEx();
				Boolean found = false;
				for (MLGProductPriceRateLine ppl:pr.getLines())
				{
					if (ppl.getM_Product_ID() != pr.getM_Product_ID())
					{
						MOrderLine oLine = new MOrderLine(order);
						oLine.setM_Product_ID(ppl.getM_Product_ID());
						oLine.setQty(Env.ONE);
						oLine.setC_UOM_ID(ppl.getM_Product().getC_UOM_ID());
						oLine.setPrice(ppl.getPriceStd());
						oLine.set_ValueOfColumn(MLGProductPriceRate.COLUMNNAME_LG_ProductPriceRate_ID, pr.getLG_ProductPriceRate_ID());
						oLine.set_ValueOfColumn(MLGRoute.COLUMNNAME_LG_Route_ID, pr.getLG_Route_ID());
						oLine.saveEx();
					}
					else
					{
						if (found)
							continue;
						BigDecimal priceVolumn = pr.calculateVolumePrice(p_c_UOM_Volumn_ID, p_qtyVolumn);
						BigDecimal priceWeight = pr.calculateWeightPrice(p_c_UOM_Weight_ID, p_qtyWeight);
						priceVolumn = priceVolumn.compareTo(ppl.getMinimumAmt()) == -1? ppl.getMinimumAmt(): priceVolumn;
						priceWeight = priceWeight.compareTo(ppl.getMinimumAmt()) == -1? ppl.getMinimumAmt(): priceWeight;
						BigDecimal price = priceVolumn.compareTo(priceWeight)==-1?priceWeight:priceVolumn;
						MOrderLine oLine = new MOrderLine(order);
						oLine.setM_Product_ID(ppl.getM_Product_ID());
						oLine.setQty(Env.ONE);
						oLine.setC_UOM_ID(ppl.getM_Product().getC_UOM_ID());
						oLine.setPrice(price);
						oLine.saveEx();
						found = true;
					}	    		   
				}

				return  "Documento creado: " + order.getDocumentNo();

			}
		}



		return "";



	}	//	doIt

	void print() throws Exception {
		Language language = Language.getLoginLanguage(); // Base Language
		MPrintFormat pf = null;
		int pfid = 0;

		// get print format for client, else copy system to client
		RowSet pfrs = MPrintFormat.getAccessiblePrintFormats(
				MTable.getTable_ID(X_T_ProductPriceRateLine.Table_Name),
				-1, null);
		pfrs.next();
		pfid = pfrs.getInt("AD_PrintFormat_ID");

		if (pfrs.getInt("AD_Client_ID") != 0)
			pf = MPrintFormat.get(getCtx(), pfid, false);
		else
			pf = MPrintFormat.copyToClient(getCtx(), pfid, getAD_Client_ID());
		pfrs.close();

		if (pf == null)
			raiseError("Error: ", "No Print Format");

		pf.setLanguage(language);
		pf.setTranslationLanguage(language);
		// query
		MQuery query = new MQuery(X_T_ProductPriceRateLine.Table_Name);
		query.addRestriction(X_T_ProductPriceRateLine.COLUMNNAME_AD_PInstance_ID,
				MQuery.EQUAL, getAD_PInstance_ID(),
				null,
				null);


		PrintInfo info = new PrintInfo(
				X_T_ProductPriceRateLine.Table_Name,
				MTable.getTable_ID(X_T_ProductPriceRateLine.Table_Name),
				getRecord_ID());
		ReportEngine re = new ReportEngine(getCtx(), pf, query, info);

		ReportCtl.preview(re);
		// wait for report window to be closed as t_bomline
		// records are deleted when process ends
		//while (re.getView().isDisplayable()) {
		//	Env.sleep(1);
		//}
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


	private void raiseError(String string, String hint) throws Exception {
		String msg = string;
		ValueNamePair pp = CLogger.retrieveError();
		if (pp != null)
			msg = pp.getName() + " - ";
		msg += hint;
		throw new Exception(msg);
	}

	public String getParamenterInfo(String name) {
		final String sql = "SELECT ip.Info FROM AD_PInstance_Para ip"
				+ " WHERE ip.AD_PInstance_ID=? AND ip.ParameterName=?";
		return DB.getSQLValueString(get_TrxName(), sql, getProcessInfo()
				.getAD_PInstance_ID(), name);
	}

	public String getParamenterName(String columnName) {
		boolean trl = !Env.isBaseLanguage(Env.getCtx(), "AD_Process_Para");
		String sql = null;
		if (trl) {
			sql = "SELECT pp.Name FROM AD_Process_Para pp "
					+ "WHERE pp.IsActive='Y' "
					+ " AND pp.AD_Process_ID=? AND pp.ColumnName=?";
		} else {
			sql = "SELECT ppt.Name "
					+ "FROM AD_Process_Para pp , AD_Process_Para_Trl ppt "
					+ "WHERE pp.IsActive='Y'"
					+ " AND pp.AD_Process_Para_ID=ppt.AD_Process_Para_ID"
					+ " AND pp.AD_Process_ID=? AND pp.ColumnName=?";
		}
		return DB.getSQLValueString(get_TrxName(), sql, getProcessInfo()
				.getAD_Process_ID(), columnName);
	}






}	//	InvoiceGenerate
