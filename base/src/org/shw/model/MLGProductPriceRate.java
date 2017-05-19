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
import java.util.List;
import java.util.Properties;

import org.compiere.model.MUOMConversion;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_LG_ProductPriceRate;
import org.compiere.util.Env;

/**
 * 	
 *  @author Susanne Calderon
 *  
 */
public class MLGProductPriceRate extends X_LG_ProductPriceRate
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6181691633960939054L;

	private MLGProductPriceRateLine[]	m_lines;

	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param W_MailMsg_ID id
	 *	@param trxName trx
	 */
	public MLGProductPriceRate (Properties ctx, int LG_ProductPriceRate_ID, String trxName)
	{
		super (ctx, LG_ProductPriceRate_ID, trxName);
	}	//	MLGProductPriceRate

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MLGProductPriceRate (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLGProductPriceRate
	
	public void createQuotation (int C_Bpartner_ID)
	{
		
	}

	public MLGProductPriceRateLine[] getLines (boolean requery)
	{
		if (m_lines == null || m_lines.length == 0 || requery)
			m_lines = getLines("");
		set_TrxName(m_lines, get_TrxName());
		return m_lines;
	}	//	getLines

	/**
	 * 	Get Lines of Invoice
	 * 	@return lines
	 */
	public MLGProductPriceRateLine[] getLines()
	{
		return getLines(false);
	}	//	getLines
	

	/**
	 * 	Get productpricerate Lines of productpricerate
	 * 	@param whereClause starting with AND
	 * 	@return lines
	 */
	private MLGProductPriceRateLine[] getLines (String whereClause)
	{
		String whereClauseFinal = "LG_ProductPriceRate_ID=? ";
		if (whereClause != "")
			whereClauseFinal += whereClause;
		List<MLGProductPriceRateLine> list = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getLG_ProductPriceRate_ID())
										.setOnlyActiveRecords(true)
										.setOrderBy(MLGProductPriceRateLine.COLUMNNAME_BreakValueVolume)
										.list();
		return list.toArray(new MLGProductPriceRateLine[list.size()]);
	}	//	getLines

	public MLGProductPriceRateLine[] getLinesOrderByVolumn (String whereClause)
	{
		String whereClauseFinal = "LG_ProductPriceRate_ID=? ";
		if (whereClause != "")
			whereClauseFinal += "AND " + whereClause;
		List<MLGProductPriceRateLine> list = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getLG_ProductPriceRate_ID())
										.setOrderBy(MLGProductPriceRateLine.COLUMNNAME_BreakValueVolume + " desc" )
										.list();
		return list.toArray(new MLGProductPriceRateLine[list.size()]);
	}	//	getLines
	

	public MLGProductPriceRateLine[] getLinesOrderByWeight (String whereClause)
	{
		String whereClauseFinal = "LG_ProductPriceRate_ID=? ";
		if (whereClause != "")
			whereClauseFinal +=  "AND " +  whereClause;
		List<MLGProductPriceRateLine> list = new Query(getCtx(), MLGProductPriceRateLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getLG_ProductPriceRate_ID())
										.setOrderBy(MLGProductPriceRateLine.COLUMNNAME_BreakValueWeight + " desc")
										.list();
		return list.toArray(new MLGProductPriceRateLine[list.size()]);
	}	//	getLines
	
	

	public static MLGProductPriceRate copyFrom (MLGProductPriceRate from, Timestamp validfrom, 
		boolean isSOTrx, int c_BPartner_ID, String trxName)
	{
		MLGProductPriceRate to = new MLGProductPriceRate (from.getCtx(), 0, trxName);
		to.set_TrxName(trxName);
		PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		if (c_BPartner_ID!= 0)
			to.setC_BPartner_ID(c_BPartner_ID);
		to.set_Value("isSOTrx", isSOTrx);
		to.saveEx();
		//
		return to;
	}	//	copyFrom
	
	public BigDecimal calculateVolumePrice(int C_UOM_Volume_ID, BigDecimal qtyVolume){

    	BigDecimal volumenPrice = Env.ZERO;
    	BigDecimal qtyconverted = qtyVolume;
    	
		for (MLGProductPriceRateLine prl:getLinesOrderByVolumn("M_Product_ID=" + getM_Product_ID()))
		{
			if (prl.getC_UOM_Volume_ID() != C_UOM_Volume_ID)
				qtyconverted = MUOMConversion.convert(C_UOM_Volume_ID, prl.getC_UOM_Volume_ID(), qtyVolume, true);
			if (prl.getBreakValueVolume().compareTo(qtyconverted) ==1)
				continue;	
    		volumenPrice = prl.getpriceVolume().multiply(qtyconverted);	
    		volumenPrice = volumenPrice.compareTo(prl.getMinimumAmt()) == -1? prl.getMinimumAmt():volumenPrice;
		}
		return volumenPrice;
	} // calculateVolumePrice
	

	public BigDecimal calculateWeightPrice(int C_UOM_Weight_ID, BigDecimal qtyweight){

    	BigDecimal weightPrice = Env.ZERO;
    	BigDecimal qtyconverted = qtyweight;
		for (MLGProductPriceRateLine prl:getLinesOrderByWeight("M_Product_ID=" + getM_Product_ID()))
		{
			if (prl.getC_UOM_Weight_ID() != C_UOM_Weight_ID)
				qtyconverted = MUOMConversion.convert(C_UOM_Weight_ID, prl.getC_UOM_Weight_ID(), qtyweight, true);
			if (prl.getBreakValueWeight().compareTo(qtyconverted) == 1)
				continue;	
			weightPrice = prl.getPriceWeight().multiply(qtyconverted);	
			weightPrice = weightPrice.compareTo(prl.getMinimumAmt())== -1?prl.getMinimumAmt():weightPrice;
		}
		return weightPrice;
	} // calculateVolumePrice
	

	public int copyLinesFrom (MLGProductPriceRate otherPriceRate)
	{
		int count = 0;
		for (MLGProductPriceRateLine ppl: otherPriceRate.getLines(""))
		{
			MLGProductPriceRateLine line = new MLGProductPriceRateLine(this);
			PO.copyValues(ppl, line, getAD_Client_ID(), getAD_Org_ID());
			line.setLG_ProductPriceRate_ID(getLG_ProductPriceRate_ID());
			line.saveEx();
			//
			count = count++;
			//if (otherPriceRate.getLines(false).length != count)
			//	log.log(Level.SEVERE, "Line difference - From=" +otherPriceRate.getLines(false).length + " <> Saved=" + count);
		}	//	copyLinesFrom
		return count;
	}
	

	
}	//	MLGProductPriceRate
