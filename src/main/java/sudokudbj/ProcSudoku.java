package sudokudbj;

import java.util.ArrayList;

import sudokudbj.model.MdlSudoku;
import sudokudbj.model.MdlSudokuCell;
import sudokudbj.model.MdlSudokuRegion;

public class ProcSudoku {

    private int remaindertestFlag = 0;
    private int debugFlag = 0;

    /**
     * 数独解法
     * @param flg 是否使用余数测试法：1：使用，0：不使用。
     */
    public ProcSudoku(int flg) {
        this.remaindertestFlag = flg;
    }

    public boolean proc(MdlSudoku mdl) {
        long startTime=System.currentTimeMillis();   //获取开始时间

        boolean result = false;
        int i = 1;
        int[] flg = new int[4];

        while(!result) {
            System.out.println("===第" + i + "次处理===:\n" + mdl);

            flg[0] = 0; flg[1] = 0; flg[2] = 0;
            flg[3] = 0;

            /*
             * 基础摒除法 Hidden Single
             * 基础摒除法就是利用1～9的数字在每一行、每一列、每一个九宫格都只能出现一次的规则进行解题的方法。
             */

            // 从位置很明确的数字单元开始处理
            // 行摒除
            ArrayList<MdlSudokuCell> doing = new ArrayList<MdlSudokuCell>();
            for (MdlSudokuRegion r : mdl.getRows()) {
                if (!r.isDone()) {
                    doing.addAll(r.hiddenSingle("行摒除", this.debugFlag));
                }
            }
            if (this.debugFlag == 1) System.out.println("行摒除" + doing);
            if (doing.size() > 0) {
                flg[1] = 1;
                mdl.getDone().addAll(doing);
            }
            if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(行摒除)===:\n" + mdl);

            // 列摒除
            doing = new ArrayList<MdlSudokuCell>();
            for (MdlSudokuRegion r : mdl.getCols()) {
                if (!r.isDone()) {
                    doing.addAll(r.hiddenSingle("列摒除", this.debugFlag));
                }
            }
            if (this.debugFlag == 1) System.out.println("列摒除" + doing);
            if (doing.size() > 0) {
                flg[0] = 1;
                mdl.getDone().addAll(doing);
            }
            if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(列摒除)===:\n" + mdl);

            // 九宫格摒除
            doing = new ArrayList<MdlSudokuCell>();
            for (MdlSudokuRegion r : mdl.getBoxs()) {
                if (!r.isDone()) {
                    doing.addAll(r.hiddenSingle("九宫格摒除", this.debugFlag));
                }
            }
            if (this.debugFlag == 1) System.out.println("九宫格摒除" + doing);
            if (doing.size() > 0) {
                flg[2] = 1;
                mdl.getDone().addAll(doing);
            }

            if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(基础摒除法)===:\n" + mdl);
            /*
             * 唯一余数法、余数法、唯余法
             * 一个格子受其所在单元中其他20格子的牵制，假如这20格子里已经出现了8个数字
             * 那个这个格子里必然是剩下的那个数字。
             * 数独中任意一个格子都可以填入1-9，如果某格的同行、同列和或同宫中
             * 已经出现了8个不同的数字，那么该格只能填入没出现的第 9 个数字。
             */
            doing = new ArrayList<MdlSudokuCell>();
            for (MdlSudokuCell cell : mdl.getCells()) {
                if (cell.getValue() == 0) {
                    if (cell.nakedSingle()) {
                        doing.add(cell);
                    }
                }
            }
            if (this.debugFlag == 1) System.out.println("★唯一余数法" + doing);
            if (doing.size() > 0) {
                flg[3] = 1;
                mdl.getDone().addAll(doing);
            }
            if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(★唯一余数法)===:\n" + mdl);


            if ((flg[0] + flg[1] + flg[2] + flg[3]) == 0) {
                /*
                 * 数对占位法：利用数对占位作为间接条件，再配合其他数字的排除推理的方法。
                 * 数对是指两格与两数相互对应，但还无法确定两数在这两格中具体的位置。
                 */
                // 行
                doing = new ArrayList<MdlSudokuCell>();
                for (MdlSudokuRegion r : mdl.getRows()) {
                    if (!r.isDone()) {
                        if (r.Pairs(this.debugFlag, "第" + i + "次处理结果(数对占位法行)")) flg[0] = 1;
                    }
                }
                if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(数对占位法行)===:\n" + mdl);

                // 列
                for (MdlSudokuRegion r : mdl.getCols()) {
                    if (!r.isDone()) {
                        if (r.Pairs(this.debugFlag, "第" + i + "次处理结果(数对占位法列)")) flg[1] = 1;
                    }
                }
                if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(数对占位法列)===:\n" + mdl);

                // 宫
                doing = new ArrayList<MdlSudokuCell>();
                for (MdlSudokuRegion r : mdl.getBoxs()) {
                    if (!r.isDone()) {
                        if (r.Pairs(this.debugFlag, "第" + i + "次处理结果(数对占位法宫)")) flg[2] = 1;
                    }
                }
                if (this.debugFlag == 1) System.out.println("===第" + i + "次处理结果(数对占位法宫)===:\n" + mdl);

                if ((flg[0] + flg[1] + flg[2]) == 0) {
                    if (this.remaindertestFlag == 1) {
                        // 余数测试法
                        // 所谓余数测试法就是在某行或列，九宫格所填数字比较多，
                        // 剩余2个或3个时，在剩余宫格添入值进行测试的解题方法。
                        MdlSudoku m = testRemainder(mdl);
                        if (m != null) {
                            System.out.println("--------------------------------------第" + i + "次处理结果(余数测试法)===:\n" + m);
                            if (m.isOK()) {
                                for (int k = 0; k < 81; k ++) {
                                    mdl.getCells()[k].setValue(m.getCells()[k].getValue());
                                }
                                result = true;
                                break ;
                            }
                        }
                    }
                }

                if ((flg[0] + flg[1] + flg[2] + flg[3]) == 0) {
                    System.out.println("WARNING there is none !!");
                    break;
                }
            }

            // 整理信息
            i ++;
            // 全部明确了，所以结束.
            // if (mdl.getDone().size() >= 81) {
            if (mdl.isOK()) {
                result = true;
            }
        }

        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + ( endTime - startTime ) + "ms");
        return result;
    }

    private MdlSudoku testRemainder(MdlSudoku mdl) {
        long startTime=System.currentTimeMillis();   //获取开始时间

        MdlSudoku result = null;
        for (MdlSudokuCell cell : mdl.getCells()) {
            int remainderCount = cell.getRemainderCount();
            if (cell.getValue() == 0) {
                if(remainderCount <= 3) {
                    int offet = cell.getIndex();
                    for(Integer key : cell.getMaybe().keySet()) {
                        MdlSudoku e = new MdlSudoku(mdl);
                        if (this.debugFlag == 1) System.out.println("■■■■■■■余数测试:[" + offet + "]" + key);
                        RemainderTest task = new RemainderTest(offet, key, e);
                        try {
                            if (task.call() == 1) {
                                if (this.debugFlag == 1) System.out.println("■■■■■■■余数测试:[" + offet + "]" + key + ":1");
                                return e;
                            }
                            else {
                                if (this.debugFlag == 1) System.out.println("■■■■■■■余数测试:[" + offet + "]" + key + ":0");
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }

        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + ( endTime - startTime ) + "ms");
        return result;
    }
}
