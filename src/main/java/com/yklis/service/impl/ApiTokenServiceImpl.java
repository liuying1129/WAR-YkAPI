package com.yklis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yklis.dao.ApiTokenDao;
import com.yklis.entity.ApiTokenEntity;
import com.yklis.service.ApiTokenService;

@Service
public class ApiTokenServiceImpl implements ApiTokenService {

	@Autowired
	private ApiTokenDao apiTokenDao;

	@Override
	public List<ApiTokenEntity> selectApiToken(ApiTokenEntity apiTokenEntity) {
		
		return apiTokenDao.selectApiToken(apiTokenEntity);
	}

	@Override
	public void insertApiToken(ApiTokenEntity apiTokenEntity) {
	
		apiTokenDao.insertApiToken(apiTokenEntity);
		
	}

	@Override
	public void updateApiToken(ApiTokenEntity apiTokenEntity) {
		
		apiTokenDao.updateApiToken(apiTokenEntity);
		
	}

	@Override
	public void updateTokenModTime(String token) {
		
		apiTokenDao.updateTokenModTime(token);
		
	}

}
