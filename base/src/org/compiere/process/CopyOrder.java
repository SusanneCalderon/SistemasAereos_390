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
package org.compiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.DBException;
import org.apache.ecs.xhtml.param;
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProject;
import org.compiere.model.MProjectType;
import org.compiere.util.DB;

/**
 *	Copy Order and optionally close
 *	
 *  @author Jorg Janke
 *  @version $Id: CopyOrder.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class CopyOrder extends SvrProcess
{
	/** Order to Copy				*/
	private int 		p_C_Order_ID = 0;
	/** Document Type of new Order	*/
	private int 		p_C_DocType_ID = 0;
	/** New Doc Date				*/
	private Timestamp	p_DateDoc = null;
	/** Close/Process Old Order		*/
	private boolean 	p_IsCloseDocument = false;
	private int 		p_C_Project_ID;
	private int 		p_C_ProjectType_ID;
	private MProject 	m_Project = null;
	private MOrder 		from = null;
	private MBPartner 	m_bpartner = null;
	/** AD_Table_ID's of documents          */
	private static int[]  documentsTableID = null;
	
	/** Table Names of documents          */
	private static String[]  documentsTableName = null;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
        ProcessInfoParameter[] parameters = getParameter();
        for (ProcessInfoParameter parameter : parameters) {
			String name = parameter.getParameterName();
			if (parameter.getParameter() == null)
				;
			else if (name.equals("C_Order_ID"))
				p_C_Order_ID = parameter.getParameterAsInt();
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = parameter.getParameterAsInt();
			else if (name.equals("DateDoc"))
				p_DateDoc = parameter.getParameterAsTimestamp();
			else if (name.equals("IsCloseDocument"))
				p_IsCloseDocument = parameter.getParameterAsBoolean();
			else if (name.equals(MProject.COLUMNNAME_C_Project_ID))
				p_C_Project_ID = parameter.getParameterAsInt();
			else if (name.equals(MProject.COLUMNNAME_C_ProjectType_ID))
				p_C_ProjectType_ID = parameter.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		
		log.info("C_Order_ID=" + p_C_Order_ID 
			+ ", C_DocType_ID=" + p_C_DocType_ID 
			+ ", CloseDocument=" + p_IsCloseDocument);
		
		if (p_C_Order_ID == 0)
			throw new IllegalArgumentException("No Order");
		from = new MOrder (getCtx(), p_C_Order_ID, get_TrxName());
		m_bpartner= (MBPartner)from.getC_BPartner();
		if (p_C_DocType_ID ==0)
			p_C_DocType_ID = m_bpartner.get_ValueAsInt("C_DocType_ID");
		MDocType dt = MDocType.get(getCtx(), p_C_DocType_ID);		
		if (dt.get_ID() == 0)
			throw new IllegalArgumentException("No DocType");
		if (p_DateDoc == null)
			p_DateDoc = new Timestamp (System.currentTimeMillis());
		//
		CreateProject();
		from.setC_Project_ID(m_Project.getC_Project_ID());
		for (MOrderLine oLine:from.getLines())
		{
			oLine.setC_Project_ID(from.getC_Project_ID());
			oLine.saveEx();
		}
		MOrder newOrder = MOrder.copyFrom (from, p_DateDoc, 
			dt.getC_DocType_ID(), dt.isSOTrx(), false, true, get_TrxName());		//	copy ASI
		newOrder.setC_DocTypeTarget_ID(p_C_DocType_ID);
		if (from.getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal)
				|| from.getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation))
				newOrder.set_ValueOfColumn("C_Order_Quotation_ID", from.getC_Order_ID());
		boolean OK = newOrder.save();
		from.saveEx();
		if (!OK)
			throw new IllegalStateException("Could not create new Order");
		//
		updateC_Project_ID();
		if (p_IsCloseDocument)
		{
			MOrder original = new MOrder (getCtx(), p_C_Order_ID, get_TrxName());
			original.setDocAction(MOrder.DOCACTION_Complete);
			original.processIt(MOrder.DOCACTION_Complete);
			original.saveEx();
			original.setDocAction(MOrder.DOCACTION_Close);
			original.processIt(MOrder.DOCACTION_Close);
			original.saveEx();
		}
		//
	//	Env.setSOTrx(getCtx(), newOrder.isSOTrx());
	//	return "@C_Order_ID@ " + newOrder.getDocumentNo();
		return dt.getName() + ": " + newOrder.getDocumentNo();
	}	//	doIt
	
	private void CreateProject()throws Exception
	{
		if (p_C_Project_ID > 0)
		{
			m_Project = new MProject(getCtx(), p_C_Project_ID, get_TrxName());
			return;
		}
		if (from.getC_Project_ID()> 0)
		{
			m_Project = new MProject(getCtx(), from.getC_Project_ID(), get_TrxName());
			return;
		}
		m_Project = new MProject(getCtx(), 0, get_TrxName());
		m_Project.setC_BPartner_ID(from.getC_BPartner_ID());
		m_Project.setDateContract(p_DateDoc);
		m_Project.setC_Currency_ID(from.getC_Currency_ID());
		m_Project.set_ValueOfColumn(MOrder.COLUMNNAME_C_Activity_ID,m_bpartner.get_Value(MOrder.COLUMNNAME_C_Activity_ID));
		m_Project.set_ValueOfColumn(MOrder.COLUMNNAME_User1_ID,   m_bpartner.get_Value(MOrder.COLUMNNAME_User1_ID));
		m_Project.setName(m_bpartner.getName());
		m_Project.setSalesRep_ID(from.getSalesRep_ID());
		m_Project.saveEx();

		MProjectType type = new MProjectType (getCtx(), p_C_ProjectType_ID, get_TrxName());
		if (type.getC_ProjectType_ID() == 0)
			throw new IllegalArgumentException("Project Type not found C_ProjectType_ID=" + p_C_ProjectType_ID);

		//	Set & Copy if Service
		m_Project.setProjectType(type);
		if (!m_Project.save())
			throw new Exception ("@Error@");
	}
	
	private void updateC_Project_ID()
	{
		fillDocumentsTableArrays();
		for (int i = 0; i < documentsTableName.length; i++)
		{
			String TableName = documentsTableName[i];
			String updateSql = "Update " + TableName + " set c_project_ID=? where c_opportunity_ID =?";

		    ArrayList<Object> paramsControl = new ArrayList<Object>();
		    paramsControl.add(documentsTableID[i]);
		    String whereControl = "select ad_column_ID from ad_column where AD_table_ID =? and columnname = 'C_Project_ID'";
		    int columnID = DB.getSQLValueEx(get_TrxName(), whereControl, paramsControl);
		    if (columnID <= 0)
		    		continue;
		    ArrayList<Object> params = new ArrayList<Object>();
		    params.add(m_Project.getC_Project_ID());
		    params.add(from.getC_Opportunity_ID());
			DB.executeUpdateEx(updateSql, params.toArray(), get_TrxName());
		}
	}
	
	private static void fillDocumentsTableArrays() {
		if (documentsTableID == null) {
			String sql = "SELECT t.AD_Table_ID, t.TableName " +
					"FROM AD_Table t, AD_Column c " +
					"WHERE t.AD_Table_ID=c.AD_Table_ID AND " +
					"c.ColumnName='C_Opportunity_ID' AND " +
					"IsView='N' " +
					"ORDER BY t.AD_Table_ID";
			ArrayList<Integer> tableIDs = new ArrayList<Integer>();
			ArrayList<String> tableNames = new ArrayList<String>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					tableIDs.add(rs.getInt(1));
					tableNames.add(rs.getString(2));
				}
			}
			catch (SQLException e)
			{
				throw new DBException(e, sql);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			//	Convert to array
			documentsTableID = new int[tableIDs.size()];
			documentsTableName = new String[tableIDs.size()];
			for (int i = 0; i < documentsTableID.length; i++)
			{
				documentsTableID[i] = tableIDs.get(i);
				documentsTableName[i] = tableNames.get(i);
			}
		}
	}

}	//	CopyOrder