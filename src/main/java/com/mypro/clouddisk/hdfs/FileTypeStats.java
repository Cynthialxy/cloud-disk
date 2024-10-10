package com.mypro.clouddisk.hdfs;

public class FileTypeStats {
    private String fileType;
    private int fileCount;
    private long totalSize;


    public FileTypeStats(String fileType) {
        this.fileType = fileType;
        this.fileCount = 0;
        this.totalSize = 0;
    }

    public void incrementFileCount() {
        this.fileCount++;
    }

    public String addToTotalSize(long filesize) {
//        String newFileSize=convertBytesToSizeString(filesize);
//        String size=convertBytesToSizeString(totalSize);
//        size=addFileSizeStrings(newFileSize,size);
        totalSize+=filesize;
        return convertBytesToSizeString(totalSize);
    }

    //提取字符串中数字的部分，并转化为以B单位对应的数值
    private long convertSizeStringToBytes(String sizeString) {
        long bytes = 0;
        if (sizeString.endsWith("KB")) {
            bytes = (long) (Double.parseDouble(sizeString.substring(0, sizeString.length() - 2)) * 1024);
        } else if (sizeString.endsWith("MB")) {
            bytes = (long) (Double.parseDouble(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024);
        } else if (sizeString.endsWith("GB")) {
            bytes = (long) (Double.parseDouble(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024 * 1024);
        } else if (sizeString.endsWith("B")) {
            bytes = Long.parseLong(sizeString.substring(0, sizeString.length() - 1));
        }
        return bytes;
    }

    //将数值转化为对应的空间大小
    public String convertBytesToSizeString(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2fKB", (double) bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", (double) bytes / (1024 * 1024));
        } else {
            return String.format("%.2fGB", (double) bytes / (1024 * 1024 * 1024));
        }
    }

    // Getters and setters for fileType, fileCount and totalSize

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

}
