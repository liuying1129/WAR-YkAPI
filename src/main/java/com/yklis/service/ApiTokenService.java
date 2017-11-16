package com.yklis.service;

import java.util.List;

import com.yklis.entity.ApiTokenEntity;

public interface ApiTokenService {
	
	List<ApiTokenEntity> selectApiToken(ApiTokenEntity apiTokenEntity);

	void insertApiToken(ApiTokenEntity apiTokenEntity);
	
	void updateApiToken(ApiTokenEntity apiTokenEntity);

	void updateTokenModTime(String token);
}
