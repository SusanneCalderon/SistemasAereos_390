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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MOrderLine;
import org.compiere.model.MUOM;
import org.compiere.model.Query;
import org.compiere.model.*;
import org.compiere.util.Env;

/**
 * 	
 *  @author Susanne Calderon
 *  
 */
public class MLGRoute extends X_LG_Route
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
	public MLGRoute (Properties ctx, int LG_Route_ID, String trxName)
	{
		super (ctx, LG_Route_ID, trxName);
	}	//	MLGRoute

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MLGRoute (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLGRoute
	
	public Collection<MLGRoute> findRouteByCity(int City_from_ID, int City_To_ID, Timestamp movementdate)
	{
			StringBuffer whereClause = new StringBuffer();
			List<Object> params = new ArrayList<Object>();

			if (City_from_ID > 0)
			{  
				whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_LG_CityFrom_ID).append("=?");
				params.add(City_from_ID);
			}		
			else if (City_To_ID > 0)
			{
				whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_LG_CityTo_ID).append("=?");
				params.add(City_To_ID);
			}
			if (movementdate != null)
			{
				whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_ValidFrom).append("<=?");
				params.add(movementdate);
			}			

			return new Query(getCtx(),MLGRoute.Table_Name, whereClause.toString(), get_TrxName())
			.setParameters(params)
			.list();
	}  // findRouteByCity
	

	public Collection<MLGRoute> findRouteByRegionOfCity(int Region_from_ID, int Region_To_ID, Timestamp movementdate, Boolean directsearch)
	{
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		if (!directsearch)
		{
			if (Region_from_ID > 0)
			{  
				whereClause.append(" AND " +MLGRoute.COLUMNNAME_LG_CityFrom_ID +  " in (select c_city_ID from LG_Region_City where lg_region_ID=?)");
				params.add(Region_from_ID);
			}		
			else if (Region_To_ID > 0)
			{
				whereClause.append(" AND " +MLGRoute.COLUMNNAME_LG_CityTo_ID +  " in (select c_city_ID from LG_Region_City where lg_region_ID=?)");
				params.add(Region_To_ID);
			}

		}
		else
		{
			if (Region_from_ID > 0)
			{  
				whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_LG_RegionFrom_ID).append("=?");
				params.add(Region_from_ID);
			}		
			else if (Region_To_ID > 0)
			{
				whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_LG_RegionTo_ID).append("=?");
				params.add(Region_To_ID);
			}			
		}
		if (movementdate != null)
		{
			whereClause.append(" AND ").append(MLGRoute.COLUMNNAME_ValidFrom).append("<=?");
			params.add(movementdate);
		}	
			
		return new Query(getCtx(),MLGRoute.Table_Name, whereClause.toString(), get_TrxName())
		.setParameters(params)
		.list();
	}  // findRouteByRegionOfCity


	public BigDecimal calculatePrice(int M_Product_ID, int C_BPartner_ID,
			Timestamp ValidFrom, Timestamp ValidTo,
			int C_UOM_Volume_ID, BigDecimal qtyVolume,
			int C_UOM_Weight_ID, BigDecimal qtyWeight,
			StringBuffer 	description, MOrderLine oLine){

    	BigDecimal volumenPrice    = Env.ZERO;
    	BigDecimal weightPrice     = Env.ZERO;
    	BigDecimal minimumPrice    = Env.ZERO;
    	BigDecimal calculatedPrice = Env.ZERO;
    	MUOM uomWeight = new MUOM(getCtx(), C_UOM_Weight_ID, get_TrxName());
    	MUOM uomVolume = new MUOM(getCtx(), C_UOM_Volume_ID, get_TrxName());
    	
		StringBuffer globalWhereClause     = new StringBuffer("");
		ArrayList<Object> globalParameters = new ArrayList<Object>();
		
		globalWhereClause.append("LG_Route_ID in (select lg_route_ID from lg_route where ");
    	globalWhereClause.append("lg_route_ID=? and m_product_ID =? " );
    	globalWhereClause.append("and(c_bpartner_ID is null or c_bpartner_ID =?)");
    	globalWhereClause.append(" and validFrom <=?");
    	globalWhereClause.append(" and (validTo is null or validTo >=?))");

    	globalParameters.add(this.get_ID());
    	globalParameters.add(M_Product_ID);
    	globalParameters.add(C_BPartner_ID);
    	globalParameters.add(ValidFrom);
    	globalParameters.add(ValidTo);
    	  
    	volumenPrice = calculateVolumePrice(globalWhereClause, globalParameters, C_UOM_Volume_ID, qtyVolume, oLine);
    	weightPrice  = calculateWeightPrice(globalWhereClause, globalParameters, C_UOM_Weight_ID, qtyWeight);
    	minimumPrice = calculateMinimumPrice(globalWhereClause, globalParameters);

    	calculatedPrice = volumenPrice.compareTo(weightPrice)>=0?volumenPrice:weightPrice;
    	calculatedPrice = minimumPrice.compareTo(calculatedPrice)>0?minimumPrice:calculatedPrice;
    	
    	// Log
    	description.append(" Ruta: "             + this.getName() + "\n"); 	
    	description.append(" UdM peso: "         + uomWeight.getName() + "\n");
    	description.append(" Cantidad peso: "    + qtyWeight.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");  	
    	description.append(" UdM volumen: "      + uomVolume.getName() + "\n");
    	description.append(" Cantidad volumen: " + qtyVolume.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");
    	description.append(" Precio VOLUMEN: "   + volumenPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");    	
    	description.append(" Precio PESO: "      + weightPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");
    	description.append(" Precio MINIMO: "    + minimumPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");
    	description.append(" Resultado: "        + calculatedPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");
    	   	
		return calculatedPrice;
	}
	
	private BigDecimal calculateVolumePrice(StringBuffer globalWhereClause, ArrayList<Object> globalParameters,
			int C_UOM_Volume_ID, BigDecimal qtyVolume,MOrderLine oLine){

    	StringBuffer whereClauseVolumen = new StringBuffer(globalWhereClause);
    	whereClauseVolumen.append(" and BreakValueVolume >? ");
    	ArrayList<Object> 		parametersVolumen = new ArrayList<Object>() ;
    	for (Object par: globalParameters) 
    		parametersVolumen.add(par);
    	parametersVolumen.add(qtyVolume);
    	// TODO: fehlt UOM
    	MLGProductPriceRateLine volumePricerate = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClauseVolumen.toString(), get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(parametersVolumen)
		.setOrderBy(" c_Bpartner_ID, BreakValueVolume asc")
		.first(); 
    	
    	BigDecimal volumenPrice = Env.ZERO;
    	if (volumePricerate != null)
    		volumenPrice = volumePricerate.getpriceVolume().multiply(qtyVolume);
    	if (oLine != null)
    		oLine.set_ValueOfColumn("LG_ProductPriceRate_ID", volumePricerate.getLG_ProductPriceRate_ID());
		return volumenPrice;
	} // calculateVolumePrice
	
	private BigDecimal calculateWeightPrice(StringBuffer globalWhereClause, ArrayList<Object> globalParameters,
			int C_UOM_Weight_ID, BigDecimal qtyWeight){

    	StringBuffer whereClauseVolumen = new StringBuffer(globalWhereClause);
    	whereClauseVolumen.append(" and BreakValueWeight >? ");
    	ArrayList<Object> 		parametersVolumen = new ArrayList<Object>() ;
    	for (Object par: globalParameters)
    		parametersVolumen.add(par);
    	parametersVolumen.add(qtyWeight);
    	// TODO: fehlt UOM
    	MLGProductPriceRateLine weightPricerate = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClauseVolumen.toString(), get_TrxName())
		.setOnlyActiveRecords(true)
		.setParameters(parametersVolumen)
		.setOrderBy(" c_Bpartner_ID, BreakValueWeight asc")
		.first(); 
    	
    	BigDecimal weightPrice = Env.ZERO;
    	if (weightPricerate != null)
    		weightPrice = weightPricerate.getPriceWeight().multiply(qtyWeight);    
		return weightPrice;
	} // calculateWeightPrice
	
	private BigDecimal calculateMinimumPrice(StringBuffer globalWhereClause, ArrayList<Object> globalParameters){

    	StringBuffer whereClauseMinimum = new StringBuffer(globalWhereClause);

    	BigDecimal minimumPrice = Env.ZERO;
    	whereClauseMinimum.append(" and MinimumAmt > 0");
    	
    	MLGProductPriceRateLine minimumPricerate = new Query(getCtx(), MLGProductPriceRate.Table_Name, whereClauseMinimum.toString(), get_TrxName())
    		.setOnlyActiveRecords(true)
    		.setParameters(globalParameters)
    		.setOrderBy(" MinimumAmt desc")
    		.first();
    	if (minimumPricerate != null)
    		minimumPrice = (BigDecimal)minimumPricerate.get_Value("MinimumAmt");  
		return minimumPrice;
	} // calculateMinimumPrice
	
	
	
	
}	//	MLGProductPriceRate
