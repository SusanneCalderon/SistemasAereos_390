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

import org.shw.model.X_LG_Request_ProductPriceRate;
import org.shw.model.X_R_Request_Product;

/** Generated Process for (SB_ProductPriceFateFinder)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class SB_ProductPriceFateFinder extends SB_ProductPriceFateFinderAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		for (int LG_ProductPriceRate_ID: getSelectionKeys())
		{
			X_LG_Request_ProductPriceRate requestppr = new X_LG_Request_ProductPriceRate(getCtx(), 0, get_TrxName());
			requestppr.setR_Request_ID(getRecord_ID());
			requestppr.setLG_ProductPriceRate_ID(LG_ProductPriceRate_ID);
			requestppr.saveEx();
		}
		return "";
	}
}