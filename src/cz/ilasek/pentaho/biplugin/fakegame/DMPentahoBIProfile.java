package cz.ilasek.pentaho.biplugin.fakegame;

import java.io.File;

import org.pentaho.commons.connection.IPentahoResultSet;

import cz.ilasek.pentaho.biplugin.fakegame.report.PentahoBIReportRenderer;
import fakegame.flow.Blackboard;
import fakegame.flow.Configurations;
import fakegame.flow.operations.BasicPreprocessing;
import fakegame.flow.operations.CreateClusters;
import fakegame.flow.operations.CrossValidationStrategy;
import fakegame.flow.operations.DataReduce;
import fakegame.flow.operations.ExperimentSeries;
import fakegame.flow.operations.FeatureRanking;
import fakegame.flow.operations.IDatasetStrategy;
import fakegame.flow.operations.IEvaluator;
import fakegame.flow.operations.IHierarchic;
import fakegame.flow.operations.IModel;
import fakegame.flow.operations.ModelConfigIterator;
import fakegame.flow.operations.OperationFactory;
import fakegame.flow.operations.OperationFlow;
import fakegame.flow.operations.OperationFlowInfo;
import fakegame.flow.operations.PrepareReports;
import fakegame.flow.operations.ProjectTo2D;
import fakegame.flow.operations.RegisterReports;
import fakegame.flow.operations.TrainingTestingStrategy;
import fakegame.flow.operations.VisualizeDataset;
import fakegame.flow.operations.WholeDatasetStrategy;
import fakegame.flow.profiles.DMBatchProfile.Strategy;
import fakegame.flow.utils.DatasetOverview;
import game.report.HTMLReportRenderer;
import game.report.ReportManager;
import game.report.ReportRenderer;

public class DMPentahoBIProfile {

    private static final String HTML_REPORT_NAME = "report.html";
    
    private final Configurations configurations;
    private final Strategy strategy;
    private final boolean series;
    private final String reportFile;    
    private final IPentahoResultSet resultSet;
    
    public DMPentahoBIProfile(Configurations configurations, Strategy strategy, boolean series, String reportFile, IPentahoResultSet resultSet) {
        this.configurations = configurations;
        this.strategy = strategy;
        this.series = series;
        this.reportFile = reportFile;
        this.resultSet = resultSet;
    }
    
    public void run() {
        OperationFlow operationFlow = new OperationFlow();
        Blackboard blackboard = new Blackboard(configurations);

        RegisterReports registerReports = new RegisterReports();
        operationFlow.appendOperation(registerReports);
        
        ImportPentahoBI importPentahoBI = new ImportPentahoBI(resultSet);
        operationFlow.appendOperation(importPentahoBI);

        operationFlow.run(blackboard);

        PrepareReports prepareReports = (reportFile == null ? new PrepareReports() : new PrepareReports(reportFile));
        operationFlow.appendOperation(prepareReports);

        BasicPreprocessing basicPreprocessing = new BasicPreprocessing();
        operationFlow.appendOperation(basicPreprocessing);

        if (blackboard.getMiningType() == DatasetOverview.MiningType.SELF_ORGANIZATION) {
            CreateClusters createClusters = new CreateClusters();
            operationFlow.appendOperation(createClusters);
        }

        DataReduce dataReduce = new DataReduce();
        operationFlow.appendOperation(dataReduce);
        operationFlow.run(blackboard);

        FeatureRanking featureRanking = new FeatureRanking();
        operationFlow.appendOperation(featureRanking);

        operationFlow.run(blackboard);

        VisualizeDataset visualizeDataset = new VisualizeDataset();
        operationFlow.appendOperation(visualizeDataset);

        operationFlow.run(blackboard);

        ProjectTo2D projectTo2D = new ProjectTo2D();
        operationFlow.appendOperation(projectTo2D);

        operationFlow.run(blackboard);

        OperationFactory operationFactory = new OperationFactory(blackboard);
        IModel buildModel = operationFactory.createModelBuilder();
        IEvaluator evaluator = operationFactory.createEvaluator();

        IDatasetStrategy chosenStrategy;
        switch (strategy) {
            case TRAIN_TEST:
                chosenStrategy = new TrainingTestingStrategy(buildModel, evaluator);
                break;
            case WHOLE_SET:
                chosenStrategy = new WholeDatasetStrategy(buildModel, evaluator);
                break;
            default://CROSSVALIDATION
                chosenStrategy = new CrossValidationStrategy(buildModel, evaluator);
        }

        IHierarchic modelBuildTree;
        if (series) {
            modelBuildTree = new ExperimentSeries(chosenStrategy);
        } else {
            modelBuildTree = chosenStrategy;
        }

        // for >1 model/classifier config files, iterate them all
        if (((blackboard.getMiningType() == DatasetOverview.MiningType.REGRESSION) &&
                configurations.getNumOfModelConfigs() > 1) ||
                configurations.getNumOfClassifierConfigs() > 1) {
            modelBuildTree = new ModelConfigIterator(modelBuildTree);
        }
        operationFlow.appendOperation(modelBuildTree);
        operationFlow.run(blackboard);

        ReportManager.INSTANCE.storeSubreport(modelBuildTree.getReport());

        operationFlow.appendOperation(new OperationFlowInfo(operationFlow));
        operationFlow.run(blackboard);
        
        ReportRenderer renderer = new HTMLReportRenderer(
                (File) blackboard.current().value(PrepareReports.PR_REPORT_FILE),
                HTML_REPORT_NAME);
        renderer.render();        
    }
    
    public StringBuffer getPentahoBIHTMLReport(String reportImagesDir)
    {
        PentahoBIReportRenderer reportRenderer = new PentahoBIReportRenderer(reportImagesDir);
        return reportRenderer.getReport();
    }
}
