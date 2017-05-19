/**********************************************************************
 * This file is part of Adempiere ERP Bazaar                          * 
 * http://www.adempiere.org                                           * 
 *                                                                    * 
 * Copyright (C) Victor Perez	                                      * 
 * Copyright (C) Contributors                                         * 
 *                                                                    * 
 * This program is free software; you can redistribute it and/or      * 
 * modify it under the terms of the GNU General Public License        * 
 * as published by the Free Software Foundation; either version 2     * 
 * of the License, or (at your option) any later version.             * 
 *                                                                    * 
 * This program is distributed in the hope that it will be useful,    * 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     * 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the       * 
 * GNU General Public License for more details.                       * 
 *                                                                    * 
 * You should have received a copy of the GNU General Public License  * 
 * along with this program; if not, write to the Free Software        * 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,         * 
 * MA 02110-1301, USA.                                                * 
 *                                                                    * 
 * Contributors:                                                      * 
 *  - Victor Perez (victor.perez@e-evolution.com	 )                *
 *                                                                    *
 * Sponsors:                                                          *
 *  - e-Evolution (http://www.e-evolution.com/)                       *
 **********************************************************************/
package org.eevolution.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProject;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 * Class Model for Logistic
 * @author Susanne Calderon
 *
 */
public class LG_WMInOutBound extends MWMInOutBound
{
	private static final long serialVersionUID = -924606040343895114L;
	
	
	
	public LG_WMInOutBound(Properties ctx, int WM_InOutBound_ID, String trxName) {
		super(ctx, WM_InOutBound_ID, trxName);
	}


	
	public void createOrderFromLines()
	{
		for (MWMInOutBoundLine ioLine:getLines(true, ""))
		{
			MOrder order = new MOrder(Env.getCtx(), 0, get_TrxName());
			order.setC_Project_ID(getC_Project_ID());
			MOrderLine oLine = new MOrderLine(order);
			oLine.setC_Project_ID(getC_Project_ID());
		}
	}

	public Boolean validateBookings()
	{
		BigDecimal sumvolume = Env.ZERO;
		for (MWMInOutBoundLine ioLine:getLines(true, ""))
		{
			sumvolume = sumvolume.add((BigDecimal)ioLine.get_Value("volume"));
			if (sumvolume.compareTo(getVolume())>=0)
				return false;
		}
		return true;
	}
	
	

	public static void createLoadingGuide(String documentno)
	{
		String innerTrxName = Trx.createTrxName("LOD");
		Trx innerTrx = Trx.get(innerTrxName, true);
		MProject project = new MProject(Env.getCtx(), 0, innerTrxName);
		MWMInOutBound loadingguide = new MWMInOutBound(Env.getCtx(), 0, innerTrxName);
		loadingguide.setDocumentNo(documentno);
		loadingguide.setC_Project_ID(project.getC_Project_ID());
	}
	
	public static void fillLoadingGuide(String documentno, Object Bookings)
	{
		String innerTrxName = Trx.createTrxName("LOD");
		Trx innerTrx = Trx.get(innerTrxName, true);
		MWMInOutBound loadingguide = new Query(Env.getCtx(), MWMInOutBound.Table_Name, MWMInOutBound.COLUMNNAME_DocumentNo + "=?", innerTrxName)
			.firstOnly();
		MWMInOutBoundLine ioLine = new MWMInOutBoundLine(loadingguide);
		ioLine.setC_Project_ID(loadingguide.getC_Project_ID());
	}
	
	
	
	
	
	
	
	
}	
