package app;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;


public class TableRender extends AbstractTableModel {


	SharedSelectionListener listener = new SharedSelectionListener();
	
	private int questionRow = -1;
	
	private static Question question;
	
	String [] sourceColNames = {"Документ", "№", "Вопрос", "Ссылка"};
	
	static LinkedList<Question> questions = new LinkedList<>();
	static LinkedList<Object[]> tableQuestions = new LinkedList<>();
	
	JTable table;
	static JEditorPane answerPreview;

	
	@Override
	public String getColumnName(int column) {
		
		return sourceColNames[column];
	
	}
	
	@Override
	public int getColumnCount() {
		return sourceColNames.length;
	}

	@Override
	public int getRowCount() {

		return tableQuestions.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return tableQuestions.get(rowIndex)[columnIndex];
	}
	
	
	
	public Object [] rend(Question question) {
		
		Object [] s = {question.getDocName(), question.getQuestionNumber(),
						question.getQuestion(), question.getLink()};
		
		return s;
	}
	

	public TableRender(JTable table, LinkedList <Question> questions) {
		
		this.table = table;
		this.answerPreview = Root.answerPane;
		this.tableQuestions = new LinkedList<>();
		this.questions = questions;
		
		// add sources to the table

		questions.forEach(question -> {
			tableQuestions.add(rend(question));
		});
		
		table.setModel(this);
		
		fireTableDataChanged();	
		
		table.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
		
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(1).setWidth(35);
		table.getColumnModel().getColumn(1).setMinWidth(35);
		table.getColumnModel().getColumn(1).setMaxWidth(35);
		table.getColumnModel().getColumn(2).setMinWidth(600);
		
			
		
		table.getSelectionModel().removeListSelectionListener(listener);
		listener = new SharedSelectionListener();
		table.getSelectionModel().addListSelectionListener(listener);
		
	
	}
	
	class SharedSelectionListener implements ListSelectionListener {


		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			questionRow = table.getSelectedRow();
			
			if (questionRow != -1 ) {
				
				try {
					
					question = questions.get(questionRow);
					table.setToolTipText(question.getQuestion());
					Root.setPreview(question.getAnswer());
					
				
				} catch (IndexOutOfBoundsException ex) {
					return;
				}
				
			
			}
			
		}
		
	}
	
}

