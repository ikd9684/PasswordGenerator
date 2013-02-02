package jp.co.excite_software.s_ikeda.pgen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jp.co.excite_software.s_ikeda.pgen.CharType.PassPhraseValidator;

@SuppressWarnings("serial")
public class PasswordGeneratorGUI extends JFrame {

    protected static final CharType SYMBOL =
            new CharType("!#$%&*+-.<=>?@".toCharArray());

    /**  */
    private static Toolkit toolkit = Toolkit.getDefaultToolkit();
    /**  */
    private static Dimension screenSize = toolkit.getScreenSize();

    public PasswordGeneratorGUI() {
        super();

        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
            // through
        }

        super.setTitle(getWindowTitle());
        this.initSize();
        this.initPosition();

        initComponents();
    }

    protected void initSize() {

        int w = this.getDefaultWindowWidth();
        int h = this.getDefaultWindowHeight();
        super.setSize(w, h);
    }

    protected void initPosition() {

        int x = (screenSize.width / 2) - (super.getWidth() / 2);
        int y = (screenSize.height / 2) - (super.getHeight() / 2);
        super.setLocation(x, y);
    }

    protected int getDefaultWindowWidth() {
        return 460;
    }
    protected int getDefaultWindowHeight() {
        return 95;
    }
    protected String getWindowTitle() {
        return "PasswordGenerator";
    }

    private boolean generating = false;
    private class GenerateThread extends Thread {
        @Override
        public void run() {

            ArrayList<CharType> charTypeList = new ArrayList<CharType>();
            if (cbxNumber.isSelected()) {
                charTypeList.add(PasswordGenerator.NUMBER);
            }
            if (cbxAlphabet.isSelected()) {
                charTypeList.add(PasswordGenerator.ALPHABET_S);
            }
            if (cbxSymbol.isSelected()) {
                charTypeList.add(SYMBOL);
            }
            CharType[] charTypes = new CharType[charTypeList.size()];
            for (int i = 0; i < charTypes.length; i++) {
                charTypes[i] = charTypeList.get(i);
            }

            int length = spinnerModel.getNumber().intValue();

            PassPhraseValidator validator = new PassPhraseValidator() {
                @Override
                public boolean isValid(String phrase) {
                    // 同じ文字が３回続かないこと
                    char p = phrase.charAt(0);
                    int count = 1;
                    for (int i = 0; i < phrase.length(); i++) {
                        if (p == phrase.charAt(i)) {
                            ++count;

                            if (3 <= count) {
                                return false;
                            }
                        }
                        else {
                            count = 1;
                            p = phrase.charAt(i);
                        }
                    }
                    return false;
                }
            };
            PasswordGenerator generator = new PasswordGenerator(charTypes, validator);

            final String password = generator.newPasswd(length);

            /*
             * 以下は演出
             */
            int wait = 10;
            String padding = "                                ";
            for (int len = 0; len < length; len++) {

                for (int i = 0; i < wait; i++) {
                    String p = password.substring(0, len) + generator.newPasswd(1) + padding;
                    txtPassword.setText(p.substring(0, length));

                    try {
                        Thread.sleep(wait);
                    }
                    catch (InterruptedException e) {
                    }
                }

                txtPassword.setText(password.substring(0, len));
            }
            txtPassword.setText(password);

            btnGenerate.setEnabled(true);
            cbxNumber.setEnabled(true);
            cbxAlphabet.setEnabled(true);
            cbxSymbol.setEnabled(true);
            spnLength.setEnabled(true);

            generating = false;
        }
    }

    protected void generate() {

        generating = true;

        btnGenerate.setEnabled(false);
        cbxNumber.setEnabled(false);
        cbxAlphabet.setEnabled(false);
        cbxSymbol.setEnabled(false);
        spnLength.setEnabled(false);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                (new GenerateThread()).start();
            }
        });
    }

    protected void validCheckBox() {
        btnGenerate.setEnabled(
                cbxNumber.isSelected()
                || cbxAlphabet.isSelected()
                || cbxSymbol.isSelected());
    }

    private JTextField txtPassword;
    private JCheckBox cbxNumber;
    private JCheckBox cbxAlphabet;
    private JCheckBox cbxSymbol;
    private SpinnerNumberModel spinnerModel;
    private JSpinner spnLength;
    private JButton btnGenerate;

    protected void initComponents() {
        super.setLayout(null);
        super.setResizable(false);

        txtPassword = new JTextField() {
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        txtPassword.setBackground(Color.BLACK);
        txtPassword.setForeground(Color.GREEN);
        txtPassword.setSelectionColor(Color.GREEN);
        txtPassword.setSelectedTextColor(Color.BLACK);
        txtPassword.setBounds(5, 5, 450, 30);
        txtPassword.setEditable(false);
        txtPassword.setFont(new Font("Courier New", Font.BOLD, 18));
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (generating) {
                    e.consume();
                    return;
                }

                txtPassword.selectAll();
                Clipboard clipboard = toolkit.getSystemClipboard();
                StringSelection selection = new StringSelection(txtPassword.getText());
                clipboard.setContents(selection, null);

                JOptionPane optionPane =
                        new JOptionPane("クリップボードにコピーしました。", JOptionPane.INFORMATION_MESSAGE);
                final JDialog dialog = optionPane.createDialog(PasswordGeneratorGUI.this, "通知");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                dialog.setVisible(false);
                            }
                        }, 1000);

                        dialog.setVisible(true);
                    }
                });
            }
        });
        super.add(txtPassword);

        cbxNumber = new JCheckBox("数字");
        cbxNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validCheckBox();
            }
        });
        cbxNumber.setBounds(5, 40, 60, 25);
        cbxNumber.setSelected(true);
        cbxNumber.setToolTipText(PasswordGenerator.NUMBER.toString());
        super.add(cbxNumber);

        cbxAlphabet = new JCheckBox("英字");
        cbxAlphabet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validCheckBox();
            }
        });
        cbxAlphabet.setBounds(70, 40, 60, 25);
        cbxAlphabet.setSelected(true);
        cbxAlphabet.setToolTipText(PasswordGenerator.ALPHABET_S.toString());
        super.add(cbxAlphabet);

        cbxSymbol = new JCheckBox("記号");
        cbxSymbol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validCheckBox();
            }
        });
        cbxSymbol.setBounds(135, 40, 60, 25);
        cbxSymbol.setSelected(true);
        cbxSymbol.setToolTipText(SYMBOL.toString());
        super.add(cbxSymbol);

        spinnerModel = new SpinnerNumberModel(8, 1, 32, 1);
        spnLength = new JSpinner(spinnerModel);
        spnLength.setBounds(210, 40, 50, 25);
        super.add(spnLength);

        btnGenerate = new JButton("生成");
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generate();
            }
        });
        btnGenerate.setBounds(275, 40, 174, 24);
        super.add(btnGenerate);
    }

    public static void main(String[] args) {
        (new PasswordGeneratorGUI()).setVisible(true);
    }

}
