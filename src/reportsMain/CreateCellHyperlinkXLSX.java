package reportsMain;

import java.io.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
public class CreateCellHyperlinkXLSX {
    public static void main(String[] args) throws Exception{
                /* Create Workbook and Worksheet XLSX Format */
        XSSFWorkbook my_workbook = new XSSFWorkbook();
        XSSFSheet my_sheet = my_workbook.createSheet("Cell Hyperlink");

                /* Let us create some XSSFHyperlink objects */
                /* First get XSSFCreationHelper object using the workbook*/
        XSSFCreationHelper helper= my_workbook.getCreationHelper();
                /* Now use createHyperlink method to get XSSFHyperlink */
        XSSFHyperlink file_link=helper.createHyperlink(Hyperlink.LINK_FILE);

                /* Define the data for these hyperlinks */
                /* Define tooltip for the hyperlinks */
        file_link.setAddress("file:///D://histo.txt");
        file_link.setTooltip("Click to open the file");

        Row row = my_sheet.createRow(1);
        Cell cell = row.createCell(1);
        cell.setCellValue("Click to Open the file");
        cell.setHyperlink(file_link);

                /* Write changes to the workbook */
        FileOutputStream out = new FileOutputStream(("D:\\cell.xlsx"));
        my_workbook.write(out);
        out.close();
    }
}