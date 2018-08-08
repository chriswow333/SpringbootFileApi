package cwb.opendata.fileapi.config;

public enum ResourceEnum {
	
	LOG4J_API("file_api_log"),
	LOG4J_API_DB("file_api_DB_log"),
	LOG4J_EXCEPTION("file_api_exception_log"),
	API_AUTH_FAIL_STATUS("authenicate_fail"),
	API_FILE_NOTFOND_STATUS("file_not_found"),
	API_INVALID_ARGUMENT_STATUS("invalid_argument"),
	REQ_PARAM_AUTHKEY("Authorization"),
	REQ_PARAM_AUTHKEY_OLD("authorizationkey"),
	REQ_PARAM_DATAID("dataid"),
	REQ_PARAM_DATAPATH("datapath"),
	REQ_PARAM_FILENAME("filename"),
	REQ_PARAM_FILE_FORMAT("format"),
	REQ_PARAM_DOWNLOADTYPE("downloadType"),
	SC_DIRECTORY("scDirectory"),
	SERVLET_OPENDATAAPI("opendataapi"),
	SERVLET_GOVDOWNLOAD("govdownload");
	
	private String value;
    
	private ResourceEnum(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
    
}
