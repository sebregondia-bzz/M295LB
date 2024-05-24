package ch.andri.m295lb;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class M295LbApplicationTests {
    private static final String SERVICE_URL = "http://localhost:8080/artifact/resources/book";

    private static final String USERNAME_admin = "admin";
    private static final String PASSWORD = "1234";

    private void addAuthorizationHeaderAdmin(HttpUriRequest request) {
        String auth = USERNAME_admin+":"+PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic "+new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    private void addAuthorizationHeader(HttpUriRequest request, String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    @Test
    public void deleteAllBooks_thenOk() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL);
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void deleteExistingBook_thenOk() throws IOException {
        // Ensure the book with ID 1 exists
        HttpPost postRequest = new HttpPost(SERVICE_URL);
        String json = "{\"bookID\":1,\"title\":\"To Delete\",\"pages\":100,\"publicationDate\":\"2024-05-24T00:00:00\",\"price\":10.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"Test Author\"}}";
        makePost(postRequest, json, HttpStatus.SC_OK);

        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/1");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getAllBooks_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetOneBook_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/1");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetNotExistingBook_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/999999");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getBookExisting_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Existing/6");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getBookExisting_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Existing/9999999");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBooksByPublicationDate_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/publicationDate/1960-07-11T00:00:00");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBooksByPublicationDate_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/publicationDate/1900-06-26T00:00:00");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBooksByPublicationDate_thenBAD_REQUEST() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/publicationDate/0000-00-00T00:00:00");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBooksByTitle_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/title/To Kill a Mockingbird");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBooksByTitle_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/title/WHYNot");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void getGetBookAmount_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/amount");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    private void makeAssert(HttpUriRequest request, int status) throws IOException {
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(status, httpResponse.getStatusLine().getStatusCode());
    }

    private void makePost(HttpPost request, String json, int status) throws IOException {
        addAuthorizationHeaderAdmin(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    private void makePut(HttpPut request, String json, int status) throws IOException {
        addAuthorizationHeaderAdmin(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    @Test
    public void addBook_thenOk() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"bookID\":199,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1900-06-26T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void addBook_thenConflict() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"bookID\":1,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addInvalidBook_thenBadRequest() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"badkey\":999,\"badkey\":\"Backend Applikation realisieren\",\"badkey\":200.0}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void addBooks_thenOk() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL+"/multiple");
        String json = "[{\"bookID\":18,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}},{\"bookID\":19,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}]";
        makePost(request, json, HttpStatus.SC_CREATED);
    }

    @Test
    public void addBooks_thenConflict() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "[{\"bookID\":22,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}},{\"bookID\":19,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}]";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addInvalidBooks_thenBadRequest() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL+"/multiple");
        String json = "[{\"badkey\":999,\"badkey\":\"Backend Applikation realisieren\",\"badkey\":200.0}, {\"bookID\":22,\"title\":\"The Great Gatsby\",\"pages\":180,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}]";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void updateExistingBook_thenOk() throws IOException {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"bookID\":18,\"title\":\"The5265262626 Great Gatsby\",\"pages\":183330,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}";

        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void updateNotExistingBook_thenNotFound() throws IOException {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"bookID\":9999999999,\"title\":\"The5265262626 Great Gatsby\",\"pages\":183330,\"publicationDate\":\"1925-04-10T00:00:00\",\"price\":15.99,\"available\":true,\"author\":{\"authorID\":1,\"name\":\"F. Scott Fitzgerald\"}}";

        makePut(request, json, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void addCreateTables_thenOk() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL+"/createTables");
        String json = "";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void deleteNotExistingBook_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/999999");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    // Tests for Authorization Matrix
    @Test
    public void unauthorizedAccess_thenUnauthorized() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidUserAccess_thenUnauthorized() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        addAuthorizationHeader(request, "invalidUser", "wrongPassword");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void authorizedUserAccess_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        addAuthorizationHeader(request, "user", "1234");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void adminAccess_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
}
