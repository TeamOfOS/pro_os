package model.fileuser;


import controller.ChangFileAttrController;
import controller.ChangeDirAttrController;
import controller.EditController;
import controller.contextController;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/*
该类是文件管理的方法
 */
public class OpenOperator {
     public  List<DirectoryItem> directoryItems =new ArrayList<>();
     private contextController contextControllers ;
     private ChangFileAttrController changFileAttrController;
     private ChangeDirAttrController changeDirAttrController;//改变目录属性界面
     private EditController editController;//打开编辑界面

     /*
     构造方法初始化
      */
    public OpenOperator(){
    }

     /*
     初始化directoryItems的文件树
      */
     public List<DirectoryItem> createDirectoryItems(){
         //缺少读取磁盘的方法，生成目录项列表
         //public List<DirectoryItem> getdirectoryItemList()
         //this.directoryItems = getdirectoryItemList();
         DirectoryItem root = null;

         if (this.directoryItems.isEmpty()){
             //如果目录项列表为空

             //生成一个根目录
              root = new DirectoryItem(0,"root",true,false,null);
               //缺少写入磁盘方法，写入一个目录项并修改它的起始盘号和文件长度
               //public void writeDisk(DirectoryItem root);
             //生成一个包含随机执行文件的目录
             DirectoryItem randomFile = new DirectoryItem(0,"root/random",true,false,null);
               //缺少写入磁盘方法，写入一个目录项并修改它的起始盘号和文件长度
               //public void writeDisk(DirectoryItem root);
             directoryItems.add(root);
             directoryItems.add(randomFile);
             for(int i=0;i<10;i++){//生成10个随机文件
                 String fName = "root/random/ex";
                 String attr = ".e";
                 DirectoryItem exeFile = new DirectoryItem(2,fName+'0'+i+attr,true,false,null);
                 exeFile.setFileContext(createRandomContext());
                 //缺少生成随机文件内容的方法
                 //public
                 //缺少写入磁盘方法，写入一个目录项并修改它的起始盘号和文件长度包括内容也要写入磁盘
                 //public void writeDisk(DirectoryItem root);
                 directoryItems.add(exeFile);


             }
         }
         else{
             //不为空，有了根目录和随机文件目录以及10个随机文件
         }

         return this.directoryItems;
     }

     /*
     随机生成系统执行文件文本内容
      */
    public String createRandomContext(){
        String context = null;
        String strEnd = "end";//str结束指令
        String strX = "x";//str x变量
        String strAdd = "+";//str +
        String strReduce ="-";//str -
        String strEnter = "\n";//str 回车
        String strEx = "!";//str ！
        String strEq = "=";//str =
        String[] strDev = {"A","B","C"};//str A，B，C
        int x = (int)(Math.random()*100)%92+8;//x变量
        int choiceInstruction = 0;//选择x加减指令还是设备指令
        int choiceBetweenAandR = 0;//选择加法还是减法
        int choiceOfDev =0;//设备选择
        int time =(int) (Math.random()*10);
        context = strX+strEq+x+strEnter;//x=?指令
        for (int i =0;i<8;i++){
            choiceInstruction = (int)(Math.random()*10)%2;
            if (choiceInstruction==0){
                //加减法
                choiceBetweenAandR = (int)(Math.random()*10)%2;
                if (choiceBetweenAandR==0){
                    //减法
                    context +=strX+strReduce+strReduce+strEnter;
                }
                else {
                    context +=strX+strAdd+strAdd+strEnter;
                    //加法
                }
            }
            else {
                time =(int) (Math.random()*10);
                choiceOfDev = (int)(Math.random()*10)%3;
                String secDev = strDev[choiceOfDev];
                context += strEx+secDev+time+strEnter;

                //设备
            }
        }
        context+=strEnd+strEnter;
        return context;

    }
    //菜单建立目录操作
    public boolean createDir(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        String path = contextControllers.getNeedPath(sec);
        String fileName = contextControllers.getCreateName(sec.getValue(),0);
        String actPath = path+'/'+fileName;
        DirectoryItem newDir = new DirectoryItem(0,actPath,false,false,null);
        newDir.setLengthOfFile(0);
        //写入磁盘，修改磁盘号和磁盘长度
        if (true)//此处为写入磁盘是否成功，写入磁盘方法
        {
            directoryItems.add(newDir);
            if (sec.getValue().isDirectory()) {
                contextControllers.addTreeItem(sec.getValue(),newDir);
            }
            else{
                contextControllers.addTreeItem(sec.getParent().getValue(),newDir);
            }
        }
        else {
          return false;
        }
        return true;
    }


    //菜单建立普通文本操作
    public boolean createTxt(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        String path = contextControllers.getNeedPath(sec);
        String fileName = contextControllers.getCreateName(sec.getValue(),1);
        String actPath = path+'/'+fileName+".t";
        DirectoryItem newDir = new DirectoryItem(1,actPath,false,false,null);
        //写入磁盘，修改磁盘号和磁盘长度
        if (true)//此处为写入磁盘是否成功，写入磁盘方法
        {
            directoryItems.add(newDir);
            if (sec.getValue().isDirectory()) {
                contextControllers.addTreeItem(sec.getValue(),newDir);
            }
            else{
                contextControllers.addTreeItem(sec.getParent().getValue(),newDir);
            }
        }
        else {
            return false;
        }
        return true;
    }


    //菜单建立执行文件操作
    public boolean createExe(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        String path = contextControllers.getNeedPath(sec);
        String fileName = contextControllers.getCreateName(sec.getValue(),2);
        String actPath = path+'/'+fileName+".e";
        DirectoryItem newDir = new DirectoryItem(2,actPath,false,false,null);
        //写入磁盘，修改磁盘号和磁盘长度
        if (true)//此处为写入磁盘是否成功，写入磁盘方法
        {
            directoryItems.add(newDir);
            if (sec.getValue().isDirectory()) {
                contextControllers.addTreeItem(sec.getValue(),newDir);
            }
            else{
                contextControllers.addTreeItem(sec.getParent().getValue(),newDir);
            }
        }
        else {
            return false;
        }
        return true;
    }


    //菜单编辑操作
    public void edit(){
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
         String strOfSystem = "系统文件";
         String strOfOnlyRead = "只读";
         String strOfRAndW = "可读可写";
         String strOfReadAttr = null;
         String fileName = sec.getValue().getactFileName();
         String strOfAttr = null;
         String info = null;
         if(sec.getValue().getTypeOfFile()==1){
             strOfAttr = ".t";
         }
         else if(sec.getValue().getTypeOfFile()==2){
             strOfAttr = ".e";
        }
         if (sec.getValue().isOnlyRead()){
             strOfReadAttr = strOfOnlyRead;
             editController.getContextArea().setEditable(false);
         }
         else{
             strOfReadAttr = strOfRAndW;
             editController.getContextArea().setEditable(true);
         }
         if(sec.getValue().isSystemFile()){
             info = fileName+strOfAttr+"("+strOfReadAttr+","+strOfSystem+")";
         }
         else{
             info = fileName+strOfAttr+"("+strOfReadAttr+")";
         }
         editController.getTextOfInfo().setText(info);
        editController.getContextArea().setText(sec.getValue().getFileContext());
        editController.getEditStage().show();
    }


    //菜单删除操作
    //0表示正确，1表示目录为非空目录删除失败，2表示修改磁盘失败,3表示是系统文件不可以删除
    public int del(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        if(sec.getValue().isSystemFile()){
            return 3;
        }
        if(sec.getValue().getTypeOfFile()==0){
            if(sec.getChildren().isEmpty()){
                    if(true){
                        //在磁盘中删去选中节点，方法缺失
                        delNode(sec);
                        return 0;
                    }
                    else {
                        return 2;
                    }

            }
            else {
                return 1;
            }
        }
        else{
            if(true){
                //在磁盘中删去选中节点，方法缺失
                delNode(sec);
                return 0;
            }
            else{
                return 2;
            }
        }

    }

    //删除节点方法
    public void delNode(TreeItem<DirectoryItem> sec){
         contextControllers.deleteTreeItem(sec.getValue());
         //System.out.println(directoryItems.size());
         for(int a=0;a<directoryItems.size();a++){
             if(directoryItems.get(a).equals(sec.getValue())){
                 directoryItems.remove(a);
                 System.out.println("移除成功");
             }
         }

    }

    //菜单运行操作
    public void run(){
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        System.out.println(sec.getValue().getFileName()+" 放入内存啦");
    }


    //菜单复制操作
    public void copy(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        contextControllers.setCopyItem(sec.getValue());

    }


    //菜单粘贴操作
    //返回0正确，1没有复制，2写入磁盘失败
    public int paste(){
        DirectoryItem copyItem = contextControllers.getCopyItem();
        if(copyItem.getTypeOfFile()==-1){
            return 1;
        }
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
         TreeItem<DirectoryItem> copyTreeItem = contextControllers.findTreeItem(contextControllers.getDirectoryItemTreeView().getRoot(),copyItem);
         int num = 1;
         String newName = copyItem.getactFileName()+"-("+num+")";
         String pastePath = contextControllers.getNeedPath(sec);
         String pasteAttr =null;
         switch (copyItem.getTypeOfFile()){
             case 1:pasteAttr = ".t";
             case 2:pasteAttr = ".e";
         }
        List<TreeItem<DirectoryItem>> childrenItems =new ArrayList<>();
         switch (sec.getValue().getTypeOfFile()){
             case 0:childrenItems = sec.getChildren();break;
             case 1:childrenItems = sec.getParent().getChildren();break;
             case 2:childrenItems = sec.getParent().getChildren();break;
         }

         boolean flag = true;
         boolean isEq = false;
         while (flag){
             isEq = false;
             for(TreeItem<DirectoryItem> t:childrenItems){
                 if(t.getValue().getactFileName().equals(newName)){
                     isEq = true;
                     break;
                 }
             }
             if(isEq){
                 num++;
                 newName = copyItem.getactFileName()+"-("+num+")";
             }
             else {
                 flag = false;
             }
         }
         System.out.println(newName);
         DirectoryItem pasteItem = new DirectoryItem(copyItem.getTypeOfFile(),pastePath+'/'+newName+pasteAttr,false,copyItem.isOnlyRead(),copyItem.getFileContext());
        System.out.println(pasteItem.getFileName());
        System.out.println(pasteItem.getactFileName());
         pasteItem.setLengthOfFile(copyItem.getLengthOfFile());

         //写入磁盘中
         if(true){
             if(sec.getValue().getTypeOfFile()==0){
                 contextControllers.addTreeItem(sec.getValue(),pasteItem);
                 directoryItems.add(pasteItem);
             }
             else if(sec.getValue().getTypeOfFile()==1||sec.getValue().getTypeOfFile()==2){
                 contextControllers.addTreeItem(sec.getParent().getValue(),pasteItem);
                 directoryItems.add(pasteItem);

             }
         }
         else {
             return 2;//写入磁盘失败
         }
        contextControllers.setCopyItem(new DirectoryItem());
         return 0;
    }


    //打开菜单更改属性界面操作
    public void changeAttr(){
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
         String secFileName = sec.getValue().getactFileName();
         boolean isOnlyRead = sec.getValue().isOnlyRead();

         if (sec.getValue().isSystemFile()){
             changFileAttrController.getSecondStage().setTitle("更改文件属性(系统文件)");
             changeDirAttrController.getDirAttrStage().setTitle("更改文件属性(系统文件)");
         }
         else {
             changFileAttrController.getSecondStage().setTitle("更改文件属性");
             changeDirAttrController.getDirAttrStage().setTitle("更改文件属性");
         }
        changFileAttrController.getChoiceOfAttr().getItems().addAll("普通文本(.t)","执行文件（.e)");
        changFileAttrController.getChoiceOfReadAttr().getItems().addAll("只读文件","可读可写文件");
         if (sec.getValue().getTypeOfFile()==1||sec.getValue().getTypeOfFile()==2){
             changFileAttrController.getTextOfFileName().setText(secFileName);
             if(isOnlyRead){
                 changFileAttrController.getChoiceOfReadAttr().setValue("只读文件");
             }
             else
             {
                 changFileAttrController.getChoiceOfReadAttr().setValue("可读可写文件");
             }
             if(sec.getValue().getTypeOfFile()==1){
                 changFileAttrController.getChoiceOfAttr().setValue("普通文本(.t)");

             }
             else if (sec.getValue().getTypeOfFile()==2){
                 changFileAttrController.getChoiceOfAttr().setValue("执行文件（.e)");
             }
             changFileAttrController.getSecondStage().show();
         }
         if (sec.getValue().getTypeOfFile()==0){

             changeDirAttrController.getTextOfDir().setText(secFileName);
             changeDirAttrController.getDirAttrStage().show();

         }

    }

    //保存更改文件属性界面内容操作
    public boolean saveFileAttr(){
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
         DirectoryItem item = sec.getValue();
         List<TreeItem<DirectoryItem>> secChildren = sec.getParent().getChildren();
         for(TreeItem<DirectoryItem> d:secChildren){
             if ((d.getValue().getTypeOfFile()==1&&changFileAttrController.isTxt())||(d.getValue().getTypeOfFile()==2&&!changFileAttrController.isTxt())){
                 if(d.getValue().getactFileName().equals(changFileAttrController.getSecFileName())){
                     System.out.println("有重复命名失败");
                     return false;
                 }
             }

         }
         //测试
        for (int i=0;i<directoryItems.size();i++){
            if(directoryItems.get(i).equals(sec.getValue())){
                System.out.println("存在相同的");
                System.out.println("文件名："+directoryItems.get(i).getFileName());
                System.out.println("读写属性："+directoryItems.get(i).isOnlyRead());
                System.out.println("文件类型："+directoryItems.get(i).getTypeOfFile());
                System.out.println("----------------------------------------------------");
            }
        }
         if(sec.getValue().getTypeOfFile()==2||sec.getValue().getTypeOfFile()==1){
             sec.getValue().setOnlyRead(changFileAttrController.isOnlyRead());
             if (changFileAttrController.isTxt()){
                 sec.getValue().setTypeOfFile(1);
                 sec.getValue().changeFileName(1,changFileAttrController.getSecFileName());
                 //修改磁盘方法
             }
             else {
                 sec.getValue().setTypeOfFile(2);
                 sec.getValue().changeFileName(2,changFileAttrController.getSecFileName());
                 //修改磁盘方法
             }
         }
         //测试
        for (int i=0;i<directoryItems.size();i++){
            if(directoryItems.get(i).equals(sec.getValue())){
                System.out.println("存在相同的");
                System.out.println("文件名："+directoryItems.get(i).getFileName());
                System.out.println("读写属性："+directoryItems.get(i).isOnlyRead());
                System.out.println("文件类型："+directoryItems.get(i).getTypeOfFile());
            }
        }
        contextControllers.updateTreeItem(sec.getValue());

        return true;
    }

    //保存更改目录属性界面内容操作
    public boolean saveDirAttr(){
        TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
        DirectoryItem item = sec.getValue();
        List<TreeItem<DirectoryItem>> secChildren = sec.getParent().getChildren();
        for(TreeItem<DirectoryItem> d:secChildren){
            if (d.getValue().getTypeOfFile() == 0 ){
                if(d.getValue().getactFileName().equals(changeDirAttrController.getTextOfDir().getText())){
                    System.out.println("有重复命名失败");
                    return false;
                }
            }

        }
        //修改磁盘内容方法缺失
        sec.getValue().changeFileName(0,changeDirAttrController.getTextOfDir().getText());
        contextControllers.updateTreeItem(sec.getValue());
        return true;
    }

    //保存文件内容操作
    public boolean saveEditContext(){
         TreeItem<DirectoryItem> sec = contextControllers.getSeclectNode();
         String newContext = editController.getContextArea().getText();
        //修改磁盘内容
        if(true){
            //写入磁盘成功
            sec.getValue().setFileContext(newContext);
            contextControllers.updateTreeItem(sec.getValue());
            return true;
        }
        else {
            return false;
        }

    }

    //G&&S
    public  List<DirectoryItem> getDirectoryItems() {
        return directoryItems;
    }

    public  void setDirectoryItems(List<DirectoryItem> directoryItems) {
        this.directoryItems = directoryItems;
    }

    public contextController getContextControllers() {
        return contextControllers;
    }

    public void setContextControllers(contextController contextControllers) {
        this.contextControllers = contextControllers;
    }

    public ChangFileAttrController getChangFileAttrController() {
        return changFileAttrController;
    }

    public void setChangFileAttrController(ChangFileAttrController changFileAttrController) {
        this.changFileAttrController = changFileAttrController;
    }

    public ChangeDirAttrController getChangeDirAttrController() {
        return changeDirAttrController;
    }

    public void setChangeDirAttrController(ChangeDirAttrController changeDirAttrController) {
        this.changeDirAttrController = changeDirAttrController;
    }

    public EditController getEditController() {
        return editController;
    }

    public void setEditController(EditController editController) {
        this.editController = editController;
    }


}
