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
package com.generallycloud.nio.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.generallycloud.nio.component.BeatFutureFactory;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketChannelContextImpl;
import com.generallycloud.nio.component.SocketSessionEventListener;
import com.generallycloud.nio.component.ssl.SslContext;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.protocol.ProtocolFactory;

public class FrontServerBootStrap {

	private ProtocolFactory					frontProtocolFactory;
	private ProtocolFactory					frontReverseProtocolFactory;
	private ServerConfiguration				frontServerConfiguration;
	private ServerConfiguration				frontReverseServerConfiguration;
	private List<SocketSessionEventListener>	frontSessionEventListeners;
	private List<SocketSessionEventListener>	frontReverseSessionEventListeners;
	private BeatFutureFactory				frontBeatFutureFactory;
	private BeatFutureFactory				frontReverseBeatFutureFactory;
	private ChannelLostReadFutureFactory		channelLostReadFutureFactory;
	private SslContext						sslContext;
	private int							interceptorLimit = 5;

	public void startup() throws IOException {

		FrontFacadeAcceptor frontFacadeAcceptor = new FrontFacadeAcceptor();

		FrontContext frontContext = new FrontContext();
		
		frontContext.setFrontInterceptor(new FrontInterceptorImpl(interceptorLimit));

		SocketChannelContext frontBaseContext = getFrontFacadeChannelContext(frontContext, frontServerConfiguration,
				frontProtocolFactory);
		
		frontBaseContext.setSocketSessionFactory(new FrontFacadeSocketSessionFactory());

		SocketChannelContext frontReverseBaseContext = getBalanceFacadeChannelContext(frontContext,
				frontReverseServerConfiguration, frontReverseProtocolFactory);
		
		frontContext.setChannelLostReadFutureFactory(channelLostReadFutureFactory);

		frontFacadeAcceptor.start(frontContext, frontBaseContext, frontReverseBaseContext);
	}

	private SocketChannelContext getFrontFacadeChannelContext(FrontContext frontContext,
			ServerConfiguration configuration, ProtocolFactory protocolFactory) {

		SocketChannelContext context = new SocketChannelContextImpl(configuration);

		context.setIoEventHandleAdaptor(frontContext.getFrontFacadeAcceptorHandler());

		context.addSessionEventListener(frontContext.getFrontFacadeAcceptorSEListener());

		context.setProtocolFactory(protocolFactory);

		context.setBeatFutureFactory(frontBeatFutureFactory);

		if (frontSessionEventListeners != null) {
			addSessionEventListener2Context(context, frontSessionEventListeners);
		}

		if (sslContext != null) {
			context.setSslContext(sslContext);
		}

		return context;
	}

	private SocketChannelContext getBalanceFacadeChannelContext(FrontContext frontContext,
			ServerConfiguration configuration, ProtocolFactory protocolFactory) {

		SocketChannelContext context = new SocketChannelContextImpl(configuration);

		context.setIoEventHandleAdaptor(frontContext.getBalanceFacadeConnectorHandler());

		context.addSessionEventListener(frontContext.getBalanceFacadeConnectorSEListener());

		context.setProtocolFactory(protocolFactory);

		context.setBeatFutureFactory(frontReverseBeatFutureFactory);

		if (frontReverseSessionEventListeners != null) {
			addSessionEventListener2Context(context, frontReverseSessionEventListeners);
		}

		return context;
	}

	public ProtocolFactory getFrontProtocolFactory() {
		return frontProtocolFactory;
	}

	public void setFrontProtocolFactory(ProtocolFactory frontProtocolFactory) {
		this.frontProtocolFactory = frontProtocolFactory;
	}

	public ProtocolFactory getFrontReverseProtocolFactory() {
		return frontReverseProtocolFactory;
	}

	public void setFrontReverseProtocolFactory(ProtocolFactory frontReverseProtocolFactory) {
		this.frontReverseProtocolFactory = frontReverseProtocolFactory;
	}

	public ServerConfiguration getFrontServerConfiguration() {
		return frontServerConfiguration;
	}

	public void setFrontServerConfiguration(ServerConfiguration frontServerConfiguration) {
		this.frontServerConfiguration = frontServerConfiguration;
	}

	public ServerConfiguration getFrontReverseServerConfiguration() {
		return frontReverseServerConfiguration;
	}

	public void setFrontReverseServerConfiguration(ServerConfiguration frontReverseServerConfiguration) {
		this.frontReverseServerConfiguration = frontReverseServerConfiguration;
	}

	public void addFrontSessionEventListener(SocketSessionEventListener listener) {
		if (frontSessionEventListeners == null) {
			frontSessionEventListeners = new ArrayList<SocketSessionEventListener>();
		}
		frontSessionEventListeners.add(listener);
	}

	public void addFrontReverseSessionEventListener(SocketSessionEventListener listener) {
		if (frontReverseSessionEventListeners == null) {
			frontReverseSessionEventListeners = new ArrayList<SocketSessionEventListener>();
		}
		frontReverseSessionEventListeners.add(listener);
	}

	private void addSessionEventListener2Context(SocketChannelContext context,
			List<SocketSessionEventListener> listeners) {
		for (SocketSessionEventListener l : listeners) {
			context.addSessionEventListener(l);
		}
	}

	public BeatFutureFactory getFrontBeatFutureFactory() {
		return frontBeatFutureFactory;
	}

	public BeatFutureFactory getFrontReverseBeatFutureFactory() {
		return frontReverseBeatFutureFactory;
	}

	public void setFrontBeatFutureFactory(BeatFutureFactory frontBeatFutureFactory) {
		this.frontBeatFutureFactory = frontBeatFutureFactory;
	}

	public void setFrontReverseBeatFutureFactory(BeatFutureFactory frontReverseBeatFutureFactory) {
		this.frontReverseBeatFutureFactory = frontReverseBeatFutureFactory;
	}

	public SslContext getSslContext() {
		return sslContext;
	}
	
	public int getInterceptorLimit() {
		return interceptorLimit;
	}

	public void setInterceptorLimit(int interceptorLimit) {
		if (interceptorLimit < 1) {
			return;
		}
		this.interceptorLimit = interceptorLimit;
	}

	public void setSslContext(SslContext sslContext) {
		this.sslContext = sslContext;
	}

	public ChannelLostReadFutureFactory getChannelLostReadFutureFactory() {
		return channelLostReadFutureFactory;
	}

	public void setChannelLostReadFutureFactory(ChannelLostReadFutureFactory channelLostReadFutureFactory) {
		this.channelLostReadFutureFactory = channelLostReadFutureFactory;
	}

}
