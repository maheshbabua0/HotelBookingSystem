import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingManagement extends JFrame {

    private JTextField nameField, phoneField, roomIdField, checkInField, checkOutField;
    private JTable bookingTable;
    private DefaultTableModel bookingModel;

    public BookingManagement() {
        setTitle("Booking Management");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        inputPanel.add(new JLabel("Customer Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        inputPanel.add(new JLabel("Room ID:"));
        roomIdField = new JTextField();
        inputPanel.add(roomIdField);

        inputPanel.add(new JLabel("Check-In Date (YYYY-MM-DD):"));
        checkInField = new JTextField();
        inputPanel.add(checkInField);

        inputPanel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"));
        checkOutField = new JTextField();
        inputPanel.add(checkOutField);

        JButton bookButton = new JButton("Book Room");
        bookButton.addActionListener(e -> bookRoom());
        inputPanel.add(bookButton);

        JButton loadButton = new JButton("Load Bookings");
        loadButton.addActionListener(e -> loadBookings());
        inputPanel.add(loadButton);

        add(inputPanel, BorderLayout.NORTH);

        bookingModel = new DefaultTableModel(new String[]{
                "Booking ID", "Customer Name", "Phone", "Room ID", "Check-In", "Check-Out", "Total Amount"
        }, 0);

        bookingTable = new JTable(bookingModel);
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        loadBookings();
    }

    private void bookRoom() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        int roomId;
        try {
            roomId = Integer.parseInt(roomIdField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Room ID");
            return;
        }
        String checkIn = checkInField.getText();
        String checkOut = checkOutField.getText();

        try (Connection con = DBConnection.getConnection()) {

            // Step 1: Check if room exists
            PreparedStatement pst = con.prepareStatement("SELECT price, status FROM rooms WHERE room_id=?");
            pst.setInt(1, roomId);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Room not found!");
                return;
            }

            double price = rs.getDouble("price");
            String status = rs.getString("status");

            // Optional: prevent booking if already marked booked
            if ("booked".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Room is already booked!");
                return;
            }

            // Step 2: Check overlapping bookings
            String checkQuery = "SELECT * FROM bookings " +
                    "WHERE room_id = ? " +
                    "AND ( (check_in <= ? AND check_out >= ?) " +
                    "OR (check_in <= ? AND check_out >= ?) " +
                    "OR (check_in >= ? AND check_out <= ?) )";

            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, roomId);
            checkStmt.setString(2, checkOut);
            checkStmt.setString(3, checkIn);
            checkStmt.setString(4, checkIn);
            checkStmt.setString(5, checkOut);
            checkStmt.setString(6, checkIn);
            checkStmt.setString(7, checkOut);

            ResultSet rs2 = checkStmt.executeQuery();
            if (rs2.next()) {
                JOptionPane.showMessageDialog(this, "Room is already booked for these dates!");
                return;
            }

            // Step 3: Insert booking
            PreparedStatement insertBooking = con.prepareStatement(
                    "INSERT INTO bookings (customer_name, phone, room_id, check_in, check_out, total_amount) VALUES (?, ?, ?, ?, ?, ?)");
            insertBooking.setString(1, name);
            insertBooking.setString(2, phone);
            insertBooking.setInt(3, roomId);
            insertBooking.setString(4, checkIn);
            insertBooking.setString(5, checkOut);
            insertBooking.setDouble(6, price);
            insertBooking.executeUpdate();

            // Step 4: Mark room as booked
            PreparedStatement updateRoom = con.prepareStatement("UPDATE rooms SET status='booked' WHERE room_id=?");
            updateRoom.setInt(1, roomId);
            updateRoom.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room booked successfully!");
            loadBookings();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadBookings() {
        bookingModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM bookings ORDER BY check_in ASC");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                bookingModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getInt("room_id"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getDouble("total_amount")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BookingManagement().setVisible(true);
        });
    }
}
