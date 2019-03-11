package reportsMain;

import fileHandeler.ReportsRepo;
import org.xml.sax.SAXException;
import properties.Constaints;
import testParser.ExcelParseUtility;
import testParser.TestData;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* Created by kbhakat on 1/18/18.
*/
public class ReportStarter {

    public static void main(String[] args) throws Exception {
        Map<String, ArrayList<TestData>> dayWiseMap = new HashMap<String, ArrayList<TestData>>();
        Map<Date, ArrayList<TestData>> masterList = new HashMap<Date, ArrayList<TestData>>();
        ReportsRepo repo = new ReportsRepo();




        dayWiseMap = repo.getTestResultFromFile();
        masterList =  repo.arrageDayWise(dayWiseMap);
        repo.writeToExcel(dayWiseMap,masterList.keySet());

//        excelUtil.setExcelFile(Constaints.File_TestData, Constaints.Sheet_Name);

//        excelTask.update(listOfExecutedMethod);


    }

    }
