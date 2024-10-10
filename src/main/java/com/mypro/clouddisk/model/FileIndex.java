/**
 * 表示文件在云盘中的索引信息。它包含了文件的各种属性，比如文件ID、用户ID、父文件ID、文件名、路径、是否为文件、创建时间和删除标志等。
 *
 * 用于在应用程序中表示和传递数据对象。可以更方便地组织和管理文件索引信息，并在程序中进行传递和处理。也实现了 toString() 方法，方便打印输出类的信息。
 */


package com.mypro.clouddisk.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class FileIndex {
	private Long fileIndexId;
	private String owner;
	private Long pFileIndexId;
	private String name;
	private String path;
	private Long parentId;
	private String isFile;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	private String deleteFlag;


	public Long getFileIndexId() {
		return fileIndexId;
	}
	public void setFileIndexId(Long fileIndexId) {
		this.fileIndexId = fileIndexId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Long getpFileIndexId() {
		return pFileIndexId;
	}
	public void setpFileIndexId(Long pFileIndexId) {
		this.pFileIndexId = pFileIndexId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getIsFile() {
		return isFile;
	}
	public void setIsFile(String isFile) {
		this.isFile = isFile;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	@Override
	public String toString() {
		return "FileIndex [fileIndexId=" + fileIndexId + ", owner=" + owner + ", pFileIndexId=" + pFileIndexId
				+ ", name=" + name + ", path=" + path + ", parentId=" + parentId + ", isFile=" + isFile
				+ ", createTime=" + createTime + ", deleteFlag=" + deleteFlag + "]";
	}
}
