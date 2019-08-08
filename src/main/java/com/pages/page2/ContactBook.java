package com.pages.page2;

import java.util.ArrayList;
import java.util.List;

public class ContactBook {
  private List<Contact> contacts = new ArrayList<>();

  public void add(Contact contact) {
    contacts.add(contact);
  }

  public void remove(int index) {
    contacts.remove(index);
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  public void printAllContacts() {
    for (Contact contact : contacts) {
      System.out.println(contact);
    }
  }
}
