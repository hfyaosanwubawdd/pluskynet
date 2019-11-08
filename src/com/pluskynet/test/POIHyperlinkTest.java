package com.pluskynet.test;

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
* @author HF
* @version 创建时间：2019年11月5日 上午9:30:46
* 类说明
*/
public class POIHyperlinkTest {

	public static void main(String[] args) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		// 使用creationHelpper来创建XSSFHyperlink对象
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFHyperlink  link = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
		link.setAddress("https://github.com/550690513");
		cell.setHyperlink(link);
		cell.setCellValue("Fork me on Github");
	}
}
