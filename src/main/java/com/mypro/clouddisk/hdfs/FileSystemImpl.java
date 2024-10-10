package com.mypro.clouddisk.hdfs;

import com.mypro.clouddisk.model.FileIndex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.mypro.clouddisk.databasecon.SqlConn.selectFromUser;

@Component
public class FileSystemImpl implements IFileSystem {
    //记录一些信息、调试消息或错误日志
    Logger logger = LoggerFactory.getLogger(FileSystemImpl.class);

    //hdfs文件系统路径
    @Value("${hadoop.namenode.rpc.url}")
    private String namenodeRpcUrl;
    private String currentUser;

    @Autowired
    private HttpServletRequest request;
    //获取会话中的用户名并赋给currentUser
    public String setCurrentUserFromSession() {
        currentUser = (String) request.getSession().getAttribute("name");
        return currentUser;
    }

    //获取到HDFS文件系统
    private FileSystem getFileSystem(String namenodeRpcUrl) throws IOException, URISyntaxException, InterruptedException {
        //Hadoop配置信息的持有者
        Configuration conf = new Configuration();
        //将String namenodeRpcUrl解析为URL对象
        URI uri = new URI(namenodeRpcUrl);
        //设置Hadoop文件系统的默认地址
        conf.set("fs.defaultFS", namenodeRpcUrl);
        //连接到给定URI表示的文件系统，并且使用currentUser用户
        return FileSystem.get(uri, conf, setCurrentUserFromSession());
    }

    //列出指定路径dir下的文件及目录
    @Override
    public List<FileIndex> ls(String dir) throws Exception{
        FileSystem fs = getFileSystem(namenodeRpcUrl); // 使用封装的方法获取文件系统对象FileSystem

        Path path = new Path(dir);
        //如果不存在，记录错误信息到日志中，并抛出异常
        if(! fs.exists(path)){
            logger.error("dir:"+dir+" not exists!");
            throw new RuntimeException("dir:"+dir+" not exists!");
        }

        List<FileIndex> list = new ArrayList<FileIndex>();
        FileStatus[] filesStatus = fs.listStatus(path);  //将给定目录下的所有文件（或目录）存储在一个列表中
        /**遍历给定目录下的文件（或目录），将它们的基本信息（如是否为文件、名称、路径、创建时间等）
         * 封装到FileIndex对象中,，并将这些对象存储在一个列表中返回
         */
        for(FileStatus f:filesStatus){
            FileIndex fileIndex = new FileIndex();   //针对每个文件（或目录），创建一个FileIndex对象，用于存储文件（或目录）的相关信息
            fileIndex.setIsFile(f.isDirectory()?"否":"是");
            fileIndex.setName(f.getPath().getName());
            fileIndex.setPath(f.getPath().toUri().getPath());
            fileIndex.setCreateTime(new Date());
            fileIndex.setOwner(f.getOwner());

            list.add(fileIndex);
        }

        //不需要再操作FileSystem了，关闭
        fs.close();

        return list;
    }

    public List<String> getUsersInfo() throws IOException, URISyntaxException, InterruptedException {
        FileSystem fs = getFileSystem(namenodeRpcUrl);
        List<String> users = new ArrayList<>();
        FileStatus[] fileStatuses = fs.listStatus(new Path("/user"));

        //遍历文件
        for (FileStatus status : fileStatuses) {
            if (status.isDirectory()) {
                users.add(status.getPath().getName()); // 添加目录路径
            }
        }

        Set<String> mergedSet = new HashSet<>();
        mergedSet.addAll(users);
        mergedSet.addAll(selectFromUser());
        List<String> newusers = new ArrayList<>(mergedSet);
        newusers.remove("root");

        return newusers;
    }

    @Override
    public void mkdir(String dir) throws Exception{
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        fs.mkdirs(new Path(dir));

        //不需要再操作FileSystem了，关闭client
        fs.close();

        System.out.println( "mkdir "+dir+" Successfully!" );

    }

    @Override
    /**
     * 删除文件或目录
     */
    public String rm(String path) throws Exception {
        FileSystem fs = getFileSystem(namenodeRpcUrl);
        //源文件路径
        Path filePath = new Path(path);

        //将要删除的文件移到recyleBin目录下
        mv(path,"/recycleBin");
        //删除源文件路径
        fs.delete(filePath,true);

        //不需要再操作FileSystem了，关闭client
        fs.close();

        System.out.println( "Delete "+path+" Successfully!" );

        return filePath.getParent().toUri().getPath();
    }

    public void upload(String localFilePath, String hdfsPath) throws IOException, URISyntaxException, InterruptedException {
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        Path srcPath=new Path(localFilePath);

        File file = new File(localFilePath);
        long fileSize = file.length(); // 获取文件大小

        Path destPath=new Path(hdfsPath);
        try{
            fs.copyFromLocalFile(srcPath,destPath);
        }catch (IOException e){
            System.out.println("上传失败！");
        }
    }

    public void download(String hdfsPath, String localFilePath) throws IOException, URISyntaxException, InterruptedException {
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        Path srcPath=new Path(hdfsPath);
        Path destPath=new Path(localFilePath);
        try{
            fs.copyToLocalFile(false,srcPath,destPath,true);
        }catch (IOException e){
            System.out.println("下载失败！");
        }
    }


    @Override
    public void mv(String sourcePath, String targetPath) throws IOException, URISyntaxException, InterruptedException {
        FileSystem fs = getFileSystem(namenodeRpcUrl);
        try {
            // 创建源路径和目标路径
            Path srcPath = new Path(sourcePath);
            Path dstPath = new Path(targetPath);

            // 执行移动操作
            fs.rename(srcPath, dstPath);
        } catch (IOException e) {
            System.out.println("转移失败！");
        }
    }
    @Override
    public String[] rename(String path, String dirName) throws Exception {
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        Path oldPath = new Path(path);
        if(oldPath.getParent() ==null){
            String[] arr = new String[2];
            arr[0]="/";
            arr[1]="/";
            return arr;
        }
        String parentPath = oldPath.getParent().toUri().getPath();

        String newPathStr = namenodeRpcUrl + parentPath + dirName;

        Path newPath = new Path(newPathStr );
        fs.rename(oldPath,newPath);

        //不需要再操作FileSystem了，关闭client
        fs.close();

        String[] arr = new String[2];
        arr[0]=parentPath;
        arr[1]=newPathStr;

        System.out.println( "rename Successfully!" );

        return arr;
    }

    @Override
    public String getFileName(String path) {
        Path filePath = new Path(path);
        return filePath.getName();
    }

    @Override
    public List<String> getOptionTranPath(String path,String parentpath) {
        List<String> hdfsDirectories = new ArrayList<>();

        try {
            // 获取HDFS文件系统
            FileSystem fs = getFileSystem(namenodeRpcUrl);

            // 定义要遍历的路径
            Path hdfsPath = new Path("/");

            // 递归遍历获取目录
            exploreHdfsDirectories(fs, hdfsPath, hdfsDirectories,path);

            // 关闭文件系统连接
            fs.close();

            // 去除HDFS的URL部分，只保留目录路径部分
            for (int i = 0; i < hdfsDirectories.size(); i++) {
                String fullPath = hdfsDirectories.get(i);
                hdfsDirectories.set(i, fullPath.substring(fullPath.indexOf("/", fullPath.indexOf("/", 0) + 2)));
            }
        } catch (IOException e) {
            System.out.println("文件读写错误！");
        } catch (URISyntaxException e) {
            System.out.println("URL无效！");
        } catch (InterruptedException e) {
            System.out.println("线程中断！");
        }
        for (int i = 0; i < hdfsDirectories.size(); i++) {
            //除去当前目录
            if(Objects.equals(hdfsDirectories.get(i), parentpath)){
                hdfsDirectories.remove(i);
                i--;
            }
            //除去其子目录
            if(hdfsDirectories.get(i).contains(path)){
                hdfsDirectories.remove(i);
                i--;
            }
        }
        hdfsDirectories.add(0,"/");
        return hdfsDirectories;
    }

    private void exploreHdfsDirectories(FileSystem fs, Path path, List<String> directories,String excludePath) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                String directoryPath = fileStatus.getPath().toString();
                directories.add(directoryPath);
                // 递归遍历子目录，但排除指定目录的子目录
                if (!directoryPath.startsWith(excludePath)) {
                    exploreHdfsDirectories(fs, fileStatus.getPath(), directories, excludePath);
                }
            }
        }
    }


    @Override
    public List<String> getDiectory(String parentpath) {
        List<String> hdfsDirectories = new ArrayList<>();

        try {
            // 获取HDFS文件系统
            FileSystem fs = getFileSystem(namenodeRpcUrl);

            // 定义要遍历的路径
            Path hdfsPath = new Path(parentpath);

            // 递归遍历获取目录
            exploreDirectories(fs, hdfsPath, hdfsDirectories);

            // 关闭文件系统连接
            fs.close();

            // 去除HDFS的URL部分，只保留目录路径部分
            for (int i = 0; i < hdfsDirectories.size(); i++) {
                String fullPath = hdfsDirectories.get(i);
                hdfsDirectories.set(i, fullPath.substring(fullPath.indexOf("/", fullPath.indexOf("/", 0) + 2)));
            }
        }  catch (IOException e) {
            System.out.println("文件读写错误！");
        } catch (URISyntaxException e) {
            System.out.println("URL无效！");
        } catch (InterruptedException e) {
            System.out.println("线程中断！");
        }
        hdfsDirectories.add(0,"/");
        return hdfsDirectories;
    }

    private void exploreDirectories(FileSystem fs, Path path, List<String> directories) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                String directoryPath = fileStatus.getPath().toString();
                directories.add(directoryPath);
            }
        }
    }




    public List<String> getAllPathsInHdfs() throws IOException, URISyntaxException, InterruptedException {
        List<String> pathsList = new ArrayList<>();

        // 获取HDFS文件系统实例
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        // 递归获取所有路径
        getAllPaths(new Path("/"), fs, pathsList);

        // 去除HDFS的URL部分，只保留目录路径部分
        for (int i = 0; i < pathsList.size(); i++) {
            String fullPath = pathsList.get(i);
            pathsList.set(i, fullPath.substring(fullPath.indexOf("/", fullPath.indexOf("/", 0) + 2)));
        }

        // 关闭FileSystem连接
        fs.close();

        return pathsList;
    }

    public void getAllPaths(Path path, FileSystem fs, List<String> pathsList) throws IOException {
        FileStatus[] statuses = fs.listStatus(path);

        for (FileStatus status : statuses) {
            if (status.isDirectory()) {
                pathsList.add(status.getPath().toString()); // 添加目录路径
                getAllPaths(status.getPath(), fs, pathsList); // 递归获取子目录路径
            } else {
                pathsList.add(status.getPath().toString()); // 添加文件路径
            }
        }
    }
    @Override
    public List<FileIndex> searchFileByPage(String keyWord, int pageSize, int pageNum) throws Exception{
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        ArrayList<FileStatus> results = null;
        PathFilter filter = new PathFilter() {
            @Override
            public boolean accept(Path path) {
                if(keyWord == null || keyWord.trim().isEmpty()){
                    return false;
                }
                if(path.getName().contains(keyWord)){
                    return  true;
                }
                return false;
            }
        };


        List<FileIndex> list = new ArrayList<FileIndex>();

        List<String> paths=getAllPathsInHdfs();

        String hdfsPath=null;
        for (String o : paths) {
            String filename = o.substring(o.lastIndexOf("/") + 1);
            if (Objects.equals(keyWord, filename)) {
                hdfsPath = o;
                break;
            }

        }
        if (hdfsPath == null) {
            return list;
        }
        String parentPath = hdfsPath.substring(0, hdfsPath.lastIndexOf("/"))+"/";

        FileStatus[] fileStatusArr = fs.listStatus(new Path(parentPath),filter);

        FileIndex fileIndex = new FileIndex();

        for(FileStatus status :fileStatusArr){
            fileIndex.setName(status.getPath().getName());
            fileIndex.setPath(status.getPath().toUri().getPath());
            fileIndex.setIsFile(status.isFile()?"是":"否");
            fileIndex.setOwner(status.getOwner());
            list.add(fileIndex);
        }
        //不需要再操作FileSystem了，关闭client
        fs.close();

        System.out.println( "Search Successfully!" );

        return list;
    }

    //获取到各种文件类型文件的总大小，并返回一个Map(文件类型，FileTypeStats对象)
    public Map<String, FileTypeStats> classifyFilesByTypeWithStats(String hdfsRootPath) throws IOException, URISyntaxException, InterruptedException {
        FileSystem fs = getFileSystem(namenodeRpcUrl);

        Map<String, FileTypeStats> fileStatsByType = new HashMap<>();
        traverseAndClassifyFilesWithStats(fs, new Path(hdfsRootPath), fileStatsByType);

        fs.close();
        return fileStatsByType;
    }

    //统计hdfs上所有类别文件的大小
    private static void traverseAndClassifyFilesWithStats(FileSystem fs, Path path, Map<String, FileTypeStats> fileStatsByType) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(path);
        long size=0;

        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                traverseAndClassifyFilesWithStats(fs, fileStatus.getPath(), fileStatsByType);
            } else {
                String fileName = fileStatus.getPath().getName();
                String fileType = classifyFileType(fileName);

                if (fileType != null) {
                    long fileSize = fileStatus.getLen();
//                    size +=fileSize;

                    //如果fileType在FileTypeStats类中存在，返回键为fileType的FileTypeStats类
                    FileTypeStats stats = fileStatsByType.getOrDefault(fileType, new FileTypeStats(fileType));
                    stats.incrementFileCount();
                    String sumSize=stats.addToTotalSize(fileSize);
//                    System.out.println(fileType+" "+fileSize+" "+sumSize);

                    fileStatsByType.put(fileType, stats);
                }
            }
        }
    }

    //给所有文件分类
    private static String classifyFileType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        if (fileExtension.equals("txt") || fileExtension.equals("doc") || fileExtension.equals("pdf")) {
            return "文本";
        } else if (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov")) {
            return "视频";
        } else if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("gif")) {
            return "图片";
        } else if (fileExtension.equals("java") || fileExtension.equals("cpp") || fileExtension.equals("py") || fileExtension.equals("sh")) {
            return "代码";
        } else {
            return "其他";
        }
    }

    public Long hdfsStorageSize() {
        long totalSize = 0;
        try {
            FileSystem fs = getFileSystem(namenodeRpcUrl);

            Path path = new Path("/");
            totalSize = fs.getContentSummary(path).getSpaceConsumed();

            System.out.println("Total HDFS storage size: " + totalSize + " bytes");
        } catch (Exception e) {
            System.out.println("无法获取hdfs总空间大小！");
        }
        return totalSize;
    }

}
