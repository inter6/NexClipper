package com.nexcloud.api.service.user;
/*
* Copyright 2019 NexCloud Co.,Ltd.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexcloud.rdb.domain.mysql.User;
import com.nexcloud.rdb.mapper.mysql.UserRepository;
import com.nexcloud.util.response.Mysql;
@Service
public class UserService{
	@Autowired private UserRepository userRepository;
	@Autowired private Mysql mysql;

	public String getUserList(){
		return mysql.resultToResponse(userRepository.getUserList());
	}
	public User getUser(String userId){
		return userRepository.getUser(userId);
	}
	public boolean addUser(User user) {
		return userRepository.addUser(user);
	}
}
