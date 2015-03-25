package app;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame {

    public final static UI instance = getInstance();
    private volatile static Mode dontDisturbMode = Mode.OFF;

    private static class Helper {

        public final static UI ui = new UI();
    }

    private static UI getInstance() {
        return Helper.ui;
    }

    public JTextField searchBox;
    public JEditorPane answerPane, questionPane;
    public JTable table;
    public JLabel lblSearch;

    private JPanel contentPane;
    private JScrollPane answerScrollPane, questionScrollPane;
    private JLabel label_1;

    private UI() {

        setLocationRelativeTo(null);
        setTitle("ReineSoft inc.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1074, 635);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        searchBox = new JTextField();
        searchBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchBox.selectAll();
            }
        });

        searchBox.setFont(new Font("Consolas", Font.PLAIN, 15));
        searchBox.setText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0432\u043E\u043F\u0440\u043E\u0441");
        searchBox.setColumns(10);

        JScrollPane scrollPane = new JScrollPane();

        table = new JTable();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        scrollPane.setViewportView(table);

        JLabel label = new JLabel("\u041E\u0442\u0432\u0435\u0442");
        label.setFont(new Font("Consolas", Font.PLAIN, 16));

        answerScrollPane = new JScrollPane();

        answerPane = new JEditorPane();
        answerScrollPane.setViewportView(answerPane);
        answerPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        answerPane.setEditable(false);

        lblSearch = new JLabel();
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));

        questionScrollPane = new JScrollPane();

        label_1 = new JLabel("\u0412\u043E\u043F\u0440\u043E\u0441");
        label_1.setFont(new Font("Consolas", Font.PLAIN, 16));

        questionPane = new JEditorPane();
        questionPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        questionPane.setEditable(false);
        questionScrollPane.setViewportView(questionPane);

        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addGap(10)
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(searchBox, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                                        .addGap(28)
                                        .addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, 569, GroupLayout.PREFERRED_SIZE))
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1028, Short.MAX_VALUE)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                .addComponent(label_1, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(questionScrollPane, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
                                        .addGap(27)
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                        .addComponent(label, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(70))
                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                        .addComponent(answerScrollPane, GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                                                        .addPreferredGap(ComponentPlacement.RELATED)))))
                        .addGap(10))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchBox, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                        .addGap(14)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE)
                        .addGap(11)
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(label_1)
                                .addComponent(label))
                        .addGap(11)
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(answerScrollPane, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                .addComponent(questionScrollPane, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                        .addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }

    public static void setDontDisturbMode(Mode dontDisturbMode) {
        UI.dontDisturbMode = dontDisturbMode;
    }

    public static Mode getDontDisturbMode() {
        return dontDisturbMode;
    }

}
