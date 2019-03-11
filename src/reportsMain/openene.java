//package reportsMain;
//
//import fileHandeler.ReportsRepo;
//import org.xml.sax.SAXException;
//import properties.Constaints;
//import testParser.ExcelParseUtility;
//import testParser.TestData;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created by kbhakat on 1/18/18.
// */
//public class openene {
//
//    public static void main(String[] args) throws Exception {
//        Map<String, ArrayList<TestData>> dayWiseMap = new HashMap<String, ArrayList<TestData>>();
//        List<String> tempfruit =  new LinkedList<String>();
//        tempfruit.add("Apple");
//        tempfruit.add("Apple");
//        tempfruit.add("Apple");
//        tempfruit.add("Apple");
//        tempfruit.add("ban");
//        tempfruit.add("ban");
//
//
//
//        String current = "";
//        HashSet<String> setmapper = new HashSet<String>(tempfruit);
//
//        Iterator itt = setmapper.iterator();
//        while(itt.hasNext()){
//            current = itt.next().toString();
//            System.out.println(" out::"+current+"::"+Integer.toString(Collections.frequency(tempfruit,current)));
//
//
//            }
//        }
//}
