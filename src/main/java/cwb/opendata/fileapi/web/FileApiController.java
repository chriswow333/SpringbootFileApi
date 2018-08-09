package cwb.opendata.fileapi.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import cwb.opendata.fileapi.common.model.DatasetService;


@RestController
@RequestMapping("/fileapi")
public class FileApiController {
	
	private static final Logger fileApiControllerExceptionLogger = LogManager.getLogger(FileApiController.class);

	@Autowired
	@Qualifier("datasetService")
	private DatasetService datasetService;
	
	@Autowired
	private ServletContext servletContext;

	@Autowired
	@Qualifier("filePathPrefix")
	private String filePathPrefix;
	
	
	@RequestMapping(path = "/v1/opendataapi/{dataid}",method = RequestMethod.GET,
			produces = { "application/json", "application/xml", "application/octet-stream"})
	public ResponseEntity<?> opendataapi(HttpServletRequest request,
			final @PathVariable(value="dataid") String dataid,
			final @RequestParam(value="format", required=false)String format ) {
		
		String dataPath = datasetService.getDatasetPathByDataid(dataid, format);
		return getResources(request, dataPath);
		
	}
	
	@RequestMapping(path = "/opendata/**",method = RequestMethod.GET,
			produces = { "application/json", "application/xml", "application/octet-stream"})
	public ResponseEntity<?> opendata(HttpServletRequest request) {
		String dataPath = new StringBuilder(filePathPrefix).append(request.getRequestURI().substring(17)).toString();
		return getResources(request, dataPath);
	}
	
	private ResponseEntity<?> getResources(HttpServletRequest requset, String dataPath){
		Path path = null;
		if(Optional.ofNullable(dataPath).isPresent() && Files.exists(path = Paths.get(dataPath))) {
			String fileHash = getFileCache(dataPath);
			String clientHash = requset.getHeader(HttpHeaders.IF_NONE_MATCH);
			if(Optional.ofNullable(clientHash).isPresent()) {
				if(clientHash.equals(fileHash)) {
					return cachedResponse(fileHash);
				}
			}
			return normalResponse(path, fileHash);
		}else {
			return notFoundResponse();
		}
	}
	
	private ResponseEntity<?> normalResponse(Path path, String fileHash){
		
		String mimeType =  servletContext.getMimeType(path.toAbsolutePath().toString());

		System.out.println(mimeType);

		MediaType mediaType = MediaType.parseMediaType(mimeType!=null?mimeType:"application/octet-stream");
		try {
			InputStreamResource resource;
			resource = new InputStreamResource(new FileInputStream(path.toAbsolutePath().toString()));
			return ResponseEntity.ok()
					// Content-Disposition
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName())
					.header(HttpHeaders.ETAG, fileHash)
					// Content-Type
					.contentType(mediaType)
					// Contet-Length
					.contentLength(Files.size(path)) //
					.body(resource);
		}catch(Exception e) {
			fileApiControllerExceptionLogger.error("",e);
		}
		return errorResponse();
	}
	
	private ResponseEntity<?> cachedResponse(String fileHash) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.ETAG, fileHash);
		return new ResponseEntity<String>("", headers, HttpStatus.NOT_MODIFIED);
	}
	
	private ResponseEntity<?> errorResponse(){
		Map<String, String> map = new HashMap<>();
		map.put("message", "Under maintenance, please try later.");
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<?> notFoundResponse(){
		Map<String, String> map = new HashMap<>();
		map.put("message", "Resouce not found.");
		return new ResponseEntity<Map<String,String>>(map, HttpStatus.NOT_FOUND);
	}
	
	private String getFileCache(String dataPath){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(dataPath));
			String hash = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			// weak hash for Nginx proxy.
			hash ="W/" + hash;
			return hash;
		}catch(IOException e) {
			return null;
		}finally {
			try {
				if(fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
