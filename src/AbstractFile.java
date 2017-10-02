public class AbstractFile {
    private String name;
    private String zipDownloadURL;

    public AbstractFile(String name, String zipDownloadURL){
        this.name = name;
        this.zipDownloadURL = zipDownloadURL;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipDownloadURL() {
        return zipDownloadURL;
    }

    public void setZipDownloadURL(String zipDownloadURL) {
        this.zipDownloadURL = zipDownloadURL;
    }
}