import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Clientchat extends JFrame implements ActionListener, Runnable {
    JTextArea ta1;
    JTextField t1;
    JButton b1, b2;
    JPanel p1, centerPanel, buttonPanel;
    String name;
    Thread th1;

    Socket s;
    BufferedReader br;
    BufferedWriter bw;

    public Clientchat() {
        this.setVisible(true);
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Chat Client");

        p1 = new JPanel();
        centerPanel = new JPanel();
        buttonPanel = new JPanel();

        ta1 = new JTextArea(5, 30);
        ta1.setEditable(false);
        JScrollPane js1 = new JScrollPane(ta1);

        b1 = new JButton("Send");
        b2 = new JButton("Sign Out");

        t1 = new JTextField(30);

        p1.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout());

        centerPanel.add(js1, BorderLayout.CENTER);
        centerPanel.add(t1, BorderLayout.SOUTH);

        p1.add(centerPanel, BorderLayout.CENTER);

        buttonPanel.add(b1);
        buttonPanel.add(b2);

        p1.add(buttonPanel, BorderLayout.SOUTH);

        this.add(p1);

        p1.setBorder(BorderFactory.createTitledBorder("Chat Client"));

        b1.addActionListener(this);
        b2.addActionListener(this);

        name = JOptionPane.showInputDialog("Enter name");
        if (name == null || name.isEmpty()) {
            System.exit(0);
        }

        this.setTitle("User: " + name);

        try {
            s = new Socket("localhost", 8888);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            bw.write(name + " has joined the chat");
            bw.newLine();
            bw.flush();

            th1 = new Thread(this);
            th1.start();
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                String message = br.readLine();
                if (message != null) {
                    ta1.append(message + "\n");
                }
            } catch (IOException e) {
                System.out.println("Read error: " + e.getMessage());
                break;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                String msg = name +":"+ t1.getText();
                if (bw != null) {
                    bw.write(msg);
                    bw.newLine();
                    bw.flush();
                }
                ta1.append(msg + "\n");
                t1.setText("");
            } catch (IOException ex) {
                System.out.println("Send error: " + ex.getMessage());
            }
        } else if (e.getSource() == b2) {
            try {
                if (bw != null) {
                    bw.write(name + " has left the chat");
                    bw.newLine();
                    bw.flush();
                }
                if (s != null) s.close();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Sign out error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Clientchat();
    }
}
