package testParser;

public class TestData {
    public enum status {
        PASS, FAIL, PASS_WARN,SKIP;
    }

    ;
    public status status;
    private String methodName;
    private String testTime;
    private String failureReason;
    private String className;
    private String feature;
    private String screenshotlink; // skipping screenshot as the link is crated in local run but not accessable from jenks
    private String failLog; // fail log is not presnt for the xml output from jenkins
    private String ScriptLog ="";
    private String waringText;

    public String getWaringText() {
        return waringText;
    }

    public void setWaringText(String waringText) {
        this.waringText = waringText;
    }

    public String getScriptLog() {
        return ScriptLog;
    }

    public void setScriptLog(String scriptLog) {
        ScriptLog = scriptLog;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getScreenshotlink() {
        return screenshotlink;
    }

    public void setScreenshotlink(String screenshotlink) {
        this.screenshotlink = screenshotlink;
    }

    public String getFailLog() {
        return failLog;
    }

    public void setFailLog(String failLog) {
        this.failLog = failLog;
    }

    public TestData.status getStatus() {
        return status;
    }

    public void setStatus(TestData.status status) {
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }
}
