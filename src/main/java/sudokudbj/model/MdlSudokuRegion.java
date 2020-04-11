package sudokudbj.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MdlSudokuRegion {
    private String comment;
    // 隶属于这个区域的单元格
    private ArrayList<MdlSudokuCell> cells = new ArrayList<MdlSudokuCell>(10);

    // 已完成的
    private HashMap<Integer, MdlSudokuCell> done = new HashMap<Integer, MdlSudokuCell>(10);
    // 未完成的
    private int[] doing  = new int[10];
    // 未完成的（存储可能分布的单元格）
    private HashMap<Integer, ArrayList<MdlSudokuCell>> wait = new HashMap<Integer, ArrayList<MdlSudokuCell>>(10);

    /**
     * MdlSudokuRegion
     * @param str 调试时，显示的信息。
     */
    public MdlSudokuRegion(String str) {
        this.comment = str;

        // 0 是不能的
        doing[0] = 0;
        doing[1] = 1;doing[2] = 2;doing[3] = 3;
        doing[4] = 4;doing[5] = 5;doing[6] = 6;
        doing[7] = 7;doing[8] = 8;doing[9] = 9;

        this.wait.put(1, new ArrayList<MdlSudokuCell>());
        this.wait.put(2, new ArrayList<MdlSudokuCell>());
        this.wait.put(3, new ArrayList<MdlSudokuCell>());
        this.wait.put(4, new ArrayList<MdlSudokuCell>());
        this.wait.put(5, new ArrayList<MdlSudokuCell>());
        this.wait.put(6, new ArrayList<MdlSudokuCell>());
        this.wait.put(7, new ArrayList<MdlSudokuCell>());
        this.wait.put(8, new ArrayList<MdlSudokuCell>());
        this.wait.put(9, new ArrayList<MdlSudokuCell>());
    }

    /* Called by ProcSudoku Only */
    public void add(MdlSudokuCell cell) {
        this.cells.add(cell);
        int key = cell.getValue();
        if (key != 0) {
            this.done.put(cell.getValue(), cell);
            this.doing[key] = 0;
            this.wait.remove(key);
        }
    }

    /* MdlSudokuCell */
    /* proc */
    public void remove(int key, MdlSudokuCell cel, int debug, String msg) {
        this.done.put(key, cel);
        this.doing[key] = -1;
        this.wait.remove(key);

        for (MdlSudokuCell cell : this.cells) {
            // 因为是信息整合，所以不显示信息
            cell.remove(key, debug, msg); // 因为已经明确了，从可能存在中删除。
        } // for
    }

    public boolean isDone() {
        return this.done.keySet().size() == 9;
    }

    public int getRemainderCount() {
        int cnt = 0;
        for (int i : doing) {
            if (i > 0) {

                cnt ++;
            }
        }
        return cnt;
    }

    /**
     * 基础摒除法
     * 基础摒除法就是利用1～9的数字在每一行、每一列、每一个九宫格都只能出现一次的规则进行解题的方法。
     * 根据数独规则，如果某格内出现了一个数字，与该格同行、同列同宫的位置不能再出现相同的数字。
     * 这种排斥同行、同列、同宫其它格内出现相同数字的思路就是排除。
     * @return 确定的单元。
     */
    public ArrayList<MdlSudokuCell> hiddenSingle(String msg, int debug) {
        long startTime  = System.currentTimeMillis();   //获取开始时间
        long startTimeN = System.nanoTime();           //获取开始时间
        if (debug == 1) System.out.println(msg + "hiddenSingle------" + this.comment);

        // 整理未处理单元格
        this.wait.clear();
        for (MdlSudokuCell cell : this.cells) {
            if (cell.getValue() == 0) {
                Set<Integer> keys = cell.getMaybe().keySet();
                for (Integer key : keys) {
                    ArrayList<MdlSudokuCell> list = this.wait.get(key);
                    if (list == null) {
                        list = new ArrayList<MdlSudokuCell>();
                        this.wait.put(key, list);
                    }
                    list.add(cell);
                } // for
            } // if
        } // for

        // debug
        if (debug == 1) {
            Set<Integer> ks = this.wait.keySet();
            for (int k : ks) {
                System.out.print(k + "=>");
                ArrayList<MdlSudokuCell> list = this.wait.get(k);
                for (MdlSudokuCell c : list) {
                    System.out.print(c);
                }
                System.out.println();
            }
        }

        String message = "";
        ArrayList<MdlSudokuCell> result = new ArrayList<MdlSudokuCell>();
        for(int key : this.doing ) {
            ArrayList<MdlSudokuCell> ary = this.wait.get(key);
            if (ary != null) {
                if (ary.size() == 1) {
                    MdlSudokuCell cel = ary.get(0);
                    if (debug == 1) {
                        message = this.comment + "★★☆" + key + "=>" + cel;
                        System.out.println(message);
                    }
                    cel.setValue(key, message);
                    result.add(cel);
                }
                for (MdlSudokuCell c : ary) {
                    Set<Integer> keys = c.getMaybe().keySet();
                    int v = 0;
                    if (keys.size() == 1) {
                        v = keys.iterator().next();
                    }
                    if (v != 0) {
                        if (debug == 1) {
                            message = this.comment + "★★★" + key + "=>"  + c;
                            System.out.println(message);
                        }
                        c.setValue(v, message);
                        result.add(c);
                    }
                }
            }
        }

        long endTimeN = System.nanoTime();         //获取结束时间
        long endTime  = System.currentTimeMillis(); //获取结束时间
        System.out.println(msg + "hiddenSingle程序运行时间： " + (endTime - startTime) + "ms" + (endTimeN - startTimeN) + "ns" + this);
        return result;
    }

    /**
     * 数对占位法
     * 数对占位法指的是在某个区域中使得某两数只能出现在某两格内，这时虽然无法判断
     * 这两个数字的位置，但可以利用两数的占位排斥掉其他数字出现在这两格，再结合排
     * 除法就可以间接填出下个数字。同时排除这两个数在这个区域别的单元格出现的可能性。
     * @param msg
     * @return
     */
    public boolean Pairs(int debug, String msg) {
        long startTime  = System.currentTimeMillis();   //获取开始时间
        long startTimeN = System.nanoTime();           //获取开始时间

        boolean result = false;
        if (getRemainderCount() <= 2) {
            return result;
        }

        // 整理未处理单元格
        this.wait.clear();
        for (MdlSudokuCell cell : this.cells) {
            if (cell.getValue() == 0) {
                Set<Integer> keys = cell.getMaybe().keySet();
                for (Integer key : keys) {
                    ArrayList<MdlSudokuCell> list = this.wait.get(key);
                    if (list == null) {
                        list = new ArrayList<MdlSudokuCell>();
                        this.wait.put(key, list);
                    }
                    /*
                     * 列摒除hiddenSingle------第7列
                     * 1=>[06]{1.5}[42]{1.5.7.8}
                     * 5=>[06]{1.5}[42]{1.5.7.8}
                     * 7=>[42]{1.5.7.8}
                     * 8=>[42]{1.5.7.8}
                     */
                    list.add(cell);
                } // for
            } // if
        } // for

        if (debug == 1) System.out.println(msg + ":" + this);
        ArrayList<MdlSudokuCell> target = new ArrayList<MdlSudokuCell>();
        ArrayList<MdlSudokuCell> comparelist = new ArrayList<MdlSudokuCell>();
        for (Integer key : this.wait.keySet()) {
            ArrayList<MdlSudokuCell> list = this.wait.get(key);
            for(MdlSudokuCell cell : list) {
                if (cell.getRemainderCount() == 2) {
                    for (MdlSudokuCell compc : comparelist) {
                        if (compc != cell && compc.equals(cell)) {
                            target.add(compc);
                            target.add(cell);
                        }
                    }

                    if(!comparelist.contains(cell)) {
                        // contains->indexOf->equals
                        comparelist.add(cell);
                    }

                }
            }
        }
        if (debug == 1) System.out.println("比较对象:" + comparelist);
        if (debug == 1) System.out.println("抽出对象:" + target);
        // {1.4} {1.4} OK
        // [53]{1.2}, [51]{7.8} NG
        if (target.size() > 1) {
            for (MdlSudokuCell cell : target) {
                for (MdlSudokuCell c : this.cells) {
                    if (c.getValue() == 0 && !c.equals(cell)) {
                        if (debug == 1) System.out.println("删除对象:" + c);
                        for (Integer key : cell.getMaybe().keySet()) {
                            if (c.getMaybe().get(key) != null) {
                                c.remove(key, 1, "Pairs");
                                result = true;
                            }
                        }
                    }
                }
            }
        }

        long endTimeN=System.nanoTime();        //获取结束时间
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("Pairs程序运行时间： " + (endTime - startTime) + "ms" + (endTimeN - startTimeN) + "ns" + this);
        return result;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.comment);
        buf.append(" : ");
        int max = this.cells.size() - 1;
        for(int i = 0; i <= max; i ++) {
            MdlSudokuCell cell = this.cells.get(i);
            buf.append(cell);
            if (i != max) {
                buf.append(",");
            }
        }
        buf.append("-isDone:");
        buf.append(this.isDone());
        buf.append("--->done:");
        for (Integer key: this.done.keySet()) {
            buf.append(key);
            buf.append(",");
        }
        buf.append("--->wait:");
        for (int key: this.doing) {
            if ( key > 0) {
                buf.append(key);
                buf.append(",");
            }
        }
        return buf.toString();
    }

    public String getComment() {
      return comment;
    }

    public void setComment(String comment) {
      this.comment = comment;
    }

    public ArrayList<MdlSudokuCell> getCells() {
      return cells;
    }

    public void setCells(ArrayList<MdlSudokuCell> cells) {
      this.cells = cells;
    }

    public HashMap<Integer, MdlSudokuCell> getDone() {
      return done;
    }

    public void setDone(HashMap<Integer, MdlSudokuCell> done) {
      this.done = done;
    }

    public int[] getDoing() {
      return doing;
    }

    public void setDoing(int[] doing) {
      this.doing = doing;
    }

    public HashMap<Integer, ArrayList<MdlSudokuCell>> getWait() {
      return wait;
    }

    public void setWait(HashMap<Integer, ArrayList<MdlSudokuCell>> wait) {
      this.wait = wait;
    }
}
