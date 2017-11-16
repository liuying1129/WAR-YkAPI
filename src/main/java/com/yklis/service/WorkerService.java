package com.yklis.service;

import java.util.List;

import com.yklis.entity.WorkerEntity;

public interface WorkerService {
	
	List<WorkerEntity> selectWorker(WorkerEntity workerEntity);
	
	List<WorkerEntity> ifCanLogin(String userId,String passWord);

}
