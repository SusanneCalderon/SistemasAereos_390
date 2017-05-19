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
package org.compiere.process;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MRfQ;
import org.compiere.model.MRfQResponse;
import org.compiere.model.MRfQResponseLine;
import org.compiere.model.MRfQResponseLineQty;
import org.compiere.model.Query;
import org.compiere.util.DB;

/**
 * 	Create RfQ PO.
 *	Create purchase order(s) for the resonse(s) and lines marked as 
 *	Selected Winner using the selected Purchase Quantity (in RfQ Line Quantity) 
 *	
 *  @author Jorg Janke
 *  @version $Id: RfQCreatePO.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 *  
 *  @author Teo Sarca, teo.sarca@gmail.com
 *  	<li>BF [ 2892588 ] Create PO from RfQ is not setting correct the price fields
 *  		https://sourceforge.net/tracker/?func=detail&aid=2892588&group_id=176962&atid=879332
 */
public class RfQCreatePO extends SvrProcess
{
	/**	RfQ 			*/
	private int		p_C_RfQ_ID = 0;
	private int		p_C_DocType_ID = 0;
	private int 	p_M_Warehouse_ID =0;

	/**
	 * 	Prepare
	 */
	protected void prepare ()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = para[i].getParameterAsInt();
			else if (name.equals(MOrder.COLUMNNAME_M_Warehouse_ID))
				p_M_Warehouse_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_C_RfQ_ID = getRecord_ID();
	}	//	prepare

	/**
	 * 	Process.
	 * 	Create purchase order(s) for the resonse(s) and lines marked as 
	 * 	Selected Winner using the selected Purchase Quantity (in RfQ Line Quantity) . 
	 * 	If a Response is marked as Selected Winner, all lines are created 
	 * 	(and Selected Winner of other responses ignored).  
	 * 	If there is no response marked as Selected Winner, the lines are used.
	 *	@return message
	 */
	protected String doIt () throws Exception
	{
		MRfQ rfq = new MRfQ (getCtx(), p_C_RfQ_ID, get_TrxName());
		if (rfq.get_ID() == 0)
			throw new IllegalArgumentException("No RfQ found");
		log.info(rfq.toString());
		
		//	Complete 
		MRfQResponse[] responses = rfq.getResponses(true, true);

		log.config("#Responses=" + responses.length);
		if (responses.length == 0)
			throw new IllegalArgumentException("No completed RfQ Responses found");
		
		//	Winner for entire RfQ
		for (int i = 0; i < responses.length; i++)
		{
			MRfQResponse response = responses[i];
			if (!response.isSelectedWinner())
				continue;
			//
			MBPartner bp = new MBPartner(getCtx(), response.getC_BPartner_ID(), get_TrxName());
			log.config("Winner=" + bp);
			MOrder order = new MOrder (getCtx(), 0, get_TrxName());
			order.setIsSOTrx(false);
			if (p_C_DocType_ID != 0)
				order.setC_DocTypeTarget_ID(p_C_DocType_ID);
			else
				order.setC_DocTypeTarget_ID();
			order.setBPartner(bp);
			order.setC_BPartner_Location_ID(response.getC_BPartner_Location_ID());
			order.setSalesRep_ID(rfq.getSalesRep_ID());
			order.setM_Warehouse_ID(p_M_Warehouse_ID);
			order.setAD_Org_ID(order.getM_Warehouse().getAD_Org_ID());
			if (response.getDateWorkComplete() != null)
				order.setDatePromised(response.getDateWorkComplete());
			else if (rfq.getDateWorkComplete() != null)
				order.setDatePromised(rfq.getDateWorkComplete());
			order.setDocAction(MOrder.DOCACTION_Complete);
			order.saveEx();
			//
			MRfQResponseLine[] lines = response.getLines(false);
			for (int j = 0; j < lines.length; j++)
			{
				//	Respones Line
				MRfQResponseLine line = lines[j];
				if (!line.isActive())
					continue;
				MRfQResponseLineQty[] qtys = line.getQtys(false);
				//	Response Line Qty
				for (int k = 0; k < qtys.length; k++)
				{
					MRfQResponseLineQty qty = qtys[k];
					//	Create PO Lline for all Purchase Line Qtys
					if (qty.getRfQLineQty().isActive() && qty.getRfQLineQty().isPurchaseQty())
					{
						MOrderLine ol = new MOrderLine (order);
						ol.setM_Product_ID(line.getRfQLine().getM_Product_ID(), 
							qty.getRfQLineQty().getC_UOM_ID());
						ol.setDescription(line.getDescription());
						ol.setQty(qty.getRfQLineQty().getQty());
						BigDecimal price = qty.getNetAmt();
						ol.setPrice();
						ol.setPrice(price);
						if (ol.getM_Product_ID() != 0)
						{
							MProduct product = (MProduct)ol.getM_Product();
							if (product.get_ValueAsBoolean("ishomologacion"))
							{
								String lg = "es_SV";
								if (ol.getParent().getC_BPartner().getAD_Language() != null)
									lg = ol.getParent().getC_BPartner().getAD_Language();
								String sqlManufacturer = "select getManufacturers(m.m_product_ID, ?) from shw_manufacturer_bpartner m" +
										" where m_product_ID=?";
								String text = DB.getSQLValueString(null, sqlManufacturer, new Object[]{lg, product.getM_Product_ID()});
								ol.setDescription(text);
								ol.set_ValueOfColumn("ishomologacion", true);
								sqlManufacturer = "select count(*) from m_Product_manufacturer " +
										" where m_product_ID=? and EstadoHomogolacion in ('H')";

								int no = DB.getSQLValue(null, sqlManufacturer, new Object[]{lg, product.getM_Product_ID()});
								ol.set_ValueOfColumn("count_Manufacturer", no);
							}	
						}
							
						ol.saveEx();
					}
				}
			}
			response.setC_Order_ID(order.getC_Order_ID());
			response.saveEx();
			return order.getDocumentNo();
		}

		
		//	Selected Winner on Line Level
		int noOrders = 0;
		for (int i = 0; i < responses.length; i++)
		{
			MRfQResponse response = responses[i];
			MBPartner bp = null;
			MOrder order = null;
			//	For all Response Lines
			MRfQResponseLine[] lines = response.getLines(false);
			for (int j = 0; j < lines.length; j++)
			{
				MRfQResponseLine line = lines[j];
				if (!line.isActive() || !line.isSelectedWinner())
					continue;
				//	New/different BP
				if (bp == null || bp.getC_BPartner_ID() != response.getC_BPartner_ID())
				{
					bp = new MBPartner(getCtx(), response.getC_BPartner_ID(), get_TrxName());
					order = null;
				}
				log.config("Line=" + line + ", Winner=" + bp);
				//	New Order
				if (order == null)
				{
					order = new MOrder (getCtx(), 0, get_TrxName());
					order.setIsSOTrx(false);
					order.setC_DocTypeTarget_ID();
					order.setBPartner(bp);
					order.setC_BPartner_Location_ID(response.getC_BPartner_Location_ID());
					order.setSalesRep_ID(rfq.getSalesRep_ID());
					order.setM_Warehouse_ID(p_M_Warehouse_ID);
					order.setAD_Org_ID(order.getM_Warehouse().getAD_Org_ID());
					order.saveEx();
					noOrders++;
					addLog(0, null, null, order.getDocumentNo());
				}
				//	For all Qtys
				MRfQResponseLineQty[] qtys = line.getQtys(false);
				for (int k = 0; k < qtys.length; k++)
				{
					MRfQResponseLineQty qty = qtys[k];
					if (qty.getRfQLineQty().isActive() && qty.getRfQLineQty().isPurchaseQty())
					{
						MOrderLine ol = new MOrderLine (order);
						ol.setM_Product_ID(line.getRfQLine().getM_Product_ID(), 
							qty.getRfQLineQty().getC_UOM_ID());
						ol.setDescription(line.getDescription());
						ol.setQty(qty.getRfQLineQty().getQty());
						BigDecimal price = qty.getNetAmt();
						ol.setPrice();
						ol.setPrice(price);
						if (ol.getM_Product_ID() != 0)
						{
							MProduct product = (MProduct)ol.getM_Product();
							if (product.get_ValueAsBoolean("ishomologacion"))
							{
								String lg = "es_SV";
								if (ol.getParent().getC_BPartner().getAD_Language() != null)
									lg = ol.getParent().getC_BPartner().getAD_Language();
								String sqlManufacturer = "select getManufacturers(m.m_product_ID, ?) from shw_manufacturer_bpartner m" +
										" where m_product_ID=?";
								String text = DB.getSQLValueString(null, sqlManufacturer, new Object[]{lg, product.getM_Product_ID()});
								ol.setDescription(text);
								ol.set_ValueOfColumn("ishomologacion", true);
							}	
						}
						ol.saveEx();
					}
				}	//	for all Qtys
			}	//	for all Response Lines
			if (order != null)
			{
				response.setC_Order_ID(order.getC_Order_ID());
				response.saveEx();
			}
		}
		
		return "#" + noOrders;
	}	//	doIt
}	//	RfQCreatePO
