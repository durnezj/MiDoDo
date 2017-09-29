import java.util.ArrayList;

public class Folder extends AbstractFile {
    private ArrayList<AbstractFile> files;
    private String folderURL;

    public Folder(String name, String download_url) {
        super(name);
        this.files = new ArrayList<>();
        this.folderURL = download_url;
    }

    public void setFiles(ArrayList<AbstractFile> files) {
        this.files = files;
    }

    public ArrayList<AbstractFile> getFiles(){
        return this.files;
    }

    public void addFile(File file){
        this.files.add(file);
    }

    public String getFolderURL() {
        return folderURL;
    }

    public void setFolderURL(String folderURL) {
        this.folderURL = folderURL;
    }
}