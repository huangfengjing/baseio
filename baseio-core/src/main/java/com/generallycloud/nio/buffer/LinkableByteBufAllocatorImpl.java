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
package com.generallycloud.nio.buffer;

import com.generallycloud.nio.AbstractLifeCycle;
import com.generallycloud.nio.Linkable;
import com.generallycloud.nio.common.LifeCycleUtil;

public class LinkableByteBufAllocatorImpl extends AbstractLifeCycle implements LinkAbleByteBufAllocator {

	private Linkable<LinkAbleByteBufAllocator>	next;

	private int							index;

	private ByteBufAllocator					allocator;

	public LinkableByteBufAllocatorImpl(ByteBufAllocator allocator, int index) {
		this.index = index;
		this.allocator = allocator;
	}

	@Override
	public Linkable<LinkAbleByteBufAllocator> getNext() {
		return next;
	}

	@Override
	public void setNext(Linkable<LinkAbleByteBufAllocator> next) {
		this.next = next;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public LinkAbleByteBufAllocator getValue() {
		return this;
	}

	@Override
	public ByteBufAllocator unwrap() {
		return allocator;
	}

	@Override
	public void release(ByteBuf buf) {
		unwrap().release(buf);
	}

	@Override
	public ByteBuf allocate(int capacity) {

		ByteBuf buf = unwrap().allocate(capacity);

		if (buf == null) {
			return getNext().getValue().allocate(capacity, this);
		}

		return buf;
	}
	
	@Override
	public ByteBuf allocate(int capacity,LinkAbleByteBufAllocator allocator) {

		if (allocator == this) {
			//FIXME 是否申请java内存
			return UnpooledByteBufAllocator.getHeapInstance().allocate(capacity);
//			return null;
		}
		
		ByteBuf buf = unwrap().allocate(capacity);

		if (buf == null) {
			
			return getNext().getValue().allocate(capacity,allocator);
		}

		return buf;
	}

	@Override
	public int getUnitMemorySize() {
		return unwrap().getUnitMemorySize();
	}

	@Override
	public void freeMemory() {
		unwrap().freeMemory();
	}

	@Override
	public int getCapacity() {
		return unwrap().getCapacity();
	}

	@Override
	protected void doStart() throws Exception {
		unwrap().start();
	}

	@Override
	protected void doStop() throws Exception {
		LifeCycleUtil.stop(unwrap());
	}

	@Override
	protected boolean logger() {
		return false;
	}

	@Override
	public String toString() {
		return unwrap().toString();
	}

	@Override
	public boolean isDirect() {
		return unwrap().isDirect();
	}

	@Override
	public ByteBuf reallocate(ByteBuf buf, int limit) {
		return unwrap().reallocate(buf, limit);
	}

	@Override
	public ByteBuf reallocate(ByteBuf buf, int limit, int maxLimit) {
		return unwrap().reallocate(buf, limit, maxLimit);
	}

	@Override
	public ByteBuf reallocate(ByteBuf buf, int limit, boolean copyOld) {
		return unwrap().reallocate(buf, limit, copyOld);
	}

	@Override
	public ByteBuf reallocate(ByteBuf buf, int limit, int maxLimit, boolean copyOld) {
		return unwrap().reallocate(buf, limit, maxLimit, copyOld);
	}
	
	@Override
	public ByteBuf allocate(int limit, int maxLimit) {
		return unwrap().allocate(limit, maxLimit);
	}

}
