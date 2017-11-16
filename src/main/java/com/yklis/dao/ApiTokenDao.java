package com.yklis.dao;

import java.util.List;

import com.yklis.entity.ApiTokenEntity;

public interface ApiTokenDao {
	
	List<ApiTokenEntity> selectApiToken(ApiTokenEntity apiTokenEntity);
	
	void insertApiToken(ApiTokenEntity apiTokenEntity);
	
	void updateApiToken(ApiTokenEntity apiTokenEntity);
	
	void updateTokenModTime(String token);

}
