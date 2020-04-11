package sudokudbj;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import sudokudbj.model.MdlSudoku;
import sudokudbj.model.MdlSudokuCell;

public class Sudoku {

    public static void main(String[] args) {
        long startTime=System.currentTimeMillis();   //获取开始时间

        // Input file
        String strFile = "./input/problem.txt";
        if (args.length > 0) {
            strFile = args[0];
        }
        System.out.println("input:[" + strFile + "]");
        // 从文件中获取输入信息
        MdlSudoku mdl = parse(strFile);
        // 从机能分割的角度，这段处理放到这里，多了个判断。
        if (mdl != null) {
            // 整理信息，从位置很明确的数字单元开始处理，直到全部完了。
            ProcSudoku proc = new ProcSudoku(1);
            proc.proc(mdl);
            System.out.println("Sudoku's Info:\n" + mdl);
            // 把结果写入文件
            String outFileName = strFile.substring(0, strFile.lastIndexOf(".")) + "a" + strFile.substring(strFile.lastIndexOf("."));
            System.out.println("input:[" + outFileName + "]");
            write(mdl, outFileName);
        }

        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + ( endTime - startTime ) + "ms");
    }

    public static MdlSudoku parse(String file) {
        long startTime=System.currentTimeMillis();   //获取开始时间

        MdlSudoku result = null;
        File fl = new File(file);
        if (!fl.exists()) {
            System.out.println("The parse[" + file + "] is not exists!");
            result = null;
            return result;
        }
        long size = fl.length();
        if (size > 1000) {
            System.out.println("The parse[" + file + "]'s size more than 1000!");
            result = null;
            return result;
        }

        FileInputStream input = null;
        ByteBuffer buffer = null;
        try {
            input = new FileInputStream(fl);
            FileChannel fcIn = input.getChannel();
            buffer = ByteBuffer.allocate((int)size);
            buffer.clear();
            int r = fcIn.read(buffer);
            System.out.println("readed:" + r + "bytes.");
            buffer.flip();
        } catch (FileNotFoundException e) {
            System.out.println("parse[" + file + "] error!");
            result = null;
        } catch (IOException e) {
            System.out.println("parse[" + file + "] error!");
            result = null;
        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (buffer != null) {
            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            // ASCII
            int[] datas = new int[81];
            int j = 0;
            for(int i = 0; i < byteArray.length; i ++) {
                if (48 <= byteArray[i] && byteArray[i] <= 57) {
                    datas[j] = byteArray[i] - 48;
                    j ++;
                }
                if (j > 80) {
                    break;
                }
            } // for

            // 通过int型数组构筑主处理
            result = new MdlSudoku(datas);
        }

        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("parse处理运行时间： " + ( endTime - startTime ) + "ms");
        return result;
    }

    public static void write(MdlSudoku mdl, String file) {
        long startTime = System.currentTimeMillis();   //获取开始时间

        StringBuffer buf = new StringBuffer();
        int i = 0;
        for (MdlSudokuCell cell : mdl.getCells()) {
            buf.append(cell.getValue());
            if (i % 9 != 8) {
                buf.append(" ");
            } else {
                buf.append("\r\n");
            }
            i ++;
        }
        /* 写入Txt文件 */
        File writename = new File(file); // 相对路径，如果没有则要建立一个新的output.txt文件
        try {
            // 创建新文件
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(buf.toString()); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();     //获取结束时间
        System.out.println("write处理运行时间： " + ( endTime - startTime ) + "ms");
    }
}
