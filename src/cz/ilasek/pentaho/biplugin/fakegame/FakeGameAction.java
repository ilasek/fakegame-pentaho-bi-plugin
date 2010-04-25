package cz.ilasek.pentaho.biplugin.fakegame;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
    private static final String CFG_DIR_PREFIX = "fgcfg_";
    
    private static final int BUFFER = 2048;
    
    private static final Map<String, Strategy> trainTestStrategyMap;
    
    static {
        trainTestStrategyMap = new HashMap<String, Strategy>();
        trainTestStrategyMap.put(CROSS_VALIDATION_STRATEGY, Strategy.CROSSVALIDATION);
        trainTestStrategyMap.put(TRAIN_TEST_STRATEGY, Strategy.TRAIN_TEST);
        trainTestStrategyMap.put(WHOLE_SET_STRATEGY, Strategy.WHOLE_SET);
    }
    
    private boolean fgExperimentSeries = false;
    private Strategy fgTrainTestStrategy = Strategy.CROSSVALIDATION;
    
    /* Inputs */
    private String experimentSeries;
    private String trainTestStrategy;
    private IPentahoResultSet data;

    /* Resources */
    private InputStream configZip;
    
    /* Outputs */
    private OutputStream responseStream;
    private OutputStream zippedReportStream;
    
    private final String reportDirectoryName;
    private final String configDirectoryName;
    
    public FakeGameAction()
    {
        reportDirectoryName = generateUniqueDirectoryName(REPORT_DIR_PREFIX);
        configDirectoryName = generateUniqueDirectoryName(CFG_DIR_PREFIX);
    }
    
    @Override
    public String getMimeType(String arg0) {
        if (arg0.equals("zippedReport"))
            return "application/zip";
        else
            return "text/html";
    }

    @Override
    public void execute() throws Exception {
        File reportDir = prepareReportDirectory();
        File cfgDir = prepareConfigDirectory(reportDir);
        
        String reportFilePrefix = reportDir.getAbsolutePath() + "/" + REPORT_FILE_PREFIX; 
        Configurations fgConfigurations = new Configurations(cfgDir);
        
        DMPentahoBIProfile profile = new DMPentahoBIProfile(fgConfigurations, fgTrainTestStrategy, 
              fgExperimentSeries, reportFilePrefix, data);
        profile.run();
        StringBuffer report = profile.getPentahoBIHTMLReport(getReportDirectoryName());
        
        if (responseStream != null)
            responseStream.write(report.toString().getBytes());
        
        writeZippedReport();
    }
    
    private File prepareConfigDirectory(final File parentDirectory) throws IOException
    {
        String tmpDir = PentahoSystem.getApplicationContext().getSolutionPath(PENTAHO_TMP_DIRECTORY);
        File cfgDir = new File(tmpDir + System.getProperty("file.separator") + getConfigDirectoryName());
        cfgDir.mkdir();
        
        unzipConfig(cfgDir);
        
        return cfgDir;
    }
    
    private void unzipConfig(File cfgDir) throws IOException
    {
        BufferedOutputStream dest = null;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(configZip));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(cfgDir.getPath() + System.getProperty("file.separator") + entry.getName());
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        zis.close();
    }
    
    private void writeZippedReport() throws IOException
    {
        if (zippedReportStream != null)
        {
            ZipOutputStream zout = new ZipOutputStream(zippedReportStream);
            File reportDir = new File(getReportDirectory());
            File[] reportFiles = reportDir.listFiles();
            
            for (File file : reportFiles)
            {
                FileInputStream in = new FileInputStream(file);
    
                // Add ZIP entry to output stream.
                zout.putNextEntry(new ZipEntry(file.getName()));
    
                // Transfer bytes from the file to the ZIP file
                int len;
                byte[] buf  = new byte[BUFFER];
                while ((len = in.read(buf)) > 0) {
                    zout.write(buf, 0, len);
                }
    
                // Complete the entry
                zout.closeEntry();
                in.close();            
            }
            zout.close();
        }
    }
    
    private File prepareReportDirectory()
    {
        
        File reportDir = new File(getReportDirectory());
        reportDir.mkdir();
        
        return reportDir;
    }
    
    private String getReportDirectory()
    {
        String tmpDir = PentahoSystem.getApplicationContext().getSolutionPath(PENTAHO_TMP_DIRECTORY);
        
        return tmpDir + System.getProperty("file.separator") + getReportDirectoryName(); 
    }
    
    private String generateUniqueDirectoryName(String namePrefix)
    {
        return namePrefix + System.currentTimeMillis();
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

    /**
     * @param configZip Stream with zipped configuration directory.
     */
    public void setConfigZip(InputStream configZip) {
        this.configZip = configZip;
    }

    /**
     * @return the configZip
     */
    public InputStream getConfigZip() {
        return configZip;
    }

    /**
     * @return the configDirectoryName
     */
    private String getConfigDirectoryName() {
        return configDirectoryName;
    }

    /**
     * @param zippedReportStream the zippedReportStream to set
     */
    public void setZippedReportStream(OutputStream zippedReportStream) {
        this.zippedReportStream = zippedReportStream;
    }

    /**
     * @return the zippedReportStream
     */
    public OutputStream getZippedReportStream() {
        return zippedReportStream;
    }
}
