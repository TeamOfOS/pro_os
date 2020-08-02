package model.fileuser;

public class DirectoryItem {

    private int typeOfFile ; //文件类型，三种 0目录、1普通文件.txt、2执行文件.e 2B
    private int attributeOfFile ;//文件属性，1B ,0目录，1txt，2.e
    private String fileName ;//文件名，绝对路径 3B
    private int numOfStartDisk ;//起始盘号，0-255 1B
    private int lengthOfFile ;//文件长度 1B


    //G&&S

    public int getTypeOfFile() {
        return typeOfFile;
    }

    public void setTypeOfFile(int typeOfFile) {
        this.typeOfFile = typeOfFile;
    }

    public int getAttributeOfFile() {
        return attributeOfFile;
    }

    public void setAttributeOfFile(int attributeOfFile) {
        this.attributeOfFile = attributeOfFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNumOfStartDisk() {
        return numOfStartDisk;
    }

    public void setNumOfStartDisk(int numOfStartDisk) {
        this.numOfStartDisk = numOfStartDisk;
    }

    public int getLengthOfFile() {
        return lengthOfFile;
    }

    public void setLengthOfFile(int lengthOfFile) {
        this.lengthOfFile = lengthOfFile;
    }
}
