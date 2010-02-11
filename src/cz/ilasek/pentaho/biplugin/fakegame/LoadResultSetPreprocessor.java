package cz.ilasek.pentaho.biplugin.fakegame;

import game.utils.Exceptions.InvalidArgument;
import game.utils.Exceptions.NonExistingAttributeException;

import java.util.InputMismatchException;

import org.pentaho.commons.connection.IPentahoMetaData;
import org.pentaho.commons.connection.IPentahoResultSet;

import preprocessing.methods.Import.AbstractBaseLoader;
import preprocessing.storage.PreprocessingStorage;
import preprocessing.storage.PreprocessingStorage.DataType;
import weka.core.FastVector;

public class LoadResultSetPreprocessor extends AbstractBaseLoader {
    
    private static final long serialVersionUID = 1L;
    private IPentahoResultSet resultSet;
    
    public LoadResultSetPreprocessor(IPentahoResultSet resultSet)
    {
        super();
        this.resultSet = resultSet;
    }

    @Override
    public boolean run() throws NonExistingAttributeException {
        if (resultSet.getRowCount() > 0)
        {
            addAttributes();
            readData();
        
            return true;
        }
        else 
            return false;
    }
    
    private void addAttributes()
    {
        IPentahoMetaData meta = resultSet.getMetaData();
        Object[][] headers = meta.getColumnHeaders();
        Object[] firstDataRow = resultSet.getDataRow(0);
        
        if (headers.length > 1)
            throw new InputMismatchException("FakeGame is not able to work with multidimensional data.");
        
        for (int i = 0; i < headers[0].length; i++)
        {
            String fieldName = headers[0][i].toString();
            DataType dataType = (firstDataRow[i] instanceof Number) ? DataType.NUMERIC : DataType.MIXED;
            
            try {
                if (isClassAttribute(fieldName))
                    store.addNewAttribute(new FastVector(), extractClassName(fieldName), dataType, PreprocessingStorage.AttributeRole.OUTPUT);
                else
                    store.addNewAttribute(new FastVector(), fieldName, dataType, PreprocessingStorage.AttributeRole.INPUT);
            } catch (InvalidArgument e) {
                logger.error("Problems during adding attribute "  + fieldName , e);
            }
        }
    }
    
    private void readData()
    {
        resultSet.beforeFirst();
        Object[] row;
        while ((row = resultSet.next()) != null)
        {
            int i = 0;
            for (Object value : row)
            {
                if (value instanceof Number)
                    value = ((Number) value).doubleValue();
                else
                    value = value.toString();
                
                store.addDataItem(i++, value);
            }
        }
    }
    
    /**
     * @param fieldName
     * @return True, if the attribute represents a class (is marked with !).
     */
    private boolean isClassAttribute(String fieldName)
    {
        return fieldName.charAt(0)== '!';
    }
    
    /**
     * Strips ! from the beginning of the class name.
     *  
     * @param fieldName
     * @return
     */
    private String extractClassName(String fieldName)
    {
        return fieldName.substring(1);   
    }    

    @Override
    public void finish() {
        resultSet.closeConnection();
        resultSet.close();
    }

    @Override
    public boolean isApplyOnTestingData() {
        return false;
    }

}
