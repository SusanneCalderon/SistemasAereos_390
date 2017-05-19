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
import org.compiere.process.SvrProcess;
/** Generated Process for (ImportPriceRate)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public abstract class ImportProductPriceRateAbstract extends SvrProcess
{
	/** Process Value 	*/
	private static final String VALUE = "ImportPriceRate";
	/** Process Name 	*/
	private static final String NAME = "ImportPriceRate";
	/** Process Id 	*/
	private static final int ID = 3000354;
 
	/**	Parameter Name for C_BPartner_ID	*/
	public static final String C_BPartner_ID = "C_BPartner_ID";
	/**	Parameter Name for LG_TransportType	*/
	public static final String LG_TransportType = "LG_TransportType";
	/**	Parameter Name for LG_ShippingMode	*/
	public static final String LG_ShippingMode = "LG_ShippingMode";
	/**	Parameter Name for M_Product_0_ID	*/
	public static final String M_Product_0_ID = "M_Product_0_ID";
	/**	Parameter Name for C_UOM_Volume_ID	*/
	public static final String C_UOM_Volume_ID = "C_UOM_Volume_ID";
	/**	Parameter Name for C_UOM_Weight_ID	*/
	public static final String C_UOM_Weight_ID = "C_UOM_Weight_ID";
	/**	Parameter Name for M_Product_1_ID	*/
	public static final String M_Product_1_ID = "M_Product_1_ID";
	/**	Parameter Name for M_Product_2_ID	*/
	public static final String M_Product_2_ID = "M_Product_2_ID";
	/**	Parameter Name for M_Product_3_ID	*/
	public static final String M_Product_3_ID = "M_Product_3_ID";
	/**	Parameter Name for M_Product_4_ID	*/
	public static final String M_Product_4_ID = "M_Product_4_ID";
	/**	Parameter Name for M_Product_5_ID	*/
	public static final String M_Product_5_ID = "M_Product_5_ID";
	/**	Parameter Name for IsValidateOnly	*/
	public static final String IsValidateOnly = "IsValidateOnly";
	/**	Parameter Name for surcharge_0_ID	*/
	public static final String surcharge_0_ID = "surcharge_0_ID";
	/**	Parameter Name for surcharge_1_ID	*/
	public static final String surcharge_1_ID = "surcharge_1_ID";
	/**	Parameter Name for surcharge_2_ID	*/
	public static final String surcharge_2_ID = "surcharge_2_ID";
	/**	Parameter Name for surcharge_3_ID	*/
	public static final String surcharge_3_ID = "surcharge_3_ID";
	/**	Parameter Name for surcharge_4_ID	*/
	public static final String surcharge_4_ID = "surcharge_4_ID";
	/**	Parameter Name for surcharge_5_ID	*/
	public static final String surcharge_5_ID = "surcharge_5_ID";
	/**	Parameter Name for BreakValue_0	*/
	public static final String BreakValue_0 = "BreakValue_0";
	/**	Parameter Name for BreakValue_1	*/
	public static final String BreakValue_1 = "BreakValue_1";
	/**	Parameter Name for BreakValue_2	*/
	public static final String BreakValue_2 = "BreakValue_2";
	/**	Parameter Name for BreakValue_3	*/
	public static final String BreakValue_3 = "BreakValue_3";
	/**	Parameter Name for BreakValue_4	*/
	public static final String BreakValue_4 = "BreakValue_4";
	/**	Parameter Name for BreakValue_5	*/
	public static final String BreakValue_5 = "BreakValue_5";

	/**	Parameter Value for businessPartnerId	*/
	private int businessPartnerId;
	/**	Parameter Value for tipotransporte	*/
	private String tipotransporte;
	/**	Parameter Value for lGShippingMode	*/
	private String lGShippingMode;
	/**	Parameter Value for mProduct0IDId	*/
	private int mProduct0IDId;
	/**	Parameter Value for uOMforVolumeId	*/
	private int uOMforVolumeId;
	/**	Parameter Value for uOMforWeightId	*/
	private int uOMforWeightId;
	/**	Parameter Value for mProduct1IDId	*/
	private int mProduct1IDId;
	/**	Parameter Value for mProduct2IDId	*/
	private int mProduct2IDId;
	/**	Parameter Value for mProduct3IDId	*/
	private int mProduct3IDId;
	/**	Parameter Value for mProduct4IDId	*/
	private int mProduct4IDId;
	/**	Parameter Value for mProduct5IDId	*/
	private int mProduct5IDId;
	/**	Parameter Value for isOnlyValidateData	*/
	private boolean isOnlyValidateData;
	/**	Parameter Value for surcharge0IDId	*/
	private int surcharge0IDId;
	/**	Parameter Value for surcharge1IDId	*/
	private int surcharge1IDId;
	/**	Parameter Value for surcharge2IDId	*/
	private int surcharge2IDId;
	/**	Parameter Value for surcharge3IDId	*/
	private int surcharge3IDId;
	/**	Parameter Value for surcharge4IDId	*/
	private int surcharge4IDId;
	/**	Parameter Value for surcharge5IDId	*/
	private int surcharge5IDId;
	/**	Parameter Value for breakValue0	*/
	private BigDecimal breakValue0;
	/**	Parameter Value for breakValue1	*/
	private BigDecimal breakValue1;
	/**	Parameter Value for breakValue2	*/
	private BigDecimal breakValue2;
	/**	Parameter Value for breakValue3	*/
	private BigDecimal breakValue3;
	/**	Parameter Value for breakValue4	*/
	private BigDecimal breakValue4;
	/**	Parameter Value for breakValue5	*/
	private BigDecimal breakValue5;
 

	@Override
	protected void prepare()
	{
		businessPartnerId = getParameterAsInt(C_BPartner_ID);
		tipotransporte = getParameterAsString(LG_TransportType);
		lGShippingMode = getParameterAsString(LG_ShippingMode);
		mProduct0IDId = getParameterAsInt(M_Product_0_ID);
		uOMforVolumeId = getParameterAsInt(C_UOM_Volume_ID);
		uOMforWeightId = getParameterAsInt(C_UOM_Weight_ID);
		mProduct1IDId = getParameterAsInt(M_Product_1_ID);
		mProduct2IDId = getParameterAsInt(M_Product_2_ID);
		mProduct3IDId = getParameterAsInt(M_Product_3_ID);
		mProduct4IDId = getParameterAsInt(M_Product_4_ID);
		mProduct5IDId = getParameterAsInt(M_Product_5_ID);
		isOnlyValidateData = getParameterAsBoolean(IsValidateOnly);
		surcharge0IDId = getParameterAsInt(surcharge_0_ID);
		surcharge1IDId = getParameterAsInt(surcharge_1_ID);
		surcharge2IDId = getParameterAsInt(surcharge_2_ID);
		surcharge3IDId = getParameterAsInt(surcharge_3_ID);
		surcharge4IDId = getParameterAsInt(surcharge_4_ID);
		surcharge5IDId = getParameterAsInt(surcharge_5_ID);
		breakValue0 = getParameterAsBigDecimal(BreakValue_0);
		breakValue1 = getParameterAsBigDecimal(BreakValue_1);
		breakValue2 = getParameterAsBigDecimal(BreakValue_2);
		breakValue3 = getParameterAsBigDecimal(BreakValue_3);
		breakValue4 = getParameterAsBigDecimal(BreakValue_4);
		breakValue5 = getParameterAsBigDecimal(BreakValue_5);
	}

	/**	 Getter Parameter Value for businessPartnerId	*/
	protected int getBusinessPartnerId() {
		return businessPartnerId;
	}

	/**	 Getter Parameter Value for tipotransporte	*/
	protected String getTipotransporte() {
		return tipotransporte;
	}

	/**	 Getter Parameter Value for lGShippingMode	*/
	protected String getLGShippingMode() {
		return lGShippingMode;
	}

	/**	 Getter Parameter Value for mProduct0IDId	*/
	protected int getMProduct0IDId() {
		return mProduct0IDId;
	}

	/**	 Getter Parameter Value for uOMforVolumeId	*/
	protected int getUOMforVolumeId() {
		return uOMforVolumeId;
	}

	/**	 Getter Parameter Value for uOMforWeightId	*/
	protected int getUOMforWeightId() {
		return uOMforWeightId;
	}

	/**	 Getter Parameter Value for mProduct1IDId	*/
	protected int getMProduct1IDId() {
		return mProduct1IDId;
	}

	/**	 Getter Parameter Value for mProduct2IDId	*/
	protected int getMProduct2IDId() {
		return mProduct2IDId;
	}

	/**	 Getter Parameter Value for mProduct3IDId	*/
	protected int getMProduct3IDId() {
		return mProduct3IDId;
	}

	/**	 Getter Parameter Value for mProduct4IDId	*/
	protected int getMProduct4IDId() {
		return mProduct4IDId;
	}

	/**	 Getter Parameter Value for mProduct5IDId	*/
	protected int getMProduct5IDId() {
		return mProduct5IDId;
	}

	/**	 Getter Parameter Value for isOnlyValidateData	*/
	protected boolean isOnlyValidateData() {
		return isOnlyValidateData;
	}

	/**	 Getter Parameter Value for surcharge0IDId	*/
	protected int getsurcharge0IDId() {
		return surcharge0IDId;
	}

	/**	 Getter Parameter Value for surcharge1IDId	*/
	protected int getsurcharge1IDId() {
		return surcharge1IDId;
	}

	/**	 Getter Parameter Value for surcharge2IDId	*/
	protected int getsurcharge2IDId() {
		return surcharge2IDId;
	}

	/**	 Getter Parameter Value for surcharge3IDId	*/
	protected int getsurcharge3IDId() {
		return surcharge3IDId;
	}

	/**	 Getter Parameter Value for surcharge4IDId	*/
	protected int getsurcharge4IDId() {
		return surcharge4IDId;
	}

	/**	 Getter Parameter Value for surcharge5IDId	*/
	protected int getsurcharge5IDId() {
		return surcharge5IDId;
	}

	/**	 Getter Parameter Value for breakValue0	*/
	protected BigDecimal getBreakValue0() {
		return breakValue0;
	}

	/**	 Getter Parameter Value for breakValue1	*/
	protected BigDecimal getBreakValue1() {
		return breakValue1;
	}

	/**	 Getter Parameter Value for breakValue2	*/
	protected BigDecimal getBreakValue2() {
		return breakValue2;
	}

	/**	 Getter Parameter Value for breakValue3	*/
	protected BigDecimal getBreakValue3() {
		return breakValue3;
	}

	/**	 Getter Parameter Value for breakValue4	*/
	protected BigDecimal getBreakValue4() {
		return breakValue4;
	}

	/**	 Getter Parameter Value for breakValue5	*/
	protected BigDecimal getBreakValue5() {
		return breakValue5;
	}

	/**	 Getter Parameter Value for Process ID	*/
	public static final int getProcessId() {
		return ID;
	}

	/**	 Getter Parameter Value for Process Value	*/
	public static final String getProcessValue() {
		return VALUE;
	}

	/**	 Getter Parameter Value for Process Name	*/
	public static final String getProcessName() {
		return NAME;
	}
}