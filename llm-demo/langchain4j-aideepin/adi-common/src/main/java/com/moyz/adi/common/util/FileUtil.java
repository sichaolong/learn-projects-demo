package com.moyz.adi.common.util;

import com.moyz.adi.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.moyz.adi.common.enums.ErrorEnum.A_FILE_NOT_EXIST;

@Slf4j
public class FileUtil {

    public static Pair saveToLocal(MultipartFile file, String path, String newName) {
        if (file.isEmpty()) {
            log.info("save to local,file is empty");
            throw new BaseException(A_FILE_NOT_EXIST);
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExt = getFileExtension(fileName);
        log.info("save to local,original name:{},new name:{}", fileName, newName);
        String pathName = path + newName + "." + fileExt;
        try {
            // 将文件保存到目标路径
            file.transferTo(new File(pathName));
        } catch (IOException e) {
            log.error("save to local error", e);
        }
        return new ImmutablePair<>(pathName, fileExt);
    }

    public static byte[] readBytes(String localPath) {
        try {
            return FileUtils.readFileToByteArray(new File(localPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            // 文件名中没有后缀或者后缀位于文件名的末尾
            return "";
        } else {
            return fileName.substring(dotIndex + 1);
        }
    }
}
