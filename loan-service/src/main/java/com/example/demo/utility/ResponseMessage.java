package com.example.demo.utility;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
	private Integer statusCode;
	private String status;
	private String message;
	private List<?> list;
	private Object data;
	public ResponseMessage(Integer statuscode, String status, String message) {
		super();
		this.statusCode = statuscode;
		this.status = status;
		this.message = message;
	}

	public ResponseMessage(Integer statuscode, String status, String message, Object data) {
		super();
		this.statusCode = statuscode;
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public ResponseMessage(Integer statuscode, String status, String message, List<?> list) {

		this.statusCode = statuscode;
		this.status = status;
		this.message = message;
		this.list = list;
	}
}
