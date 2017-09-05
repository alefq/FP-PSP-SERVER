package py.org.fundacionparaguaya.pspserver.odkclient;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opendatakit.aggregate.odktables.rest.entity.*;
import org.opendatakit.api.forms.entity.FormUploadResult;
import org.opendatakit.api.offices.entity.RegionalOffice;
import org.opendatakit.api.users.entity.RoleDescription;
import org.opendatakit.api.users.entity.UserEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Collections.*;

public class OdkClient {

    private static Log logger = LogFactory.getLog(OdkClient.class);

    public static String USER_LIST_ENDPOINT = "/users/list";
    public static String USER_CURRENT_ENDPOINT = "/users/current";
    public static String USER_CHANGE_PASSWORD = "/users/current/password";
    public static String OFFICES_ENDPOINT = "/offices";
    public static String OFFICES_DELETE_ENDPOINT = "/offices/{officeId}";


    public static String FORM_UPLOAD_ENDPOINT = "/forms/{appId}/{odkClientVersion}";

    public static String ROLES_GRANTED_ENDPOINT = "/roles/granted";
    public static String ROLES_LIST_ENDPOINT = "/roles/list";

    public static String ADMIN_USERS_ENDPOINT = "/admin/users";
    public static String ADMIN_ANONYMOUS_USER_ENDPOINT = "/admin/users/anonymous";
    public static String ADMIN_DELETE_USER_ENDPOINT = "/admin/users/username:{username}";
    public static String ADMIN_CHANGE_PASSWORD = "/admin/users/username:{username}/password";

    public static String TABLES_ENDPOINT = "/odktables/{appId}/tables";
    public static String TABLE_MANIFEST_ENDPOINT =
            "/odktables/{appId}/manifest/{odkClientVersion}/{tableId}";
    public static String TABLE_ROWS_ENDPOINT =
            "/odktables/{appId}/tables/{tableId}/ref/{schemaETag}/rows";
    public static String TABLE_SINGLE_ROW_ENDPOINT =
            "/odktables/{appId}/tables/{tableId}/ref/{schemaETag}/rows/{rowId}";
    public static String TABLE_SINGLE_ROW_ATTACHMENT_MANIFEST =
            "/odktables/{appId}/tables/{tableId}/ref/{schemaETag}/attachments/{rowId}/manifest";
    public static String TABLE_ATTACHMENT_MANIFEST_ENDPOINT =
            "/odktables/{appId}/tables/{tableId}/ref/{schemaETag}/attachments/manifest";
    public static String TABLE_OFFICES_ENDPOINT = "/odktables/{appId}/tables/{tableId}/offices";


    public static String TABLE_FILE_PROXY_ENDPOINT = "/odktables/{appId}/files/{odkClientVersion}";
    public static String TABLE_EXPORT_PROXY_ENDPOINT = "/odktables/{appId}";
    public static String TABLE_ATTACHMENT_PROXY_ENDPOINT = "";



    private RestTemplate restTemplate;
    private URL odkUrl;
    private String odkAppId;
    private String odkClientVersion;
    private String odkRealm;

    private Authentication authentication;

    public OdkClient(RestTemplate restTemplate, URL odkUrl, String odkAppId,
                     String odkClientVersion, String odkRealm) {
        this.restTemplate = restTemplate;
        this.odkUrl = odkUrl;
        this.odkAppId = odkAppId;
        this.odkClientVersion = odkClientVersion;
        this.odkRealm = odkRealm;
    }

    public String getOdkRealm() {
        return odkRealm;
    }

    public String getFileProxyEndpoint() {
        return getUrl(TABLE_FILE_PROXY_ENDPOINT);
    }

    public String getTableExportProxyEndpoint() {
        return getUrl(TABLE_EXPORT_PROXY_ENDPOINT);
    }

    public String getAttachmentProxyEndpoint() {
        return getUrl(TABLE_ATTACHMENT_PROXY_ENDPOINT);
    }

    public String getFormUploadEndpoint() {
        return getUrl(FORM_UPLOAD_ENDPOINT);
    }

    public HttpStatus deleteOffice(String officeId) {
        String deleteUrl =
                odkUrl.toExternalForm() + OFFICES_DELETE_ENDPOINT.replace("{officeId}", officeId);
        ResponseEntity<String> getResponse =
                restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, String.class);
        logger.debug("Called " + deleteUrl + " and received " + getResponse.getStatusCode());

        return getResponse.getStatusCode();
    }

    public HttpStatus updateOffice(RegionalOffice office) {
        String postUrl = odkUrl.toExternalForm() + OFFICES_ENDPOINT;

        HttpEntity<RegionalOffice> postUserEntity = new HttpEntity<>(office);
        ResponseEntity<RegionalOffice> postResponse =
                restTemplate.exchange(postUrl, HttpMethod.POST, postUserEntity, RegionalOffice.class);
        logger.debug("Sending " + office.toString());
        logger.debug("Called " + postUrl + " and received " + postResponse.getStatusCode());

        return postResponse.getStatusCode();
    }


    public UserEntity getCurrentUser() {
        String getUserUrl = odkUrl.toExternalForm() + USER_CURRENT_ENDPOINT;
        ResponseEntity<UserEntity> getResponse =
                restTemplate.exchange(getUserUrl, HttpMethod.GET, null, UserEntity.class);

        return getResponse.getBody();
    }


    public HttpStatus deleteUser(String username) {
        String deleteUserUrl =
                odkUrl.toExternalForm() + ADMIN_DELETE_USER_ENDPOINT.replace("{username}", username);
        ResponseEntity<UserEntity> getResponse =
                restTemplate.exchange(deleteUserUrl, HttpMethod.DELETE, null, UserEntity.class);

        return getResponse.getStatusCode();
    }

    public void setCurrentUserPassword(String password) {
        String changePasswordUrl = odkUrl.toExternalForm() + USER_CHANGE_PASSWORD;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json");
        HttpEntity<?> request = new HttpEntity<>(password, headers);
        ResponseEntity<String> postResponse =
                restTemplate.postForEntity(changePasswordUrl, request, String.class);
    }

    public HttpStatus updateUser(UserEntity userEntity) {
        String postUserUrl = odkUrl.toExternalForm() + ADMIN_USERS_ENDPOINT;

        HttpEntity<UserEntity> postUserEntity = new HttpEntity<>(userEntity);
        ResponseEntity<UserEntity> postResponse =
                restTemplate.exchange(postUserUrl, HttpMethod.POST, postUserEntity, UserEntity.class);

        return postResponse.getStatusCode();
    }

    public HttpStatus updateAnonymousUser(UserEntity userEntity) {
        String postUserUrl = odkUrl.toExternalForm() + ADMIN_ANONYMOUS_USER_ENDPOINT;

        HttpEntity<UserEntity> postUserEntity = new HttpEntity<>(userEntity);
        ResponseEntity<UserEntity> postResponse =
                restTemplate.exchange(postUserUrl, HttpMethod.POST, postUserEntity, UserEntity.class);

        return postResponse.getStatusCode();
    }


    public HttpStatus changePasswordUser(String username, String password) {
        String postUserUrl =
                odkUrl.toExternalForm() + ADMIN_CHANGE_PASSWORD.replace("{username}", username);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json");
        HttpEntity<?> request = new HttpEntity<>(password, headers);

        // Submit a new user
        ResponseEntity<String> postResponse =
                restTemplate.exchange(postUserUrl, HttpMethod.POST, request, String.class);

        return postResponse.getStatusCode();
    }

    public List<UserEntityForm> getUserAuthorityGrid() {
        String getUserListUrl = odkUrl.toExternalForm() + ADMIN_USERS_ENDPOINT;
        ResponseEntity<List<UserEntity>> getResponse = restTemplate.exchange(getUserListUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<UserEntity>>() {});
        List<UserEntityForm> entityFormList = new ArrayList<UserEntityForm>();
        for (UserEntity userEntity : getResponse.getBody()) {
            entityFormList.add(new UserEntityForm(userEntity));
        }
        return entityFormList;
    }

    public List<RoleDescription> getRoleList() {
        String getUserListUrl = odkUrl.toExternalForm() + ROLES_LIST_ENDPOINT;
        ResponseEntity<List<RoleDescription>> getResponse = restTemplate.exchange(getUserListUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RoleDescription>>() {});
        return getResponse.getBody();
    }

    public List<RegionalOffice> getOfficeList() {
        String getOfficeUrl = odkUrl.toExternalForm() + OFFICES_ENDPOINT;
        ResponseEntity<List<RegionalOffice>> getResponse = restTemplate.exchange(getOfficeUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RegionalOffice>>() {});
        return getResponse.getBody();
    }

    public List<String> getTableIds() {
        String getTablesUrl = getUrl(TABLES_ENDPOINT);

        logger.info("Calling " + getTablesUrl);

        ResponseEntity<TableResourceList> getResponse = restTemplate.exchange(getTablesUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<TableResourceList>() {});
        TableResourceList tables = getResponse.getBody();

        List<String> tableIds = new ArrayList<String>();
        for (TableResource table : tables.getTables()) {
            tableIds.add(table.getTableId());
        }
        return tableIds;
    }

    public OdkTablesFileManifest getTableManifest(String tableId) {

        String getManifestUrl = getUrl(TABLE_MANIFEST_ENDPOINT).replace("{tableId}", tableId);
        logger.debug("Calling " + getManifestUrl);

        ResponseEntity<OdkTablesFileManifest> getResponse = restTemplate.exchange(getManifestUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<OdkTablesFileManifest>() {});
        OdkTablesFileManifest manifest = getResponse.getBody();

        return manifest;

    }



    public List<String> getTableOffices(String tableId) {

        String getOfficesUrl = getUrl(TABLE_OFFICES_ENDPOINT).replace("{tableId}", tableId);
        logger.debug("Calling " + getOfficesUrl);

        ResponseEntity<List<String>> getResponse = restTemplate.exchange(getOfficesUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});
        List<String> offices = getResponse.getBody();

        return offices;

    }

    public OdkTablesFileManifest getTableAttachmentManifest(String tableId, String schemaETag) {

        String getManifestUrl = getUrl(TABLE_ATTACHMENT_MANIFEST_ENDPOINT).replace("{tableId}", tableId)
                .replace("{schemaETag}", schemaETag);
        logger.debug("Calling " + getManifestUrl);

        ResponseEntity<OdkTablesFileManifest> getResponse = restTemplate.exchange(getManifestUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<OdkTablesFileManifest>() {});
        OdkTablesFileManifest manifest = getResponse.getBody();

        return manifest;

    }

    public String getFormDefinition(String url) {
        ResponseEntity<String> getResponse =
                restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String response = getResponse.getBody();
        logger.info(response);
        return response;
    }

    public TableResource getTableResource(String tableId) {

        String getTableUrl = getUrl(TABLES_ENDPOINT) + "/" + tableId;
        logger.debug("Calling " + getTableUrl);

        ResponseEntity<TableResource> getResponse = restTemplate.exchange(getTableUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<TableResource>() {});
        TableResource tableResource = getResponse.getBody();
        return tableResource;

    }

    public RowResourceList getRowResourceList(String tableId, String schemaETag, String sortColumn,
                                              boolean ascending) {

        StringBuilder getRowListUrl = new StringBuilder(getUrl(TABLE_ROWS_ENDPOINT)
                .replace("{tableId}", tableId).replace("{schemaETag}", schemaETag));
        getRowListUrl.append("?sortColumn=" + sortColumn);
        getRowListUrl.append("&ascending=" + ascending);


        logger.debug("Calling " + getRowListUrl);
        ResponseEntity<RowResourceList> getResponse = restTemplate.exchange(getRowListUrl.toString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<RowResourceList>() {});
        RowResourceList tableResource = getResponse.getBody();
        return tableResource;

    }

    public RowOutcomeList putRowList(String tableId, String schemaETag, RowList rowList) {

        StringBuilder getRowListUrl = new StringBuilder(getUrl(TABLE_ROWS_ENDPOINT)
                .replace("{tableId}", tableId).replace("{schemaETag}", schemaETag));

        logger.debug("Calling " + getRowListUrl);

        HttpEntity<RowList> putRowListEntity = new HttpEntity<>(rowList);
        ResponseEntity<RowOutcomeList> postResponse = restTemplate.exchange(getRowListUrl.toString(),
                HttpMethod.PUT, putRowListEntity, RowOutcomeList.class);

        return postResponse.getBody();
    }

    public RowResource getSingleRow(String tableId, String schemaETag, String rowId) {

        StringBuilder getSingleRowUrl =
                new StringBuilder(getUrl(TABLE_SINGLE_ROW_ENDPOINT).replace("{tableId}", tableId)
                        .replace("{schemaETag}", schemaETag).replace("{rowId}", rowId));

        logger.info("Calling " + getSingleRowUrl);
        ResponseEntity<RowResource> getResponse = restTemplate.exchange(getSingleRowUrl.toString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<RowResource>() {});
        RowResource tableResource = getResponse.getBody();
        return tableResource;

    }

    public OdkTablesFileManifest getSingleRowAttachments(String tableId, String schemaETag,
                                                         String rowId) {

        StringBuilder getSingleRowUrl =
                new StringBuilder(getUrl(TABLE_SINGLE_ROW_ATTACHMENT_MANIFEST).replace("{tableId}", tableId)
                        .replace("{schemaETag}", schemaETag).replace("{rowId}", rowId));

        logger.info("Calling " + getSingleRowUrl);
        ResponseEntity<OdkTablesFileManifest> getResponse =
                restTemplate.exchange(getSingleRowUrl.toString(), HttpMethod.GET, null,
                        new ParameterizedTypeReference<OdkTablesFileManifest>() {});
        OdkTablesFileManifest rowAttachmentManifest = getResponse.getBody();
        return rowAttachmentManifest;

    }


    public String getUrl(String endpoint) {
        return odkUrl.toExternalForm()
                + (endpoint.replace("{appId}", odkAppId).replace("{odkClientVersion}", odkClientVersion));
    }

    /**
     * Upload a file, which is sent to the ODK Server
     *
     * @param file
     * @param offices
     * @return
     * @throws IOException File needs to be converted to FileSystemResource before transmission.
     * @see http://stackoverflow.com/questions/41632647/multipart-file-upload-with-spring-resttemplate-and-jackson
     * @see https://stackoverflow.com/questions/42212557/uploading-a-list-of-multipartfile-with-spring-4-resttemplate-java-client-rest
     */
    public FormUploadResult oldUploadFile(MultipartFile file, List<String> offices)
            throws IOException {


        String postUploadUrl = odkUrl.toExternalForm() + (FORM_UPLOAD_ENDPOINT
                .replace("{appId}", odkAppId).replace("{odkClientVersion}", odkClientVersion));

        int sizeOf = 0;
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();

        for (String office : offices) {
            logger.info("Office bytes: " + office.getBytes().length);
            sizeOf += office.getBytes().length;
            parts.add(GeneralConsts.OFFICE_ID, office);
        }

        parts.add(GeneralConsts.ZIP_FILE, new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                // Causes errors if the file does not have a name ending in .zip.
                return "temp.zip";
            }
        });

        logger.info("File bytes: " + file.getBytes().length);
        sizeOf += file.getBytes().length;

        logger.info("Size of all content: " + sizeOf);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, header);

        logger.info(
                "Request entity Content Length header: " + requestEntity.getHeaders().getContentLength());

        ResponseEntity<FormUploadResult> entity =
                restTemplate.postForEntity(postUploadUrl, requestEntity, FormUploadResult.class);
        return entity.getBody();
    }


    public FormUploadResult uploadFile(MultipartFile file, List<String> offices) throws IOException
    {
        OdkUploadClient odkUploadClient = new OdkUploadClient(this);
        return odkUploadClient.uploadFile(file, offices, authentication);
    }

    public OdkClient authentication(Authentication authentication) {
        this.authentication = authentication;
        return this;
    }

    public Map<String, SurveyQuestion> getQuestionsDefinition(String tableId) {
        OdkTablesFileManifest manifest = this.getTableManifest(tableId);
        OdkTablesFileManifestEntry formDefEntry = null;
        for (OdkTablesFileManifestEntry entry : manifest.getFiles()) {
            if (entry.filename != null
                    && entry.filename.toLowerCase().endsWith(GeneralConsts.FORMS_JSON_FILENAME.toLowerCase())) {
                formDefEntry = entry;
                break;
            }
        }
        String jsonFormDefinition = this.getFormDefinition(formDefEntry.downloadUrl);
        try {
            JsonNode rootNode = new ObjectMapper().readValue(jsonFormDefinition, JsonNode.class);
            logger.info("jsonFormDefinition:\n" + jsonFormDefinition);
            return QuestionsUtils.getSurveyQuestionMap(rootNode);

        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }

    public List<ArrayList<DataKeyValue>> getQuestionsRows(String tableId) {
        TableResource tableResource = this.getTableResource(tableId);

        RowResourceList rowResourceList = this.getRowResourceList(tableId, tableResource.getSchemaETag(), "", true);

        List<ArrayList<DataKeyValue>> collect = rowResourceList.getRows().stream()
                .map(Row::getValues)
                .collect(Collectors.toList());
        return collect;
    }
}
