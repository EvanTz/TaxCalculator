package incometaxcalculator.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import incometaxcalculator.data.management.TaxpayerManager;
import incometaxcalculator.exceptions.WrongFileEndingException;
import incometaxcalculator.exceptions.WrongFileFormatException;
import incometaxcalculator.exceptions.WrongReceiptDateException;
import incometaxcalculator.exceptions.WrongReceiptKindException;
import incometaxcalculator.exceptions.WrongTaxpayerStatusException;

@SuppressWarnings("serial")
public class GraphicalInterface extends JFrame {

    private JPanel contentPane;
    private TaxpayerManager taxpayerManager = new TaxpayerManager();
    private JTextField txtTaxRegistrationNumber;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GraphicalInterface frame = new GraphicalInterface();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GraphicalInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 500);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(204, 204, 204));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e2) {
            e2.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName();
                    return filename.endsWith("_INFO.txt") || filename.endsWith("_INFO.xml");
                }
            }
            @Override
            public String getDescription() {
                return "TXT and XML files";
            }
        });

        DefaultListModel<String> taxRegisterNumberModel = new DefaultListModel<String>();

        JList<String> taxRegisterNumberList = new JList<String>(taxRegisterNumberModel);
        taxRegisterNumberList.setBackground(new Color(153, 204, 204));
        taxRegisterNumberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taxRegisterNumberList.setSelectedIndex(0);
        taxRegisterNumberList.setVisibleRowCount(3);

        JScrollPane taxRegisterNumberListScrollPane = new JScrollPane(taxRegisterNumberList);
        taxRegisterNumberListScrollPane.setSize(300, 300);
        taxRegisterNumberListScrollPane.setLocation(70, 100);
        contentPane.add(taxRegisterNumberListScrollPane);

        JButton btnLoadTaxpayer = new JButton("Load Taxpayer");

        btnLoadTaxpayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int answer = fileChooser.showDialog(null,"Load Taxpayer");

                if (answer == JFileChooser.APPROVE_OPTION) {
                    String fullFilePath = fileChooser.getSelectedFile().toString();
                    String taxRegistrationNumberFile = fileChooser.getSelectedFile().getName();
                    String taxRegistrationNumber = taxRegistrationNumberFile.split("_")[0];

                    while (taxRegistrationNumber.length() != 9 && answer == 0) {
                        JOptionPane.showMessageDialog(null,
                                "The tax  registration number must have 9 digits.\n" + " Try again.");
                        answer = fileChooser.showDialog(null,"Load Taxpayer");
                        fullFilePath = fileChooser.getSelectedFile().toString();
                        taxRegistrationNumberFile = fileChooser.getSelectedFile().getName();
                        taxRegistrationNumber = taxRegistrationNumberFile.split("_")[0];
                    }
                    if (answer == 0) {
                        int trn = 0;
                        try {
                            trn = Integer.parseInt(taxRegistrationNumber);
                            if (taxpayerManager.containsTaxpayer(trn)) {
                                JOptionPane.showMessageDialog(null, "This taxpayer is already loaded.");
                            } else {
                                taxpayerManager.loadTaxpayer(fullFilePath);
                                taxRegisterNumberModel.addElement(taxRegistrationNumber);
                            }
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(null,
                                    "The tax registration number must have only digits.");
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, "The file doesn't exists.");
                        } catch (WrongFileFormatException e1) {
                            JOptionPane.showMessageDialog(null, "Please check your file format and try again.");
                        } catch (WrongFileEndingException e1) {
                            JOptionPane.showMessageDialog(null, "Please check your file ending and try again.");
                        } catch (WrongTaxpayerStatusException e1) {
                            JOptionPane.showMessageDialog(null, "Please check taxpayer's status and try again.");
                        } catch (WrongReceiptKindException e1) {
                            JOptionPane.showMessageDialog(null, "Please check receipts kind and try again.");
                        } catch (WrongReceiptDateException e1) {
                            JOptionPane.showMessageDialog(null,
                                    "Please make sure your date is " + "DD/MM/YYYY and try again.");
                        }
                    }

                }
            }
        });

        btnLoadTaxpayer.setBounds(0, 0, 146, 23);
        contentPane.add(btnLoadTaxpayer);

        JButton btnSelectTaxpayer = new JButton("Select Taxpayer");
        btnSelectTaxpayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (taxpayerManager.containsAnyTaxpayer()) {
                    List<String> registrationNumberList = Collections.list(taxRegisterNumberModel.elements());
                    String[] registrationNumberArray = registrationNumberList.toArray(new String[0]);

                    String message = "Choose the tax registration number \nthat you want to be displayed: ";

                    String trn = (String) JOptionPane.showInputDialog(null, message,
                            "", JOptionPane.PLAIN_MESSAGE, null, registrationNumberArray,
                            taxRegisterNumberList.getSelectedValue());

                    if (trn != null) {
                        int taxRegistrationNumber;
                        try {
                            taxRegistrationNumber = Integer.parseInt(trn);
                            if (taxpayerManager.containsTaxpayer(taxRegistrationNumber)) {
                                TaxpayerData taxpayerData = new TaxpayerData(taxRegistrationNumber,
                                        taxpayerManager);
                                taxpayerData.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(null, "This tax registration number isn't loaded.");
                            }
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(null, "You must give a tax registration number.");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "There isn't any taxpayer loaded. Please load one first.");
                }
            }
        });

        btnSelectTaxpayer.setBounds(147, 0, 139, 23);
        contentPane.add(btnSelectTaxpayer);

        JButton btnDeleteTaxpayer = new JButton("Delete Taxpayer");
        btnDeleteTaxpayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (taxpayerManager.containsAnyTaxpayer()) {
                    List<String> registrationNumberList = Collections.list(taxRegisterNumberModel.elements());
                    String[] registrationNumberArray = registrationNumberList.toArray(new String[0]);

                    String message = "Choose the tax registration number that you want to delete: ";

                    String trn = (String) JOptionPane.showInputDialog(null, message,
                            "", JOptionPane.PLAIN_MESSAGE, null, registrationNumberArray,
                            taxRegisterNumberList.getSelectedValue());

                    int taxRegistrationNumber;
                    try {
                        taxRegistrationNumber = Integer.parseInt(trn);
                        if (taxpayerManager.containsTaxpayer(taxRegistrationNumber)) {
                            taxpayerManager.removeTaxpayer(taxRegistrationNumber);
                            taxRegisterNumberModel.removeElement(trn);
                        }
                    } catch (NumberFormatException ignored) {

                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "There isn't any taxpayer loaded. Please load one first.");
                }
            }
        });
        btnDeleteTaxpayer.setBounds(287, 0, 146, 23);
        contentPane.add(btnDeleteTaxpayer);

        txtTaxRegistrationNumber = new JTextField();
        txtTaxRegistrationNumber.setEditable(false);
        txtTaxRegistrationNumber.setBackground(new Color(153, 204, 204));
        txtTaxRegistrationNumber.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtTaxRegistrationNumber.setText("Tax Registration Number:");
        txtTaxRegistrationNumber.setBounds(70, 80, 300, 20);
        contentPane.add(txtTaxRegistrationNumber);
        txtTaxRegistrationNumber.setColumns(10);

    }
}