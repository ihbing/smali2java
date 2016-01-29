package cn.denghui.smali2java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data {
	
	/* 
	 * 
	 * �ѽ������
	 * 1��ָ�����ת������	
	 * 2���Ĵ���ȡֵ��ֵ����
	 * 3��unicode����ת����16����ת������
	 * 4���Ĵ�����ֵ�������������
	 * 5�����׶�������
	 * 6��switch�ṹ����
	 * 7����û��classʱ��Щ�������ת��
	 * 8��if�ṹ����
	 * 9��ѭ���ṹ����
	 * 10��if�б�����������,����ֱ�����Ӳ���ֵ���Ĵ���
	 * 11��long��double��Ҫ�����Ĵ���
	 * 
	 * δ���
	 * 1��if���жϿ�����booleanֵ,��������ֵ,������class����ֵ
	 * 2���������ظ�����
	 * 4��try-catch��ṹ����
	 * 
	 * 
	 * 
	 */
	
	//�洢ָ���ַ���
	public static HashMap<String, String[]> cmdData;
	//����������ת�����췽����
	public static String className = null;
	//�Ƿ����࣬�ж��Ƿ�Ҫ���ļ�β�Ӻ�׺
	public static boolean inClass = false;
	//���ݾ�̬������񣬼Ĵ����Ĵ�С��ָ��λ��(this)λ�Ƿ�ֵ
	public static boolean isStaticMethod = false;
	public static int FourZ_SIZE = 0;
	
	//�����в�������
	public static int jd_params_num = 0;
	//�����д�Ų���������
	public static List<String> params = null;
	//�洢method�мĴ�����ֵ
	public static String[] v_value = null;
	//�洢�����Ľ��
	private static List<String> pool = null;
	
	public static void initPool(){
		cmdData = Command.initCmdData();
		pool = new ArrayList<String>();
		v_value = new String[30]; //��û��Locals��Ĭ����30���洢�ռ�
		params = null;
		jd_params_num=0;
		
		isStaticMethod = false;
		inClass = false;
		className = null;
		
		FourZ_SIZE = 0;
	}
	
	public static void putPrefix(){
		putLine("{");
		FourZ_SIZE ++ ;
	}
	
	public static void putSuffix() {
		FourZ_SIZE -- ;
		putLine("}");
	}
	
	public static void putValue(String v,String value) {
		int index = Integer.valueOf(v.substring(1));
		Data.v_value[index] = value;
	}
	
	public static String getValue(String v) {
		int index = Integer.valueOf(v.substring(1));
		if(Data.v_value != null && index<Data.v_value.length && Data.v_value[index]!=null && !Data.v_value[index].equals("")){
			return Data.v_value[index];
		}
		return v;
	}
	
	public static void putLine(String line){
		String kongge = FourZ_KONG();
		push(kongge+line);			
	}
	
	public static boolean isPoolNull(){
		if(pool==null)return true;
		return false;
	}
	public static int getPoolSize(){
		return pool.size();
	}
	
	public static void push(String line){
		pool.add(line);
	}
	
	public static String pop(){
		return pool.remove(pool.size()-1);
	}
	
	private static String FourZ_KONG() {
		if(FourZ_SIZE < 0 ){
			FourZ_SIZE = 0 ;
		}
		String result = "";
		for (int i = 0; i < FourZ_SIZE; i++) {
			result+="    ";
		}
		return result;
	}

	
	
	
	
	
	public static void printPool(String filePath) throws IOException{
		
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
            file.createNewFile();   
        }
		PrintWriter ps = new PrintWriter(file);
		for (String line : pool) {
			ps.println(line);
		}
		ps.flush();
		ps.close();
	}
	
	public static void printV(){
		for (int i = 0; i < v_value.length; i++) {
			System.out.println("v["+i+"] = "+v_value[i]);
		}
	}
	
//	public static void printLine(String line){
//		System.out.println(line);
//	}
	
	public static void printList(List<String> list){
		for (String line : list) {
			System.out.println(line);
		}
	}
}
