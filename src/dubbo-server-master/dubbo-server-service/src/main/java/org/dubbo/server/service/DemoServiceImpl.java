package org.dubbo.server.service;

import org.dubbo.server.api.DemoService;
import org.springframework.stereotype.Service;

@Service("demoService")
public class DemoServiceImpl implements DemoService {

	public String sayHello(String name) {
		return "Hello " + name;
	}
}
