package reactor.tcp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageList;
import reactor.io.Buffer;

class NettyTcpConnectionChannelInboundHandler extends ChannelInboundHandlerAdapter {

	private final NettyTcpConnection<?, ?> conn;

	private ByteBuf remainder;

	public NettyTcpConnectionChannelInboundHandler(NettyTcpConnection<?, ?> conn) {
		this.conn = conn;
	}

	@Override
	public final void messageReceived(ChannelHandlerContext ctx, MessageList<Object> msgs) throws Exception {
		int size = msgs.size();
		for (int i = 0; i < size; i ++) {
			Object m = msgs.get(i);
			if (m instanceof ByteBuf) {
				ByteBuf data = (ByteBuf) m;

				if (remainder == null) {
					try {
						passToConnection(data);
					} finally {
						if (data.isReadable()) {
							remainder = data;
						} else {
							data.release();
						}
					}
				} else {
					if (!bufferHasSufficientCapacity(remainder, data)) {
						ByteBuf combined = createCombinedBuffer(remainder, data, ctx);
						remainder.release();
						remainder = combined;
					} else {
						remainder.writeBytes(data);
					}
					data.release();

					try {
						passToConnection(remainder);
					} finally {
						if (remainder.isReadable()) {
							remainder.discardSomeReadBytes();
						} else {
							remainder.release();
							remainder = null;
						}
					}
				}
			}
		}
	}

	private boolean bufferHasSufficientCapacity(ByteBuf receiver, ByteBuf provider) {
		return receiver.writerIndex() <= receiver.maxCapacity() - provider.readableBytes();
	}

	private ByteBuf createCombinedBuffer(ByteBuf partOne, ByteBuf partTwo, ChannelHandlerContext ctx) {
    ByteBuf combined = ctx.alloc().buffer(partOne.readableBytes() + partTwo.readableBytes());
    combined.writeBytes(partOne);
    combined.writeBytes(partTwo);
    return combined;
	}

	private void passToConnection(ByteBuf data) {
		Buffer b = new Buffer(data.nioBuffer());
		int start = b.position();
		conn.read(b);
		data.skipBytes(b.position() - start);
	}
}
