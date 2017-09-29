public class File extends AbstractFile {
    private String downloadURL;
    //TODO: size of file is also availabla, may implement it for verboser logging
    public File(String name, String downloadURL) {
        super(name);
        this.downloadURL = downloadURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
}
