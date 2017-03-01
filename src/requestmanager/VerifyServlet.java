package requestmanager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;

/**
 * Servlet implementation class VerifyServlet
 */
public class VerifyServlet extends HttpServlet {

	private static ConsumerManager consumerManager;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VerifyServlet() {
		super();
	}

	protected void doGet(HttpServletRequest httpReq, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Enumeration en = httpReq.getParameterNames();
			HttpSession session = httpReq.getSession(false);

			// enumerate through the keys and extract the values
			// from the keys!
			while (en.hasMoreElements()) {
				String parameterName = (String) en.nextElement();
				String parameterValue = httpReq.getParameter(parameterName);
				session.setAttribute(parameterName, parameterValue);
			}
			ParameterList response1 = new ParameterList(httpReq.getParameterMap());

			// retrieve the previously stored discovery information
			DiscoveryInformation discovered = (DiscoveryInformation) httpReq.getSession().getAttribute("openid-disc");

			// extract the receiving URL from the HTTP request
			StringBuffer receivingURL = httpReq.getRequestURL();
			String queryString = httpReq.getQueryString();
			if (queryString != null && queryString.length() > 0) {
				receivingURL.append("?").append(httpReq.getQueryString());
			}
			// verify the response; ConsumerManager needs to be the same
			// (static) instance used to place the authentication request

			VerificationResult verification = consumerManager.verify(receivingURL.toString(), response1, discovered);

			// examine the verification result and extract the verified
			// identifier
			Identifier verified = verification.getVerifiedId();
			if (verified != null) {
				response.sendRedirect("Home.jsp");
			}
		} catch (MessageException | DiscoveryException | AssociationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		authenticateUser(request.getParameter("openidvalue"), request, response);
	}

	protected void authenticateUser(String openidUrl, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		DiscoveryInformation discoveryInformation = null;
		ConsumerManager consumerManager = getConsumerManager();
		try {
			List<DiscoveryInformation> discoveries = consumerManager.discover(openidUrl);
			discoveryInformation = consumerManager.associate(discoveries);
			AuthRequest authRequest = createAuthRequest(discoveryInformation, request.getRequestURL().toString());
			response.sendRedirect(authRequest.getDestinationUrl(true));
		} catch (DiscoveryException e) {
			String message = "Error occurred during discovery!";
			throw new RuntimeException(message, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ConsumerManager getConsumerManager() {
		try {
			if (consumerManager == null) {
				consumerManager = new ConsumerManager();
				consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
				consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
			}
		} catch (ConsumerException e) {
			String message = "Exception creating ConsumerManager!";
			throw new RuntimeException(message, e);
		}
		return consumerManager;
	}

	public static AuthRequest createAuthRequest(DiscoveryInformation discoveryInformation, String returnToUrl) {
		AuthRequest ret = null;
		try {
			ret = getConsumerManager().authenticate(discoveryInformation, returnToUrl);
			FetchRequest fetch = FetchRequest.createFetchRequest();
			fetch.addAttribute("email", "http://axschema.org/contact/email", true);
			fetch.addAttribute("fullname", "http://axschema.org/namePerson", true);
			ret.addExtension(fetch);
		} catch (Exception e) {
			String message = "Exception occurred while building AuthRequest object!";
			throw new RuntimeException(message, e);
		}
		return ret;
	}
}
