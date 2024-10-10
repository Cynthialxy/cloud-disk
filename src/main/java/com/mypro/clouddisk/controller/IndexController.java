package com.mypro.clouddisk.controller;

import com.mypro.clouddisk.hdfs.FileTypeStats;
import com.mypro.clouddisk.hdfs.IFileSystem;
import com.mypro.clouddisk.model.FileIndex;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class IndexController {

    @Autowired
    private IFileSystem fileSystem = null;

    /**
     * 展示根目录下的目录与文件
     */
    @RequestMapping("/home")
    public String ls(String path,Model model) {
        FileIndex fileIndex = new FileIndex();

        path = (path==null || path.trim().isEmpty()) ? "/": path.trim();
        fileIndex.setPath(path);
        String fileName = fileSystem.getFileName(path);
        fileIndex.setName(fileName);
        model.addAttribute("rootDir",fileIndex);

        List<FileIndex> list = new ArrayList<FileIndex>();
        try {
            list = fileSystem.ls(path);
        } catch (Exception e) {
            System.out.println("hdfs根目录无法展示！");
        }

        model.addAttribute("rootFiles",list);

        return "home";
    }

    @RequestMapping("/")
    public String login(String path,Model model) {

        return "index";
    }

    /**
     * 注册
     */
    @RequestMapping("/register")
    public String register(String path,Model model) {
        FileIndex fileIndex = new FileIndex();

        return "register";
    }

    @RequestMapping("/registerto")
    public String registerto(String path,Model model) {
        FileIndex fileIndex = new FileIndex();

        return "registerto";
    }
    /**
     * 展示根目录下的目录与文件
     */
    @RequestMapping("/users")
    public String lsFriends(String path,Model model) throws IOException, URISyntaxException, InterruptedException {

        List<String> list = new ArrayList<>();
        list.add("root");
        List<String> listuser = fileSystem.getUsersInfo();

        list.addAll(listuser);

        model.addAttribute("userFiles",list);

        return "users";
    }

    /**
     * upload
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             String parentPath, RedirectAttributes redirectAttributes) throws IOException, URISyntaxException, InterruptedException {
        //通过classifyFilesByTypeWithStats方法获取到各种文件类型文件的总大小
        Map<String, FileTypeStats> staticResult = fileSystem.classifyFilesByTypeWithStats("/");

        long totalSize = fileSystem.hdfsStorageSize();
        long fileSize = 0;
        /**
         * 获取到各种文件类型文件的总大小
         */
        //通过entrySet()方法返回一个Map中所有的键值对
        for (Map.Entry<String, FileTypeStats> entry : staticResult.entrySet()) {
            FileTypeStats value = entry.getValue();
            long size=value.getTotalSize();
            fileSize += size;
        }
        //hdfs剩余空间
        long freeSpace = totalSize - fileSize;

        try {
            String fileName = getOriginalFilename(file.getOriginalFilename());
            String dstPath = parentPath.endsWith("/") ? (parentPath + fileName) : (parentPath + "/" + fileName);

            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);
            if (tempFile.length() <= freeSpace) {

                // 调用 FileSystemImpl 的 upload 方法
                fileSystem.upload(tempFile.getAbsolutePath(), dstPath);

                tempFile.delete();

                System.out.println("You successfully uploaded '" + file.getOriginalFilename() + "' to HDFS");
            }else{
                System.out.println("You failed uploaded '" + file.getOriginalFilename() + "' to HDFS, there is insufficient space on HDFS.");
            }
        } catch (Exception e) {
            System.out.println("文件上传失败！");
        }

        return "redirect:/home?path=" + parentPath;
    }

    /**
     * 下载
     */
    @RequestMapping("/download")
    public String download(@RequestParam("file") String hdfsPath) {
        Path filePath = null;
        try {
            filePath = new Path(hdfsPath);
            String localFilePath = "E:/";
            fileSystem.download(hdfsPath, localFilePath);

            System.out.println("File downloaded successfully from HDFS to local: " + localFilePath);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            System.out.println("下载终止!");
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
        return "redirect:/home?path=" + filePath.getParent();
    }
    /**
     * add directory
     */
    @PostMapping("/mkdir")
    public String mkdir(String directName,String parentPath) throws Exception {
        List<String> result =  fileSystem.getDiectory(String.valueOf(parentPath));

        String newDir = parentPath.endsWith("/")?("hdfs://192.168.133.102:8020"+parentPath+directName):("hdfs://192.168.133.102:8020"+parentPath+"/"+directName);
        List<String> newResult = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            String pathString = result.get(i);
            java.nio.file.Path path1 =  Paths.get(pathString);
            String fileName = "/"+ String.valueOf(path1.getFileName());  // 获取路径的最后一个部分
            newResult.add(fileName);
        }
        //修改fileindex的name和path
        //修改子目录下的path
        directName="/"+directName;
        if (newResult.contains(directName)){
            System.out.println("该目录已存在");
        }else {
            fileSystem.mkdir(newDir);
        }
        return "redirect:./home?path=" + parentPath;
    }

    /**
     *delete
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam String path) throws Exception {
        String parentPath = fileSystem.rm(path);

        //跳转到父目录
        return "redirect:./home?path=" + parentPath;
    }

    @ResponseBody
    @PostMapping("/transferForm")
    public void moveFile(@RequestParam("sourcePath") String sourcePath, @RequestParam("targetPath") String targetPath) throws IOException, URISyntaxException, InterruptedException {
        System.out.println(sourcePath);
        System.out.println(sourcePath);
        System.out.println(targetPath);
        fileSystem.mv(sourcePath, targetPath);
    }


    @ResponseBody
    @RequestMapping(value="/checkMD5",method=RequestMethod.POST)
    public String checkMD5(String md5code){
        //校验云盘中是否已存在MD5等于md5code的文件
        return "no";
    }

    @RequestMapping("renameForm")
    public String renameForm(String directName,String isRoot,String path) throws Exception {
        Path thepath=new Path(path);
        Path parentPath=thepath.getParent();

        List<String> result =  fileSystem.getDiectory(String.valueOf(parentPath));
        List<String> newResult = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            String pathString = result.get(i);
            java.nio.file.Path path1 =  Paths.get(pathString);
            String fileName = "/"+ String.valueOf(path1.getFileName());  // 获取路径的最后一个部分
            newResult.add(fileName);
        }
            //修改fileindex的name和path
            //修改子目录下的path
        directName="/"+directName;
        if (newResult.contains(directName)){
            System.out.println("该目录已存在");
            return "redirect:./home?path=" + thepath.getParent();
        }else {
            String[] arr = fileSystem.rename(path, directName);

            //判断是修改本目录名称还是修改子目录名称，本目录名称修改完依然跳转到本目录页面，修改子目录的名称的话还是跳转到本目录
            if (isRoot != null && isRoot.equals("yes")) {
                return "redirect:./home?path=" + arr[1];  //mypath
            } else {
                return "redirect:./home?path=" + arr[0];//parent
            }
        }
    }

    @RequestMapping(value = "/getOptionalPath", method = RequestMethod.POST)
    @ResponseBody
    public List<String> getOptionalPath(@RequestParam String path){
        // 如果传入的路径不为空，获取其父路径；如果为空，则赋值为根目录“/”
        String parentpath;
        if (path != null && !path.isEmpty()) {
            int lastIndex = path.lastIndexOf("/");
            parentpath = lastIndex > 0 ? path.substring(0, lastIndex) : "/";
        } else {
            parentpath = "/";
        }

        //查询出当前fileIndexId可转移的目录集合，当前用户下该目录以及该目录的子目录不可选
        List<String> result =  fileSystem.getOptionTranPath(path,parentpath);
        return result;

    }

    @RequestMapping("/searchFiles")
    public String searchFiles(String keyWord,@RequestParam(value="pageNum",defaultValue="1")int pageNum,Model model) throws Exception {
        int pageSize = 3;
        List<FileIndex> result = fileSystem.searchFileByPage(keyWord,pageSize,pageNum);

        com.github.pagehelper.Page<FileIndex> page = new com.github.pagehelper.Page<>();
        page.addAll(result);
        model.addAttribute("result", page);
        model.addAttribute("keyWord",keyWord);
        return "searchResult";
    }
    @RequestMapping("/locationFiles")
    public ResponseEntity<String> localtionFiles(String keyWord, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, Model model) {
        try {
            int pageSize = 3;
            List<FileIndex> result = fileSystem.searchFileByPage(keyWord, pageSize, pageNum);
            String path = result.isEmpty() ? "/defaultPath" : result.get(0).getPath();

            // 添加路径到model中
            model.addAttribute("path", path);

            return ResponseEntity.ok(path); // 返回路径给前端
        } catch (Exception e) {
            System.out.println("定位失败！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
    @RequestMapping("/stasticFiles")
    public String stasticFiles(Model model) throws IOException, URISyntaxException, InterruptedException {

        //通过classifyFilesByTypeWithStats方法获取到各种文件类型文件的总大小
        Map<String, FileTypeStats> staticResult = fileSystem.classifyFilesByTypeWithStats("/");

        long totalSize = fileSystem.hdfsStorageSize();
//        System.out.println("There is " + totalSize + " on hdfs filesystem.");

        /**
         * 测试获取到的各种文件类型文件的总大小
         */
        //通过entrySet()方法返回一个Map中所有的键值对
        for (Map.Entry<String, FileTypeStats> entry : staticResult.entrySet()) {
            FileTypeStats value = entry.getValue();
            value.getTotalSize();
//            String sizeWithUnit=value.convertBytesToSizeString(size);
//            System.out.println(size);
        }

        model.addAttribute("staticResult", staticResult);
        model.addAttribute("totalSize",totalSize);

        return "stasticfiles";
    }

    private String getOriginalFilename(String originalFilename){
        if(originalFilename == null)
        {
            return "";
        }

        if(originalFilename.contains("/") || originalFilename.contains("\\")){
            File file = new File(originalFilename);
            return file.getName();
        }
        return  originalFilename;
    }
}
       