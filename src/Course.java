import java.lang.reflect.Array;
import java.util.ArrayList;

public class Course {
    public static final String COURSE_DOC_HOME_URL = "https://minerva.ugent.be/main/document/document.php?cidReq=";
    private String title;
    private String prof;
    private String cidreq; //E63%%%%%%%%%
    private ArrayList<String> zipDownloadLinkList;

    public Course(String title, String prof, String cidreq) {
        this.title = title;
        this.prof = prof;
        this.cidreq = cidreq;
        this.zipDownloadLinkList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getCidreq() {
        return cidreq;
    }

    public void setCidreq(String cidreq) {
        this.cidreq = cidreq;
    }

    public String getCourseDocumentHomeURL(){
        return COURSE_DOC_HOME_URL + this.cidreq;
    }

    public ArrayList<String> getZipDownloadLinkList() {
        return zipDownloadLinkList;
    }

    public void setZipDownloadLinkList(ArrayList<String> zipDownloadLinkList) {
        this.zipDownloadLinkList = zipDownloadLinkList;
    }

     public void addToZipList(String downloadLink) {
        this.zipDownloadLinkList.add(downloadLink);
     }

}
