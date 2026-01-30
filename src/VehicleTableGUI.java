import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VehicleTableGUI {

    private static ArrayList<Vehicle> vehicles = new ArrayList<>();

    public static void main(String[] args) {
        // Sample data
        vehicles.add(new Car("C001", "Toyota", "Corolla", 5000, 5));
        vehicles.add(new Bike("B001", "Yamaha", "R15", 1500, 155));
        vehicles.add(new Van("V001", "Nissan", "NV200", 7000, 1200));
        vehicles.add(new Car("C002", "Honda", "Civic", 5500, 5));

        SwingUtilities.invokeLater(VehicleTableGUI::showVehicleTables);
    }

    private static void showVehicleTables() {
        JFrame frame = new JFrame("Vehicle List");
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(3, 1, 10, 10));

        frame.add(createVehiclePanel("CAR", Car.class));
        frame.add(createVehiclePanel("BIKE", Bike.class));
        frame.add(createVehiclePanel("VAN", Van.class));

        frame.setVisible(true);
    }

    private static JPanel createVehiclePanel(String title, Class<?> type) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(title + " LIST", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
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

        // Renderer for Available column (color)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if ("Yes".equals(value)) {
                    c.setForeground(new Color(0, 128, 0)); // Green
                } else {
                    c.setForeground(Color.RED);
                }
                c.setFont(new Font("Arial", Font.PLAIN, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
