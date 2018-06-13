package model.CPMRM;

import java.util.ArrayList;
import java.util.Iterator;


public class StandardCPStage {
	public String name; //阶段时间
	public ArrayList<String> coreActivities;//主要诊疗活动
	public ArrayList<String> coreOrdersLong;//长期医嘱
	public ArrayList<String> coreOrdersCur;//临时医嘱
	public ArrayList<String> coreOrdersOut;//出院医嘱
	public ArrayList<String> coreServices;//主要护理

	public StandardCPStage(){
		name = new String();
		coreActivities = new ArrayList<String>();
		coreOrdersLong = new ArrayList<String>();
		coreOrdersCur = new ArrayList<String>();
		coreOrdersOut = new ArrayList<String>();
		coreServices = new ArrayList<String>();
	}

	public String getCoreActivities()
	{
		StringBuffer result = new StringBuffer();
		for(Iterator<String> iterator = coreActivities.iterator();iterator.hasNext();)
		{
			result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
        }
		return result.toString();
	}

	public String getCoreOrders()
	{
		StringBuffer result = new StringBuffer();
		Iterator<String> iterator = coreOrdersLong.iterator();
		if(iterator.hasNext())
		{
			result.append("长期医嘱\n");
			for(;iterator.hasNext();)
			{
				result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
	        }
			result.append("\n");
		}
		iterator = coreOrdersCur.iterator();
		if(iterator.hasNext())
		{
			result.append("临时医嘱\n");
			for(;iterator.hasNext();)
			{
				result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
	        }
			result.append("\n");
		}

		iterator = coreOrdersOut.iterator();
		if(iterator.hasNext())
		{
			result.append("出院医嘱\n");
			for(;iterator.hasNext();)
			{
				result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
	        }
			result.append("\n");
		}
		return result.toString();
	}

	public String getCoreOrdersLongAndOut()
	{
		StringBuffer result = new StringBuffer();
		Iterator<String> iterator = coreOrdersLong.iterator();
		if(iterator.hasNext())
		{
			result.append("长期医嘱\n");
			for(;iterator.hasNext();)
			{
				result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
	        }
			result.append("\n");
		}
		iterator = coreOrdersOut.iterator();
		if(iterator.hasNext())
		{
			result.append("出院医嘱\n");
			for(;iterator.hasNext();)
			{
				result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 200)+"\n");
	        }
			result.append("\n");
		}
		return result.toString();
	}

	public String getCoreServices()
	{
		StringBuffer result = new StringBuffer();
		for(Iterator<String> iterator = coreServices.iterator();iterator.hasNext();)
		{
			result.append("□ "+StringRebuilder.doRebuildHasSpace(iterator.next(), 150)+"\n");
        }
		return result.toString();
	}

	public String[] getAllContent()
	{
		String[] result = new String[4];
		result[0] = StringRebuilder.doRebuild(name, 53);
		result[1] = getCoreActivities();
		result[2] = getCoreOrders();
		result[3] = getCoreServices();
		return result;
	}

	public String getName()
	{
		return StringRebuilder.doRebuild(name, 53);
	}



}
