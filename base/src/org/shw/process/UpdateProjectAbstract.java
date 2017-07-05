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

import java.sql.Timestamp;
import org.compiere.process.SvrProcess;
/** Generated Process for (updateProjects)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public abstract class UpdateProjectAbstract extends SvrProcess
{
	/** Process Value 	*/
	private static final String VALUE = "updateProjects";
	/** Process Name 	*/
	private static final String NAME = "updateProjects";
	/** Process Id 	*/
	private static final int ID = 3000352;
 
	/**	Parameter Name for IsSummary	*/
	public static final String IsSummary = "IsSummary";
	/**	Parameter Name for DateContract	*/
	public static final String DateContract = "DateContract";
	/**	Parameter Name for C_Project_ID	*/
	public static final String C_Project_ID = "C_Project_ID";

	/**	Parameter Value for isSummaryLevel	*/
	private boolean isSummaryLevel;
	/**	Parameter Value for contractDate	*/
	private Timestamp contractDate;
	/**	Parameter Value for contractDateTo	*/
	private Timestamp contractDateTo;
	/**	Parameter Value for projectId	*/
	private int projectId;
 

	@Override
	protected void prepare()
	{
		isSummaryLevel = getParameterAsBoolean(IsSummary);
		contractDate = getParameterAsTimestamp(DateContract);
		contractDateTo = getParameterToAsTimestamp(DateContract);
		projectId = getParameterAsInt(C_Project_ID);
	}

	/**	 Getter Parameter Value for isSummaryLevel	*/
	protected boolean isSummaryLevel() {
		return isSummaryLevel;
	}

	/**	 Getter Parameter Value for contractDate	*/
	protected Timestamp getContractDate() {
		return contractDate;
	}

	/**	 Getter Parameter Value for contractDateTo	*/
	protected Timestamp getContractDateTo() {
		return contractDateTo;
	}

	/**	 Getter Parameter Value for projectId	*/
	protected int getProjectId() {
		return projectId;
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