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

package org.shw.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoiceBatch;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MTab;
import org.compiere.model.MUOMConversion;
import org.compiere.util.Env;

/**
 *
 * @author  Ashley G Ramdass
 */
public class Callout_SHW extends CalloutEngine
{

    /**
    *  docType - set document properties based on document type.
    *  @param ctx
    *  @param WindowNo
    *  @param mTab
    *  @param mField
    *  @param value
    *  @return error message or ""
    */
   public String invoiceBatch (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
   {
       Integer C_InvoiceBatch_ID = (Integer)value;
       if (C_InvoiceBatch_ID == 0)
    	   return "";
       MInvoiceBatch ib = new MInvoiceBatch(Env.getCtx(), C_InvoiceBatch_ID, null);
       mTab.setValue(MInvoiceBatch.COLUMNNAME_C_InvoiceBatch_ID, ib.getDocumentAmt());  
       
       return "";
   }

   public String callout (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
   {
       return "";
   }
   public String paymentDocType (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
   {
	   Properties A_Ctx = ctx;
       Integer C_DocType_ID = (Integer)value;
       if (C_DocType_ID == 0)
    	   return "";
       MDocType dt = new MDocType(A_Ctx, C_DocType_ID, null);
       Boolean isReceipt = dt.getDocBaseType().equals(MDocType.DOCBASETYPE_APPayment)? false:true;
       mTab.setValue(MPayment.COLUMNNAME_IsReceipt, isReceipt);
       if (isReceipt)
    	   mTab.setValue(MPayment.COLUMNNAME_TenderType, 'X');
       else
    	   mTab.setValue(MPayment.COLUMNNAME_TenderType, 'K');  
	   
       return "";
   }
   
   
  
   
}

