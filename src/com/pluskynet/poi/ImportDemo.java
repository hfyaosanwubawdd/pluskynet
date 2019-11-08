package com.pluskynet.poi;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.pluskynet.util.C3P0connsPollUTIL;

/**
* @author HF
* @version 创建时间：2019年5月27日 上午10:19:46
* 类说明  poi导入excel.xls
*/
public class ImportDemo {
	public static void main(String[] args) throws IOException, SQLException {
		 push();
	}
	public static void push() throws IOException, SQLException{
		Connection conn = C3P0connsPollUTIL.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "insert into reason_supply(reason) values";
		File file = new File("E:\\我的资源 目录.xls");
        if(file != null){
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = new HSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(1);
            if(sheet.getPhysicalNumberOfRows() > 2){
                for(int k = 1; k < sheet.getPhysicalNumberOfRows(); k++){
                    Row row = sheet.getRow(k);
                    Cell cell0 = row.getCell(2);
                    System.out.println(cell0.toString());
                    sql += "('"+cell0.toString()+"'),";
                }
            }
            workbook.close();
            fileInputStream.close();
        }
	        stmt.execute(sql.substring(0,sql.length()-1));
		}
}
