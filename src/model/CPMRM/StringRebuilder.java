package model.CPMRM;

import org.netlib.util.intW;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.text.Font;

public class StringRebuilder {

	static Font font = Font.getDefault();
	static FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
	//根据长度重排
	static public String doRebuildHasSpace(String src,int len)
	{
		StringBuffer s = new StringBuffer();
		int pre = 0;
		for(int index = 1; index < src.length();++index)
		{
			if(fm.computeStringWidth(src.substring(pre, index))>=len)
			{
				s.append(src.substring(pre,index)+"\n   ");
				pre = index;
			}
		 }
		s.append(src.substring(pre));
		return s.toString();
    }

	static public String doRebuild(String src,int len)
	{
		StringBuffer s = new StringBuffer();

		int pre = 0;
		for(int index = 1; index < src.length();++index)
		{
			if(fm.computeStringWidth(src.substring(pre, index))>=len)
			{
				s.append(src.substring(pre,index)+"\n");
				pre = index;
			}
		 }
		s.append(src.substring(pre));
		return s.toString();
    }
}