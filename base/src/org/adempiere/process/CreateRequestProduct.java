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

package org.adempiere.process;

import java.math.BigDecimal;

import org.compiere.model.X_R_Request_Product;
import org.compiere.model.MRequest;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

/** Generated Process for (CreateRequestProduct)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class CreateRequestProduct extends CreateRequestProductAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{

		//MRequest request = new MRequest(getCtx(), getRecord_ID(), get_TrxName());
		for (int M_Product_ID: getSelectionKeys())
		{
			X_R_Request_Product requestProduct = new X_R_Request_Product(getCtx(), 0, get_TrxName());
			requestProduct.setR_Request_ID(getRecord_ID());
			requestProduct.setM_Product_ID(M_Product_ID);
			requestProduct.setValidFrom(getValidfrom());
			requestProduct.setIsMandatory(isMandatory());
			int C_BPartner_ID = getSelectionAsInt(M_Product_ID, "PPO_C_BPartner_ID");
			BigDecimal QtyOrdered = getSelectionAsBigDecimal(M_Product_ID, "P_QtyOrdered");
			requestProduct.set_ValueOfColumn("QtyOrdered",QtyOrdered);
			requestProduct.saveEx();
		}
		return "";
	}
}