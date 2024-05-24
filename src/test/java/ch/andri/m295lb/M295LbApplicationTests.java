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

    private static final String USERNAME_user = "user";

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
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Existing/1");
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
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/publicationDate/1997-06-26T00:00:00");
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
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/title/It");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
    @Test
    public void getGetBooksByTitle_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/title/WHY Not");
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
        HttpUriRequest deleteRequest = new HttpPost(SERVICE_URL);
        addAuthorizationHeaderAdmin(deleteRequest);
        HttpClientBuilder.create().build().execute(deleteRequest);

        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"bookID\":100,\"title\":\"Backend Applikation realisieren\",\"pages\":1\",\"publicationDate\":1986-09-15,\"price\":29.99,\"available\":True,\"author\":{\"authorID\":1,\"name\": \"F. Scott Fitzgerald\"}}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void addBook_thenConflict() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"bookID\":100,\"title\":\"Backend Applikation r324234252ealisieren\",\"pages\":122\",\"publicationDate\":1986-09-15,\"price\":219.99,\"available\":False,\"author\":{\"authorID\":1,\"name\": \"F. Scott Fitzgerald\"}}";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addInvalidBook_thenBadRequest() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"badkey\":999,\"badkey\":\"Backend Applikation realisieren\",\"badkey\":200.0}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void updateExistingBook_thenOk() throws IOException {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\n" +
                "  \"bookID\":8,  \n" +
                "  \"title\": \"The Great Gatsby\",\n" +
                "  \"pages\": 180,\n" +
                "  \"publicationDate\": \"1925-04-10T00:00:00\", // Assuming this date is in the past\n" +
                "  \"price\": 15.99,\n" +
                "  \"available\": true,\n" +
                "  \"author\": {\n" +
                "    \"authorID\":1,  \n" +
                "    \"name\": \"F. Scott Fitzgerald\"\n" +
                "  }\n" +
                "}";
        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void deleteExistingBook_thenOk() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/999");
        addAuthorizationHeaderAdmin(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
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
        addAuthorizationHeader(request, "user", "password");
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

    // Edge Case: Adding a Book with boundary values
    @Test
    public void addBookWithBoundaryValues_thenOk() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"number\":1,\"designation\":\"Edge Case Book\",\"cost\":0.01}";
        makePost(request, json, HttpStatus.SC_OK);
    }
}




/*package ch.andri.m295lb;

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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class M295LbApplicationTests {
    private static final String SERVICE_URL
            = "http://localhost:8080/artifact/resources/book";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    private void addAuthorizationHeader(HttpUriRequest request) {
        String auth = USERNAME+":"+PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic "+new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    @Test
    public void getAllBooks_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Book");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void getGetOneBook_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Book/295");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void getGetNotExistingBook_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL+"/Book/999999");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_NOT_FOUND,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    public void makeAssert(HttpUriRequest request, int status) throws IOException {
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                status,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    public void makePost(HttpPost request, String json, int status) throws IOException {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    public void makePut(HttpPut request, String json, int status) throws IOException {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    @Test
    public void addBook_thenOk() throws IOException {
        HttpUriRequest deleteRequest = new HttpDelete(SERVICE_URL+"/Book/999");
        addAuthorizationHeader(deleteRequest);
        HttpClientBuilder.create().build().execute(deleteRequest);

        HttpPost request = new HttpPost(SERVICE_URL+"/Book");
        addAuthorizationHeader(request);
        String json = "{\"number\":999,\"designation\":\"Backend Applikation realisieren\",\"cost\":200.0}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void addBook_thenConflict() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL+"/Book");
        addAuthorizationHeader(request);
        String json = "{\"number\":295,\"designation\":\"Backend Applikation realisieren\",\"cost\":200.0}";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addInvalidBook_thenBadRequest() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL+"/Book");
        addAuthorizationHeader(request);
        String json = "{\"badkey\":999,\"badkey\":\"Backend Applikation realisieren\",\"badkey\":200.0}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void updateExistingBook_thenOk() throws IOException {
        HttpPut request = new HttpPut(SERVICE_URL+"/Book");
        addAuthorizationHeader(request);
        String json = "{\"number\":295,\"designation\":\"Backend Applikation realisieren\",\"cost\":215.0}";
        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void deleteExistingBook_thenOk() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL+"/Book/999");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void deleteNotExistingBook_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL+"/Book/999999");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_NOT_FOUND,
                httpResponse.getStatusLine().getStatusCode()
        );
    }
}
*/