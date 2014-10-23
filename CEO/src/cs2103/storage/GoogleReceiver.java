package cs2103.storage;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;

import cs2103.CommonUtil;
import cs2103.exception.HandledException;

public class GoogleReceiver {
	private static final String APPLICATION_NAME = "cs2103-CEO/1.0";
	private static final String client_secret = "{\"installed\":{\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"client_secret\":\"zCMOqFnPqBLS-jLR7q2p1LGt\",\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"client_email\":\"\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"oob\"],\"client_x509_cert_url\":\"\",\"client_id\":\"179875106660-efg7a9ehbjv4lcq2pohh1hd1npgdp1fp.apps.googleusercontent.com\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\"}}";
	private static final String userId = "user";
	private final JsonFactory JSON_FACTORY;
	private final Collection<String> scopes;
	private final GoogleClientSecrets clientSecrets;
	private final AuthorizationCodeFlow flow;
	private final VerificationCodeReceiver receiver;
	private HttpTransport httpTransport;
	private AuthStoreFactory dataStoreFactory;
	
	public GoogleReceiver() throws IOException, GeneralSecurityException {
		dataStoreFactory = new AuthStoreFactory();
		JSON_FACTORY = JacksonFactory.getDefaultInstance();
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    scopes = new ArrayList<String>();
	    scopes.add(CalendarScopes.CALENDAR);
	    scopes.add(TasksScopes.TASKS);
	    receiver = new LocalServerReceiver();
	    clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new ByteArrayInputStream(client_secret.getBytes())));
	    flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, scopes).setDataStoreFactory(dataStoreFactory).build();
	}
	
	public Credential authorize() throws HandledException, IOException{
		try{
			Credential credential = flow.loadCredential(userId);
			if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
				return credential;
			}
			String redirectUri = receiver.getRedirectUri();
			onAuthorization(flow.newAuthorizationUrl().setRedirectUri(redirectUri));
			String code = receiver.waitForCode();
			TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
			return flow.createAndStoreCredential(response, userId);
		} finally {
			receiver.stop();
		}
	}
	
	public Calendar getCalendar() throws HandledException, IOException{
		return new Calendar.Builder(httpTransport, JSON_FACTORY, this.authorize()).setApplicationName(APPLICATION_NAME).build();
	}
	
	public Tasks getTasks() throws HandledException, IOException{
		return new Tasks.Builder(httpTransport, JSON_FACTORY, this.authorize()).setApplicationName(APPLICATION_NAME).build();
	}
	
	private void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws HandledException, IOException{
	    String url = authorizationUrl.build();
	    Preconditions.checkNotNull(url);
	    CommonUtil.print("Please open the following address in your browser:");
	    CommonUtil.print(url);
	    if (CommonUtil.checkSyncSupport()){
	    	Desktop desktop = Desktop.getDesktop();
	    	CommonUtil.print("Attempting to open that address in the default browser now...");
	    	desktop.browse(URI.create(url));
	    } else {
	    	throw new HandledException(HandledException.ExceptionType.LOGIN_FAIL);
	    }
	}
}
