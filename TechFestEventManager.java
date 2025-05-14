import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class TechFestEventManager {
    static class Event {
        int id;
        String name;
        String description;

        Event(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | " + name + " - " + description;
        }
    }

    static class Participant {
        int id;
        String name;

        Participant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Name: " + name;
        }
    }

    static class Registration {
        Participant participant;
        Event event;

        Registration(Participant participant, Event event) {
            this.participant = participant;
            this.event = event;
        }

        @Override
        public String toString() {
            return participant.name + " registered for " + event.name;
        }
    }

    static java.util.List<Event> events = new java.util.ArrayList<>();
    static java.util.List<Participant> participants = new java.util.ArrayList<>();
    static java.util.List<Registration> registrations = new java.util.ArrayList<>();

    static Scanner sc = new Scanner(System.in);
    static int eventCounter = 1, participantCounter = 1;

    static void saveEventsToFile() {
        try (PrintWriter writer = new PrintWriter("events.txt")) {
            for (Event e : events) {
                writer.println(e.id + ";" + e.name + ";" + e.description);
            }
        } catch (Exception e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    static void loadEventsFromFile() {
        try (Scanner fileScanner = new Scanner(new File("events.txt"))) {
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(";");
                if (data.length == 3) {
                    events.add(new Event(Integer.parseInt(data[0]), data[1], data[2]));
                    eventCounter = Math.max(eventCounter, Integer.parseInt(data[0]) + 1);
                }
            }
        } catch (Exception e) {
            System.out.println("No saved events found.");
        }
    }

    static void addEvent() {
        System.out.print("Enter event name: ");
        String name = sc.nextLine();
        System.out.print("Enter event description: ");
        String desc = sc.nextLine();
        events.add(new Event(eventCounter++, name, desc));
        saveEventsToFile();
        System.out.println("Event added successfully.\n");
    }

    static void viewEvents() {
        if (events.isEmpty()) {
            System.out.println("No events available.\n");
            return;
        }
        System.out.println("Available Events:");
        for (Event e : events) {
            System.out.println(e);
        }
        System.out.println();
    }

    static void viewParticipants() {
        if (participants.isEmpty()) {
            System.out.println("No participants registered.\n");
            return;
        }
        System.out.println("Registered Participants:");
        for (Participant p : participants) {
            System.out.println(p);
        }
        System.out.println();
    }

    static void viewRegistrations() {
        if (registrations.isEmpty()) {
            System.out.println("No registrations yet.\n");
            return;
        }
        System.out.println("Registrations:");
        for (Registration r : registrations) {
            System.out.println(r);
        }
        System.out.println();
    }

    static void registerParticipant() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        Participant p = new Participant(participantCounter++, name);
        participants.add(p);
        System.out.println("Registered with ID: " + p.id + "\n");

        viewEvents();
        System.out.print("Enter Event ID to register: ");
        int eid;
        try {
            eid = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.\n");
            return;
        }

        Event selectedEvent = null;
        for (Event e : events) {
            if (e.id == eid) {
                selectedEvent = e;
                break;
            }
        }
        if (selectedEvent != null) {
            registrations.add(new Registration(p, selectedEvent));
            System.out.println("Successfully registered for " + selectedEvent.name + "\n");
        } else {
            System.out.println("Invalid Event ID.\n");
        }
    }

    public static void main(String[] args) {
        loadEventsFromFile();
        while (true) {
            System.out.println("=== TechFest Event Management ===");
            System.out.println("1. Admin - Add Event");
            System.out.println("2. Admin - View Events");
            System.out.println("3. Admin - View Participants");
            System.out.println("4. Admin - View Registrations");
            System.out.println("5. Participant - Register & Join Event");
            System.out.println("6. GUI Mode");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();
            System.out.println();

            switch (choice) {
                case "1": addEvent(); break;
                case "2": viewEvents(); break;
                case "3": viewParticipants(); break;
                case "4": viewRegistrations(); break;
                case "5": registerParticipant(); break;
                case "6": javax.swing.SwingUtilities.invokeLater(() -> new TechFestGUI()); break;
                case "0": System.out.println("Exiting..."); return;
                default: System.out.println("Invalid choice.\n");
            }
        }
    }

    static class TechFestGUI extends JFrame {
        JTextArea displayArea = new JTextArea(10, 40);

        TechFestGUI() {
            setTitle("TechFest Event Manager GUI");
            setSize(500, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new FlowLayout());

            JButton viewBtn = new JButton("View Events");
            JButton addBtn = new JButton("Add Event");
            JButton regBtn = new JButton("Register Participant");
            JButton viewRegBtn = new JButton("View Registrations");
            JButton closeBtn = new JButton("Close");

            add(viewBtn);
            add(addBtn);
            add(regBtn);
            add(viewRegBtn);
            add(closeBtn);
            add(new JScrollPane(displayArea));

            viewBtn.addActionListener(e -> displayEvents());
            addBtn.addActionListener(e -> guiAddEvent());
            regBtn.addActionListener(e -> guiRegister());
            viewRegBtn.addActionListener(e -> displayRegistrations());
            closeBtn.addActionListener(e -> dispose());

            setVisible(true);
        }

        void displayEvents() {
            if (events.isEmpty()) {
                displayArea.setText("No events available.\n");
                return;
            }
            StringBuilder sb = new StringBuilder("Available Events:\n");
            for (Event e : events) sb.append(e).append("\n");
            displayArea.setText(sb.toString());
        }

        void guiAddEvent() {
            String name = JOptionPane.showInputDialog(this, "Event Name:");
            if (name == null || name.trim().isEmpty()) return;

            String desc = JOptionPane.showInputDialog(this, "Event Description:");
            if (desc == null || desc.trim().isEmpty()) return;

            events.add(new Event(eventCounter++, name, desc));
            saveEventsToFile();
            displayArea.setText("Event added successfully.\n");
        }

        void guiRegister() {
            String pname = JOptionPane.showInputDialog(this, "Participant Name:");
            if (pname == null || pname.trim().isEmpty()) return;

            Participant p = new Participant(participantCounter++, pname);
            participants.add(p);

            if (events.isEmpty()) {
                displayArea.setText("No events available.\n");
                return;
            }

            String[] options = events.stream().map(e -> e.id + ": " + e.name).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(this, "Choose Event:",
                    "Event Selection", JOptionPane.PLAIN_MESSAGE, null,
                    options, options[0]);

            if (selected != null) {
                int id = Integer.parseInt(selected.split(":")[0]);
                for (Event e : events) {
                    if (e.id == id) {
                        registrations.add(new Registration(p, e));
                        displayArea.setText(p.name + " registered for " + e.name + "\n");
                        return;
                    }
                }
            }
        }

        void displayRegistrations() {
            if (registrations.isEmpty()) {
                displayArea.setText("No registrations yet.\n");
                return;
            }
            StringBuilder sb = new StringBuilder("Registrations:\n");
            for (Registration r : registrations) sb.append(r).append("\n");
            displayArea.setText(sb.toString());
        }
    }
}
