import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RentalAppGUI {

    // ================= DATA =================
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static double totalIncome = 0;
    private static JFrame mainFrame;

    // ================= OTP MAP =================
    private static HashMap<String, String> otpMap = new HashMap<>();
    private static HashMap<String, String> customerNameMap = new HashMap<>();
    private static HashMap<String, Integer> rentalDaysMap = new HashMap<>();

    public static void main(String[] args) {
        totalIncome = FileManager.load(vehicles);
        SwingUtilities.invokeLater(RentalAppGUI::createDashboard);
    }

    // ================= DASHBOARD =================
    private static void createDashboard() {
        mainFrame = new JFrame("Vehicle Rental Dashboard");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        ImageIcon originalIcon = new ImageIcon("D:\\VehicleRentalSystem\\src\\car.jpg");
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel bgLabel = new JLabel(scaledIcon);
        bgLabel.setLayout(new BorderLayout());
        mainFrame.setContentPane(bgLabel);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("VEHICLE RENTAL SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 48));
        heading.setForeground(new Color(255, 215, 0));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topPanel.add(heading, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        styleMainButton(searchBtn, new Color(52, 73, 94));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        bgLabel.add(topPanel, BorderLayout.NORTH);

        // ===== LEFT BUTTON PANEL =====
        JPanel leftPanel = new JPanel(new GridLayout(6, 1, 15, 15));
        leftPanel.setOpaque(false);

        JButton addBtn = createDashboardButton("Add Vehicle", new Color(52, 152, 219));
        JButton viewBtn = createDashboardButton("View Vehicles", new Color(46, 204, 113));
        JButton rentBtn = createDashboardButton("Rent Vehicle", new Color(241, 196, 15));
        JButton returnBtn = createDashboardButton("Return Vehicle", new Color(231, 76, 60));
        JButton incomeBtn = createDashboardButton("Total Income", new Color(155, 89, 182));
        JButton exitBtn = createDashboardButton("Exit", new Color(149, 165, 166));

        leftPanel.add(addBtn);
        leftPanel.add(viewBtn);
        leftPanel.add(rentBtn);
        leftPanel.add(returnBtn);
        leftPanel.add(incomeBtn);
        leftPanel.add(exitBtn);

        bgLabel.add(leftPanel, BorderLayout.WEST);

        // ===== BUTTON ACTIONS =====
        addBtn.addActionListener(e -> addVehicleGUI());
        viewBtn.addActionListener(e -> showVehicleTables());
        rentBtn.addActionListener(e -> rentVehicleGUI());
        returnBtn.addActionListener(e -> returnVehicleGUI());
        incomeBtn.addActionListener(e -> showTotalIncome());
        exitBtn.addActionListener(e -> System.exit(0));

        searchBtn.addActionListener(e -> {
            String id = searchField.getText().trim();
            Vehicle v = searchById(id);
            if (v != null) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Vehicle Found:\nID: " + v.getVehicleId() +
                                "\nBrand: " + v.getBrand() +
                                "\nModel: " + v.getModel() +
                                "\nRate: Rs." + v.getBaseRatePerDay() +
                                "\nAvailable: " + (v.isAvailable() ? "Yes" : "No")
                );
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Vehicle not found!");
            }
        });

        mainFrame.setVisible(true);
    }

    private static JButton createDashboardButton(String text, Color color) {
        JButton btn = new JButton(text);
        styleMainButton(btn, color);
        return btn;
    }

    private static void styleMainButton(JButton btn, Color baseColor) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 45));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
            public void mousePressed(MouseEvent e) { btn.setBackground(baseColor.darker()); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(baseColor); }
        });
    }

    // ================= ADD VEHICLE =================
    private static void addVehicleGUI() {
        JDialog dialog = new JDialog(mainFrame, "Add Vehicle", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField typeField = new JTextField();
        JTextField idField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField rateField = new JTextField();
        JTextField extraField = new JTextField();

        dialog.add(new JLabel("Type (Car/Bike/Van):"));
        dialog.add(typeField);
        dialog.add(new JLabel("Vehicle ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Brand:"));
        dialog.add(brandField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Base Rate:"));
        dialog.add(rateField);
        dialog.add(new JLabel("Seats / CC / Cargo:"));
        dialog.add(extraField);

        JButton addBtn = new JButton("Add Vehicle");
        styleMainButton(addBtn, new Color(52, 152, 219));
        dialog.add(new JLabel());
        dialog.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                String type = typeField.getText().trim().toLowerCase();
                String id = idField.getText().trim();
                String brand = brandField.getText().trim();
                String model = modelField.getText().trim();
                double rate = Double.parseDouble(rateField.getText().trim());
                String extra = extraField.getText().trim();

                if (searchById(id) != null) {
                    JOptionPane.showMessageDialog(dialog, "Vehicle ID already exists!");
                    return;
                }

                Vehicle v = switch (type) {
                    case "car" -> new Car(id, brand, model, rate, Integer.parseInt(extra));
                    case "bike" -> new Bike(id, brand, model, rate, Integer.parseInt(extra));
                    case "van" -> new Van(id, brand, model, rate, Double.parseDouble(extra));
                    default -> null;
                };

                if (v != null) {
                    vehicles.add(v);
                    FileManager.save(vehicles, totalIncome);
                    JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Invalid type entered!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid data!");
            }
        });

        dialog.setVisible(true);
    }

    // ================= VIEW VEHICLES =================
    public static void showVehicleTables() {
        JFrame frame = new JFrame("Vehicle List");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(mainFrame);
        frame.setLayout(new GridLayout(3, 1, 10, 10));

        frame.add(createVehiclePanel("CAR", Car.class));
        frame.add(createVehiclePanel("BIKE", Bike.class));
        frame.add(createVehiclePanel("VAN", Van.class));

        frame.setVisible(true);
    }

    private static JPanel createVehiclePanel(String title, Class<?> type) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(title + " LIST", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(new Color(52, 73, 94));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(label, BorderLayout.NORTH);

        String[] columns = {"ID", "Brand", "Model", "Rate", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Vehicle v : vehicles) {
            if (type.isInstance(v)) {
                model.addRow(new Object[]{
                        v.getVehicleId(),
                        v.getBrand(),
                        v.getModel(),
                        v.getBaseRatePerDay(),
                        v.isAvailable() ? "Yes" : "No"
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if ("Yes".equals(value)) c.setForeground(new Color(39, 174, 96));
                else c.setForeground(new Color(192, 57, 43));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ================= RENT VEHICLE (WITH RECEIPT) =================
    public static void rentVehicleGUI() {
        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No vehicles available.");
            return;
        }

        JDialog dialog = new JDialog(mainFrame, "Rent Vehicle", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField daysField = new JTextField();
        JTextField nameField = new JTextField();

        dialog.add(new JLabel("Vehicle ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Rental Days:"));
        dialog.add(daysField);
        dialog.add(new JLabel("Customer Name:"));
        dialog.add(nameField);

        JButton rentBtn = new JButton("Rent Vehicle");
        styleMainButton(rentBtn, new Color(241, 196, 15));
        dialog.add(new JLabel());
        dialog.add(rentBtn);

        rentBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                Vehicle v = searchById(id);
                if (v == null || !v.isAvailable()) {
                    JOptionPane.showMessageDialog(dialog, "Vehicle not available!");
                    return;
                }
                int days = Integer.parseInt(daysField.getText().trim());
                String customerName = nameField.getText().trim();

                v.rentVehicle();
                double cost = v.calculateRentalCost(days);
                totalIncome += cost;

                String otp = generateOTP();
                otpMap.put(id, otp);
                customerNameMap.put(id, customerName);
                rentalDaysMap.put(id, days);

                FileManager.save(vehicles, totalIncome);

                // ===== SHOW RECEIPT =====
                String receipt = "===== RENTAL RECEIPT =====\n" +
                        "Customer Name : " + customerName + "\n" +
                        "Vehicle ID    : " + v.getVehicleId() + "\n" +
                        "Vehicle Type  : " + v.getClass().getSimpleName() + "\n" +
                        "Days Rented   : " + days + "\n" +
                        "Total Cost    : Rs." + cost + "\n" +
                        "RETURN OTP    : " + otp + "\n" +
                        "==========================";

                JOptionPane.showMessageDialog(dialog, receipt, "Rental Receipt", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid data!");
            }
        });

        dialog.setVisible(true);
    }

    // ================= RETURN VEHICLE =================
    public static void returnVehicleGUI() {
        JDialog dialog = new JDialog(mainFrame, "Return Vehicle", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField otpField = new JTextField();

        dialog.add(new JLabel("Vehicle ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Enter OTP:"));
        dialog.add(otpField);

        JButton returnBtn = new JButton("Return Vehicle");
        styleMainButton(returnBtn, new Color(231, 76, 60));
        dialog.add(new JLabel());
        dialog.add(returnBtn);

        returnBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String enteredOtp = otpField.getText().trim();
            Vehicle v = searchById(id);

            if (v == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid Vehicle ID!");
                return;
            }
            if (v.isAvailable()) {
                JOptionPane.showMessageDialog(dialog, "Vehicle is not rented!");
                return;
            }
            if (!otpMap.containsKey(id) || !otpMap.get(id).equals(enteredOtp)) {
                JOptionPane.showMessageDialog(dialog, "Invalid OTP! Cannot return vehicle.");
                return;
            }

            v.returnVehicle();
            otpMap.remove(id);
            customerNameMap.remove(id);
            rentalDaysMap.remove(id);

            FileManager.save(vehicles, totalIncome);

            JOptionPane.showMessageDialog(dialog, "Vehicle returned successfully!");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================= TOTAL INCOME =================
    public static void showTotalIncome() {
        JOptionPane.showMessageDialog(mainFrame, "Total Rental Income: Rs. " + totalIncome,
                "Total Income", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= HELPER =================
    private static Vehicle searchById(String id) {
        for (Vehicle v : vehicles)
            if (v.getVehicleId().equalsIgnoreCase(id))
                return v;
        return null;
    }

    private static String generateOTP() {
        Random r = new Random();
        return String.valueOf(100000 + r.nextInt(900000));
    }
}
