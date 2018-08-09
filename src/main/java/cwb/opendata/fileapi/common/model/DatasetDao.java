package cwb.opendata.fileapi.common.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("datasetDao")
public class DatasetDao {

    @Autowired
    private JdbcTemplate template;
    
    private static final String GET_DATAPATH_FROM_METADATA_BY_DATANAMEFIXID = 
    		"SELECT datasetname FROM metadata_format AS f INNER JOIN metadata AS m "
    		+ " ON m.datanamefixid = f.datanamefixid WHERE m.status > 0 AND m.datanamefixid = ? AND f.format = ?";

    private static final String GET_FORMATS_FROM_METADATA_BY_DATANAMEFIXID = 
    		"SELECT format FROM metadata_format AS f INNER JOIN metadata AS m "
    		+ "ON m.datanamefixid = f.datanamefixid WHERE m.status > 0 AND m.datanamefixid = ?";
    
    public String getRealDataPath(String dataid, String format){
    	List<String> results = template.queryForList(GET_DATAPATH_FROM_METADATA_BY_DATANAMEFIXID, 
    			new Object[] {dataid, format},String.class);
    	return results.size()==1?results.get(0):null;
    	
    }
    
    public List<String> getDatasetFileFormats(String dataid){
    	return template.queryForList(GET_FORMATS_FROM_METADATA_BY_DATANAMEFIXID, 
    			new Object[] {dataid},String.class);
	}
    
}
