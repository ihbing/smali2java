package cn.denghui.smali2java;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SwitchParser {
	
	public static String[] command = new String[]{
		"packed-switch",	//switch��ʼ
		":goto_",			//����ת�ĵ�ַ
		"goto",				//��ʼ��ת
		":pswitch_data_",	//�洢���ݿ�ʼ
		".packed-switch",	//�洢��ʼֵ
		":pswitch_",		//��˳���Ӧ��ֵ
		".end packed-switch"//����switch
	};
	
	//�ж�line�Ƿ���switch��
	public static boolean inSwitch = false;		//0��ʼֵ   1��ʼswitch 2����switch
	//switchҪ��ת�ĵط�
	
	public static List<String> tmp_data = null;		//�����ʱ����
	
	private static int switch_begin = 0;
	private static int case_data_begin = 0;
	private static int switch_end = 0;
	private static boolean ispacked = false;
	
	private static int case_init_value = 0;
	private static Map<String,String> case_value = null;
	
	private static void initSwitchData(){
		switch_begin = 0;
		case_data_begin = 0;
		switch_end = 0;
		case_init_value = 0;
		case_value = new HashMap<String,String>();
		tmp_data = new ArrayList<String>();
	}
	
	public static boolean parseLine(String line){
		if(line.startsWith("packed-switch") || line.startsWith("sparse-switch")){
			initSwitchData();		//���绯����
			inSwitch = true;
			tmp_data.add(line);
		}else if(line.startsWith(".end packed-switch")||line.startsWith(".end sparse-switch")){
			
			tmp_data.add(line);
			
			initParamData(tmp_data);// ������ʱ����
			List<String> switch_data = handle(tmp_data);
			
			for (String line_data : switch_data) {
				parseLineData(line_data);
			}
			inSwitch = false;
		}else if(inSwitch){
			tmp_data.add(line);
		}else{
			return false;
		}
		return true;
	}
	
	private static void parseLineData(String line_data) {
		if(line_data.startsWith(":pswitch_") || line_data.startsWith(":sswitch_")){
			Data.putLine("case "+case_value.get(line_data)+":");
		}else if(line_data.startsWith(".end packed-switch")||line_data.startsWith(".end sparse-switch")){
			Data.putSuffix();
		}else if(line_data.startsWith(":goto") || line_data.startsWith(":cond")){
			Data.putLine(line_data);
		}else{
			LineParser.parseLine(line_data);
		}
	}

	private static void initParamData(List<String> result){
		boolean isCaseValue = false;
		int init = 0;
		int linenum = 0;
		for (String line : result) {
			linenum++;
			line = line.trim();
			if(line.startsWith("packed-switch")||line.startsWith("sparse-switch")){
				switch_begin = linenum;
				ispacked = true;
			}else if(line.startsWith(".packed-switch")){
				isCaseValue = true;
				case_init_value = Integer.valueOf(UnicodeUtil.translateValue(line.substring(line.indexOf(" ")+1)));
			}else if(line.startsWith(".sparse-switch")){
				isCaseValue = true;
			}else if(line.startsWith(".end packed-switch")||line.startsWith(".end sparse-switch")){
				isCaseValue = false;
				switch_end = linenum;
				ispacked = false;
			}else if(isCaseValue){
				if(ispacked){
					case_value.put(line, case_init_value+init+++"");
				}else{
					String[] split = line.split(" -> ");
					case_value.put(split[1], UnicodeUtil.translateValue(split[0]));
				}
			}else if(line.startsWith(":pswitch_data_")||line.startsWith(":sswitch_data_")){
				case_data_begin = linenum;
			}
		}
	}
	//���������ִ����˳��
	private static List<String> handle(List<String> list) {
		List<String> list_result = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if(i+1>=switch_begin && i+1 < case_data_begin){
				list_result.add(list.get(i));
			}else if(i+1 == switch_end){
				list_result.add(list.get(i));
			}
		}
		return list_result;
	}
}
