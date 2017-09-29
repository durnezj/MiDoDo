import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * This class contains the logic for logging in to Minerva and getting all the resources from the website to build the download list.
 * Also the method for downloading this list
 */
public class MinervaWebUtility {
    public static final String LOGIN_URL = "https://minerva.ugent.be/secure/index.php";
    private WebClient webClient;
    public ArrayList<Course> coursesList;

    /**
     * This function needs to be called before executing everyhting else.
     * @return boolean True on success, False on failure
     */
    private boolean initialise() {
        this.coursesList = new ArrayList<>();
        this.webClient = new WebClient(BrowserVersion.CHROME);
        //set some options of the HtmlUnit WebClient
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        return true;
    }

    public MinervaWebUtility(){
        this.initialise();
    }

    /**
     * This function gets an authentication salt from the minerva login page.
     *
     * @return String the authentication salt string as a 32 character string, NULL on error
     */
    public String getAuthenticationSalt() {
        //TODO: rewrite to use HtmlUnit
        String authenticationSalt;
        URLConnection minervaConnection;
        try {
            minervaConnection = new URL(LOGIN_URL).openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            minervaConnection.getInputStream(), "UTF-8"
                    )
            );
            String inputLine;
            StringBuilder responseHTML = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                responseHTML.append(inputLine + "\n");
            in.close();
            Document parsedHTML = Jsoup.parse(responseHTML.toString());
            authenticationSalt = parsedHTML.select("input[name=authentication_salt]").first().attr("value");
            //System.out.println(authenticationSalt);
            return authenticationSalt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This function logs in to the Minerva platform.
     *
     * @param username String of the username
     * @param password String of the password
     * @return True on successful login, false on failed login
     */
    public boolean login(String username, String password) {
        try {
            //TODO: returns based on HTTP status code, need for return based on scrape of which page has been returned (login page or logged in page)
            HtmlPage loginPage = this.webClient.getPage("https://minerva.ugent.be/secure/index.php");
            HtmlForm alternativeLoginForm = loginPage.getFormByName("frm_login");
            HtmlSubmitInput loginButton = alternativeLoginForm.getInputByName("submitAuth");
            HtmlTextInput loginUsername = alternativeLoginForm.getInputByName("login");
            HtmlPasswordInput loginPassword = alternativeLoginForm.getInputByName("password");
            loginUsername.setValueAttribute(username);
            loginPassword.setValueAttribute(password);

            HtmlPage logged_in = loginButton.click();
            //System.out.println(logged_in.getWebResponse().getContentAsString());
            this.buildCourseIndex(logged_in);
            System.out.println("Overview of your courses:");
            for(Course course: this.coursesList){
                System.out.println(course.getTitle());
                System.out.println("\t- " + course.getCidreq());
                System.out.println("\t- " + course.getProf());
            }
            this.buildCourseStructure();

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * This function scrapes the index page and builds the index of the courses from the landing page.
     * @return boolean Treu on succes, false on fail.
     */
    public boolean buildCourseIndex(HtmlPage MinervaHomepage) {
        //made for cursussen zonder categorie, need cases to expand this. Again, only made for engineering atm.
        Document parsedHomepage = Jsoup.parse(MinervaHomepage.getWebResponse().getContentAsString());
        Elements courseDivs= parsedHomepage.select("div[id^=course_E6]");
        for (Element el :courseDivs) {
            String[] profString = el.text().split("-");
            this.coursesList.add(new Course(
                    Jsoup.parse(el.childNode(1).toString()).text(),
                    profString[profString.length - 1].replaceFirst(" ", ""),
                    el.id().split("_")[1]
            ));
        }
        return true;
    }

    /**
     * this function build the folder - file structure for each course.
     * @return boolean True on success, False on failure
     */
    public boolean buildCourseStructure() throws IOException {
        if(!coursesList.isEmpty()) {
            for (Course course : this.coursesList) {
                HtmlPage courseDocumentsHomepage = this.webClient.getPage(course.getCourseDocumentHomeURL());
            }
            return true;
        } else {
            return false;
        }



    }
}