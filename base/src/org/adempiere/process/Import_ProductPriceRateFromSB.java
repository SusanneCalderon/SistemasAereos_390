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
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.Query;
import org.compiere.model.X_I_ProductPriceRate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.shw.model.MLGProductPriceRate;
import org.shw.model.MLGProductPriceRateLine;
import org.shw.model.MLGRoute;

/** Generated Process for (Import_ProductPriceRate)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class Import_ProductPriceRateFromSB extends Import_ProductPriceRateFromSBAbstract
{
	private 	String 				clientCheck = " AND AD_Client_ID in (0, " ;
	private 	String 				whereClauseRecordsToImport ="";
	private 	String 				FromID =  " and i_ProductPricerate_ID in (";
	private int 						noFieldsBreakValue = 0;
	private  int 					noFieldsProduct = 0;
	private  int 					noFieldsSurCharge = 0;
	private int					m_AD_Client_ID = 0;
	/** Record Import **/
	private int 					imported = 0;
	/** No import records */
	private int 					notimported = 0;
	private boolean isImported = false;
	@Override
	protected void prepare()
	{
		super.prepare();
	}
	

	@Override
	protected String doIt() throws Exception
	{
		clientCheck = clientCheck + Env.getAD_Client_ID(getCtx()) + ")";
		int i = 100;
		
		
		for (int I_ProductPriceRate_ID: getSelectionKeys())
		{
			FromID = FromID + I_ProductPriceRate_ID + ",";
		}
		FromID = FromID.substring(0, FromID.length() -1) + ")";
		String sqlNoBreakFields = "select count(*) from ad_column where ad_table_ID = ? and lower(columnname) like 'breakvalue_%'";
		String sqlNoProductFields = "select count(*) from ad_column where ad_table_ID = ? and lower(columnname) like 'm_product_%'";
		String sqlNoProductSurCharge = "select count(*) from ad_column where ad_table_ID = ? and lower(columnname) like 'surcharge_%'";
		noFieldsBreakValue = DB.getSQLValueEx(get_TrxName(), sqlNoBreakFields, X_I_ProductPriceRate.Table_ID);
		noFieldsProduct = DB.getSQLValueEx(get_TrxName(), sqlNoProductFields, X_I_ProductPriceRate.Table_ID);
		noFieldsSurCharge = DB.getSQLValueEx(get_TrxName(), sqlNoProductSurCharge, X_I_ProductPriceRate.Table_ID);
		whereClauseRecordsToImport = " AND I_IsImported<>'Y' OR I_IsImported IS NULL AND (I_ErrorMsg  IS NULL or  trim(i_errormsg) = '') and processed = 'N'";
		whereClauseRecordsToImport = whereClauseRecordsToImport + FromID;
		StringBuffer sql = null;
		int no = 0;
		m_AD_Client_ID = Env.getAD_Client_ID(getCtx());
		setParameters();
		fillValues();
		setRoute();
		commitEx();
		if (isOnlyValidateData())
			return "Datos actualizados";

		importRecords();
		return "Imported: " + imported + ", Not imported: " + notimported;
		
	}
	
	private List<X_I_ProductPriceRate> getRecords(boolean imported) 
	{
		final StringBuffer whereClause = new StringBuffer(
				X_I_ProductPriceRate.COLUMNNAME_I_IsImported).append("='N' AND ")
				.append("( I_ErrorMsg  IS NULL or  trim(i_errormsg) = '') ")
				.append(FromID);
		if (isisOnlyRecordID())
		{
			whereClause.append(" AND  I_ProductPriceRate_ID = " + getRecord_ID());
		}

		return new Query(getCtx(), X_I_ProductPriceRate.Table_Name,
				whereClause.toString(), get_TrxName()).setClient_ID()
				.list();
	}
	private List<X_I_ProductPriceRate> getRecordsWithoutRoute(boolean imported) 
	{
		final StringBuffer whereClause = new StringBuffer(
				X_I_ProductPriceRate.COLUMNNAME_I_IsImported).append("='N' AND LG_Route_ID is null");

		return new Query(getCtx(), X_I_ProductPriceRate.Table_Name,
				whereClause.toString(), get_TrxName()).setClient_ID()
				.list();
	}

	private void importRecords() 
	{
		for (X_I_ProductPriceRate ppi : getRecords(false)) {
			for (int i = 0; i < noFieldsProduct; i++)
			{
				String product_column = "M_Product_" + i + "_ID";
				if (ppi.get_ValueAsInt(product_column) <=0)
					continue;
				isImported = false;
				MLGProductPriceRate ppr = importProductPriceRate(ppi, i);
				if (ppr != null)
					isImported = true;

				if (isImported) {
					ppi.setLG_ProductPriceRate_ID(ppr.getLG_ProductPriceRate_ID());
					ppi.setI_IsImported(true);
					ppi.setProcessed(true);
					if (ppi.get_ValueAsString("I_ErrorMsg").equals(""))
						ppi.set_ValueOfColumn("I_ErrorMsg", "");
					ppi.saveEx();
					imported++;
				} else {
					ppi.setI_IsImported(false);
					ppi.setProcessed(true);
					ppi.saveEx();
					notimported++;
				}
			}
		}
		
			
	}
	
	private MLGProductPriceRate importProductPriceRate(X_I_ProductPriceRate ppi, int i)
	{
		try
		{
			ArrayList< Object> params = new ArrayList<Object>();
			String product_column = "M_Product_" + i + "_ID";		
			String whereClause = "LG_Route_ID =? and validFrom=? and validTo=? and c_BPartner_ID=? and m_product_ID =?";
			params.add(ppi.getLG_Route_ID());
			params.add(ppi.getValidFrom());
			params.add(ppi.getValidTo());
			params.add(ppi.getC_BPartner_ID());
			params.add(ppi.get_Value(product_column));
			MLGProductPriceRate ppr = new Query(getCtx(), MLGProductPriceRate.Table_Name, whereClause, get_TrxName())
					.setOnlyActiveRecords(true)
					.setParameters(params)
					.setClient_ID()
					.first();
			if (ppr == null)
				ppr = new MLGProductPriceRate(getCtx(), 0, get_TrxName());
			String price_column = "PriceList_" + i;
			ppr.setC_BPartner_ID(ppi.getC_BPartner_ID());
			ppr.set_ValueOfColumn(X_I_ProductPriceRate.COLUMNNAME_LG_ShippingMode, ppi.getLG_ShippingMode());
			ppr.set_ValueOfColumn(X_I_ProductPriceRate.COLUMNNAME_LG_TransportType, ppi.getLG_TransportType());
			ppr.set_ValueOfColumn("M_Product_ID", ppi.get_Value(product_column));

			ppr.set_ValueOfColumn("C_UOM_Volume_ID", ppi.get_Value("C_UOM_Volume_ID"));
			ppr.set_ValueOfColumn("C_UOM_Weight_ID", ppi.get_Value("C_UOM_Weight_ID"));
			ppr.set_ValueOfColumn("LG_AirPort_Destiny_ID", ppi.get_Value("LG_AirPort_Destiny_ID"));
			ppr.set_ValueOfColumn("LG_AirPort_Origin_ID", ppi.get_Value("LG_AirPort_Origin_ID"));
			ppr.set_ValueOfColumn("LG_CityFrom_ID", ppi.get_Value("LG_CityFrom_ID"));
			ppr.set_ValueOfColumn("LG_CityTo_ID", ppi.get_Value("LG_CityTo_ID"));
			ppr.set_ValueOfColumn("LG_Commodity_ID", ppi.get_Value("LG_Commodity_ID"));
			ppr.set_ValueOfColumn("LG_RateType", ppi.get_Value("LG_RateType"));
			ppr.set_ValueOfColumn("LG_Route_ID", ppi.get_Value("LG_Route_ID"));
			ppr.set_ValueOfColumn("M_Shipper_ID", ppi.get_Value("M_Shipper_ID"));
			ppr.set_ValueOfColumn("MinimumAmt", ppi.get_Value("MinimumAmt"));
			ppr.set_ValueOfColumn("OWS", ppi.get_Value("OWS"));
			ppr.set_ValueOfColumn("PriceLimit", ppi.get_Value(price_column));
			ppr.set_ValueOfColumn("ValidFrom", ppi.get_Value("ValidFrom"));
			ppr.set_ValueOfColumn("ValidTo", ppi.get_Value("ValidTo"));
			String name = ppr.getLG_Route().getName() +" " +  ppr.getC_BPartner().getValue() + " " + ppr.getValidFrom() + " to " + ppr.getValidTo();
			ppr.setName(name);
			ppr.saveEx();	

			
			
			for (int j = 0; j < noFieldsBreakValue; j++)
			{
				String break_column = "BreakValue_" + j;
				BigDecimal breakValue = (BigDecimal)ppi.get_Value(break_column);
				if (breakValue.compareTo(Env.ZERO) != 0 )
				{
					MLGProductPriceRateLine ppl = importProductPriceRateLine_Break(ppi, ppr, j);
					if (ppl == null)
					{
						break;
					}
				}
					continue;
			}
			for (int j = 0; j < noFieldsSurCharge; j++)
			{
				String columnName = "surcharge_" + j + "_ID";
				String columnAmtName = "chargeAmt_" + j;
				BigDecimal chargamt = (BigDecimal)ppi.get_Value(columnAmtName);
				int surCharge_ID = ppi.get_ValueAsInt(columnName);
				if (surCharge_ID != 0 && chargamt.compareTo(Env.ZERO) != 0)
				{
					MLGProductPriceRateLine ppl = importProductPriceRateLine_SurCharge(ppi, ppr, j);
					if (ppl == null)
					{
						break;
					}
				}
					continue;
			}
			isImported=true;
			return ppr;
		}
		catch (Exception e) {
			ppi.set_ValueOfColumn("I_ErrorMsg", e.getMessage()); 
			isImported = false;
			return null;
		}
	
		}
	
	private void fillValues()
	{
		StringBuffer sql = null;
		int no = 0;
		m_AD_Client_ID = Env.getAD_Client_ID(getCtx());
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
			  + "SET M_Shipper_ID=(SELECT MAX(M_Shipper_ID) FROM M_Shipper p"
			  + " WHERE I_ProductPriceRate.Shipper_Name=p.Name AND I_ProductPriceRate.AD_Client_ID=p.AD_Client_ID) "
			  + "WHERE M_Shipper_ID IS NULL AND Shipper_Name IS NOT NULL"
			  + FromID
			  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Shipper from Value=" + no);
		
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
				  + "SET LG_AirPort_Destiny_ID =(SELECT MAX(LG_AirPort_ID) FROM LG_AirPort p"
				  + " WHERE trim(lower(I_ProductPriceRate.IATACode_Destiny))=trim(lower(p.IATACode))) "
				  + "WHERE LG_AirPort_Destiny_ID IS NULL AND IATACode_Destiny IS NOT NULL"
				  + FromID
				  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Airport from IATACode" + no);
			
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
					  + "SET LG_AirPort_Origin_ID =(SELECT MAX(LG_AirPort_ID) FROM LG_AirPort p"
					  + " WHERE trim(lower(I_ProductPriceRate.IATACode_Origin))=trim(lower(p.IATACode))) "
					  + "WHERE LG_AirPort_Origin_ID IS NULL AND IATACode_Origin IS NOT NULL"
					  + FromID
					  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Airport from IATACode" + no);
				
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
						  + "SET LG_CityFrom_ID =(SELECT MAX(C_City_ID) FROM C_City p"
						  + " WHERE trim(lower(I_ProductPriceRate.CityFrom_Name))=trim(lower(p.Name))) "
						  + "WHERE LG_CityFrom_ID IS NULL AND CityFrom_Name IS NOT NULL"
						  + FromID
						  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set CityFrom from name" + no);
					
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
							  + "SET LG_CityTo_ID =(SELECT MAX(C_City_ID) FROM C_City p"
							  + " WHERE trim(lower(I_ProductPriceRate.CityTo_Name))=trim(lower(p.Name))) "
							  + "WHERE LG_CityTo_ID IS NULL AND CityTo_Name IS NOT NULL"
							  + FromID
							  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set CityTo from name" + no);					

						
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
								  + "SET LG_Route_ID =(SELECT MAX(LG_Route_ID) FROM LG_Route p"
								  + " WHERE trim(lower(I_ProductPriceRate.routename))=trim(lower(p.Name))) "
								  + "WHERE LG_Route_ID IS NULL AND routename IS NOT NULL"
								  + FromID
								  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Route from name" + no);
							

		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
									  + "SET LG_Commodity_ID =(SELECT MAX(LG_Commodity_ID) FROM LG_Commodity p"
									  + " WHERE trim(lower(I_ProductPriceRate.commodityValue))=trim(lower(p.value))) "
									  + "WHERE LG_Commodity_ID IS NULL AND commodityValue IS NOT NULL" 
									  + FromID
									  + " AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Commodity from value" + no);		
	}
	
	private MLGRoute getLGRoute(X_I_ProductPriceRate ppi)
	{
		if (ppi.getLG_CityFrom_ID() != 0 || ppi.getLG_CityTo_ID() !=0)
		{
			if (ppi.getLG_CityFrom_ID() == 0 || ppi.getLG_CityTo_ID() ==0)
					{
						return null;
					}
			ArrayList< Object> params = new ArrayList<Object>();
			String whereClause = "LG_CityFrom_ID =? and LG_CityTo_ID=? and lg_transporttype=?";
			params.add(ppi.getLG_CityFrom_ID());
			params.add(ppi.getLG_CityTo_ID());
			params.add(ppi.getLG_TransportType());
			MLGRoute route = new Query(getCtx(), MLGRoute.Table_Name, whereClause, get_TrxName())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.setParameters(params)
					.first();
			if (route == null)
			{
				route = new MLGRoute(getCtx(), 0, get_TrxName());
				route.setLG_CityFrom_ID(ppi.getLG_CityFrom_ID());
				route.setLG_CityTo_ID(ppi.getLG_CityTo_ID());
				route.setLG_TransportType(ppi.getLG_TransportType());
				params.clear();
				params.add(1000004);
				params.add(route.getLG_TransportType());
				String transporttype = DB.getSQLValueStringEx(get_TrxName(), "Select name from ad_ref_list where ad_reference_ID = ? and value =?", params);
				route.setName(route.getLG_CityFrom().getLocode() + " / " + route.getLG_CityTo().getLocode() + " " + transporttype);
				route.saveEx();
			}
			return route;
		}
		if (ppi.getLG_AirPort_Destiny_ID() != 0 || ppi.getLG_AirPort_Origin_ID()!=0)
		{

			if (ppi.getLG_AirPort_Destiny_ID() == 0 || ppi.getLG_AirPort_Origin_ID()==0)
					{
						return null;
					}
			ArrayList< Object> params = new ArrayList<Object>();
			String whereClause = "LG_AirPort_Origin_ID =? and LG_AirPort_Destiny_ID=? and lg_transporttype=?";
			
			params.add(ppi.getLG_AirPort_Origin_ID());
			params.add(ppi.getLG_AirPort_Destiny_ID());
			params.add(ppi.getLG_TransportType());
			if (!ppi.getroutename().equals(""))
			{
				whereClause = whereClause + " and name = ?";
				params.add(ppi.getroutename());
			}
			MLGRoute route = new Query(getCtx(), MLGRoute.Table_Name, whereClause, get_TrxName())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.setParameters(params)
					.first();
			if (route == null)
			{
				route = new MLGRoute(getCtx(), 0, get_TrxName());
				route.set_ValueOfColumn("LG_AirPort_Origin_ID", ppi.getLG_AirPort_Origin_ID());
				route.set_ValueOfColumn("LG_AirPort_Destiny_ID", ppi.getLG_AirPort_Destiny_ID());
				
				route.setLG_TransportType(ppi.getLG_TransportType());
				if (!ppi.getroutename().equals(""))
					route.setName(ppi.getroutename());
				else
				{
					params.clear();
					params.add(1000004);
					params.add(route.getLG_TransportType());
					String transporttype = DB.getSQLValueStringEx(get_TrxName(), "Select name from ad_ref_list where ad_reference_ID = ? and value =?", params);
					route.setName(route.getLG_CityFrom().getLocode() + " / " + route.getLG_CityTo().getLocode() + " " + transporttype);
				}
					
				route.saveEx();
			}
			return route;
		
		}
		return null;
	}
	
	private MLGProductPriceRateLine importProductPriceRateLine_Break(X_I_ProductPriceRate ppi, MLGProductPriceRate ppr, int i)
	{
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(ppr.getLG_ProductPriceRate_ID());
		String break_column = "BreakValue_" + i;		
		String price_column = "PriceList_" + i;
		params.add((BigDecimal)ppi.get_Value(break_column));
		try
		{
			
		String whereClause = "LG_ProductPriceRate_ID=? and breakvalue = ?";
		MLGProductPriceRateLine ppl = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClause, get_TrxName())
				.setOnlyActiveRecords(true)
				.setParameters(params)
				.first();
		if (ppl == null)
			ppl = new MLGProductPriceRateLine(ppr);
		ppl.setBreakValue((BigDecimal)ppi.get_Value(break_column));
		if (ppr.getC_UOM_Volume_ID() !=0)
			ppl.setpriceVolume((BigDecimal)ppi.get_Value(price_column));
		else
			ppl.setPriceWeight((BigDecimal)ppi.get_Value(price_column));
		
		ppl.setM_Product_ID(ppr.getM_Product_ID());
		ppl.saveEx();
		return ppl;		
		}
		catch (Exception e) 
		{
			ppi.set_ValueOfColumn("I_ErrorMsg", e.getMessage()); 
			return null;
		}
	}
	
	private MLGProductPriceRateLine importProductPriceRateLine_SurCharge(X_I_ProductPriceRate ppi, MLGProductPriceRate ppr, int i)
	{
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(ppr.getLG_ProductPriceRate_ID());
		String columnName = "surcharge_" + i + "_ID";
		String columnAmtName = "chargeAmt_" + i;
		params.add(ppi.get_ValueAsInt(columnName));
		try
		{
			
		String whereClause = "LG_ProductPriceRate_ID=? and M_Product_ID = ?";
		MLGProductPriceRateLine ppl = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClause, get_TrxName())
				.setOnlyActiveRecords(true)
				.setParameters(params)
				.first();
		if (ppl == null)
			ppl = new MLGProductPriceRateLine(ppr);
		ppl.setM_Product_ID(ppi.get_ValueAsInt(columnName));
		ppl.setPriceLimit((BigDecimal)ppi.get_Value(columnAmtName));
		ppl.setPriceList((BigDecimal)ppi.get_Value(columnAmtName));
		ppl.setPriceStd((BigDecimal)ppi.get_Value(columnAmtName));
		ppl.saveEx();
		return ppl;		
		}
		catch (Exception e) 
		{
			ppi.set_ValueOfColumn("I_ErrorMsg", e.getMessage()); 
			return null;
		}
	}
	
	private void setRoute()
	{
		for (X_I_ProductPriceRate ppi : getRecordsWithoutRoute(false)) {
			MLGRoute route = getLGRoute(ppi);
			if (route == null)
			{
				ppi.set_ValueOfColumn("I_ErrorMsg", "No fue posible definir la ruta"); 
				ppi.saveEx();
				continue;
			}
			ppi.setLG_Route_ID(route.getLG_Route_ID());
			ppi.saveEx();
		}
	}

	public String getWhereClause() {
		return " AND AD_Client_ID=" + 1000012;
	}
	
	private void setParameters()
	{
		int no = 0;
		
		StringBuffer sql = new StringBuffer();
		String whereClause = " WHERE (I_IsImported<>'Y' OR I_IsImported IS NULL)";
		//	Set Client, Org, IsActive, Created/Updated, EnforcePriceLimit, IsSOPriceList, IsTaxIncluded, PricePrecision
		sql = new StringBuffer ("UPDATE I_ProductPriceRate "
			+ "SET AD_Client_ID = COALESCE (AD_Client_ID, ").append(m_AD_Client_ID).append("),"
			+ " AD_Org_ID = COALESCE (AD_Org_ID, 0),"
			+ " IsActive = COALESCE (IsActive, 'Y'),"
			+ " Created = COALESCE (Created, SysDate),"
			+ " CreatedBy = COALESCE (CreatedBy, 0),"
			+ " Updated = COALESCE (Updated, SysDate),"
			+ " UpdatedBy = COALESCE (UpdatedBy, 0),"
			+ " I_ErrorMsg = ' ',"
			+ " I_IsImported = 'N' "
			+ whereClause);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.info("Reset=" + no);
		ArrayList<Object> params = new ArrayList<Object>();
		if (getBusinessPartnerId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBusinessPartnerId());
			sql.append("Update I_ProductPriceRate set C_BPartner_ID=? where C_BPartner_ID is null "  + getWhereClause());
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}

		if (getMProduct0IDId()!=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct0IDId());
			sql.append("Update I_ProductPriceRate set M_Product_0_ID=? where M_Product_0_ID is null "+ getWhereClause());
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getMProduct1IDId()!=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct1IDId());
			sql.append("Update I_ProductPriceRate set M_Product_1_ID=? where M_Product_1_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}if (getMProduct2IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct2IDId());
			sql.append("Update I_ProductPriceRate set M_Product_2_ID=? where M_Product_2_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}if (getMProduct3IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct3IDId());
			sql.append("Update I_ProductPriceRate set M_Product_3_ID=? where M_Product_3_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getMProduct4IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct4IDId());
			sql.append("Update I_ProductPriceRate set M_Product_4_ID=? where M_Product_4_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getMProduct5IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getMProduct5IDId());
			sql.append("Update I_ProductPriceRate set M_Product_5_ID=? where M_Product_5_ID is null "+ getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}		

		if (getsurcharge0IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge0IDId());
			sql.append("Update I_ProductPriceRate set surcharge_0_ID=? where surcharge_0_ID is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getsurcharge1IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge1IDId());
			sql.append("Update I_ProductPriceRate set surcharge_1_ID=? where surcharge_1_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getsurcharge2IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge2IDId());
			sql.append("Update I_ProductPriceRate set surcharge_2_ID=? where surcharge_2_ID is null "  + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getsurcharge3IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge3IDId());
			sql.append("Update I_ProductPriceRate set surcharge_3_ID=? where surcharge_3_ID is null " + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getsurcharge4IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge4IDId());
			sql.append("Update I_ProductPriceRate set surcharge_4_ID=? where surcharge_4_ID is null "  + getWhereClause() + whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getsurcharge5IDId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getsurcharge5IDId());
			sql.append("Update I_ProductPriceRate set surcharge_5_ID=? where surcharge_5_ID is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		

		if (getBreakValue0().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue0());
			sql.append("Update I_ProductPriceRate set breakValue_0=? where breakValue_0 is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getBreakValue1().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue1());
			sql.append("Update I_ProductPriceRate set breakValue_1=? where breakValue_1 is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}if (getBreakValue2().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue2());
			sql.append("Update I_ProductPriceRate set breakValue_2=? where breakValue_2 is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getBreakValue3().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue3());
			sql.append("Update I_ProductPriceRate set breakValue_3=? where breakValue_3 is null "  + getWhereClause() +whereClauseRecordsToImport);
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getBreakValue4().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue4());
			sql.append("Update I_ProductPriceRate set breakValue_4=? where breakValue_4 is null "  + getWhereClause()  + whereClauseRecordsToImport );
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}if (getBreakValue5().compareTo(Env.ZERO) !=0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getBreakValue5());
			sql.append("Update I_ProductPriceRate set breakValue_5=? where breakValue_5 is null "  + getWhereClause()  + whereClauseRecordsToImport );
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		

		if (getTipotransporte() != null || getTipotransporte() != "")
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getTipotransporte());
			sql.append("Update I_ProductPriceRate set LG_TransportType=? where LG_TransportType is null "  + getWhereClause()  + whereClauseRecordsToImport );
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getLGShippingMode() != null)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getLGShippingMode());
			sql.append("Update I_ProductPriceRate set LG_ShippingMode=?  where LG_ShippingMode is null "  + whereClauseRecordsToImport + "AND LG_ShippingMode is null "  + getWhereClause());
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getUOMforWeightId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getUOMforWeightId());
			sql.append("Update I_ProductPriceRate set C_UOM_Weight_ID=? WHERE C_UOM_Weight_ID is null  " + whereClauseRecordsToImport + getWhereClause());
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
		if (getUOMforVolumeId() != 0)
		{
			sql.delete(0, sql.length());
			params.clear();
			params.add(getUOMforVolumeId());
			sql.append("Update I_ProductPriceRate set C_UOM_Volume_ID=? WHERE C_UOM_Volume_ID is null" + whereClauseRecordsToImport  + getWhereClause());
			no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		}
			
	}

}