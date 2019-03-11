package testParser;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import properties.Constaints;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;


public class ExcelParseUtility {

    private static XSSFSheet ExcelWSheet;
    private static XSSFSheet ExcelWSheetsummary;
    private static XSSFWorkbook ExcelWBook;

    private static XSSFCell Cell;


    private static XSSFRow Row;
    private static Set<String> keySet;
    static int featureCol;
    static int total_Test_cases_col = 0;
    static int passed_col = 0;
    static int failed_col = 0;
    static int pass_Warn_col = 0;


    // This method is to set the File path and to open the Excel file, Pass Excel
    // Path and Sheetname as Arguments to this method


    public ExcelParseUtility(String path, String SheetName) throws Exception {
        try {

            // Open the Excel file

            FileInputStream ExcelFile = new FileInputStream(path);

            // Access the required test data sheet

            ExcelWBook = new XSSFWorkbook(ExcelFile);
//            ExcelWSheetsummary = ExcelWBook.createSheet(Constaints.Sheet_Summary);
            ExcelWSheet = ExcelWBook.getSheet(SheetName);


        } catch (Exception e) {

            throw (e);

        }
    }

    public static Map<String, ArrayList<TestData>> upDateSheet(Map<String, ArrayList<TestData>> dayWiseExecution, Set<Date> dayArrangement) throws Exception {
        ArrayList<TestData> singleDayTests = new ArrayList<TestData>();
        String day;
        TestData t1 = new TestData();
        int featureCol = getColumnIndex(ExcelWSheet,Constaints.FeatureColumnName);
        int classCol = 0;
        int methodCol = 0;
        classCol = setNewColumn(ExcelWSheet,Constaints.ClassColumnName);
        methodCol = setNewColumn(ExcelWSheet,Constaints.MethodColumnName);
        int methodIndex = 0;
        int statusCol = 0;
        int failReason = 0;
        int failLog = 0;
        int failScript = 0;
        int screenshot = 0;
        System.out.println("Column method name is found on index:" + methodCol);
//        keySet = dayWiseExecution.keySet();
//        Iterator runner = keySet.iterator();
        Iterator runner = dayArrangement.iterator();
        while (runner.hasNext()) {
            day = runner.next().toString();
            singleDayTests = dayWiseExecution.get(day);
            if (getColumnIndex(ExcelWSheet,day) > 0) {
                System.out.println("Column exists" + day);
                statusCol = getColumnIndex(ExcelWSheet,day);
            } else {
                statusCol = setNewColumn(ExcelWSheet,day);
                failReason = setNewColumn(ExcelWSheet,Constaints.Exl_Fail_Reason+":"+day);
                failLog = setNewColumn(ExcelWSheet,Constaints.Exl_Fail_Log+":"+day);
                failScript = setNewColumn(ExcelWSheet,Constaints.Exl_Fail_Script_Log+":"+day);
                screenshot = setNewColumn(ExcelWSheet,Constaints.ScreenShotText+":"+day);
                
            }
            for (int i = 0; i < singleDayTests.size(); i++) {
                methodIndex = getMethodRowIndex(ExcelWSheet, singleDayTests.get(i).getMethodName(), methodCol, singleDayTests.get(i).getClassName(), classCol);
                if (methodIndex > -1) {
                    System.out.println("Method exists,updating on same." + methodIndex);
                    if(getCellData(ExcelWSheet,methodIndex, statusCol).equals(t1.status.PASS_WARN.toString())){
                        continue;
                    }
                    if (!getCellData(ExcelWSheet,methodIndex, statusCol).equals(t1.status.PASS.toString())){
                        setCellData(ExcelWSheet,singleDayTests.get(i).getStatus().toString(), methodIndex, statusCol);
                         setCellData(ExcelWSheet,singleDayTests.get(i).getFailureReason(), methodIndex, failReason);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getFailLog(), methodIndex, failLog);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getScriptLog(), methodIndex, failScript);
                        setCellData(ExcelWSheet,singleDayTests.get(i).getFeature(), methodIndex, featureCol);
                        if(!(singleDayTests.get(i).getScreenshotlink() == null) &&
                                (singleDayTests.get(i).getScreenshotlink().contains("\\screenshots\\"))){
                        setCellData(ExcelWSheet, singleDayTests.get(i).getScreenshotlink() + "png", methodIndex, screenshot);
                        }else{
                        setCellData(ExcelWSheet,singleDayTests.get(i).getScreenshotlink(), methodIndex, screenshot);
                        }
                        setCellData(ExcelWSheet,singleDayTests.get(i).getClassName(), methodIndex, classCol);
                        if(singleDayTests.get(i).getStatus().toString().equals(t1.status.PASS_WARN.toString()))
                            setCellData(ExcelWSheet,singleDayTests.get(i).getWaringText(), methodIndex, failReason);
                    }
                } else {
                    //TODO: find packang and classname first.
                    System.out.println("new Method,updating");
                    methodIndex = ExcelWSheet.getLastRowNum() + 1;
                    ExcelWSheet.createRow(methodIndex);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getFeature(), methodIndex, featureCol);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getClassName(), methodIndex, classCol);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getMethodName(), methodIndex, methodCol);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getStatus().toString(), methodIndex, statusCol);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getFailureReason(), methodIndex, failReason);
                    setCellData(ExcelWSheet,singleDayTests.get(i).getFailLog(), methodIndex, failLog);
                    if(!(singleDayTests.get(i).getScreenshotlink() == null) &&
                            (singleDayTests.get(i).getScreenshotlink().contains("\\screenshots\\"))){
                        setCellData(ExcelWSheet,singleDayTests.get(i).getScreenshotlink()+"png", methodIndex, screenshot);
                    }else{
                        setCellData(ExcelWSheet,singleDayTests.get(i).getScreenshotlink(), methodIndex, screenshot);
                    }
                    setCellData(ExcelWSheet,singleDayTests.get(i).getScriptLog(), methodIndex, failScript);
                    if(singleDayTests.get(i).getStatus().toString().equals(t1.status.PASS_WARN.toString()))
                        setCellData(ExcelWSheet,singleDayTests.get(i).getWaringText(), methodIndex, failReason);

                }
            }

        }

        System.out.println("Sheet update complete:");
        colourStatus(ExcelWSheet);

        //Creating manager page
//        setSummaryPage(dayWiseExecution);

//TODO: arange column data too.


        return dayWiseExecution;
    }

    public static void writeScreenshots() {
        String pathToFile = "";
        CellStyle hlink_style = ExcelWBook.createCellStyle();
        Font hlink_font = ExcelWBook.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);
        FileOutputStream out = null;
        try {
            System.out.println("Writing screenshot");
            out = new  FileOutputStream(Constaints.File_TestData);
            System.out.println("File found");

            XSSFCreationHelper helper = ExcelWBook.getCreationHelper();



            for (int i = 0; i <= ExcelWSheet.getLastRowNum(); i++) {
                for (int j = 0; j < ExcelWSheet.getRow(0).getPhysicalNumberOfCells(); j++) {
                    Cell = ExcelWSheet.getRow(i).getCell(j, Row.CREATE_NULL_AS_BLANK);
                    if (Cell.toString().contains("\\screenshots\\")) {

                    pathToFile = Cell.toString().replace("\\","//");
                        if(pathToFile.contains("running")){
                        System.out.println("File Path"+pathToFile);
                        }
                        XSSFHyperlink file_link = helper.createHyperlink(Hyperlink.LINK_FILE);
                        File pngFile = new File(Constaints.ScreenPath_Path+pathToFile);
                        file_link.setAddress(pngFile.toURI().toString());
//                        file_link.setAddress("file:///D://histo.txt");
                        file_link.setTooltip("Click to open the file");
//                    file_link.setAddress("file:///"+Constaints.ScreenPath_Path+pathToFile);
                        Cell.setCellValue("Screenshot");
                        Cell.setHyperlink(file_link);

                    Cell.setCellStyle(hlink_style);
                        System.out.println("Success");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getLastDate(Map<String, ArrayList<TestData>> dayWiseExecution){
        TestData t1 = new TestData();
        //set Last run:
        keySet = dayWiseExecution.keySet();
        String keyDate = "1999-01-18";
        String lastDay = "";
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date lastDate = null;
        try {
            lastDate = myFormat.parse(keyDate);

        Iterator runner = keySet.iterator();//2018-01-18
        while (runner.hasNext()) {
            keyDate = runner.next().toString();

            if(myFormat.parse(keyDate).after(lastDate)){
                lastDay = keyDate;
            }
        }


        } catch (ParseException e) {
            e.printStackTrace();
        }
     return lastDay;


    }
    public static void createSheet2Colums(){
        try {

            ExcelWBook.setActiveSheet(ExcelWBook.getSheetIndex(Constaints.Sheet_Summary));
            ExcelWSheetsummary =  ExcelWBook.getSheet(Constaints.Sheet_Summary);
            Row = ExcelWSheetsummary.createRow(0);

            //Create a new cell in current row
            XSSFCell cell = Row.createCell(0);
//Set value to new value
            cell.setCellValue(Constaints.ReportHeading);
//        featureCol = setNewColumn(ExcelWSheetsummary,"Feature");
//            total_Test_cases_col = setNewColumn(ExcelWSheetsummary,"Total Test Case");
//            failed_col = setNewColumn(ExcelWSheetsummary,"Failed");
//            passed_col = setNewColumn(ExcelWSheetsummary,"Passed");
//            pass_Warn_col = setNewColumn(ExcelWSheetsummary,"Pass with Warn");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSummaryPage(Map<String, ArrayList<TestData>> dayWiseExecution) throws Exception {
        createSheet2Colums();
//      ArrayList<TestData> dayResult;
//        TestData t1 = new TestData();
//        Set<String> features = new HashSet<String>();
//        String currfeature;
//
//        int featureRow = 0;
//        int pass = 0;
//        int passWarn =0;
//        int fail = 0;
//        String lastDay = getLastDate(dayWiseExecution);
//        System.out.println("Last report is from :"+lastDay);
//        dayResult = dayWiseExecution.get(lastDay);
//
//        for(int i = 0;i<dayResult.size();i++){
//
//            features.add(dayResult.get(i).getFeature());
//
//        }
//
//        Iterator runner = features.iterator();//2018-01-18
//        while (runner.hasNext()) {
//            currfeature = runner.next().toString();
//        for(int i = 0;i<dayResult.size();i++){
//            if(!(dayResult.get(i).getFeature() == currfeature)){
//                continue;
//            }
//                if(dayResult.get(i).getStatus().toString().equals(t1.status.PASS.toString())){
//                    pass++;
//                }
//                if(dayResult.get(i).getStatus().toString().equals(t1.status.PASS_WARN.toString())){
//                    passWarn++;
//                }
//                if(dayResult.get(i).getStatus().toString().equals(t1.status.FAIL.toString())){
//                    fail++;
//                }
//            }
//
////            featureRow = getRowIndex(ExcelWSheetsummary,currfeature, featureCol);
//            featureRow = ExcelWSheetsummary.getLastRowNum() + 1;
////TODO: add if to add on existing.
//            setCellData(ExcelWSheetsummary,currfeature, featureRow, featureCol);
//            setCellData(ExcelWSheetsummary,Integer.toString(pass), featureRow, passed_col);
//            setCellData(ExcelWSheetsummary,Integer.toString(passWarn), featureRow, pass_Warn_col);
//            setCellData(ExcelWSheetsummary,Integer.toString(fail), featureRow, failed_col);
//            setCellData(ExcelWSheetsummary,Integer.toString((fail+pass+passWarn)), featureRow, total_Test_cases_col);
//            // write to xls

//        }
        }



    // This method is to read the test data from the Excel cell, in this we are
    // passing parameters as Row num and Col num

    public static String getCellData(XSSFSheet sheet,int RowNum, int ColNum) throws Exception {

        try {

            Cell = sheet.getRow(RowNum).getCell(ColNum);

            String CellData = Cell.getStringCellValue();

            return CellData;

        } catch (Exception e) {

            return "";

        }

    }

    // This method creates last column

    public static int setNewColumn(XSSFSheet sheet,String columnName) throws Exception {


        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
//        Date date = new Date();
        setCellData(sheet,columnName, 0, numberOfColumn);


        return numberOfColumn;

    }

    // This method gets column index of header

    public static int getColumnIndex(XSSFSheet sheet,String columnName) throws Exception {

        try {

            int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();

            for (int i = 0; i <= numberOfColumn; i++) {
                String df = sheet.getRow(0).getCell(i).getStringCellValue().toString();
                if (sheet.getRow(0).getCell(i).getStringCellValue().toString().equals(columnName)) {
                    return i;
                }
            }

        } catch (Exception e) {

            return -1;

        }
        return -1;

    }

    // This method gets row index of value present

    public static int getRowIndex(XSSFSheet sheet,String rowString, int columnIndex) throws Exception {

        try {

            int numberOfRows = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i <= numberOfRows; i++) {
                if (sheet.getRow(i).getCell(columnIndex).getStringCellValue().toString().equals(rowString)) {
                    return i;
                }
            }

        } catch (Exception e) {

            return -1;

        }
        return -1;

    }

    // This method gets row index of value present

    public static int getMethodRowIndex(XSSFSheet sheet,String rowString, int columnIndex,String className,int classnamecol) throws Exception {

        try {

            int numberOfRows = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i <= numberOfRows; i++) {
                if (sheet.getRow(i).getCell(columnIndex).getStringCellValue().toString().equals(rowString) &&
                        sheet.getRow(i).getCell(classnamecol).getStringCellValue().toString().equals(className)){
                    return i;
                }
            }

        } catch (Exception e) {

            return -1;

        }
        return -1;

    }


    // This method gets column count

    public static int getColumnCount(XSSFSheet sheet) throws Exception {

        return sheet.getRow(0).getPhysicalNumberOfCells();

    }

    // This method gets row count

    public static int getRowCount(XSSFSheet sheet) throws Exception {

        return sheet.getPhysicalNumberOfRows();

    }

    // This method is to write in the Excel cell, Row num and Col num are the
    // parameters

    public static void setCellData(XSSFSheet sheet,String Result, int RowNum, int ColNum) throws Exception {

        try {


            Row = sheet.getRow(RowNum);

            Cell = Row.getCell(ColNum, Row.CREATE_NULL_AS_BLANK);

            if (Cell == null) {

                Cell = Row.createCell(ColNum);

                Cell.setCellValue(Result);

            } else {

                Cell.setCellValue(Result);

            }

        } catch (Exception e) {


            throw (e);

        }

    }
    // This method is to close the files

    public static void closeOpenFiles() throws Exception {

        try {

            // Constant variables Test Data path and Test Data file name

            FileOutputStream fileOut = new FileOutputStream(Constaints.File_TestData);
            ExcelWBook.write(fileOut);
            fileOut.flush();

            fileOut.close();

        } catch (Exception e) {

            throw (e);

        }

    }


    public static boolean isSheetEmpty() throws Exception {
//TODO:unimportant

        return ExcelWSheet.getRow(0).getPhysicalNumberOfCells() == 0 ? true : false;

    }


    public static void colourStatus(XSSFSheet sheet) throws Exception {
        TestData t1 = new TestData();
        CellStyle stylePass = ExcelWBook.createCellStyle();
        CellStyle styleFail = ExcelWBook.createCellStyle();
        CellStyle stylePassWarn = ExcelWBook.createCellStyle();
        stylePass.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        stylePass.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleFail.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleFail.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stylePassWarn.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        stylePassWarn.setFillPattern(CellStyle.SOLID_FOREGROUND);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            for (int j = 0; j < sheet.getRow(0).getPhysicalNumberOfCells(); j++) {
                Cell = sheet.getRow(i).getCell(j, Row.CREATE_NULL_AS_BLANK);
                if (Cell.toString().equals(t1.status.PASS.toString())) {
                    System.out.println("Excel written successfully.." + sheet.getRow(i).getCell(j).toString());
                    Cell.setCellStyle(stylePass);
                }
                if (Cell.toString().equals(t1.status.FAIL.toString())) {
                    System.out.println("Excel written successfully.." + sheet.getRow(i).getCell(j).toString());
                    Cell.setCellStyle(styleFail);

                }
                if (Cell.toString().equals(t1.status.PASS_WARN.toString())) {
                    System.out.println("Excel written successfully.." + sheet.getRow(i).getCell(j).toString());
                    Cell.setCellStyle(stylePassWarn);

                }
            }
        }


    }




    public static void createFile(String path, String SheetName) throws Exception {

//        XSSFSheet sheet = workbook.createSheet(SheetName);
        ExcelWBook = new XSSFWorkbook();
        ExcelWSheet = ExcelWBook.createSheet(SheetName);

        //Create a new row in current sheet
        Row = ExcelWSheet.createRow(0);

        //Create a new cell in current row
        XSSFCell cell = Row.createCell(0);
//Set value to new value
        cell.setCellValue(Constaints.FeatureColumnName);
        try {
            FileOutputStream out =
                    new FileOutputStream(new File(path));
            ExcelWBook.write(out);
            out.close();
            System.out.println("Excel written successfully..");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
