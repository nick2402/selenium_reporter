package testParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import properties.Constaints;

public class ParserUtils {


    public Map<String, ArrayList<TestData>> parseXmlFileForTestCase(Map<String, ArrayList<TestData>> executionMap, File file) throws SAXException, IOException, ParserConfigurationException {
        //Get all test cases in each file.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setValidating(true);//TODO: if proper xml is not found will give error.
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        ArrayList<TestData> testcaseList = new ArrayList<TestData>();
        TestData t1;
        int splitIndex;
        String[] className;
        // loop through test suites
        NodeList testsuiteList = doc.getElementsByTagName(Constaints.parentNodeClass);
        String sout = "";
        for (int index2 = 0; index2 < testsuiteList.getLength(); index2++) {
            sout = "";
            NodeList testList = testsuiteList.item(index2).getChildNodes();
List<String> ErrorProneList = new ArrayList<String>();
            ErrorProneList.add("US_25782_ChangesForStorageSystemsPanel_DashboardTest");
            ErrorProneList.add("US_24492_VerifyActivityPanelFunctionality_DashboardTest");
            ErrorProneList.add("US_23293_ChangesForDeviceTypeAndActivityTypeSelection_DashboardTest");
            ErrorProneList.add("US_23293_ChangesForActivityPanel_DashboardTest");
            ErrorProneList.add("US_19851_DashboardSingleSystemView_DashboardTest");
            ErrorProneList.add("US24500_CustomizedAndAllocatedCapacityPanels_DashboardTest");
            ErrorProneList.add("US19988_VerifyLoginBannerFunctionality_DashboardTest");




            if(!(sout.length()>0)){
            for (int index = 0; index < testList.getLength(); index++) {

                if (testList.item(index).getNodeName().equals("system-out")) {
                    sout = testList.item(index).getChildNodes().item(0).getNodeValue();
                }
            }
            }
            // loop through test cases
            for (int index = 0; index < testList.getLength(); index++) {

                if (!(testList.item(index).getNodeName().equals(Constaints.childNodeMethod))) {
                    continue;
                }
                Node currentItem = testList.item(index);
                t1 = new TestData();
                t1.setMethodName(currentItem.getAttributes().getNamedItem("name").getNodeValue().split("\\[")[0]);
                //

                System.out.println("Test method :"+t1.getMethodName());
                className = currentItem.getAttributes().getNamedItem("classname").getNodeValue().split("\\.");
                splitIndex = className.length;
                t1.setFeature(className[splitIndex - 2]);
                t1.setClassName(className[splitIndex - 1]);
                System.out.println("Test class :"+t1.getClassName());
                if(t1.getMethodName().equals("US_24492_5_ActivityPanelForDashboard")){
                    t1.setClassName(className[splitIndex - 1]);
                    System.out.println("Test class :"+t1.getClassName());
                }
                if(ErrorProneList.contains(t1.getClassName())){
                   continue;
                }

                String operationText = sout;
                if(currentItem.getChildNodes().getLength() > 0){
                    for(int indexchild=0;indexchild<currentItem.getChildNodes().getLength();indexchild++){
                        if ((currentItem.getChildNodes().item(indexchild).getNodeName().equals("failure"))) {
                            t1.setFailureReason(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("type").getNodeValue());
                            t1.setFailLog(currentItem.getChildNodes().item(indexchild).getFirstChild().getNodeValue());//message
                            t1.setStatus(t1.status.FAIL);
                            if(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("message").getNodeValue().contains("[ running with")){
                            t1 = getScriptLog(operationText, t1);
                            }
                            else{
                                t1.setScriptLog(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("message").getNodeValue());
                            }
                            t1 = getScreenshot(operationText, t1);

                        }
                        if ((currentItem.getChildNodes().item(indexchild).getNodeName().equals("error"))) {
                            t1.setFailureReason(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("type").getNodeValue()); //type
                            if(!(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("message")==null)){
                            t1.setFailLog(currentItem.getChildNodes().item(indexchild).getAttributes().getNamedItem("message").getNodeValue()); // message
                            }
                            t1.setStatus(t1.status.FAIL);
                            if(t1.getScriptLog().length()<1){
                            t1 = getScriptLog(operationText, t1);
                            t1 = getScreenshot(operationText, t1);
                            }
                        }
                        if ((currentItem.getChildNodes().item(indexchild).getNodeName().equals("skipped"))) {
                            t1.setStatus(t1.status.SKIP);
                        }

                    }
                }
                   else {
                    if(getWarningLog(operationText, t1.getMethodName()).length()>0){
                        t1.setStatus(t1.status.PASS_WARN);
                        t1.setWaringText(getWarningLog(operationText, t1.getMethodName()));
                        t1 = getScreenshot(operationText, t1);
                    }else {
                    t1.setStatus(t1.status.PASS);
                    }
                }
                testcaseList.add(t1);
            }
        }
        String day = doc.getElementsByTagName(Constaints.parentNodeClass).item(0).getAttributes().getNamedItem("timestamp").getNodeValue().split("T")[0];
        if (executionMap.containsKey(day)) {
            System.out.println("same day data found");
            ArrayList<TestData> templist;
            System.out.println("Total number of test case in file " + file.getName() + " was :" + testcaseList.size());
            templist = executionMap.get(day);
            System.out.println("previous number of test case :" + templist.size());
            templist.addAll(testcaseList);
            System.out.println("new  number of test case after append :" + templist.size());
            executionMap.put(day, templist);
            return executionMap;
        }

        executionMap.put(day, testcaseList);
        return executionMap;


    }

    public TestData getScreenshot(String operationText, TestData test) {
        String inLogoutFailText="";
        if(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(test.getMethodName())) <
                operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(test.getMethodName()))){


        String inTestFailText = operationText.substring(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(test.getMethodName()))
                , operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(test.getMethodName())));


        int testEndAnchor = operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(test.getMethodName()));
        if(operationText.substring(testEndAnchor).contains(Constaints.StartOfTestCase)){
            inLogoutFailText = operationText.substring(testEndAnchor,(operationText.indexOf(Constaints.StartOfTestCase,testEndAnchor)));
        }else{
            inLogoutFailText = operationText.substring(testEndAnchor);
        }

        if (inTestFailText.contains("\\screenshots\\")) {
            test.setScreenshotlink(inTestFailText.substring(inTestFailText.lastIndexOf("\\screenshots\\"), inTestFailText.indexOf("png",inTestFailText.lastIndexOf("\\screenshots\\"))));
        }
        else if(inLogoutFailText.contains("\\screenshots\\")) {
            test.setScreenshotlink(inLogoutFailText.substring(inLogoutFailText.lastIndexOf("\\screenshots\\"), inLogoutFailText.indexOf("png",inLogoutFailText.lastIndexOf("\\screenshots\\"))));
        }
        else{
            test.setScreenshotlink("No screenshot found for this execution.");
        }
        }
        return test;

    }

    public TestData getScriptLog(String operationText, TestData test) {
        if(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(test.getMethodName())) <
                operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(test.getMethodName()))){

        String temp = operationText.substring(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(test.getMethodName()))
                , operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(test.getMethodName())));
        String[] scripts ;

        if(temp.contains(Constaints.Fail_Log_Text)){
            scripts = temp.substring(temp.indexOf(Constaints.Fail_Log_Text), temp.length() - 1).split("\\n");
            for(int i=0;i<scripts.length;i++){
                if(!scripts[i].contains("------- printStackTrace ----------")){
                    test.setScriptLog(scripts[i]);
                    break;
                }
            }
//           temp = temp.substring(temp.indexOf(Constaints.Fail_Log_Text), temp.length() - 1);
//            if(temp.contains("\\screenshots\\")){
//         test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//            }
            return test;
        }

       if(temp.contains(Constaints.Fail_Excp_Log_Text)){
           scripts = temp.substring(temp.indexOf(Constaints.Fail_Excp_Log_Text), temp.length() - 1).split("\\n");
           for(int i=1;i<scripts.length;i++){
               if(!scripts[i].contains("------- printStackTrace ----------")){
                   test.setScriptLog(scripts[i].contains("]:")?(scripts[i].split("]:") [1]):(scripts[i]));
                   break;
               }
           }
//           temp = temp.substring(temp.indexOf(Constaints.Fail_Excp_Log_Text), temp.length() - 1);
//           if(temp.contains("\\screenshots\\")){
//         test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//           }
           return test;
       }
        if(temp.contains(Constaints.Fail_Asrt_Log_Text)){
            scripts = temp.substring(temp.indexOf(Constaints.Fail_Asrt_Log_Text), temp.length() - 1).split("\\n");
            for(int i=1;i<scripts.length;i++){
                if(!scripts[i].contains("------- printStackTrace ----------")){
                    test.setScriptLog(scripts[i].contains("]:")?(scripts[i].split("]:") [1]):(scripts[i]));

                }
            }

//            temp = temp.substring(temp.indexOf(Constaints.Fail_Asrt_Log_Text), temp.length() - 1);
//            if(temp.contains("\\screenshots\\")){
//            test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//            }
            return test;
        }

        if(temp.contains(Constaints.Fail_AsrtError_Log_Text)){
            scripts = temp.substring(temp.indexOf(Constaints.Fail_AsrtError_Log_Text)-200, temp.length() - 1).split("\\n");
            for(int i=1;i<scripts.length;i++){
                if(scripts[i].contains(Constaints.Fail_AsrtError_Log_Text)){
                    test.setScriptLog(scripts[i-1].contains("]:")?(scripts[i-1].split("]:") [1]):(scripts[i-1]));
//                    test.setScriptLog(scripts[i-1].split("]:") [1]);

                }
            }

//            temp = temp.substring(temp.indexOf(Constaints.Fail_AsrtError_Log_Text), temp.length() - 1);
//            if(temp.contains("\\screenshots\\")){
//            test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//            }
            return test;
        }

        if(temp.contains(Constaints.SrFail_Log_Text)){
            scripts = temp.substring(temp.indexOf(Constaints.SrFail_Log_Text), temp.length() - 1).split("\\n");
            for(int i=0;i<scripts.length;i++){
                if(scripts[i].contains(Constaints.SrFail_Log_Text)){
                    test.setScriptLog(scripts[i]+scripts[i+1]+scripts[i+2]);
                    break;
//                    test.setScriptLog(scripts[i-1].split("]:") [1]);

                }
            }

//            temp = temp.substring(temp.indexOf(Constaints.SrFail_Log_Text), temp.length() - 1);
//            if(temp.contains("\\screenshots\\")){
//            test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//            }
            return test;
        }
        if(temp.contains(Constaints.SrFail_Error_Text)){
            scripts = temp.substring(temp.indexOf(Constaints.SrFail_Error_Text), temp.length() - 1).split("\\n");
            for(int i=0;i<scripts.length;i++){
                if(scripts[i].contains(Constaints.SrFail_Error_Text)){
                    test.setScriptLog(scripts[i]+" "+scripts[i+1]);
//                    test.setScriptLog(scripts[i-1].split("]:") [1]);

                }
            }

//            temp = temp.substring(temp.indexOf(Constaints.SrFail_Error_Text), temp.length() - 1);
//            if(temp.contains("\\screenshots\\")){
//            test.setScreenshotlink(temp.substring(temp.indexOf("\\screenshots\\"), temp.indexOf("png")));
//            }
            return test;
        }
        }
        return test;
    }

    public String getWarningLog(String operationText, String methodName) {
//        String temp = operationText.substring(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(methodName)));

        //TODO: verify warning message as well.
        if (operationText.contains(Constaints.WarningLog)) {
            String temp = operationText.substring(operationText.indexOf(Constaints.StartOfTestCase, operationText.indexOf(methodName))
                    , operationText.indexOf(Constaints.EndOfTestCase, operationText.indexOf(methodName)));
            if(temp.contains(Constaints.WarningLog)){
            return temp.substring(temp.indexOf(Constaints.WarningLog), temp.length() - 1).split("\\n")[0];
        }

    }
        return "";
    }

    public void createXml(Map<String, ArrayList<TestData>> executionMap, String fileLocation) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        //Get all test cases in each file.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);//TODO: if proper xml is not found will give error.
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        String day;
        boolean firstElement = true;
        Element test;
        Element dayelement;
        Element root;
        ArrayList<TestData> singleDayTests;

        Set<String> keySet = executionMap.keySet();
        Iterator runner = keySet.iterator();
        while (runner.hasNext()) {
            day = runner.next().toString();
            singleDayTests = executionMap.get(day);

            if (firstElement) {

                root = doc.createElement("Report");
                doc.appendChild(root);
                dayelement = doc.createElement("day");  // day
                dayelement.setAttribute("date", day);
                root.appendChild(dayelement);
                firstElement = false;
            } else {
                dayelement = doc.createElement("day");  // day
                dayelement.setAttribute("date", day);

                doc.getLastChild().appendChild(dayelement);
            }

            for (int i = 0; i < singleDayTests.size(); i++) {
                test = doc.createElement("testcase");  //package
                dayelement.appendChild(test);
                test.setAttribute("methodName", singleDayTests.get(i).getMethodName());
                test.setAttribute("status", singleDayTests.get(i).getStatus().toString());
                test.setAttribute("featureName", singleDayTests.get(i).getFeature());
                test.setAttribute("className", singleDayTests.get(i).getClassName());
                test.setAttribute("WarningMsg", singleDayTests.get(i).getWaringText());
//                test.setAttribute("failReason", singleDayTests.get(i).getFailureReason());
//                test.setAttribute("failLog", singleDayTests.get(i).getFailLog());


                if ((singleDayTests.get(i).getStatus().toString().length() > 1)) {
                    test.setAttribute("failReason", singleDayTests.get(i).getFailureReason());
                    test.setAttribute("faillog", singleDayTests.get(i).getFailLog());
                    test.setAttribute("scriptLog", singleDayTests.get(i).getScriptLog());
                }
            }
        }

        if (new File(fileLocation).isFile()) {
            new File(fileLocation).delete();
        }
// write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileLocation));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

        System.out.println("File saved!");

    }

}
