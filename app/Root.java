package app;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinTask;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;


public class Root extends LinkedList <QuestionDoc> {
	
	static JTextField searchBox;
	static JEditorPane answerPane;
	static TableRender render;
	
	public static JTextField getSearchBox() {
		return searchBox;
	}
	
	public static void setPreview(String answer) {
		answerPane.setText(answer);
	}
	
	private static StopWatch stopwatch;
	private static int current = 0;
	private static Double average = 0.0;
	
	public static void main(String [] args) throws FileNotFoundException, IOException, URISyntaxException {
		
		
		UI ui = new UI();
		answerPane = ui.editorPane;
		searchBox = ui.searchBox;

		
		Root root = new Root();

		JFileChooser dir = new JFileChooser(System.getProperty("user.home") + "\\Desktop");
		dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		
		int result = dir.showDialog(ui,"Выбрать папку");
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			Collection <File> files = FileUtils.listFiles(dir.getSelectedFile(), 
					new RegexFileFilter(".+(?<!_о).doc"),
					DirectoryFileFilter.DIRECTORY);
			
			
			Loading loading = new Loading();
			loading.setVisible(true);
			
			loading.button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Runtime.getRuntime().exit(1);
				}
			});
			
			
			
			// total questions to load
			final int total = files.size();
			
			loading.progressBar.setMaximum(total);
			

			ArrayList <Double> times = new ArrayList<>();
				
			files.forEach(file -> {
				
				stopwatch = new StopWatch();
				
				root.add(new QuestionDoc(file.getPath()));
				
				times.add(stopwatch.elapsedTime());
				
				current+=1;
				int stack = total-current;
				
				times.forEach(time -> average+=time);
				
				average/=times.size();
				double approximateTime = average * stack;
				
				String text;
				
				if (approximateTime >= 60)  {
					text = new BigDecimal(approximateTime/60).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
						.replaceFirst("\\.", " мин. ") + " сек.";
				}
				else {
					text = (int) approximateTime + " сек."; 
				}
				
				loading.label.setText("Ожидание: ~ " + text);
				
				loading.progressBar.setValue(current);
				
				loading.setTitle("Загрузка компонентов... ("+current + " из " + total+")");
				
			});
			
			loading.dispose();
		
			ui.setVisible(true);
		} else {
			Runtime.getRuntime().exit(1);
		}
		
		LinkedList <Question> questions = new LinkedList<>();
		
		root.forEach(questionDocument -> {
			
			questionDocument.forEach(question -> { 
				
				questions.add(question);
			
			});
				
		});
		
		render = new TableRender(ui.table, questions);
		
		searchBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					
					if (searchBox.getText().isEmpty()) {
						
						render = new TableRender(ui.table, questions);
						
						//refresh all documents
						
					}
					
					else {
						
						String creteria = searchBox.getText();
						
						LinkedList<Question> questions = new LinkedList<>();
						
						root.forEach(doc -> {
							
							doc.forEach(question -> {
								
								boolean isValid = 
										Pattern.compile(".*"+creteria+".*", Pattern.CASE_INSENSITIVE)
											.matcher(question.getQuestion()).matches();
								
								if (isValid) {
									questions.add(question);
								}
								
							});
						});
						
						render = new TableRender(ui.table, questions);
						
						
					}
					
				}
				
			}
		});

	}
	
}
