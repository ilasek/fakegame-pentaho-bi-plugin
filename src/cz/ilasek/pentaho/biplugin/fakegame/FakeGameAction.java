package cz.ilasek.pentaho.biplugin.fakegame;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.platform.api.action.IStreamingAction;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import fakegame.flow.Configurations;
import fakegame.flow.profiles.DMBatchProfile.Strategy;

public class FakeGameAction implements IStreamingAction 
{
    private static final String CROSS_VALIDATION_STRATEGY = "cross-validation";
    private static final String TRAIN_TEST_STRATEGY = "train-test";
    private static final String WHOLE_SET_STRATEGY = "whole-set";
    
    private static final String PENTAHO_TMP_DIRECTORY = "system/tmp";
    private static final String REPORT_DIR_PREFIX = "fgreport_";
    private static final String REPORT_FILE_PREFIX = "fghtmlreport_";
    
    private static final Map<String, Strategy> trainTestStrategyMap;
    
    static {
        trainTestStrategyMap = new HashMap<String, Strategy>();
        trainTestStrategyMap.put(CROSS_VALIDATION_STRATEGY, Strategy.CROSSVALIDATION);
        trainTestStrategyMap.put(TRAIN_TEST_STRATEGY, Strategy.TRAIN_TEST);
        trainTestStrategyMap.put(WHOLE_SET_STRATEGY, Strategy.WHOLE_SET);
    }
    
    private boolean fgExperimentSeries = false;
    private Strategy fgTrainTestStrategy = Strategy.CROSSVALIDATION;
    private Configurations fgConfigurations;    
    
    private String experimentSeries;
    private String trainTestStrategy;
    private String configurationDirectory;
    private IPentahoResultSet data;

    private OutputStream responseStream;
    private String responseMessage; 
    
    private final String reportDirectoryName;
    
    public FakeGameAction()
    {
        reportDirectoryName = generateReportDirectoryName();
    }
    
    @Override
    public String getMimeType(String arg0) {
        return "text/html";
    }

    @Override
    public void execute() throws Exception {
//        StringBuilder html = new StringBuilder("<html><h1>Moje zpráva</h1>");
//        html.append("<p>" + experimentSeries + " ... " + configurationDirectory + "</p>");
//        html.append("<p>... Num of model configs " + fgConfigurations.getNumOfModelConfigs() + "</p>");
//        html.append("</html>");
//               
//        responseMessage = html.toString();
        
        
        File reportDir = prepareReportDirectory();
        String reportFilePrefix = reportDir.getAbsolutePath() + "/" + REPORT_FILE_PREFIX; 
        
//        InputStream input = new FileInputStream(new File("/home/ivo/Dokumenty/Skola/11semestr/X36PM2/weka_doc/WekaManual-3.6.0.pdf"));
//        
//        byte[] bytes = new byte[512];
//        int bytesRead = 0;
//        while ((bytesRead = input.read(bytes)) > 0)
//            responseStream.write(bytes, 0, bytesRead);        

//        Configurations configurations = new Configurations(new File("/home/ivo/workspace/fakegame/core/trunk/target/cfg/fake/quick_linear")); // resource - adresar cfg/quicklinear
//        boolean series = false; // parameter series
//        DMBatchProfile.Strategy strategy = DMBatchProfile.Strategy.CROSSVALIDATION;
//        String reportFile = "/home/ivo/workspace/fakegame/core/trunk/target/data/iris.txt"; // report file
//        ImportFileConfig importFileConfig = (ImportFileConfig) fgConfigurations.getOperationsConfig(ImportFileConfig.class);
//        importFileConfig.setDatasetFileName("/home/ivo/workspace/fakegame/core/trunk/target/data/iris.txt");
        
//        DMBatchProfile profile = new DMBatchProfile(fgConfigurations, fgTrainTestStrategy, 
//                fgExperimentSeries, reportFilePrefix);
        
        DMPentahoBIProfile profile = new DMPentahoBIProfile(fgConfigurations, fgTrainTestStrategy, 
              fgExperimentSeries, reportFilePrefix, data);
        profile.run();
        StringBuffer report = profile.getPentahoBIHTMLReport(getReportDirectoryName());
        
        responseStream.write(report.toString().getBytes());
    }
    
    private File prepareReportDirectory()
    {
        String tmpDir = PentahoSystem.getApplicationContext().getSolutionPath(PENTAHO_TMP_DIRECTORY);
        File reportDir = new File(tmpDir + "/" + getReportDirectoryName());
        reportDir.mkdir();
        
        return reportDir;
    }
    
    private String generateReportDirectoryName()
    {
        return REPORT_DIR_PREFIX + System.currentTimeMillis();
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

    /**
     * @param experimentSeries the experimentSeries to set
     */
    public void setExperimentSeries(String experimentSeries) 
    {
        if (experimentSeries.equalsIgnoreCase("true"))
            fgExperimentSeries = true;
        else
            fgExperimentSeries = false;
        
        this.experimentSeries = experimentSeries;
    }

    /**
     * @return the experimentSeries
     */
    public String getExperimentSeries() {
        return experimentSeries;
    }

    /**
     * @param trainTestStrategy the trainTestStrategy to set
     */
    public void setTrainTestStrategy(String trainTestStrategy) {
        fgTrainTestStrategy = trainTestStrategyMap.get(trainTestStrategy);
        if (fgTrainTestStrategy == null)
            fgTrainTestStrategy = Strategy.CROSSVALIDATION;
        
        this.trainTestStrategy = trainTestStrategy;
    }

    /**
     * @return the trainTestStrategy
     */
    public String getTrainTestStrategy() {
        return trainTestStrategy;
    }

    /**
     * @param configurationDirectory the configurationDirectory to set
     */
    public void setConfigurationDirectory(String configurationDirectory) {
        fgConfigurations = new Configurations(new File(configurationDirectory));
        
        this.configurationDirectory = configurationDirectory;
    }

    /**
     * @return the configurationDirectory
     */
    public String getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @param data the data to set
     */
    public void setData(IPentahoResultSet data) {
        this.data = data;
    }

    /**
     * @return the data
     */
    public IPentahoResultSet getData() {
        return data;
    }

    /**
     * @return the reportDirectoryName
     */
    private String getReportDirectoryName() {
        return reportDirectoryName;
    }

}
