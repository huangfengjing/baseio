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
package com.generallycloud.test.nio.balance;

import com.generallycloud.nio.balance.BalanceContext;
import com.generallycloud.nio.codec.protobase.ProtobaseProtocolFactory;
import com.generallycloud.nio.codec.protobase.future.ProtobaseReadFuture;
import com.generallycloud.nio.common.SharedBundle;
import com.generallycloud.nio.component.IoEventHandleAdaptor;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.protocol.ReadFuture;
import com.generallycloud.test.nio.common.IoConnectorUtil;

public class TestBalanceLoad {

	public static void main(String[] args) throws Exception {
		
		SharedBundle.instance().loadAllProperties("nio");

		IoEventHandleAdaptor eventHandleAdaptor = new IoEventHandleAdaptor() {

			@Override
			public void accept(SocketSession session, ReadFuture future) throws Exception {
				
				ProtobaseReadFuture f = (ProtobaseReadFuture)future;
				
				if (BalanceContext.BALANCE_CHANNEL_LOST.equals(f.getFutureName())) {
					System.out.println("客户端已下线：" + f.getReadText());
				} else {
					System.out.println("收到报文：" + future.toString());
					String res = "_____________" + f.getReadText();
					System.out.println("处理报文：" + res);
					f.write(res);
					session.flush(future);
				}
			}
		};

		ServerConfiguration configuration = new ServerConfiguration();

		configuration.setSERVER_PORT(8800);

		SocketChannelConnector connector = IoConnectorUtil.getTCPConnector(eventHandleAdaptor, configuration);

		connector.getContext().setProtocolFactory(new ProtobaseProtocolFactory());
		
		connector.connect();
	}

}
