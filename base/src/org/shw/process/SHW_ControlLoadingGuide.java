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

import java.math.BigDecimal;

import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.eevolution.model.MWMInOutBound;
import org.eevolution.model.MWMInOutBoundLine;

/**
 *  Creates Payment from c_invoice, including Aging
 *
 *  @author Susanne Calderon
 */

public class SHW_ControlLoadingGuide  extends SvrProcess
{
    
    
    @Override    
    protected void prepare()
    {    	
    }
    
    
    
    @Override
    protected String doIt() throws Exception
    {	
    	
    	MWMInOutBound iobound = new MWMInOutBound(getCtx(), getRecord_ID(), get_TrxName());
    	if (iobound.getWeight().compareTo(Env.ZERO) ==0 && iobound.getVolume().compareTo(Env.ZERO) == 0)
    	{
    		iobound.set_ValueOfColumn("IsConfirmed", true);
    		iobound.saveEx();
    		return "";
    	}
    	Boolean weightapproved = iobound.getWeight().compareTo(Env.ZERO)==0?true:false;
    	Boolean volumneapproved = iobound.getVolume().compareTo(Env.ZERO)==0?true:false;
    	BigDecimal weight = Env.ZERO;
    	BigDecimal volume = Env.ZERO;
    	BigDecimal lineweight = Env.ZERO;
    	BigDecimal linevolume = Env.ZERO;
    	for (MWMInOutBoundLine ioLine:iobound.getLines(true, ""))
    	{
    		lineweight = (BigDecimal)ioLine.get_Value(MWMInOutBound.COLUMNNAME_Weight);
    		linevolume = (BigDecimal)ioLine.get_Value(MWMInOutBound.COLUMNNAME_Volume);
    		weight = weight.add(lineweight);
    		volume = volume.add(linevolume);    		
    	}
    	if (iobound.getWeight().compareTo(Env.ZERO)!=0)
    		weightapproved = iobound.getWeight().longValue()>weight.longValue()?true:false;
    	if (iobound.getVolume().compareTo(Env.ZERO)!=0)	
    		volumneapproved = iobound.getVolume().longValue()>volume.longValue()?true:false;
    	if (weightapproved && volumneapproved)
    		iobound.set_ValueOfColumn("IsConfirmed", true);
    	else 
    		iobound.set_ValueOfColumn("IsConfirmed", false);   
    	iobound.saveEx();
    	return "";
    }
    
    
    
    
}
