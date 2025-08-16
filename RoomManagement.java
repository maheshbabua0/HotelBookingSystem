import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RoomManagement extends JFrame {
    JTextField roomNumberField, typeField, priceField;
    JTable table;
    DefaultTableModel model;

    public RoomManagement() {
        setTitle("Manage Rooms");
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        panel.add(roomNumberField);

        panel.add(new JLabel("Type:"));
        typeField = new JTextField();
        panel.add(typeField);

        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);

        JButton addBtn = new JButton("Add Room");
        panel.add(addBtn);

        add(panel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Number", "Type", "Price", "Status"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addRoom());
        loadRooms();
    }

    private void addRoom() {
        String number = roomNumberField.getText();
        String type = typeField.getText();
        String price = priceField.getText();

        try (Connection con = DBConnection.getConnection()) {
            String query = "INSERT INTO rooms (room_number, type, price) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, number);
            pst.setString(2, type);
            pst.setDouble(3, Double.parseDouble(price));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room added successfully!");
            loadRooms();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadRooms() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
