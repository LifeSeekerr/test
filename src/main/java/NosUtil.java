import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
//1
public class NosUtil {
    String accessKey = "your-accesskey";
    String secretKey = "your-secretKey ";

    static void upload(String bucketName, File file, String key) {
        Credentials credentials = new BasicCredentials("ab1856bb39044591939d7b94e1b8e5ee", "ed1573cd7de34086a0ba5c3c521c6df1");
        //Credentials credentials = new BasicCredentials("ab1856bb39044591939d7b94e1b8e5ee", "ed1573cd7de34086a0ba5c3c521c6df1");
        NosClient nosClient = new NosClient(credentials);
        nosClient.setEndpoint("nos-hz.163yun.com");
        try {
            nosClient.putObject(bucketName, key, file);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    static void multiUpload(String bucketName, File file, String basePath) {
        if (StringUtils.startsWith(file.getName(), ".")) {
            return;
        }
        if (file.isFile()) {
            String key = StringUtils.substringAfterLast(file.getAbsolutePath(), basePath + "/");
            if (StringUtils.isBlank(key)) {
                key = file.getName();
            }
            System.out.println("upload file : " + file.getAbsolutePath() + " key : " + key);
            upload(bucketName, file, file.getName());
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File subFile : files) {
                multiUpload(bucketName, subFile, basePath);
            }
        }
    }

    static List<NOSObjectSummary> getFileList(boolean isPrint, String prefix) {
        Credentials credentials = new BasicCredentials("ab1856bb39044591939d7b94e1b8e5ee", "ed1573cd7de34086a0ba5c3c521c6df1");
        NosClient nosClient = new NosClient(credentials);
        nosClient.setEndpoint("nos-hz.163yun.com");
//        ObjectListing objectListing = nosClient.listObjects("nts-bucket-output");
//        ObjectListing objectListing = nosClient.listObjects("jdvodoi9j5ej8");

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName("nts-bucket-output");
//        listObjectsRequest.setBucketName("jdvodoi9j5ej8");
        listObjectsRequest.setMaxKeys(500);
        if (StringUtils.isNotBlank(prefix)) {
            listObjectsRequest.setPrefix(prefix);
        }

        ObjectListing objectListing = nosClient.listObjects(listObjectsRequest);


        List<NOSObjectSummary> sums = objectListing.getObjectSummaries();
        if (isPrint) {
            for (NOSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
        }
        return sums;
    }

    static List<NOSObjectSummary> getFileList(boolean isPrint) {
        return getFileList(isPrint, null);
    }

    public static void downloadFile() throws Exception {
        //先实例化一个NosClient
        String accessKey = "ab1856bb39044591939d7b94e1b8e5ee";
        String secretKey = "ed1573cd7de34086a0ba5c3c521c6df1";
        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        NosClient nosClient = new NosClient(credentials);
        nosClient.setEndpoint("nos-jd.163yun.com");
//然后通过nosClient对象来初始化TransferManager
        TransferManager transferManager = new TransferManager(nosClient);
        File file = new  File("/Users/luoweiheng/Documents/nts/encrypt_hls_no/1_@#$%^*&/1/1/1603778439161.m3u8");
        Download download = transferManager.download("jdvodoi9j5ej8", "encrypt_hls_no/1_@#$%^*&/1/1/1603778439161.m3u8", file);
        download.waitForCompletion();
        transferManager.shutdownNow();
    }

    public static void deleteFiles(String[] keys) throws Exception {
        try {
            String accessKey = "ab1856bb39044591939d7b94e1b8e5ee";
            String secretKey = "ed1573cd7de34086a0ba5c3c521c6df1";
            Credentials credentials = new BasicCredentials(accessKey, secretKey);
            NosClient nosClient = new NosClient(credentials);
            nosClient.setEndpoint("nos-hz.163yun.com");
            DeleteObjectsRequest deleteObjectsRequest = (new DeleteObjectsRequest("nts-bucket-output")).withKeys(keys);
            DeleteObjectsResult result = nosClient.deleteObjects(deleteObjectsRequest);
            List<DeleteObjectsResult.DeletedObject>  deleteObjects = result.getDeletedObjects();
            //print the delete results
            for (DeleteObjectsResult.DeletedObject items: deleteObjects){
                System.out.println(items.getKey());
            }
// 部分对象删除失败
        } catch (MultiObjectDeleteException e) {
            List<MultiObjectDeleteException.DeleteError> deleteErrors = e.getErrors();
            for (MultiObjectDeleteException.DeleteError error : deleteErrors) {
                System.out.println(error.getKey());
            }
        } catch (ServiceException e) {
            //捕捉nos服务器异常错误
        } catch (ClientException ace) {
            //捕捉客户端错误
        }
    }

    public static void deleteFileByKeywords(String... keyWords) throws Exception {
        List<NOSObjectSummary> list = getFileList(false);
        List<String> targetKeys = new LinkedList<>();
        for (NOSObjectSummary summary : list) {
            if (ArrayUtils.isEmpty(keyWords)) {
                targetKeys.add(summary.getKey());
            }
            for (String keyWord : keyWords) {
                if (summary.getKey().contains(keyWord)) {
                    targetKeys.add(summary.getKey());
                }
            }
        }
        deleteFiles(targetKeys.toArray(new String[0]));
    }

    public static void main(String[] args) {
        try {
//            deleteFileByKeywords("recordtest", "live-preview", "screenshot", "xc_test", ".ts", ".m3u8");
//            multiUpload(new File("/Users/luoweiheng/Documents/nts/videos/biemianqiang.mp4"), "/Users/luoweiheng/Documents/nts/videos/");
            upload("nts-bucket-output", new File("/Users/luoweiheng/Desktop/3f4a1c39-6467-4374-a9d5-a4e11ffcb974.mp3"),
                    "input.mp3");
//            getFileList(true);
//            getFileList(true);
//            downloadFile();
//            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() {

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String os = System.getProperty("os.name");
        long physicalFree = osmxb.getFreePhysicalMemorySize() / 1024 / 1024 / 1024;
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / 1024 /1024 / 1024;
        long physicalUse = physicalTotal - physicalFree;
        long coreCount = osmxb.getAvailableProcessors();
        System.out.println("操作系统的版本：" + os);
        System.out.println("操作系统物理内存已用的空间为：" + physicalFree + " GB");
        System.out.println("操作系统物理内存的空闲空间为：" + physicalUse + " GB");
        System.out.println("操作系统总物理内存：" + physicalTotal + " GB");
        System.out.println("cpu核数：" + coreCount);

    }

    public void request() {

    }
}
