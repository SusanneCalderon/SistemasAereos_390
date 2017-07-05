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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MProject;
import org.compiere.model.Query;
import org.compiere.util.Trx;

/** Generated Process for (updateProjects)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class UpdateProject extends UpdateProjectAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		ArrayList<Object> params = new ArrayList<>();
		String whereClause = "IsSummary=?  ";
		params.add(isSummaryLevel());
		if (getProjectId()!=0)
		{
			whereClause = whereClause + " and c_Project_ID = ?";
			params.add(getProjectId());
		}
		if (getContractDate()!= null)
		{
			whereClause = whereClause + " and datecontract between ? and ?";
			params.add(getContractDate());
			params.add(getContractDateTo());
		}


		int[] IDs = new Query(getCtx(), MProject.Table_Name, whereClause, get_TrxName())
				.setParameters(params)
				.setClient_ID()
				.setOrderBy("Value")
				.getIDs();

        Trx dbTransaction = null;		
		for (int c_Project_ID:IDs)
		{
			Integer projectID = (Integer)c_Project_ID;			
            dbTransaction = Trx.get(projectID.toString(), true);
            MProject project = new MProject(getCtx(), c_Project_ID, dbTransaction.getTrxName());
			project.updateProject();
		
            if (dbTransaction != null) {
                dbTransaction.commit(true);
                dbTransaction.close();

				log.log(Level.INFO, project.getValue() + " " + project.getName());
            }
		}
		return "";


	}
}