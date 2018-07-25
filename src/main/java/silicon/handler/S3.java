package silicon.handler;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.util.UUID;

public class S3 {

    public static String getPublicURL(String awsAccessKeyId){
        return "https://s3.amazonaws.com/"+System.getenv("AWS_PUBLIC_BUCKET")+"/" + awsAccessKeyId;
    }

    public static URL getSignedURL(String awsAccessKeyId){
        URL url;
        try{
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000 * 60 * 60; // 1 hour.
            expiration.setTime(msec);
            GeneratePresignedUrlRequest generatePresignedUrlRequest;
            generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(System.getenv("AWS_PRIVATE_BUCKET"), awsAccessKeyId);

            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
            generatePresignedUrlRequest.setExpiration(expiration);
            url = s3.generatePresignedUrl(generatePresignedUrlRequest);
            return url;
        }catch (Exception e){
        }
        return null;
    }

    public static URL putSignedURL(String contentType){
        URL url;
        try{
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000 * 60 * 60; // 1 hour.
            expiration.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest;
            generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(System.getenv("AWS_PRIVATE_BUCKET"), UUID.randomUUID().toString());

            generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
            generatePresignedUrlRequest.setExpiration(expiration);
            generatePresignedUrlRequest.setContentType(contentType);

            url = s3.generatePresignedUrl(generatePresignedUrlRequest);
            return url;
        }catch (Exception e){
            return null;
        }
    }



}
