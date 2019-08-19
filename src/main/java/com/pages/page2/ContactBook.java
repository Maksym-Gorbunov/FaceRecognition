package com.pages.page2;

import com.db.MongoDB;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ContactBook {
  private List<Contact> contacts = new ArrayList<>();
//  private MongoDB db;

//  public void connect(){
//    db = new MongoDB();
//  }

  public void add(Contact contact) {
    contacts.add(contact);
    MongoDB.insertContact(contact);
  }

  public void remove(Contact contact) {
    contacts.remove(contact);
    MongoDB.deleteContact(contact);
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
    List<Document> result = MongoDB.getAllDocuments();
    for(Document document : result){
      Contact contact = new Contact();
      contact.set_id((ObjectId) document.get("_id"));
      contact.setName((String) document.get("name"));
      contact.setSurname((String) document.get("surname"));
      contact.setPhone((String) document.get("phone"));
      contact.setEmail((String) document.get("email"));
      contacts.add(contact);
    }
  }

  public void editContact(Contact contact){
    contacts.stream().filter(c -> c.get_id() == contact.get_id()).forEach(c -> {
      c.setName(contact.getName());
      c.setSurname(contact.getSurname());
      c.setPhone(contact.getPhone());
      c.setEmail(contact.getEmail());
    });
    MongoDB.updateContact(contact);
  }
}
