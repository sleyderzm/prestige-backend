package silicon.handler;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import silicon.model.Subscriber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class IdentityMind {
    public static void mitekValidation(Subscriber subscriber){

        try{
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = uriForMitekValidation(subscriber);
            if(builder == null) return;

            HttpGet g = new HttpGet(builder.build());
            g.setHeader("Content-type", "application/json");
            g.setHeader("Authorization", "Basic " + encodingBasicAuthentication());
            HttpResponse resp = client.execute(g);
            String json_string = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(json_string);
            System.out.println(actualObj.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static String encodingBasicAuthentication(){
        String basicAuthData = System.getenv("IDENTITY_MIND_API_USER") + ":" + System.getenv("IDENTITY_MIND_API_TOKEN");
        return  Base64.getEncoder().encodeToString((basicAuthData).getBytes());
    }

    public static JsonNode createSubscriberAsConsumer(Subscriber subscriber){

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost p = new HttpPost(System.getenv("IDENTITY_MIND_URL"));
        try {
            JSONObject params = paramsFromSubscriber(subscriber);
            p.setEntity(new StringEntity(params.toString(), "UTF8"));
            p.setHeader("Content-type", "application/json");
            p.setHeader("Authorization", "Basic " + encodingBasicAuthentication());
            HttpResponse resp = client.execute(p);
            String json_string = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(json_string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getS3FileAs64Encoded(Subscriber subscriber) {
        String awsAccessKeyId = subscriber.getAwsAccessKeyId();
        try {
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            S3Object obj = s3.getObject(
                    new GetObjectRequest(System.getenv("AWS_PRIVATE_BUCKET"), awsAccessKeyId));
            BufferedImage imgBuf = ImageIO.read(obj.getObjectContent());
            ObjectMetadata objectMetadata = obj.getObjectMetadata();
            String mimeType = objectMetadata.getContentType();
            String extension;

            if(subscriber.getExtensionFile() != null){
                extension = subscriber.getExtensionFile();
            }else{
                MimeType type = MimeTypes.getDefaultMimeTypes().forName(mimeType);
                extension = type.getExtension();
                extension = extension.substring(1);
            }

            String base64 = mimeType + ";base64," + encodeBase64URL(imgBuf, extension);
            return base64;
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private static String encodeBase64URL(BufferedImage imgBuf, String extension) {
        String base64;
        try{
            Base64.Encoder encoder = Base64.getEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(imgBuf, extension, out);

            byte[] bytes = out.toByteArray();
            base64 = new String(encoder.encode(bytes), "UTF-8");

            return base64;
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private static JSONObject paramsFromSubscriber(Subscriber subscriber){

        JSONObject params = new JSONObject();
        if(subscriber.getEmail() != null){
            params.put("man", subscriber.getEmail());
            params.put("tea", subscriber.getEmail());
        }

        if(subscriber.getBirthdate() != null) params.put("dob", Utils.dateToISO8601(subscriber.getBirthdate()));
        params.put("bfn", subscriber.getFirstName());
        params.put("bln", subscriber.getLastName());
        params.put("sfn", subscriber.getFirstName());
        params.put("sln", subscriber.getLastName());
        params.put("afn", subscriber.getFirstName());
        params.put("aln", subscriber.getLastName());

        if(subscriber.getFingerprint() != null){
            params.put("dfp", subscriber.getFingerprint());
            params.put("dft", "AU"); //augur
        }

        if(subscriber.getIp() != null){
            params.put("ip", subscriber.getIp());
        }

        if(subscriber.getState() != null && !subscriber.getState().isEmpty()){
            params.put("bs", subscriber.getState());
            params.put("ss", subscriber.getState());
            params.put("docState", subscriber.getState());
        }

        if(subscriber.getBillingAddress() != null && !subscriber.getBillingAddress().isEmpty()){
            params.put("bsn", subscriber.getBillingAddress());
            params.put("ssn", subscriber.getBillingAddress());
        }

        if(subscriber.getState() != null && !subscriber.getState().isEmpty()){
            params.put("bs", subscriber.getState());
            params.put("ss", subscriber.getState());
            params.put("docState", subscriber.getState());
        }

        if(subscriber.getCountry() != null){
            params.put("bco", subscriber.getCountry());
            params.put("sco", subscriber.getCountry());
            params.put("docCountry", subscriber.getCountry());
        }

        if(subscriber.getDocumentType() != null) params.put("docType", subscriber.getDocumentType());

        if(subscriber.getAwsAccessKeyId() != null){
            String scanData = getS3FileAs64Encoded(subscriber);
            if(scanData != null) params.put("scanData", scanData);
        }

        if(subscriber.getContribution() >= Subscriber.BIG_CONTRIBUTION){
            params.put("profile", ">=25k");
        }else{
            params.put("profile", "<25k");
        }

        return params;

    }

    private static URIBuilder uriForMitekValidation(Subscriber subscriber){
        try {
            URIBuilder builder = new URIBuilder(
                    System.getenv("IDENTITY_MIND_URL") + "/" + subscriber.getTransactionId() + "/addDocument"
            );
            if(subscriber.getBirthdate() != null) builder.setParameter("dob", Utils.dateToISO8601(subscriber.getBirthdate()));
            if(subscriber.getDocumentType() != null) builder.setParameter("docType", subscriber.getDocumentType());
            if(subscriber.getCountry() != null){
                builder.setParameter("docCountry", subscriber.getCountry()); // ISO 3166
                builder.setParameter("aco", subscriber.getCountry()); // ISO 3166
            }
            if(subscriber.getFirstName() != null){
                builder.setParameter("afn", subscriber.getFirstName());
            }
            if(subscriber.getLastName() != null){
                builder.setParameter("aln", subscriber.getLastName());
            }
            if(subscriber.getState() != null && !subscriber.getState().isEmpty()){
                builder.setParameter("docState", subscriber.getState());
                builder.setParameter("as", subscriber.getState());
            }

            if(subscriber.getAwsAccessKeyId() != null){
                String scanData = getS3FileAs64Encoded(subscriber);
                if(scanData != null) builder.setParameter("scanData", scanData);
            }

            return builder;
        }catch (Exception e){
            System.out.println(e.toString());
        }

        return null;



    }
}