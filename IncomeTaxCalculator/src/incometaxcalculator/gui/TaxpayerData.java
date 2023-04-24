package incometaxcalculator.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import incometaxcalculator.data.management.Receipt;
import incometaxcalculator.data.management.TaxpayerManager;
import incometaxcalculator.exceptions.*;
import org.jfree.data.time.Year;

@SuppressWarnings("serial")
public class TaxpayerData extends JFrame {

    private static final short ENTERTAINMENT = 0;
    private static final short BASIC = 1;
    private static final short TRAVEL = 2;
    private static final short HEALTH = 3;
    private static final short OTHER = 4;
    private JPanel contentPane;

    public TaxpayerData(int taxRegistrationNumber, TaxpayerManager taxpayerManager) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(200, 100, 480, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        DefaultListModel<Integer> receiptsModel = new DefaultListModel<Integer>();

        JList<Integer> receiptsList = new JList<Integer>(receiptsModel);
        receiptsList.setBackground(new Color(153, 204, 204));
        receiptsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        receiptsList.setSelectedIndex(0);
        receiptsList.setVisibleRowCount(3);

        JScrollPane receiptsListScrollPane = new JScrollPane(receiptsList);
        receiptsListScrollPane.setSize(150, 200);
        receiptsListScrollPane.setLocation(100, 170);
        contentPane.add(receiptsListScrollPane);

        TextArea receiptDetails = new TextArea();
        receiptDetails.setBackground(new Color(153, 204, 204));
        receiptDetails.setEditable(false);

        JScrollPane receiptDetailsPane = new JScrollPane(receiptDetails,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        receiptDetailsPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        receiptDetailsPane.setSize(150, 200);
        receiptDetailsPane.setLocation(260, 170);
        contentPane.add(receiptDetailsPane);

        HashMap<Integer, Receipt> receipts = taxpayerManager.getReceiptHashMap(taxRegistrationNumber);
        Iterator<HashMap.Entry<Integer, Receipt>> iterator = receipts.entrySet().iterator();

        while (iterator.hasNext()) {
            HashMap.Entry<Integer, Receipt> entry = iterator.next();
            Receipt receipt = entry.getValue();
            receiptsModel.addElement(receipt.getId());
        }

        receiptsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    receiptDetails.setText(null);
                    receiptDetails.append("Receipt ID: "+receipts.get(receiptsList.getSelectedValue()).getId()+"\n");
                    receiptDetails.append("Date: "+receipts.get(receiptsList.getSelectedValue()).getIssueDate()+"\n");
                    receiptDetails.append("Kind: "+receipts.get(receiptsList.getSelectedValue()).getKind()+"\n");
                    receiptDetails.append("Amount: "+receipts.get(receiptsList.getSelectedValue()).getAmount()+"\n");
                    receiptDetails.append("Company: "+receipts.get(receiptsList.getSelectedValue()).getCompany().getName()+"\n");
                    receiptDetails.append("Country: "+receipts.get(receiptsList.getSelectedValue()).getCompany().getCountry()+"\n");
                    receiptDetails.append("City: "+receipts.get(receiptsList.getSelectedValue()).getCompany().getCity()+"\n");
                    receiptDetails.append("Street: "+receipts.get(receiptsList.getSelectedValue()).getCompany().getStreet()+"\n");
                    receiptDetails.append("Number: "+receipts.get(receiptsList.getSelectedValue()).getCompany().getNumber());
                }
            }
        });

        JButton btnAddReceipt = new JButton("Add Receipt");
        btnAddReceipt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String[] date = {""};
                JPanel receiptImporterPanel = new JPanel(new GridLayout(9, 2));
                JTextField receiptID = new JTextField(16);
                JButton dateButton = new JButton("Select Date");
                dateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JPanel dateSelectionPanel = new JPanel(new GridLayout(3, 2));
                        JLabel dayLabel = new JLabel("Day: ");
                        dateSelectionPanel.add(dayLabel);
                        JComboBox<String> day = new JComboBox<String>(getDays());
                        dateSelectionPanel.add(day);
                        JLabel monthLabel = new JLabel("Month: ");
                        dateSelectionPanel.add(monthLabel);
                        JComboBox<String> month = new JComboBox<String>(getMonths());
                        dateSelectionPanel.add(month);
                        JLabel yearLabel = new JLabel("Year: ");
                        dateSelectionPanel.add(yearLabel);
                        JComboBox<String> year = new JComboBox<String>(getYears());
                        dateSelectionPanel.add(year);
                        JOptionPane.showMessageDialog(receiptImporterPanel,dateSelectionPanel);

                        date[0] = (day.getSelectedItem()+"/"+(month.getSelectedIndex()+1)+"/"+year.getSelectedItem());
                        dateButton.setText(date[0]);
                    }
                });

                JComboBox<String> kind = new JComboBox<String>(new String[]{"Entertainment","Basic","Travel","Health","Other"});
                JTextField amount = new JTextField(16);
                JTextField company = new JTextField(16);
                JTextField country = new JTextField(16);
                JTextField city = new JTextField(16);
                JTextField street = new JTextField(16);
                JTextField number = new JTextField(16);
                int receiptIDValue, numberValue;
                float amountValue;
                String dateValue, kindValue, companyValue, countryValue;
                String cityValue, streetValue;
                receiptImporterPanel.add(new JLabel("Receipt ID:"));
                receiptImporterPanel.add(receiptID);
                receiptImporterPanel.add(new JLabel("Date:"));
                receiptImporterPanel.add(dateButton);
                receiptImporterPanel.add(new JLabel("Kind:"));
                receiptImporterPanel.add(kind);
                receiptImporterPanel.add(new JLabel("Amount:"));
                receiptImporterPanel.add(amount);
                receiptImporterPanel.add(new JLabel("Company:"));
                receiptImporterPanel.add(company);
                receiptImporterPanel.add(new JLabel("Country:"));
                receiptImporterPanel.add(country);
                receiptImporterPanel.add(new JLabel("City:"));
                receiptImporterPanel.add(city);
                receiptImporterPanel.add(new JLabel("Street:"));
                receiptImporterPanel.add(street);
                receiptImporterPanel.add(new JLabel("Number:"));
                receiptImporterPanel.add(number);
                int op = JOptionPane.showConfirmDialog(null, receiptImporterPanel, "",
                        JOptionPane.OK_CANCEL_OPTION);
                if (op == 0) {
                    try {
                        receiptIDValue = Integer.parseInt(receiptID.getText());
                        dateValue = date[0];
                        kindValue = kind.getSelectedItem().toString();
                        amountValue = Float.parseFloat(amount.getText());
                        companyValue = company.getText();
                        countryValue = country.getText();
                        cityValue = city.getText();
                        streetValue = street.getText();
                        numberValue = Integer.parseInt(number.getText());

                        taxpayerManager.addReceipt(receiptIDValue, dateValue, amountValue, kindValue,
                                companyValue, countryValue, cityValue, streetValue, numberValue,
                                taxRegistrationNumber);
                        receiptsModel.addElement(receiptIDValue);
                    }catch (NumberFormatException nf){
                        JOptionPane.showMessageDialog(null,
                                "Invalid Input! \nPlease fill all fields correctly.");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null,
                                "Problem with opening INFO file to add the receipt.");
                    } catch (WrongReceiptKindException e1) {
                        JOptionPane.showMessageDialog(null, "Please check receipts kind and try again.");
                    } catch (WrongReceiptDateException e1) {
                        JOptionPane.showMessageDialog(null,
                                "Please make sure your date " + "is DD/MM/YYYY and try again.");
                    } catch (ReceiptAlreadyExistsException e1) {
                        JOptionPane.showMessageDialog(null, "Receipt ID already exists.");
                    }
                }
            }
        });
        btnAddReceipt.setBounds(0, 0, 102, 23);
        contentPane.add(btnAddReceipt);

        JButton btnDeleteReceipt = new JButton("Delete Receipt");
        btnDeleteReceipt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel receiptRemoverPanel = new JPanel(new GridLayout(2, 2));
                receiptRemoverPanel.add(new JLabel("Receipt ID to delete:"));

                List<Integer> receiptNumberList = Collections.list(receiptsModel.elements());
                Integer[] receiptNumberArray = receiptNumberList.toArray(new Integer[0]);
                JComboBox<Integer> receiptSelection = new JComboBox<Integer>(receiptNumberArray);
                receiptSelection.setSelectedIndex(0);
                receiptRemoverPanel.add(receiptSelection);

                int op = JOptionPane.showConfirmDialog(null, receiptRemoverPanel, "",
                        JOptionPane.OK_CANCEL_OPTION);
                if (op == 0) {
                    try {
                        int receiptIDValue = (Integer) receiptSelection.getSelectedItem();
                        taxpayerManager.removeReceipt(receiptIDValue);
                        receiptsModel.removeElement(receiptIDValue);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null,
                                "Problem with opening INFO file to delete the receipt.");
                    } catch (WrongReceiptKindException e1) {
                        JOptionPane.showMessageDialog(null, "Please check receipt's kind and try again.");
                    } catch (NullPointerException ignored){
                        ;
                    }
                }
            }
        });
        btnDeleteReceipt.setBounds(100, 0, 118, 23);
        contentPane.add(btnDeleteReceipt);

        JButton btnViewReport = new JButton("View Report");
        btnViewReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChartDisplay.createBarChart(taxpayerManager.getTaxpayerBasicTax(taxRegistrationNumber),
                        taxpayerManager.getTaxpayerVariationTaxOnReceipts(taxRegistrationNumber),
                        taxpayerManager.getTaxpayerTotalTax(taxRegistrationNumber));
                ChartDisplay.createPieChart(
                        taxpayerManager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, ENTERTAINMENT),
                        taxpayerManager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, BASIC),
                        taxpayerManager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, TRAVEL),
                        taxpayerManager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, HEALTH),
                        taxpayerManager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, OTHER));
            }
        });
        btnViewReport.setBounds(214, 0, 109, 23);
        contentPane.add(btnViewReport);


        JButton btnSaveData = new JButton("Save Data To Log File");
        btnSaveData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel fileLoaderPanel = new JPanel(new BorderLayout());
                fileLoaderPanel.setSize(600,700);
                JPanel boxPanel = new JPanel(new BorderLayout());
                JPanel taxRegistrationNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                JLabel TRN = new JLabel("Choose file format.");
                TRN.setFont(new Font("Tahoma", Font.PLAIN, 14));
                taxRegistrationNumberPanel.add(TRN);
                JPanel loadPanel = new JPanel(new GridLayout(1, 2));
                loadPanel.add(taxRegistrationNumberPanel);
                fileLoaderPanel.add(loadPanel, BorderLayout.NORTH);
                fileLoaderPanel.add(boxPanel, BorderLayout.CENTER);

                JCheckBox txtBox = new JCheckBox("Txt file");
                JCheckBox xmlBox = new JCheckBox("Xml file");

                txtBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        xmlBox.setSelected(false);
                    }
                });

                xmlBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txtBox.setSelected(false);
                    }
                });
                txtBox.doClick();
                boxPanel.add(txtBox, BorderLayout.WEST);
                boxPanel.add(xmlBox, BorderLayout.CENTER);


                JFileChooser directoryChooser = new JFileChooser(System.getProperty("user.dir"));
                directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                directoryChooser.setControlButtonsAreShown(false);

                JLabel directoryLabel = new JLabel("Select directory to save Log file.");
                directoryLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

                JPanel directoryPanel = new JPanel(new BorderLayout());

                directoryPanel.add(directoryChooser,BorderLayout.SOUTH);
                directoryPanel.add(directoryLabel,BorderLayout.NORTH);

                fileLoaderPanel.add(directoryPanel,BorderLayout.SOUTH);

                int answer = JOptionPane.showConfirmDialog(null, fileLoaderPanel, "",
                        JOptionPane.OK_CANCEL_OPTION);
                if (answer == 0) {
                    String filePath = directoryChooser.getCurrentDirectory().toString();
                    try {
                        if (txtBox.isSelected()) {
                            try {
                                taxpayerManager.saveLogFile(taxRegistrationNumber, "txt",filePath);
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(null,
                                        "Problem with opening file ." + taxRegistrationNumber + "_LOG.txt");
                            } catch (WrongFileFormatException e1) {
                                JOptionPane.showMessageDialog(null, "Wrong file format");
                            }
                        } else {
                            try {
                                taxpayerManager.saveLogFile(taxRegistrationNumber, "xml",filePath);
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(null,
                                        "Problem with opening file ." + taxRegistrationNumber + "_LOG.xml");
                            } catch (WrongFileFormatException e1) {
                                JOptionPane.showMessageDialog(null, "Wrong file format");
                            }
                        }
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(null,
                                "The tax registration number must have only digits.");
                    }
                }
            }
        });
        btnSaveData.setBounds(322, 0, 140, 23);
        contentPane.add(btnSaveData);

        JTextPane txtpnName = new JTextPane();
        configureLabelWidgets(txtpnName,"Name :",34);
        contentPane.add(txtpnName);

        JTextPane txtpnTrn = new JTextPane();
        configureLabelWidgets(txtpnTrn,"TRN :",65);
        contentPane.add(txtpnTrn);

        JTextPane txtpnStatus = new JTextPane();
        configureLabelWidgets(txtpnStatus,"Status :",96);
        contentPane.add(txtpnStatus);

        JTextPane txtpnIncome = new JTextPane();
        configureLabelWidgets(txtpnIncome,"Income :",127);
        contentPane.add(txtpnIncome);

        JTextArea taxpayerName = new JTextArea();
        configureValueWidgets(taxpayerName, taxpayerManager.getTaxpayerName(taxRegistrationNumber), 34);
        contentPane.add(taxpayerName);

        JTextArea taxpayerTRN = new JTextArea();
        configureValueWidgets(taxpayerTRN, taxRegistrationNumber + "", 65);
        contentPane.add(taxpayerTRN);

        JTextArea taxpayerStatus = new JTextArea();
        configureValueWidgets(taxpayerStatus, taxpayerManager.getTaxpayerStatus(taxRegistrationNumber), 96);
        contentPane.add(taxpayerStatus);

        JTextArea taxpayerIncome = new JTextArea();
        configureValueWidgets(taxpayerIncome, taxpayerManager.getTaxpayerIncome(taxRegistrationNumber), 127);
        contentPane.add(taxpayerIncome);

        JButton editStatus = new JButton("Edit Status");
        editStatus.setFont(new Font("Tahoma", Font.PLAIN, 11));
        editStatus.setBounds(330, 96, 100, 20);
        contentPane.add(editStatus);

        JButton editIncome = new JButton("Edit Income");
        editIncome.setFont(new Font("Tahoma", Font.PLAIN, 11));
        editIncome.setBounds(330, 127, 100, 20);
        contentPane.add(editIncome);

        JTextPane txtpnReceipts = new JTextPane();
        txtpnReceipts.setEditable(false);
        txtpnReceipts.setText("Receipts :");
        txtpnReceipts.setBounds(10, 170, 80, 20);
        contentPane.add(txtpnReceipts);

        editStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] statusCategories = new String[]{"Married Filing Jointly", "Married Filing Separately", "Single",
                        "Head of Household"};

                // selectedOption is null if "Cancel" is selected
                Object selectedOption = JOptionPane.showInputDialog(contentPane, "Select status: ",
                        "", JOptionPane.PLAIN_MESSAGE, null, statusCategories,
                        taxpayerManager.getTaxpayerStatus(taxRegistrationNumber));

                String newStatus = (selectedOption != null) ?
                        (String)selectedOption
                        :taxpayerManager.getTaxpayerStatus(taxRegistrationNumber);

                if(!newStatus.equals(taxpayerManager.getTaxpayerStatus(taxRegistrationNumber))){
                    try {
                        taxpayerManager.changeStatus(newStatus, taxRegistrationNumber);
                        taxpayerStatus.setText(taxpayerManager.getTaxpayerStatus(taxRegistrationNumber));
                    }catch (WrongTaxpayerStatusException | IOException wrongStatus){
                        JOptionPane.showMessageDialog(contentPane,"Invalid status input!");
                    }
                }
            }
        });
        editIncome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "Type income: ";
                try {
                    float newIncome = Float.parseFloat(JOptionPane.showInputDialog(contentPane,message,
                            taxpayerManager.getTaxpayerIncome(taxRegistrationNumber)));

                    taxpayerIncome.setText(Float.toString(newIncome));
                    taxpayerManager.changeIncome(newIncome,taxRegistrationNumber);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(contentPane,"Invalid income input!");
                } catch (Exception ignored){
                    // for when we cancel and the parseFloat fails
                }
            }
        });
    }

    private void configureLabelWidgets(JTextPane pane, String label, int y){
        pane.setEditable(false);
        pane.setText(label);
        pane.setBounds(10, y, 92, 20);
    }

    private void configureValueWidgets(JTextArea area, String value, int y){
        area.setFont(new Font("Tahoma", Font.PLAIN, 11));
        area.setEditable(false);
        area.setText(value);
        area.setBounds(110, y, 213, 20);
    }

    private String[] getDays(){
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = Integer.toString(i+1);
        }
        return  days;
    }
    private String[] getMonths(){
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);
        return symbols.getMonths();
    }
    private String[] getYears(){
        int currentYear = new Year().getYear();
        String[] years = new String[100];
        for (int i = 0; i < 100; i++) {
            years[i] = Integer.toString(currentYear-i);
        }
        return years;
    }
}