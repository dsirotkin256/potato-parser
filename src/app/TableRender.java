package app;

import java.util.Collection;
import java.util.LinkedList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class TableRender extends AbstractTableModel {

    private LinkedList<Question> questions;
    private LinkedList<Object[]> tableQuestions;
    private SharedSelectionListener listener;
    private int row = -1;
    private static Question question;
    private String[] columnNames = {"№", "Вопрос"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return tableQuestions.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableQuestions.get(rowIndex)[columnIndex];
    }

    private void pull(Collection<Question> questions) {
        this.tableQuestions.clear();
        this.questions = new LinkedList<>(questions);

        questions.forEach(question -> {
            tableQuestions.add(rend(tableQuestions.size(), question));
        });

        fireTableDataChanged();
    }

    public void update(Collection<Question> questions) {
        this.questions.clear();
        pull(questions);
    }

    private Object[] rend(int n, Question question) {
        return new Object[]{n + 1, question.getQuestion()};
    }

    private void setTableListener(ListSelectionListener listener) {
        App.ui.table.getColumnModel().getColumn(0).setWidth(45);
        App.ui.table.getColumnModel().getColumn(0).setMinWidth(45);
        App.ui.table.getColumnModel().getColumn(0).setMaxWidth(45);

        App.ui.table.getSelectionModel().addListSelectionListener(listener);
    }

    public TableRender() {

        questions = new LinkedList<>();
        tableQuestions = new LinkedList<>();

        App.ui.table.setModel(this);
        listener = new SharedSelectionListener();
        setTableListener(listener);
    }

    class SharedSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            row = App.ui.table.getSelectedRow();

            if (row != -1) {
                try {
                    question = questions.get(row);
                    question.setPreview();
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
            }
        }
    }
}
