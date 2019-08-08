package com.pages.page2;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Page2 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel root;
  private JPanel mainPanel;

  private JPanel labelsPanel;
  private JLabel nameLabel;

  private JPanel textPanel;
  private JTextField nameTextField;
  private JTextField phoneTextField;
  private JTextField emailTextField;
  private JTextField surnameTextField;
  private JComboBox<Contact> contactsComboBox;


  private JPanel buttonsPanel;
  private JButton addButton;
  private JButton printButton;
  private JButton deleteButton;
  private JButton editButton;
  private JButton saveButton;
  private JButton cancelButton;
  private ContactBook contactBook = new ContactBook();
  private boolean edit = false;
  private Contact editContact = new Contact();

  // Constructor
  public Page2(final Gui gui) {
    this.gui = gui;
    root = gui.getTab2();
//    this.nameTextField = gui.getNameTextField2();
//    this.surnameTextField = gui.getSurnameTextField2();
//    this.phoneTextField = gui.getPhoneTextField2();
//    this.emailTextField = gui.getEmailTextField2();
//    this.addButton = gui.getAddContactButton2();
//    this.editButton = gui.getEditButton2();
//    this.saveButton = gui.getSaveButton2();
//    this.cancelButton = gui.getCancelButton2();
//    this.printButton = gui.getPrintAllContactsButton2();
//    this.deleteButton = gui.getDeleteButton2();
//    this.contactsComboBox = gui.getContactsComboBox2();

//    dynamicEditDeletePrintButtons();
//    dynamicAddSaveButtons();
//    addListeners();

//    temp();

    initComponents();
  }

  private void temp() {
//    DB.populateContactBook();


//    contactBook.add(new Contact("Max", "Fry", "max@mail.com", "0766533"));
//    contactBook.add(new Contact("Bob", "Person", "bob@mail.com", "0765013"));
//    contactBook.getContacts().stream().forEach(c -> contactsComboBox.addItem(c));
  }

  // Initialize defaults values on start
  private void initComponents() {
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
        contactBook.remove(contactsComboBox.getSelectedIndex());
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
        gui.getPagesPanel().setEnabled(false);
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

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        contactsComboBox.setEnabled(true);
        gui.getPagesPanel().setEnabled(true);

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
        gui.getPagesPanel().setEnabled(true);
        clearAllTextFields();
        edit = false;
      }
    });
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
