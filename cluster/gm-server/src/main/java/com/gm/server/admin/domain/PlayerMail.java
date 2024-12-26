package com.gm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.server.common.annotation.Excel;
import com.gm.server.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * @author wishcher tree
 * @date 2022/5/9 18:26
 */
public class PlayerMail extends BaseEntity {
    @Excel(name = "邮件标识", cellType = Excel.ColumnType.STRING)
    private Long mailId;

    @Excel(name = "邮件标题")
    private String title;

    @Excel(name = "邮件内容")
    private String content;

    @Excel(name = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @Excel(name = "有效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validDate;

    @Excel(name = "附件")
    private String annex;

    @Excel(name = "邮件状态")
    private String state;

    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstCreateTime;

    public Long getMailId() {
        return mailId;
    }

    public void setMailId(Long mailId) {
        this.mailId = mailId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getFirstCreateTime() {
        return firstCreateTime;
    }

    public void setFirstCreateTime(Date firstCreateTime) {
        this.firstCreateTime = firstCreateTime;
    }
}
