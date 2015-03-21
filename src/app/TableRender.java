package app;

import java.util.LinkedList;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class TableRender extends AbstractTableModel {

    static LinkedList<Question> questions;
    static LinkedList<Object[]> tableQuestions;

    static {
        questions = new LinkedList<>();
        tableQuestions = new LinkedList<>();
    }

    SharedSelectionListener listener;

    private int questionRow = -1;

    private static Question question;

    String[] sourceColNames = {"№", "Вопрос"};

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

    public Object[] rend(int n, Question question) {

        Object[] s = {n + 1, question.getQuestion()};

        return s;
    }

    public TableRender(JTable table, LinkedList<Question> questions) {

        this.table = table;
        this.answerPreview = Root.answerPane;
        this.tableQuestions = new LinkedList<>();
        this.questions = questions;

        // add sources to the table
        questions.forEach(question -> {
            tableQuestions.add(rend(tableQuestions.size(), question));
        });

        table.setModel(this);

        fireTableDataChanged();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        table.getColumnModel().getColumn(0).setWidth(45);
        table.getColumnModel().getColumn(0).setMinWidth(45);
        table.getColumnModel().getColumn(0).setMaxWidth(45);

        listener = new SharedSelectionListener();
        table.getSelectionModel().addListSelectionListener(listener);

    }

    class SharedSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            questionRow = table.getSelectedRow();

            if (questionRow != -1) {

                try {
                    question = questions.get(questionRow);
                    question.setPreview();

                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
            }

        }

    }

}
