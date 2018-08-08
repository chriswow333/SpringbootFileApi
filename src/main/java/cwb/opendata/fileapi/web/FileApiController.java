package cwb.opendata.fileapi.web;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cwb.opendata.fileapi.common.model.DatasetDao;


@RestController
@RequestMapping("/fileapi")
public class FileApiController {
	
	private static final Logger fileApiControllerExceptionLogger = LogManager.getLogger(FileApiController.class);

	@Autowired
	@Qualifier("datasetDao")
	private DatasetDao datasetDao;
	
	@Autowired
	private ServletContext servletContext;

	@Autowired
	@Qualifier("filePathPrefix")
	private String filePathPrefix;
	
	
	@RequestMapping(path = "/v1/opendataapi/{dataid}",method = RequestMethod.GET,
			produces = { "application/json", "application/xml", "application/octet-stream"})
	public ResponseEntity<?> opendataapi(final @PathVariable(value="dataid") String dataid,
			final @RequestParam(value="format", required=false)String format ) {
		String dataPath = datasetDao.getRealDataPath(dataid, format==null?"XML":format);

		return getResources(dataPath);
	}
	
	@RequestMapping(path = "/opendata/**",method = RequestMethod.GET,
			produces = { "application/json", "application/xml", "application/octet-stream"})
	public ResponseEntity<?> opendata(HttpServletRequest request) {
		String dataPath = new StringBuilder(filePathPrefix).append(request.getRequestURI().substring(17)).toString();
		return getResources(dataPath);
	}
	
	
	private ResponseEntity<?> getResources(String dataPath){
		Path path = null;
		if(Optional.ofNullable(dataPath).isPresent() && Files.exists(path = Paths.get(dataPath))) {
			try {
				
				String mimeType =  servletContext.getMimeType(dataPath);
				MediaType mediaType = MediaType.parseMediaType(mimeType);
				
				InputStreamResource resource;
				resource = new InputStreamResource(new FileInputStream(dataPath));

				return ResponseEntity.ok()
						// Content-Disposition
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName())
						// Content-Type
						.contentType(mediaType)
						// Contet-Length
						.contentLength(Files.size(path)) //
						.body(resource);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fileApiControllerExceptionLogger.error("",e);
			}
			
		}else {
			Map<String, String> map = new HashMap<>();
			map.put("message", "Resouce not found.");
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}
		
		Map<String, String> map = new HashMap<>();
		map.put("message", "Under maintenance, please try later.");
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	
		
	}
	
}
