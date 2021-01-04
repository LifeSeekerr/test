import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.netease.backend.nts.common.preset.TransPreset;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.readFileToString;

public class PresetUtil {

    public static TransPreset getPreset(String presetFile) {
        String data = null;
        TransPreset request = null;
        try {
            URL url = PresetUtil.class.getClassLoader().getResource(presetFile);
            data = readFileToString(new File(url.getFile()), Charsets.UTF_8);
            request = new Gson().fromJson(data, TransPreset.class);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return request;
    }
}
