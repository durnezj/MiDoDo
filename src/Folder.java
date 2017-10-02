import java.util.ArrayList;

public class Folder extends AbstractFile {
    private ArrayList<AbstractFile> files;

    public Folder(String name, String zip_download_url) {
        super(name,zip_download_url);
        this.files = new ArrayList<>();
    }

    public void setFiles(ArrayList<AbstractFile> files) {
        this.files = files;
    }

    public ArrayList<AbstractFile> getFiles(){
        return this.files;
    }

    public void addFile(AbstractFile file){
        this.files.add(file);
    }
}