package com.yklis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yklis.dao.WorkerDao;
import com.yklis.entity.WorkerEntity;
import com.yklis.service.WorkerService;

@Service
public class WorkerServiceImpl implements WorkerService {

	@Autowired
	private WorkerDao workerDao;
	
	@Override
	public List<WorkerEntity> selectWorker(WorkerEntity workerEntity) {

		return workerDao.selectWorker(workerEntity);
	}
	
	@Override
	public List<WorkerEntity> ifCanLogin(String userId,String passWord){
		
		return workerDao.ifCanLogin(userId, passWord);
	}

}
