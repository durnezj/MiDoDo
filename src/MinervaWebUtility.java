import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class contains the logic for logging in to Minerva and getting all the resources from the website to build the download list.
 * Also the method for downloading this list
 */
public class MinervaWebUtility {
    public static final String LOGIN_URL = "https://minerva.ugent.be/secure/index.php";
    private WebClient webClient;
    public ArrayList<Course> coursesList;
    private String downloadDirectory;

    /**
     * This function needs to be called before executing everyhting else.
     *
     * @return boolean True on success, False on failure
     */
    private boolean initialise() {
        this.coursesList = new ArrayList<>();
        this.webClient = new WebClient(BrowserVersion.CHROME);
        this.downloadDirectory = "C:\\Users\\Thibault Durnez\\Documents\\midodo test";
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        return true;
    }

    public MinervaWebUtility() {
        this.initialise();
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
            this.buildCourseStructure();
            this.downloadAndExtractFiles();

            /*
            System.out.println("Overview of your courses:");
            for (Course course : this.coursesList) {
                System.out.println(course.getTitle() + " " + course.getProf() + " " + course.getCidreq());
                for (String zip : course.getZipDownloadLinkList())
                    System.out.println("\t" + zip);
            }
            */
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * This function scrapes the index page and builds the index of the courses from the landing page.
     *
     * @return boolean Treu on succes, false on fail.
     */
    public boolean buildCourseIndex(HtmlPage MinervaHomepage) {
        //made for cursussen zonder categorie, need cases to expand this. Again, only made for engineering atm.
        Document parsedHomepage = Jsoup.parse(MinervaHomepage.getWebResponse().getContentAsString());
        Elements courseDivs = parsedHomepage.select("div[id^=course_E6]");
        for (Element el : courseDivs) {
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
     *
     * @return boolean True on success, False on failure
     */
    public boolean buildCourseStructure() throws IOException {
        if (!coursesList.isEmpty()) {
            for (Course course : this.coursesList) {
                //System.out.println("Course Title: " + course.getTitle());
                HtmlPage courseDocumentsHomepage = this.webClient.getPage(course.getCourseDocumentHomeURL());
                Document parsedCourseHomepage = Jsoup.parse(courseDocumentsHomepage.getWebResponse().getContentAsString());
                Elements courseFilesList = parsedCourseHomepage.select("div[id^=document::]");
                for (Element folderOrFile : courseFilesList) {
                    String folderOrFileName = folderOrFile.child(0).child(0).attr("title");
                    String zipDownloadlink = folderOrFile.nextSibling().childNode(0).attr("href");

                    if (zipDownloadlink.startsWith("document.php?")) {
                        zipDownloadlink = "https://minerva.ugent.be/main/document/" + zipDownloadlink;
                    }
                    if (zipDownloadlink.startsWith("http://")) {
                        zipDownloadlink.replace("http://", "https://");
                    }
                    course.addToZipList(zipDownloadlink);
                }
            }
            return true;
        } else {
            throw new NullPointerException("empty Course List!");
        }
    }


    public void downloadAndExtractFiles() {
        for (Course course : this.coursesList) {
            String courseFolderFullPath = this.downloadDirectory + "\\" + course.getTitle();
            File courseFolder = new File(courseFolderFullPath);
            boolean dirOk = false;

            try {
                //get boolean and return it for better error checking
                dirOk = courseFolder.mkdir();
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            System.out.println("unzipping");
            for (String zipDownloadLink : course.getZipDownloadLinkList()) {
                if(zipDownloadLink.toLowerCase().contains("action=downloadfolder")) {//it's a zip to extract
                    downloadAndExtractZipFile(zipDownloadLink, courseFolderFullPath);
                } else { //it's a regular file to download
                    downloadRegularFile(zipDownloadLink, courseFolderFullPath);
                }
            }

        }
    }

    public void downloadRegularFile(String FileURL, String outputFolder) {
        try {
            String fileName = FileURL.split("path=%2F")[1].split("&")[0];
            System.out.println(outputFolder + "\\" + fileName);
            InputStream filePage = this.webClient.getPage(FileURL).getWebResponse().getContentAsStream();
            OutputStream fileOuput = new FileOutputStream(new File(outputFolder + "\\" + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = filePage.read(bytes)) != -1) {
                fileOuput.write(bytes, 0, read);
            }
            //TODO CHECK FOR EXISTING SO WE DONT OVERWRITE STUFF!!!!!!!!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadAndExtractZipFile(String zipURL, String outputFolder) {
        //byte[] buffer = new byte[1024];
        //File tempDownloadFile = new File(outputFolder + "\\MiDoDoTemp.zip");
        try {
            //download to temp file
            //InputStream ZipPage = this.webClient.getPage(new URL(zipURL)).getEnclosingWindow().get
            //OutputStream os = new FileOutputStream(tempDownloadFile);

            final UnexpectedPage pdfPage = this.webClient.getPage(zipURL);
            InputStream is = pdfPage.getWebResponse().getContentAsStream();
            OutputStream outStream = null;

            File targetFile = new File(outputFolder + "\\MiDoDoTemp.zip");
            outStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1)
            {
                outStream.write(buffer, 0, bytesRead);
            }

            ZipFile zipFile = new ZipFile(targetFile);
            System.out.println("extracting to " + outputFolder + "\\");
            zipFile.extractAll(outputFolder);

            targetFile.delete();
            System.out.println("Done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}