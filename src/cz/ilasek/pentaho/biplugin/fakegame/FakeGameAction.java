package cz.ilasek.pentaho.biplugin.fakegame;

import java.io.File;
import java.io.OutputStream;

import org.pentaho.platform.api.action.IStreamingAction;

import configuration.fakegame.ImportFileConfig;

import fakegame.flow.Configurations;
import fakegame.flow.profiles.DMBatchProfile;

public class FakeGameAction implements IStreamingAction 
{
    private OutputStream responseStream;
    private String responseMessage;
    
    private String message;

    @Override
    public String getMimeType(String arg0) {
        return "text/html";
    }

    @Override
    public void execute() throws Exception {
        StringBuilder html = new StringBuilder("<html><h1>Moje zpráva<h1>");
        html.append("<p>asdf" + message + "</p>" + System.getProperty("user.dir"));
        html.append("</html>");
               
        responseMessage = html.toString();
        
//        InputStream input = new FileInputStream(new File("/home/ivo/Dokumenty/Skola/11semestr/X36PM2/weka_doc/WekaManual-3.6.0.pdf"));
//        
//        byte[] bytes = new byte[512];
//        int bytesRead = 0;
//        while ((bytesRead = input.read(bytes)) > 0)
//            responseStream.write(bytes, 0, bytesRead);        

        Configurations configurations = new Configurations(new File("/home/ivo/workspace/fakegame/core/trunk/target/cfg/fake/quick_linear")); // resource - adresar cfg/quicklinear
        boolean series = false; // parameter series
        DMBatchProfile.Strategy strategy = DMBatchProfile.Strategy.CROSSVALIDATION;
        String reportFile = "/home/ivo/fakegame/report.html"; // report file
        ImportFileConfig importFileConfig = (ImportFileConfig) configurations.getOperationsConfig(ImportFileConfig.class);
        importFileConfig.setDatasetFileName("/home/ivo/workspace/fakegame/core/trunk/target/data/iris.txt");
        
        DMBatchProfile profile = new DMBatchProfile(configurations, strategy, series, reportFile);
        profile.run();
        
        responseStream.write(responseMessage.getBytes());
    }

    /**
     * @param responseStream the responseStream to set
     */
    public void setResponseStream(OutputStream responseStream) {
        this.responseStream = responseStream;
    }

    /**
     * @return the responseStream
     */
    public OutputStream getResponseStream() {
        return responseStream;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param responseMessage the responseMessage to set
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return the responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

}
