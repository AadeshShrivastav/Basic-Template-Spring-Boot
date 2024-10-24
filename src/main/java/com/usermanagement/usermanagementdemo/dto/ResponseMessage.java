package com.usermanagement.usermanagementdemo.dto;

public class ResponseMessage {
    private Integer status;
    private String message;
    private Object data;

    public ResponseMessage(Integer status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ResponseMessage(Object data, Integer status) {
        this.status = status;
        this.message = null;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
