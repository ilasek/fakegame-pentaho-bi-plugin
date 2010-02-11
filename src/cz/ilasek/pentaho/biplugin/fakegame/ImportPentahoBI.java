package cz.ilasek.pentaho.biplugin.fakegame;

import org.pentaho.commons.connection.IPentahoResultSet;

import preprocessing.methods.Import.AbstractBaseLoader;
import fakegame.flow.Blackboard;
import fakegame.flow.operations.AbstractImport;

public class ImportPentahoBI extends AbstractImport {
    
    private IPentahoResultSet resultSet;
    
    public ImportPentahoBI(IPentahoResultSet resultSet)
    {
        this.resultSet = resultSet;
    }

    @Override
    public String getDescription() {
        return "Is able to import the result-set from another action in Pentaho BI action sequence";
    }

    @Override
    public String getName() {
        return "ImportPentahoBI";
    }

    @Override
    protected AbstractBaseLoader getLoader(Blackboard blackboard) {
        return new LoadResultSetPreprocessor(resultSet);
    }

    @Override
    protected String getDataSetFileName(Blackboard blackboard) {
        return "biresultset";
    }

}
