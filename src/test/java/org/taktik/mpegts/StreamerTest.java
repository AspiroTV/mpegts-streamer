package org.taktik.mpegts;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.taktik.mpegts.sinks.MTSSink;
import org.taktik.mpegts.sinks.UDPTransport;
import org.taktik.mpegts.sources.MTSSource;
import org.taktik.mpegts.sources.MTSSources;
import org.taktik.mpegts.sources.MultiMTSSource;
import org.taktik.mpegts.sources.MultiMTSSource.MultiMTSSourceBuilder;

public class StreamerTest {
	public static void main(String[] args) throws Exception {

		// Set up mts sink
		MTSSink transport = UDPTransport.builder()
				.setAddress("239.222.1.1")
				.setPort(1234)
				.setSoTimeout(5000)
				.setTtl(1)
				.build();

		File directory = new File("D:\\down\\tv2no");
		File[] chunks = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ts");
			}
		});
		Collection<MTSSource> mtsSources = new ArrayList(chunks.length);
		for (int i = 0; i < chunks.length; i++) {
			mtsSources.add(MTSSources.from(chunks[i]));
		}

		
		MultiMTSSourceBuilder builder = MultiMTSSource.builder().addSources(mtsSources);
		MultiMTSSource sources =  builder.build();

		// build streamer
		Streamer streamer = Streamer.builder()
				.setSource(sources)
				//.setSink(ByteChannelSink.builder().setByteChannel(fc).build())
				.setSink(transport)
				.build();

		// Start streaming
		streamer.stream();

	}
}
