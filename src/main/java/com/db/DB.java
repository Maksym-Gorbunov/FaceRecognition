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
  private MongoClient client;
  private MongoDatabase db;
  private String dbName = "hilodb";
  private String collectionName = "Contacts";
  private MongoCollection<Document> collection;

  public DB() {
    String atlasConnectionString = "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/test?retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(atlasConnectionString);
    client = new MongoClient(uri);
    Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
    mongoLogger.setLevel(Level.SEVERE);
    db = client.getDatabase(dbName);
    collection = db.getCollection(collectionName);
  }


  public void insertContact(Contact contact) {
    MongoCollection<Document> collection = db.getCollection(collectionName);
    Document document = new Document("name", contact.getName())
            .append("surname", contact.getSurname())
            .append("email", contact.getEmail())
            .append("phone", contact.getPhone());
    collection.insertOne(document);
  }


  public List<Document> getAllDocuments() {
    List<Document> result = collection.find().into(new ArrayList<>());
    return result;
  }














  // Create collection
  public void createCollection(String collectionName) {
    MongoCollection<Document> collection = db.getCollection(collectionName);
    collection.insertOne(new Document());
  }

  // Delete collection
  public void deleteCollection(String collectionName) {
    MongoCollection<Document> collection = db.getCollection(collectionName);
    collection.drop();
    collection = db.getCollection(collectionName);
    System.out.println(collection);
  }

  // Lists all db names
  public void listDatabases() {
    for (String db : client.listDatabaseNames()) {
      System.out.println(db);
    }
  }

  // Lists all collection names
  public void listCollectionNames() {
    for (String collectionName : db.listCollectionNames()) {
      System.out.println(collectionName);
    }
  }


//  static void insertManyContacts(String collectionName, List<Contact> contact) {
//    MongoCollection<Document> collection = db.getCollection(collectionName);
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


  public void updateFieldByContactName(String collectionName, String contactName, String fieldName, String fieldValue) {
    MongoCollection<Document> collection = db.getCollection(collectionName);
    BasicDBObject newDocument = new BasicDBObject();
    newDocument.append("$set", new BasicDBObject().append(fieldName, fieldValue));
    BasicDBObject query = new BasicDBObject().append("name", contactName);
    collection.updateOne(query, newDocument);
  }

  public void findAndPrint(String field, String value) {
    MongoCollection<Document> collection = db.getCollection("Contacts");
    List<Document> result = collection.find(new Document(field, value)).into(new ArrayList<>());
    System.out.println(result);
  }


  public void updateIncrement() {
    MongoCollection<Document> collection = db.getCollection("Contacts");
    BasicDBObject updateQuery = new BasicDBObject();
    updateQuery.append("$inc", new BasicDBObject().append("age", 555));
    BasicDBObject searchQuery = new BasicDBObject().append("name", "Max");
    collection.updateOne(searchQuery, updateQuery);
  }


}