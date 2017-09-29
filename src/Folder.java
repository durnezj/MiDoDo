import java.util.ArrayList;

public class Folder extends AbstractFile {
    private ArrayList<AbstractFile> files;

    public Folder(String name) {
        super(name);
        this.files = new ArrayList<>();
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
}