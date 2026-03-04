package com.example.demo.utility;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
	private Object data;
	private List<Object> list;
	private Integer statusCode;
	private String status;
	private String message;

	public ResponseMessage(List<Object> list, Integer statusCode, String status, String message) {
		this.list = list;
		this.statusCode = statusCode;
		this.status = status;
		this.message = message;
	}

	public ResponseMessage(Object data, Integer statusCode, String status, String message) {
		this.data = data;
		this.statusCode = statusCode;
		this.status = status;
		this.message = message;
	}

	public ResponseMessage(Integer statusCode, String status, String message) {
		this.statusCode = statusCode;
		this.status = status;
		this.message = message;
	}

}
