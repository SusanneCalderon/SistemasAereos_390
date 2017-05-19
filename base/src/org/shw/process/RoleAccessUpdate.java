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
package org.shw.process;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MRole;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 *	Update Role Access
 *	
 *  @author Jorg Janke
 *  @version $Id: RoleAccessUpdate.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 * 
 * @author Teo Sarca, teo.sarca@gmail.com
 * 		<li>BF [ 3018005 ] Role Access Update: updates all roles if I log in as System
 * 			https://sourceforge.net/tracker/?func=detail&aid=3018005&group_id=176962&atid=879332
 */
public class RoleAccessUpdate extends SvrProcess
{
	/**	Update Role				*/
	private int	P_AD_Role_ID = -1;
	/**	Update Roles of Client	*/
	private int	P_AD_Client_ID = -1;
	
	
	/**
	 * 	Prepare
	 */
	protected void prepare ()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Role_ID"))
				P_AD_Role_ID = para[i].getParameterAsInt();
			else if (name.equals("AD_Client_ID"))
				P_AD_Client_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		log.info("AD_Client_ID=" + P_AD_Client_ID + ", AD_Role_ID=" + P_AD_Role_ID);
		String resultRole = "";
		String result = "";
		//
		
			List<Object> params = new ArrayList<Object>();
			String whereClause = "1=1";
			if (P_AD_Client_ID > 0)
			{
				whereClause += " AND AD_Client_ID=? ";
				params.add(P_AD_Client_ID);
			}
			if (P_AD_Role_ID >= 0) // System Role
			{
				whereClause += " AND AD_Role_ID=?";
				params.add(P_AD_Role_ID);
			}
			//sql += "ORDER BY AD_Client_ID, Name";
			
			List<MRole> roles = new Query(getCtx(), MRole.Table_Name, whereClause, get_TrxName())
			.setOnlyActiveRecords(true)
			.setParameters(params)
			.setOrderBy("AD_Client_ID, Name")
			.list();
			
			for (MRole role : roles)
			{
				
				String roleClientOrgUser = role.getAD_Role_ID() + ","
					+ getAD_Client_ID() + "," + role.getAD_Org_ID() + ",'Y', SysDate," 
					+ role.getUpdatedBy() + ", SysDate," + role.getUpdatedBy() 
					+ ",'Y' ";	//	IsReadWrite
				
				String sqlWindow = "INSERT INTO AD_Window_Access "
					+ "(AD_Window_ID, AD_Role_ID,"
					+ " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,IsReadWrite) "
					+ "SELECT DISTINCT w.AD_Window_ID, " + roleClientOrgUser
					+ "FROM AD_Window w"
					+ " INNER JOIN AD_Tab t ON (w.AD_Window_ID=t.AD_Window_ID)"
					+ " INNER JOIN AD_Table tt ON (t.AD_Table_ID=tt.AD_Table_ID) "
					+ " WHERE t.SeqNo=(SELECT MIN(SeqNo) FROM AD_Tab xt "	// only check first tab			
					+ "WHERE xt.AD_Window_ID=w.AD_Window_ID)"
					+ " and not exists(select 1 from ad_window_Access wa where ad_role_ID =? and wa.ad_window_ID = w.ad_window_ID)"
					+ " AND tt.AccessLevel IN ";
				
				String sqlProcess = "INSERT INTO AD_Process_Access "
					+ "(AD_Process_ID, AD_Role_ID,"
					+ " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,IsReadWrite) "
					+ "SELECT DISTINCT p.AD_Process_ID, " + roleClientOrgUser
					+ "FROM AD_Process p "
					+ " WHERE not exists(select 1 from ad_process_Access pa where ad_role_ID = ? and pa.ad_process_ID = p.ad_process_ID)"
					+ " AND AccessLevel IN ";

				String sqlForm = "INSERT INTO AD_Form_Access "
					+ "(AD_Form_ID, AD_Role_ID," 
					+ " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,IsReadWrite) "
					+ "SELECT f.AD_Form_ID, " + roleClientOrgUser
					+ "FROM AD_Form f "
					+ " where not exists(select 1 from ad_form_Access fa where ad_role_ID =? and fa.ad_form_ID = f.ad_form_ID)"
					+ " AND AccessLevel IN ";
				
				String sqlBrowse = "INSERT INTO AD_Browse_Access "
					+ "(AD_Browse_ID, AD_Role_ID," 
					+ " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,IsReadWrite) "
					+ "SELECT b.AD_Browse_ID, " + roleClientOrgUser
					+ "FROM AD_Browse b "
					+ "  where not exists(select 1 from ad_Browse_Access ba where ad_role_ID =? and ba.ad_browse_ID = b.ad_browse_ID)"
					+ " AND AccessLevel IN ";

				String sqlWorkflow = "INSERT INTO AD_WorkFlow_Access "
					+ "(AD_WorkFlow_ID, AD_Role_ID,"
					+ " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,IsReadWrite) "
					+ "SELECT w.AD_WorkFlow_ID, " + roleClientOrgUser
					+ "FROM AD_WorkFlow w "
					
					+ "  where not exists(select 1 from ad_workflow_Access wa where ad_role_ID =? and wa.ad_workflow_ID = w.ad_workflow_ID)"
					+ " AND AccessLevel IN ";

				

				/**
				 *	Fill AD_xx_Access
				 *	---------------------------------------------------------------------------
				 *	SCO# Levels			S__ 100		4	System info
				 *						SCO	111		7	System shared info
				 *						SC_ 110		6	System/Client info
				 *						_CO	011		3	Client shared info
				 *						_C_	011		2	Client
				 *						__O	001		1	Organization info
				 *	Roles:
				 *		S		4,7,6
				 *		_CO		7,6,3,2,1
				 *		__O		3,1,7
				 */
				String roleAccessLevel = null;
				String roleAccessLevelWin = null;
				if (MRole.USERLEVEL_System.equals(role.getUserLevel()))
					roleAccessLevel = "('4','7','6')";
				else if (MRole.USERLEVEL_Client.equals(role.getUserLevel()))
					roleAccessLevel = "('7','6','3','2')";
				else if (MRole.USERLEVEL_ClientPlusOrganization.equals(role.getUserLevel()))
					roleAccessLevel = "('7','6','3','2','1')";
				else //	if (USERLEVEL_Organization.equals(getUserLevel()))
				{
					roleAccessLevel = "('3','1','7')";
					roleAccessLevelWin = roleAccessLevel
						+ " AND w.Name NOT LIKE '%(all)%'";
				}
				if (roleAccessLevelWin == null)
					roleAccessLevelWin = roleAccessLevel;
				//
				//
				int win = DB.executeUpdate(sqlWindow + roleAccessLevelWin,role.getAD_Role_ID(), get_TrxName());
				int proc = DB.executeUpdate(sqlProcess + roleAccessLevel, role.getAD_Role_ID(),get_TrxName());
				int form = DB.executeUpdate(sqlForm + roleAccessLevel, role.getAD_Role_ID(),get_TrxName());
				int browse = DB.executeUpdate(sqlBrowse + roleAccessLevel, role.getAD_Role_ID(),get_TrxName());
				int wf = DB.executeUpdate(sqlWorkflow + roleAccessLevel,role.getAD_Role_ID(), get_TrxName());			
				role.loadAccess(true);
				resultRole = "@AD_Window_ID@ #" + win 
						+ " -  @AD_Process_ID@ #" + proc
						+ " -  @AD_Form_ID@ #" + form
						+ " -  @AD_Browse_ID@ #"+ browse
						+ " -  @AD_Workflow_ID@ #" + wf
						+ " -  @DocAction@ #";
				result = result + resultRole;
			}
		
			return result;
	}	//	doIt

	/**
	 * 	Update Role
	 *	@param role role
	 */
	
	//add main method, preparing for nightly build
	
	
	
	
}	//	RoleAccessUpdate
