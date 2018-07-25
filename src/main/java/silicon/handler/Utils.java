package silicon.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {
    public static String dateToISO8601(Date date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String cleanXSS(String value) {
        if(value == null) return null;
        if(value.equals("")) return value;
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("(?i)<script.*?>.*?<script.*?>", "");
        value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
        value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
        value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");
        return value;
    }

    public static String cleanCSVFunctions(String value) {
        if(value == null) return null;
        if(value.equals("")) return value;
        String first = value.substring(0, 1);
        if(first.equals("=") || first.equals("+") || first.equals("-") || first.equals("@"))
            value = value.substring(1);

        return value;
    }

    public static JsonNode getBody(HttpServletRequest request){
        try{
            BufferedReader reader = request.getReader();
            String body = reader.readLine();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(body);
        }catch (Exception e){
            System.out.println(e.toString());
        }

        return null;

    }


    public static String validSringParam(String param){
        if(param == null) return null;
        if(param.equals("null")) return null;
        if(param.equals("undefined")) return null;
        param = Utils.cleanXSS(param);
        param = Utils.cleanCSVFunctions(param);
        return param;
    }

    public static List<String> validateRequiredParams(String[] paramsName, Object[] paramsValue){
        List<String> invalidParams = new ArrayList<String>();
        int i = 0;
        for(Object param : paramsValue){
            if(param == null){
                invalidParams.add(paramsName[i]);
            }
            i++;
        }

        if(invalidParams.size() == 0){
            return null;
        }

        return invalidParams;
    }

    public static Date addTime(Date date, int field, int amount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, amount);
        return c.getTime();
    }

    public static Double round8Decimals(Double value){
        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.CEILING);
        String amountTokenString = df.format(value).replace(",", ".");
        return Double.parseDouble(amountTokenString);
    }
}
