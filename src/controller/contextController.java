package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.fileuser.DirectoryItem;
import model.progress.Clock;
import model.progress.PCB;
import os.OS;
import ui.PCBVo;
import ui.UIResources;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*
该类是主要内容界面的控制器类
 */
public class contextController implements Initializable {
	@FXML
	private AnchorPane clock; //时钟界面
	@FXML
	private AnchorPane cpu;   //cpu界面
	@FXML
	public AnchorPane user;  //用户界面
	@FXML
	private AnchorPane disk;  //磁盘界面
	@FXML
	private AnchorPane device; //设备界面
	@FXML
	private AnchorPane progress; //进程界面
	@FXML
	private AnchorPane memory; //内存界面
	@FXML
	private TreeView<DirectoryItem> directoryItemTreeView ; //用户目录项的目录树
	@FXML
	private Button startBtn ;//启动系统按钮
	@FXML
	private ContextMenu rightMenu; //右键菜单
	@FXML
	private MenuItem del;//删除菜单选项
	@FXML
	private MenuItem edit;//编辑菜单选项
	@FXML
	private MenuItem run;//运行菜单选项
	@FXML
	private MenuItem copy;//复制菜单选项
	@FXML
	private MenuItem paste;//粘贴菜单选项
	@FXML
	private MenuItem changeAttr;//改变属性菜单选项
	@FXML
	private MenuItem create;//建立菜单
	@FXML
	private MenuItem createDir;//建立目录菜单选项
	@FXML
	private MenuItem createTxt;//建立普通文本菜单选项
	@FXML
	private MenuItem createExe;//建立执行文件菜单选项

	@FXML
	private Text systemTimeTxt;
	@FXML
	private Text timesliceTxt;
	@FXML
	private TextArea processRunningView;
	@FXML
	private TextArea processResultView;
	@FXML
	private TableView<PCBVo> pcbQueueView;
	@FXML
	private TableColumn pidCol;
	@FXML
	private TableColumn statusCol;
	@FXML
	private TableColumn priorityCol;


	private Alert alert;//警告提示
	private DirectoryItem copyItem = new DirectoryItem();//复制项目
	private static int dirNum = 0;//命名目录数字
	private static int txtNum = 0;//命名普通文件数字
	private static int exeNum = 0;//命名执行文件数字
	private OS os;
	private UpdateUIThread updateUIThread;


	//构造方法
	public contextController() throws Exception{
		alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
	}

	/*----------------点击启动系统按钮的操作-------------------*/
	/*-------------------响应用户请求------------------------*/
	public void osSwitch() throws Exception {

		if (!os.launched) {
			launchOS();
			startBtn.setText("关闭系统");
			System.out.println("相应了吗");
			addRightMenu(this.directoryItemTreeView);
			System.out.println("相应了吗2");
		}else{
			closeOs();
			startBtn.setText("启动系统");
		}

	}
	/**
	 * 启动系统
	 */
	public void launchOS() throws Exception {
		os.launched = true;
		os.start();
		initComponent();
		new Thread(updateUIThread).start();
	}
	/**
	 * 关闭系统
	 */
	public void closeOs(){
		os.launched = false;
		os.close();

	}

	/**
	 * 实时更新正在要执行进程的信息
	 * 00和qiuyu 应该也会用到这个 在这里面加就好 （from csy）
 	 */

	private class UpdateUIThread implements Runnable {
		@Override
		public void run() {
			while (os.launched) {
				try {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {

							//更新进程执行过程视图
							contextController.this.processRunningView.appendText(os.cpu.getInstuction()+ "\n");
							contextController.this.processResultView.appendText(os.cpu.getResultOfProcess()+"\n");
							//更新系统时钟视图
							contextController.this.systemTimeTxt.setText(OS.clock.getSystemTime() + "");
							//更新时间片视图
							contextController.this.timesliceTxt.setText(OS.clock.getRestTime() + "");
							//更新进程队列视图
							List<PCB> pcbs = os.memory.getAllPCB();
							List<PCBVo> pcbVos = new ArrayList<>(pcbs.size());
							for (PCB pcb : pcbs) {
								PCBVo pcbVo = new PCBVo(pcb);
								pcbVos.add(pcbVo);
							}
							ObservableList<PCBVo> datas = FXCollections.observableList(pcbVos);
							pcbQueueView.setItems(datas);

						}
					});


					Thread.sleep(Clock.TIMESLICE_UNIT);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	/**
	 * 初始化进程队列视图
	 */
	public void initPcbQueueView() {
		pidCol.setCellValueFactory(new PropertyValueFactory<>("PID"));
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
	}

	/*
	将输出进程的队列的模块初始化
	这里面 00和qiuyu  应该也用得到 写完初始化方法往里面加就可
	 */

	public void initComponent() throws Exception {
		processRunningView.setText("");
		processResultView.setText("");
		//初始化进程队列视图
		initPcbQueueView();
		initDirectoryItemTree();//我把芳芳的移到这里了  初始化直接调用这个方法
		//可添加初始化磁盘分配等代码 （00和qiuyu）
	}


	/*
	treeView的节点增加右键菜单
	 */
     public void addRightMenu(TreeView<DirectoryItem> treeView){
		 treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			 public void handle(MouseEvent event) {
				 Node node = event.getPickResult().getIntersectedNode();

				 TreeItem<DirectoryItem> secItem = treeView.getSelectionModel().getSelectedItem();
				 node.setOnMouseClicked(e->{
					 if(e.getButton()== MouseButton.PRIMARY){
						 System.out.println("按下鼠标左键");
							 rightMenu.hide();

					 }
				 });
				 //给node对象添加下来菜单；
				 node.setOnContextMenuRequested(e->{

				 	if (secItem.getValue().getTypeOfFile() == 0){

				 		run.setDisable(true);
				 		edit.setDisable(true);
				 		copy.setDisable(true);

					}
				 	else {
						run.setDisable(false);
						edit.setDisable(false);
						copy.setDisable(false);
					}


				 });


			 }
		 });
	 }

	/*
	构建目录树方法
	 */
	public void initDirectoryItemTree(){

		List<DirectoryItem> dirItems = OS.openOperator.createDirectoryItems();
		DirectoryItem root = dirItems.get(0);
		TreeItem<DirectoryItem> rootTreeItem = new TreeItem(root);
		DirectoryItem random = dirItems.get(1);
		TreeItem<DirectoryItem> randomTreeItem = new TreeItem(random);
		directoryItemTreeView.setRoot(rootTreeItem);
		for(DirectoryItem t:dirItems){

			if(!t.equals(root)&&t.equals(random)){
				addTreeItem(root,t);
			}
			else if(!t.equals(root)&&!t.equals(random)){
				addTreeItem(random,t);
			}
			else {

			}
		}

		directoryItemTreeView.setCellFactory(new Callback<TreeView<DirectoryItem>, TreeCell<DirectoryItem>>() {
			@Override
			public TreeCell<DirectoryItem> call(TreeView<DirectoryItem> param) {
				return new TreeCell<DirectoryItem>(){
					@Override
					protected void updateItem(DirectoryItem item,boolean empty){
						super.updateItem(item,empty);
						if(empty){
							setText(null);
							setGraphic(null);
						}else{
							setText(item.getactFileName());
							if(item.getTypeOfFile()==0){
								setGraphic(UIResources.getDirectoryIcon());
							}else if(item.getTypeOfFile()==1){
								setGraphic(UIResources.gettxtFileIcon());

							}else if(item.getTypeOfFile()==2){
								setGraphic(UIResources.getexeFileIcon());

							}
						}
					}
				};
			}
		});
		directoryItemTreeView.refresh();
	}

	/*
	添加目录树节点
	 */
	public void addTreeItem(DirectoryItem parent,DirectoryItem newItem){
        TreeItem<DirectoryItem> root = directoryItemTreeView.getRoot();
        TreeItem<DirectoryItem> parentItem = findTreeItem(root,parent);
        parentItem.getChildren().add(new TreeItem<>(newItem));
        directoryItemTreeView.refresh();

	}

	/*
	删除目录树节点
	 */
	public void deleteTreeItem(DirectoryItem item){
		TreeItem<DirectoryItem> root = directoryItemTreeView.getRoot();
		TreeItem<DirectoryItem> treeItem = findTreeItem(root,item);
		if(treeItem!=null){
			treeItem.getParent().getChildren().remove(treeItem);
			directoryItemTreeView.refresh();
		}

	}

	/*
	更新目录树节点
	 */
	public void updateTreeItem(DirectoryItem item){

		TreeItem<DirectoryItem> root = directoryItemTreeView.getRoot();
		TreeItem<DirectoryItem> treeItem = findTreeItem(root,item);
		if (treeItem!=null){
			treeItem.setValue(item);
			directoryItemTreeView.refresh();
		}

	}

	/*
	从根节点开始搜索节点
	 */
	public TreeItem<DirectoryItem> findTreeItem(TreeItem<DirectoryItem> root,DirectoryItem item){
		if(root.getValue().equals(item)){
			return root;
		}
		if (root.isLeaf())
			return null;
		for (TreeItem<DirectoryItem> directoryItemTreeItem : root.getChildren()){
			TreeItem it = findTreeItem(directoryItemTreeItem,item);
			if (it!=null){
				return it;
			}
		}
		return null;
	}

	/*
	返回选中节点
	 */
	public TreeItem<DirectoryItem> getSeclectNode(){
		TreeItem<DirectoryItem> sec  = directoryItemTreeView.getSelectionModel().getSelectedItem();
		return sec;

	}

	/*
	返回创建节点的父节点路径String
	 */
	public String getNeedPath(TreeItem<DirectoryItem> sec){
		if(sec.getValue().getTypeOfFile()==0){
			return sec.getValue().getFileName();
		}
		else if(sec.getValue().getTypeOfFile()==1||sec.getValue().getTypeOfFile()==2){
			String path = sec.getValue().getFileName();
			int endNum = path.lastIndexOf('/');
			String needPath = path.substring(0,endNum);
			return needPath;
		}
		else{
			return null;
		}

	}

	/*
	创建新文件或者目录的文件名
	 */
	public String getCreateName(DirectoryItem parent,int type){
		String strType =null;
		String actNewName;
		boolean flag = true;
		int num =0;
		boolean isEq = false;
    if(type ==0){
    	strType = "dir";
    	num = dirNum;
	}
    else if(type==1){
    	strType = "text";
    	num = txtNum;

	}
    else if (type==2){
    	strType = "ex";
    	num = exeNum;

	}
    actNewName = strType+'0'+num;
    TreeItem<DirectoryItem> parentItem;
    parentItem = findTreeItem(directoryItemTreeView.getRoot(),parent);
    List<TreeItem<DirectoryItem>> childrenList =  parentItem.getChildren();
    while (flag){
    	isEq = false;
		for(TreeItem<DirectoryItem> t:childrenList){
			if(t.getValue().getTypeOfFile()==type){
				if(actNewName.equals(t.getValue().getactFileName())) {

					//flag=false;
					isEq = true;
					break;
				}
				}
			}
		if(isEq){
			num++;
			actNewName = strType+'0'+num;
		}
		else {
			flag = false;
			num++;
		}
		}
    switch (type){
		case 0:dirNum = num;break;
		case 1:txtNum = num;break;
		case 2:exeNum = num;break;
	}
    return actNewName;
	}


	//菜单建立目录操作
	public void createDir(){
	if(!os.openOperator.createDir()){
		alert.setTitle("错误提示");
		alert.setHeaderText(null);
		alert.setContentText("创建目录失败！");
		alert.show();
	}
	}

	//菜单建立普通文本操作
	public void createTxt(){
		if(!os.openOperator.createTxt()){
			alert.setTitle("错误提示");
			alert.setHeaderText(null);
			alert.setContentText("创建普通文本失败！");
			alert.show();
		}
	}

	//菜单建立执行文件操作
	public void createExe(){
		if(!os.openOperator.createExe()){
			alert.setTitle("错误提示");
			alert.setHeaderText(null);
			alert.setContentText("创建执行文件失败！");
			alert.show();
		}
	}

	//菜单删除操作
	//0表示正确，1表示目录为非空目录删除失败，2表示修改磁盘失败,3表示是系统文件不可以删除
	public void del(){
		switch (os.openOperator.del()){
			case 0:break;
			case 1:	alert.setHeaderText(null);alert.setContentText("该目录为非空目录，不可删除！");
				alert.show();break;
			case 2:	alert.setHeaderText(null);alert.setContentText("未知磁盘原因，删除失败！");
				alert.show();break;
			case 3:	alert.setHeaderText(null);alert.setContentText("该文件为系统文件不可删除！");
				alert.show();break;
		}
	}

	//菜单复制操作
	public void copy(){
		os.openOperator.copy();
		System.out.println("复制了");
	}

	//菜单粘贴操作
	//返回0正确，1没有复制，2写入磁盘失败
	public void paste(){
		switch (os.openOperator.paste()){
			case 0:break;
			case 1:alert.setContentText("没有复制文件");alert.show();break;
			case 2:alert.setContentText("未知磁盘原因，粘贴失败");alert.show();break;
		}
	}

	//菜单编辑操作
	public void edit(){
		os.openOperator.edit();
	}

	//菜单运行操作
	public void run(){
		os.openOperator.run();
		//缺少将文件放入内存的方法
	}

	//菜单更改属性操作
	public void changeAttr(){
		os.openOperator.changeAttr();
	}


	//G&&S
	public TreeView<DirectoryItem> getDirectoryItemTreeView() {
		return directoryItemTreeView;
	}

	public void setDirectoryItemTreeView(TreeView<DirectoryItem> directoryItemTreeView) {
		this.directoryItemTreeView = directoryItemTreeView;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

	public DirectoryItem getCopyItem() {
		return copyItem;
	}

	public void setCopyItem(DirectoryItem copyItem) {
		this.copyItem = copyItem;
	}

	public AnchorPane getClock() {
		return clock;
	}

	public void setClock(AnchorPane clock) {
		this.clock = clock;
	}

	public AnchorPane getCpu() {
		return cpu;
	}

	public void setCpu(AnchorPane cpu) {
		this.cpu = cpu;
	}

	public AnchorPane getUser() {
		return user;
	}

	public void setUser(AnchorPane user) {
		this.user = user;
	}

	public AnchorPane getDisk() {
		return disk;
	}

	public void setDisk(AnchorPane disk) {
		this.disk = disk;
	}

	public AnchorPane getDevice() {
		return device;
	}

	public void setDevice(AnchorPane device) {
		this.device = device;
	}

	public AnchorPane getProgress() {
		return progress;
	}

	public void setProgress(AnchorPane progress) {
		this.progress = progress;
	}

	public AnchorPane getMemory() {
		return memory;
	}

	public void setMemory(AnchorPane memory) {
		this.memory = memory;
	}

	public Button getStartBtn() {
		return startBtn;
	}

	public void setStartBtn(Button startBtn) {
		this.startBtn = startBtn;
	}

	public ContextMenu getRightMenu() {
		return rightMenu;
	}

	public void setRightMenu(ContextMenu rightMenu) {
		this.rightMenu = rightMenu;
	}

	public MenuItem getDel() {
		return del;
	}

	public void setDel(MenuItem del) {
		this.del = del;
	}

	public MenuItem getEdit() {
		return edit;
	}

	public void setEdit(MenuItem edit) {
		this.edit = edit;
	}

	public MenuItem getRun() {
		return run;
	}

	public void setRun(MenuItem run) {
		this.run = run;
	}

	public MenuItem getCopy() {
		return copy;
	}

	public void setCopy(MenuItem copy) {
		this.copy = copy;
	}

	public MenuItem getPaste() {
		return paste;
	}

	public void setPaste(MenuItem paste) {
		this.paste = paste;
	}

	public MenuItem getChangeAttr() {
		return changeAttr;
	}

	public void setChangeAttr(MenuItem changeAttr) {
		this.changeAttr = changeAttr;
	}

	public MenuItem getCreate() {
		return create;
	}

	public void setCreate(MenuItem create) {
		this.create = create;
	}

	public MenuItem getCreateDir() {
		return createDir;
	}

	public void setCreateDir(MenuItem createDir) {
		this.createDir = createDir;
	}

	public MenuItem getCreateTxt() {
		return createTxt;
	}

	public void setCreateTxt(MenuItem createTxt) {
		this.createTxt = createTxt;
	}

	public MenuItem getCreateExe() {
		return createExe;
	}

	public void setCreateExe(MenuItem createExe) {
		this.createExe = createExe;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public static int getDirNum() {
		return dirNum;
	}

	public static void setDirNum(int dirNum) {
		contextController.dirNum = dirNum;
	}

	public static int getTxtNum() {
		return txtNum;
	}

	public static void setTxtNum(int txtNum) {
		contextController.txtNum = txtNum;
	}

	public static int getExeNum() {
		return exeNum;
	}

	public static void setExeNum(int exeNum) {
		contextController.exeNum = exeNum;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
