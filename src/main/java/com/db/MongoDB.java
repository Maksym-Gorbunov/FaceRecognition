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


// This class handel all data on MongoDB, cloud
public class MongoDB {
  public static MongoClient client;
  public static MongoDatabase mongoDatabase;
  public static DB db;
  public static String databaseName;
  public static String collectionName;
  public static MongoCollection<Document> collection;
  public static String atlasConnectionString;

  // Constructor
  static {
    System.out.println("Connecting to MongoDB on cloud..");
    databaseName = "hilodb";
    collectionName = "Contacts";
    atlasConnectionString = "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/recognize?retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(atlasConnectionString);
    client = new MongoClient(uri);
    Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
    mongoLogger.setLevel(Level.SEVERE);
    mongoDatabase = client.getDatabase(databaseName);
    collection = mongoDatabase.getCollection(collectionName);
    db = client.getDB(databaseName);
  }

  // Upload file to db
  public static void uploadFile(File file) {
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
  public static void downloadFile(String fileName) {
    GridFS gfsPhoto = new GridFS(db, "images");
    String fName = FilenameUtils.removeExtension(fileName);
    GridFSDBFile imageForOutput = gfsPhoto.findOne(fName);
    try {
      imageForOutput.writeTo(Constants.imgPath+"downloads\\"+fileName);
      System.out.println("**** new file was created");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(imageForOutput);
  }

  // Add new contact
  public static void insertContact(Contact contact) {
    Document document = new Document("_id", contact.get_id())
            .append("name", contact.getName())
            .append("surname", contact.getSurname())
            .append("email", contact.getEmail())
            .append("phone", contact.getPhone());
    collection.insertOne(document);
  }

  // Get all documents
  public static List<Document> getAllDocuments() {
    List<Document> result = collection.find().into(new ArrayList<>());
    return result;
  }

  // Delete all contacts
  public static void deleteContact(Contact contact) {
    Bson filter = new Document("_id", contact.get_id());
    collection.deleteOne(filter);
  }

  // Update contacts
  public static void updateContact(Contact contact) {
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
  public static void createCollection(String collectionName) {
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