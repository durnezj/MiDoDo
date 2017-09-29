import java.util.ArrayList;

public class Course {
    public static final String COURSE_DOC_HOME_URL = "https://minerva.ugent.be/main/document/document.php?cidReq=";
    private String title;
    private String prof;
    private String cidreq; //E63%%%%%%%%%
    private ArrayList<AbstractFile> files;

    public Course(String title, String prof, String cidreq) {
        this.title = title;
        this.prof = prof;
        this.cidreq = cidreq;
        this.files = new ArrayList<>();
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

    public void addFile(AbstractFile file){
        this.files.add(file);
    }
}
