import com.netease.backend.nts.common.AdaptTransEnum;
import com.netease.backend.nts.common.NTSException;
import com.netease.backend.nts.common.job.transcode.TranscodeJob;
import com.netease.backend.nts.common.preset.AdaptVideo;
import com.netease.backend.nts.common.preset.TransPreset;
import com.netease.backend.nts.sdk.NTSClient;

import java.util.LinkedList;
import java.util.List;

public class NtsUtil {
    private NTSClient client = null;

    public void init() {
        try {
            client = new NTSClient("nts://10.201.209.68:5679/nts?passwd=nts");
        } catch (NTSException e) {
            e.printStackTrace();
        }
    }

    public void sendTranscodeTask() throws NTSException {
        TransPreset preset = PresetUtil.getPreset("preset_transcode_vod_MP4_sd.xml");
        final TranscodeJob job = client.createJob(TranscodeJob.class);

        job.setInputBucket("nts-bucket-output");
        job.setIntputKey("av1test.mkv");
        job.setOutputBucket("nts-bucket-output");
        job.setOutputKey("test_test.ss.dd");

        preset.setAdaptTransEnum(AdaptTransEnum.ADAPT_HLS);
        List<AdaptVideo> adaptVideos = new LinkedList<>();
        adaptVideos.add(new AdaptVideo(0, 0));
        adaptVideos.add(new AdaptVideo(360, 600000));
        adaptVideos.add(new AdaptVideo(480, 1024000));
        preset.getVideo().setAdaptVideos(adaptVideos);

        preset.setSegmentTime(30);
    }
}
