package model.fileuser;

/*
该类是文件的目录项
 */

public class DirectoryItem  {

    private int typeOfFile ; //文件类型，三种 0目录、1普通文件.txt、2执行文件.e 2B

    //文件属性 1B 0只读文件 1系统文件 2普通文件 3目录属性 其余几位是无效位
    private boolean isOnlyRead ; //只读文件
    private boolean isSystemFile ; //系统文件
    private boolean isFile ; //普通文件
    private boolean isDirectory ; //是目录
    private String fileName ;//文件名，绝对路径 3B
    private int numOfStartDisk ;//起始盘号，0-255 1B
    private int lengthOfFile ;//文件长度 1B
    private String fileContext ; //文件的内容
    private String copyFromStr = null;
    public byte[] bytes;//转化成bytes
    //构造方法
    public DirectoryItem(){

        typeOfFile=-1;
    }
    public DirectoryItem(int typeOfFile,String fileName,boolean isSystemFile,boolean isOnlyRead,String fileContext,String copyFromStr){
        this.bytes = new byte[8];
        this.fileName = fileName;
        this.typeOfFile = typeOfFile;
        this.isSystemFile = isSystemFile;
        this.isOnlyRead = isOnlyRead;
        this.lengthOfFile = 1;
        this.numOfStartDisk = 2;
        this.fileContext = fileContext;
        this.copyFromStr = copyFromStr;
        byte[] nameBytes = getactFileName().getBytes();
        System.out.println(getactFileName());
        if (nameBytes.length>3){
            System.out.println(this.fileName+"命名有误");
            //return;
        }
        int i;

        for (i=0;i<nameBytes.length;i++){
            bytes[i] = nameBytes[i];
        }
        for (;i<3;i++){
            bytes[i] = 0;
        }
        if(typeOfFile ==0){
            this.isDirectory = true;
            this.isFile = false;
            this.bytes[3]=0;
            this.bytes[4]=0;
        }
        else if (typeOfFile ==1){
            this.isDirectory = false;
            this.isFile = true;
            this.bytes[3]='t';
            this.bytes[4]=0;
        }
        else if (typeOfFile==2){
            this.isDirectory = false;
            this.isFile = true;
            this.bytes[3]='e';
            this.bytes[4]=0;
        }
        //转换byte
        int proprety =0;
        if (this.isDirectory){
            proprety +=1;
            proprety=proprety<<1;
            //System.out.println(proprety);
        }
        else if (this.typeOfFile==1){
            proprety=proprety<<1;
            proprety +=1;
            //System.out.println(proprety);
        }
        else if(this.typeOfFile==2){
            //System.out.println(proprety);
        }
        if (this.isSystemFile){
            proprety=proprety<<1;
            proprety+=1;
            //System.out.println(proprety);
        }
        else{
            proprety=proprety<<1;
            //System.out.println(proprety);
        }
        if (this.isOnlyRead){
            proprety = proprety<<1;
            proprety+=1;
            //System.out.println(proprety);
        }
        else{
            proprety = proprety<<1;
            //System.out.println(proprety);
        }
        bytes[6] = (byte) this.numOfStartDisk;
        bytes[7] = (byte)this.lengthOfFile;
        bytes[5] = (byte)proprety;
        System.out.println("5属性："+bytes[5]);
        System.out.println("6开始磁盘号："+bytes[6]);
        System.out.println("7长度："+bytes[7]);

    }

    //化成byte
    public DirectoryItem(byte[] bytes){
        this.bytes = bytes;
        this.fileName = new String(bytes,0,3);
        String strFileType = new String(bytes,3,2);
        if ((bytes[5]&1)==1){
            this.isOnlyRead = true;
        }
        else{
            this.isOnlyRead = false;
        }
        if((bytes[5]&2)==2){
            this.isSystemFile = true;
        }
        else{
            this.isSystemFile = false;
        }
        if((bytes[5]&4)==4){
            this.isFile = true;
            this.isDirectory = false;
        }
        else {
            this.isFile = false;
            this.isDirectory = true;
        }
        this.numOfStartDisk = bytes[6];
        this.lengthOfFile = bytes[7];
        this.fileContext = null;
    }



    //获得实际文件名（不包含路径）的方法
    public String getactFileName(){
        int i = this.fileName.lastIndexOf("/");
        int e = this.fileName.lastIndexOf(".");
    System.out.println(i);
        String actName = null;
        if(i==-1){
            System.out.println(i);
            actName = this.fileName.substring(0);
        }
        else {
            if(e==-1){

                actName = this.fileName.substring(i+1);
            }
            else {

                actName = this.fileName.substring(i+1,e);
            }
        }

        return actName;
    }

    //更改文件名
    public void changeFileName(int type,String name){
        int endNum = this.fileName.lastIndexOf("/");
        String path = null;
        if(endNum==-1){
            path  = null;
        }
        else {
            path = this.fileName.substring(0,endNum);
        }
        if (type==0){
            fileName = path+"/"+name;
        }
        else if (type==1){
            fileName = path+"/"+name+".t";
        }
        else if (type==2){
            fileName = path+"/"+name+".e";
        }

    }

    //G&&S

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getFileContext() {
        return fileContext;
    }

    public void setFileContext(String fileContext) {
        this.fileContext = fileContext;
    }

    public boolean isOnlyRead() {
        return isOnlyRead;
    }

    public void setOnlyRead(boolean onlyRead) {
        isOnlyRead = onlyRead;
    }

    public boolean isSystemFile() {
        return isSystemFile;
    }

    public void setSystemFile(boolean systemFile) {
        isSystemFile = systemFile;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public int getTypeOfFile() {
        return typeOfFile;
    }

    public void setTypeOfFile(int typeOfFile) {
        this.typeOfFile = typeOfFile;
    }

    public String getFileName() {
        return fileName;
    }

    //修改文件名
    public void setFileName(String fileName) throws Exception{

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

    public String getCopyFromStr() {
        return copyFromStr;
    }

    public void setCopyFromStr(String copyFromStr) {
        this.copyFromStr = copyFromStr;
    }
}
