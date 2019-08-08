package com.db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pages.page2.Contact;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
  static MongoClient mongoClient;
  static MongoDatabase database;
  static String collectionName = "Contacts";
  static MongoCollection<Document> collection;

  static {
    MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/test?retryWrites=true&w=majority");
    mongoClient = new MongoClient(uri);
    Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
    mongoLogger.setLevel(Level.SEVERE);
    database = mongoClient.getDatabase("hilodb");
    collection = database.getCollection(collectionName);
  }


  public static void populateContactBook() {
//    createCollection("Contacts");
//    insertContact("Contacts", new Contact("Will", "Torre", "will@mail.com", "09879855"));
//    insertContact("Contacts", new Contact("Nick", "Lonny", "nick@mail.com", "09879855"));

    printContacts();
//    updateFieldByContactName("Contacts", "Will", "newField", "uuuuu");
//    findAndPrint("name", "Will");
//    updateIncrement();
  }


  static void printContacts() {
//    MongoCollection<Document> collection = database.getCollection(collectionName);
    List<Document> result = collection.find().into(new ArrayList<>());
    result.stream().map(Document::toJson).forEach(System.out::println);
    /* OLD Style Java:
    for (Document doc : result) {
      System.out.println(doc.toJson());
    }
    */
  }

  // Create collection
  static void createCollection(String collectionName) {
    MongoCollection<Document> collection = database.getCollection(collectionName);
    collection.insertOne(new Document());
  }

  // Delete collection
  static void deleteCollection(String collectionName) {
    MongoCollection<Document> collection = database.getCollection(collectionName);
    collection.drop();
    collection = database.getCollection(collectionName);
    System.out.println(collection);
  }

  // Lists all database names
  static void listDatabases() {
    for (String db : mongoClient.listDatabaseNames()) {
      System.out.println(db);
    }
  }

  // Lists all collection names
  static void listCollectionNames() {
    for (String collectionName : database.listCollectionNames()) {
      System.out.println(collectionName);
    }
  }

  static void insertContact(String collectionName, Contact contact) {
    MongoCollection<Document> collection = database.getCollection(collectionName);
    Document document = new Document("name", contact.getName())
            .append("surname", contact.getSurname())
            .append("email", contact.getEmail())
            .append("phone", contact.getPhone());
    collection.insertOne(document);
  }

//  static void insertManyContacts(String collectionName, List<Contact> contact) {
//    MongoCollection<Document> collection = database.getCollection(collectionName);
//    List<Document> document = asList(
//            new Document ("name", contact.getName())
//                    .append("surname", contact.getSurname())
//                    .append("email", contact.getEmail())
//                    .append("phone", contact.getPhone()),
//
//            new Document("name", "Patrik")
//                    .append("email","patrik@email.com")
//                    .append("customerId", 101));
//    collection.insertMany(document);
//  }


  static void updateFieldByContactName(String collectionName, String contactName, String fieldName, String fieldValue) {
    MongoCollection<Document> collection = database.getCollection(collectionName);
    BasicDBObject newDocument = new BasicDBObject();
    newDocument.append("$set", new BasicDBObject().append(fieldName, fieldValue));
    BasicDBObject query = new BasicDBObject().append("name", contactName);
    collection.updateOne(query, newDocument);
  }

  static void findAndPrint(String field, String value) {
    MongoCollection<Document> collection = database.getCollection("Contacts");
    List<Document> result = collection.find(new Document(field, value)).into(new ArrayList<>());
    System.out.println(result);
  }


  static void updateIncrement() {
    MongoCollection<Document> collection = database.getCollection("Contacts");
    BasicDBObject updateQuery = new BasicDBObject();
    updateQuery.append("$inc", new BasicDBObject().append("age", 555));
    BasicDBObject searchQuery = new BasicDBObject().append("name", "Max");
    collection.updateOne(searchQuery, updateQuery);
  }

}