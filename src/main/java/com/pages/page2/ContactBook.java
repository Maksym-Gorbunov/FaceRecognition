package com.pages.page2;

import com.db.DB;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ContactBook {
  private List<Contact> contacts = new ArrayList<>();
  private DB db = new DB();

  public void add(Contact contact) {
    contacts.add(contact);
    db.insertContact(contact);
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

  public void populateContactBook() {
    List<Document> result = db.getAllDocuments();
    for(Document document : result){
      Contact contact = new Contact();
      contact.setName((String) document.get("name"));
      contact.setSurname((String) document.get("surname"));
      contact.setPhone((String) document.get("phone"));
      contact.setEmail((String) document.get("email"));
      contacts.add(contact);
    }
  }
}
