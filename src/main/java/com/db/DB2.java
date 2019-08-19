package com.db;

import com.constants.Constants;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.DB;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

public class DB2 {

  public static void uploadFile() {
    String connectionString = "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/test?retryWrites=true&w=majority";
    String dbName = "hilodb";
    String collectionName = "Contacts";
    String filePath = Constants.imgPath + "car3.png";

    MongoClient client = new MongoClient(new MongoClientURI(connectionString));
    DB db =  client.getDB(dbName);
    DBCollection collection = db.getCollection(collectionName);

    String newFileName = "newImage";
    File imageFile = new File(filePath);
    GridFS gfsPhoto = new GridFS(db, "images000");
    GridFSInputFile gfsFile = null;
    try {
      gfsFile = gfsPhoto.createFile(imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    gfsFile.setFilename(newFileName);
    gfsFile.save();


  }



  public static void loadFile() {
    String connectionString = "mongodb+srv://maks:777@hilodb-ejqv2.mongodb.net/test?retryWrites=true&w=majority";
    String dbName = "hilodb";
    String collectionName = "Contacts";
//    String filePath = Constants.imgPath + "car3.png";

    MongoClient client = new MongoClient(new MongoClientURI(connectionString));
    DB db =  client.getDB(dbName);
    DBCollection collection = db.getCollection(collectionName);

    String newFileName = "newImage";
    GridFS gfsPhoto = new GridFS(db, "images000");
    GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);
    try {
      imageForOutput.writeTo(Constants.imgPath+"downloaded_001.png");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(imageForOutput);



  }


}
