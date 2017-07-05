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

import org.compiere.model.MBPartner;
import org.compiere.model.MDiscountSchemaLine;
import org.compiere.model.MOpportunity;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRequest;
import org.compiere.model.MUOMConversion;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.shw.model.MLGProductPriceRate;
import org.shw.model.MLGProductPriceRateLine;
import org.shw.model.X_LG_Request_ProductPriceRate;
import org.shw.model.X_R_Request_Product;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class CreateQuotationFromSalesOpportunity  extends SvrProcess
{	

	int p_DocType_ID = 0;
	int P_User1_ID = 0;
	MOpportunity opp = null;
	BigDecimal qty = Env.ZERO;
	BigDecimal TotalLines = Env.ZERO;
	BigDecimal TotalProduct = Env.ZERO;
	
	private MProductPricing	m_productPrice = null;
			
    
    @Override    
    protected void prepare()
    {    	
    	ProcessInfoParameter[] parameters = getParameter();
		for (ProcessInfoParameter para : parameters) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("C_DocType_ID"))
				p_DocType_ID = para.getParameterAsInt();
			else if (name.equals("User1_ID"))
				P_User1_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

    }
    
      
    @Override
    protected String doIt() throws Exception
    {
        opp = new MOpportunity(getCtx(), getRecord_ID(), get_TrxName());
    	MOrder quotation = new MOrder(getCtx(), 0, get_TrxName());
    	quotation.setAD_Org_ID(opp.getAD_Org_ID());
    	quotation.setC_Campaign_ID(opp.getC_Campaign_ID());
    	quotation.setDateOrdered(Env.getContextAsDate(getCtx(), "#Date"));

    	MBPartner bpartner = null;
    	quotation.setC_BPartner_ID(opp.getC_BPartner_ID());
    	bpartner = (MBPartner)opp.getC_BPartner();
    	quotation.setAD_User_ID(opp.getAD_User_ID());
    	quotation.setSalesRep_ID(opp.getSalesRep_ID());
    	quotation.setC_DocTypeTarget_ID(p_DocType_ID);
    	quotation.setIsSOTrx(true);
    	quotation.setC_BPartner_Location_ID(bpartner.getPrimaryC_BPartner_Location_ID());
    	//
    	quotation.setM_Warehouse_ID(1000019);
    	quotation.setM_PriceList_ID(bpartner.getM_PriceList_ID());
    	quotation.setC_PaymentTerm_ID(bpartner.getC_PaymentTerm_ID());
    	quotation.setUser1_ID(P_User1_ID);
    	quotation.setC_Activity_ID(bpartner.get_ValueAsInt(MOrder.COLUMNNAME_C_Activity_ID));
    	quotation.setDescription(opp.getDescription());
    	quotation.setC_Opportunity_ID(opp.getC_Opportunity_ID());
    	quotation.saveEx();
    	createLinesFromPriceRate(quotation);
    	createLinesFromRequestProducts(quotation);
    	opp.setOpportunityAmt(quotation.getGrandTotal());
    	opp.saveEx();

    	return "";
    }
    
    private Boolean createLinesFromPriceRate(MOrder quotation)
    {
    	String whereClause = " lg_request_ProductPriceRate.r_request_ID in (select r_request_ID from r_request where c_opportunity_ID = ?)";
    	List<X_LG_Request_ProductPriceRate> reqs = new Query(getCtx(), X_LG_Request_ProductPriceRate.Table_Name, whereClause, get_TrxName())
    			.setParameters(opp.getC_Opportunity_ID())
    			.list();
    	for (X_LG_Request_ProductPriceRate reqppr:reqs)
    	{
    		MLGProductPriceRate ppr = new MLGProductPriceRate(getCtx(),reqppr.getLG_ProductPriceRate_ID(),get_TrxName());
    		MRequest req = new MRequest(getCtx(), reqppr.getR_Request_ID(), get_TrxName());
    		MOrderLine oLine = new MOrderLine(quotation);
    		oLine.setM_Product_ID(ppr.getM_Product_ID());
    		oLine.set_ValueOfColumn("LG_ProductPriceRate_ID", ppr.getLG_ProductPriceRate_ID());
    		if (req.get_ValueAsInt("C_UOM_Volume_ID") >0)
    			oLine.setC_UOM_ID(req.get_ValueAsInt("C_UOM_Volume_ID"));
    		else 
    			oLine.setC_UOM_ID(100);  
    		oLine.setC_Tax_ID(1000009);
    		BigDecimal vol = Env.ZERO;
    		if (ppr.getLG_Route().getLG_TransportType().equals("2") ||ppr.getLG_Route().getLG_TransportType().equals("4"))
    			oLine.setQty(Env.ONE);
    		else
    		{
    			qty = (BigDecimal)req.get_Value("Volume");
    			 vol = MUOMConversion.convert(oLine.getC_UOM_ID(), ppr.getC_UOM_Volume_ID(), qty, true);
     			oLine.setQty(vol);
    		}
    		BigDecimal price = getPrice(ppr, oLine, vol);
    		oLine.setPrice(price);
    		oLine.saveEx();
    		TotalLines = TotalLines.add(oLine.getLineNetAmt());
    		TotalProduct = TotalProduct.add(oLine.getLineNetAmt());
    		createChargeLines(ppr, quotation, (BigDecimal)req.get_Value("CumulatedQty"));
    		createProportionalCharge(ppr, quotation);
    	}
    	return true;
    }
    
    private BigDecimal getPrice(MLGProductPriceRate ppr, MOrderLine oLine, BigDecimal qty)
    {
    	String whereClause = "lg_ProductPriceRate_ID=? and M_Product_0_ID is null";
    	BigDecimal price = Env.ZERO;
    	//BigDecimal vol = MUOMConversion.convert(oLine.getC_UOM_ID(), ppr.getC_UOM_Volume_ID(), qty, true);
    	List<MLGProductPriceRateLine> lines = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClause, get_TrxName())
    			.setParameters(ppr.getLG_ProductPriceRate_ID())
    			.setOrderBy("BreakValueVolume Asc")
    			.list();
    	whereClause = "(C_Bpartner_ID = ? OR C_BPartner_ID is null) and (M_Product_ID =? OR M_Product_ID is null)";
    	ArrayList<Object> params = new ArrayList<>();
    	params.add(ppr.getM_Product_ID());
    	params.add(oLine.getC_Order().getC_BPartner_ID());
    	MDiscountSchemaLine dsl = new Query(getCtx(), MDiscountSchemaLine.Table_Name, whereClause, get_TrxName())
    			.setParameters(params)
    			.setOrderBy("C_Bpartner_ID, M_Product_ID")
    			.first();
    	BigDecimal marge = new BigDecimal(20);
    	if (dsl != null)
    		marge = dsl.getLimit_Discount();
    	
    	if (lines.isEmpty())
    	{
    		BigDecimal salesprice = ppr.getPriceLimit();
    		if (marge.compareTo(Env.ZERO) !=0)
    		{
    			marge = marge.divide(Env.ONEHUNDRED).add(Env.ONE);
    			salesprice = salesprice.multiply(marge);
    		}    		
    		return salesprice;
    	}
    	for (MLGProductPriceRateLine line:lines)
    	{
    		if (qty.compareTo(line.getBreakValueVolume())<=0)
    		{
    			price = (BigDecimal)line.get_Value("priceVolume");
    			break;
    		}
    	}
    	return price;
    }
    private Boolean createChargeLines(MLGProductPriceRate ppr, MOrder quotation, BigDecimal qty)
    {
    	String sqlProduct = "M_Product.m_product_ID In (select m_product_0_ID from lg_ProductPriceRateLine " + 
    " where lg_ProductPriceRate_ID=? and m_product_0_ID is not null AND lg_SurChargeType in ('FLAT','SCALED'))";
    	int[] prod_IDs = new Query(getCtx(), MProduct.Table_Name	, sqlProduct, get_TrxName())
    			.setParameters(ppr.getLG_ProductPriceRate_ID())
    			.getIDs();
    	ArrayList<Object> params = new ArrayList<Object>();
    	for (int m_product_ID : prod_IDs)
    	{    		
    		String whereClause = "lg_ProductPriceRate_ID=? and m_product_0_ID=? ";
    		params.clear();
    		params.add(ppr.getLG_ProductPriceRate_ID());
    		params.add(m_product_ID);
    		BigDecimal price = Env.ZERO;
    		//BigDecimal vol = MUOMConversion.convert(oLine.getC_UOM_ID(), ppr.getC_UOM_Volume_ID(), qty, true);
    		List<MLGProductPriceRateLine> lines = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClause, get_TrxName())
    				.setParameters(params)
    				.setOrderBy("BreakValueVolume Asc")
    				.list();
    		for (MLGProductPriceRateLine line:lines)
    		{
    			if (qty.compareTo(line.getBreakValueVolume())<=0)
    			{
    				price = (BigDecimal)line.get_Value("priceVolume");
    				MOrderLine oLine = new MOrderLine(quotation);
        			oLine.setM_Product_ID(ppr.get_ValueAsInt("M_Product_0_ID"));
        			if (line.get_ValueAsString("lg_SurChargeType").equals("FLAT"))
        			{

        				oLine.setPrice(price);
        				oLine.setQty(Env.ONE);
        			}
        			else 
        			{
        				oLine.setQty(Env.ONE);
        				oLine.setPrice(price.multiply(qty));
        			}
        			oLine.set_ValueOfColumn("LG_ProductPriceRate_ID", ppr.getLG_ProductPriceRate_ID());
        			oLine.setC_Tax_ID(1000009);
    				oLine.setM_Product_ID(line.get_ValueAsInt("M_Product_0_ID"));
        			oLine.saveEx();
        			TotalLines = TotalLines.add(oLine.getLineNetAmt());
    				break;
    			}   			
    		}
    	}
    	return true;
    }
    
    private Boolean createProportionalCharge(MLGProductPriceRate ppr, MOrder quotation)
    {

    	String sqlProduct = "M_Product.m_product_ID In (select m_product_0_ID from lg_ProductPriceRateLine " + 
    " where lg_ProductPriceRate_ID=? and m_product_0_ID is not null AND lg_SurChargeType in ('PROP'))";
    	int[] prod_IDs = new Query(getCtx(), MProduct.Table_Name	, sqlProduct, get_TrxName())
    			.setParameters(ppr.getLG_ProductPriceRate_ID())
    			.getIDs();
    	ArrayList<Object> params = new ArrayList<Object>();
    	for (int m_product_ID : prod_IDs)
    	{    		
    		String whereClause = "lg_ProductPriceRate_ID=? and m_product_0_ID=? ";
    		params.clear();
    		params.add(ppr.getLG_ProductPriceRate_ID());
    		params.add(m_product_ID);
    		BigDecimal price = Env.ZERO;
    		//BigDecimal vol = MUOMConversion.convert(oLine.getC_UOM_ID(), ppr.getC_UOM_Volume_ID(), qty, true);
    		List<MLGProductPriceRateLine> lines = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClause, get_TrxName())
    				.setParameters(params)
    				.setOrderBy("BreakValueVolume Asc")
    				.list();
    		for (MLGProductPriceRateLine line:lines)
    		{
    			if (qty.compareTo(line.getBreakValueVolume())<=0)
    			{
    				BigDecimal percent = (BigDecimal)line.get_Value("Percent");
    				percent = percent.divide(Env.ONEHUNDRED);
    				price = TotalLines.multiply(percent);
    				MOrderLine oLine = new MOrderLine(quotation);
    				oLine.setM_Product_ID(line.get_ValueAsInt("M_Product_0_ID"));
/*
    				//	Set Price if Actual = 0
    				MPriceList pl = new MPriceList(getCtx(), quotation.getM_PriceList_ID(), get_TrxName());
    				MPriceListVersion plv = pl.getPriceListVersion(quotation.getDateOrdered());
    				String sql_ProductPrice = "select count(*) from m_productprice where m_product_ID=? and m_pricelist_version_id=?";
    				//ArrayList<Object> params = new ArrayList<Object>();
    				params.clear();
    				params.add(oLine.getM_Product_ID());
    				params.add( plv.getM_PriceList_Version_ID());
    				int no = DB.getSQLValueEx(get_TrxName(), sql_ProductPrice, params);
    				if (no == 0)
    				{
    					X_M_ProductPrice pp = new X_M_ProductPrice(getCtx(), 0, get_TrxName());
    					pp.setM_PriceList_Version_ID(plv.getM_PriceList_Version_ID());
    					pp.setM_Product_ID(oLine.getM_Product_ID());
    					pp.set_ValueOfColumn("LG_Route_ID", 1000018);
    					pp.saveEx();
    				}*/
    				oLine.setPrice(price);
    				oLine.setQty(Env.ONE);
    				oLine.set_ValueOfColumn("LG_ProductPriceRate_ID", ppr.getLG_ProductPriceRate_ID());
    				oLine.setC_Tax_ID(1000009);
    				oLine.saveEx();
    				break;
    			}   			
    		}
    	}
    	return true;
    
    }
	private MProductPricing getProductPricing (int M_PriceList_ID,MOrderLine oLine)
	{
		m_productPrice = new MProductPricing (oLine.getM_Product_ID(), 
				oLine.getParent().getC_BPartner_ID(), oLine.getQtyOrdered(), true, get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
		m_productPrice.setPriceDate(oLine.getParent().getDateOrdered());
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice
	

	public void setPrice (int M_PriceList_ID, MOrderLine oLine)
	{
		if (oLine.getM_Product_ID() == 0)
			return;
		getProductPricing (M_PriceList_ID,oLine);
		
	}	//	setPrice
	  private Boolean createLinesFromRequestProducts(MOrder quotation)
	    {
	    	String whereClause = " R_Request_Product.r_request_ID in (select r_request_ID from r_request where c_opportunity_ID = ?)";
	    	List<X_R_Request_Product> reqs = new Query(getCtx(), X_R_Request_Product.Table_Name, whereClause, get_TrxName())
	    			.setParameters(opp.getC_Opportunity_ID())
	    			.list();
	    	for (X_R_Request_Product reqproduct:reqs)
	    	{
	    		MOrderLine oLine = new MOrderLine(quotation);
	    		oLine.setM_Product_ID(reqproduct.getM_Product_ID());
	    		oLine.setC_UOM_ID(reqproduct.getM_Product().getC_UOM_ID());
	    		oLine.setQty((BigDecimal)reqproduct.get_Value("QtyOrdered"));
	    		oLine.setPrice();
	    		//setPrice(quotation.getM_PriceList_ID(), oLine);
	    		oLine.saveEx();
	    	}
	    	return true;
	    }
	    	
	    		


}
