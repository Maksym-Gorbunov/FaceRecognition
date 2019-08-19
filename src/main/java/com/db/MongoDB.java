package com.db;

import org.apache.commons.io.FilenameUtils;
import com.constants.Constants;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.pages.page2.Contact;
import org.bson.Document;
import org.bson.conversions.Bson;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDB {
  private MongoClient client;
  private MongoDatabase mongoDatabase;
  private DB db;
  private String databaseName;
  private String collectionName;
  private MongoCollection<Document> collection;
  private String atlasConnectionString;

  public MongoDB() {
    System.out.println("Connecting to MongoDB on cloud..");
    databaseName = "hilodb";
    collectionName = "Contacts";
    atlasConnectionString = "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/test?retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(atlasConnectionString);
    client = new MongoClient(uri);
    Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
    mongoLogger.setLevel(Level.SEVERE);
    mongoDatabase = client.getDatabase(databaseName);
    collection = mongoDatabase.getCollection(collectionName);
    db =  client.getDB(databaseName);
  }

  // Upload file to db
  public void uploadFile(File file) {
    String fileName = FilenameUtils.removeExtension(file.getName());
    File imageFile = new File(file.getAbsolutePath());
    GridFS gfsPhoto = new GridFS(db, "images");
    GridFSInputFile gfsFile = null;
    try {
      gfsFile = gfsPhoto.createFile(imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    gfsFile.setFilename(fileName);
    gfsFile.save();
  }

  // Download file from db, ex "colors.png"
  public void downloadFile(String fileName) {
    GridFS gfsPhoto = new GridFS(db, "images");
    String fName = FilenameUtils.removeExtension(fileName);
    GridFSDBFile imageForOutput = gfsPhoto.findOne(fName);
    try {
      imageForOutput.writeTo(Constants.imgPath+fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(imageForOutput);
  }







  public void insertContact(Contact contact) {
    Document document = new Document("_id", contact.get_id())
            .append("name", contact.getName())
            .append("surname", contact.getSurname())
            .append("email", contact.getEmail())
            .append("phone", contact.getPhone());
    collection.insertOne(document);
  }

  public List<Document> getAllDocuments() {
    List<Document> result = collection.find().into(new ArrayList<>());
    return result;
  }

  public void deleteContact(Contact contact) {
    Bson filter = new Document("_id", contact.get_id());
    collection.deleteOne(filter);
  }

  public void updateContact(Contact contact) {
    BasicDBObject newDocument = new BasicDBObject();
    newDocument.append("$set", new BasicDBObject()
            .append("name", contact.getName())
            .append("surname", contact.getSurname())
            .append("email", contact.getEmail())
            .append("phone", contact.getPhone()));
    BasicDBObject query = new BasicDBObject().append("_id", contact.get_id());
    collection.updateOne(query, newDocument);
  }


  // Create collection
  public void createCollection(String collectionName) {
    mongoDatabase.createCollection(collectionName);
  }

  /*
  // Delete collection
  public void deleteCollection(String collectionName) {
    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
    collection.drop();
    collection = mongoDatabase.getCollection(collectionName);
    System.out.println(collection);
  }

  // Lists all mongoDatabase names
  public void listDatabases() {
    for (String mongoDatabase : client.listDatabaseNames()) {
      System.out.println(mongoDatabase);
    }
  }

  // Lists all collection names
  public void listCollectionNames() {
    for (String collectionName : mongoDatabase.listCollectionNames()) {
      System.out.println(collectionName);
    }
  }

  public void insertManyContacts(String collectionName, List<Contact> contact) {
    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
    List<Document> document = asList(
            new Document ("name", contact.getName())
                    .append("surname", contact.getSurname())
                    .append("email", contact.getEmail())
                    .append("phone", contact.getPhone()),

            new Document("name", "Patrik")
                    .append("email","patrik@email.com")
                    .append("customerId", 101));
    collection.insertMany(document);
  }

  public void updateFieldByContactName(String contactName, String fieldName, String fieldValue) {
    BasicDBObject newDocument = new BasicDBObject();
    newDocument.append("$set", new BasicDBObject().append(fieldName, fieldValue));
    BasicDBObject query = new BasicDBObject().append("name", contactName);
    collection.updateOne(query, newDocument);
  }

  public void findAndPrint(String field, String value) {
    MongoCollection<Document> collection = mongoDatabase.getCollection("Contacts");
    List<Document> result = collection.find(new Document(field, value)).into(new ArrayList<>());
    System.out.println(result);
  }


  public void updateIncrement() {
    MongoCollection<Document> collection = mongoDatabase.getCollection("Contacts");
    BasicDBObject updateQuery = new BasicDBObject();
    updateQuery.append("$inc", new BasicDBObject().append("age", 555));
    BasicDBObject searchQuery = new BasicDBObject().append("name", "Max");
    collection.updateOne(searchQuery, updateQuery);
  }
  */
}