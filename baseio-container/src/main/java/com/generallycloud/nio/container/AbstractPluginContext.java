/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.generallycloud.nio.container;

import java.util.List;
import java.util.Map;

import com.generallycloud.nio.common.StringUtil;
import com.generallycloud.nio.container.service.FutureAcceptorFilter;
import com.generallycloud.nio.container.service.FutureAcceptorService;

public abstract class AbstractPluginContext extends InitializeableImpl implements PluginContext {

	private int	pluginIndex;

	protected AbstractPluginContext() {

		Sequence sequence = ApplicationContext.getInstance().getSequence();

		this.pluginIndex = sequence.AUTO_PLUGIN_INDEX.getAndIncrement();
	}

	@Override
	public int getPluginIndex() {
		return pluginIndex;
	}

	@Override
	public void configFutureAcceptorFilter(List<FutureAcceptorFilter> filters) {

	}

	@Override
	public void configFutureAcceptor(Map<String, FutureAcceptorService> acceptors) {

	}
	
	protected void putServlet(Map<String, FutureAcceptorService> acceptors,FutureAcceptorService service){
		
		String serviceName = service.getServiceName();
		
		if (StringUtil.isNullOrBlank(serviceName)) {
			serviceName = service.getClass().getSimpleName();
		}
		
		service.setServiceName(serviceName);
		
		acceptors.put(serviceName, service);
	}

}
