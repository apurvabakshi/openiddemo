package requestmanager;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.sreg.SRegRequest;

/**
 * Servlet implementation class VerifyServlet
 */
//@WebServlet("/VerifyServlet")
public class VerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		VerifyUser(request.getParameter("openidvalue"),request, response);
	}
	
	protected void VerifyUser(String openidUrl, HttpServletRequest request, HttpServletResponse response) throws IOException{

			DiscoveryInformation discoveryInformation = null;
			//
			ConsumerManager consumerManager = getConsumerManager();
			try {
			List<DiscoveryInformation> discoveries = consumerManager.discover(openidUrl);
			discoveryInformation = consumerManager.associate(discoveries);
			 AuthRequest authRequest = createOpenIdAuthRequest(discoveryInformation, "http://localhost:8080/openidconnect/Home.jsp");
			// System.out.println("URL:"+authRequest.getDestinationUrl(true));
			 //  request.getRequestDispatcher(authRequest.getDestinationUrl(true)).forward(request, response);
			 response.sendRedirect(authRequest.getDestinationUrl(true));
			} catch (DiscoveryException e) {
				String message = "Error occurred during discovery!";
				throw new RuntimeException(message, e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private static ConsumerManager consumerManager;
	
	private static ConsumerManager getConsumerManager() {
		try {
			if (consumerManager == null) {
				consumerManager = new ConsumerManager();
				consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
				consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
			}
		} catch (ConsumerException e) {
			String message = "Exception creating ConsumerManager!";
		//	log.error(message, e);
			throw new RuntimeException(message, e);
		}
		return consumerManager;
	}
	
	public static AuthRequest createOpenIdAuthRequest(DiscoveryInformation discoveryInformation, String returnToUrl) {
		AuthRequest ret = null;
		//
		try {
			// Create the AuthRequest object
			ret = getConsumerManager().authenticate(discoveryInformation, returnToUrl);
			// Create the Simple Registration Request
			SRegRequest sRegRequest = SRegRequest.createFetchRequest();
			sRegRequest.addAttribute("email", false);
			sRegRequest.addAttribute("fullname", false);
			sRegRequest.addAttribute("dob", false);
			sRegRequest.addAttribute("postcode", false);
			ret.addExtension(sRegRequest);
			
		} catch (Exception e) {
			String message = "Exception occurred while building AuthRequest object!";
	//		log.error(message, e);
			throw new RuntimeException(message, e);
		}
		return ret;
	}
}
