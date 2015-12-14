import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Tin Huynh on 12/8/2015.
 */

public class ParseJson {

    public Map<String, Integer> jsonanalysis(String inputFile, String sandboxId, String processId) {
        Map<String, Integer> volume = new HashMap<String, Integer>();
        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = null;
            jp = f.createJsonParser(new File(inputFile + sandboxId + "_" + processId + ".json"));

            JsonToken current;
            current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                System.out.println("Error: root should be object: quiting.");
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                // move from field name to field value
                current = jp.nextToken();
                if (fieldName.equals("searchResults")) {
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        jp.nextToken();
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            // read the record into a tree model,
                            // this moves the parsing position to the end of it
                            JsonNode node = jp.readValueAsTree();
                            // And now we have random access to everything in the object
                            String key = node.get("pubDate").getTextValue().substring(0, 10);
                            Integer value = volume.get(key);
                            //set value is null (it's mean it does not existed in map) then return 0
                            //else value is not null then return val
                            value = (value == null) ? 0 : value;
                            volume.put(key, value + 1);
                        }
                    } else {
                        System.out.println("Error: records should be an array: skipping.");
                        jp.skipChildren();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return volume;
    }

    public void writeToExcel(String outputFile, String sandboxId, String processId){
        Map<String, Integer> arr = jsonanalysis(outputFile, sandboxId, processId);
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outputFile + sandboxId + "_" + processId + ".xls", true));

            for(Map.Entry entry: arr.entrySet()){
                buffer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
                buffer.flush();
            }
            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("Write to excel file has done!");
    }
}
