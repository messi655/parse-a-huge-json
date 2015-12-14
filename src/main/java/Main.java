/**
 * Created by Tin Huynh on 12/7/2015.
 */
public class Main {

    public static void main(String args[]){
        ParseJson parseJson = new ParseJson();
        parseJson.writeToExcel(args[0], args[1], args[2]);
    }
}
