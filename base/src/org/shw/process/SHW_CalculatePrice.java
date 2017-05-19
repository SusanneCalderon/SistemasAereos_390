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
import java.util.Calendar;

import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;
import org.shw.model.MLGProductPriceRate;
import org.shw.model.MLGProductPriceRateLine;
import org.shw.model.MLGRoute;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_CalculatePrice  extends SvrProcess
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
    	StringBuffer description = new StringBuffer("");
    	BigDecimal	calculatedPrice = Env.ZERO;
    	MOrderLine	oLine 			= null;

    	oLine = new MOrderLine(getCtx(), getRecord_ID(), get_TrxName());
    	int route_ID= oLine.getParent().get_ValueAsInt("LG_Route_ID");
    	if (route_ID != 0)
    	{
        	MLGRoute route = new MLGRoute(getCtx(), route_ID, get_TrxName());        	
        	calculatedPrice = route.calculatePrice(
        			oLine.getM_Product_ID(),							// Producto
        			oLine.getParent().getC_BPartner_ID(),				// BP
        			oLine.getParent().getDateOrdered(),					// Fecha de
        			oLine.getParent().getDateOrdered(),					// Fecha a    			
        			p_C_UOM_Volumen_ID, p_qtyVolumen,					// UOM_ID y cantidad de volumen
        			p_C_UOM_Weight_ID, p_qtyWeight,						// UOM_ID y cantidad de peso
        			description, oLine);

        	oLine.setPriceActual(calculatedPrice);
        	oLine.set_ValueOfColumn("Comments", description.toString());
        	//oLine.setDescription(description.toString());
        	oLine.saveEx();
    	}
    	return "";
    }
    
}
