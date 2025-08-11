import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Billing extends JFrame {
    JTextField bookingIdField;
    JTextArea billArea;

    public Billing() {
        setTitle("Billing");
        setSize(500, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(new JLabel("Booking ID:"));
        bookingIdField = new JTextField();
        panel.add(bookingIdField);

        JButton generateBtn = new JButton("Generate Bill");
        panel.add(generateBtn);

        billArea = new JTextArea();
        billArea.setEditable(false);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(billArea), BorderLayout.CENTER);

        generateBtn.addActionListener(e -> generateBill());
    }

    private void generateBill() {
        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Booking ID must be a number.");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT b.*, r.room_number, r.type FROM bookings b LEFT JOIN rooms r ON b.room_id = r.room_id WHERE booking_id=?");
            pst.setInt(1, bookingId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                StringBuilder bill = new StringBuilder();
                bill.append("----- Hotel Invoice -----\n");
                bill.append("Booking ID: ").append(rs.getInt("booking_id")).append("\n");
                bill.append("Customer: ").append(rs.getString("customer_name")).append("\n");
                bill.append("Phone: ").append(rs.getString("phone")).append("\n");
                bill.append("Room ID: ").append(rs.getInt("room_id")).append(" (").append(rs.getString("room_number")).append(")\n");
                bill.append("Type: ").append(rs.getString("type")).append("\n");
                bill.append("Check-In: ").append(rs.getDate("check_in")).append("\n");
                bill.append("Check-Out: ").append(rs.getDate("check_out")).append("\n");
                bill.append("Total Amount: ₹").append(rs.getDouble("total_amount")).append("\n");
                bill.append("-------------------------\n");
                billArea.setText(bill.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
