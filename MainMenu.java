import javax.swing.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    public MainMenu(String role) {
        setTitle("Hotel Booking System - " + role.toUpperCase());
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu roomMenu = new JMenu("Rooms");
        JMenuItem manageRooms = new JMenuItem("Manage Rooms");
        roomMenu.add(manageRooms);

        JMenu bookingMenu = new JMenu("Bookings");
        JMenuItem manageBookings = new JMenuItem("Manage Bookings");
        JMenuItem billing = new JMenuItem("Billing");
        bookingMenu.add(manageBookings);
        bookingMenu.add(billing);

        menuBar.add(roomMenu);
        menuBar.add(bookingMenu);

        setJMenuBar(menuBar);

        manageRooms.addActionListener(e -> new RoomManagement().setVisible(true));
        manageBookings.addActionListener(e -> new BookingManagement().setVisible(true));
        billing.addActionListener(e -> new Billing().setVisible(true));
    }
}
