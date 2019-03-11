package fileHandeler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.xml.sax.SAXException;
import properties.Constaints;
import testParser.ExcelParseUtility;
import testParser.ParserUtils;
import testParser.TestData;

import javax.xml.parsers.ParserConfigurationException;


public class ReportsRepo {

    //	ArrayList<TestData> listOfExecutedMethod = new ArrayList<TestData>();
    Map<String, ArrayList<TestData>> dayWiseExecution = new HashMap<String, ArrayList<TestData>>();
    ParserUtils parser = new ParserUtils();


    public Map<String, ArrayList<TestData>> getTestResultFromFile() throws ParserConfigurationException, SAXException, IOException {


        // TODO: get all the files in a directory

        File file = new File(Constaints.Repo_Path);
        File[] listOfFiles = file.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File found " + listOfFiles[i].getName());
                //TODO: check if returned null, skip adding it to map.
                dayWiseExecution = parser.parseXmlFileForTestCase(dayWiseExecution, listOfFiles[i]);


            }

        }
        System.out.println("Total column to update are:" + dayWiseExecution.size());


        return dayWiseExecution;
    }
    public Map<Date, ArrayList<TestData>>  arrageDayWise(Map<String, ArrayList<TestData>> dayWiseExecution) throws Exception {
        Map<Date, ArrayList<TestData>> masterList = new HashMap<Date, ArrayList<TestData>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Set<String> keySet = dayWiseExecution.keySet();
        Iterator runner = keySet.iterator();
        String day;
        while (runner.hasNext()) {
            day = runner.next().toString();
            masterList.put(new java.sql.Date(dateFormat.parse(day).getTime()),dayWiseExecution.get(day));
        }


        Map<Date, ArrayList<TestData>> m1 = new TreeMap(masterList);
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
////        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        dayWiseExecution.clear();
//        for (Map.Entry<Date, ArrayList<TestData>> entry : m1.entrySet())
//        {
//            dayWiseExecution.put(df.format(entry.getKey()),entry.getValue());
//        }


        return m1;
    }


    public void writeToExcel(Map<String, ArrayList<TestData>> dayWiseExecution ,Set<Date> dayArrangement) throws Exception {
        boolean issheetEmpty = true;
        if (!new File(Constaints.File_TestData).isFile()) {
            System.out.println("Excel fine doesnot exist, creating new");
            ExcelParseUtility.createFile(Constaints.File_TestData, Constaints.Sheet_Name);
            issheetEmpty = false;
        }
        ExcelParseUtility excelUtil = new ExcelParseUtility(Constaints.File_TestData, Constaints.Sheet_Name);
        System.out.println("In update method");
        //verify is sheet empty
        if (excelUtil.isSheetEmpty()) {
            System.out.println("Sheet found is empty, and so further process can not occur.");
            System.exit(0);
        }
        //Create xls file
        dayWiseExecution = excelUtil.upDateSheet(dayWiseExecution,dayArrangement);
        excelUtil.closeOpenFiles();
        ExcelParseUtility excelUtil2 = new ExcelParseUtility(Constaints.File_TestData, Constaints.Sheet_Name);
        System.out.println("opening sheet");
        excelUtil2.writeScreenshots();
        //Create XML file
        parser.createXml(dayWiseExecution, Constaints.File_Output);
        excelUtil2.closeOpenFiles();


    }
}
