package com.dovakin0007.notes_service.service;

public class GrpcUserService implements DefaultServiceImpl {

	@Override
	public String createUuid() {
		return java.util.UUID.randomUUID().toString();
	}
}
