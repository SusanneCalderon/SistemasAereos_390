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

import org.compiere.model.MInvoice;
import org.compiere.model.MProject;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 * Class Model for Logistic
 * @author Susanne Calderon
 *
 */
public class LG_Project extends MProject
{
	private static final long serialVersionUID = -924606040343895114L;
	
	
	
	public LG_Project(Properties ctx, int C_Project_ID, String trxName) {
		super(ctx, C_Project_ID, trxName);
	}
	
	
	public static void IncluirPurchaseInvoice(String projectName, int c_BPartner_ID, BigDecimal amt, String name)
	{

		String innerTrxName = Trx.createTrxName("Prj");
		Trx innerTrx = Trx.get(innerTrxName, true);
		String whereClause = MProject.COLUMNNAME_Value + "=?";
		MProject project = new Query(Env.getCtx(), MProject.Table_Name, whereClause, innerTrxName)
			.setParameters(projectName)
			.firstOnly();
		MInvoice invoice = new MInvoice(Env.getCtx(), 0, innerTrxName);
		invoice.setC_Project_ID(project.getC_Project_ID());
	}
	
	
	
}	
