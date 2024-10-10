package com.mypro.clouddisk.hdfs;

import com.mypro.clouddisk.model.FileIndex;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface IFileSystem {
    //ls 列出目录和文件
    List<FileIndex> ls(String dir) throws Exception;

    //创建目录
    void mkdir(String dir) throws Exception;

     //删除文件或目录，返回父亲目录
    String rm(String path) throws Exception;

    //上传
    void upload(String localFilePath, String hdfsPath) throws Exception;

   //下载
    void download(String hdfsPath, String localFilePath) throws Exception;

    //移动
    void mv(String sourcePath, String targetPath) throws IOException, URISyntaxException, InterruptedException;

    //重命名
    String[] rename(String path, String dirName) throws Exception;

    //获取文件名
    String getFileName(String path);

    //查询出当前fileIndexId可转移的目录集合，当前用户下该目录以及该目录的子目录不可选
    List getOptionTranPath(String path, String parentpath);

    List<FileIndex> searchFileByPage(String keyWord, int pageSize, int pageNum) throws Exception;

    List<String> getDiectory(String parentpath);

    //    Map classifyFilesByType(String s) throws IOException, URISyntaxException, InterruptedException;
    List<String> getAllPathsInHdfs() throws IOException, URISyntaxException, InterruptedException;

    void getAllPaths(Path path, FileSystem fs, List<String> pathsList) throws IOException;

    Map<String, FileTypeStats> classifyFilesByTypeWithStats(String hdfsRootPath) throws IOException, URISyntaxException, InterruptedException;

    Long hdfsStorageSize();
    List<String> getUsersInfo() throws IOException, URISyntaxException, InterruptedException;

}