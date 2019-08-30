package com.pages.Page7;

// // This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class API
{
  public static void main(String[] args)
  {
    HttpClient httpclient = HttpClients.createDefault();

    try
    {
      URIBuilder builder = new URIBuilder("https://api.trafikinfo.trafikverket.se/v2/data.xml");


      String requestBody = "<REQUEST>" +
              // Use your valid authenticationkey
              "<LOGIN authenticationkey='yourAuthenticationKey'/>" +
              "<QUERY objecttype='TrainMessage' schemaversion='1.3'>" +
              "<FILTER>" +
              "<IN name='AffectedLocation' value='Blg'/>" +
              "</FILTER>" +
              "<EXCLUDE>Deleted</EXCLUDE>" +
              "</QUERY>" +
              "</REQUEST>";

//      webclient.UploadStringAsync(address, "POST", requestBody);


//      URIBuilder builder = new URIBuilder("https://tsopendata.azure-api.net/HistoriskFordon/v0.1/");

//      builder.setParameter("$expand", "{string}");
//      builder.setParameter("$filter", "Artal eq '1995'");
//      builder.setParameter("$select", "{string}");
//      builder.setParameter("$orderby", "{string}");
//      builder.setParameter("$top", "{number}");
//      builder.setParameter("$skip", "{number}");
//      builder.setParameter("$count", "true");
//      builder.setParameter("$count", "{boolean}");

      URI uri = builder.build();
      HttpGet request = new HttpGet(uri);
      System.out.println("** "+ uri);
//      request.setHeader("Ocp-Apim-Subscription-Key", "****************");

      // Request body
      StringEntity reqEntity = new StringEntity("{body}");
//      request.setEntity(reqEntity);

      request.setHeader("Content-Type", "text/xml");



      HttpResponse response = httpclient.execute(request);
      HttpEntity entity = response.getEntity();

      if (entity != null)
      {
        System.out.println(EntityUtils.toString(entity));
      }
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
}
