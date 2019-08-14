package com.pages.page2;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// CRUD MongoDB
public class Page2 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab2;

  private JPanel comboboxPanel = new JPanel();
  private JComboBox<Contact> contactsComboBox = new JComboBox<Contact>();

  private JPanel mainPanel = new JPanel();

  private JPanel labelsPanel = new JPanel();
  private JLabel nameLabel = new JLabel("name:");
  private JLabel surnameLabel = new JLabel("surname:");
  private JLabel phoneLabel = new JLabel("phone:");
  private JLabel emailLabel = new JLabel("email:");

  private JPanel textPanel = new JPanel();
  private JTextField nameTextField = new JTextField();
  private JTextField surnameTextField = new JTextField();
  private JTextField phoneTextField = new JTextField();
  private JTextField emailTextField = new JTextField();

  private JPanel buttonsPanel = new JPanel();
  private JButton addButton = new JButton("Add");
  private JButton printButton = new JButton("Print");
  private JButton deleteButton = new JButton("Delete");
  private JButton editButton = new JButton("Edit");
  private JButton saveButton = new JButton("Save");
  private JButton cancelButton = new JButton("Cancel");

  private ContactBook contactBook = new ContactBook();
  private boolean edit = false;
  private Contact editContact = new Contact();


  // Constructor
  public Page2(final Gui gui) {
    this.gui = gui;
    tab2 = gui.getTab2();

    dynamicEditDeletePrintButtons();
    dynamicAddSaveButtons();

    initComponents();
    addListeners();

    populateMenuBar();

    contactBook.populateContactBook();
    populateComboBox();
  }

  private void populateComboBox() {
    contactBook.getContacts().stream().forEach(c -> contactsComboBox.addItem(c));
  }

  // Initialize defaults values on start
  private void initComponents() {
    comboboxPanel.setPreferredSize(new Dimension(800, 50));
    comboboxPanel.add(contactsComboBox);
    tab2.add(comboboxPanel);

    mainPanel.setPreferredSize(new Dimension(800, 400));
    mainPanel.setLayout(new GridLayout(1, 2));

    labelsPanel.setLayout(new GridLayout(4, 1));
    labelsPanel.setBackground(Color.green);
    labelsPanel.add(nameLabel);
    labelsPanel.add(surnameLabel);
    labelsPanel.add(phoneLabel);
    labelsPanel.add(emailLabel);
    mainPanel.add(labelsPanel);

    textPanel.setLayout(new GridLayout(4, 1));
    textPanel.setBackground(Color.red);
    textPanel.add(nameTextField);
    textPanel.add(surnameTextField);
    textPanel.add(phoneTextField);
    textPanel.add(emailTextField);
    mainPanel.add(textPanel);

    tab2.add(mainPanel);

    buttonsPanel.setPreferredSize(new Dimension(800, 50));
    buttonsPanel.add(addButton);
    buttonsPanel.add(printButton);
    buttonsPanel.add(deleteButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(cancelButton);
    tab2.add(buttonsPanel);

    saveButton.setVisible(false);
    cancelButton.setVisible(false);
  }

  // Add gui elements listeners
  private void addListeners() {
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name = nameTextField.getText();
        String surname = surnameTextField.getText();
        String email = emailTextField.getText();
        String phone = phoneTextField.getText();
        if (!name.equals("") && !surname.equals("") && !email.equals("") && !phone.equals("")) {
          Contact contact = new Contact(name, surname, email, phone);
          if (!contactBook.getContacts().contains(contact)) {
            contactsComboBox.addItem(contact);
            contactBook.add(contact);
            System.out.println("New contact was added successfully!");
          } else {
            System.out.println("Contact already exist");
          }
        }
        clearAllTextFields();
      }
    });

    printButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contactBook.printAllContacts();
        System.out.println("Total: " + contactBook.getContacts().size());
        clearAllTextFields();
      }
    });

    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contactBook.remove((Contact) contactsComboBox.getSelectedItem());
        contactsComboBox.removeItem(contactsComboBox.getSelectedItem());
        System.out.println("Contact was successfully removed!");
        clearAllTextFields();
      }
    });

    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        edit = true;
        Contact contact = (Contact) contactsComboBox.getSelectedItem();
        editContact = contact;
        nameTextField.setText(contact.getName());
        surnameTextField.setText(contact.getSurname());
        phoneTextField.setText(contact.getPhone());
        emailTextField.setText(contact.getEmail());

        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        contactsComboBox.setEnabled(false);
        gui.getTabs().setEnabled(false);
      }
    });

    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Contact contact = (Contact) contactsComboBox.getSelectedItem();
        contact.setName(nameTextField.getText());
        contact.setSurname(surnameTextField.getText());
        contact.setPhone(phoneTextField.getText());
        contact.setEmail(emailTextField.getText());
        contactBook.editContact(contact);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        contactsComboBox.setEnabled(true);
        gui.getTabs().setEnabled(true);
        clearAllTextFields();
        edit = false;
      }
    });

    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        contactsComboBox.setEnabled(true);
        gui.getTabs().setEnabled(true);
        clearAllTextFields();
        edit = false;
      }
    });
  }

  public void populateMenuBar() {
    System.out.println("Refresh MongoDB code will run here");
//    JMenu fileMenu = gui.getJMenuBar().getMenu(0);
//    JMenuItem mongoRefresh = new JMenuItem("Mongo refresh");
//    fileMenu.add(mongoRefresh);
//
//    mongoRefresh.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        contactBook.populateContactBook();
//        populateComboBox();
//        gui.getTabs().setSelectedComponent(tab2);
//      }
//    });
  }

  // Clear all text fields
  public void clearAllTextFields() {
    nameTextField.setText("");
    surnameTextField.setText("");
    emailTextField.setText("");
    phoneTextField.setText("");
  }

  // Check if all text fields not empty
  public boolean fieldsNotEmpty() {
    if ((nameTextField.getText().trim().length() > 0)
            && (surnameTextField.getText().trim().length() > 0)
            && (emailTextField.getText().trim().length() > 0)
            && (phoneTextField.getText().trim().length() > 0)) {
      return true;
    }
    return false;
  }

  // Dynamic edit, delete, print buttons start
  private void dynamicEditDeletePrintButtons() {
    editButton.setEnabled(false);
    printButton.setEnabled(false);
    deleteButton.setEnabled(false);
    new Thread(comboBoxTarget).start();
    contactsComboBox.addActionListener(comboBoxActionListener);
  }

  final ActionListener comboBoxActionListener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equalsIgnoreCase("Enable")) {
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
        printButton.setEnabled(true);
      } else if (e.getActionCommand().equalsIgnoreCase("Disable")) {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        printButton.setEnabled(false);
      }
    }
  };

  final Runnable comboBoxTarget = new Runnable() {
    public void run() {
      while (true) {
        final ActionListener[] listeners = contactsComboBox.getActionListeners();
        for (ActionListener listener : listeners) {
          // edit, delete, print case
          if (!edit && contactsComboBox.getItemCount() > 0) {
            final ActionEvent event = new ActionEvent(contactsComboBox, 1, "Enable");
            listener.actionPerformed(event);
          } else {
            final ActionEvent event = new ActionEvent(contactsComboBox, 1, "Disable");
            listener.actionPerformed(event);
          }
        }
      }
    }
  };
  // Dynamic edit, delete, print buttons end


  // Dynamic add, save buttons start
  private void dynamicAddSaveButtons() {
    addButton.setEnabled(false);
    saveButton.setEnabled(false);
    new Thread(fieldsTarget).start();
    nameTextField.addActionListener(fieldsActionListener);
  }

  final ActionListener fieldsActionListener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equalsIgnoreCase("AddEnable")) {
        addButton.setEnabled(true);
      }
      if (e.getActionCommand().equalsIgnoreCase("SaveEnable")) {
        saveButton.setEnabled(true);
      } else if (e.getActionCommand().equalsIgnoreCase("Disable")) {
        addButton.setEnabled(false);
        saveButton.setEnabled(false);
      }
    }
  };

  final Runnable fieldsTarget = new Runnable() {
    public void run() {
      while (true) {
        final ActionListener[] listeners = nameTextField.getActionListeners();
        for (ActionListener listener : listeners) {
          if (fieldsNotEmpty()) {
            // add case
            if (!edit) {
              final ActionEvent addEvent = new ActionEvent(nameTextField, 1, "AddEnable");
              listener.actionPerformed(addEvent);
            }
            // save case
            if (edit) {
              final ActionEvent saveEvent = new ActionEvent(nameTextField, 1, "SaveEnable");
              listener.actionPerformed(saveEvent);
            }
          } else {
            final ActionEvent event = new ActionEvent(nameTextField, 1, "Disable");
            listener.actionPerformed(event);
          }
        }
      }
    }
  };
  // Dynamic add, save buttons end

}
