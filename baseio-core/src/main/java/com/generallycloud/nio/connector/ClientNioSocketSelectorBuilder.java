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
package com.generallycloud.nio.connector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.generallycloud.nio.acceptor.NioChannelService;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketSelector;
import com.generallycloud.nio.component.SocketSelectorBuilder;
import com.generallycloud.nio.component.SocketSelectorEventLoop;

/**
 * @author wangkai
 *
 */
public class ClientNioSocketSelectorBuilder implements SocketSelectorBuilder {

	private SocketChannelConnector connector;

	public ClientNioSocketSelectorBuilder(SocketChannelConnector connector) {
		this.connector = connector;
	}

	// FIXME open channel
	@Override
	public SocketSelector build(SocketSelectorEventLoop selectorLoop) throws IOException {

		SocketChannelContext context = selectorLoop.getChannelContext();

		NioChannelService nioChannelService = (NioChannelService) context.getChannelService();

		SocketChannel channel = (SocketChannel) nioChannelService.getSelectableChannel();

		// 打开selector
		java.nio.channels.Selector selector = java.nio.channels.Selector.open();

		channel.register(selector, SelectionKey.OP_CONNECT);

		return new ClientNioSocketSelector(selectorLoop, selector, channel, connector);
	}

	public void setConnector(SocketChannelConnector connector) {
		this.connector = connector;
	}
}
