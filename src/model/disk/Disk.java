package model.disk;

import controller.contextController;
import javafx.scene.control.TreeItem;
import model.fileuser.DirectoryItem;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk {

    RandomAccessFile diskModel;//磁盘模型

    private contextController contextController;
    public Disk() throws FileNotFoundException {
        diskModel = new RandomAccessFile("disk.dat","rw");

    }

    /*
    从磁盘中获得分配表
     */
    public byte[] getFat() throws IOException{

        byte[] fat = new byte[128];
        diskModel.seek(0);
        diskModel.read(fat,0,fat.length);

        return fat;
    }

    /*
    是否能创建文件
     */
    public boolean isCreateOp(DirectoryItem directoryItem,TreeItem<DirectoryItem> parentItem) throws IOException {
        if(firstBlock()==-1){
            System.out.println("磁盘容量不足");
            return false;
        }
        if (emptyInDir(parentItem.getValue().getNumOfStartDisk())==-1){
            System.out.println("父目录不够放");
            return false;
        }
        System.out.println("可以创建文件");
        return true;
    }
    /*
    创建文件,修改磁盘内容
     */
    public void createOp(TreeItem<DirectoryItem> directoryItem) throws IOException {



            directoryItem.getValue().setNumOfStartDisk(firstBlock());
            setFat(directoryItem.getValue().getNumOfStartDisk(),-1);
            //System.out.println("开始磁盘号："+directoryItem.getValue().getNumOfStartDisk());
            byte[] buffer = new byte[64]; //磁盘读取的byte
            byte[] dirBytes = directoryItem.getValue().getBytes(); //目录的byte

           TreeItem<DirectoryItem> parentDir = directoryItem.getParent();
           if(parentDir==null){
               parentDir = directoryItem;
           }
           diskModel.seek(parentDir.getValue().getNumOfStartDisk()*64); //定位磁盘指针
           diskModel.read(buffer,0,buffer.length); //读取磁盘相应盘块
            int piont = 0;//指针
           for (int i=0;i<64;i+=8){
               if(buffer[i]==0){//终止条件是0
                   //System.out.println("有这个条件吗进入");
                    piont = i;
                   diskModel.seek(parentDir.getValue().getNumOfStartDisk()*64+piont);
                   diskModel.write(dirBytes,0,dirBytes.length);
                    break;
               }
           }

    }

    /*
    复制站贴文件操作
     */
    public int pasteOp(DirectoryItem directoryItem,TreeItem<DirectoryItem> parentItem) throws IOException {
        if (emptyInDir(parentItem.getValue().getNumOfStartDisk())==-1){
            System.out.println("所在目录不够位置");
            return -1;
        }
        int coutBlock = directoryItem.getFileContext().getBytes().length/64;
        if(coutBlock()<coutBlock){
            System.out.println("空间不足！");
            return -2;
        }
       System.out.println("可以写入,记得再加一个编辑文字内容的方法");
        return 1;
    }
    /*
    编辑文字内容
     */
    public boolean eidtOp(TreeItem<DirectoryItem> directoryItem,String context) throws IOException {
        byte[] contextBytes = context.getBytes();
        byte[] replaceBytes = new byte[64];
        int coutBlock = contextBytes.length/64+1;
/*        if (coutBlock==0){
            coutBlock=1;
        }*/
        //System.out.println("新内容需要磁盘数："+coutBlock);
        int becoutBlock = directoryItem.getValue().getLengthOfFile();
        byte[] buffer = new byte[64];
/*        System.out.println("文本长度："+contextBytes.length);
        System.out.println("之前的磁盘数量；"+becoutBlock);
        System.out.println("新内容的磁盘数量："+coutBlock);*/
        if(coutBlock()<(coutBlock-becoutBlock)){
            System.out.println("磁盘容量不足");
            return false;
        }
        directoryItem.getValue().setLengthOfFile(coutBlock);
        TreeItem<DirectoryItem> parentDir = directoryItem.getParent();
        int parentStartBlock = parentDir.getValue().getNumOfStartDisk();
        String comName = directoryItem.getValue().getactFileName();
        diskModel.seek(parentStartBlock*64);
        diskModel.read(buffer,0,buffer.length);
        int i;
        System.out.println("现在的名字："+comName);
        for(i=0;i<64;i+=8){
            String nowName = new String(buffer,i,3);
            if(comName.equals(nowName)){
                //修改磁盘长度
                byte[] beBytes = directoryItem.getValue().getBytes();
                beBytes[7] =(byte) coutBlock;
                System.arraycopy(beBytes,0,buffer,i,8);
                diskModel.seek(parentStartBlock*64+i);
                diskModel.write(beBytes,0,8);
                System.out.println("文件的目录项修改成功");
                break;
            }
        }
        if(i==64){
            System.out.println("未找到目录项");
        }
        if (becoutBlock>coutBlock){//未debug不确定
            int a;
            int nowBlock = directoryItem.getValue().getNumOfStartDisk();
            int nextBlock = directoryItem.getValue().getNumOfStartDisk();
            for (a=0;a<coutBlock;a++){
                nowBlock = nextBlock;
                //System.out.println("下面这句可能报错");
                int len = contextBytes.length-a*64;
                if (len>64){
                    len = 64;
                }
                System.arraycopy(new byte[64],0,replaceBytes,0,64);
                System.arraycopy(contextBytes,a*64,replaceBytes,0,len);
                //System.out.println("上面这句可能报错");
                diskModel.seek(nowBlock*64);
                diskModel.write(replaceBytes,0,64);
                nextBlock = nextBlock(nowBlock);
            }
            setFat(nowBlock,-1);
            for (;a<becoutBlock;a++){
                nowBlock = nextBlock;
                nextBlock = nextBlock(nowBlock);
                setFat(nowBlock,0);
            }

            System.out.println("修改磁盘完成");
            return true;
        }
        else {
            int a;
            int nowBlock = directoryItem.getValue().getNumOfStartDisk();
            int nextBlock = directoryItem.getValue().getNumOfStartDisk();
            for (a=0;a<becoutBlock;a++){
                nowBlock = nextBlock;
               // System.out.println("下面这句可能报错");
                int len = contextBytes.length-a*64;
                if (len>64){
                    len = 64;
                }
                System.arraycopy(new byte[64],0,replaceBytes,0,64);
                System.arraycopy(contextBytes,a*64,replaceBytes,0,len);
/*                System.out.println("上面这句可能报错");*/
                diskModel.seek(nowBlock*64);
                diskModel.write(replaceBytes,0,64);
                nextBlock = nextBlock(nowBlock);
/*                System.out.println("nowBlock1:"+nowBlock);
                System.out.println("nextBlock1:"+nextBlock);*/
            }
            nextBlock = firstBlock();
            setFat(nowBlock,nextBlock);
            for(;a<coutBlock;a++){
                nowBlock = nextBlock;
                setFat(nowBlock,-1);//磁盘分配表修改完成
                nextBlock = firstBlock();
                setFat(nowBlock,nextBlock);//磁盘分配表修改完成

                int len =contextBytes.length-a*64;
                if(len>64){
                    len = 64;
                }
                System.arraycopy(new byte[64],0,replaceBytes,0,64);
                System.arraycopy(contextBytes,a*64,replaceBytes,0,len);
                diskModel.seek(nowBlock*64);
                diskModel.write(replaceBytes,0,64);
/*                System.out.println("nowBlock1:"+nowBlock);
                System.out.println("nextBlock1:"+nextBlock);*/
            }
            System.out.println("修改磁盘完成");
            setFat(nowBlock,-1);
            return true;
        }



    }
    /*
    更改属性
     */
    public int changeAttrOp(TreeItem<DirectoryItem> directoryItem,String name,boolean isOnlyRead,int typeOfFile) throws IOException {
        if(name.getBytes().length>3){
            System.out.println("命名过长不可使用");
            return 1;
        }
        TreeItem<DirectoryItem> parentDir = directoryItem.getParent();
        byte[] buffer = new byte[64];
        byte[] bytes = new byte[8];
        char comChar[] = new char[3];
        directoryItem.getValue().getactFileName().getChars(0,directoryItem.getValue().getactFileName().length(),comChar,0);
        String comStr = new String(comChar);
        int startBlock = parentDir.getValue().getNumOfStartDisk();


                //directoryItem.getValue().getactFileName();
        diskModel.seek(startBlock*64);
        diskModel.read(buffer,0,buffer.length);
/*        System.out.println("comName:"+comStr);
        System.out.println("comlen:"+comStr.length());*/
        for (int i=0;i<64;i+=8){
            String nowName = new String(buffer,i,3);
           // System.out.println("nowName:"+nowName.toString());
            if(comStr.equals(nowName)){
                //System.out.println("字符串相同");
                System.arraycopy(new byte[3],0,buffer,i,3);
                System.arraycopy(name.getBytes(),0,buffer,i,name.getBytes().length);
/*                System.out.println("buffer0"+buffer[i]);
                System.out.println("buffer1"+buffer[i+1]);
                System.out.println("buffer2"+buffer[i+2]);*/
                if (typeOfFile==1){
                    buffer[i+5]|=4;
                }
                else if (typeOfFile==2){
                    buffer[i+5]|=11;
                }
                if (isOnlyRead){
                    buffer[i+5]|=1;
                }

                System.arraycopy(buffer,i,bytes,0,8);
                directoryItem.getValue().setBytes(bytes);
                diskModel.seek(startBlock*64+i);
                diskModel.write(bytes,0,bytes.length);
/*                System.out.print("bytename");
                System.out.print(bytes[0]);
                System.out.print(bytes[1]);
                System.out.print(bytes[2]);
                System.out.println(bytes[5]);*/
                System.out.println("写入磁盘更改属性成功！");
                return 3;
            }

        }
        System.out.println("未找到相应文件");
        return 2;
    }

    /*
    删除文件
     */
    public boolean delOp(TreeItem<DirectoryItem> directoryItem) throws IOException {
        TreeItem<DirectoryItem> parentDir = directoryItem.getParent();
        byte[] buffer = new byte[64];
        int startBlock = parentDir.getValue().getNumOfStartDisk();
        diskModel.seek(startBlock*64);
        diskModel.read(buffer,0,buffer.length);
        //System.out.println("startBlock:"+startBlock);
        String comName = directoryItem.getValue().getactFileName();
        //System.out.println("comName:"+comName);
        for (int i=0;i<64;i+=8){
            //System.out.println("buffer i:"+i);

            String nowName = new String(buffer,i,3);
           // System.out.println("nowName:"+nowName);
            if(nowName.equals(comName)){
                for (int j=i;j<i+8;j++){
                    buffer[j] = 0;
                }
                diskModel.seek(startBlock*64);
                diskModel.write(buffer,0,buffer.length);
                System.out.println("删除目录项成功");
                break;
            }
        }
        int nowBlock = directoryItem.getValue().getNumOfStartDisk();
        int nextBlock = directoryItem.getValue().getNumOfStartDisk();
        //System.out.println("nowBlock:"+nowBlock);
        //System.out.println("nextBlock:"+nextBlock);
        //System.out.println(directoryItem.getValue().getLengthOfFile());
       for (int i=0;i<directoryItem.getValue().getLengthOfFile();i++){

           diskModel.seek(nowBlock);
           nextBlock = diskModel.readByte();
           setFat(nowBlock,0);
           nowBlock = nextBlock;
         //  System.out.println("我进来了");
       }
        System.out.println("修改分配表成功");
        return true;
    }



    /*
    搜索父目录里面的空位置，返回第一个空位置
     */
    public int emptyInDir(int parentBlock) throws IOException {
        byte[] buffer = new byte[64];
        diskModel.seek(parentBlock*64);
        diskModel.read(buffer,0,64);
        System.out.println("父节点磁盘的位置："+parentBlock);
        for(int i=0;i<64;i+=8) {
            if (buffer[i] == 0) {
                System.out.println("找到空位置了");
                System.out.println("i的位置：" + i);
                return i;
            }
            System.out.println("i"+i+"内容："+buffer[i]);
        }
            System.out.println("没找到空位置");
            return -1;

    }

    /*
    搜索磁盘第一个空闲的盘块
     */
    public int firstBlock() throws IOException {
        byte[] f = getFat();
        for (int i=0;i<f.length;i++)
            if (f[i] == 0) {
                return i;
            }
        return -1;
    }
    /*
    搜索当前磁盘连接的下一个磁盘
     */
    public int nextBlock(int nowBlock) throws IOException {
        int next ;
        diskModel.seek(nowBlock);
        next = (int)diskModel.readByte();
        return next;
    }
    /*
    搜索空磁盘的数量
     */
    public  int coutBlock() throws IOException {
        int cout = 0;
        byte[] f = getFat();
        for (int i=0;i<f.length;i++){
            if (f[i]==0){
                cout++;
            }
        }
        return cout;
    }

    /*
    修改磁盘分配表
     */
    public void setFat(int nowBlock,int nextBlock) throws IOException {

        if (nowBlock>256||nextBlock>256){
            System.out.println("磁盘号出错");
            return;
        }
        diskModel.seek(nowBlock);
        diskModel.writeByte(nextBlock);
        contextController.updateFatView();
    }

    /*
    清空磁盘
     */
    public void clearDisk() throws IOException {
        for(int i=2;i<64*128;i++){
            diskModel.seek(i);
            diskModel.writeByte(0);
        }
        contextController.updateFatView();
    }
    /*
    查看disk文件里的所有
     */
    public void printDisk() throws IOException {
/*        byte[] fat = getFat();
        for (int i=2;i<128;i++){
            if (fat[i]!=0){
                System.out.println("---------------------------");
                System.out.println("当前磁盘号"+i);
                System.out.println("该磁盘内容：");
                byte[] context = new byte[64];
                diskModel.seek(i*64);
                diskModel.read(context,0,64);
                for (int j=0;j<64;j++){
                    System.out.println(context[j]);
                }
                System.out.println("---------------------------");
            }
        }*/
        System.out.println("读取结束");
    }
    //G&S

    public RandomAccessFile getDiskModel() {
        return diskModel;
    }

    public void setDiskModel(RandomAccessFile diskModel) {
        this.diskModel = diskModel;
    }

    public controller.contextController getContextController() {
        return contextController;
    }

    public void setContextController(controller.contextController contextController) {
        this.contextController = contextController;
    }

}
