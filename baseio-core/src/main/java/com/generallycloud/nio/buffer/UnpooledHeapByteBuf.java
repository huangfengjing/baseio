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

/**
 * @author wangkai
 *
 */
public class UnpooledHeapByteBuf extends AbstractHeapByteBuf {

	protected UnpooledHeapByteBuf(ByteBufAllocator allocator, byte[] memory) {
		super(allocator, memory);
		this.produce(memory.length);
	}

	protected void produce(int capacity) {
		this.capacity = capacity;
		this.limit = capacity;
		this.referenceCount = 1;
	}
	
	protected UnpooledHeapByteBuf produce(ByteBuf buf) {
		this.capacity = buf.capacity();
		this.limit = buf.limit();
		this.position = buf.position();
		this.referenceCount = 1;
		return this;
	}

	@Override
	public ByteBuf reallocate(int limit, boolean copyOld) {

		byte[] newMemory = new byte[limit];

		if (copyOld) {
			System.arraycopy(memory, 0, newMemory, 0, position());
		} else {
			this.position = 0;
		}

		this.memory = newMemory;
		this.capacity = limit;
		this.limit = limit;
		this.referenceCount = 1;
		return this;
	}

	@Override
	public ByteBuf doDuplicate() {
		return new DuplicateByteBuf(new UnpooledHeapByteBuf(allocator, memory).produce(this), this);
	}

	@Override
	protected void doRelease() {
		this.memory = null;
	}

	@Override
	public PooledByteBuf newByteBuf(ByteBufAllocator allocator) {
		throw new UnsupportedOperationException();
	}

}
