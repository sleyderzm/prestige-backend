package silicon.handler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import silicon.model.CheckProcessInfo;
import silicon.model.CheckStatusResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GreenMoney {

    public static CheckStatusResult getCheckIdStatus(String checkId){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost p = new HttpPost(System.getenv("GREEN_MONEY_API_URL") + "eCheck.asmx/CheckStatus");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Check_ID", checkId));
            params.add(new BasicNameValuePair("x_delim_data", ""));
            params.add(new BasicNameValuePair("x_delim_char", ""));
            params.add(new BasicNameValuePair("Client_ID", System.getenv("GREEN_MONEY_API_CLIENT_ID")));
            params.add(new BasicNameValuePair("ApiPassword", System.getenv("GREEN_MONEY_API_PASSWORD")));
            p.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            p.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse resp = client.execute(p);
            String xml_string = EntityUtils.toString(resp.getEntity());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml_string)));

            CheckStatusResult checkStatusResult = new CheckStatusResult();

            Element xmlCheckStatusResult =  (Element) doc.getElementsByTagName("CheckStatusResult").item(0);

            String[] attributes = {"result", "resultDescription", "verifyResult", "verifyResultDescription", "verifyOverriden", "deleted", "deletedDate", "processed", "processedDate", "rejected", "rejectedDate", "echeckResponse"};

            for(int i = 0; i < attributes.length ; i++ ){
                String attribute = attributes[i];
                Node xmlNode =  xmlCheckStatusResult.getElementsByTagName(Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1)).item(0);
                if (xmlNode != null){
                    checkStatusResult.setAttribute(attribute, xmlNode.getTextContent());
                }
            }

            return checkStatusResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CheckProcessInfo getCheckIdProcessInfo(String checkId){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost p = new HttpPost(System.getenv("GREEN_MONEY_API_URL") + "eCheck.asmx/ProcessInfo");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Process_ID", checkId));
            params.add(new BasicNameValuePair("x_delim_data", ""));
            params.add(new BasicNameValuePair("x_delim_char", ""));
            params.add(new BasicNameValuePair("Client_ID", System.getenv("GREEN_MONEY_API_CLIENT_ID")));
            params.add(new BasicNameValuePair("ApiPassword", System.getenv("GREEN_MONEY_API_PASSWORD")));
            p.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            p.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse resp = client.execute(p);
            String xml_string = EntityUtils.toString(resp.getEntity());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml_string)));

            CheckProcessInfo checkProcessInfo = new CheckProcessInfo();

            Element xmlProcessInfoResult =  (Element) doc.getElementsByTagName("ProcessInfoResult").item(0);

            String[] attributes = {"result", "resultDescription", "processType", "process_ID", "nameFirst", "nameLast", "companyName", "emailAddress", "phone", "phoneExtension", "address1", "address2", "city", "state", "zip", "country", "checkMemo", "checkAmount", "checkDate", "checkNumber", "recurringType", "recurringOffset", "recurringPayments"};

            for(int i = 0; i < attributes.length ; i++ ){
                String attribute = attributes[i];
                Node xmlNode =  xmlProcessInfoResult.getElementsByTagName(Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1)).item(0);
                if (xmlNode != null){
                    checkProcessInfo.setAttribute(attribute, xmlNode.getTextContent());
                }
            }

            return checkProcessInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
