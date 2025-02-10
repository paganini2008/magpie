// package com.github.doodler.common.utils;
//
// import java.io.File;
// import java.io.FileInputStream;
// import javax.servlet.http.HttpServletResponse;
// import org.springframework.util.FileCopyUtils;
// import lombok.experimental.UtilityClass;
// import lombok.extern.slf4j.Slf4j;
//
/// **
// *
// * @Description: ExportUtils
// * @Author: Fred Feng
// * @Date: 06/01/2025
// * @Version 1.0.0
// */
// @Slf4j
// @UtilityClass
// public class ExportUtils {
//
// public static void export(HttpServletResponse response, String fileNameWithPath,
// String exportFileName) {
// try {
// File file = new File(fileNameWithPath);
// if (!file.exists()) {
// return;
// }
// response.reset();
// response.addHeader("Pargam", "no-cache");
// response.addHeader("Cache-Control", "no-cache");
// response.setBufferSize(1024);
// response.setCharacterEncoding("UTF-8");
// response.setHeader("Content-Disposition",
// "attachment; filename=\"".concat(templateFileName).concat("\""));
// FileInputStream fileInputStream = new FileInputStream(file);
// FileCopyUtils.copy(fileInputStream, response.getOutputStream());
// if (deleteFile) {
// file.delete();
// }
// } catch (Exception e) {
// log.error(e.toString());
// }
// }
// }
