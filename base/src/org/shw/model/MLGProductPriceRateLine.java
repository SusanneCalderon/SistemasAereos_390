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
package  org.shw.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MConversionRate;
import org.compiere.model.MOrder;
import org.compiere.model.MUOM;
import org.compiere.model.MUOMConversion;
import org.compiere.model.X_LG_ProductPriceRateLine;
import org.compiere.util.Env;

/**
 * 	
 *  @author Susanne Calderon
 *  
 */
public class MLGProductPriceRateLine extends X_LG_ProductPriceRateLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6181691633960939054L;

	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param W_MailMsg_ID id
	 *	@param trxName trx
	 */
	public MLGProductPriceRateLine (Properties ctx, int LG_ProductPriceRateLine_ID, String trxName)
	{
		super (ctx, LG_ProductPriceRateLine_ID, trxName);
	}	//	MLGProductPriceRate

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MLGProductPriceRateLine (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLGProductPriceRate
	

	public MLGProductPriceRateLine (MLGProductPriceRate pricerate)
	{
		this (pricerate.getCtx(), 0, pricerate.get_TrxName());
		if (pricerate.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setLG_ProductPriceRate_ID(pricerate.getLG_ProductPriceRate_ID());
		setLG_Route_ID(pricerate.getLG_Route_ID());
		setM_Product_ID(pricerate.getM_Product_ID());
		setPriceLimit(Env.ZERO);
		setPriceList(Env.ZERO);
		setPriceStd(Env.ZERO);
		setpriceVolume(Env.ZERO);
		setPriceWeight(Env.ZERO);
		setBreakValue(Env.ZERO);
		
	}	//	MOrderLine
	
	
	
	
	
}	//	MLGProductPriceRate
