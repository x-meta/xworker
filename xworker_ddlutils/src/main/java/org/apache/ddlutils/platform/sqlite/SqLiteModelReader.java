package org.apache.ddlutils.platform.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.TypeMap;
import org.apache.ddlutils.platform.DatabaseMetaDataWrapper;
import org.apache.ddlutils.platform.JdbcModelReader;
import org.apache.ddlutils.platform.MetaDataColumnDescriptor;

/*
 * Reads a database model from a SQLite database.
 */
public class SqLiteModelReader extends JdbcModelReader {

    /*
     * Creates a new model reader for H2 databases.
     * 
     * @param platform
     *            The platform that this model reader belongs to
     */
    public SqLiteModelReader(Platform platform) {
        super(platform);
        setDefaultCatalogPattern(null);
        setDefaultSchemaPattern(null);
    }

    protected Collection readForeignKeys(DatabaseMetaDataWrapper metaData, String tableName) throws SQLException {
        // TODO
        return new ArrayList();
    }

    protected Collection readIndices(DatabaseMetaDataWrapper metaData, String tableName) throws SQLException {
        // TODO
        return new ArrayList();
    }

    /* Below here copied from H2.  May still need tweaking */
    
    @Override
    @SuppressWarnings("unchecked")
    protected Column readColumn(DatabaseMetaDataWrapper metaData, Map values) throws SQLException {
        Column column = super.readColumn(metaData, values);
        if (values.get("CHARACTER_MAXIMUM_LENGTH") != null) {
            column.setSize(values.get("CHARACTER_MAXIMUM_LENGTH").toString());
        }
        if (values.get("COLUMN_DEFAULT") != null) {
            column.setDefaultValue(values.get("COLUMN_DEFAULT").toString());
        }
        if (values.get("NUMERIC_SCALE") != null) {
            column.setScale((Integer) values.get("NUMERIC_SCALE"));
        }
        if (TypeMap.isTextType(column.getTypeCode()) && (column.getDefaultValue() != null)) {
            column.setDefaultValue(unescape(column.getDefaultValue(), "'", "''"));
        }
        return column;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List initColumnsForColumn() {
        List result = new ArrayList();
        result.add(new MetaDataColumnDescriptor("COLUMN_DEF", 12));
        result.add(new MetaDataColumnDescriptor("COLUMN_DEFAULT", 12));
        result.add(new MetaDataColumnDescriptor("TABLE_NAME", 12));
        result.add(new MetaDataColumnDescriptor("COLUMN_NAME", 12));
        result.add(new MetaDataColumnDescriptor("DATA_TYPE", 4, new Integer(1111)));
        result.add(new MetaDataColumnDescriptor("NUM_PREC_RADIX", 4, new Integer(10)));
        result.add(new MetaDataColumnDescriptor("DECIMAL_DIGITS", 4, new Integer(0)));
        result.add(new MetaDataColumnDescriptor("NUMERIC_SCALE", 4, new Integer(0)));
        result.add(new MetaDataColumnDescriptor("COLUMN_SIZE", 12));
        result.add(new MetaDataColumnDescriptor("CHARACTER_MAXIMUM_LENGTH", 12));
        result.add(new MetaDataColumnDescriptor("IS_NULLABLE", 12, "YES"));
        result.add(new MetaDataColumnDescriptor("REMARKS", 12));
        return result;
    }
}
