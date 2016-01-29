package cn.denghui.smali2java;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;


public class SmaliTranslateFrame extends JFrame{
	private JTextArea jtext_left;
	private JTextArea jtext_right;
	private JPanel panel;
	private String javaPath;
	public static void main(String[] args) {
		SmaliTranslateFrame frame =  new SmaliTranslateFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public SmaliTranslateFrame() {
		initSize();
		initTextArea();
		initToolBar();
		//注册拖拽事件
		new DropTarget(jtext_left, DnDConstants.ACTION_COPY_OR_MOVE,
				new DropFileListener());
	}
	
	private void initToolBar() {
		JToolBar jToolBar = new JToolBar();
		JButton jbt2 = new JButton("打开文件");
		jbt2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(javaPath==null)return;
					Runtime.getRuntime().exec("cmd.exe /c start " + javaPath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		jToolBar.add(jbt2);
		this.add(jToolBar,"North");
	}

	private void initSize() {
		Dimension displaysize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(displaysize.width / 3 * 2, displaysize.height / 5 * 4);
		this.setLocation((displaysize.width - getSize().width) / 2, (displaysize.height - getSize().height) / 2);
		this.setTitle("smali翻译");
	}
	
	private void initTextArea() {
		jtext_left = new JTextArea();
		Font font = this.jtext_left.getFont();
		jtext_left.setText("将文件拖进来...");
	    this.jtext_left.setFont(font.deriveFont(15.0F));
	    this.jtext_left.setMargin(new Insets(10, 10, 10, 10));
		JScrollPane jScrollPane = new JScrollPane(jtext_left);
		jScrollPane.setViewportView(jtext_left);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		jtext_right = new JTextArea();
	    this.jtext_right.setFont(font.deriveFont(15.0F));
	    this.jtext_right.setMargin(new Insets(10, 10, 10, 10));
		JScrollPane jScrollPane2 = new JScrollPane(jtext_right);
		jScrollPane2.setViewportView(jtext_right);
		jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(jScrollPane);
		panel.add(jScrollPane2);
		this.add(panel);
	}
	
	
	public void copyFile(File srcFile,File descFile) throws IOException{
		jtext_left.setText("");
		FileInputStream fis = new FileInputStream(srcFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		PrintWriter pw = new PrintWriter(descFile);
		String line = null;
		while((line = br.readLine())!=null){
			if(line.indexOf("package")!=-1){
				continue;
			}
			jtext_left.append(line+"\n");
			pw.println(line);
		}
		br.close();
		pw.close();
		jtext_left.setCaretPosition(0);
	}
	
	public void dropDone(File file) {
		//读取文件内容
		try {
			javaPath = file.getPath().replace(".smali", ".java");
			List<String> readFile = FileUtil.readFile(file);
			showText(jtext_left,readFile);
			
			Data.initPool();
			for (String line2 : readFile) {
				String line = line2.trim();
				if(line.equals(""))continue;
				if(Command.IS_NO_VALUE_CMD(line))continue;
				Enginer.parseLine(line);
			}
			if(Data.inClass) Data.putSuffix();
			Data.printPool(javaPath);
			
			List<String> result = FileUtil.readFile(new File(javaPath));
			showText(jtext_right,result);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private void showText(JTextArea jtext,List<String> content) {
		jtext.setText("");
		for (String line : content) {
			jtext.append(line+"\n");
		}
	}
	
	public class DropFileListener implements DropTargetListener {
		// 处理文件拖拽事件
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {}
		@Override
		public void dragOver(DropTargetDragEvent dtde) {}
		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		@Override
		public void dragExit(DropTargetEvent dte) {}
		@Override
		public void drop(DropTargetDropEvent dtde) {
			try {
				Transferable tr = dtde.getTransferable();
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(3);
					List<File> list = (List<File>)tr.getTransferData(DataFlavor.javaFileListFlavor);
					if(list.size()!=1 || !list.get(0).isFile()){
						jtext_left.setText("只支持拖拽一个文件");
						return;
					}
					dropDone(list.get(0));
					dtde.dropComplete(true);
				}else{
					dtde.rejectDrop();
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
