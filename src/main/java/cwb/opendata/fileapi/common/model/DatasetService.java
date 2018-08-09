package cwb.opendata.fileapi.common.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("datasetService")
public class DatasetService {

	
	@Autowired
	@Qualifier("datasetDao")
	private DatasetDao datasetDao;
	
	public String getDatasetPathByDataid(String dataid, String format) {
		
		if(Optional.ofNullable(format).isPresent()) {
			return datasetDao.getRealDataPath(dataid, format);
		}else {
			List<String> formats = datasetDao.getDatasetFileFormats(dataid);
			boolean checkDataidExist = formats.stream().collect(Collectors.toList()).size() == 0?false:true;
			if(checkDataidExist) {
				// Find XML format first, otherwise get random format. 
				format = formats.stream().filter(v -> v.equalsIgnoreCase("XML")).findAny().orElse(formats.stream().findFirst().get());
				return datasetDao.getRealDataPath(dataid, format);
			}else {
				return null;
			}
			
		}
		
	}
}
