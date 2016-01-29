package cn.denghui.smali2java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Enginer {
	public static String result_path = "C:\\Users\\kendy\\Desktop\\1";
	public static String src_dir = "E:\\SmaliTool\\result\\smali\\360MobileSafe_5_2_2_1023_apk\\1s";
	
	public static void main(String[] args) throws IOException {
		
		File smaliFile = new File(src_dir);
		List<File> list = new ArrayList<File>();
		
		FileUtil.listFiles(smaliFile, list);

		for (File path : list) {
			//解析文件开始
			Data.initPool();
			
			String filePath = path.getPath().substring(src_dir.length()+1).replace(".smali", ".java");
			List<String> readFile = FileUtil.readFile(path);
			
			for (String line2 : readFile) {
				String line = line2.trim();
				if(line.equals(""))continue;
				if(Command.IS_NO_VALUE_CMD(line))continue;
				parseLine(line);
			}
			if(Data.inClass) Data.putSuffix();
			Data.printPool(result_path+"\\"+filePath);
			
		}
		
	}
	
	
	
	private static void lineAfter() {
		//每行结束后的处理
		if(!Data.isPoolNull() && Data.getPoolSize()>2){
			String up_line = Data.pop();
			String up_up_line = Data.pop();
			if(up_up_line!=null && up_up_line.contains(" {v} ")){
				up_up_line = up_up_line.substring(up_up_line.indexOf(" = ")+3);
				Data.putLine(up_up_line);
				Data.push(up_line);
				return;
			}
			Data.push(up_up_line);
			Data.push(up_line);
		}
	}

	public static void parseLine(String line){
		//每行结束后的处理,主要为删除{v}
		lineAfter();
		
		//方法外的指令处理
		boolean classCmd = ClassLineParser.parseLine(line);
		
		if(classCmd) {
			return;
		}
		boolean switchCmd = SwitchParser.parseLine(line);
		if(switchCmd) {
			return;
		}
//		boolean ifCmd = IFParser.parseLine(line);
//		if(ifCmd) {
//			System.out.println("if line:"+line);
//			return;
//		}
		boolean methodCmd = LineParser.parseLine(line);
		if(methodCmd) return;
		boolean otherCmd = OTHER_PRE_CMD(line);
		if(otherCmd) return;
		
	}
	
	public static boolean OTHER_PRE_CMD(String line){
		if(line.startsWith(":goto")){
			Data.putLine(line);
		}else if(line.startsWith(":cond")){
			Data.putLine(line);
		}else {
			return false;
		}
		return true;
	}
	
}
